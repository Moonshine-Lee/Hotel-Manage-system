package com.example.hotel_manage.Config;

import com.example.hotel_manage.Utils.HttpUtils;
import com.example.hotel_manage.Utils.RoomStringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {
    @Bean
    public HttpUtils httpUtils(){
        return new HttpUtils();
    }
    @Bean
    public RoomStringUtils roomStringUtils() {
        return new RoomStringUtils();
    }
}
