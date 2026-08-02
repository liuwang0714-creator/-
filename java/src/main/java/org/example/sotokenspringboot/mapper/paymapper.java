package org.example.sotokenspringboot.mapper;


import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.sotokenspringboot.pojo.pay;

@Mapper
public interface paymapper {


    @Insert("insert into " +
            "pay(order_id,pay_type,pay_status,total_price) " +
            "values(#{order_id},#{pay_type},#{pay_state},#{pay_price})")
    void wx(String order_id,String pay_type,String pay_state,Double pay_price );

    @Insert("insert into " +
            "pay(order_id,pay_type,pay_status,total_price) " +
            "values(#{order_id},#{pay_type},#{pay_state},#{pay_price})")
    void zfb(String order_id,String pay_type,String pay_state,Double pay_price );

    @Select("select * from pay where order_id=#{order_id}")
    pay selectid(String order_id);
}
