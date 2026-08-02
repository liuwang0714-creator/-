package org.example.sotokenspringboot.service;

import lombok.RequiredArgsConstructor;
import org.example.sotokenspringboot.mapper.MessageMapper;
import org.example.sotokenspringboot.pojo.Message;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageMapper messageMapper;
    private final usersvice usersvice;

    public Message save(Message message) {
        if (message.getSendTime() == null) {
            message.setSendTime(LocalDateTime.now());
        }
        if (message.getMsgType() == null) {
            message.setMsgType("TEXT");
        }
        messageMapper.insert(message);
        return message;
    }

    public List<Message> getHistory(String username1, String username2, int limit) {
        if (!usersvice.canChatWith(username1, username2)) {
            return List.of();
        }
        List<Message> messages = messageMapper.findByUsernames(username1, username2, limit);
        Collections.reverse(messages);
        return messages;
    }
}
