package io.github.qifan777.knowledge.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.qifan777.knowledge.ai.message.dto.AiMessageParams;
import io.github.qifan777.knowledge.ai.message.dto.AiMessageWrapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RequestMapping("demo/message")
@RestController
@AllArgsConstructor
public class MessageDemoController {

    private final ChatModel chatModel;


    private final ObjectMapper objectMapper;
    private final VectorStore vectorStore;
    // 模拟数据库存储会话和消息
    private final ChatMemory chatMemory = new InMemoryChatMemory();

    /**
     * 非流式问答
     *
     * @param prompt 用户提问
     * @return org.springframework.ai.chat.model.ChatResponse
     */
    @GetMapping("chat")
    public String chat(@RequestParam String prompt) {
        ChatClient chatClient = ChatClient.create(chatModel);
        return chatClient.prompt()
                // 输入单条提示词
                .user(prompt)
                // call代表非流式问答，返回的结果可以是ChatResponse，也可以是Entity（转成java类型），也可以是字符串直接提取回答结果。
                .call()
                .content();
    }

    /**
     * 流式问答
     *
     * @param prompt 用户提问
     * @return SSE流式响应
     */
    @GetMapping(value = "chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chatStream(@RequestParam String prompt) {
        return ChatClient.create(chatModel).prompt()
                // 输入多条消息，可以将历史消息记录传入
                .messages(new SystemMessage("你是一个Java智能助手，应用你的Java知识帮助用户解决问题或者编写程序"),
                        new UserMessage(prompt))
                // 流式返回
                .stream()
                // 构造SSE（ServerSendEvent）格式返回结果
                .chatResponse().map(chatResponse -> ServerSentEvent.builder(toJson(chatResponse))
                        .event("message")
                        .build());
    }

    /**
     * 将流式回答结果转json字符串
     *
     * @param chatResponse 流式回答结果
     * @return String json字符串
     */
    @SneakyThrows
    public String toJson(ChatResponse chatResponse) {
        return objectMapper.writeValueAsString(chatResponse);
    }

    /**
     * 调用自定义函数回答用户的提问
     *
     * @param prompt       用户的提问
     * @param functionName 函数名称（bean的名称，类名小写）
     * @return SSE流式响应
     */
    @GetMapping(value = "chat/stream/function", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chatStreamWithFunction(@RequestParam String prompt, @RequestParam String functionName) {
        return ChatClient.create(chatModel).prompt()
                .messages(new UserMessage(prompt))
                // spring ai会从已注册为bean的function中查找函数，将它添加到请求中。如果成功触发就会调用函数
                .functions(functionName)
                .stream()
                .chatResponse()
                .map(chatResponse -> ServerSentEvent.builder(toJson(chatResponse))
                        .event("message")
                        .build());
    }

    /**
     * 从向量数据库中查找文档，并将查询的文档作为上下文回答。
     *
     * @param prompt 用户的提问
     * @return SSE流响应
     */
    @GetMapping(value = "chat/stream/database", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chatStreamWithDatabase(@RequestParam String prompt) {
        // question_answer_context是一个占位符，会替换成向量数据库中查询到的文档。QuestionAnswerAdvisor会替换。
        String promptWithContext = """
                下面是上下文信息
                ---------------------
                {question_answer_context}
                ---------------------
                给定的上下文和提供的历史信息，而不是事先的知识，回复用户的意见。如果答案不在上下文中，告诉用户你不能回答这个问题。
                """;
        return ChatClient.create(chatModel).prompt()
                .user(prompt)
                .advisors(new QuestionAnswerAdvisor(vectorStore, SearchRequest.builder().build(), promptWithContext))
                .stream()
                .content()
                .map(chatResponse -> ServerSentEvent.builder(chatResponse)
                        .event("message")
                        .build());
    }

    /**
     * 根据会话id，从数据库中查找历史消息，并将消息作为上下文回答。
     *
     * @param prompt    用户的提问
     * @param sessionId 会话id
     * @return SSE流响应
     */
    @GetMapping(value = "chat/stream/history", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chatStreamWithHistory(@RequestParam String prompt,
                                                               @RequestParam String sessionId) {
        // 1. 如果需要存储会话和消息到数据库，自己可以实现ChatMemory接口，这里使用InMemoryChatMemory，内存存储。
        // 2. 传入会话id，MessageChatMemoryAdvisor会根据会话id去查找消息。
        // 3. 只需要携带最近10条消息
        var messageChatMemoryAdvisor = new MessageChatMemoryAdvisor(chatMemory, sessionId, 10);
        return ChatClient.create(chatModel).prompt()
                .user(prompt)
                // MessageChatMemoryAdvisor会在消息发送给大模型之前，从ChatMemory中获取会话的历史消息，然后一起发送给大模型。
                .advisors(messageChatMemoryAdvisor)
                .stream()
                .content()
                .map(chatResponse -> ServerSentEvent.builder(chatResponse)
                        .event("message")
                        .build());
    }

    @PostMapping("ignore")
    public void ignore(@RequestParam AiMessageWrapper wrapper, @RequestParam AiMessageParams params) {
    }

}
