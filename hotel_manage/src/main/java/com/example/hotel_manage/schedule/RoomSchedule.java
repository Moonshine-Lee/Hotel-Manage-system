package com.example.hotel_manage.schedule;

import com.example.hotel_manage.Handler.WebSocketHandler;
import com.example.hotel_manage.Mapper.RoomMapper;
import com.example.hotel_manage.Pojo.RoomFront;
import com.example.hotel_manage.Pojo.RoomSql;
import com.example.hotel_manage.Service.RoomService;
import com.example.hotel_manage.Utils.RoomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
@EnableScheduling
public class RoomSchedule {
    @Autowired
    RoomMapper roomMapper ;
    @Autowired
    RoomService roomService ;
    @Autowired
    RoomStringUtils roomStringUtils;
    @Autowired
    WebSocketHandler webSocketHandler;

    //房间text数据日常更新 每天六点执行
    @Scheduled(cron = "0 0 6 * * ?")
    public void roomAvailableTextUpdate() {

        List<RoomFront> availableRoom = roomService.findAvailableRoom(LocalDate.now().plusDays(90));
        List<RoomFront> allRoom = roomService.findAllRoom();
        for(RoomFront room : allRoom) {
            Boolean[] isAvailableIn90Days = room.getIsAvailableIn90Days();
            Boolean[] booleans=new Boolean[isAvailableIn90Days.length];
            for(int i=0;i<89;i++) {
                booleans[i]=isAvailableIn90Days[i+1];
            }

            if(availableRoom.contains(room)) {
                booleans[89]=true;
            }
            else {
                booleans[89]=false;
            }
            room.setIsAvailableIn90Days(booleans);
            roomMapper.update(roomStringUtils.frontToSql(room));
        }
    }

    //逾期未退房检测
    // 逾期未退房检测
    @Scheduled(cron = "0 0 14 * * ?")
    public void checkOutUrge() {
        List<String> unchekoutRoomIds = roomService.findUnchekoutRoomIds();

        // 发送 WebSocket 消息通知前端弹出窗口
        webSocketHandler.sendMessageToAll("请注意，有未退房记录！"+unchekoutRoomIds);
    }

}
