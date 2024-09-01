package com.example.hotel_manage.Pojo;

import com.example.hotel_manage.Pojo.Enum.RoomStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomFront {
    private RoomStatus roomStatus;
    private String roomId;
    private Float price;
    private String roomType;
    private Boolean[] isAvailableIn90Days;
    private String description;
}
