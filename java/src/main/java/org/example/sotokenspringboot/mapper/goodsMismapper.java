package org.example.sotokenspringboot.mapper;

import org.apache.ibatis.annotations.*;
import org.example.sotokenspringboot.pojo.goods;
import org.springframework.data.repository.query.Param;

import java.util.List;
@Mapper
public interface goodsMismapper {
    @Select("select * from goods where create_id=#{userId} limit #{offset},#{pageSize} ")
    List<goods> goodslist(@Param("offset") Integer offset, @Param("pageSize") Integer pageSize, @Param("userId") Integer userId);


    @Select("select count(*) from goods where create_id=#{userId}")
    Long goodscount(@Param("userId") Integer userId);


    @Insert("insert into goods(goods_name,price,stock,description,create_time,create_id,goods_file_id) values(#{goods.goods_name},#{goods.price},#{goods.stock},#{goods.description},now(),#{userId},#{goods.goods_file_id})")
    void add(@Param("goods") goods goods, @Param("userId") Integer userId);

    @Select("select * from goods where id=#{id}")
    goods selectid(Integer id);

    @Update("update goods set goods_name=#{goods_name}, price=#{price}, stock=#{stock}, description=#{description}, update_time=now(), goods_file_id=#{goods_file_id} where id=#{id}")
    void update(goods goods);

    @Delete("delete from goods where id=#{id}")
    void delete(Integer id);

    @Update("update goods set stock = stock - #{buyNum} where id = #{goods_id}")
    void updatestock(@Param("goods_id") Integer goods_id, @Param("buyNum") Integer buyNum);

    @Select("select g.*,f.file_url from goods as g " +
            "INNER JOIN file as f ON g.goods_file_id =f.id " +
            "where g.goods_name LIKE CONCAT('%', #{name}, '%') " +
            "or g.description LIKE CONCAT('%', #{name}, '%')")
    List<goods> search(String name);
}