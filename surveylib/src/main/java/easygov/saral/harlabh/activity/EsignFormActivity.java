package easygov.saral.harlabh.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import easygov.saral.harlabh.R;
import easygov.saral.harlabh.adapters.EsignFormAdapter;
import easygov.saral.harlabh.models.responsemodels.esignformmodel.EsignDatum;
import easygov.saral.harlabh.models.responsemodels.esignformmodel.EsignFormModel;
import easygov.saral.harlabh.models.responsemodels.generalmodel.GeneralModel;
import easygov.saral.harlabh.utils.Constants;
import easygov.saral.harlabh.utils.GeneralFunctions;
import easygov.saral.harlabh.utils.Prefs;
import easygov.saral.harlabh.utils.RestClient;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class EsignFormActivity extends AppCompatActivity {
    private EsignFormModel esignModel;
    private RecyclerView rvEsignForm;
    private TextView tvNextEsign;
    private Prefs mPrefs;
    private CheckBox cbCheck;
    private ImageView ivEsignBack;
    private LinearLayout llEsignForm;
    private List<EsignDatum> details =new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esign_form);
        rvEsignForm= findViewById(R.id.rvEsignForm);
        tvNextEsign= findViewById(R.id.tvNextEsign);
        cbCheck= findViewById(R.id.cbCheck);
        llEsignForm= findViewById(R.id.llEsignForm);
        ivEsignBack= findViewById(R.id.ivEsignBack);

        ivEsignBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rvEsignForm.setLayoutManager(new LinearLayoutManager(this));


        rvEsignForm.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                GeneralFunctions.hideSoftKeyboard(EsignFormActivity.this);
                return false;
            }
        });
        Bundle bundle =new Bundle();
        mPrefs=Prefs.with(this);

        if(mPrefs.getString(Constants.FromAadhar,"").equals("YES"))
        {
            cbCheck.setVisibility(View.VISIBLE);
            tvNextEsign.setText(getResources().getString(R.string.saveapprove));
            tvNextEsign.setBackgroundResource(R.color.grey);
            tvNextEsign.setClickable(false);
        }

        else {
            cbCheck.setVisibility(View.INVISIBLE);
            tvNextEsign.setBackgroundResource(R.color.Sign);
            tvNextEsign.setText(getResources().getString(R.string.Saved));
            tvNextEsign.setClickable(true);
        }

        cbCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    tvNextEsign.setBackgroundResource(R.color.Sign);
                    tvNextEsign.setClickable(true);
                }
                else {
                    tvNextEsign.setBackgroundResource(R.color.grey);
                    tvNextEsign.setClickable(false);
                }
            }
        });
        bundle=getIntent().getExtras();
        if(bundle!=null) {
            esignModel=new Gson().fromJson(bundle.getString("EsignForm"),new TypeToken<EsignFormModel>() {}.getType());
           details=esignModel.data.fieldsData;
            rvEsignForm.setItemViewCacheSize(details.size());
            rvEsignForm.setAdapter(new EsignFormAdapter(this,esignModel.data.fieldsData));
        }

        tvNextEsign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkValidate())
                {
                    hitSaveEsignDetails();
                }
            }
        });

    }

    private void hitSaveEsignDetails() {
        GeneralFunctions.showDialog(this);
        Map<String ,String> map =new HashMap<>();
        for(int i=0;i<details.size();i++)
        {
            if(details.get(i).field__system_name!=null)
                map.put(details.get(i).id+"_"+details.get(i).field__system_name,mPrefs.getString(details.get(i).id+"_"+details.get(i).field__system_name,""));
        }
        map.put("userServiceid",mPrefs.getString(Constants.UserServiceID,""));

        Call<GeneralModel> call = RestClient.get().esignSave(map);
        call.enqueue(new Callback<GeneralModel>() {
            @Override
            public void onResponse(Call<GeneralModel> call, Response<GeneralModel> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful())
                {
                    if(response.body().code.equals("401"))
                    {
                        GeneralFunctions.tokenExpireAction(EsignFormActivity.this);
                    }
                    else if(response.body().success==1)
                    {
                        for(int i=0;i<details.size();i++)
                        {
                            mPrefs.remove(details.get(i).id+"_"+details.get(i).field__system_name);
                        }
                        if(mPrefs.getString(Constants.FromAadhar,"").equals("YES")) {
                            Intent intent = new Intent(EsignFormActivity.this, EsignDoneActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        else {
                            Intent intent = new Intent(EsignFormActivity.this, SuccessFulPaymentActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<GeneralModel> call, Throwable t) {
            GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(llEsignForm,getResources().getString(R.string.netIssue));
            }
        });
    }


    private boolean checkValidate()
    {
        for(int i=0;i<details.size();i++)
        {
            if(details.get(i).validate==true)
            {
                if(mPrefs.getString(details.get(i).id+"_"+details.get(i).field__system_name,"").isEmpty())
                {
                    GeneralFunctions.makeSnackbar(llEsignForm,getResources().getString(R.string.plenter)+details.get(i).display_name);
                    return false;
                }
            }

            if(details.get(i).rule_type!=null)
            {
                if(details.get(i).rule_type.equals("contact"))
                {
                    if(mPrefs.getString(details.get(i).id+"_"+details.get(i).field__system_name,"").length()!=10)
                    {
                        GeneralFunctions.makeSnackbar(llEsignForm,getResources().getString(R.string.invalidnumber));

                        return false;
                    }
                }

                if(details.get(i).rule_type.equals("pincode"))
                {
                    if(mPrefs.getString(details.get(i).id+"_"+details.get(i).field__system_name,"").length()!=6)
                    {
                        GeneralFunctions.makeSnackbar(llEsignForm,getResources().getString(R.string.plzipvalid));

                        return false;
                    }
                }

                if(details.get(i).rule_type.equals("height"))
                {
                    if(mPrefs.getString(details.get(i).id+"_"+details.get(i).field__system_name,"").length()>=2&&mPrefs.getString(details.get(i).display_name,"").length()<=3)
                    {
                        GeneralFunctions.makeSnackbar(llEsignForm,getResources().getString(R.string.plvalidheight));

                        return false;
                    }
                }

                if(details.get(i).rule_type.equals("age"))
                {
                    if(mPrefs.getString(details.get(i).id+"_"+details.get(i).field__system_name,"").length()>2)
                    {
                        GeneralFunctions.makeSnackbar(llEsignForm,getResources().getString(R.string.plvalidage));

                        return false;
                    }
                }

            if(details.get(i).rule_type.equals("email"))
            {
                if(!android.util.Patterns.EMAIL_ADDRESS.matcher(mPrefs.getString(details.get(i).id+"_"+details.get(i).field__system_name,"")).matches())
                {
                    GeneralFunctions.makeSnackbar(llEsignForm,getResources().getString(R.string.plemail));

                    return false;
                }
            }

            }}

        if(cbCheck.getVisibility()==View.VISIBLE)
        {
            if(!cbCheck.isChecked())
            {
                GeneralFunctions.makeSnackbar(llEsignForm,getResources().getString(R.string.plcheckbox));

                return false;
            }

        }

        return true;
    }

    public boolean emailValidator(String email)
    {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

}
