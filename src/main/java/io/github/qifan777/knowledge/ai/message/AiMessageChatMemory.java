package io.github.qifan777.knowledge.ai.message;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollectionUtil;
import io.github.qifan777.knowledge.ai.session.AiSessionFetcher;
import io.github.qifan777.knowledge.ai.session.AiSessionRepository;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AbstractMessage;
import org.springframework.ai.chat.messages.Media;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class AiMessageChatMemory implements ChatMemory {
    private final AiMessageRepository messageRepository;
    private final AiSessionRepository sessionRepository;

    @Override
    public void add(String conversationId, List<Message> messages) {
        List<AiMessage> aiMessageList = messages
                .stream()
                .map(message -> toAiMessage(message, conversationId))
                .toList();
        // 当前的现场处于异步状态，非没有servletRequest无法获取当前登录的用户信息。
        // 模拟请求
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
        // 设置当前的用id
        String userId = sessionRepository.findById(conversationId, AiSessionFetcher.$.creator()).orElseThrow().creator().id();
        StpUtil.switchTo(userId);
        // 保存到数据库，这样创建人和编辑人才有数据
        messageRepository.saveEntities(aiMessageList);
    }


    @Override
    public List<Message> get(String conversationId, int lastN) {
        return messageRepository.findBySessionId(conversationId, lastN)
                .stream()
                .map(AiMessageChatMemory::toSpringAiMessage)
                .toList();
    }

    @Override
    public void clear(String conversationId) {
        messageRepository.deleteBySessionId(conversationId);
    }

    public static AiMessage toAiMessage(Message message, String sessionId) {
        return AiMessageDraft.$.produce(draft -> {
            draft.setSessionId(sessionId)
                    .setTextContent(message.getContent())
                    .setType(message.getMessageType())
                    .setMedias(new ArrayList<>());
            if (!CollectionUtil.isEmpty(message.getMedia())) {
                List<AiMessage.Media> mediaList = message
                        .getMedia()
                        .stream()
                        .map(media -> new AiMessage.Media(media.getMimeType().getType(), media.getData().toString()))
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
        return new DefaultMessage(aiMessage.type(), aiMessage.textContent(), mediaList);
    }

    @SneakyThrows
    public static Media toSpringAiMedia(AiMessage.Media media) {
        return new Media(new MediaType(media.getType()), new URL(media.getData()));
    }

    public static class DefaultMessage extends AbstractMessage {
        protected DefaultMessage(MessageType messageType, String textContent, List<Media> media) {
            super(messageType, textContent, media);
        }
    }
}
