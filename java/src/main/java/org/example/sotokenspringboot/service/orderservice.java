package org.example.sotokenspringboot.service;

import org.example.sotokenspringboot.pojo.order;
import org.example.sotokenspringboot.pojo.wuliu;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface orderservice {

    void add(order order);

    List<order> userselectuser_id(Integer user_id);

    order userselectid(String order_id);

    void updatestate(String order_id,Integer state);

    List<order> merchantselectuser_id(Integer user_id);

    order merchantselectid(String order_id);

    void updatestateexpress(String order_id,Integer state,String express);

    List<wuliu> wuliuselectuser_id();

    void wuliuupdatestate(String order_id);

    List<order> search(String name);

}
