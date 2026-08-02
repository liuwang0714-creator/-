package org.example.sotokenspringboot.pojo;


import lombok.Data;

@Data
public class wxlogin {
    private String openid;
    private String session_key;
    private String unionid;
    private Integer errcode;
    private String errmsg;
}