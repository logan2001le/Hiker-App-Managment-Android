package com.example.hikermanagment;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "hikers")
public class Hiker implements Serializable {

    @PrimaryKey(autoGenerate=true)
    public long hiker_id;
    public String name;
    public String location;
    public String date;
    public String park_available;
    public int length;
    public String diff_level;
    public String description;


}

