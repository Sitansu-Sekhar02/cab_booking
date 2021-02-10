package com.blucore.chalochalecab.Driver;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.blucore.chalochalecab.Model.MyLocation;

@Database(entities = {MyLocation.class}, version = 1, exportSchema = false)
public abstract class MyDataBase extends RoomDatabase {
    public abstract LocationDao dao();}
