package ru.slagger.aon.DataBase.Local;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;

import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import ru.slagger.aon.DataBase.ModelDB.Day;

@Database(entities = {Day.class}, version = 1,exportSchema = false)

public abstract class AONRoomDatabase extends RoomDatabase {
    public abstract DayDAO dayDAO();
    private static AONRoomDatabase instance;

    public static AONRoomDatabase getInstance(Context context)
    {
        if(instance == null)
            instance = Room.databaseBuilder(context,AONRoomDatabase.class,"AON_DB")
                    .allowMainThreadQueries()
                    .build();
        return instance;
    }
}
