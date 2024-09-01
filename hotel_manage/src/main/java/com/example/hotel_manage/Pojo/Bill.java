package com.example.hotel_manage.Pojo;

import com.example.hotel_manage.Pojo.Enum.BillType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bill {
    private Integer billId;
    private BillType billType;
    private Integer orderId;
    private String roomId;
    private Integer checkinId;
    private Boolean success;
    private Boolean refund;
    private Float amount;
    private String paymentMethod;
    private Timestamp paymentTime;
    private String payingReference;
}

