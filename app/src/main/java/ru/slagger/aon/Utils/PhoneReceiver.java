package ru.slagger.aon.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.Toast;
import ru.slagger.aon.TelActivity;

public class PhoneReceiver extends BroadcastReceiver {
    private static boolean incomingCall = false;
    String message=" ";
    private static String phoneNumber;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            String phoneState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (phoneState.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            }
            if (phoneState.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                if (TextUtils.isEmpty(phoneNumber)){
                    Toast.makeText(context, "Номер телефона не определен!", Toast.LENGTH_LONG).show();
                    return;
                }
                incomingCall = true;
                message = phoneNumber;

                Intent intent1 = new Intent(context, TelActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent1.putExtra("tel",message);
                context.startActivity(intent1);
            }
        }
    }
}
