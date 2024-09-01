package com.example.hotel_manage.Controller;

import com.example.hotel_manage.Pojo.Bill;
import com.example.hotel_manage.Pojo.Enum.BillType;
import com.example.hotel_manage.Pojo.PageBean;
import com.example.hotel_manage.Pojo.Result;
import com.example.hotel_manage.Service.BillService;
import com.example.hotel_manage.Utils.DateConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/bill")
public class BillController {
    @Autowired
    BillService billService;
    @GetMapping("/save")
    public Result createBill(@RequestParam String roomId,@RequestParam Float amount,
                             @RequestParam BillType billType,@RequestParam String paymentMethod,
                             @RequestParam Boolean check) {
        Bill bill=new Bill();
        bill.setBillType(billType);
        bill.setRoomId(roomId);
        bill.setAmount(amount);
        bill.setPaymentMethod(paymentMethod);
        billService.createBill(bill,check);
        return Result.success();
    }
    @PostMapping("/update")
    public Result updateBill(@RequestBody Bill bill) {
        billService.updateBill(bill);
        return Result.success();
    }

    @GetMapping("/list")
    public Result list(@RequestParam (defaultValue = "1")Integer page,
                       @RequestParam (defaultValue = "10")Integer size,
                       @RequestParam (required = false) BillType billType,
                       @RequestParam (required = false) String roomId,
                       @RequestParam (required = false) String paymentDate
                       )
    {
        LocalDate date = DateConverter.convertStringToLocalDate(paymentDate);
        PageBean list = billService.list(page, size, billType, roomId, date);
        return Result.success(list);

    }

    //订单成功支付后的回调函数
    @PostMapping("/recal")
    public Result billPaymentRecall(){
        return Result.success();
    }
    //小额订单可以不进行异步校验



}
