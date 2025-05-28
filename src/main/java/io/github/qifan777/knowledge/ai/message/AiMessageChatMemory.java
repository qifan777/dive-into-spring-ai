package io.github.qifan777.knowledge.ai.message;

import cn.hutool.core.collection.CollectionUtil;
import io.qifan.infrastructure.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.content.Media;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class AiMessageChatMemory implements ChatMemory {
    private final AiMessageRepository messageRepository;

    public static AiMessage toAiMessage(Message message, String sessionId) {
        return AiMessageDraft.$.produce(draft -> {
            draft.setSessionId(sessionId)
                    .setTextContent(message.getText())
                    .setType(message.getMessageType())
                    .setMedias(new ArrayList<>());
            if (message instanceof UserMessage userMessage &&
                    !CollectionUtil.isEmpty(userMessage.getMedia())) {
                List<AiMessage.Media> mediaList = userMessage
                        .getMedia()
                        .stream()
                        .map(media -> new AiMessage.Media()
                                .setType(media.getMimeType().getType())
                                .setData(media.getData().toString()))
                        .toList();
                draft.setMedias(mediaList);
            }
        });
    }

    public static Message toSpringAiMessage(AiMessage aiMessage) {
        List<Media> mediaList = new ArrayList<>();
        if (!CollectionUtil.isEmpty(aiMessage.medias())) {
            mediaList = aiMessage.medias().stream().map(AiMessageChatMemory::toSpringAiMedia).toList();
        }
        if (aiMessage.type().equals(MessageType.ASSISTANT)) {
            return new AssistantMessage(aiMessage.textContent());
        }
        if (aiMessage.type().equals(MessageType.USER)) {
            return UserMessage.builder().text(aiMessage.textContent())
                    .media(mediaList)
                    .build();
        }
        if (aiMessage.type().equals(MessageType.SYSTEM)) {
            return new SystemMessage(aiMessage.textContent());
        }
        throw new BusinessException("不支持的消息类型");
    }

    @SneakyThrows
    public static Media toSpringAiMedia(AiMessage.Media media) {
        return Media.builder().data(new URL(media.getData()))
                .mimeType(new MediaType(media.getType()))
                .build();
    }

    /**
     * 不实现，手动前端发起请求保存用户的消息和大模型回复的消息
     */
    @Override
    public void add(@NotNull String conversationId, @NotNull List<Message> messages) {
    }


    /**
     * 查询会话内的消息最新n条历史记录
     *
     * @param conversationId 会话id
     * @return org.springframework.ai.chat.messages.Message格式的消息
     */
    @Override
    public @NotNull List<Message> get(@NotNull String conversationId) {
        return messageRepository
                // 查询会话内的最新10条消息
                .findBySessionId(conversationId, 10)
                .stream()
                // 转成Message对象
                .map(AiMessageChatMemory::toSpringAiMessage)
                .toList();
    }

    /**
     * 清除会话内的消息
     *
     * @param conversationId 会话id
     */
    @Override
    public void clear(@NotNull String conversationId) {
        messageRepository.deleteBySessionId(conversationId);
    }
}
