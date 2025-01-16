package io.github.qifan777.knowledge.react;


import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import io.qifan.infrastructure.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.ai.model.function.FunctionCallingOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

//@Component
@Slf4j
@Primary
@RequiredArgsConstructor
public class ReActChatModel implements ChatModel {
    private final DashScopeChatModel chatModel;
    public static final String META_KEY = "thinkChains";
    private final String imStart = "<|im_start|>";
    private final String imEnd = "<|im_end|>";
    @Value("classpath:/prompts/react/react.st")
    Resource react;
    @Value("classpath:/prompts/react/react-tool.st")
    Resource reactTool;

    @Override
    public ChatResponse call(Prompt prompt) {
        if (prompt.getOptions() instanceof DashScopeChatOptions options) {
            var fromOptions = DashScopeChatOptions.fromOptions(options);
            fromOptions.setStop(List.of("Observation"));
            String inputPrompt = buildInputPrompt(prompt.getInstructions(), fromOptions);
            String output = "";
            List<ReActThinkResult> reActThinkResults = new ArrayList<>();
            ReActThinkChainsResult reactThinkChainsResult;
            while (true) {
                String call = call(inputPrompt + output, fromOptions);
                ReActThinkResult reactThinkResult = parseReactThink(call);
                if (StringUtils.hasText(reactThinkResult.getAction())) {
                    FunctionCallback functionCallback = FunctionCallUtils.getFunctionCallback(reactThinkResult.getAction());
                    String response = "";
                    if (functionCallback != null) {
                        response = functionCallback.call(reactThinkResult.getActionInput(), new ToolContext(options.getToolContext() == null ? new HashMap<>() : options.getToolContext()));
                    }
                    reactThinkResult.setActionOutput(response);
                    reActThinkResults.add(reactThinkResult);
                    output = reactThinkResult.getOriginText() + "\nObservation: " + reactThinkResult.getActionOutput();
                } else {
                    output += "\n" + reactThinkResult.getOriginText();
                    reactThinkChainsResult = parseFinalAnswer(reActThinkResults, reactThinkResult.getOriginText(), output);
                    break;
                }
            }
            return ChatResponse.builder().withMetadata(META_KEY, reactThinkChainsResult)
                    .withGenerations(List.of(new Generation(new AssistantMessage(reactThinkChainsResult.getFinalAnswer()))))
                    .build();
        }
        throw new BusinessException("模型不支持function参数");

    }

    @Override
    public Flux<ChatResponse> stream(Prompt prompt) {
        if (prompt.getOptions() instanceof DashScopeChatOptions options) {
            var fromOptions = DashScopeChatOptions.fromOptions(options);
            fromOptions.setStop(List.of("Observation"));
            String inputPrompt = buildInputPrompt(prompt.getInstructions(), fromOptions);
            return stream(inputPrompt, fromOptions);
        }
        throw new BusinessException("模型不支持function参数");
    }

    public ChatResponse copyResponse(String content, ChatResponse chatResponse) {
        return new ChatResponse(List.of(new Generation(new AssistantMessage(content, chatResponse.getResult().getOutput().getMetadata()), chatResponse.getResult().getMetadata())), chatResponse.getMetadata());
    }

    private String call(String prompt, DashScopeChatOptions options) {
        return chatModel.call(new Prompt(new UserMessage(prompt), options)).getResult().getOutput().getContent();
    }

    private Flux<ChatResponse> stream(String prompt, DashScopeChatOptions options) {
        AtomicReference<String> answer = new AtomicReference<>("");
        String stop = "Final Answer:";
        log.info("提示词: {}", prompt);
        return chatModel.stream(new Prompt(new UserMessage(prompt), options))
                .windowUntil(chatResponse -> {
                    answer.set(answer.get() + chatResponse.getResult().getOutput().getContent());
                    return answer.get().contains(stop) || chatResponse.getResult().getMetadata().getFinishReason().equals("stop");
                })
                .flatMap(flux -> flux.reduce(
                        new ChatResponse(List.of(new Generation(new AssistantMessage("")))),
                        (chatResponse, chatResponse2) -> {
                            String content = chatResponse.getResult().getOutput().getContent() + chatResponse2.getResult().getOutput().getContent();
                            return copyResponse(content, chatResponse2);
                        }))
                .switchMap(chatResponse -> {
                    String content = chatResponse.getResult().getOutput().getContent();
                    if (content.contains(stop)) {
                        String[] split = chatResponse.getResult().getOutput().getContent().split(stop);
                        return Flux.just(copyResponse(split.length > 1 ? split[1] : "", chatResponse));
                    }
                    ReActThinkResult reactThinkResult = parseReactThink(content);
                    if (StringUtils.hasText(reactThinkResult.getAction())) {
                        FunctionCallback functionCallback = FunctionCallUtils.getFunctionCallback(reactThinkResult.getAction());
                        String response = "";
                        if (functionCallback != null) {
                            response = functionCallback.call(reactThinkResult.getActionInput(), new ToolContext(options.getToolContext() == null ? new HashMap<>() : options.getToolContext()));
                        }
                        reactThinkResult.setActionOutput(response);
                        return stream(prompt + content + "\nObservation: " + response, options);
                    }
                    return Flux.just(chatResponse);
                });
    }

