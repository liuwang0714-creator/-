package org.example.sotokenspringboot.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.sotokenspringboot.pojo.order;
import org.example.sotokenspringboot.pojo.wuliu;

import java.util.List;

@Mapper
public interface ordermapper {

    @Insert("insert into " +
            "`order`(good_id,order_id,buyNum,addr_id,coupon,fahuo,user_id,merchant_id," +
            "beizhu,total_price,state," +
            "name,phone,addr_detail) " +
            " values(#{good_id},#{order_id},#{buyNum},#{addr.id}," +
            "#{coupon},#{fahuo},#{user_id},#{merchant_id}," +
            "#{beizhu},#{total_price},#{state}," +
            "#{addr.name},#{addr.phone},#{addr.addr})")
    void add(order order);



    @Select("SELECT\n" +
            "    o.*,\n" +
            "    g.goods_name,\n" +
            "    f1.file_url AS goods_file_url,\n" +
            "    u.nickname AS nickname,\n" +
            "    f2.file_url AS file_url\n" +
            "FROM\n" +
            "    `order` o\n" +
            "LEFT JOIN goods g ON o.good_id = g.id\n" +
            "LEFT JOIN `file` f1 ON g.goods_file_id = f1.id\n" +
            "LEFT JOIN user u ON g.create_id = u.id\n" +
            "LEFT JOIN `file` f2 ON u.user_file_id = f2.id\n" +
            "WHERE\n" +
            "    o.user_id = #{user_id} " +
            "ORDER BY\n" +
            "    o.create_time DESC")
    List<order> userselectuser_id(Integer user_id);

    @Select("SELECT\n" +
            "    o.*,\n" +
            "    g.goods_name,\n" +
            "    f1.file_url AS goods_file_url,\n" +
            "    u.nickname AS nickname,\n" +
            "    f2.file_url AS file_url\n" +
            "FROM\n" +
            "    `order` o\n" +
            "LEFT JOIN goods g ON o.good_id = g.id\n" +
            "LEFT JOIN `file` f1 ON g.goods_file_id = f1.id\n" +
            "LEFT JOIN user u ON o.user_id = u.id\n" +
            "LEFT JOIN `file` f2 ON u.user_file_id = f2.id\n" +
            "WHERE\n" +
            "    o.merchant_id = #{user_id} " +
            "ORDER BY\n" +
            "    o.create_time DESC")
    List<order> merchantselectuser_id(Integer user_id);


    @Select("SELECT\n" +
            "    o.*,\n" +
            "    g.goods_name,\n" +
            "    f1.file_url AS goods_file_url,\n" +
            "    u.nickname AS nickname,\n" +
            "    f2.file_url AS file_url\n" +
            "FROM\n" +
            "    `order` o\n" +
            "LEFT JOIN goods g ON o.good_id = g.id\n" +
            "LEFT JOIN `file` f1 ON g.goods_file_id = f1.id\n" +
            "LEFT JOIN user u ON g.create_id = u.id\n" +
            "LEFT JOIN `file` f2 ON u.user_file_id = f2.id\n" +
            "WHERE\n" +
            "    o.order_id = #{order_id} " )
    order userselectid(String order_id);

    @Select("SELECT\n" +
            "    o.*,\n" +
            "    g.goods_name,\n" +
            "    f1.file_url AS goods_file_url,\n" +
            "    u.nickname AS nickname,\n" +
            "    f2.file_url AS file_url\n" +
            "FROM\n" +
            "    `order` o\n" +
            "LEFT JOIN goods g ON o.good_id = g.id\n" +
            "LEFT JOIN `file` f1 ON g.goods_file_id = f1.id\n" +
            "LEFT JOIN user u ON o.user_id = u.id\n" +
            "LEFT JOIN `file` f2 ON u.user_file_id = f2.id\n" +
            "WHERE\n" +
            "    o.order_id = #{order_id} " )
    order merchantselectid(String order_id);

    @Update("update `order` set state=#{state} where order_id=#{order_id}")
    void updatestate(String order_id, Integer state);

    @Update("update `order` set state=#{state},express_no=#{express} where order_id=#{order_id}")
    void updatestateexpress(String order_id, Integer state, String express);


    @Select("select * from `order` where (state=2 or state=3) and fahuo=1")
    List<wuliu> wuliuselectuser_id();


    @Update("update `order` set state=3 where order_id=#{order_id}")
    void wuliuupdatestate(String order_id);

    @Select("SELECT\n" +
            "    o.*,\n" +
            "    g.goods_name,\n" +
            "    f1.file_url AS goods_file_url,\n" +
            "    u.nickname AS nickname,\n" +
            "    f2.file_url AS file_url\n" +
            "FROM\n" +
            "    `order` o\n" +
            "LEFT JOIN goods g ON o.good_id = g.id\n" +
            "LEFT JOIN `file` f1 ON g.goods_file_id = f1.id\n" +
            "LEFT JOIN user u ON o.user_id = u.id\n" +
            "LEFT JOIN `file` f2 ON u.user_file_id = f2.id\n" +
            "WHERE\n" +
            "        o.order_id LIKE CONCAT('%', #{name}, '%') \n" +
            "        OR o.beizhu LIKE CONCAT('%', #{name}, '%')\n")
    List<order> search(String name);




}
