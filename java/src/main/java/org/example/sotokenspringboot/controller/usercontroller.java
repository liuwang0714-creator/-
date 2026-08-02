package org.example.sotokenspringboot.controller;


import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSON;
import org.example.sotokenspringboot.pojo.*;
import org.example.sotokenspringboot.service.adminservice;
import org.example.sotokenspringboot.service.usersvice;
import org.example.sotokenspringboot.utils.wxopenfeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/user/")
public class usercontroller {
    @Autowired
    private usersvice usersvice;
    @Autowired
    private adminservice adminservice;
    @Autowired
    private wxopenfeign wxopenfeign;


    @GetMapping("selectid")
    public Result<Integer> selectid(){
        return Result.success(StpUtil.getLoginIdAsInt());
    }

    @PostMapping("login")
    public Result<Object> login(@RequestBody LoginRequest request) {
        try {
            String username = request.getUsername();
            String password = request.getPassword();
            boolean bool = request.isBool();
            User user = usersvice.findByUsername(username);

            if (user == null) {
                return Result.error("用户名错误");
            }
            if (user.getState() == 1) {
                return Result.error("账号已被封禁,如果问题请联系管理员");
            }else if (user.getState() == 2){
                return Result.error("账号已注销,如果问题请联系管理员");
            }else if (user.getState() == 3){
                return Result.error("账号未激活,请前往激活");
            }
            if (StpUtil.isDisable(user.getId())) {
                return new Result<>(2, "账号已被封禁，暂时无法登录", user.getBan_time());
            }
            if (user.getPassword().equals(SaSecureUtil.md5(password))) {
                StpUtil.login(user.getId(), bool);
                SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
                return Result.success(tokenInfo);
            }
            return Result.error("用户名或密码错误");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("登录失败: " + e.getMessage());
        }
    }

    @PostMapping("uni_im_token")
    public Map<String, Object> uni_im_token(Integer userId) {
        User user = usersvice.findById(userId);
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> mapinfo = new HashMap<String, Object>();


        mapinfo.put("_id",userId);
        mapinfo.put("nickname",user.getNickname());
        mapinfo.put("avatar_file",user.getFile_url());
        map.put("errCode",0);
        map.put("userInfo",mapinfo);
        System.out.println(map);
        return map;

    }

    @RequestMapping("register")
    public Result<String> register(String username, String password,Integer role_id,String nickname) {
        try {
            User user = usersvice.findByUsername(username);
            if (role_id != 2 && role_id != 3) {
                return Result.error("角色id错误");
            }
            if (user != null) {
                return Result.error("用户名已存在");
            }
            User u = new User();
            u.setUser_file_id(1);
            u.setUsername(username);
            u.setPassword(SaSecureUtil.md5(password));
            u.setNickname(nickname);
            u.setLogin_type("1");
            usersvice.register(u);
            Integer quserId = u.getId();
//        role_id是角色id,前端传入2表示商家,3表示用户
            adminservice.roleper(quserId.toString(), role_id.toString());
            return Result.success("注册成功");
        } catch (Exception e) {
            throw new RuntimeException("注册失败"+ e.getMessage());
        }
    }

    @RequestMapping("info")
    public Result<User> info() {
        Integer userId = Integer.valueOf(StpUtil.getLoginIdAsString());
        User user = usersvice.findById(userId);
        user.setToken(StpUtil.getTokenValue());
        return Result.success(user);
    }
    @RequestMapping("infoid")
    public Result<User> infoId(Integer userid) {
        if(userid==null){
            return Result.error("用户id错误");
        }
        User user = usersvice.findById(userid);
        return Result.success(user);
    }

    @PutMapping("updatepwd")
    public Result<String> updatepwd(String oldpassword, String newpassword) {
        try {
            User user = usersvice.findById(Integer.valueOf(StpUtil.getLoginIdAsString()));
            if (user.getPassword().equals(SaSecureUtil.md5(oldpassword))) {
                user.setPassword(SaSecureUtil.md5(newpassword));
                usersvice.updatepwd(user);
                StpUtil.logout(user.getId());
                return Result.success("修改成功");
            } else {
                return Result.error("原密码错误");
            }
        }catch (Exception e) {
            return Result.error("修改失败");
        }
    }

    @RequestMapping("updatetx")
    public Result<String> updatetx(Integer user_file_id) {
        try {
            User user = usersvice.findById(Integer.valueOf(StpUtil.getLoginIdAsString()));
            user.setUser_file_id(user_file_id);
            usersvice.updateprofile(user);
            System.out.println(user);
            return Result.success("修改成功");
        } catch (Exception e) {
            return Result.error("修改失败"+e.getMessage());
        }
    }

    @RequestMapping("updateprofile")
    public Result<String> updateprofile(String nickname, String phone, String birthday) {
        try {
            User user = usersvice.findById(Integer.valueOf(StpUtil.getLoginIdAsString()));
            user.setNickname(nickname);
            user.setPhone(phone);
            user.setBirthday(birthday);
            user.setLogin_type("2");
            usersvice.updateprofile(user);
            return Result.success("修改成功");
        } catch (Exception e) {
            return Result.error("修改失败");
        }
    }
    @RequestMapping("logout")
    public Result<String> logout() {
        StpUtil.logout();
        return Result.success("退出成功");
    }


    @GetMapping("role")
    public Result<String> getRoleList() {
        if(StpUtil.hasRole("admin")){
            return Result.success("admin");
        }else if(StpUtil.hasRole("merchant")){
            return Result.success("merchant");
        }else if(StpUtil.hasRole("user")){
            return Result.success("user");
        }else {
            return Result.success("错误");
        }
    }
    @GetMapping("isLogin")
    public Boolean isLogin(){
        Boolean isLogin = StpUtil.isLogin();
        return isLogin;
    }
    @GetMapping("wxlogin")
    public Result<Object> wxlogin(String code){
        System.out.println(code);
        // 微信小程序 appid 与 secret 应通过配置文件注入，此处为占位符（详见《05-代码修改说明.md》）
        String response=wxopenfeign.getTest("YOUR_WECHAT_APPID",
                "YOUR_WECHAT_APPSECRET", code,
                "authorization_code");
        wxlogin tokenVo = JSON.parseObject(response, wxlogin.class);
        if(tokenVo.getOpenid() != null){
            wxuser wxuser = usersvice.selectwxuser(tokenVo.getOpenid());
            if(wxuser != null){
                StpUtil.login(wxuser.getId());
                SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
                return Result.success(tokenInfo);
            }else{
                Random random = new Random();
                User u = new User();
                u.setUser_file_id(1);
                u.setOpenid(tokenVo.getOpenid());
                u.setUsername(tokenVo.getOpenid());
                u.setNickname("微信用户_"+random.nextInt(900000)+100000);
                u.setLogin_type("2");
                usersvice.register(u);
                adminservice.roleper(u.getId().toString(), "3");
                StpUtil.login(u.getId());
                SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
                return Result.success(tokenInfo);
            }
        }
        return Result.error("登录失败");
    }
}
