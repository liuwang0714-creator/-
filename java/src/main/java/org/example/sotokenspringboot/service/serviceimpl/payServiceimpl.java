package org.example.sotokenspringboot.service.serviceimpl;

import org.example.sotokenspringboot.mapper.paymapper;
import org.example.sotokenspringboot.pojo.pay;
import org.example.sotokenspringboot.service.payService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class payServiceimpl implements payService {

    @Autowired
    private paymapper paymapper;

    @Override
    public void wx(String order_id,String pay_type,String pay_state,Double pay_price ){
        paymapper.wx(order_id,pay_type,pay_state,pay_price);
    }


    @Override
    public void zfb(String order_id,String pay_type,String pay_state,Double pay_price ) {
        paymapper.zfb(order_id, pay_type, pay_state, pay_price);
    }

    @Override
    public pay selectid(String order_id) {
        return paymapper.selectid(order_id);
    }
}
