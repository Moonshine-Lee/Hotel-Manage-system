package com.example.hotel_manage.Pojo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.sql.Timestamp;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Checkin {
    private int days;
    private Integer checkinId;
    private String roomId;
    private String customerIdentityCard;
    private Timestamp checkInTime;
    private String checkInStatus;
    private Timestamp checkOutTime;
    private Float paymentAmount;
    private String customerPhone;



    public Checkin(String roomId, Integer days, String customerIdentityCard, String customerPhone, float paymentAmount){
        this.roomId = roomId;
        this.days=days;
        this.customerIdentityCard = customerIdentityCard;
        this.customerPhone = customerPhone;
        this.paymentAmount = paymentAmount;
        this.checkInStatus="check_in";
        this.checkInTime = new Timestamp(System.currentTimeMillis());
    }
}

