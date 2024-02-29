package com.example.hikermanagment;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Hiker.class},version = 1)

public abstract class AppDatabase extends RoomDatabase {
    public abstract HikerDao hikerDao();
}