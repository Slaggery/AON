package ru.slagger.aon.DataBase.DataSource;

import java.util.List;

import io.reactivex.Flowable;
import ru.slagger.aon.DataBase.ModelDB.Day;

public class DayRepository implements IDayDataSource {

    private IDayDataSource iDayDataSource;

    public DayRepository(IDayDataSource iDayDataSource) {
        this.iDayDataSource = iDayDataSource;
    }

    private static DayRepository instance;

    public static DayRepository getInstance(IDayDataSource iDayDataSource)
    {
        if(instance ==null)
            instance = new DayRepository(iDayDataSource);
        return instance;
    }

    @Override
    public Flowable<List<Day>> getDayItems() {
        return iDayDataSource.getDayItems();
    }

    @Override
    public Flowable<List<Day>> getDayItemById(int DayItemId) {
        return iDayDataSource.getDayItemById(DayItemId);
    }

    @Override
    public int countDayItems() {
        return iDayDataSource.countDayItems();
    }

    @Override
    public void emptyDay() {
        iDayDataSource.emptyDay();
    }

    @Override
    public void insertToDay(Day... days) {
        iDayDataSource.insertToDay(days);
    }

    @Override
    public void updateDay(Day... days) {
        iDayDataSource.updateDay(days);
    }

    @Override
    public void deleteDayItem(Day day) {
        iDayDataSource.deleteDayItem(day);
    }
}