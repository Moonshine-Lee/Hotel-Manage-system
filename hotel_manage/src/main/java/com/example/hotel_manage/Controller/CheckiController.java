package com.example.hotel_manage.Controller;

import com.example.hotel_manage.Pojo.PageBean;
import com.example.hotel_manage.Pojo.Result;
import com.example.hotel_manage.Pojo.RoomType;
import com.example.hotel_manage.Service.CheckinService;
import com.example.hotel_manage.Utils.DateConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.Retention;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/checkin")
public class CheckiController {
    @Autowired
    CheckinService checkinService;
    @GetMapping("/list")
    public Result findCheckin(@RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "10") int size,
                              @RequestParam(required = false) String customerPhone,
                              @RequestParam(required = false) String roomId,
                              @RequestParam(required = false) String checkinDate) {
        LocalDate date = DateConverter.convertStringToLocalDate(checkinDate);

        PageBean list = checkinService.list(page, size, customerPhone, roomId, date);
        return Result.success(list);
    }
    @GetMapping("/statis")
    public Result StaticType(){
        return null;
    }
}
