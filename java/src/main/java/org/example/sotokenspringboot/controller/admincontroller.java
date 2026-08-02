package org.example.sotokenspringboot.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;
import org.example.sotokenspringboot.pojo.*;
import org.example.sotokenspringboot.service.adminservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/")
public class admincontroller {
    @Autowired
    private adminservice adminservice;
//    对用户进行角色授权
    @RequestMapping("roleper")
    public Result<String> roleper(String userId, String roleId) {
        adminservice.roleper(userId, roleId);
        return Result.success("修改成功");
    }

    @RequestMapping("userlist")
    public Result<PageBean<User>> userlist(Integer pageNum, Integer pageSize){;
        return Result.success(adminservice.userlist(pageNum,pageSize));
    }
    @RequestMapping("state")
    public Result<String> state(Integer userid, Integer times) {
        if(userid == 1){
            return Result.error("此用户为超级管理员");
        }
        if (times == 0) {
            StpUtil.untieDisable(userid);
            adminservice.banUser(userid, 0);
            return Result.success("解封成功");
        }
        long second = (long) times * 60 * 60;
        long now = System.currentTimeMillis();
        long banEndTime = now + second * 1000;

        adminservice.banUser(userid, banEndTime);
        StpUtil.logout(userid);
        StpUtil.disable(userid, second);
        return Result.success("禁用成功,已封禁"+times+"小时");
    }
    @SaCheckRole("admin")
    @PutMapping("updatepwd")
    public Result<String> updatepwd(Integer userid,String password, String rpassword) {
        try {
            if (password.equals(rpassword)) {
                adminservice.updatepwd(userid,SaSecureUtil.md5(rpassword));
                return Result.success("修改成功");
            }else {
                return Result.error("密码不一致");
            }
        }catch (Exception e) {
            return Result.error("修改失败"+e.getMessage());
        }

    }
    @GetMapping("state_user")
    public Result<String> getStateList(Integer state,Integer userid) {
        if(userid==1){
            return Result.error("此用户为超级管理员");
        }
        adminservice.updateState(state,userid);
        return Result.success("操作成功");
    }
    @PutMapping("role_user")
    public Result<String> role_user(Integer userId,Integer roleId) {
        if(roleId!=2 && roleId!=3){
            return Result.error("请选择正确的角色");
        }
        if(userId==1){
            return Result.error("此用户为超级管理员");
        }
        adminservice.role_user (userId,roleId);
        return Result.success("操作成功");
    }

    @GetMapping("orderlist")
    public Result<PageBean<order>> orderlist(Integer pageNum, Integer pageSize) {
        return Result.success(adminservice.orderlist(pageNum,pageSize));
    }

    @GetMapping("updateorderstate")
    public Result<String> updateorderstate(String orderid,Integer state,Integer fahuo) {
        adminservice.updateorderstate(orderid,state,fahuo);
        return Result.success("操作成功");
    }

    @GetMapping("zongcount")
    public Result<adminhx> zongcount() {
        return Result.success(adminservice.zongcount());
    }

    @GetMapping("getordercount")
    public Result<List<dpfh>> getordercount() {
        return Result.success(adminservice.getordercount());
    }

    @GetMapping("getamount")
    public Result<List<dpfh>> getamount() {
        return Result.success(adminservice.getamount());
    }

    @GetMapping("getgoodstop10")
    public Result<List<goodscount>> getgoodstop10() {
        return Result.success(adminservice.getgoodstop10());
    }

    @GetMapping("search")
    public Result<List<User>> search(String name){
        return Result.success(adminservice.search(name));
    }


}


