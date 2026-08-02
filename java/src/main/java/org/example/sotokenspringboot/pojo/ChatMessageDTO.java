package org.example.sotokenspringboot.pojo;

import lombok.Data;

@Data
public class ChatMessageDTO {
    /** CONNECT / MESSAGE / ONLINE / ERROR */
    private String type;
    private Long id;
    private Long fromUserId;
    private String fromUsername;
    private String fromNickname;
    private String fromAvatar;
    private String fromRoleCode;
    private Long toUserId;
    private String toUsername;
    private String toNickname;
    private String toAvatar;
    private String toRoleCode;
    private String content;
    private String msgType;
    private String sendTime;
    private Object data;
}
