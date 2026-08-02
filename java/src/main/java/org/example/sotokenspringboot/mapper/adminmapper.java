package org.example.sotokenspringboot.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.sotokenspringboot.pojo.*;

import java.util.List;

@Mapper
public interface adminmapper {
    @Select("select g.*, f.file_url from goods as g " +
            "INNER JOIN file as f ON g.goods_file_id = f.id")
    List<goods> goodslist();


    @Select("select count(*) from goods")
    Long goodscount();

    @Insert("insert into sys_user_role values(#{userId},#{roleId})")
    void roleper(String userId,String roleId);

    @Select("select id, username, password, nickname, phone, create_time as createTime," +
            " update_time as updateTime, user_file_id, birthday, state, ban_time,role_id " +
            "from user limit #{offset},#{pageSize}")
    List<User> userlist( Integer offset, Integer pageSize);


    @Select("select id, username, password, nickname, phone, create_time as createTime," +
            " update_time as updateTime, user_file_id, birthday, state, ban_time, role_id " +
            "from user " +
            "WHERE " +
            "      username LIKE CONCAT('%', #{name}, '%') " +
            "      OR nickname LIKE CONCAT('%', #{name}, '%') ")
    List<User> search(String name);


    @Select("select count(*) from user")
    Long usercount();


    @Select("update user set password=#{password} where id=#{userId}")
    void updatepwd(Integer userId,String password);

    @Select("update user set ban_time=#{banEndTime} where id=#{userId}")
    void banUser(Integer userId, long banEndTime);

    @Update("update user set state=#{state} where id=#{usertid}")
    void updateState(Integer state,Integer usertid);


    @Update("update sys_user_role set role_id=#{roleId} where user_id=#{userId}")
    void role_user(Integer userId,Integer roleId);

    @Select("select * from `order` limit #{offset},#{pageSize}")
    List<order> orderlist(Integer offset, Integer pageSize);

    @Select("select count(*) from `order`")
    Long ordercount();

    @Update("update `order` set state= #{state},fahuo=#{fahuo} where order_id= #{orderid}")
    void updateorderstate(String orderid, Integer state,Integer fahuo);

    @Select("SELECT\n" +
            "  -- 用户相关\n" +
            "  (SELECT COUNT(*) FROM `user`) AS total_user,\n" +
            "  (SELECT COUNT(*) FROM `user` WHERE DATE(create_time) = CURDATE()) AS today_new_user,\n" +
            "  (SELECT COUNT(*) \n" +
            "   FROM `user` u \n" +
            "   JOIN sys_user_role sur ON u.id = sur.user_id \n" +
            "   JOIN sys_role r ON sur.role_id = r.id \n" +
            "   WHERE r.role_code = 'merchant') AS total_merchant,\n" +
            "  -- 商品相关\n" +
            "  (SELECT COUNT(*) FROM goods) AS total_goods,\n" +
            "  -- 订单&金额\n" +
            "  (SELECT COUNT(*) FROM `order`) AS total_order,\n" +
            "  (SELECT SUM(total_price) FROM pay WHERE pay_status = 1) AS total_turnover,\n" +
            "  -- 待处理订单\n" +
            "  (SELECT COUNT(*) FROM `order` WHERE state = 1) AS wait_delivery_order,\n" +
            "  (SELECT COUNT(*) FROM `order` WHERE state = 0) AS wait_pay_order;")
    adminhx zongcount();

    @Select("SELECT\n" +
            "  DATE(create_time) AS time_date,\n" +
            "  COUNT(*) AS `count`\n" +
            "FROM `order`\n" +
            "WHERE create_time >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)\n" +
            "GROUP BY time_date\n" +
            "ORDER BY time_date ASC;")
    List<dpfh> getordercount();

    @Select("SELECT\n" +
            "  DATE(create_time) AS time_date,\n" +
            "  SUM(total_price) AS `count`\n" +
            "FROM pay\n" +
            "WHERE pay_status = 1 \n" +
            "  AND create_time >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)\n" +
            "GROUP BY time_date\n" +
            "ORDER BY time_date ASC;")
    List<dpfh> getamount();


    @Select("SELECT\n" +
            "  g.id,\n" +
            "  g.goods_name,\n" +
            "  COUNT(o.id) AS `count`\n" +
            "FROM goods g\n" +
            "LEFT JOIN `order` o ON g.id = o.good_id\n" +
            "GROUP BY g.id, g.goods_name\n" +
            "ORDER BY `count` DESC\n" +
            "LIMIT 10;")
    List<goodscount> getgoodstop10();
}
