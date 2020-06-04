package ru.slagger.aon.Utils;

import ru.slagger.aon.DataBase.DataSource.DayRepository;
import ru.slagger.aon.DataBase.Local.AONRoomDatabase;
import ru.slagger.aon.Model.City;
import ru.slagger.aon.Retrofit.IA2AONAPI;
import ru.slagger.aon.Retrofit.RetrofitClient;

public class Common {
    //Database
    public static AONRoomDatabase roomDatabase;
    public static DayRepository dayRepository;

    public static final String BASE_URL = "http://9370603969.myjino.ru/AON/";

    public static City currentCity;

    public static IA2AONAPI getAPI()
    {
        return RetrofitClient.getClient(BASE_URL).create(IA2AONAPI.class);
    }
}
