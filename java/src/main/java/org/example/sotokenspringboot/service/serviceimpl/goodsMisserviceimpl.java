package org.example.sotokenspringboot.service.serviceimpl;

import org.example.sotokenspringboot.mapper.filemapper;
import org.example.sotokenspringboot.mapper.goodsMismapper;
import org.example.sotokenspringboot.pojo.Files;
import org.example.sotokenspringboot.pojo.PageBean;
import org.example.sotokenspringboot.pojo.goods;
import org.example.sotokenspringboot.service.goodsMisservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class goodsMisserviceimpl implements goodsMisservice {


    @Autowired
    private goodsMismapper goodsmapper;
    @Autowired
    private filemapper filemapper;

    @Override
    public PageBean<goods> goodslist(Integer pageNum, Integer pageSize, Integer userId) {
        int offset = (pageNum - 1) * pageSize;
        List<goods> goodslist = goodsmapper.goodslist(offset, pageSize, userId);
        Long total = goodsmapper.goodscount(userId);

        // 1. 全部改成 Integer！！！和数据库goods_file_id、files.id完全一致！
        Set<Integer> imgIds = goodslist.stream()
                .map(goods::getGoods_file_id) // 这里返回Integer，不是Long
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 2. final定义，解决lambda final报错，if/else同时初始化
        final Map<Integer, String> idToUrlMap;
        if (!imgIds.isEmpty()) {
            // Set转List，传给selectBatchIds
            List<Integer> idList = new ArrayList<>(imgIds);
            List<Files> images = filemapper.selectBatchIds(idList);

            idToUrlMap = images.stream()
                    .collect(Collectors.toMap(Files::getId, Files::getFile_url));
        } else {
            idToUrlMap = new HashMap<>();
        }

        // 3. 回填：同步改成Integer！解决Long不兼容Integer报错
        goodslist.forEach(goods -> {
            Integer imgId = goods.getGoods_file_id(); // 重点：Integer！不是Long！
            if (imgId != null) {
                goods.setFile_url(idToUrlMap.get(imgId));
            }
        });

        return new PageBean<>(total, goodslist);
    }


    @Override
    public void add(goods goods,Integer userId) {
        goodsmapper.add(goods, userId);
    }
    @Override
    public goods selectid(Integer id){
        goods goods = goodsmapper.selectid(id);
        if (goods != null && goods.getGoods_file_id() != null) {
            Files file = filemapper.selectId(goods.getGoods_file_id());
            if (file != null) {
                goods.setFile_url(file.getFile_url());
            }
        }
        return goods;
    }

    @Override
    public void update(goods goods){
        goodsmapper.update(goods);
    }

    @Override
    public void delete(Integer id){
        goodsmapper.delete(id);
    }


    @Override
    public void updatestock(Integer goods_id, Integer buyNum){
        goodsmapper.updatestock(goods_id, buyNum);
    }

    @Override
    public List<goods> search(String name){
        return goodsmapper.search(name);
    }

}
