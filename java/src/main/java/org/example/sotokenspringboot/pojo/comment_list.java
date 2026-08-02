package org.example.sotokenspringboot.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class comment_list {
    private Long comment_id;      // 评论ID
    private Long one_comment_id ;
    private String content;      // 评论内容
    private Long user_id;         // 用户ID
    private String nickname;     // 用户名
    private String file_url;    // 头像URL（
    private String comment_url;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
    private Integer to_coment_num;
}