package easygov.saral.harlabh.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import easygov.saral.harlabh.R;

import easygov.saral.harlabh.models.surveypaging.StartSurveyPaging;
import easygov.saral.harlabh.models.surveypaging.SurveyPagingData;
import easygov.saral.harlabh.utils.Constants;
import easygov.saral.harlabh.utils.GeneralFunctions;
import easygov.saral.harlabh.utils.Prefs;
import easygov.saral.harlabh.utils.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KnowMoreActivity extends AppCompatActivity {

    private ImageView ivCrossKnow;
    private SurveyPagingData schemes;
    private Prefs mPrefs;
    private RelativeLayout rlKnowMoreContainer;
    Map<String,String> map=new HashMap<>();
    int schemesarr[]=new int[1];
    int PERMISSION_ALL = 1;
    private StartSurveyPaging entitlement;
    String buttext="";
    private int qlschemes[]=new int[1];
    String[] PERMISSIONS = { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};


    private TextView tvServName,tvgvfp,tvgvfp1,tvDesc,tvValueVal,tvgvfp2,tvServiceLevel,tvBenefitsKnowMore,tvBenefitsText,tvApplySchemeKnow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_know_more);
        Bundle bundle=new Bundle();
        bundle=getIntent().getExtras();
        init();
        if(bundle!=null)
        {entitlement=new Gson().fromJson(bundle.getString("enlist"),new TypeToken<StartSurveyPaging>() {}.getType());
            schemes=new Gson().fromJson(bundle.getString("servicename"),new TypeToken<SurveyPagingData>() {}.getType());
            tvServName.setText(schemes.service__name);
            tvServiceLevel.setText(schemes.service__service_level.toString()+getResources().getString(R.string.days));

           // removeTags(schemes.service__analysis,"<p>(.*?)</p>",tvDesc);
            tvgvfp.setText(getResources().getString(R.string.Rs)+" " +schemes.govt_fee);
            tvgvfp1.setText(getResources().getString(R.string.Rs)+" " +schemes.service_charge);
            //wantvDesc.setText(getResources().getString(R.string.Rs)+" " +);

            tvDesc.setText(Html.fromHtml(schemes.service__analysis));

            double p=Double.parseDouble(schemes.govt_fee)+
                    Double.parseDouble(schemes.service_charge);

           tvValueVal.setText(Html.fromHtml(schemes.service__benefits));

            tvgvfp2.setText(getResources().getString(R.string.Rs)+" "+p);

            String s="";
            try {
                for(int i=0;i<schemes.benefits.size();i++)
                {
                    s=schemes.benefits.get(i)+"\n";
                }

                String sck=schemes.service__benefits;
                s=s+sck;
                if(s!=null&&s.length()>2)
                    tvBenefitsText.setText(s);

                else {
                    tvBenefitsKnowMore.setVisibility(View.GONE);
                    tvBenefitsText.setVisibility(View.GONE);
                }
            }
            catch (Exception e)
            {
                tvBenefitsKnowMore.setVisibility(View.GONE);
                tvBenefitsText.setVisibility(View.GONE);
            }
            if(schemes.has_elgibility_survey) {


              //  tvDesc.setText(Html.fromHtml(schemes.service__benefits));

                tvApplySchemeKnow.setText(getResources().getString(R.string.checkeligible));

            }
            else{
                tvApplySchemeKnow.setText(getResources().getString(R.string.apply)+" "+getResources().getString(R.string.Rs)+p);

            }




        }
        ivCrossKnow= findViewById(R.id.ivCrossKnow);
        ivCrossKnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishActivity();
            }
        });

    }

    private void init() {
        tvServName= findViewById(R.id.tvServName);
        tvgvfp= findViewById(R.id.tvgvfp);
        tvgvfp1= findViewById(R.id.tvgvfp1);
        mPrefs=Prefs.with(this);
        tvDesc= findViewById(R.id.tvDesc);
        tvBenefitsKnowMore= findViewById(R.id.tvBenefitsKnowMore);
        tvBenefitsText= findViewById(R.id.tvBenefitsText);
        tvValueVal= findViewById(R.id.tvValueVal);
        tvgvfp2= findViewById(R.id.tvgvfp2);
        tvServiceLevel= findViewById(R.id.tvServiceLevel);
        tvApplySchemeKnow= findViewById(R.id.tvApplySchemeKnow);
        rlKnowMoreContainer= findViewById(R.id.rlKnowMoreContainer);
        tvApplySchemeKnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPrefs.remove(Constants.UserServiceID);
                mPrefs.save(Constants.ServiceId,schemes.id.toString());
                double p1=Double.parseDouble(schemes.govt_fee)+
                        Double.parseDouble(schemes.service_charge);
                mPrefs.save(Constants.Name,schemes.service__name);
                mPrefs.save(Constants.GovtFee,getResources().getString(R.string.Rs)+" "+schemes.govt_fee);
                mPrefs.save(Constants.AssistanceCharge,getResources().getString(R.string.Rs)+" "+schemes.service_charge);
                mPrefs.save(Constants.TotalCharge,String.valueOf(p1));
                mPrefs.save(Constants.ServiceLevel,schemes.service__service_level.toString()+getResources().getString(R.string.days));
                if(schemes.has_elgibility_survey) {



                    schemesarr[0] = schemes.id;
                    map.put("current_scheme_list", new Gson().toJson(schemesarr));
                    map.put("survey_id", mPrefs.getString(Constants.SurveyId,""));
                    map.put("qualified_schemes", new Gson().toJson(qlschemes));
                    map.put("existing_field_list", new Gson().toJson(entitlement.data.existing_field_list));
                    map.put("rule_type", entitlement.data.rule_type);
                    map.put(Constants.PARAM_BENEFICIARY_TYPE_ID,mPrefs.getString(Constants.BeneficiaryID,""));
                    hitNext();
                }

                else {

                    // mPrefs.save(Constants.ServiceId,schemes.get(position).id.toString());
                    if(!hasPermissions(KnowMoreActivity.this, PERMISSIONS)){
                        ActivityCompat.requestPermissions(KnowMoreActivity.this, PERMISSIONS, PERMISSION_ALL);
                    }
                    else {
                        Intent intent = new Intent(KnowMoreActivity.this, FourthApplicationActivity.class);
                       startActivity(intent);
                        finish();
                    }


                }
            }
        });

    }


    @Override
    public void onBackPressed() {
      finishActivity();
       // super.onBackPressed();
    }

    private void finishActivity()
    {
        Intent intent =new Intent(this,SecondSchemesActivity.class);
        startActivity(intent);
        overridePendingTransition( R.anim.reverseanimationup_activity, R.anim.reverseanimatedown_activity );
        finish();
    }

    private void removeTags(String str,String pat,TextView tv)
    {
        String set="";
        Pattern pattern = Pattern.compile(pat);//"<p>(.*?)</p>"
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            set+=matcher.group(1)+"\n";

        }
        tv.setText(set);
    }




    public void hitNext()
    {
        GeneralFunctions.showDialog(this);
        map.put("geography_id",mPrefs.getString(Constants.GeographyId,""));
        Call<StartSurveyPaging> call= RestClient.get().setEnt(map);
        call.enqueue(new Callback<StartSurveyPaging>() {
            @Override
            public void onResponse(Call<StartSurveyPaging> call, Response<StartSurveyPaging> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful()) {
                    if(response.body().code==401)
                    {
                            GeneralFunctions.tokenExpireAction(KnowMoreActivity.this);
                    }
                    if(response.body().success==1) {
                        StartSurveyPaging object = response.body();
                        mPrefs.save(Constants.GenerateSurveyId,"yes");

                        Intent intent = new Intent(KnowMoreActivity.this, ThirdEligibilityActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("final", new Gson().toJson(response.body()));
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        GeneralFunctions.makeSnackbar(rlKnowMoreContainer,response.body().message);
                    }
                    // ((SelectServiceActivity)context).finish();

                }
                else{
                    GeneralFunctions.makeSnackbar(rlKnowMoreContainer,getResources().getString(R.string.serverIssue));
                }


            }

            @Override
            public void onFailure(Call<StartSurveyPaging> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(rlKnowMoreContainer,getResources().getString(R.string.netIssue));
            }
        });
    }


    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
