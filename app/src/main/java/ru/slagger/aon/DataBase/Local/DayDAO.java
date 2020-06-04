package ru.slagger.aon.DataBase.Local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Flowable;
import ru.slagger.aon.DataBase.ModelDB.Day;

@Dao
public interface DayDAO {
    @Query("SELECT * FROM Day")
    Flowable<List<Day>> getDayItems();

    @Query ("SELECT * FROM Day WHERE id=:ItemId")
    Flowable<List<Day>> getDayItemById(int ItemId);

    @Query ("SELECT COUNT(*) from Day")
    int countDayItems();

    @Query ("DELETE FROM Day")
    void emptyDay();

    @Insert
    void insertToDay(Day...Days);

    @Update
    void updateDay(Day...days);

    @Delete
    void deleteDayItem(Day day);
}