    private String buildInputPrompt(List<Message> instructions, FunctionCallingOptions options) {
        Set<String> functionsForThisRequest = new HashSet<>(FunctionCallUtils.findFunctions(options));
        List<FunctionCallback> functionCallbacks = FunctionCallUtils.getFunctionCallback(functionsForThisRequest);
        String tools = functionCallbacks
                .stream()
                .map(functionCallback -> new PromptTemplate(reactTool)
                        .createMessage(Map.of("name", functionCallback.getName(),
                                "description", functionCallback.getDescription(),
                                "parameters", functionCallback.getInputTypeSchema())).getContent())
                .collect(Collectors.joining(","));
        String names = String.join(",", functionsForThisRequest);
        StringBuilder inputPrompt = new StringBuilder();
        for (int i = 0; i < instructions.size(); i++) {
            Message message = instructions.get(i);
            String content = message.getContent();
            if (i == instructions.size() - 1) {
                Map<String, Object> params = Map.of("query", content, "tools_text", tools, "tools_name_text", names);
                content = new PromptTemplate(react).createMessage(params).getContent();
            }
            inputPrompt.append(buildMessage(message.getMessageType(), content));
        }
        inputPrompt.append(buildMessage(MessageType.ASSISTANT, ""))
                .delete(inputPrompt.length() - imEnd.length() - 1, inputPrompt.length());
        return inputPrompt.toString();
    }

    private String buildMessage(MessageType type, String content) {

        PromptTemplate queryTemplate = new PromptTemplate("""
                
                {im_start}{type}
                {query}{im_end}""");
        return queryTemplate.createMessage(Map.of("im_start", imStart, "im_end", imEnd, "query", content, "type", type.getValue()))
                .getContent();
    }

    private ReActThinkResult parseReactThink(String text) {
        int thoughtIndex = text.indexOf("Thought:");
        int actionIndex = text.lastIndexOf("\nAction:");
        int actionInputIndex = text.lastIndexOf("\nAction Input:");
        int observationIndex = text.lastIndexOf("\nObservation:");
        if (0 <= actionIndex && actionIndex < actionInputIndex) {
            observationIndex = observationIndex > 0 ? observationIndex : text.length();
            String thought = text.substring(thoughtIndex + "Thought:".length(), actionIndex).trim();
            String action = text.substring(actionIndex + "\nAction:".length(), actionInputIndex).trim();
            String actionInput = text.substring(actionInputIndex + "\nAction Input:".length(), observationIndex).trim();
            return new ReActThinkResult(thought, action, actionInput, text.substring(0, observationIndex), "");
        } else {
            return new ReActThinkResult("", "", "", text, "");
        }
    }

    private ReActThinkChainsResult parseFinalAnswer(List<ReActThinkResult> reActThinkResults, String text, String originText) {
        int thoughtIndex = text.lastIndexOf("Thought:");
        int finalAnswerIndex = text.lastIndexOf("\nFinal Answer:");
        if (0 <= thoughtIndex && thoughtIndex < finalAnswerIndex) {
            String thought = text.substring(thoughtIndex + "Thought:".length(), finalAnswerIndex).trim();
            String finalAnswer = text.substring(finalAnswerIndex + "\nFinal Answer:".length()).trim();
            return new ReActThinkChainsResult(reActThinkResults, thought, finalAnswer, originText);
        }
        throw new BusinessException("解析大模型输出结果失败");
    }


    @Override
    public ChatOptions getDefaultOptions() {
        return chatModel.getDefaultOptions();
    }
}
