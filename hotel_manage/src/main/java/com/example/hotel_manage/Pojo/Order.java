package com.example.hotel_manage.Pojo;

import com.example.hotel_manage.Pojo.Enum.OrderType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.sql.Timestamp;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private Integer orderId;
    private String roomType;
    private Boolean successPaid;
    private OrderType orderType;
    private String phoneNumber;
    private Float money;
    private Timestamp createTime;
    private Timestamp updateTime;
    private String roomId;
    private String paymentReference;
    private LocalDate orderDate;
    private Integer orderDays;
}

