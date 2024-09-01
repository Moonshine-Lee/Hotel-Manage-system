package com.example.hotel_manage.Controller;

import com.example.hotel_manage.Exception.RoomUnavailableException;
import com.example.hotel_manage.Pojo.*;
import com.example.hotel_manage.Pojo.Enum.RoomStatus;
import com.example.hotel_manage.Service.CheckinService;
import com.example.hotel_manage.Service.RoomService;
import com.example.hotel_manage.Utils.DateConverter;
import com.example.hotel_manage.anno.authority_anno.AdminAnno;
import com.example.hotel_manage.anno.authority_anno.ReceptionistAnno;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/room")
public class RoomController {
    @Autowired
    CheckinService checkinService;

    @Autowired
    private RoomService roomService;
    //返回room可用状态会返回一个长度为90的布尔型维数组
    @ReceptionistAnno
    @GetMapping("/{roomId}")
    public Result findById(@PathVariable String roomId) {
       return Result.success(roomService.findById(roomId));
    }
    @GetMapping("/update")
    public Result update(@RequestParam(required = true) String roomId,@RequestParam(required = true) Float price,
                         @RequestParam(required = false) RoomStatus roomStatus,@RequestParam(required = true) String roomType,
                         @RequestParam(required = false) String description
    ){
        RoomSql roomSql=new RoomSql();
        roomSql.setRoomId(roomId);
        roomSql.setRoomType(roomType);
        roomSql.setRoomStatus(roomStatus);
        roomSql.setDescription(description);
        roomSql.setPrice(price);
        roomService.update(roomSql);
        return Result.success();
    }
    @GetMapping("/diffMoney")
    public Result cauculateMoney(@RequestParam String roomType,@RequestParam Integer days){
        Float v = roomService.diffPrice(roomType, days);
        return Result.success(v);
    }

    @GetMapping("/checkin")
    public Result checkin(@RequestParam Boolean withOrder,@RequestParam String roomId,@RequestParam Integer days,
                          @RequestParam String customerIdentityCard,@RequestParam String customerPhone,
                          @RequestParam Float paymentAmount)
    {
        try {
            roomService.checkin(withOrder,roomId,days,customerIdentityCard,customerPhone,paymentAmount);
        }
        catch (RoomUnavailableException e){
            return Result.error(e.getMessage());
        }

        return Result.success();
    }
    @AdminAnno
    @GetMapping("/add")
    public Result addRoom(@RequestParam RoomStatus status,@RequestParam String roomId,@RequestParam String roomType,@RequestParam Float price){
        roomService.addRoom(status,roomId,roomType,price);
        return Result.success();
    }
    @GetMapping("/type")
    public Result getRoomType(){
        List<RoomType> roomType = roomService.getRoomType();
        return Result.success(roomType);
    }
    @GetMapping("/updateRoomType")
    public Result updateRoomType
            (@RequestParam String oldRoomType,@RequestParam Float price,
             @RequestParam String roomType){
        roomService.updateRoomType(oldRoomType,price,roomType);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable String id ){
        roomService.deleteById(id);
        return Result.success();
    }
    @GetMapping("/checkinWithOrder")
    public Result checkInWithOrder(@RequestParam String roomId,
                                   @RequestParam Integer days,
                                   @RequestParam String customerIdentityCard,
                                   @RequestParam String customerPhone,
                                   @RequestParam Float paymentAmount
                                   )
    {
        checkinService.checkinWithOrder(roomId,days,customerIdentityCard,customerPhone,paymentAmount);
        return Result.success();
    }

    @GetMapping("/clean")
    public Result getRoomToBeCleaned(){
        return Result.success(roomService.findRoomToBeCleaned());
    }

    @PostMapping("/clean")
    public Result cleanRoom(@RequestBody RoomSql room){
        roomService.clean(room.getRoomId());
        return Result.success();
    }
    @GetMapping("/checkout")
    public Result checkout(@RequestParam Integer checkinId, @RequestParam String roomId){
        roomService.checkout(roomId,checkinId);
        return Result.success();
    }
    @GetMapping("/findByDateAndTypeCheckIn")
    public Result findAvailableRoomCheckin(
                                    @RequestParam Integer continueTime,
                                    @RequestParam String roomType){
        if (continueTime>90){
            return Result.error("入住办理不可超过90天");
        }
        LocalDate date=LocalDate.now();
        List<RoomFront> availableRoomByType = roomService.findAvailableRoomByType(date, continueTime, roomType);
        for (RoomFront front: availableRoomByType){
            front.setIsAvailableIn90Days(null);  //减轻前端负担
        }
        return Result.success(availableRoomByType);
    }
    @GetMapping("/findByDateAndType")
    public Result findAvailableRoom(@RequestParam String date,
                                    @RequestParam Integer continueTime,
                                    @RequestParam String roomType){
        LocalDate date1 = DateConverter.convertStringToLocalDate(date);
        List<RoomFront> availableRoomByType = roomService.findAvailableRoomByType(date1, continueTime, roomType);
        for (RoomFront front: availableRoomByType){
            front.setIsAvailableIn90Days(null);  //减轻前端负担
        }
        return Result.success(availableRoomByType);
    }
    @GetMapping("/list")
    public Result findRoom(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "5") Integer size,
                           String roomId,String roomType,RoomStatus status
                           ){
        PageBean pageBean=roomService.findRoom(page,size,roomId,roomType,status);
        return Result.success(pageBean);
    }
}

