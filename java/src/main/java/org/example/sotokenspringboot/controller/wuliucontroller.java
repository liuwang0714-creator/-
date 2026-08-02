package org.example.sotokenspringboot.controller;


import org.example.sotokenspringboot.pojo.Result;
import org.example.sotokenspringboot.pojo.order;
import org.example.sotokenspringboot.pojo.wuliu;
import org.example.sotokenspringboot.service.orderservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("wuliu/")
public class wuliucontroller {

    @Autowired
    private orderservice orderservice;

    @RequestMapping("list")
    public Result<List<wuliu>> list() {
        return Result.success(orderservice.wuliuselectuser_id());
    }

    @RequestMapping("updatestate")
    public Result<String> updatestate(String order_id) {
        order order = orderservice.merchantselectid(order_id);
        if(order.getState()==2){
            orderservice.wuliuupdatestate(order_id);
            return Result.success("修改成功");
        }
        return Result.error("修改失败");
    }


}
