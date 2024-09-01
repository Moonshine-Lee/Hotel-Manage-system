package com.example.hotel_manage.Mapper;

import com.example.hotel_manage.Pojo.Checkin;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Mapper
public interface CheckinMapper {

    @Insert("INSERT INTO check_in (room_id,customer_phone, customer_identitycard, check_in_time,days, check_in_status, check_out_time, payment_amount) " +
            "VALUES (#{checkIn.roomId},#{checkIn.customerPhone}, #{checkIn.customerIdentityCard}, #{checkIn.checkInTime},#{checkIn.days}, #{checkIn.checkInStatus}, #{checkIn.checkOutTime}, #{checkIn.paymentAmount})")
    @Options(useGeneratedKeys = true, keyProperty = "checkIn.checkinId")
    int insert(@Param("checkIn") Checkin checkIn);

    @Select("SELECT * FROM check_in WHERE checkin_id = #{id}")
    Checkin findById(@Param("id") Integer id);

    @Update("UPDATE check_in SET room_id=#{checkIn.roomId}, customer_identitycard=#{checkIn.customerIdentityCard}, check_in_time=#{checkIn.checkInTime}, " +
            "check_in_status=#{checkIn.checkInStatus}, check_out_time=#{checkIn.checkOutTime}, payment_amount=#{checkIn.paymentAmount} " +
            "WHERE checkin_id = #{checkIn.checkinId}")
    int update(@Param("checkIn") Checkin checkIn);

    @Delete("DELETE FROM check_in WHERE checkin_id = #{id}")
    int deleteById(@Param("id") Integer id);
    @Select("select * from check_in where customer_identitycard=#{customerIdentityCard} and #{date} between check_in_time and DATE_ADD(check_in_time, INTERVAL days DAY)")
    Checkin findByCustomerIdentityCardAndDate(@Param("customerIdentityCard") String customerIdentityCard,@Param("date") LocalDate date);

    @Select("select * from check_in where room_id=#{roomId} and #{date} between check_in_time and DATE_ADD(check_in_time, INTERVAL days DAY)")
    Checkin findByRoomIdAndDate(@Param("roomId") String roomId, @Param("date") LocalDate date);
    @Update("update check_in set check_in_status='checkout',check_out_time=#{checkoutTime} where checkin_id=#{checkinId}")
    void checkOut(@Param("checkinId") Integer checkinId,@Param("checkoutTime")  LocalDateTime checkoutTime);
    @Select("select * from check_in where #{date}=DATE_ADD(check_in_time, INTERVAL days DAY)")
    List<Checkin> findByEndDate(LocalDate date);

    List<Checkin> list(@Param("customerPhone")String customerPhone,@Param("roomId") String roomId,@Param("date") LocalDate date);
}
