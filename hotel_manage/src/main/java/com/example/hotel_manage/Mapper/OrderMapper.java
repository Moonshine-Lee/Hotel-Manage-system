package com.example.hotel_manage.Mapper;

import com.example.hotel_manage.Pojo.Enum.OrderType;
import com.example.hotel_manage.Pojo.Order;
import org.apache.ibatis.annotations.*;
import org.aspectj.weaver.ast.Or;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface OrderMapper {

    @Insert("INSERT INTO `order` (phone_number ,order_type, money, room_id, payment_id, order_date, order_days) " +
            "VALUES ( #{order.phoneNumber} ,#{order.orderType}, #{order.money}, #{order.roomId}, #{order.paymentReference}, #{order.orderDate}, #{order.orderDays})")
    @Options(useGeneratedKeys = true, keyProperty = "order.orderId")
    void insert(@Param("order") Order order);

    @Select("SELECT * FROM `order` WHERE order_id = #{id}")
    Order findById(@Param("id") Integer id);


    void update(@Param("order") Order order);

    @Delete("DELETE FROM `order` WHERE order_id = #{id}")
    int deleteById(@Param("id") Integer id);
    //查询某日期已经被预定的房间号
    @Select("SELECT room_id FROM `order` WHERE #{orderDate} BETWEEN order_date AND DATE_ADD(order_date,  INTERVAL order_days - 1 DAY)")
    List<String> findOrderIdByDate(@Param("orderDate") LocalDate orderDate);

    @Select("SELECT * FROM `order` WHERE #{orderDate} BETWEEN order_date AND DATE_ADD(order_date,  INTERVAL order_days - 1 DAY)")
    List<Order> findOrderByDate(@Param("orderDate") LocalDate orderDate);


    @Select("select * from `order` where room_id=#{roomId} and #{orderDate} BETWEEN order_date AND DATE_ADD(order_date,  INTERVAL order_days - 1 DAY) " )
    Order findOrderByRoomIdAndDate(@Param("roomId") String roomId,@Param("orderDate") LocalDate orderDate);

    List<Order> list(@Param("roomId") String roomId,@Param("phoneNumber") String phoneNumber,@Param("orderType") OrderType orderType,@Param("orderDate") LocalDate orderDate);
    @Select("SELECT * FROM `order` WHERE phone_number = #{phone} AND CURDATE() BETWEEN order_date AND DATE_ADD(order_date, INTERVAL order_days DAY)")
    List<Order> findByPhoneAndDate(@Param("phone") String phone);
    @Select("SELECT * FROM `order` WHERE room_id = #{roomId} AND CURDATE() BETWEEN order_date " +
            "AND DATE_ADD(order_date, INTERVAL order_days DAY)")
    Order findByRoomId(@Param("roomId") String roomId);
}
