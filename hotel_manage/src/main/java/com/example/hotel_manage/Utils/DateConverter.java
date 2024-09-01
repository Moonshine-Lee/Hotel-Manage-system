package com.example.hotel_manage.Utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateConverter {
    public static LocalDate convertStringToLocalDate(String paymentDate) {
        if (paymentDate==null || paymentDate==""){
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // 根据你的日期格式调整pattern
        try {
            return LocalDate.parse(paymentDate, formatter);
        } catch (DateTimeParseException e) {
            // 处理解析错误，例如抛出异常或返回null
            throw new IllegalArgumentException("Invalid date format. Please use yyyy-MM-dd.");
        }
    }

    public static void main(String[] args) {
        String paymentDate = "2024-06-05";
        LocalDate localDate = convertStringToLocalDate(paymentDate);
        System.out.println("Converted LocalDate: " + localDate);
    }
}

