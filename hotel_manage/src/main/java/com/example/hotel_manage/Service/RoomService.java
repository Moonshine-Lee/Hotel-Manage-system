package com.example.hotel_manage.Service;

import com.example.hotel_manage.Pojo.Enum.RoomStatus;
import com.example.hotel_manage.Pojo.PageBean;
import com.example.hotel_manage.Pojo.RoomFront;
import com.example.hotel_manage.Pojo.RoomSql;
import com.example.hotel_manage.Pojo.RoomType;
import com.example.hotel_manage.anno.authority_anno.ReceptionistAnno;

import java.time.LocalDate;
import java.util.List;

public interface RoomService {
    void setAvailableInNDays(String roomId, Integer nDays);

    public Boolean checkAvailability_in_i_days(String roomId, Integer i);
    //查找某房间的状态
    RoomFront findById(String roomId);
    //列出所有房间
    List<RoomFront> findAllRoom();
    //查找某一日期的可用房间

    void setRoomUnavailable(String roomId, LocalDate date, Integer nDays);

    List<RoomFront> findAvailableRoom(LocalDate date);
    //查找某一日期，可连续入住xx天的房间
    List<RoomFront> findAvailableRoom(LocalDate date, Integer continue_time);
    //根据房间号，以及入住时间，办理入住(仅支持当天)  如果房间未预定，则直接办理， 已预定，则核对资质（订单号）
    //checkIn 首先判断是否可用，然后设置未来n天均不可用
    //应设计并发机制（后续补充）
    //checkin 首先判断是否可用，然后设置未来n天均不可用
    //应设计并发机制（后续补充）
    //在checkin中应实现搬表（把实现了的order 搬到新的地方）
    //checkIn 首先判断是否可用，然后设置未来n天均不可用
    //应设计并发机制（后续补充）
    //在checkin中应实现搬表（把实现了的order 搬到新的地方）
    @ReceptionistAnno
    Boolean checkin(Boolean withorder, String roomId, Integer time, String indentityCard, String phone, float payment_amount);

    public List<RoomFront> findRoomToBeCleaned();

    //提前退房的话，则设置后几天的该room均为可用状态
    @ReceptionistAnno
    Boolean checkout(String roomId, Integer checkinId);

    public void clean(String roomId);

    List<String> findUnchekoutRoomIds( );

    void setPrice(String roomType,Float money);

    Float diffPrice(String roomType,Integer days);

    PageBean findRoom(Integer page, Integer pageSize, String roomId, String roomType, RoomStatus status);


    void addRoom(RoomStatus status, String roomId, String roomType,Float price);

    List<RoomType> getRoomType();

    void deleteById(String roomId);

    void update(RoomSql roomSql);

    List<RoomFront> findAvailableRoomByType(LocalDate date, Integer continueTime, String roomType);

    void updateRoomType(String oldRoomType, Float price, String roomType);
}
