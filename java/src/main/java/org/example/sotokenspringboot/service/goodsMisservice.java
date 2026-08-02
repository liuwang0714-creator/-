package org.example.sotokenspringboot.service;

import org.example.sotokenspringboot.pojo.PageBean;
import org.example.sotokenspringboot.pojo.goods;

import java.util.List;


public interface goodsMisservice {

    PageBean<goods> goodslist(Integer pageNum, Integer pageSize, Integer userId);

    void add(goods goods,Integer userId);

    goods selectid(Integer id);

    void update(goods goods);
    void delete(Integer id);

    void updatestock(Integer goods_id, Integer buyNum);

    List<goods> search(String name);

}
