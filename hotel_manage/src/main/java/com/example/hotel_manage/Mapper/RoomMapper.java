package com.example.hotel_manage.Mapper;

import com.example.hotel_manage.Pojo.Enum.RoomStatus;
import com.example.hotel_manage.Pojo.RoomSql;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;

import java.util.List;

@Mapper
public interface RoomMapper {
    @Update("update room set room_status='available' where room_id=#{roomId}")
    void clean(String roomId);
    @Insert("INSERT INTO room (room_id, price, room_type, is_available_in90days, description) " +
            "VALUES (#{room.roomId}, #{room.price}, #{room.roomType}, #{room.isAvailableIn90Days}, #{room.description})")
    int insert(@Param("room") RoomSql room);

    @Select("SELECT * FROM room WHERE room_id = #{id}")
    RoomSql findById(@Param("id") String id);

    @Update("UPDATE room SET price=#{room.price}, room_type=#{room.roomType}, " +
            "description=#{room.description},room_status=#{room.roomStatus} WHERE room_id = #{room.roomId}")
    int update(@Param("room") RoomSql room);
    @Update("UPDATE room SET price=#{room.price}, room_type=#{room.roomType}, " +
            "description=#{room.description},room_status=#{room.roomStatus},is_available_in90days=#{room.isAvailableIn90Days} WHERE room_id = #{room.roomId}")
    int updateWithAvailable(@Param("room") RoomSql room);

    @Delete("DELETE FROM room WHERE room_id = #{id}")
    int deleteById(@Param("id") String id);

    @Select("select * from room")
    List<RoomSql> findAllRooms();

    @Select("select room_id from room")
    List<String> findAllRoomIds();

    @Select("select * from room where room_id = #{RoomId}")
    RoomSql findRoomById(@Param("RoomId") String id);

    List<RoomSql> getRoomSqlListByIds(List<String> ids);

    @Update("update room set is_available_in90days=#{available},room_status = 'occupied'  where room_id=#{id}")
    void checkIn( @Param("id") String id,@Param("available") String isAvailableIn90Days);

    @Update("update room set price=#{price} where room_type=#{roomType}")
    void setprice(@Param("roomType") String roomType, @Param("price") double price);
    @Select("select avg(room.price) from room where room_type=#{roomType}")
    Float getPrice(@Param("roomType") String roomType);

    List<RoomSql> list(@Param("roomId") String roomId,@Param("roomType") String roomType,@Param("roomStatus") RoomStatus roomStatus);

    @Update("update room set room_status='cleaning',is_available_in90days=#{avai}  where room_id=#{roomId}")
    void checkout(@Param("roomId") String roomId,@Param("avai") String avai);
}
