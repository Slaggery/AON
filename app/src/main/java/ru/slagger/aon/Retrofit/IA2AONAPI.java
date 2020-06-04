package ru.slagger.aon.Retrofit;

import java.util.List;
import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import ru.slagger.aon.Model.BlackNumber;
import ru.slagger.aon.Model.City;
import ru.slagger.aon.Model.Month;

public interface IA2AONAPI {
    @FormUrlEncoded
    @POST("get_all_city.php")
    Observable<List<City>> getAllCity(@Field("id") String id);

    @FormUrlEncoded
    @POST("register_city.php")
    Observable<String>registerNewCity(@Field("id") String id,
                              @Field("city")String city);

    @FormUrlEncoded
    @POST("update_city.php")
    Observable<String>updateCityForVolume(@Field("city") String city,
                                      @Field("month")String month,
                                          @Field("volume") String volume);

    @FormUrlEncoded
    @POST("get_city_month.php")
    Observable<List<Month>>getAllCityMonth(@Field("city") String city,
                                           @Field("month")String month);

    @GET("get_black_list.php")
    Observable<List<BlackNumber>> getBlackList();

    @FormUrlEncoded
    @POST("update_black_list.php")
    Observable<String>updateBlackList(@Field("number") String number);

    @FormUrlEncoded
    @POST ("testbot.php")
    Observable<String>sendMessage(@Field("id")String idchat,
                                  @Field("message")String message);
}
