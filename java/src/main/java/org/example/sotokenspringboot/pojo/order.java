package org.example.sotokenspringboot.pojo;

import lombok.Data;

@Data
public class order {
    private Integer good_id;
    private String order_id;
    private Integer buyNum;
    private addr addr;
    private Integer coupon;
    private Integer fahuo;
    private Integer user_id;
    private Integer merchant_id;
    private String beizhu;
    private Double total_price;
    private String create_time;
    private String update_time;
    private Integer state;
    private String pay_time;
    private String name;
    private String phone;
    private String addr_detail;
    private String goods_name;
    private String goods_file_url;
    private String nickname;
    private String file_url;
}
