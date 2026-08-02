package org.example.sotokenspringboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.sotokenspringboot.pojo.Message;

import java.util.List;

@Mapper
public interface MessageMapper {

    int insert(Message message);

    List<Message> findByUsernames(@Param("username1") String username1,
                                  @Param("username2") String username2,
                                  @Param("limit") int limit);
}
