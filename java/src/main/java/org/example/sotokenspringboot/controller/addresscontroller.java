package org.example.sotokenspringboot.controller;


import cn.dev33.satoken.stp.StpUtil;
import org.example.sotokenspringboot.pojo.Result;
import org.example.sotokenspringboot.pojo.addr;
import org.example.sotokenspringboot.service.addressservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address/")
public class addresscontroller {
    @Autowired
    private addressservice addressservice;

    @PutMapping("add")
    public Result<String> add(@RequestBody addr addr) {
        try{
            if(addr.getName()==null||addr.getName().equals("")||addr.getPhone()==null||addr.getPhone().equals("")||addr.getAddr()==null||addr.getAddr().equals("")){
                return Result.error("请填写完整信息");
            }
            Integer userId = Integer.valueOf(StpUtil.getLoginIdAsString());
            addr.setUser_id(userId);
            addressservice.add(addr);
            return Result.success("添加成功");
        }catch (Exception e){
            return Result.error("添加失败"+e.getMessage());
        }
    }

    @RequestMapping("list")
    public Result<List<addr>> list() {
        Integer user_id = Integer.valueOf(StpUtil.getLoginIdAsString());
        return Result.success(addressservice.list(user_id));

    }
    @DeleteMapping("delete")
    public Result<String> delete(Integer id) {
        addressservice.delete(id);
        return Result.success("删除成功");
    }
}
