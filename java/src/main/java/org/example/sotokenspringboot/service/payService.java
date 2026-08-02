package org.example.sotokenspringboot.service;


import org.example.sotokenspringboot.pojo.pay;
import org.springframework.stereotype.Service;

@Service
public interface payService {


    void wx(String order_id,String pay_type,String pay_state,Double pay_price );

    void zfb(String order_id,String pay_type,String pay_state,Double pay_price );


    pay selectid(String order_id);
}
