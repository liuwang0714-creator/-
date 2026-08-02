package org.example.sotokenspringboot.pojo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Message {
    private Long id;
    private Long fromUserId;
    private Long toUserId;
    private String content;
    private String msgType;
    private LocalDateTime sendTime;

    // 关联的用户信息（由 SQL 填充）
    private String fromUsername;
    private String fromNickname;
    private String fromAvatar;
    private String toUsername;
    private String toNickname;
    private String toAvatar;
}
