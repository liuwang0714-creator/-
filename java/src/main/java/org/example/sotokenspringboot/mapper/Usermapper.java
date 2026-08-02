package org.example.sotokenspringboot.mapper;

import org.apache.ibatis.annotations.*;
import org.example.sotokenspringboot.pojo.Conversation;
import org.example.sotokenspringboot.pojo.User;
import org.example.sotokenspringboot.pojo.wxuser;

import java.util.List;

@Mapper
public interface Usermapper {

    @Select("select * from user where id=#{userId}")
    User findById(Integer userId);

    @Select("select * from user where username=#{username}")
    User findByUsername(String username);

    // 查询角色
    List<String> getRoleListByUserId(Long userId);

    // 查询权限
    List<String> getPermissionListByUserId(Long userId);

    @Insert("insert into user(username,password,login_type,openid,nickname,user_file_id,update_time) values(#{username},#{password},#{login_type},#{openid},#{nickname},#{user_file_id},now())")
    @Options(useGeneratedKeys = true, keyProperty = "id")  // 关键！加这行
    void insert(User user);

    @Update("update user set password=#{password} where id=#{id}")
    void updatepwd(User user);

    @Update("update user set nickname=#{nickname},phone=#{phone},Birthday=#{birthday},user_file_id=#{user_file_id} where id=#{id}")
    void updateprofile(User user);

    @Select("select * from user where openid=#{openid}")
    wxuser selectwxuser(String openid);

    // ========== IM 即时通讯相关查询 ==========

    /** 按用户名查询用户（含角色编码和头像，用于 IM 聊天） */
    @Select("SELECT u.*, r.role_code AS roleCode, f.file_url AS avatar FROM user u " +
            "LEFT JOIN sys_role r ON u.role_id = r.id " +
            "LEFT JOIN file f ON u.user_file_id = f.id " +
            "WHERE u.username = #{username}")
    User findByUsernameWithRole(String username);

    /** 按ID查询用户（含角色编码和头像，用于 IM 聊天） */
    @Select("SELECT u.*, r.role_code AS roleCode, f.file_url AS avatar FROM user u " +
            "LEFT JOIN sys_role r ON u.role_id = r.id " +
            "LEFT JOIN file f ON u.user_file_id = f.id " +
            "WHERE u.id = #{id}")
    User findByIdWithRole(Integer id);

    /** 查询所有正常用户（含角色编码和头像，用于聊天目标列表） */
    @Select("SELECT u.*, r.role_code AS roleCode, f.file_url AS avatar FROM user u " +
            "LEFT JOIN sys_role r ON u.role_id = r.id " +
            "LEFT JOIN file f ON u.user_file_id = f.id " +
            "WHERE u.state = 0 AND r.role_code IS NOT NULL ORDER BY u.id")
    List<User> findChatTargets();

    /** 查询用户最近会话列表 */
    List<Conversation> findConversations(@Param("userId") Long userId);

}
