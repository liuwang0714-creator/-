package org.example.sotokenspringboot.controller;

import lombok.RequiredArgsConstructor;
import org.example.sotokenspringboot.pojo.Conversation;
import org.example.sotokenspringboot.pojo.Message;
import org.example.sotokenspringboot.pojo.Result;
import org.example.sotokenspringboot.pojo.User;
import org.example.sotokenspringboot.service.MessageService;
import org.example.sotokenspringboot.service.usersvice;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ImController {

    private final usersvice usersvice;
    private final MessageService messageService;

    @GetMapping("/chat/targets")
    public Result<List<User>> chatTargets(@RequestParam String username) {
        return Result.success(usersvice.findChatTargets(username));
    }

    @GetMapping("/chat/conversations")
    public Result<List<Conversation>> conversations(@RequestParam String username) {
        return Result.success(usersvice.findConversations(username));
    }

    @GetMapping("/chat/can-chat")
    public Result<Boolean> canChat(@RequestParam String fromUsername,
                                   @RequestParam String toUsername) {
        if (usersvice.canChatWith(fromUsername, toUsername)) {
            return Result.success(true);
        }
        return Result.error(usersvice.getChatDenyReason(fromUsername, toUsername));
    }

    @GetMapping("/message/history")
    public Result<List<Message>> history(@RequestParam String fromUsername,
                                         @RequestParam String toUsername,
                                         @RequestParam(defaultValue = "50") int limit) {
        if (!usersvice.canChatWith(fromUsername, toUsername)) {
            return Result.error(usersvice.getChatDenyReason(fromUsername, toUsername));
        }
        return Result.success(messageService.getHistory(fromUsername, toUsername, limit));
    }
}
