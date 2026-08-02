package org.example.sotokenspringboot.controller;

import org.example.sotokenspringboot.pojo.Result;
import org.example.sotokenspringboot.pojo.order;
import org.example.sotokenspringboot.pojo.pay;
import org.example.sotokenspringboot.service.orderservice;
import org.example.sotokenspringboot.service.payService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pay/")
public class paycontroller {

    @Autowired
    private orderservice orderservice;

    @Autowired
    private payService payService;

    @RequestMapping("wx")
    public Result<String> wx(String order_id){
        pay pay=payService.selectid(order_id);
        if(pay!=null){
            return Result.success("订单已支付");
        }
        order order=orderservice.userselectid(order_id);

        if(order==null){
            return Result.success("已支付");
        }
        if(!order.getState().equals(0)){
            return Result.success("订单已支付");
        }
        payService.wx(order.getOrder_id(),"1","1",order.getTotal_price());
        orderservice.updatestate(order.getOrder_id(),1);
        return Result.success("微信支付成功" + order_id);
    }

    @RequestMapping("zfb")
    public Result<String> zfb( String order_id) {
        pay pay=payService.selectid(order_id);
        if(pay!=null){
            return Result.success("已支付");
        }
        order order=orderservice.userselectid(order_id);
        if(order==null){
            return Result.success("订单不存在");
        }
        if(!order.getState().equals(0)){
            return Result.success("订单已支付");
        }
        payService.zfb(order.getOrder_id(),"1","1",order.getTotal_price());
        orderservice.updatestate(order.getOrder_id(),1);
        return Result.success("支付宝支付成功" + order_id);
    }
}
