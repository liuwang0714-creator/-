package org.example.sotokenspringboot.pojo;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Integer id;
    private String username;
    @JsonIgnore
    private String password;
    private String nickname;
    private String phone;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
    private Integer user_file_id;
    private String file_url;
    private String birthday;
    private Integer state;
    private long ban_time;
    private Integer role_id;
    private String login_type;
    private String openid;
    private String token;

    // IM 聊天相关字段（由 SQL 左连接填充）
    private String roleCode;
    private String avatar;
}
