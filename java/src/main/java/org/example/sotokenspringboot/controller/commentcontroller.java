package org.example.sotokenspringboot.controller;


import cn.dev33.satoken.stp.StpUtil;
import org.example.sotokenspringboot.pojo.*;
import org.example.sotokenspringboot.service.commentservice;
import org.example.sotokenspringboot.service.fileservice;
import org.example.sotokenspringboot.utils.ContentCheckUtil;
import org.example.sotokenspringboot.utils.DeleteFileNioDemo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment/")
public class commentcontroller {
    @Autowired
    private commentservice commentservice;

    @Autowired
    private fileservice fileservice;

    @RequestMapping("list")
    public Result<PageBean<comment>> list(Integer pageNum, Integer pageSize,Integer goods_id) {
        return Result.success(commentservice.list(pageNum, pageSize,goods_id));
    }
    @RequestMapping("add")
    public Result<String> add(String content, Integer goods_id,@RequestParam(required = false) Integer file_id) {
        try{
            if (content==null || content.trim().isEmpty() || content.length() > 1000){
                return Result.error("参数错误");
            }
            try{
                if(ContentCheckUtil.finalCheck(content).equals("有违规")){
                    return Result.error("内容违规");
                }
            }catch (Exception e) {
                return Result.error("内容检查失败");
            }
            comment comment = new comment();
            comment.setUser_id(StpUtil.getLoginIdAsInt());
            comment.setComment(content);
            comment.setGoods_id(goods_id);
            comment.setFile_id(file_id);
            commentservice.add(comment);
            return Result.success("评论成功");
        }catch (Exception e){
            return Result.error("评论失败"+e.getMessage());
        }
    }

    @DeleteMapping("delete")
    public Result<String> delete(Integer id,String file_url) {
        comment comment = commentservice.selectbyid(id);
        if(comment==null){
            return Result.error("评论不存在");
        }
        if(comment.getUser_id()==StpUtil.getLoginIdAsInt() || StpUtil.getLoginIdAsInt()==1){
            if(id== null){
                return Result.error("没有权限");
            }else{
                if(file_url==null){
                    commentservice.delete(id);
                    return Result.success("删除成功");
                }else{
                    commentservice.delete(id);
                    fileservice.delete(file_url);
                    DeleteFileNioDemo.deleteFileNio(file_url);
                    return Result.success("删除成功");
                }
            }
        }else{
            return Result.error("没有权限");
        }
    }
    @PutMapping("like")
    public Result<String> likeadd(Integer commentId) {
        try{
            if(commentId==null){
                return Result.error("参数错误");
            }
            commentservice.likeadd(StpUtil.getLoginIdAsInt(),commentId);
            return Result.success("点赞成功");
        }catch (Exception e){
            return Result.error("点赞失败");
        }
    }
    @DeleteMapping("like")
    public Result<String> deletelike(Integer commentId) {
        try{
            if(commentId==null){
                return Result.error("参数错误");
            }
            commentservice.deletelike(commentId);
            return Result.success("取消点赞成功");
        }catch (Exception e){
            return Result.error("取消点赞失败");
        }
    }


    @GetMapping("comment_list")
    public Result<PageBean<comment_list>> comment_list(Integer goods_id) {
        return Result.success(commentservice.getCommentListByArticleId(goods_id));
    }


    @PutMapping("to_comment")
    public Result<String> to_comment(String content,Integer comment_id, @RequestParam(required = false) Integer file_id) {
        try{
            if(comment_id==null){
                return Result.error("参数错误");
            }
            try{
                if(ContentCheckUtil.finalCheck(content).equals("有违规")) {
                    return Result.error("内容违规");
                }
            }catch (Exception e) {
                return Result.error("内容检查失败");
            }
            Integer user_id=StpUtil.getLoginIdAsInt();
            commentservice.to_comment(user_id,comment_id,content,file_id);
            commentservice.to_coment_add(comment_id);
            return Result.success("回复成功");
        }catch (Exception e){
            return Result.error("回复失败"+e.getMessage());
        }
    }

    @GetMapping("to_comment_list")
    public Result<PageBean<comment_list>> to_comment_list(Integer comment_id) {
        return Result.success(commentservice.to_list(comment_id));
    }

    @DeleteMapping("to_delete")
    public Result<String> to_delete(@RequestParam Integer id, @RequestParam String file_url) {
        comment comment = commentservice.selectbytoid(id);
        if(comment==null){
            return Result.error("评论不存在");
        }
        if(comment.getUser_id()==StpUtil.getLoginIdAsInt() || StpUtil.getLoginIdAsInt()==1){
            if(file_url==null || id== null){
                return Result.error("没有权限");
            }else{
                commentservice.to_delete(id);
                commentservice.to_coment_delete(comment.getOne_id());
                fileservice.delete(file_url);
                DeleteFileNioDemo.deleteFileNio(file_url);
                return Result.success("删除成功");
            }
        }else{
            return Result.error("没有权限");
        }
    }
    @PutMapping("to_like")
    public Result<String> to_likeadd(Integer commentId) {
        try{
            if(commentId==null){
                return Result.error("参数错误");
            }
            commentservice.to_likeadd(StpUtil.getLoginIdAsInt(),commentId);
            return Result.success("点赞成功");
        }catch (Exception e){
            return Result.error("点赞失败");
        }
    }
    @DeleteMapping("to_like")
    public Result<String> to_deletelike(Integer commentId) {
        try{
            if(commentId==null){
                return Result.error("参数错误");
            }
            commentservice.to_deletelike(commentId);
            return Result.success("取消点赞成功");
        }catch (Exception e){
            return Result.error("取消点赞失败");
        }
    }
}
