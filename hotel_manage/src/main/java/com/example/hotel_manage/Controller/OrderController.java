package com.example.hotel_manage.Controller;

import com.example.hotel_manage.Pojo.Enum.OrderType;
import com.example.hotel_manage.Pojo.Order;
import com.example.hotel_manage.Pojo.PageBean;
import com.example.hotel_manage.Pojo.Result;
import com.example.hotel_manage.Service.OrderService;
import com.example.hotel_manage.Utils.DateConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    OrderService orderService;

    @PostMapping("/save")
    public Result insertOrder(@RequestBody Order order) {
        Order order1 = orderService.createOrder(order);
        if(order1 != null) {
            return Result.success(order1);
        }
        else return Result.error("创建失败,房型或日期不可用");
    }
    @GetMapping("/list")
    public Result list(@RequestParam (defaultValue = "1")Integer page,
                       @RequestParam (defaultValue = "10")Integer size,
                       @RequestParam (required = false) String roomId,
                       @RequestParam (required = false) String phoneNumber,
                       @RequestParam (required = false) OrderType orderType,
                       @RequestParam (required = false) String orderDate
                       )
    {
        LocalDate date = DateConverter.convertStringToLocalDate(orderDate);
        PageBean list = orderService.list(page, size, roomId, phoneNumber, orderType, date);
        return Result.success(list);
    }
    @GetMapping("/findRoomsByPhone")
    public Result findByPhone(@RequestParam String phone){
        List<String> byPhone = orderService.findByPhone(phone);
        return Result.success(byPhone);
    }
    @GetMapping("/findOrderByRoomId")
    public Result findByRoomIdAndDate(@RequestParam String roomId){
        Order byRoomId = orderService.findByRoomId(roomId);
        return Result.success(byRoomId);
    }

    @GetMapping("/delete/{id}")
    public Result deleteOrder(@PathVariable Integer id) {
        orderService.deleteOrder(id);
        return Result.success();
    }
    @PostMapping("/update")
    public Result updateOrder(@RequestBody Order order) {
        orderService.updateOrder(order);
        return Result.success();
    }

    @PostMapping("/byDate")
    public Result getOrderByDate(@RequestBody LocalDate localDate) {
        List<Order> orderByDate = orderService.getOrderByDate(localDate);
        return Result.success(orderByDate);
    }


}
