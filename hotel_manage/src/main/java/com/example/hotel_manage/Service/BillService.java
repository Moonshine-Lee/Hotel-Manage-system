package com.example.hotel_manage.Service;

import com.example.hotel_manage.Pojo.Bill;
import com.example.hotel_manage.Pojo.Enum.BillType;
import com.example.hotel_manage.Pojo.PageBean;

import java.time.LocalDate;
import java.util.List;

public interface BillService {
    //现实业务中，对于支付的生成 可以通过读取传入数据/ api自动唤起 等方式 ，
    void createBill(Bill bill,boolean unpaid);

    void updateBill(Bill bill);

    PageBean list(Integer page, Integer size, BillType billType, String roomId, LocalDate paymentDate);
}
