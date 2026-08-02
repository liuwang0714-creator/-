package org.example.sotokenspringboot.pojo;


import lombok.Data;

@Data
public class likeComent {
    private Integer id;
    private Integer user_id;
    private Integer comment_id;
    private Integer createAt;
}
