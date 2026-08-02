package org.example.sotokenspringboot.service.serviceimpl;

import cn.dev33.satoken.stp.StpInterface;
import org.example.sotokenspringboot.mapper.Usermapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StpInterfaceImpl implements StpInterface {

    // 注入你的 Mapper
    @Autowired
    private Usermapper userMapper;

    /**
     * 返回【权限列表】：从数据库查
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // loginId 就是登录时的 userId
        Long userId = Long.parseLong(loginId.toString());

        // 去数据库查：这个用户拥有哪些权限（SQL我下面给你）
        return userMapper.getPermissionListByUserId(userId);
    }

    /**
     * 返回【角色列表】：从数据库查
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        Long userId = Long.parseLong(loginId.toString());

        // 去数据库查：这个用户是什么角色（admin/merchant/user）
        return userMapper.getRoleListByUserId(userId);
    }
}