package com.example.hotel_manage.Mapper;

import com.example.hotel_manage.Pojo.Order;
import org.apache.ibatis.annotations.*;
@Mapper
public interface OrderAchieveMapper {
    @Insert("INSERT INTO `order_archive` (type, money,  room_id, payment_reference, order_date, order_days) " +
            "VALUES (#{order.type}, #{order.money},  #{order.roomId}, #{order.paymentReference}, #{order.orderDate}, #{order.orderDays})")
    int insert(@Param("order") Order order);

    @Select("SELECT * FROM `order_archive` WHERE order_id = #{id}")
    Order findById(@Param("id") Integer id);

    @Update("UPDATE `order_archive` SET type=#{order.type}, money=#{order.money},  " +
            "room_id=#{order.roomId}, payment_reference=#{order.paymentReference}, order_date=#{order.orderDate}, order_days=#{order.orderDays} " +
            "WHERE order_id = #{order.orderId}")
    int update(@Param("order") Order order);

    @Delete("DELETE FROM `order_archive` WHERE order_id = #{id}")
    int deleteById(@Param("id") Integer id);
}
