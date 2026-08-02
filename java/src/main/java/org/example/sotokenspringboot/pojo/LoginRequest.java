package org.example.sotokenspringboot.pojo;

import lombok.Data;

@Data
public class LoginRequest {
    String username;
    String password;
    boolean bool;
}
