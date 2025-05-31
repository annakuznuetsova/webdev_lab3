package com.example.hotelroomapp;

import androidx.room.*;
import java.util.List;

@Dao
public interface RoomDao {

    @Insert
    void insert(RoomEntity room);

    @Update
    void update(RoomEntity room);

    @Delete
    void delete(RoomEntity room);

    @Query("SELECT * FROM room ORDER BY number ASC")
    List<RoomEntity> getAllRooms();

    @Query("SELECT * FROM room WHERE number = :id LIMIT 1")
    RoomEntity findById(int id);

    @Query("SELECT * FROM room WHERE number = :roomNumber LIMIT 1")
    RoomEntity findByNumber(int roomNumber);

    @Query("SELECT * FROM room WHERE price BETWEEN :minPrice AND :maxPrice " +
            "AND (:availableOnly IS 0 OR available = 1) " +
            "AND (:petsAllowed IS 0 OR allowsPets = 1) " +
            "AND (:smokingAllowed IS 0 OR smokingAllowed = 1) " +
            "ORDER BY number ASC")
    List<RoomEntity> filterRooms(double minPrice, double maxPrice, boolean availableOnly, boolean petsAllowed, boolean smokingAllowed);
}
