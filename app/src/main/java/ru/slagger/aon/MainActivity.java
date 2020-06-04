package ru.slagger.aon;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import ru.slagger.aon.Retrofit.IA2AONAPI;
import ru.slagger.aon.Utils.Common;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION = 1000;
    IA2AONAPI mService;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    LinearLayout lnr_visible;
    EditText edt_number;
    Button btn_number;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_PERMISSION:
            {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "Разрешение получено", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Разрешение не получено", Toast.LENGTH_SHORT).show();
            }
            break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_PHONE_STATE
            },REQUEST_PERMISSION);
        mService = Common.getAPI();
        lnr_visible = findViewById(R.id.lnr_visible);
        edt_number = findViewById(R.id.edt_number);
        btn_number = findViewById(R.id.btn_number);

        btn_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_number.getText().toString().equals("+7")||TextUtils.isEmpty(edt_number.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Необходимо ввести номер", Toast.LENGTH_SHORT).show();
                    return;
                }
                addDarkNumber(edt_number.getText().toString());
            }
        });
    }

    private void addDarkNumber(String number) {
        compositeDisposable.add(mService.updateBlackList(number)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                edt_number.setText("+7");
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
