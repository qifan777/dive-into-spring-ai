package io.github.qifan777.knowledge.ai.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.qifan777.knowledge.ai.agent.Agent;
import io.github.qifan777.knowledge.ai.message.dto.AiMessageInput;
import io.github.qifan777.knowledge.ai.message.dto.AiMessageWrapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.content.Media;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.util.Map;

@RequestMapping("message")
@RestController
@AllArgsConstructor
@Slf4j
public class AiMessageController {
    private final AiMessageChatMemory chatMemory;
    private final ChatModel chatModel;
    //    private final ImageModel imageModel;
    private final VectorStore vectorStore;
    private final ObjectMapper objectMapper;
    private final AiMessageRepository messageRepository;
    private final ApplicationContext applicationContext;

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

//    @PostMapping("chat/image")
//    public String textToImageChat(@RequestBody AiMessageInput input) {
//        return imageModel.call(new ImagePrompt(input.getTextContent())).getResult().getOutput().getUrl();
//    }

    /**
     * 为了支持文件问答，需要同时接收json（AiMessageWrapper json体）和 MultipartFile（文件）
     * Content-Type 从 application/json 修改为 multipart/form-data
     * 之前接收请求参数是用@RequestBody, 现在使用@RequestPart 接收json字符串再手动转成AiMessageWrapper.
     * SpringMVC的@RequestPart是支持自动将Json字符串转换为Java对象，也就是说可以等效`@RequestBody`，
     * 但是由于前端FormData无法设置Part的Content-Type，所以只能手动转json字符串再转成Java对象。
     *
     * @param input 消息包含文本信息，会话id，多媒体信息（图片语言）。参考src/main/dto/AiMessage.dto
     * @param file  文件问答
     * @return SSE流
     */
    @SneakyThrows
    @PostMapping(value = "chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chat(@RequestPart String input, @RequestPart(required = false) MultipartFile file) {
        AiMessageWrapper aiMessageWrapper = objectMapper.readValue(input, AiMessageWrapper.class);
        String[] functionBeanNames = new String[0];
        // 如果启用Agent则获取Agent的bean
        if (aiMessageWrapper.getParams().getEnableAgent()) {
            // 获取带有Agent注解的bean
            Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(Agent.class);
            functionBeanNames = new String[beansWithAnnotation.size()];
            functionBeanNames = beansWithAnnotation.keySet().toArray(functionBeanNames);
        }
        return ChatClient.create(chatModel).prompt()
                // 启用文件问答
                .system(promptSystemSpec -> useFile(promptSystemSpec, file))
                .user(promptUserSpec -> toPrompt(promptUserSpec, aiMessageWrapper.getMessage()))
                // agent列表
                .toolNames(functionBeanNames)
                .advisors(advisorSpec -> {
                    // 使用历史消息
                    useChatHistory(advisorSpec, aiMessageWrapper.getMessage().getSessionId());
                    // 使用向量数据库
                    useVectorStore(advisorSpec, aiMessageWrapper.getParams().getEnableVectorStore());
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
        if (message instanceof UserMessage userMessage &&
                !CollectionUtils.isEmpty(userMessage.getMedia())) {
            // 用户发送的图片/语言
            Media[] medias = new Media[userMessage.getMedia().size()];
            promptUserSpec.media(userMessage.getMedia().toArray(medias));
        }
        // 用户发送的文本
        promptUserSpec.text(message.getText());
    }

    public void useChatHistory(ChatClient.AdvisorSpec advisorSpec, String sessionId) {
        // 1. 如果需要存储会话和消息到数据库，自己可以实现ChatMemory接口，这里使用自己实现的AiMessageChatMemory，数据库存储。
        // 2. 传入会话id，MessageChatMemoryAdvisor会根据会话id去查找消息。
        // 3. 只需要携带最近10条消息
        // MessageChatMemoryAdvisor会在消息发送给大模型之前，从ChatMemory中获取会话的历史消息，然后一起发送给大模型。
        advisorSpec.advisors(MessageChatMemoryAdvisor.builder(chatMemory).conversationId(sessionId).build());
    }

    public void useVectorStore(ChatClient.AdvisorSpec advisorSpec, Boolean enableVectorStore) {
        if (!enableVectorStore) return;
        // question_answer_context是一个占位符，会替换成向量数据库中查询到的文档。QuestionAnswerAdvisor会替换。
        String promptWithContext = """
                {query}
                下面是上下文信息
                ---------------------
                {question_answer_context}
                ---------------------
                给定的上下文和提供的历史信息，而不是事先的知识，回复用户的意见。如果答案不在上下文中，告诉用户你不能回答这个问题。
                """;
        advisorSpec.advisors(QuestionAnswerAdvisor.builder(vectorStore)
                .promptTemplate(new PromptTemplate(promptWithContext))
                .build());
    }

    @SneakyThrows
    public void useFile(ChatClient.PromptSystemSpec spec, MultipartFile file) {
        if (file == null) return;
        String content = new TikaDocumentReader(new InputStreamResource(file.getInputStream())).get().get(0).getText();
        Message message = new PromptTemplate("""
                已下内容是额外的知识，在你回答问题时可以参考下面的内容
                ---------------------
                {context}
                ---------------------
                """)
                .createMessage(Map.of("context", content));
        spec.text(message.getText());
    }

}
