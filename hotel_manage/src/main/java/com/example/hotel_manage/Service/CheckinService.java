package com.example.hotel_manage.Service;

import com.example.hotel_manage.Pojo.PageBean;
import com.example.hotel_manage.Pojo.RoomType;

import java.time.LocalDate;
import java.util.List;

public interface CheckinService {
    public PageBean list(int page, int size, String customerPhone,
                         String roomId, LocalDate checkinDate);

    void checkinWithOrder(String roomId, Integer days, String customerIdentityCard, String customerPhone, Float paymentAmount);

    List<RoomType> statistictype();
}
