package org.example.sotokenspringboot.mapper;


import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.example.sotokenspringboot.pojo.Files;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Mapper
public interface filemapper {

    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    @Insert("insert into file(file_url,create_id) values(#{file_url},#{create_id})")
    Integer insetFIle(Files files);


    @Select("select * from file where id=#{id}")
    Files selectId(Integer id);

    @Select({
            "<script>",
            "SELECT id, file_url FROM file",
            "WHERE id IN",
            "<foreach collection='ids' item='item' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "</script>"
    })
    List<Files> selectBatchIds(@Param("ids") List<Integer> ids);

    @Select("delete from file where file_url=#{url}")
    void delete(String url);
}
