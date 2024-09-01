package com.example.hotel_manage.Pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomType {
    private String roomType;
    private Float price;
    private Integer count;  //房间剩余数量
}
