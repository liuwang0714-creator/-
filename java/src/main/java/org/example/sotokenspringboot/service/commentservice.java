package org.example.sotokenspringboot.service;

import org.example.sotokenspringboot.pojo.PageBean;
import org.example.sotokenspringboot.pojo.comment;
import org.example.sotokenspringboot.pojo.comment_list;
import org.example.sotokenspringboot.pojo.tocomment;

public interface commentservice {

    PageBean<comment> list(Integer pageNum, Integer pageSize, Integer goods_id);

    void add(comment comment);

    void delete(Integer id);

    void likeadd(Integer userId, Integer commentId);

    void deletelike(Integer comment_id);

    void to_comment(Integer user_id, Integer comment_id, String content, Integer file_id);

    PageBean<comment_list> to_list(Integer comment_id);

    void to_delete(Integer id);

    void to_likeadd(Integer userId, Integer commentId);

    void to_deletelike(Integer comment_id);

    PageBean<comment_list> getCommentListByArticleId(Integer goods_id);

    void to_coment_add(Integer comment_id);

    void to_coment_delete(Integer comment_id);

    comment selectbyid(Integer id);
    comment selectbytoid(Integer to_id);
}
