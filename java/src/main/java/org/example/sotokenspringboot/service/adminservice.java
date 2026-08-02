package org.example.sotokenspringboot.service;

import org.example.sotokenspringboot.pojo.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface adminservice {
    void roleper(String roleId, String userId);

    PageBean<User> userlist(Integer pageNum, Integer pageSize);

    void updatepwd(Integer userid,String password);

    void banUser(Integer userId, long banEndTime);

    void updateState(Integer userId, Integer state);

    List<goods> goodslist();

    void role_user(Integer userId,Integer roleId);

    PageBean<order> orderlist(Integer pageNum, Integer pageSize);

    void updateorderstate(String orderid,Integer state,Integer fahuo);

    adminhx zongcount();

    List<dpfh> getordercount();

    List<dpfh> getamount();

    List<goodscount> getgoodstop10();

    List<User> search(String name);
}