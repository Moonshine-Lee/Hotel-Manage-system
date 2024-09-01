package com.example.hotel_manage.Mapper;

import com.example.hotel_manage.Pojo.Bill;
import com.example.hotel_manage.Pojo.Enum.BillType;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface BillMapper {

    @Insert("INSERT INTO bill (bill_type,room_id,order_id,checkin_id,amount, payment_method) " +
            "VALUES (#{bill.billType},#{bill.roomId},#{bill.orderId},#{bill.checkinId},#{bill.amount}, #{bill.paymentMethod})")
    @Options(useGeneratedKeys = true,keyProperty = "bill.billId")
    void insert(@Param("bill") Bill bill);

    @Select("SELECT * FROM bill WHERE bill_id = #{id}")
    Bill findById(@Param("id") Integer id);

    void update( Bill bill);

    @Delete("DELETE FROM bill WHERE bill_id = #{id}")
    void deleteById(@Param("id") Integer id);

    List<Bill> list(@Param("billType") BillType billType,@Param("roomId") String roomId,@Param("paymentDate") LocalDate paymentDate);
}
