package org.example.sotokenspringboot.service;


import org.example.sotokenspringboot.pojo.PageBean;
import org.example.sotokenspringboot.pojo.addr;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface addressservice {
    void add(addr addr);

    List<addr> list(Integer user_id);

    void delete(Integer id);


}
