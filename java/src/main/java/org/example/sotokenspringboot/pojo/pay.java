package org.example.sotokenspringboot.pojo;


import lombok.Data;

@Data
public class pay {

    private String order_id;
    private String pay_type;
    private String pay_state;
    private Double pay_price;
    private String create_time;
}
