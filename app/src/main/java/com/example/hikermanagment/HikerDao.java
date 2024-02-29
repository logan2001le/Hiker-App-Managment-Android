package com.example.hikermanagment;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface HikerDao {

    @Insert
    long insertHiker (Hiker hiker);

    @Query("SELECT * FROM hikers ORDER BY name")
    List<Hiker> getAllHikers();

    @Query("SELECT * FROM hikers where name= :name")
    List<Hiker> checkHiker(String name);

    @Update
    int updateHiker(Hiker hiker);

    @Delete
    int deleteHiker(Hiker hiker);

    @Query("DELETE FROM hikers")
    void deleteAllHiker();
    @Query("SELECT * FROM hikers WHERE name LIKE '%' || :name || '%'")
    List<Hiker> searchHiker(String name);
}
