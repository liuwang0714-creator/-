package org.example.sotokenspringboot.service.serviceimpl;

import org.example.sotokenspringboot.mapper.commentmapper;
import org.example.sotokenspringboot.pojo.*;
import org.example.sotokenspringboot.service.commentservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class commentserviceimpl implements commentservice {

    @Autowired
    private commentmapper commentmapper;
    @Override
    public PageBean<comment> list(Integer pageNum, Integer pageSize, Integer goods_id){
        int offset = (pageNum - 1) * pageSize;
        List<comment> list = commentmapper.list(offset, pageSize, goods_id);
        Long total = commentmapper.count();
        PageBean<comment> pb = new PageBean<>();
        pb.setTotal(total);
        pb.setItems(list);
        return pb;
    }

    @Override
    public PageBean<comment_list> getCommentListByArticleId(Integer goods_id){
        List<comment_list> list = commentmapper.getCommentListByArticleId(goods_id);

        Long total = commentmapper.count();
        PageBean<comment_list> pb = new PageBean<>();
        pb.setTotal(total);
        pb.setItems(list);
        return pb;
    }

    @Override
    public void to_coment_add(Integer comment_id){
        commentmapper.to_coment_add(comment_id);
    }

    @Override
    public void to_coment_delete(Integer comment_id){
        commentmapper.to_coment_delete(comment_id);
    }


    @Override
    public void add(comment comment){
        commentmapper.add(comment);
    }

    @Override
    public void delete(Integer id){
        commentmapper.delete(id);
    }

    @Override
    public void likeadd(Integer userId, Integer commentId){
        commentmapper.likeadd(userId, commentId);
    }

    @Override
    public void deletelike(Integer comment_id){
        commentmapper.deletelike(comment_id);
    }

    @Override
    public void to_comment(Integer user_id, Integer comment_id, String content, Integer file_id){
        commentmapper.to_comment(user_id, comment_id, content, file_id);
    }

    @Override
    public PageBean<comment_list> to_list(Integer comment_id){
        List<comment_list> list = commentmapper.to_list(comment_id);
        Long total = commentmapper.to_count();
        PageBean<comment_list> pb = new PageBean<>();
        pb.setTotal(total);
        pb.setItems(list);
        return pb;
    }

    @Override
    public void to_delete(Integer id){
        commentmapper.to_delete(id);
    }

    @Override
    public void to_likeadd(Integer userId, Integer commentId){
        commentmapper.to_likeadd(userId, commentId);
    }

    @Override
    public void to_deletelike(Integer comment_id){
        commentmapper.to_deletelike(comment_id);
    }

    @Override
    public comment selectbyid(Integer id){
        return commentmapper.selectbyid(id);
    }

    @Override
    public comment selectbytoid(Integer to_id){
        return commentmapper.selectbytoid(to_id);
    }

}
