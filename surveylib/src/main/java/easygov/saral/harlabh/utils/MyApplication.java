package easygov.saral.harlabh.utils;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;

import easygov.saral.harlabh.R;
import io.fabric.sdk.android.Fabric;
import java.util.Date;

/**
 * Created by apoorv on 22/08/17.
 */

public class MyApplication extends Application {

    public static boolean is_scheme = false;

    public static String firstname="";
    public static String lastname="";
    public static String street1 ="";
    public static String street2="";
    public static String city="";
    public static String zip="";
    public static String phone="";
    public static Bundle dataBundle=new Bundle();
    public static int stateId;
    public static int distId;
    public static String language;

    public static double latitude=0.0; // latitude
    public static double longitude=0.0; // longitude
    public static String Client="haryana";
    public static String dismiss="";
    public static int Tester=0;

    @Override
    public void onCreate() {
        super.onCreate();
        dismiss=getResources().getString(R.string.dismiss);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        SmsReceiver myReceiver = new SmsReceiver();
        registerReceiver(myReceiver, intentFilter);
        Fabric.with(this, new Crashlytics());
        UIUtils.overrideFont(getApplicationContext(), "MONOSPACE", "fonts/Montserrat-Regular.ttf"); // font from assets: "assets/fonts/Roboto-Regular.ttf
    }
    public static boolean compareDate(Date date1,Date date2)
    {
        return !date2.after(date1);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
    }




}
