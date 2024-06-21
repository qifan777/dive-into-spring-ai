package io.github.qifan777.knowledge.ai.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.qifan777.knowledge.ai.message.dto.AiMessageInput;
import io.github.qifan777.knowledge.ai.message.dto.AiMessageWrapper;
import io.qifan.ai.dashscope.DashScopeAiChatModel;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.messages.Media;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RequestMapping("message")
@RestController
@AllArgsConstructor
@Slf4j
public class AiMessageController {
    private final AiMessageChatMemory chatMemory;
    private final DashScopeAiChatModel dashScopeAiChatModel;
    private final VectorStore vectorStore;
    private final ObjectMapper objectMapper;
    private final AiMessageRepository messageRepository;

    @DeleteMapping("history/{sessionId}")
    public void deleteHistory(@PathVariable String sessionId) {
        chatMemory.clear(sessionId);
    }

    /**
     * 消息保存
     *
     * @param input 用户发送的消息/AI回复的消息
     */
    @PostMapping
    public void save(@RequestBody AiMessageInput input) {
        messageRepository.save(input.toEntity());
    }

    /**
     * @param input 消息包含文本信息，会话id，多媒体信息（图片语言）。参考src/main/dto/AiMessage.dto
     * @return SSE流
     */
    @PostMapping(value = "chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chat(@RequestBody AiMessageWrapper input) {
        String[] functionNames = new String[input.getParams().getFunctionNames().size()];
        return ChatClient.create(dashScopeAiChatModel).prompt()
            .user(promptUserSpec -> toPrompt(promptUserSpec, input.getMessage()))
            .functions(input.getParams().getFunctionNames().toArray(functionNames))
            .advisors(advisorSpec -> {
                // 使用历史消息
                useChatHistory(advisorSpec, input.getMessage().getSessionId());
                // 如果启用向量数据库
                if (input.getParams().getEnableVectorStore()) {
                    // 使用向量数据库w
                    useVectorStore(advisorSpec);
                }
            })
            .stream()
            .chatResponse()
            .map(chatResponse -> ServerSentEvent.builder(toJson(chatResponse))
                // 和前端监听的事件相对应
                .event("message")
                .build());
    }

    @SneakyThrows
    public String toJson(ChatResponse response) {
        return objectMapper.writeValueAsString(response);
    }

    public void toPrompt(ChatClient.PromptUserSpec promptUserSpec, AiMessageInput input) {
        // AiMessageInput转成Message
        Message message = AiMessageChatMemory.toSpringAiMessage(input.toEntity());
        if (!CollectionUtils.isEmpty(message.getMedia())) {
            // 用户发送的图片/语言
            Media[] medias = new Media[message.getMedia().size()];
            promptUserSpec.media(message.getMedia().toArray(medias));
        }
        // 用户发送的文本
        promptUserSpec.text(message.getContent());
    }

    public void useChatHistory(ChatClient.AdvisorSpec advisorSpec, String sessionId) {
        // 1. 如果需要存储会话和消息到数据库，自己可以实现ChatMemory接口，这里使用自己实现的AiMessageChatMemory，数据库存储。
        // 2. 传入会话id，MessageChatMemoryAdvisor会根据会话id去查找消息。
        // 3. 只需要携带最近10条消息
        // MessageChatMemoryAdvisor会在消息发送给大模型之前，从ChatMemory中获取会话的历史消息，然后一起发送给大模型。
        advisorSpec.advisors(new MessageChatMemoryAdvisor(chatMemory, sessionId, 10));
    }

    public void useVectorStore(ChatClient.AdvisorSpec advisorSpec) {
        // question_answer_context是一个占位符，会替换成向量数据库中查询到的文档。QuestionAnswerAdvisor会替换。
        String promptWithContext = """
            下面是上下文信息
            ---------------------
            {question_answer_context}
            ---------------------
            给定的上下文和提供的历史信息，而不是事先的知识，回复用户的意见。如果答案不在上下文中，告诉用户你不能回答这个问题。
            """;
        advisorSpec.advisors(new QuestionAnswerAdvisor(vectorStore, SearchRequest.defaults(), promptWithContext));
    }
}
