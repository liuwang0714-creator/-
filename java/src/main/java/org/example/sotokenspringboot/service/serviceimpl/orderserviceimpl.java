package org.example.sotokenspringboot.service.serviceimpl;


import org.example.sotokenspringboot.mapper.ordermapper;
import org.example.sotokenspringboot.pojo.order;
import org.example.sotokenspringboot.pojo.wuliu;
import org.example.sotokenspringboot.service.orderservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class orderserviceimpl implements orderservice {
    @Autowired
    private ordermapper ordermapper;

    @Override
    public void add(order order) {
        ordermapper.add(order);
    }

    @Override
    public List<order> userselectuser_id(Integer user_id) {
        return ordermapper.userselectuser_id(user_id);
    }

    @Override
    public List<order> merchantselectuser_id(Integer user_id) {
        return ordermapper.merchantselectuser_id(user_id);
    }

    @Override
    public order userselectid(String order_id) {
        return ordermapper.userselectid(order_id);
    }

    @Override
    public order merchantselectid(String order_id) {
        return ordermapper.merchantselectid(order_id);
    }

    @Override
    public void updatestate(String order_id, Integer state) {
        ordermapper.updatestate(order_id,state);
    }

    @Override
    public void updatestateexpress(String order_id,Integer state,String express){
        ordermapper.updatestateexpress(order_id,state,express);
    }

    @Override
    public List<wuliu> wuliuselectuser_id() {
        return ordermapper.wuliuselectuser_id();
    }

    @Override
    public void wuliuupdatestate(String order_id) {
        ordermapper.wuliuupdatestate(order_id);
    }

    @Override
    public List<order> search(String name){
        return ordermapper.search(name);
    }



}
