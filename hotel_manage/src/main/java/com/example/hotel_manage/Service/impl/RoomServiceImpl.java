package com.example.hotel_manage.Service.impl;

import com.example.hotel_manage.Exception.RoomUnavailableException;
import com.example.hotel_manage.Mapper.*;
import com.example.hotel_manage.Pojo.*;
import com.example.hotel_manage.Pojo.Enum.BillType;
import com.example.hotel_manage.Pojo.Enum.RoomStatus;
import com.example.hotel_manage.Service.BillService;
import com.example.hotel_manage.Service.RoomService;
import com.example.hotel_manage.Utils.RoomStringUtils;
import com.example.hotel_manage.anno.authority_anno.ReceptionistAnno;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RoomServiceImpl implements RoomService {
    @Autowired
    RoomMapper roomMapper;
    @Autowired
    OrderAchieveMapper orderAchieveMapper;
    @Autowired
    RoomStringUtils roomStringUtils;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private BillService billService;
    @Autowired
    private CheckinMapper checkinMapper;

    @Override
    @ReceptionistAnno
    public RoomFront findById(String roomId) {
        RoomSql roomSql = roomMapper.findById(roomId);
        RoomFront roomFront = roomStringUtils.sqlToFront(roomSql);
        return roomFront;
    }

    //针对i小于90的情况，查找是否可用
    @ReceptionistAnno
    @Override
    public Boolean checkAvailability_in_i_days(String roomId, Integer i) {
        RoomSql roomSql = roomMapper.findById(roomId);
        RoomFront roomFront = roomStringUtils.sqlToFront(roomSql);
        for (int j = 0; j < i; j++) {
            if (!roomFront.getIsAvailableIn90Days()[j]) {
                return false;
            }
        }

        return true;
    }


    @ReceptionistAnno
    @Override
    public List<RoomFront> findAllRoom() {
        List<RoomSql> roomSqlList = roomMapper.findAllRooms();
        List<RoomFront> roomFrontList = new ArrayList<>();
        for (RoomSql roomSql : roomSqlList) {
            roomFrontList.add(roomStringUtils.sqlToFront(roomSql));
        }
        return roomFrontList;
    }

    @Override
    public void setRoomUnavailable(String roomId, LocalDate date, Integer nDays) {
        LocalDate now = LocalDate.now();
        long days = ChronoUnit.DAYS.between(date, now);
        if (days >= 90)
            return;
        RoomSql roomById = roomMapper.findRoomById(roomId);
        Boolean[] booleans = roomStringUtils.StrToBool(roomById.getIsAvailableIn90Days());
        for (int j = (int) days; j < days + nDays && j < 90; j++) {
            booleans[j] = false;
        }
        roomById.setIsAvailableIn90Days(roomStringUtils.boolToString(booleans));
        roomMapper.update(roomById);
    }

    @ReceptionistAnno
    @Override
    public List<RoomFront> findAvailableRoom(LocalDate date) {
        LocalDate now = LocalDate.now();
        long days = ChronoUnit.DAYS.between(now, date);
        //如果超过90天（串最大范围）则order表和room表联查
        if (days >= 90) {
            List<String> allRoomIds = roomMapper.findAllRoomIds();
            List<String> idsList = orderMapper.findOrderIdByDate(date);
            List<String> availableIds = new ArrayList<>();
            for (String roomId : allRoomIds) {
                if (!idsList.contains(roomId)) {
                    availableIds.add(roomId);
                }
            }
            List<RoomSql> roomSqlList = new ArrayList<>();
            for (String availableId : availableIds) {
                roomSqlList.add(roomMapper.findRoomById(availableId));
            }
            List<RoomFront> roomFrontList = new ArrayList<>();
            for (RoomSql roomSql : roomSqlList) {
                roomFrontList.add(roomStringUtils.sqlToFront(roomSql));
            }
            return roomFrontList;
        } else {
            List<RoomSql> allRooms = roomMapper.findAllRooms();
            List<RoomFront> roomFrontList = new ArrayList<>();
            for (RoomSql roomSql : allRooms) {
                if (roomSql.getIsAvailableIn90Days().charAt((int) days) == '1')
                    roomFrontList.add(roomStringUtils.sqlToFront(roomSql));
            }
            return roomFrontList;
        }
    }

    @ReceptionistAnno
    @Override
    public void setAvailableInNDays(String roomId, Integer nDays) {
        RoomSql byId = roomMapper.findById(roomId);
        Boolean[] booleans = roomStringUtils.StrToBool(byId.getIsAvailableIn90Days());
        for (int i = 0; i <= nDays; i++) {
            booleans[i] = true;
        }
        byId.setIsAvailableIn90Days(roomStringUtils.boolToString(booleans));
        roomMapper.update(byId);
    }

    @ReceptionistAnno
    @Override
    public List<RoomFront> findAvailableRoom(LocalDate date, Integer continue_time) {
        List<RoomFront> l1 = findAvailableRoom(date);
        for (int i = 1; i <= continue_time; i++) {
            List<RoomFront> l2 = findAvailableRoom(date.plusDays(1));
            l1.retainAll(l2);
        }
        return l1;
    }

    //checkIn 首先判断是否可用，然后设置未来n天均不可用
    //应设计并发机制（后续补充）
    //在checkin中应实现搬表（把实现了的order 搬到新的地方）

    //对于晚上12点以后开房的情况 则设置check_in 的date为昨天
    @ReceptionistAnno
    @Override
    @Transactional
    public Boolean checkin(Boolean withorder, String roomId, Integer time, String indentityCard, String phone, float payment_amount) {
        if (time >= 90) {
            throw new RoomUnavailableException("入住时间设置超过90天，拒绝操作");
        }
        RoomSql roomSql = roomMapper.findById(roomId);
        Boolean sign = false;

        if (!checkAvailability_in_i_days(roomId, time)) {
            if (!withorder) {
                throw new RoomUnavailableException("该房间已经被他人入住");
            }
            if (!(roomSql.getRoomStatus().equals(RoomStatus.available))) {
                if (roomSql.getRoomStatus().equals(RoomStatus.occupied))
                    throw new RoomUnavailableException("房间尚未退房，无法入住");
                if (roomSql.getRoomStatus().equals(RoomStatus.cleaning))
                    throw new RoomUnavailableException("房间打扫中，无法入住");
            }
            Order order = orderMapper.findOrderByRoomIdAndDate(roomId, LocalDate.now());
            Checkin identity_checkin = checkinMapper.findByCustomerIdentityCardAndDate(indentityCard, LocalDate.now());
            if (identity_checkin != null && !identity_checkin.getCheckInStatus().equals("checkout")) {
                throw new RoomUnavailableException("该身份证已经办理过入住，不可重复办理");
            }
            if (order.getOrderDays() < time) {
                throw new RoomUnavailableException("入住申请超过订单申请的时常，拒绝入住");
            }
            if (order == null) {
                throw new RoomUnavailableException("提供信息有误，查询不到预定订单");
            }
            if (!order.getPhoneNumber().equals(phone)) {
                throw new RoomUnavailableException("手机号与订单不符,请提供预定订单时的手机号");
            }
        }
        //12点后入住 算作昨天
        LocalDateTime now = LocalDateTime.now();
        LocalTime midnight = LocalTime.MIDNIGHT;
        LocalTime sixAM = LocalTime.of(6, 0);
        LocalTime currentTime = now.toLocalTime();
        int after12 = (currentTime.isAfter(midnight) && currentTime.isBefore(sixAM)) ? 1 : 0;


        Boolean[] booleans = roomStringUtils.StrToBool(roomSql.getIsAvailableIn90Days());
        for (int i = 0; i < time - after12; i++) {
            booleans[i] = false;
        }
        String str_afterUpdate = roomStringUtils.boolToString(booleans);
        roomMapper.checkIn(roomId, str_afterUpdate);

/*
        Checkin checkin = new Checkin(roomId, time, indentityCard, phone, payment_amount);
*/
        Checkin checkin = new Checkin();
        checkin.setRoomId(roomId);
        checkin.setDays(time);
        checkin.setCustomerIdentityCard(indentityCard);
        checkin.setCustomerPhone(phone);
        checkin.setPaymentAmount(payment_amount);
        checkin.setCheckInStatus("check_in");
        checkin.setCheckInTime(new Timestamp(System.currentTimeMillis()));
        checkinMapper.insert(checkin);
        if (!sign) {
            Bill bill = new Bill();
            bill.setRoomId(roomId);
            bill.setCheckinId(checkin.getCheckinId());
            bill.setAmount(checkin.getPaymentAmount());
            bill.setBillType(BillType.offlineCheckin);
            bill.setPaymentTime(new Timestamp(System.currentTimeMillis()));
            billService.createBill(bill, false);
        }

        return true;
    }

    //提前退房的话，则设置后几天的该room均为可用状态
    @ReceptionistAnno
    @Override
    public Boolean checkout(String roomId, Integer checkinId) {
        Checkin checkin = checkinMapper.findById(checkinId);
        checkinMapper.checkOut(checkin.getCheckinId(), LocalDateTime.now());

        /*Order orderByRoomIdAndDate = orderMapper.findOrderByRoomIdAndDate(roomId, checkin.getCheckInTime().toLocalDateTime().toLocalDate());
        if(orderByRoomIdAndDate!=null){
            orderAchieveMapper.insert(orderByRoomIdAndDate);
            orderMapper.deleteById(orderByRoomIdAndDate.getOrderId());}*/


        //设计是否提前退房的判断逻辑
        //12点后入住 算作昨天
        LocalTime midnight = LocalTime.MIDNIGHT;
        LocalTime sixAM = LocalTime.of(6, 0);
        LocalTime checkinTime = checkin.getCheckInTime().toLocalDateTime().toLocalTime();
        int after12 = (checkinTime.isAfter(midnight) && checkinTime.isBefore(sixAM)) ? 1 : 0;

        LocalDate.now();
        long between = ChronoUnit.DAYS.between(checkin.getCheckInTime().toLocalDateTime().toLocalDate(), LocalDate.now());
        Integer days = checkin.getDays() - (int) between + after12;
        RoomSql room = roomMapper.findRoomById(roomId);
        Boolean[] booleans = roomStringUtils.StrToBool(room.getIsAvailableIn90Days());
        for (int i = 0; i < days; i++) {
            booleans[i] = true;
        }
        String avai = roomStringUtils.boolToString(booleans);
        roomMapper.checkout(roomId, avai);

        return true;
    }

    @Override
    public void clean(String roomId) {
        RoomSql roomById = roomMapper.findRoomById(roomId);
        if (!(roomById.getRoomStatus().equals(RoomStatus.cleaning))) {
            throw new RoomUnavailableException("房间未退房，不可打扫");
        }

        roomMapper.clean(roomId);
    }

    @Override
    public List<String> findUnchekoutRoomIds() {
        LocalDateTime yesterdayStart = LocalDate.now().minusDays(1).atTime(LocalTime.MIDNIGHT);
        LocalDateTime yesterdayEnd = LocalDate.now().minusDays(1).atTime(LocalTime.of(6, 0));
        List<Checkin> byEndDateToday = checkinMapper.findByEndDate(LocalDate.now());
        //利用java Stream的特性高效处理代码

        //需要剔除昨天0-8点入住的订单 因为其应该算作昨天就被退房了
        List<Checkin> validCheckins = byEndDateToday.stream()
                .filter(checkin -> {
                    LocalDateTime checkInTime = checkin.getCheckInTime().toLocalDateTime();
                    return !checkInTime.isAfter(yesterdayStart) || checkInTime.isBefore(yesterdayEnd);
                })
                .collect(Collectors.toList());
        //对于晚上12点以后倒8点以前入住的订单 则需要查询明天的ByEndDate，
        List<Checkin> byEndDateTomorrow = checkinMapper.findByEndDate(LocalDate.now().plusDays(1));
        LocalDateTime todayStart = LocalDate.now().atTime(LocalTime.MIDNIGHT);
        LocalDateTime todayEnd = LocalDate.now().atTime(LocalTime.of(6, 0));
        List<Checkin> earlyMorningCheckins = byEndDateTomorrow.stream()
                .filter(checkin -> {
                    LocalDateTime checkInTime = checkin.getCheckInTime().toLocalDateTime();
                    return checkInTime.isAfter(todayStart) && checkInTime.isBefore(todayEnd);
                })
                .collect(Collectors.toList());
        validCheckins.addAll(earlyMorningCheckins);
        List<String> uncheckoutRoomIds = validCheckins.stream()
                .map(checkin -> checkin.getRoomId()).collect(Collectors.toList());
        return uncheckoutRoomIds;
    }

    @Override
    public void setPrice(String roomType, Float price) {
        roomMapper.setprice(roomType, price);
        return;
    }

    @Override
    public Float diffPrice(String roomType, Integer days) {
        Float price = roomMapper.getPrice(roomType);
        return price * days;
    }

    @Override
    public PageBean findRoom(Integer page, Integer pageSize, String roomId, String roomType, RoomStatus status) {
        PageHelper.startPage(page, pageSize);
        List<RoomSql> list = roomMapper.list(roomId, roomType, status);
        PageInfo<RoomSql> pageInfo = new PageInfo<>(list);
        PageBean pageBean = new PageBean(pageInfo.getTotal(), pageInfo.getList());
        return pageBean;

    }

    @Override
    public void addRoom(RoomStatus status, String roomId, String roomType, Float price) {

        List<RoomSql> allRooms = roomMapper.findAllRooms();
        for (RoomSql room : allRooms) {
            if (room.getRoomId().equals(roomId)) {
                throw new RoomUnavailableException("添加失败，该房号重复了");
            }
            if (room.getRoomType().equals(roomType)) {
                if (!room.getPrice().equals(price)) {
                    throw new RoomUnavailableException("添加失败，与该房型价格不一致");
                }
            }
        }

        RoomSql roomSql = new RoomSql();
        roomSql.setRoomId(roomId);
        roomSql.setRoomType(roomType);
        roomSql.setRoomStatus(status);
        roomSql.setPrice(price);
        roomSql.setIsAvailableIn90Days("11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111");
        roomMapper.insert(roomSql);
        return;
    }

    @Override
    public List<RoomType> getRoomType() {
        // 检查 roomMapper 是否为 null
        if (roomMapper == null) {
            throw new NullPointerException("roomMapper is null");
        }

        List<RoomSql> allRooms = roomMapper.findAllRooms();

        // 检查 allRooms 是否为 null
        if (allRooms == null) {
            throw new NullPointerException("allRooms is null");
        }

        // 使用哈希实现 O(n) 更新
        Map<String, RoomType> roomTypeMap = new HashMap<>();
        for (RoomSql roomSql : allRooms) {
            // 检查 roomSql 和 roomSql.getRoomType() 是否为 null
            if (roomSql == null || roomSql.getRoomType() == null) {
                throw new NullPointerException("roomSql or roomSql.getRoomType() is null");
            }

            RoomType roomType = roomTypeMap.get(roomSql.getRoomType());
            if (roomType == null) {
                RoomType roomType1 = new RoomType();
                roomType1.setRoomType(roomSql.getRoomType());
                roomType1.setPrice(roomSql.getPrice());
                roomType1.setCount(0);
                if (roomSql.getRoomStatus() == null) {
                    throw new NullPointerException("roomSql.getroomStatus() is null");
                }
                if (roomSql.getRoomStatus() != null && roomSql.getRoomStatus().equals(RoomStatus.available)) {

                    roomType1.setCount(1);
                }
                roomTypeMap.put(roomSql.getRoomType(), roomType1);
            } else {
                if (roomSql.getRoomStatus() == null) {
                    throw new NullPointerException("roomSql.getroomStatus() is null");
                }
                if (roomSql.getRoomStatus() != null && roomSql.getRoomStatus().equals(RoomStatus.available)) {
                    roomType.setCount(roomType.getCount() + 1);
                }
                roomTypeMap.replace(roomSql.getRoomType(), roomType);
            }
        }

        List<RoomType> roomTypeList = new ArrayList<>(roomTypeMap.values());
        return roomTypeList;
    }

    @Override
    public void deleteById(String roomId) {
        roomMapper.deleteById(roomId);
    }

    @Override
    public void update(RoomSql roomSql) {
        roomMapper.update(roomSql);
    }

    @Override
    public List<RoomFront> findAvailableRoomByType(LocalDate date, Integer continueTime, String roomType) {
        List<RoomFront> l1 = findAvailableRoomType(date, roomType);
        for (int i = 1; i <= continueTime; i++) {
            List<RoomFront> l2 = findAvailableRoomType(date.plusDays(1), roomType);
            l1.retainAll(l2);
        }
        return l1;
    }

    @Override
    public void updateRoomType(String oldRoomType, Float price, String roomType) {
        List<RoomSql> allRooms = roomMapper.findAllRooms();
        for (RoomSql roomSql : allRooms) {
            if (roomSql.getRoomType().equals(oldRoomType)) {
                roomSql.setPrice(price);
                roomSql.setRoomType(roomType);
                roomMapper.update(roomSql);
            }
        }
    }

    public List<RoomFront> findAvailableRoomType(LocalDate date, String roomType) {
        LocalDate now = LocalDate.now();
        long days = ChronoUnit.DAYS.between(now, date);
        //如果超过90天（串最大范围）则order表和room表联查
        if (days >= 90) {
            List<String> allRoomIds = roomMapper.findAllRoomIds();
            List<String> idsList = orderMapper.findOrderIdByDate(date);
            List<String> availableIds = new ArrayList<>();
            for (String roomId : allRoomIds) {
                if (!idsList.contains(roomId)) {
                    availableIds.add(roomId);
                }
            }
            List<RoomSql> roomSqlList = new ArrayList<>();
            for (String availableId : availableIds) {
                RoomSql roomById = roomMapper.findRoomById(availableId);
                if (roomById.getRoomType() == roomType) {
                    roomSqlList.add(roomById);
                }
            }
            List<RoomFront> roomFrontList = new ArrayList<>();
            for (RoomSql roomSql : roomSqlList) {
                roomFrontList.add(roomStringUtils.sqlToFront(roomSql));
            }
            return roomFrontList;
        } else {
            List<RoomSql> allRooms = roomMapper.findAllRooms();
            List<RoomFront> roomFrontList = new ArrayList<>();
            for (RoomSql roomSql : allRooms) {
                if (roomSql.getIsAvailableIn90Days().charAt((int) days) == '1')
                    if (roomSql.getRoomType().equals(roomType)
                    ) {
                        roomFrontList.add(roomStringUtils.sqlToFront(roomSql));
                    }
            }
            return roomFrontList;
        }
    }

    @Override
    public List<RoomFront> findRoomToBeCleaned() {
        List<RoomSql> allRooms = roomMapper.findAllRooms();
        List<RoomFront> roomFrontList = new ArrayList<>();
        for (RoomSql roomSql : allRooms) {
            if (roomSql.getRoomStatus().equals(RoomStatus.cleaning)) {
                roomFrontList.add(roomStringUtils.sqlToFront(roomSql));
            }
        }
        return roomFrontList;
    }
}
