package org.example.sotokenspringboot.controller;


import cn.dev33.satoken.stp.StpUtil;
import org.example.sotokenspringboot.pojo.Result;
import org.example.sotokenspringboot.pojo.addr;
import org.example.sotokenspringboot.pojo.goods;
import org.example.sotokenspringboot.pojo.order;
import org.example.sotokenspringboot.service.goodsMisservice;
import org.example.sotokenspringboot.service.orderservice;
import org.example.sotokenspringboot.utils.OrderIdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static kotlin.jvm.internal.Reflection.typeOf;

@RestController
@RequestMapping("/order/")
public class ordercontroller {
    @Autowired
    private orderservice orderservice;

    @Autowired
    private goodsMisservice goodsmisservice;


    @PostMapping("add")
    public Result<String> add(@RequestBody order order) {
        System.out.println(order);
        OrderIdUtil orderIdUtil = new OrderIdUtil(1, 1);
        // 调用生成订单号方法
        String order_id = orderIdUtil.getOrderId();
        if(order.getBuyNum()<=0) return Result.error("请输入正确的购买数量");
        try {
            Integer user_id = Integer.valueOf(StpUtil.getLoginIdAsString());
            List<order> selectorder = orderservice.userselectuser_id(user_id);
            for (order s : selectorder) {
                if (s.getGood_id().equals(order.getGood_id())) {
                    if (s.getState() == 0) {
                        System.out.println("已存在");
                        return new Result<>(3, "有未支付订单", s.getOrder_id());
                    }
                }
            }
            if (order.getGood_id() == null) {
                return Result.error("请选择商品");
            }
            if (order.getFahuo() == 1) {
                if (order.getAddr() == null || order.getAddr().getName()==null) {
                    return Result.error("请选择收货地址");
                }
            }

            goods goods = goodsmisservice.selectid(order.getGood_id());
            if (goods.getStock() < order.getBuyNum() || goods.getStock() < 1) {
                return Result.error("库存不足");
            }
            order.setTotal_price(goods.getPrice() * order.getBuyNum());
            order.setUser_id(user_id);
            order.setState(0);
            order.setFahuo(order.getFahuo());
            order.setMerchant_id(goods.getCreate_id());
            order.setOrder_id(order_id);
            orderservice.add(order);
        } catch (Exception e) {
            return Result.error("下单失败");
        }
        goodsmisservice.updatestock(order.getGood_id(), order.getBuyNum());
        return Result.success(order_id);
    }
    @GetMapping("user/orderidselect")
    private Result<order> orderidselect(String order_id) {
        System.out.println(order_id);
        order order = orderservice.userselectid(order_id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        return Result.success(order);
    }
    @GetMapping("user/orderlist")
    private Result<List<order>> orderlist(Integer pageNum, Integer pageSize) {
        Integer user_id = Integer.valueOf(StpUtil.getLoginIdAsString());
        List<order> list = orderservice.userselectuser_id(user_id);
        return Result.success(list);
    }

    @PutMapping("user/updatestate")
    private Result<String> userupdatestate(String order_id) {
        order order = orderservice.merchantselectid(order_id);
        System.out.println(order.getState());
        System.out.println(order.getState().getClass().getSimpleName());

        if (order.getState() == 3) {
            orderservice.updatestate(order_id, 4);
            return Result.success("收货成功");
        }else{
            return Result.error("订单状态错误");
        }
    }

    @GetMapping("merchant/orderlist")
    private Result<List<order>> orderlist(){
        Integer user_id = Integer.valueOf(StpUtil.getLoginIdAsString());
        List<order> list = orderservice.merchantselectuser_id(user_id);
        return Result.success(list);
    }
    @GetMapping("merchant/orderidselect")
    private Result<order> merchantorderidselect(String order_id) {
        order order = orderservice.merchantselectid(order_id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        return Result.success(order);
    }
    @PutMapping("merchant/updatestate")
    private Result<String> merchantupdatestate(String order_id,String express) {
        System.out.println(order_id);
        order order = orderservice.merchantselectid(order_id);
        System.out.println(order.getState());
        System.out.println(order.getState().getClass().getSimpleName());

        if (order.getState() == 1) {
            if(order.getFahuo()==1){
                if(express==null){
                    return Result.error("请填写物流单号");
                }
                orderservice.updatestateexpress(order_id, 2,express);
                return Result.success("发货成功");
            }
            orderservice.updatestate(order_id, 3);
            return Result.success("发货成功");
        }else{
            return Result.error("订单状态错误");
        }
    }
    @GetMapping("search")
    public Result<List<order>> search(String name) {
        System.out.println(name);
        return Result.success(orderservice.search(name));
    }


}
