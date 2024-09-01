package com.example.hotel_manage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@ServletComponentScan
@SpringBootApplication
@EnableAsync
public class HotelManageApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotelManageApplication.class, args);
    }

}
