package org.example.sotokenspringboot.service.serviceimpl;

import org.example.sotokenspringboot.mapper.adminmapper;
import org.example.sotokenspringboot.mapper.filemapper;
import org.example.sotokenspringboot.pojo.*;
import org.example.sotokenspringboot.service.adminservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class adminserviceimpl implements adminservice {

    @Autowired
    private adminmapper adminmapper;

    @Autowired
    private filemapper filemapper;

    @Override
    public void roleper(String userId, String roleId) {
        adminmapper.roleper(userId, roleId);
    }
    @Override
    public List<goods> goodslist() {
        return adminmapper.goodslist();
    }
    @Override
    public PageBean<User> userlist(Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<User> userlist = adminmapper.userlist(offset, pageSize);
        Long total = adminmapper.usercount();

        PageBean<User> pb = new PageBean<>();
        pb.setTotal(total);
        pb.setItems(userlist);
        return pb;
    }

    @Override
    public void updatepwd(Integer userId,String password){
        adminmapper.updatepwd(userId,password);
    }

    @Override
    public void banUser(Integer userId, long banEndTime) {
        adminmapper.banUser(userId, banEndTime);
    }
    @Override
    public void updateState(Integer userId, Integer state) {
        adminmapper.updateState(userId, state);
    }

    @Override
    public void role_user(Integer userId,Integer roleId) {
        adminmapper.role_user(userId, roleId);
    }

    @Override
    public PageBean<order> orderlist(Integer pageNum, Integer pageSize) {
        Integer offset = (pageNum - 1) * pageSize;
        List<order> orderlist = adminmapper.orderlist(offset, pageSize);
        Long total = adminmapper.ordercount();
        PageBean<order> pb = new PageBean<>();
        pb.setTotal(total);
        pb.setItems(orderlist);
        return pb;
    }


    @Override
    public void updateorderstate(String orderid,Integer state,Integer fahuo){
        adminmapper.updateorderstate(orderid,state,fahuo);
    }

    @Override
    public adminhx zongcount(){
        return adminmapper.zongcount();
    }

    @Override
    public List<dpfh> getordercount(){
        return adminmapper.getordercount();
    }

    @Override
    public List<dpfh> getamount(){
        return adminmapper.getamount();
    }

    @Override
    public List<goodscount> getgoodstop10(){
        return adminmapper.getgoodstop10();
    }


    @Override
    public List<User> search(String name){
        System.out.println(name);
        return adminmapper.search(name);
    }
}
