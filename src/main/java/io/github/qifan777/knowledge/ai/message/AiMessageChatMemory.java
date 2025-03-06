package io.github.qifan777.knowledge.ai.message;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qifan.infrastructure.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.model.Media;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class AiMessageChatMemory implements ChatMemory {
    private final AiMessageMapper messageMapper;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public static AiMessage toAiMessage(Message message, String sessionId, ObjectMapper mapper) {
        AiMessage aiMessage = new AiMessage();
        aiMessage.setAiSessionId(sessionId);
        aiMessage.setTextContent(message.getText());
        aiMessage.setType(message.getMessageType());
        aiMessage.setMedias(mapper.writeValueAsString(new ArrayList<>()));
        if (message instanceof UserMessage userMessage &&
                !CollectionUtil.isEmpty(userMessage.getMedia())) {
            List<AiMessage.Media> mediaList = userMessage
                    .getMedia()
                    .stream()
                    .map(media -> {
                        AiMessage.Media media1 = new AiMessage.Media();
                        media1.setType(media.getMimeType().getType());
                        media1.setData(media.getData().toString());
                        return media1;
                    })
                    .toList();
            aiMessage.setMedias(mapper.writeValueAsString(mediaList));
        }
        return aiMessage;
    }

    @SneakyThrows
    public static Message toSpringAiMessage(AiMessage aiMessage, ObjectMapper objectMapper) {
        List<Media> mediaList = new ArrayList<>();
        List<AiMessage.Media> medias = objectMapper.readValue(aiMessage.getMedias(), new TypeReference<List<AiMessage.Media>>() {
        });
        if (!CollectionUtil.isEmpty(medias)) {
            mediaList = medias.stream().map(AiMessageChatMemory::toSpringAiMedia).toList();
        }
        if (aiMessage.getType().equals(MessageType.ASSISTANT)) {
            return new AssistantMessage(aiMessage.getTextContent());
        }
        if (aiMessage.getType().equals(MessageType.USER)) {
            return new UserMessage(aiMessage.getTextContent(), mediaList);
        }
        if (aiMessage.getType().equals(MessageType.SYSTEM)) {
            return new SystemMessage(aiMessage.getTextContent());
        }
        throw new BusinessException("不支持的消息类型");
    }

    @SneakyThrows
    public static Media toSpringAiMedia(AiMessage.Media media) {
        return new Media(new MediaType(media.getType()), new URL(media.getData()));
    }

    /**
     * 不实现，手动前端发起请求保存用户的消息和大模型回复的消息
     */
    @Override
    public void add(String conversationId, List<Message> messages) {
    }

    /**
     * 查询会话内的消息最新n条历史记录
     *
     * @param conversationId 会话id
     * @param lastN          最近n条
     * @return org.springframework.ai.chat.messages.Message格式的消息
     */
    @Override
    public List<Message> get(String conversationId, int lastN) {
        return messageMapper
                .selectPage(new Page<>(1, lastN), Wrappers.lambdaQuery(AiMessage.class).eq(AiMessage::getAiSessionId, conversationId)
                        .orderByAsc(AiMessage::getCreatedTime)).getRecords()
                .stream().map(row -> toSpringAiMessage(row, objectMapper))
                .toList();
    }

    /**
     * 清除会话内的消息
     *
     * @param conversationId 会话id
     */
    @Override
    public void clear(String conversationId) {
        messageMapper.delete(Wrappers.lambdaQuery(AiMessage.class).eq(AiMessage::getAiSessionId, conversationId));
    }
}
