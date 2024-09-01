package com.example.hotel_manage.Service.impl;

import com.example.hotel_manage.Exception.RoomUnavailableException;
import com.example.hotel_manage.Mapper.BillMapper;
import com.example.hotel_manage.Mapper.OrderMapper;
import com.example.hotel_manage.Mapper.RoomMapper;
import com.example.hotel_manage.Pojo.Bill;
import com.example.hotel_manage.Pojo.Checkin;
import com.example.hotel_manage.Pojo.Enum.BillType;
import com.example.hotel_manage.Pojo.Order;
import com.example.hotel_manage.Pojo.PageBean;
import com.example.hotel_manage.Service.BillService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BillServiceImpl implements BillService {
    @Autowired
    BillMapper billMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private RoomMapper roomMapper;

    //现实业务中，对于支付的生成 可以通过读取传入数据/ api自动唤起 等方式 ，

    //启动异步线程监听bill订单支付状态 check表示是否启动异步任务
   /* @Override
    @Transactional //添加事务管理注解 保证操作完整性
    public void createBill(Bill bill, boolean check) {
        if (roomMapper.findRoomById(bill.getRoomId()) == null) {
            throw new RoomUnavailableException("房间号错误，请重新输入");
        }
        billMapper.insert(bill);
        if (check) {
            monitorPaymentStatusAsync(bill);
        } else {
            bill.setSuccess(true);
            bill.setPaymentTime(Timestamp.valueOf(LocalDateTime.now()));
            billMapper.update(bill);
        }
        return;

    }*/
    //原异步校验接口已取消
    @Override
    @Transactional //添加事务管理注解 保证操作完整性
    public void createBill(Bill bill, boolean unpaid) {
        if (roomMapper.findRoomById(bill.getRoomId()) == null) {
            throw new RoomUnavailableException("房间号错误，请重新输入");
        }
        bill.setSuccess(!unpaid);
        billMapper.insert(bill);
    }


    @Override
    public void updateBill(Bill bill) {
        billMapper.update(bill);
        return;
    }

    @Override
    public PageBean list(Integer page, Integer size, BillType billType, String roomId, LocalDate paymentDate) {
        PageHelper.startPage(page, size);
        List<Bill> list = billMapper.list(billType, roomId, paymentDate);
        PageInfo<Bill> pageInfo=new PageInfo<>(list);
        PageBean pageBean=new PageBean(pageInfo.getTotal(),pageInfo.getList());
        return pageBean;
    }

    //异步任务查询支付状态
    //(1)轮询方式 （2）监听回调方式
    @Async("taskExecutor")
    public void monitorPaymentStatusAsync(Bill bill) {
        while (true) {
            boolean isPaid = checkPaymentStatus(bill);
            if (isPaid) {
                // 更新账单状态
                bill.setSuccess(true);
                billMapper.update(bill);
                Order byId = orderMapper.findById(bill.getOrderId());
                byId.setSuccessPaid(true);
                orderMapper.update(byId);
                break;
            }
            // 睡眠一段时间再检查
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private boolean checkPaymentStatus(Bill bill) {

        // 实现实际的支付状态检查逻辑
        // 返回true表示已支付，false表示未支付
        return false;
    }
}
