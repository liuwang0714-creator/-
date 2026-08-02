package org.example.sotokenspringboot.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sotokenspringboot.pojo.ChatMessageDTO;
import org.example.sotokenspringboot.pojo.Message;
import org.example.sotokenspringboot.pojo.OnlineUserDTO;
import org.example.sotokenspringboot.pojo.User;
import org.example.sotokenspringboot.service.MessageService;
import org.example.sotokenspringboot.service.usersvice;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final WebSocketSessionManager sessionManager;
    private final MessageService messageService;
    private final usersvice usersvice;
    private final ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("WebSocket连接建立: {}", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        ChatMessageDTO dto = objectMapper.readValue(textMessage.getPayload(), ChatMessageDTO.class);
        String type = dto.getType();

        if ("CONNECT".equals(type)) {
            handleConnect(session, dto);
        } else if ("MESSAGE".equals(type)) {
            handleMessage(session, dto);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId != null) {
            sessionManager.removeSession(userId);
            broadcastOnlineUsers();
            log.info("用户 {} 断开连接", userId);
        }
    }

    private void handleConnect(WebSocketSession session, ChatMessageDTO dto) {
        String username = dto.getFromUsername();
        if (username == null && dto.getFromUserId() != null) {
            User byId = usersvice.findByIdWithRole(dto.getFromUserId().intValue());
            if (byId != null) {
                username = byId.getUsername();
            }
        }
        if (username == null) {
            sendError(session, "请提供用户名");
            return;
        }

        User user = usersvice.findByUsernameWithRole(username);
        if (user == null) {
            sendError(session, "用户不存在");
            return;
        }

        session.getAttributes().put("userId", user.getId().longValue());
        session.getAttributes().put("username", user.getUsername());
        sessionManager.addSession(user.getId().longValue(), session);

        broadcastOnlineUsers();
        log.info("用户 {} ({}) 已连接", user.getId(), user.getUsername());
    }

    private void handleMessage(WebSocketSession session, ChatMessageDTO dto) {
        String fromUsername = (String) session.getAttributes().get("username");
        if (fromUsername == null) {
            sendError(session, "请先连接");
            return;
        }

        String toUsername = dto.getToUsername();
        if (toUsername == null || toUsername.isBlank()) {
            sendError(session, "请提供接收者用户名 toUsername");
            return;
        }
        if (dto.getContent() == null || dto.getContent().isBlank()) {
            sendError(session, "消息内容不能为空");
            return;
        }

        if (!usersvice.canChatWith(fromUsername, toUsername)) {
            sendError(session, usersvice.getChatDenyReason(fromUsername, toUsername));
            return;
        }

        User fromUser = usersvice.findByUsernameWithRole(fromUsername);
        User toUser = usersvice.findByUsernameWithRole(toUsername);

        Message message = new Message();
        message.setFromUserId(fromUser.getId().longValue());
        message.setToUserId(toUser.getId().longValue());
        message.setContent(dto.getContent().trim());
        message.setMsgType("TEXT");
        messageService.save(message);

        ChatMessageDTO push = buildChatMessage(message, fromUser, toUser);
        push.setType("MESSAGE");

        sessionManager.sendToUser(fromUser.getId().longValue(), push);
        sessionManager.sendToUser(toUser.getId().longValue(), push);
    }

    private ChatMessageDTO buildChatMessage(Message message, User fromUser, User toUser) {
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setId(message.getId());
        dto.setFromUserId(message.getFromUserId());
        dto.setToUserId(message.getToUserId());
        dto.setContent(message.getContent());
        dto.setMsgType(message.getMsgType());
        if (message.getSendTime() != null) {
            dto.setSendTime(message.getSendTime().format(FORMATTER));
        }
        if (fromUser != null) {
            dto.setFromUsername(fromUser.getUsername());
            dto.setFromNickname(fromUser.getNickname());
            dto.setFromAvatar(fromUser.getAvatar());
            dto.setFromRoleCode(fromUser.getRoleCode());
        }
        if (toUser != null) {
            dto.setToUsername(toUser.getUsername());
            dto.setToNickname(toUser.getNickname());
            dto.setToAvatar(toUser.getAvatar());
            dto.setToRoleCode(toUser.getRoleCode());
        }
        return dto;
    }

    private void broadcastOnlineUsers() {
        List<Long> onlineIds = sessionManager.getOnlineUserIds();
        List<OnlineUserDTO> onlineUsers = onlineIds.stream()
                .map(id -> usersvice.findByIdWithRole(id.intValue()))
                .filter(u -> u != null)
                .map(u -> {
                    OnlineUserDTO dto = new OnlineUserDTO();
                    dto.setId(u.getId().longValue());
                    dto.setUsername(u.getUsername());
                    dto.setNickname(u.getNickname());
                    dto.setRoleCode(u.getRoleCode());
                    dto.setAvatar(u.getAvatar());
                    return dto;
                })
                .toList();

        ChatMessageDTO onlineMsg = new ChatMessageDTO();
        onlineMsg.setType("ONLINE");
        onlineMsg.setData(onlineUsers);

        sessionManager.broadcast(onlineMsg);
    }

    private void sendError(WebSocketSession session, String error) {
        try {
            Map<String, String> err = new HashMap<>();
            err.put("type", "ERROR");
            err.put("content", error);
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(err)));
        } catch (Exception e) {
            log.error("发送错误消息失败", e);
        }
    }
}
