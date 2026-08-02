package org.example.sotokenspringboot.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class tocomment {
    private Integer id;
    private String comment;
    private Integer user_id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime create_time;
    private Integer like_count;
    private Integer file_id;
    private Integer one_id;
}