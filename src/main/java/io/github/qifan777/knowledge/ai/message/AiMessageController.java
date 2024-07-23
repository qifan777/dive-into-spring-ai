package io.github.qifan777.knowledge.ai.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.qifan777.knowledge.ai.agent.Agent;
import io.github.qifan777.knowledge.ai.message.dto.AiMessageInput;
import io.github.qifan777.knowledge.ai.message.dto.AiMessageParams;
import io.github.qifan777.knowledge.ai.session.AiSession;
import io.github.qifan777.knowledge.ai.session.AiSessionRepository;
import io.qifan.infrastructure.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.babyfish.jimmer.sql.EnableDtoGeneration;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.model.Media;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;

@RequestMapping("message")
@RestController
@AllArgsConstructor
@Slf4j
@EnableDtoGeneration
public class AiMessageController {
    private final AiMessageChatMemory chatMemory;
    private final OpenAiChatModel dashScopeAiChatModel;
    private final ObjectMapper objectMapper;
    private final AiMessageRepository messageRepository;
    private final ApplicationContext applicationContext;
    private final AiSessionRepository aiSessionRepository;

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
    public Flux<ServerSentEvent<String>> chat(@RequestBody AiMessageInput input) {
        AiSession aiSession = aiSessionRepository.findById(input.getSessionId())
                .orElseThrow(() -> new BusinessException("会话不存在"));
        AiMessageParams params = aiSession.params();
        String[] functionBeanNames = new String[0];
        // 如果启用Agent则获取Agent的bean
        if (params.getEnableAgent()) {
            // 获取带有Agent注解的bean
            Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(Agent.class);
            functionBeanNames = new String[beansWithAnnotation.keySet().size()];
            functionBeanNames = beansWithAnnotation.keySet().toArray(functionBeanNames);
        }
        return ChatClient.create(dashScopeAiChatModel).prompt()
                .system(promptSystemSpec -> toCsvPrompt(promptSystemSpec, params))
                .user(promptUserSpec -> toPrompt(promptUserSpec, input))
                // agent列表
                .functions(functionBeanNames)
                .advisors(advisorSpec -> {
                    // 使用历史消息
                    useChatHistory(advisorSpec, input.getSessionId());
                    // 如果启用向量数据库
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
        if (message instanceof UserMessage userMessage&& !CollectionUtils.isEmpty(userMessage.getMedia())) {
            // 用户发送的图片/语言
            Media[] medias = new Media[userMessage.getMedia().size()];
            promptUserSpec.media(userMessage.getMedia().toArray(medias));
        }
        // 用户发送的文本
        promptUserSpec.text(message.getContent());
    }

    @SneakyThrows
    public void toCsvPrompt(ChatClient.PromptSystemSpec spec, AiMessageParams params) {
        String file = params.getFile();
        if (!StringUtils.hasText(file)) return;
        UrlResource urlResource = new UrlResource(file);
        byte[] bytes = urlResource.getInputStream().readAllBytes();
        String content = new String(bytes);
        Message message = new PromptTemplate("""
                下面是表格数据信息
                ---------------------
                {context}
                ---------------------
                给定的上下文和提供的历史信息，而不是事先的知识，回复用户的意见。如果答案不在上下文中，告诉用户你不能回答这个问题。
                """)
                .createMessage(Map.of(
//                        "role", enableProfession ? "专家" : "初学者",
                        "context", content));
        log.info(message.getContent());
        spec.text(message.getContent());
    }

    public void useChatHistory(ChatClient.AdvisorSpec advisorSpec, String sessionId) {
        // 1. 如果需要存储会话和消息到数据库，自己可以实现ChatMemory接口，这里使用自己实现的AiMessageChatMemory，数据库存储。
        // 2. 传入会话id，MessageChatMemoryAdvisor会根据会话id去查找消息。
        // 3. 只需要携带最近10条消息
        // MessageChatMemoryAdvisor会在消息发送给大模型之前，从ChatMemory中获取会话的历史消息，然后一起发送给大模型。
        advisorSpec.advisors(new MessageChatMemoryAdvisor(chatMemory, sessionId, 10));
    }


}
