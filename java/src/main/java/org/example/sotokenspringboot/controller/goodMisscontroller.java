package org.example.sotokenspringboot.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import org.example.sotokenspringboot.pojo.PageBean;
import org.example.sotokenspringboot.pojo.Result;
import org.example.sotokenspringboot.pojo.goods;
import org.example.sotokenspringboot.service.adminservice;
import org.example.sotokenspringboot.service.goodsMisservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/goods/")
public class goodMisscontroller {
    @Autowired
    private goodsMisservice goodmissservice;

    @Autowired
    private adminservice adminservice;

    @RequestMapping("/list")
    public Result<PageBean<goods>> list(Integer pageNum, Integer pageSize) {
        Integer userId = Integer.valueOf(StpUtil.getLoginIdAsString());
        return Result.success(goodmissservice.goodslist(pageNum, pageSize, userId));
    }
    @PostMapping("/add")
    public Result<String> add(@RequestBody goods goods) {
        Integer userId = Integer.valueOf(StpUtil.getLoginIdAsString());
        goodmissservice.add(goods,userId);
        return Result.success("增加成功");
    }
    @GetMapping("/selectid")
    public Result<goods> selectid(Integer id) {
        return Result.success(goodmissservice.selectid(id));
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody goods goods) {
        try {
            goodmissservice.update(goods);
            return Result.success("修改成功");
        } catch (Exception e) {
            return Result.error("修改失败"+e.getMessage());
         }
    }

    @DeleteMapping("/delete")
    public Result<String> delete(Integer id) {
        goodmissservice.delete(id);
        return Result.success("删除成功");
    }
    @RequestMapping("/goodslist")
    public Result<List<goods>> goodslist() {
        return Result.success(adminservice.goodslist());
    }

    @GetMapping("/search")
    public Result<List<goods>> search(String name) {
        System.out.println(name);
        return Result.success(goodmissservice.search(name));
    }


}
