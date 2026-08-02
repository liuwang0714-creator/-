package org.example.sotokenspringboot.service;

import org.springframework.stereotype.Service;

@Service
public class ChatPermissionService {

    public boolean canChat(String fromRole, String toRole) {
        if (fromRole == null || toRole == null || fromRole.equals(toRole)) {
            return false;
        }
        // admin 可与所有其他角色会话
        if ("admin".equals(fromRole)) {
            return true;
        }
        // merchant 可与 user、admin 会话
        if ("merchant".equals(fromRole)) {
            return "user".equals(toRole) || "admin".equals(toRole);
        }
        // user 可与 merchant、admin 会话
        if ("user".equals(fromRole)) {
            return "merchant".equals(toRole) || "admin".equals(toRole);
        }
        return false;
    }

    public String getDenyReason(String fromRole, String toRole) {
        if (fromRole == null || toRole == null) {
            return "用户角色信息异常，无法发起会话";
        }
        if (fromRole.equals(toRole)) {
            return "不能与自己发起会话";
        }
        return switch (fromRole) {
            case "admin" -> "无权与该用户发起会话";
            case "merchant" -> "商家仅可与普通用户或管理员发起会话";
            case "user" -> "普通用户仅可与商家或管理员发起会话";
            default -> "当前角色无权发起会话";
        };
    }
}
