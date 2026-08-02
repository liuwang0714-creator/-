package org.example.sotokenspringboot.mapper;

import org.apache.ibatis.annotations.*;
import org.example.sotokenspringboot.pojo.comment;
import org.example.sotokenspringboot.pojo.comment_list;
import org.example.sotokenspringboot.pojo.tocomment;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface commentmapper {

    @Select("select * from comment where goods_id=#{goods_id} limit #{offset},#{pageSize} ")
    List<comment> list(Integer offset, Integer pageSize, Integer goods_id);

    @Select("select count(*) from comment")
    Long count();

    @Insert("insert into comment (comment,user_id, goods_id, file_id) values (#{comment},#{user_id}, #{goods_id},#{file_id})")
    void add(comment comment);

    @Insert("insert into comment_to(comment,user_id,file_id,one_id) values(#{content},#{user_id},#{file_id},#{comment_id})")
    void to_comment(Integer user_id, Integer comment_id, String content, Integer file_id);

    @Delete("delete from comment where id=#{id}")
    void delete(Integer id);

    @Insert("insert into comment_likes(comment_id,user_id) values(#{commentId},#{userId})")
    void likeadd(@Param("userId") Integer userId, @Param("commentId") Integer commentId);

    @Delete("delete from comment_likes where comment_id=#{commentId}")
    void deletelike(@Param("commentId") Integer commentId);

    @Select("SELECT " +
            "  c.id AS comment_id, " +
            "  c.comment AS content, " +
            "  c.create_time AS createTime, " +
            "  u.id AS user_id, " +
            "  u.nickname AS nickname, " +
            "  f1.file_url AS file_url, " +
            "  f2.file_url AS comment_url, " +
            "  c.one_id AS one_comment_id " +
            "FROM comment_to c " +
            "LEFT JOIN `user` u ON c.user_id = u.id " +
            "LEFT JOIN file f1 ON u.user_file_id = f1.id " +
            "LEFT JOIN file f2 ON c.file_id = f2.id " +
            "WHERE c.one_id = #{comment_id} " +
            "ORDER BY c.create_time")
    List<comment_list> to_list(Integer comment_id);

    @Select("select count(*) from comment_to")
    Long to_count();

    @Delete("delete from comment_to where id=#{id}")
    void to_delete(Integer id);

    @Insert("insert into comment_to_likes(comment_to_id,user_id) values(#{commentId},#{userId})")
    void to_likeadd(@Param("userId") Integer userId, @Param("commentId") Integer commentId);

    @Delete("delete from to_comment_likes where comment_to_id=#{commentId}")
    void to_deletelike(@Param("commentId") Integer commentId);

    //    @Select("SELECT * FROM comment c WHERE c.goods_id = #{goods_id}")
    //    List<Map<String, Object>> getCommentListByArticleId(@Param("goods_id") Integer goods_id);
    @Select("SELECT " +
            "  c.id AS comment_id, " +
            "  c.comment AS content, " +
            "  c.create_time AS createTime, " +
            "  u.id AS user_id, " +
            "  u.nickname AS nickname, " +
            "  f1.file_url AS file_url, " +
            "  f2.file_url AS comment_url, " +
            "  c.to_coment_num " +
            "FROM comment c " +
            "LEFT JOIN `user` u ON c.user_id = u.id " +
            "LEFT JOIN file f1 ON u.user_file_id = f1.id " +
            "LEFT JOIN file f2 ON c.file_id = f2.id " +
            "WHERE c.goods_id = #{goods_id} " +
            "ORDER BY c.create_time DESC")
    List<comment_list> getCommentListByArticleId(@Param("goods_id") Integer goods_id);

    @Update("update comment set to_coment_num = to_coment_num + 1 where id = #{comment_id}")
    void to_coment_add(@Param("comment_id") Integer comment_id);

    @Update("update comment set to_coment_num = to_coment_num - 1 where id = #{comment_id}")
    void to_coment_delete(@Param("comment_id") Integer comment_id);


    @Select("select * from comment where id = #{id}")
    comment selectbyid(Integer id);

    @Select("select * from comment_to where id = #{id}")
    comment selectbytoid(Integer id);
}
