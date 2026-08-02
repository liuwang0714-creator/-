package org.example.sotokenspringboot.service.serviceimpl;

import org.example.sotokenspringboot.mapper.Usermapper;
import org.example.sotokenspringboot.mapper.filemapper;
import org.example.sotokenspringboot.pojo.Conversation;
import org.example.sotokenspringboot.pojo.Files;
import org.example.sotokenspringboot.pojo.User;
import org.example.sotokenspringboot.pojo.wxuser;
import org.example.sotokenspringboot.service.ChatPermissionService;
import org.example.sotokenspringboot.service.usersvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class userserviceimpl implements usersvice {
    @Autowired
    private Usermapper usermapper;

    @Autowired
    private filemapper filemapper;

    @Autowired
    private ChatPermissionService chatPermissionService;


    @Override
    public User findById(Integer userId) {
        User u = usermapper.findById(userId);
        if (u == null) {
            return null;
        }

        if (u.getUser_file_id() == null) {
            u.setFile_url("9a26b684-6add-406b-ae9f-912a5b8c9e62.png");
            return u;
        }

        Files f = filemapper.selectId(u.getUser_file_id());
        if (f != null) {
            u.setFile_url(f.getFile_url());
        }
        return u;
    }

    @Override
    public User findByUsername(String username) {
        return usermapper.findByUsername(username);
    }

    @Override
    public void register(User user) {
        user.setUser_file_id(1);
        usermapper.insert(user);
    }
    @Override
    public void updatepwd(User user) {
        usermapper.updatepwd(user);
    }
    @Override
    public void updateprofile(User user) {
        usermapper.updateprofile(user);
    }

    public wxuser selectwxuser(String openid) {
        return usermapper.selectwxuser(openid);
    }

    // ========== IM 即时通讯方法 ==========

    @Override
    public User findByUsernameWithRole(String username) {
        User user = usermapper.findByUsername(username);
        if (user == null) return null;
        // 手动查角色编码（避免 MyBatis 列别名映射问题）
        List<String> roles = usermapper.getRoleListByUserId(user.getId().longValue());
        if (!roles.isEmpty()) {
            user.setRoleCode(roles.get(0));
        }
        // 查头像
        if (user.getUser_file_id() != null) {
            Files f = filemapper.selectId(user.getUser_file_id());
            if (f != null) user.setAvatar(f.getFile_url());
        }
        return user;
    }

    @Override
    public User findByIdWithRole(Integer id) {
        User user = usermapper.findById(id);
        if (user == null) return null;
        List<String> roles = usermapper.getRoleListByUserId(user.getId().longValue());
        if (!roles.isEmpty()) {
            user.setRoleCode(roles.get(0));
        }
        if (user.getUser_file_id() != null) {
            Files f = filemapper.selectId(user.getUser_file_id());
            if (f != null) user.setAvatar(f.getFile_url());
        }
        return user;
    }

    @Override
    public List<User> findChatTargets(String currentUsername) {
        User current = findByUsernameWithRole(currentUsername);
        if (current == null || current.getRoleCode() == null) {
            return List.of();
        }
        List<User> allTargets = usermapper.findChatTargets();
        return allTargets.stream()
                .filter(u -> !u.getUsername().equals(currentUsername))
                .filter(u -> chatPermissionService.canChat(current.getRoleCode(), u.getRoleCode()))
                .peek(u -> u.setPassword(null))
                .toList();
    }

    @Override
    public List<Conversation> findConversations(String username) {
        User current = findByUsernameWithRole(username);
        if (current == null) {
            return List.of();
        }
        return usermapper.findConversations(current.getId().longValue()).stream()
                .filter(c -> c != null && c.getPartnerRoleCode() != null
                        && chatPermissionService.canChat(current.getRoleCode(), c.getPartnerRoleCode()))
                .toList();
    }

    @Override
    public boolean canChatWith(String fromUsername, String toUsername) {
        if (fromUsername == null || toUsername == null || fromUsername.equals(toUsername)) {
            return false;
        }
        User from = findByUsernameWithRole(fromUsername);
        User to = findByUsernameWithRole(toUsername);
        if (from == null || to == null) {
            return false;
        }
        if (from.getRoleCode() == null || to.getRoleCode() == null) {
            return false;
        }
        if (from.getState() != null && from.getState() != 0) {
            return false;
        }
        if (to.getState() != null && to.getState() != 0) {
            return false;
        }
        return chatPermissionService.canChat(from.getRoleCode(), to.getRoleCode());
    }

    @Override
    public String getChatDenyReason(String fromUsername, String toUsername) {
        if (fromUsername.equals(toUsername)) {
            return "不能与自己发起会话";
        }
        User from = findByUsernameWithRole(fromUsername);
        User to = findByUsernameWithRole(toUsername);
        if (from == null || to == null) {
            return "用户不存在";
        }
        return chatPermissionService.getDenyReason(from.getRoleCode(), to.getRoleCode());
    }
}
