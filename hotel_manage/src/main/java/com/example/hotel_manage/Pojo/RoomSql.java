package com.example.hotel_manage.Pojo;

import com.example.hotel_manage.Pojo.Enum.RoomStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomSql {
    private RoomStatus roomStatus;
    private String roomId;
    private Float price;
    private String roomType;
    private String isAvailableIn90Days;
    private String description;
}

