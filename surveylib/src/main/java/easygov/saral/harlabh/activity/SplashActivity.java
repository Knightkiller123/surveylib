package easygov.saral.harlabh.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import easygov.saral.harlabh.R;
import easygov.saral.harlabh.fragments.LanguageFragment;
import easygov.saral.harlabh.models.responsemodels.generalmodel.GeneralModel;
import easygov.saral.harlabh.utils.Constants;
import easygov.saral.harlabh.utils.GeneralFunctions;
import easygov.saral.harlabh.utils.LocaleHelper;
import easygov.saral.harlabh.utils.MyApplication;
import easygov.saral.harlabh.utils.Prefs;
import easygov.saral.harlabh.utils.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    public static Context inst;
    private ImageView ivLogoAnim;
    private Prefs mPrefs;
    private ProgressBar pbProgress;
    PopupWindow pw;
    Handler handler ;
    //int s=0;
    private RelativeLayout rlUpdate,rlSplashMain,rlAppUnderConst;
    private TextView tvUpdate,tvCancel,tvInnovation,tvKillApp;

    private TextView tvGo;
    private EditText etUrl;
    //private ImageView ivDigi,ivHrLogo,ivCm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        inst=this;
        mPrefs=Prefs.with(this);

        rlAppUnderConst = findViewById(R.id.rlAppUnderConst);
        handler = new Handler();
        ivLogoAnim= findViewById(R.id.ivLogoAnim);
        pbProgress= findViewById(R.id.pbProgress);
        rlUpdate= findViewById(R.id.rlUpdate);
        rlSplashMain= findViewById(R.id.rlSplashMain);
        tvInnovation= findViewById(R.id.tvInnovation);
        tvUpdate= findViewById(R.id.tvUpdate);
        tvCancel= findViewById(R.id.tvCancel);
        tvKillApp= findViewById(R.id.tvKillApp);

        //updateViews("en");
        //dynamicBuild();




        tvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
                        ("market://details?id="+getApplicationContext().getPackageName())));
            }
        });

        tvKillApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        tvInnovation.setText(getResources().getString(R.string.innovate), TextView.BufferType.SPANNABLE);
        Spannable str = (Spannable) tvInnovation.getText();

        str.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.black)), 25, 33, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 25, 33, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

    }
    int dev=0,staging=0,production=0;

    private String after="";


    private void dynamicBuild() {
        tvGo= findViewById(R.id.tvGo);
        etUrl= findViewById(R.id.etUrl);
        tvGo.setVisibility(View.VISIBLE);
        etUrl.setVisibility(View.VISIBLE);
        tvGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!etUrl.getText().toString().isEmpty()) {
                   //Constants.BASE_URL = etUrl.getText().toString();
                    checkAadhaarAvailabilityApi();
                }

                else {
                    GeneralFunctions.makeSnackbar(rlSplashMain,"Enter Url");
                }
            }
        });

       // etUrl.setText("http://");
        //etUrl.setSelection(7);
        /*etUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                after=s.toString();
                if(after.length()<7) {

                    etUrl.setText("http://");
                    etUrl.setSelection(7);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/





    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }


    private void request()
    {
        // this.shouldShowRequestPermissionRationale(new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION});
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE
                    ,Manifest.permission.ACCESS_FINE_LOCATION}, 1011);
        }
    }
    private void startApp()
    {
       /* try {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {


                    handler.removeCallbacksAndMessages(null);
                    //startAppWithLang();
                }


            }, 800);
        }
        catch (Exception e)
        {

        }*/
       // mPrefs.save(Constants.SchemeActive,"1");
        //mPrefs.save(Constants.AadhaarBackendEnable,"1");

        try {
            getSupportFragmentManager().beginTransaction().addToBackStack(null).add(R.id.rlSplashMain,new LanguageFragment()).commit();

        }
        catch (Exception e)
        {}


    }




    public void startAppWithLang(String preLang)
    {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_PHONE_STATE)== PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {

            Intent intent = new Intent(this, SignActivity.class);
            startActivity(intent);
            finish();
        }
        else {request();}
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1011: {
                if (grantResults.length > 0 ) {

                    boolean phoneState= grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean location =grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if(location && phoneState) {
                        Intent intent = new Intent(this, SignActivity.class);
                        startActivity(intent);
                        finish();
                    }


                    if(!phoneState)
                    {
                        if(ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this, Manifest.permission.READ_PHONE_STATE))
                        {
                            //Toast.makeText(inst, "last tym", Toast.LENGTH_SHORT).show();
                            // request();

                            permissionNeeded(getResources().getString(R.string.phnPermissionNeeded));
                        }
                        else {
                            GeneralFunctions.PleaseGrantPermission(rlSplashMain,SplashActivity.this);
                        }
                    }

                   else if(!location)
                    {
                        if(ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this, Manifest.permission.ACCESS_FINE_LOCATION))
                        {
                            //Toast.makeText(inst, "last tym", Toast.LENGTH_SHORT).show();
                           // request();


                            permissionNeeded(getResources().getString(R.string.locationPermissionNeeded));
                        }
                        else {
                            GeneralFunctions.PleaseGrantPermission(rlSplashMain,SplashActivity.this);
                        }
                    }




                }

                /*else if (ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this, Manifest.permission.ACCESS_FINE_LOCATION))
                {
                    Toast.makeText(inst, "Last tym", Toast.LENGTH_SHORT).show();
                    request();
                }
                else {
                    GeneralFunctions.PleaseGrantPermission(rlSplashMain,SplashActivity.this);

                }*/
                return;
            }
        }
    }

    PackageInfo pInfo;
    @Override
    protected void onResume() {
        super.onResume();
        //anrdial();


        try {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {


                   // handler.removeCallbacksAndMessages(null);
                   checkAadhaarAvailabilityApi();
                }


            }, 2000);
        }
        catch (Exception e)
        {

        }


    }



    private void checkAadhaarAvailabilityApi() {

        Call<GeneralModel> call = RestClient.get().checkAadhaarEnabled();
        call.enqueue(new Callback<GeneralModel>() {
            @Override
            public void onResponse(Call<GeneralModel> call, Response<GeneralModel> response) {
                //pbProgress.setVisibility(View.INVISIBLE);
                if(response.isSuccessful()) {
                    if(response.body().code.equals("401"))
                    {
                        GeneralFunctions.tokenExpireAction(SplashActivity.this);
                    }
                    else if (response.body().success == 1) {
                        mPrefs.save(Constants.IMAGESTR, response.body().data.service_category_image_url);
                        try {
                            pInfo = SplashActivity.this.getPackageManager().getPackageInfo(getPackageName(), 0);
                            if (response.body().data.enforce_version != null) {
                                if (Integer.parseInt(response.body().data.enforce_version) > (pInfo.versionCode%100)) {
                                    rlUpdate.setVisibility(View.VISIBLE);
                                    pbProgress.setVisibility(View.GONE);
                                }
                                else  if(response.body().data.is_app_maintenance_on!=null && response.body().data.is_app_maintenance_on.equals("1"))
                                {
                                    rlAppUnderConst.setVisibility(View.VISIBLE);
                                    pbProgress.setVisibility(View.GONE);
                                }
                                else {
                                    if (response.body().data.is_aadhaar_enabled != null)
                                        mPrefs.save(Constants.AadhaarBackendEnable, response.body().data.is_aadhaar_enabled);



                                    if (response.body().data.is_scheme_enabled != null) {
                                        mPrefs.save(Constants.SchemeActive, response.body().data.is_scheme_enabled);


                                    } else {
                                        mPrefs.save(Constants.SchemeActive, "0");
                                    }



                                    if (response.body().data.enable_signup_aadhaar != null)
                                        mPrefs.save(Constants.enable_signup_aadhaar, response.body().data.enable_signup_aadhaar);


                                    if (response.body().data.enable_apply_non_aadhaar != null) {
                                        if(response.body().data.enable_apply_non_aadhaar.equals("1"))
                                        mPrefs.save(Constants.ENABLE_APPLY_NON_AADHAR,true);
                                        else
                                        mPrefs.save(Constants.ENABLE_APPLY_NON_AADHAR,false);


                                    } else {
                                        mPrefs.save(Constants.ENABLE_APPLY_NON_AADHAR, false);
                                    }
                                    try {
                                        startApp();

                                    }
                                    catch (Exception e)
                                    {}

                                }
                            } else {
                                if (response.body().data.is_aadhaar_enabled != null)
                                    mPrefs.save(Constants.AadhaarBackendEnable, response.body().data.is_aadhaar_enabled);
                                if (response.body().data.is_scheme_enabled != null) {
                                    mPrefs.save(Constants.SchemeActive, response.body().data.is_scheme_enabled);

                                } else {
                                    mPrefs.save(Constants.SchemeActive, "0");

                                }
                                try {
                                    startApp();

                                }
                                catch (Exception e)
                                {

                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();

                           // startApp();
                        }


                    }
                }

                else {
                    GeneralFunctions.makeSnackbar(rlSplashMain,getResources().getString(R.string.serverIssue));
                }


            }

            @Override
            public void onFailure(Call<GeneralModel> call, Throwable t) {
                //  pbProgress.setVisibility(View.INVISIBLE);
                //startApp();
                GeneralFunctions.makeSnackbar(rlSplashMain,getResources().getString(R.string.netIssue));

            }
        });
    }




    public void updateViews(String languageCode) {
        LocaleHelper.setLocale(this, languageCode);
      /*  Context context = LocaleHelper.setLocale(this, languageCode);
        Resources resources = context.getResources();*/
    }

   /* private static boolean updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return true;
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void permissionNeeded(String msg) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg)
                .setPositiveButton(this.getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                dialogInterface.dismiss();
                                request();
                            }
                        }).show();


    }


    /*private void anrdial() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Api's fat gayi , contact 7905854833")
                .setPositiveButton(this.getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                dialogInterface.dismiss();
                                finish();
                                //request();
                            }
                        }).show();


    }*/
}
