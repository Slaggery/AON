package ru.slagger.aon;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import ru.slagger.aon.DataBase.DataSource.DayRepository;
import ru.slagger.aon.DataBase.Local.AONRoomDatabase;
import ru.slagger.aon.DataBase.Local.DayDataSource;
import ru.slagger.aon.DataBase.ModelDB.Day;
import ru.slagger.aon.Model.BlackNumber;
import ru.slagger.aon.Model.Month;
import ru.slagger.aon.Retrofit.IA2AONAPI;
import ru.slagger.aon.Utils.Common;

public class TelActivity extends AppCompatActivity {
    String telephoneNumber, mDay,simpleDate;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    List<Day> dayList;
    List<BlackNumber> blackNumberList;
    Calendar calendar = Calendar.getInstance(Locale.getDefault());
    SimpleDateFormat dayFormat = new SimpleDateFormat("dd",Locale.getDefault());
    IA2AONAPI mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tel);
        Common.roomDatabase = AONRoomDatabase.getInstance(this);
        Common.dayRepository = DayRepository.getInstance(DayDataSource.getInstance(Common.roomDatabase.dayDAO()));
        mService = Common.getAPI();
        mDay = dayFormat.format(calendar.getTime());

        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
        String[] monthNames = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
        String mYear = yearFormat.format(calendar.getTime());
        String mMonth = monthNames[calendar.get(Calendar.MONTH)];
        simpleDate = mMonth + " " + mYear;

        Bundle tel = getIntent().getExtras();
        telephoneNumber = tel.get("tel").toString();

        String darkNumber = String.format("%.5s",telephoneNumber);

        if (darkNumber.equals("+7495") || darkNumber.equals("+7499"))
        {
            Toast.makeText(this, "Данный номер находится в черном списке!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(TelActivity.this);
        builder.setTitle("Получен номер входящего вызова");
        builder.setMessage(telephoneNumber);
        builder.setCancelable(true);

        final AlertDialog dialog = builder.create();
        dialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
          //      dialog.dismiss();
                dialogLoadBlackList();
            }
        },1500);
    }

    private void checkDay() {
        if (dayList.size()==0){
            dialogCheckBlackList();
            //checkBlackList();
        }
        else{
            if (mDay.equals(dayList.get(0).date)){
                int q=0;
                for (int i=0;i<dayList.size();i++){
                    if (telephoneNumber.equals(dayList.get(i).number)){
                        Toast.makeText(this, "Данный номер сегодня уже добавлялся", Toast.LENGTH_SHORT).show();
                        q=1;
                        break;
                    }
                }
                if (q==0){
                    dialogCheckBlackList();
                   // checkBlackList();
                }
                else {
                    finish();
                }
            }
            else {
                Common.dayRepository.emptyDay();
                dayList.clear();
            }
        }
    }

    private void dialogSendMessage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(TelActivity.this);
        builder.setMessage("Проверка прошла успешно, можно отправлять сообщение в telegram!");
        builder.setCancelable(true);
        final AlertDialog dialog = builder.create();
        dialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
          //      dialog.dismiss();
                sendMessage();
            }
        },1500);
    }

    private void sendMessage() {
        compositeDisposable.add(mService.getAllCityMonth("samara",simpleDate)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<Month>>() {
                    @Override
                    public void accept(final List<Month> months) throws Exception {
                        sendMessageTelegram();
                        updateCityForVolume(months);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(TelActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));

    }

    private void updateCityForVolume(List<Month> months) {
        compositeDisposable.add(mService.updateCityForVolume("samara",
                simpleDate,
                String.valueOf(months.get(0).Volume+1))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Day dayItem = new Day();
                        dayItem.date = mDay;
                        dayItem.number = telephoneNumber;
                        Common.dayRepository.insertToDay(dayItem);
                        Toast.makeText(TelActivity.this, s, Toast.LENGTH_SHORT).show();
                        compositeDisposable.dispose();
                        System.exit(0);


                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(TelActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void sendMessageTelegram() {
        compositeDisposable.add(mService.sendMessage("-1001448595022",
                ((dayList.size()+1) + ". " + telephoneNumber))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                    }
                }));
    }

    private void dialogCheckBlackList(){
        AlertDialog.Builder builder = new AlertDialog.Builder(TelActivity.this);
        builder.setMessage("Проверка номера в черном списке, если его там нет, то отправляем сообщение");
        builder.setCancelable(true);
        final AlertDialog dialog = builder.create();
        dialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
           //     dialog.dismiss();
                checkBlackList();
            }
        },1500);
    }

    private void checkBlackList() {
        if (blackNumberList.size()==0){
            dialogSendMessage();
            //sendMessage();
        }
        else
        {
            int q=0;
            for (int i=0;i<blackNumberList.size();i++){
                if (telephoneNumber.equals(blackNumberList.get(i).Number)){
                    Toast.makeText(this, "Данный номер в черном списке!", Toast.LENGTH_SHORT).show();
                    q=1;
                    break;
                }
            }
            if (q==0){
                dialogSendMessage();
                //sendMessage();
            }
            else {
                finish();
            }
        }
    }
    private void dialogLoadBlackList(){
        AlertDialog.Builder builder = new AlertDialog.Builder(TelActivity.this);
        builder.setMessage("Получаем список черных номеров");
        final AlertDialog dialog = builder.create();
        dialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
           //     dialog.dismiss();
                loadBlackList();
            }
        },1500);

    }
    private void loadBlackList() {
        compositeDisposable.add(mService.getBlackList()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(new Consumer<List<BlackNumber>>() {
            @Override
            public void accept(List<BlackNumber> blackNumbers) throws Exception {
                blackNumberList = blackNumbers;
                AlertDialog.Builder builder = new AlertDialog.Builder(TelActivity.this);
                builder.setMessage("Список черных номеров получен");
                builder.setCancelable(true);
                final AlertDialog dialog = builder.create();
                dialog.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
         //               dialog.dismiss();
                        dialogGetDatabase();
                    }
                },1500);
            }
        }));
    }

    private void dialogGetDatabase() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TelActivity.this);
        builder.setMessage("Получаем список всех номеров телефонов за сегодня");
        builder.setCancelable(true);
        final AlertDialog dialog = builder.create();
        dialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
     //           dialog.dismiss();
                getDatabase();
            }
        },1500);
    }


    private void getDatabase() {
        compositeDisposable.add(Common.dayRepository.getDayItems()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(new Consumer<List<Day>>() {
            @Override
            public void accept(List<Day> days) throws Exception {
                dayList = days;
                AlertDialog.Builder builder = new AlertDialog.Builder(TelActivity.this);
                builder.setMessage("Список всех номеров телефонов за сегодня получен");
                builder.setCancelable(true);
                final AlertDialog dialog = builder.create();
                dialog.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                      //  dialog.dismiss();
                        checkDay();
                    }
                },1500);
            }
        }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }

    @Override
    protected void onStop() {
        super.onStop();
        compositeDisposable.clear();
    }
}
