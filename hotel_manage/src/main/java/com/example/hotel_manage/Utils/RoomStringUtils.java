package com.example.hotel_manage.Utils;

import com.example.hotel_manage.Pojo.RoomFront;
import com.example.hotel_manage.Pojo.RoomSql;

import java.util.List;

//用于转换代表房间可用状态的字符串与布尔型数组

public class RoomStringUtils {
    public Boolean[] StrToBool (String str) {
        Boolean[] bools = new Boolean[90];
        for (int i = 0; i < 90; i++) {
            if (str.charAt(i) == '1') {
                bools[i] = true;
            }
            else if (str.charAt(i) == '0') {
                bools[i] = false;
            }
        }
        return bools;
    }
    public String boolToString(Boolean[] bools) {
        String str = "";
        for (int i = 0; i < bools.length; i++) {
            str += bools[i] ? "1" : "0";
        }
        return str;
    }
    public RoomFront sqlToFront(RoomSql roomSql){
        RoomFront roomFront = new RoomFront();
        roomFront.setRoomId(roomSql.getRoomId());
        roomFront.setDescription(roomSql.getDescription());
        roomFront.setRoomType(roomSql.getRoomType());
        roomFront.setRoomStatus(roomSql.getRoomStatus());
        roomFront.setPrice(roomSql.getPrice());
        roomFront.setIsAvailableIn90Days(this.StrToBool(roomSql.getIsAvailableIn90Days()));
        return roomFront;
    }
    public RoomSql frontToSql(RoomFront roomFront){
        RoomSql roomSql = new RoomSql();
        roomSql.setRoomId(roomFront.getRoomId());
        roomSql.setDescription(roomFront.getDescription());
        roomSql.setRoomType(roomFront.getRoomType());
        roomSql.setPrice(roomFront.getPrice());
        roomSql.setRoomStatus(roomFront.getRoomStatus());
        roomSql.setIsAvailableIn90Days(this.boolToString(roomFront.getIsAvailableIn90Days()));
        return roomSql;
    }
}
