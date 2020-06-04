package ru.slagger.aon.DataBase.Local;

import java.util.List;

import io.reactivex.Flowable;
import ru.slagger.aon.DataBase.DataSource.IDayDataSource;
import ru.slagger.aon.DataBase.ModelDB.Day;

public class DayDataSource implements IDayDataSource {

    private DayDAO dayDAO;
    private static DayDataSource instance;

    public DayDataSource(DayDAO dayDAO) {
        this.dayDAO = dayDAO;
    }

    public static DayDataSource getInstance(DayDAO dayDAO)
    {
        if(instance ==null)
            instance = new DayDataSource(dayDAO);
        return instance;
    }

    @Override
    public Flowable<List<Day>> getDayItems() {
        return dayDAO.getDayItems();
    }

    @Override
    public Flowable<List<Day>> getDayItemById(int DayItemId) {
        return dayDAO.getDayItemById(DayItemId);
    }

    @Override
    public int countDayItems() {
        return dayDAO.countDayItems();
    }

    @Override
    public void emptyDay() {
        dayDAO.emptyDay();
    }

    @Override
    public void insertToDay(Day... days) {
        dayDAO.insertToDay(days);
    }

    @Override
    public void updateDay(Day... days) {
        dayDAO.updateDay(days);
    }

    @Override
    public void deleteDayItem(Day day) {
        dayDAO.deleteDayItem(day);
    }
}
