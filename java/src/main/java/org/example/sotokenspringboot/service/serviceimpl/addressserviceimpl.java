package org.example.sotokenspringboot.service.serviceimpl;


import org.example.sotokenspringboot.mapper.addressmapper;
import org.example.sotokenspringboot.pojo.addr;
import org.example.sotokenspringboot.service.addressservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class addressserviceimpl implements addressservice {
    @Autowired
    private addressmapper addressmapper;
    @Override
    public void add(addr addr) {
        addressmapper.add(addr);
    }
    @Override
    public List<addr> list(Integer user_id){
        return addressmapper.list(user_id);
    }

    @Override
    public void delete(Integer id) {
        addressmapper.delete(id);
    }
}
