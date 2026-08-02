package org.example.sotokenspringboot.service;

import org.example.sotokenspringboot.pojo.Conversation;
import org.example.sotokenspringboot.pojo.User;
import org.example.sotokenspringboot.pojo.wxuser;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface usersvice {

    User findById(Integer userId);

    User findByUsername(String username);

    void register(User user);
    void updatepwd(User user);
    void updateprofile(User user);

    wxuser selectwxuser(String appid);

    // ========== IM 即时通讯 ==========

    /** 带角色信息的用户查询 */
    User findByUsernameWithRole(String username);
    User findByIdWithRole(Integer id);

    /** 获取可聊天目标用户列表 */
    List<User> findChatTargets(String currentUsername);

    /** 获取会话列表 */
    List<Conversation> findConversations(String username);

    /** 检查两个用户是否可聊天 */
    boolean canChatWith(String fromUsername, String toUsername);

    /** 获取不可聊天的原因 */
    String getChatDenyReason(String fromUsername, String toUsername);
}
