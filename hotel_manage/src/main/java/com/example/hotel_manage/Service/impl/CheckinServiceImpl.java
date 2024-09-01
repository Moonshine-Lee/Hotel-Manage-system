package com.example.hotel_manage.Service.impl;

import com.example.hotel_manage.Mapper.CheckinMapper;
import com.example.hotel_manage.Pojo.Checkin;
import com.example.hotel_manage.Pojo.PageBean;
import com.example.hotel_manage.Pojo.RoomFront;
import com.example.hotel_manage.Pojo.RoomType;
import com.example.hotel_manage.Service.CheckinService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Service
public class CheckinServiceImpl implements CheckinService {
    @Autowired
    CheckinMapper checkinMapper;
    @Override
    public PageBean list(int page, int size, String customerPhone,
                         String roomId, LocalDate checkinDate)
    {
        PageHelper.startPage(page, size);
        List<Checkin> list = checkinMapper.list(customerPhone, roomId, checkinDate);
        PageInfo<Checkin> pageInfo=new PageInfo<>(list);
        PageBean pageBean=new PageBean(pageInfo.getTotal(),pageInfo.getList());
        return pageBean;

    }

    @Override
    public void checkinWithOrder(String roomId, Integer days, String customerIdentityCard, String customerPhone, Float paymentAmount) {
        Checkin checkin=new Checkin();
        checkin.setCheckInTime(new  Timestamp(System.currentTimeMillis()));
        checkin.setRoomId(roomId);
        checkin.setCheckInStatus("check_in");
        checkin.setDays(days);
        checkin.setCustomerIdentityCard(customerIdentityCard);
        checkin.setPaymentAmount(paymentAmount);
        checkin.setCustomerPhone(customerPhone);
        checkinMapper.insert(checkin);
    }

    @Override
    public List<RoomType> statistictype() {
        return List.of();
    }
}
