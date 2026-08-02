package org.example.sotokenspringboot.pojo;

import lombok.Data;

@Data
public class OnlineUserDTO {
    private Long id;
    private String username;
    private String nickname;
    private String roleCode;
    private String avatar;
}
