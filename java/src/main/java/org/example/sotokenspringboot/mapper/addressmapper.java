package org.example.sotokenspringboot.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.sotokenspringboot.pojo.addr;

import java.util.List;

@Mapper
public interface addressmapper {
    @Insert("insert into address(user_id,phone,name,addr) values(#{user_id},#{phone},#{name},#{addr})")
    void add(addr addr);
    @Select("select * from address where user_id=#{user_id}")
    List<addr> list(Integer user_id);
    @Delete("delete from address where id=#{id}")
    void delete(Integer id);
}
