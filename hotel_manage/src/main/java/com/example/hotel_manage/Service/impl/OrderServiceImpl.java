package com.example.hotel_manage.Service.impl;

import com.example.hotel_manage.Exception.RoomUnavailableException;
import com.example.hotel_manage.Mapper.BillMapper;
import com.example.hotel_manage.Mapper.OrderMapper;
import com.example.hotel_manage.Mapper.RoomMapper;
import com.example.hotel_manage.Pojo.*;
import com.example.hotel_manage.Pojo.Enum.BillType;
import com.example.hotel_manage.Pojo.Enum.OrderType;
import com.example.hotel_manage.Service.BillService;
import com.example.hotel_manage.Service.OrderService;
import com.example.hotel_manage.Service.RoomService;
import com.example.hotel_manage.Utils.RoomStringUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    RoomService roomService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private BillMapper billMapper;
    @Autowired
    private BillService billService;
    @Autowired
    private RoomMapper roomMapper;
    @Autowired
    private RoomStringUtils roomStringUtils;


    @Override
    public List<Order> getAllOrders() {
        return List.of();
    }

    @Override
    public Order getOrderById(int id) {
        return orderMapper.findById(id);
    }

    //create order不需要传入房间号，房间号将被分配
    @Override
    @Transactional
    public Order createOrder(Order order) {
        if(ChronoUnit.DAYS.between(LocalDate.now(),order.getOrderDate())<0){
            throw new RoomUnavailableException("不可预订过去的房间");
        }

        //防止插入脏数据
        int sign = 0;
        RoomFront room = null;
        for (RoomFront roomFront : roomService.findAvailableRoom(order.getOrderDate(), order.getOrderDays())) {
            if (roomFront.getRoomId().equals(order.getRoomId())) {
                room = roomFront;
                sign = 1;
            }
        }
        if (sign == 0) {
            throw new RoomUnavailableException("创建订单失败，房间不可用");
        }
        Boolean[] isAvailableIn90Days = room.getIsAvailableIn90Days();
        orderMapper.insert(order);
        Bill bill = new Bill();
        bill.setOrderId(order.getOrderId());
        bill.setBillType(BillType.onlineOrder);
        bill.setAmount(order.getMoney());
        bill.setRoomId(order.getRoomId());
        bill.setPaymentTime(new Timestamp(System.currentTimeMillis()));
        long days = ChronoUnit.DAYS.between( LocalDate.now(),order.getOrderDate());
        int i=order.getOrderDays();
        for(int j=(int) days;j<days+i&&j<90;j++){
            isAvailableIn90Days[j] =false;
        }
        room.setIsAvailableIn90Days(isAvailableIn90Days);
        RoomStringUtils roomStringUtils=new RoomStringUtils();
        RoomSql roomSql = roomStringUtils.frontToSql(room);
        roomMapper.updateWithAvailable(roomSql);
        billService.createBill(bill, false);

        return order;


    }

    @Override
    public void updateOrder(Order order) {
        orderMapper.update(order);
        return;

    }

    @Override
    public void deleteOrder(int id) {
        Order byId = orderMapper.findById(id);
        LocalDate orderDate = byId.getOrderDate();

        long between = ChronoUnit.DAYS.between(LocalDate.now(), orderDate);
        int days= (int) between;
        RoomSql roomById = roomMapper.findRoomById(byId.getRoomId());
        RoomFront roomFront = roomStringUtils.sqlToFront(roomById);
        Boolean[] isAvailableIn90Days = roomFront.getIsAvailableIn90Days();
        for(int i=days;i<days+byId.getOrderDays()&&i<90;i++){
            isAvailableIn90Days[i] =true;
        }
        roomFront.setIsAvailableIn90Days(isAvailableIn90Days);
        RoomSql roomSql = roomStringUtils.frontToSql(roomFront);
        roomMapper.updateWithAvailable(roomSql);
        orderMapper.deleteById(id);
        return;
    }

    //此处用作前台显示当日可办理入住的order
    @Override
    public List<Order> getOrderByDate(LocalDate date) {
        return orderMapper.findOrderByDate(date);
    }


    @Override
    public Order getOrderByRoomIdAndDate(String roomId, LocalDate date) {
        return orderMapper.findOrderByRoomIdAndDate(roomId, date);
    }

    @Override
    public PageBean list(Integer page, Integer size, String roomId, String phoneNumber, OrderType orderType, LocalDate orderDate) {
        PageHelper.startPage(page, size);
        List<Order> list = orderMapper.list(roomId, phoneNumber, orderType, orderDate);
        PageInfo<Order> pageInfo = new PageInfo<>(list);
        PageBean pageBean = new PageBean(pageInfo.getTotal(), pageInfo.getList());
        return pageBean;
    }

    @Override
    public List<String> findByPhone(String phone) {
        List<String> roomList=new ArrayList<>();
        List<Order> byPhoneAndDate = orderMapper.findByPhoneAndDate(phone);
        for(Order order:byPhoneAndDate){
            roomList.add(order.getRoomId());
        }
        return roomList;
    }

    @Override
    public Order findByRoomId(String roomId) {
        Order byRoomId = orderMapper.findByRoomId(roomId);
        LocalDateTime now = LocalDateTime.now();
        LocalTime midnight = LocalTime.MIDNIGHT;
        LocalTime sixAM = LocalTime.of(6, 0);
        LocalTime currentTime = now.toLocalTime();
        int after12 = (currentTime.isAfter(midnight) && currentTime.isBefore(sixAM)) ? 1 : 0;
        long between = ChronoUnit.DAYS.between(byRoomId.getOrderDate(), LocalDate.now());
        Integer days= byRoomId.getOrderDays()-(int) between+after12;
        byRoomId.setOrderDays(days);
        return byRoomId;
    }
}
