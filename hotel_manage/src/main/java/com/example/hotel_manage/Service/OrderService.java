package com.example.hotel_manage.Service;

import com.example.hotel_manage.Pojo.Enum.OrderType;
import com.example.hotel_manage.Pojo.Order;
import com.example.hotel_manage.Pojo.PageBean;
import org.aspectj.weaver.ast.Or;

import java.time.LocalDate;
import java.util.List;

public interface OrderService {
    public List<Order> getAllOrders();

    public Order getOrderById(int id);
    public Order createOrder(Order order);

    public void updateOrder(Order order);
    public void deleteOrder(int id);

    public List<Order> getOrderByDate(LocalDate date);

    public Order getOrderByRoomIdAndDate(String roomId, LocalDate date);


    PageBean list(Integer page, Integer size, String roomId, String phoneNumber, OrderType orderType, LocalDate orderDate);

    List<String> findByPhone(String phone);

    Order findByRoomId(String roomId);
}
