package ru.slagger.aon.DataBase.DataSource;

import java.util.List;

import io.reactivex.Flowable;
import ru.slagger.aon.DataBase.ModelDB.Day;

public interface IDayDataSource {
    Flowable<List<Day>> getDayItems();
    Flowable<List<Day>> getDayItemById(int DayItemId);
    int countDayItems();

    void emptyDay();
    void insertToDay(Day...days);
    void updateDay(Day...days);
    void deleteDayItem(Day day);
}
