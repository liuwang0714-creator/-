package org.example.sotokenspringboot.pojo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Conversation {
    private Long partnerId;
    private String partnerUsername;
    private String partnerNickname;
    private String partnerAvatar;
    private String partnerRoleCode;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
}
