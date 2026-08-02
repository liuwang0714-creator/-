package org.example.sotokenspringboot.pojo;

import lombok.Data;

@Data
public class adminhx {
    private Integer total_user;
    private Integer today_new_user;
    private Integer total_merchant;
    private Integer total_goods;
    private Integer total_order;//总订单、
    private Integer total_turnover;//累计成交额、
    private Integer wait_delivery_order;//待发货订单、
    private Integer wait_pay_order;//待支付订单
}
