package easygov.saral.harlabh.activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import easygov.saral.harlabh.adapters.BenefitsAdapter;
import easygov.saral.harlabh.models.BenefitList;
import easygov.saral.harlabh.models.responsemodels.generalmodel.GeneralModel;
import easygov.saral.harlabh.models.responsemodels.getkyc.GetKycData;
import easygov.saral.harlabh.models.responsemodels.getkyc.GetKycModel;
import easygov.saral.harlabh.models.responsemodels.kycmodel.Kyc;
import easygov.saral.harlabh.models.responsemodels.kycmodel.KycModel;
import easygov.saral.harlabh.models.responsemodels.statesndistricts.DistrictDetails;
import easygov.saral.harlabh.models.responsemodels.statesndistricts.DistrictsModel;
import easygov.saral.harlabh.models.responsemodels.statesndistricts.StateDetails;
import easygov.saral.harlabh.models.responsemodels.statesndistricts.StatesModel;
import easygov.saral.harlabh.models.surveypaging.CustomBenefits;
import easygov.saral.harlabh.models.surveypaging.StartSurveyPaging;
import easygov.saral.harlabh.utils.GeneralFunctions;
import easygov.saral.harlabh.utils.MyApplication;
import easygov.saral.harlabh.utils.Prefs;
import easygov.saral.harlabh.R;
import easygov.saral.harlabh.adapters.SeconPagerAdapter;
import easygov.saral.harlabh.utils.Constants;
import easygov.saral.harlabh.utils.RestClient;
import easygov.saral.harlabh.utils.SmsListener;
import easygov.saral.harlabh.utils.SmsReceiver;
import easygov.saral.harlabh.utils.VerhoeffAlgorithm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView tvNextForm,tvDobStatic,tvCareOf;
    private List<StateDetails> states;
    private Spinner spSpinNoAadhaar,SpStateBillForm,spDistBillForm,spCareOf;
    Map<String,String> map =new HashMap<>();
    private Prefs mPrefs;
    private Calendar myCalendar;
    private  TextView tvEdit;
    private String surveyManage="no";
    private List<String> spinnerStates=new ArrayList<>();
    private List<Integer> stateIds=new ArrayList<>();
    private List<Integer> distIds=new ArrayList<>();
    private List<String> spinDistricts=new ArrayList<>();
    DatePickerDialog.OnDateSetListener date;
    private List<DistrictDetails> districts =new ArrayList<>();
    String stateSelectedString,districtSelectedString;
    public  int stateSelected=0;
    private   int distSelected=0;
    private Pattern ePat;
    private Kyc kycExternal;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private String careof="";
    private String genderSelected="";
    private String yob="";
    private int fromQr=0;
    private EditText etNameDet,etAddDet1 ,etZipDet ,  etPhnDet ,etEmailDet ,etLastNameDet,etCityDet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();
        setCareofSpinner();
        //setData();

        /*if(mPrefs.getString(Constants.IS_USER_FIRST_TIME,"").equals("yes"))
        {
            mPrefs.save(Constants.IS_USER_FIRST_TIME,"no");
            getSurveyId("myself");
        }

        else
            getKyc(mPrefs.getString(Constants.ProfileId,""),mPrefs.getString(Constants.Applyingfor,""));

*/

        getKyc("","myself");
        disableViews();

    }

    private void disableViews() {

        tvNextForm.setVisibility(View.GONE);
        spCareOf.setEnabled(false);
        spDistBillForm.setEnabled(false);
        spSpinNoAadhaar.setEnabled(false);
        tvDobStatic.setEnabled(false);
        tvDobStatic.setTextColor(ContextCompat.getColor(this,R.color.black));
        etLastNameDet.setTextColor(ContextCompat.getColor(this,R.color.black));
        SpStateBillForm.setEnabled(false);
        etLastNameDet.setEnabled(false);
        etEmailDet.setEnabled(false);
        etPhnDet.setEnabled(false);
        etZipDet.setEnabled(false);
        etAddDet1.setEnabled(false);
        etNameDet.setEnabled(false);
        etCityDet.setEnabled(false);

    }
    private void enableViews() {

        spCareOf.setEnabled(true);
        spDistBillForm.setEnabled(true);
        spSpinNoAadhaar.setEnabled(true);
        tvDobStatic.setEnabled(true);
        tvDobStatic.setTextColor(ContextCompat.getColor(this,R.color.black));
        etLastNameDet.setTextColor(ContextCompat.getColor(this,R.color.black));
        SpStateBillForm.setEnabled(true);
        etLastNameDet.setEnabled(true);
        etEmailDet.setEnabled(true);
        etPhnDet.setEnabled(true);
        etZipDet.setEnabled(true);
        etAddDet1.setEnabled(true);
        etNameDet.setEnabled(true);
        etCityDet.setEnabled(true);
        etNameDet.requestFocus();

    }

    private void getKyc(String id, final String type) {
        GeneralFunctions.showDialog(ProfileActivity.this);
        Call<GetKycModel> call= RestClient.get().getKyc(id,type);
        call.enqueue(new Callback<GetKycModel>() {
            @Override
            public void onResponse(Call<GetKycModel> call, Response<GetKycModel> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful())
                {
                    if(response.body().success==1)
                    {
                        if(response.body().data!=null) {
                            setKycData(response.body().data);
                            if(type.equals("myself"))
                                mPrefs.save(Constants.SelfName,response.body().data.name);
                            //\ else  mPrefs.save(Constants.SelfName,"");
                        }
                    }
                    else {
                        if((response.body().code)==404)
                        {}
                        else
                            GeneralFunctions.makeSnackbar(etNameDet,response.body().message);
                    }
                }
                else {
                    GeneralFunctions.makeSnackbar(etNameDet,getResources().getString(R.string.serverIssue));
                }
            }

            @Override
            public void onFailure(Call<GetKycModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(etNameDet,getResources().getString(R.string.netIssue));
            }
        });
    }

    private void setKycData(GetKycData data) {

        if(data.name!=null)
        {
            etNameDet.setText(data.name);
        }

        if(data.co!=null)
        {
            etLastNameDet.setText(data.relative_name);
        }
        if(data.house!=null)
        {
            etAddDet1.setText(data.house);
        }


        if(data.subdist!=null)
        {
            etCityDet.setText(data.subdist);
        }
        if(data.phone!=null)
        {
            etPhnDet.setText( data.phone);
        }

        if(data.pc!=null)
        {
            etZipDet.setText(data.pc);
        }

        if(data.email!=null)
        {
            etEmailDet.setText(data.email);
        }

        if(data.dob!=null)
        {
            tvDobStatic.setText(data.dob);
        }

    }


    private void setFocusListeners() {
      /*  etNameDet.setOnFocusChangeListener(this);
        etEntityName.setOnFocusChangeListener(this);
        etRepresentativeContactNo.setOnFocusChangeListener(this);
        etRepresentativeName.setOnFocusChangeListener(this);
        etAddDet1.setOnFocusChangeListener(this);
        etAdd2.setOnFocusChangeListener(this);
        etZipDet.setOnFocusChangeListener(this);
        etPhnDet.setOnFocusChangeListener(this);
        etEmailDet.setOnFocusChangeListener(this);
        etCityDet.setOnFocusChangeListener(this);
        etLastNameDet.setOnFocusChangeListener(this);
      */  }


    private void setCareofSpinner() {
        ArrayList<String> care=new ArrayList<>();
        care.add(" "+getResources().getString(R.string.sonof));
        care.add(" "+getResources().getString(R.string.daughterof));
        care.add(" "+getResources().getString(R.string.wifeof));
        ArrayAdapter<String> adapter= new ArrayAdapter<>(this, R.layout.spinner_title_text, care);
        adapter.setDropDownViewResource(R.layout.spinner_adapter_value);



        spCareOf.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) parent.getItemAtPosition(position);
                if (view!=null)
                ((TextView)view).setTextColor(ContextCompat.getColor(ProfileActivity.this,R.color.black));
                switch (position) {
                    case 0:
                        tvCareOf.setText(getResources().getString(R.string.sonoff));
                        careof = "S/O: ";
                        mPrefs.save(Constants.CareOf, selected);
                        break;
                    case 1:
                        tvCareOf.setText(getResources().getString(R.string.daughteroff));
                        careof = "D/O: ";
                        mPrefs.save(Constants.CareOf, selected);
                        break;
                    case 2:
                        tvCareOf.setText(getResources().getString(R.string.wifeoff));
                        careof = "W/O: ";
                        mPrefs.save(Constants.CareOf, selected);
                        break;
                    default:
                        tvCareOf.setText(getResources().getString(R.string.sonoff));
                        careof = "S/O: ";
                        mPrefs.save(Constants.CareOf, selected);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spCareOf.setAdapter(adapter);


        spCareOf.setSelection(mPrefs.getInt(Constants.CareOfPos,0));
    }

    /*private void setData() {


        if(mPrefs.getString(Constants.FirstName,"")!=null)
        {
            etNameDet.setText(mPrefs.getString(Constants.FirstName,""));
        }

        if(mPrefs.getString(Constants.LastName,"")!=null)
        {
            etLastNameDet.setText(mPrefs.getString(Constants.LastName,""));
        }

        if(mPrefs.getString(Constants.Street1,"")!=null)
        {
            etAddDet1.setText(mPrefs.getString(Constants.Street1,""));
        }

        if(mPrefs.getString(Constants.Street2,"")!=null)
        {
            etAdd2.setText(mPrefs.getString(Constants.Street2,""));
        }

        if(mPrefs.getString(Constants.City,"")!=null)
        {
            etCityDet.setText(mPrefs.getString(Constants.City,""));
        }
        if(mPrefs.getString(Constants.Phone,"")!=null)
        {
            etPhnDet.setText(mPrefs.getString(Constants.Phone,""));
        }

        if(mPrefs.getString(Constants.Zip,"")!=null)
        {
            etZipDet.setText(mPrefs.getString(Constants.Zip,""));
        }

        if(mPrefs.getString(Constants.StaticEmail,"")!=null)
        {
            etEmailDet.setText(mPrefs.getString(Constants.StaticEmail,""));
        }

        if(mPrefs.getString(Constants.StaticDob,"")!=null)
        {
            tvDobStatic.setText(mPrefs.getString(Constants.StaticDob,""));
        }

    }*/

    private void init() {
        tvNextForm = findViewById(R.id.tvNextForm);


        mPrefs = Prefs.with(this);


        spSpinNoAadhaar = findViewById(R.id.spSpinNoAadhaar);
        tvEdit = findViewById(R.id.tvEdit);
        SpStateBillForm = findViewById(R.id.SpStateBillForm);
        spDistBillForm = findViewById(R.id.spDistBillForm);
        tvCareOf = findViewById(R.id.tvCareOf);
        spCareOf = findViewById(R.id.spCareOf);
        etLastNameDet = findViewById(R.id.etLastNameDet);
        ePat = Patterns.EMAIL_ADDRESS;
        setCustomDate();
        tvDobStatic = findViewById(R.id.tvDobStatic);
        //map.put("dob",tvDobStatic.getText().toString());

        findViewById(R.id.ivBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        tvDobStatic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*new DatePickerDialog(ProfileActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();*/

                if(!yob.isEmpty())
                    myCalendar.set(Calendar.YEAR, Integer.parseInt(yob));


                new SpinnerDatePickerDialogBuilder()
                        .context(ProfileActivity.this)
                        .callback(dates)
                        .build()
                        .show();
            }
        });

        tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvEdit.setVisibility(View.GONE);

                tvNextForm.setVisibility(View.VISIBLE);
                enableViews();
            }
        });


       /* try {
            String s= getIntent().getStringExtra("isqr");
            if(s.equals("yes"))
            {
                if(ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(ProfileActivity.this, QrReadActivity.class);
                    startActivity(intent);
                }
                else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(
                                new String[]{Manifest.permission.CAMERA},
                                CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
                    }
                }
            }
        } catch (Exception e)
        {

        }*/


        final ArrayList<String> gender=new ArrayList<>();
        gender.add("   "+getResources().getString(R.string.male));
        gender.add("   "+getResources().getString(R.string.femlae));
        gender.add("   "+getResources().getString(R.string.transgender));
        map.put("gender","497");
        ArrayAdapter<String> adapter= new ArrayAdapter<>(this, R.layout.spinner_title_text, gender);
        adapter.setDropDownViewResource(R.layout.spinner_adapter_value);

        Bundle bundle=new Bundle();
        bundle=getIntent().getExtras();
        /*if(bundle!=null)
        {
            try {
                surveyManage=bundle.getString("frommanage");
            }
            catch (Exception e)
            {
                surveyManage="no";
            }
        }*/

        spSpinNoAadhaar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (view!=null)
                ((TextView)view).setTextColor(ContextCompat.getColor(ProfileActivity.this,R.color.black));

                if(position==0)
                {
                    map.put("gender","497");
                    genderSelected="male";
                }
                else if(position==1)
                {
                    map.put("gender","498");
                    genderSelected="female";}
                else if (position==2)
                {
                    map.put("gender","499");
                    genderSelected="trans";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spSpinNoAadhaar.setAdapter(adapter);
        spSpinNoAadhaar.setSelection(mPrefs.getInt(Constants.GenderPos,0));
        getStates();
        etNameDet= findViewById(R.id.etNameDet);
        etAddDet1= findViewById(R.id.etAddDet1);
      /*  etAdd2= findViewById(R.id.etAdd2);
        etEntityName= findViewById(R.id.etEntityName);
        etRepresentativeName= findViewById(R.id.etRepresentativeName);
        etRepresentativeContactNo= findViewById(R.id.etRepresentativeContactNo);

      */  //etDistDet= (EditText) findViewById(R.id.etDistDet);
        //etGenderDet= (EditText) findViewById(R.id.etGenderDet);

        etPhnDet= findViewById(R.id.etPhnDet);
        etEmailDet= findViewById(R.id.etEmailDet);

        etCityDet= findViewById(R.id.etLocality);
        //etStateDet= (EditText) findViewById(R.id.etStateDet);
        etZipDet= findViewById(R.id.etZipDet);

        tvNextForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkValidate()) {
                    map.put("relation_type",mPrefs.getString(Constants.Applyingfor,""));

                    hitKycData();
                }
            }
        });

    }


    com.tsongkha.spinnerdatepicker.DatePickerDialog.OnDateSetListener dates;

    private void setCustomDate()
    {
        myCalendar = Calendar.getInstance();
        dates= new com.tsongkha.spinnerdatepicker.DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(com.tsongkha.spinnerdatepicker.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String s=year+"-"+monthOfYear+"-"+dayOfMonth;
                yob="";
                mPrefs.save("date_of_survey_birth",s);
                updateLabel(year,  monthOfYear+1,  dayOfMonth);
            }
        };



    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mPrefs.save(Constants.FromStaticBack,"yes");
    }



    private boolean checkValidate() {
        switch (careof)
        {
            case "S/O: " :
                mPrefs.save(Constants.CareOfPos,0);
                break;
            case "D/O: ":
                mPrefs.save(Constants.CareOfPos,1);
                break;
            case "W/O: ":
                mPrefs.save(Constants.CareOfPos,2);
                break;
            default:
                mPrefs.save(Constants.CareOfPos,0);


        }

        switch (genderSelected)
        {
            case "male" :
                mPrefs.save(Constants.GenderPos,0);
                break;
            case "female":
                mPrefs.save(Constants.GenderPos,1);
                break;
            case "trans":
                mPrefs.save(Constants.GenderPos,2);
                break;
            default:
                mPrefs.save(Constants.GenderPos,0);


        }
        map.put("survey_id",mPrefs.getString(Constants.SurveyId,""));

        if(!etNameDet.getText().toString().trim().isEmpty())
        {
            map.put("name",etNameDet.getText().toString());
            // firstname=etNameDet.getText().toString();
            mPrefs.save(Constants.FirstName,etNameDet.getText().toString());

            // StaticSurveyPrefsModel sspm = new StaticSurveyPrefsModel();

            // sspm.FirstName=etNameDet.getText().toString();

            // mPrefs.save("StaticSurveyPrefsModel",new Gson().toJson(sspm));
        }
        else {
            GeneralFunctions.makeSnackbar(etNameDet,getResources().getString(R.string.plentername));
            return false;
        }

        if(!etLastNameDet.getText().toString().trim().isEmpty())
        {
            map.put("co",careof+etLastNameDet.getText().toString().trim());
            //lastname=etLastNameDet.getText().toString();
            mPrefs.save(Constants.LastName,etLastNameDet.getText().toString().trim());


        }
        else {
            GeneralFunctions.makeSnackbar(etNameDet,getResources().getString(R.string.plcareof));
            return false;
        }

        if(!etPhnDet.getText().toString().trim().isEmpty())
        {
            map.put("phone",etPhnDet.getText().toString());
            // phone=etPhnDet.getText().toString();
            mPrefs.save(Constants.Phone,etPhnDet.getText().toString());
        }
        else {
            GeneralFunctions.makeSnackbar(etNameDet,getResources().getString(R.string.plphone));
            return false;
        }


        if(etPhnDet.getText().toString().trim().length()==10)
        {
            map.put("phone",etPhnDet.getText().toString());
            // phone=etPhnDet.getText().toString();
            mPrefs.save(Constants.Phone,etPhnDet.getText().toString());
        }
        else {
            GeneralFunctions.makeSnackbar(etNameDet,getResources().getString(R.string.invalidnumber));
            return false;
        }

        if(!etAddDet1.getText().toString().trim().isEmpty())
        {
            map.put("house",etAddDet1.getText().toString());
            //street1=etAddDet1.getText().toString();
            mPrefs.save(Constants.Street1,etAddDet1.getText().toString());
        }
        else {
            GeneralFunctions.makeSnackbar(etNameDet,getResources().getString(R.string.pladd1));
            return false;
        }
/*

        if(!etAdd2.getText().toString().trim().isEmpty())
        {
            map.put("street",etAdd2.getText().toString());
            // street2=etAdd2.getText().toString();
            mPrefs.save(Constants.Street2,etAdd2.getText().toString());
        }
        else {
            GeneralFunctions.makeSnackbar(etNameDet,getResources().getString(R.string.pladd2));
            return false;
        }
        */
        if(!etCityDet.getText().toString().trim().isEmpty())
        {
            map.put("subdist",etCityDet.getText().toString());
            //city=etCityDet.getText().toString();
            mPrefs.save(Constants.City,etCityDet.getText().toString());
        }
        else {
            GeneralFunctions.makeSnackbar(etNameDet,getResources().getString(R.string.plcity));
            return false;
        }


        if(stateSelected>0)
        {
            map.put("state",stateSelectedString);
            mPrefs.save(Constants.Declarationstate,stateSelectedString);
        }
        else {
            GeneralFunctions.makeSnackbar(etNameDet,getResources().getString(R.string.plstate));
            return false;
        }

        if(distSelected>0)
        {
            map.put("dist",districtSelectedString);
            mPrefs.save(Constants.Declarationdist,districtSelectedString);
        }
        else {
            GeneralFunctions.makeSnackbar(etNameDet,getResources().getString(R.string.pldist));
            return false;
        }







        if(!etEmailDet.getText().toString().trim().isEmpty())
        {
            if(ePat.matcher(etEmailDet.getText().toString()).matches()) {
                map.put("email", etEmailDet.getText().toString());
                mPrefs.save(Constants.StaticEmail, etEmailDet.getText().toString());
            }
            else {
                GeneralFunctions.makeSnackbar(etAddDet1,getResources().getString(R.string.plemail));
                return false;
            }
        }





        if(tvDobStatic.getText().toString().isEmpty())
        {
            GeneralFunctions.makeSnackbar(etNameDet,getResources().getString(R.string.pldob));
            return false;
            //map.put("dob",etDobDet.getText().toString());
        }
        else {
            map.put("dob",tvDobStatic.getText().toString());
            mPrefs.save(Constants.StaticDob, tvDobStatic.getText().toString());
        }



        if(!etZipDet.getText().toString().trim().isEmpty())
        {
            map.put("pc",etZipDet.getText().toString());
            //zip=etZipDet.getText().toString();
            mPrefs.save(Constants.Zip,etZipDet.getText().toString());
        }
        else {
            GeneralFunctions.makeSnackbar(etNameDet,getResources().getString(R.string.plzipvalid));
            return false;
        }




        return true;
    }



    private void hitKycData()
    {
        //   GeneralFunctions.showDialog(this);

        if(mPrefs.getString(Constants.Applyingfor,"").equals("someoneelse")||mPrefs.getString(Constants.Applyingfor,"").equals("family")){
            map.put("profile_id",mPrefs.getString(Constants.ProfileId,""));




        }

        else {
            map.put("profile_id","");
        }

        Call<GeneralModel> call = RestClient.get().savekycData(map);
        call.enqueue(new Callback<GeneralModel>() {
            @Override
            public void onResponse(Call<GeneralModel> call, Response<GeneralModel> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful())
                {
                    if(response.body().code.equals("401"))
                    {
                        GeneralFunctions.tokenExpireAction(ProfileActivity.this);
                    }

                    else if(response.body().success==1)
                    {
                        disableViews();
                        mPrefs.save(Constants.SelfName,etNameDet.getText().toString().trim());
                        tvEdit.setVisibility(View.VISIBLE);
                   /*     mPrefs.save(Constants.FormFilled,"YES");
                        mPrefs.save(Constants.FromOtpCanceled,"NO");
                        if(mPrefs.getString(Constants.Applyingfor,"").equals("myself"))
                            mPrefs.save(Constants.SelfName,etNameDet.getText().toString().trim());
                        finish();*/
                    }
                    else {

                        GeneralFunctions.makeSnackbar(etNameDet,response.body().message);
                    }
                }
                else {
                    GeneralFunctions.makeSnackbar(etNameDet,getResources().getString(R.string.serverIssue));
                }
            }

            @Override
            public void onFailure(Call<GeneralModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(etNameDet,getResources().getString(R.string.netIssue));
            }
        });
    }


    /*  private void hitKycData()
    {
        GeneralFunctions.showDialog(this);

         Call<GeneralModel> call = RestClient.get().savekycData(map);
        call.enqueue(new Callback<GeneralModel>() {
            @Override
            public void onResponse(Call<GeneralModel> call, Response<GeneralModel> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful())
                {
                    if(response.body().success==1)
                    {
                        mPrefs.save(Constants.FormFilled,"YES");
                        mPrefs.save(Constants.FromOtpCanceled,"NO");

                        finish();
                    }
                    else {
                        GeneralFunctions.makeSnackbar(etNameDet,response.body().message);
                    }
                }
            }

            @Override
            public void onFailure(Call<GeneralModel> call, Throwable t) {
GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(etNameDet,getResources().getString(R.string.netIssue));
            }
        });
    }
*/
    private void setDate()
    {
        myCalendar = Calendar.getInstance();

        // EditText edittext= (EditText) findViewById(R.id.Birthday);
        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                // updateLabel();
            }

        };
    }

    private void updateLabel(int year, int i, int dayOfMonth) {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        Calendar calendar = Calendar.getInstance();
        int currentYear=calendar.get(Calendar.YEAR);
        int currentMonth =calendar.get(Calendar.MONTH);
        int currentDay =calendar.get(Calendar.DAY_OF_MONTH);






        SimpleDateFormat myFormats = new SimpleDateFormat("dd MM yyyy");

        String firstDate=currentDay+" "+currentMonth+" "+currentYear;
        String secondDate=dayOfMonth+" "+i+" "+year;
        if(MyApplication.compareDate(Calendar.getInstance().getTime(),myCalendar.getTime())) {
            // tvDobStatic.setText(sdf.format(myCalendar.getTime()));
            tvDobStatic.setText(dayOfMonth+"/"+i+"/"+year);
            map.put("dob", tvDobStatic.getText().toString());
            mPrefs.save(Constants.StaticDob, tvDobStatic.getText().toString());
        }
        else {
            GeneralFunctions.makeSnackbar(etNameDet,getResources().getString(R.string.validDate));
        }
    }



    private void getStates() {
        // GeneralFunctions.showDialog(this);
        Call<StatesModel> call= RestClient.get().getAllStates("1000");
        call.enqueue(new Callback<StatesModel>() {
            @Override
            public void onResponse(Call<StatesModel> call, Response<StatesModel> response) {
                GeneralFunctions.dismissDialog();
                if (response.isSuccessful()) {
                    if(response.body().code.equals("401"))
                    {
                        GeneralFunctions.tokenExpireAction(ProfileActivity.this);
                    }
                    else if (response.body().success == 1) {

                        states=response.body().data.objects;
                        setStates();
                        //statesObj = response.body();
                        //rlStates.setAdapter(new StatesAdapter(StateSelectActivity.this, statesObj.data.objects));
/*
                        DbHelper db=new DbHelper(StateSelectActivity.this);
                        db.addStates(statesObj.data.objects);*/
                        //getAllDistricts();
                    } else {

                    }

                }
            }


            @Override
            public void onFailure(Call<StatesModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(etNameDet,getResources().getString(R.string.netIssue));
            }
        });
    }

    private void setStates() {
        int stateId=0;
        stateIds.add(0);
        spinnerStates.add(getResources().getString(R.string.select));
        //  if(Integer.parseInt(mPrefs.getString(Constants.StateId,""))>0)
        //SpStateBillForm.setSelection(mPrefs.getInt(Constants.StateId,0));
        for (int i=0;i<states.size();i++)
        {
            spinnerStates.add(states.get(i).state_name);
            stateIds.add(states.get(i).states_id);
            if(mPrefs.getString(Constants.StateId,"").length()>0)
                if(Integer.parseInt(mPrefs.getString(Constants.StateId,""))==states.get(i).states_id)
                    stateId=i+1;
        }
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(ProfileActivity.this,
                R.layout.spinner_title_text,spinnerStates)
        {
            @Override
            public boolean isEnabled(int position){
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position==0) {
                    // Set the disable item text color
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        adapter.setDropDownViewResource(R.layout.spinner_adapter_value);
        SpStateBillForm.setAdapter(adapter);


        SpStateBillForm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (view!=null)
                ((TextView)view).setTextColor(ContextCompat.getColor(ProfileActivity.this,R.color.black));

                if(position!=0) {
                    String selected = (String) parent.getItemAtPosition(position);
                    stateSelectedString=(String) parent.getItemAtPosition(position);
                    int pos = spinnerStates.indexOf(selected);
                    MyApplication.stateId=stateIds.get(position);
                    stateSelected=1;
                  /*  CheckoutFragment.billingMap.put("state",stateIds.get(pos).toString());
                    CheckoutFragment.billingMap.put("billing_state",stateIds.get(pos).toString());*/
                    ((TextView) SpStateBillForm.getSelectedView()).setTextColor(ContextCompat.getColor(ProfileActivity.this,R.color.black));
                    spDistBillForm.setAdapter(null);
                    spinDistricts.clear();
                    spinDistricts= new ArrayList<>();
                    getDistrictsbyId(stateIds.get(pos));
                }

                else {
                    ((TextView) SpStateBillForm.getSelectedView()).setTextColor(ContextCompat.getColor(ProfileActivity.this,R.color.darkgrey));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if(stateId>=0) {
            SpStateBillForm.setSelection(stateId);
            //  getDistrictsbyId(Integer.parseInt(mPrefs.getString(Constants.StateId,"")));
        }



    }

    private void getDistrictsbyId(Integer id)
    {
        GeneralFunctions.showDialog(ProfileActivity.this);
        Call<DistrictsModel> call = RestClient.get().getSelectedDistricts(id.toString(),"10000");
        call.enqueue(new Callback<DistrictsModel>() {
            @Override
            public void onResponse(Call<DistrictsModel> call, Response<DistrictsModel> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful())
                {
                    if(response.body().code.equals("401"))
                    {
                        GeneralFunctions.tokenExpireAction(ProfileActivity.this);
                    }
                    else  if(response.body().success==1)
                    {
                        districts= new ArrayList<>();
                        spDistBillForm.setAdapter(null);
                        districts=response.body().data.objects;
                        distSelected=0;
                        setDistricts(fromQr);

                    }
                }



            }

            @Override
            public void onFailure(Call<DistrictsModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(etNameDet,getResources().getString(R.string.netIssue));
            }
        });
    }

    private void setDistricts(int fromQr) {
        int distId=0;
        distIds.add(0);
        spinDistricts.add(getResources().getString(R.string.select));
        for(int i=0;i<districts.size();i++)
        {
            spinDistricts.add(districts.get(i).district_name);
            distIds.add(districts.get(i).districts_id);
            if(mPrefs.getString(Constants.DistId,"").length()>0) {
                if (fromQr == 0) {
                    if (Integer.parseInt(mPrefs.getString(Constants.DistId, "")) == districts.get(i).districts_id)
                        distId = i + 1;
                } else {
                    if (kycExternal.poa.dist != null && spinDistricts.get(i).equals(kycExternal.poa.dist)) {
                        distId = i;
                        fromQr = 0;
                    }
                }
            }

        }
        ArrayAdapter<String> adapters=new ArrayAdapter<String>(ProfileActivity.this, R.layout.spinner_title_text,spinDistricts)
        {
            @Override
            public boolean isEnabled(int position){
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position==0) {
                    // Set the disable item text color
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        adapters.setDropDownViewResource(R.layout.spinner_adapter_value);
        spDistBillForm.setAdapter(adapters);


        spDistBillForm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (view!=null)
                ((TextView)view).setTextColor(ContextCompat.getColor(ProfileActivity.this,R.color.black));

                if(position!=0) {
                    String selected = (String) parent.getItemAtPosition(position);
                    districtSelectedString=(String) parent.getItemAtPosition(position);
                    int pos = spinDistricts.indexOf(selected);
                    distSelected=1;
                    MyApplication.distId=distIds.get(position);

                   /* CheckoutFragment.billingMap.put("district",distIds.get(pos).toString());
                    CheckoutFragment.billingMap.put("billing_district",distIds.get(pos).toString());*/
                    ((TextView) spDistBillForm.getSelectedView()).setTextColor(ContextCompat.getColor(ProfileActivity.this,R.color.black));

                }

                else {
                    ((TextView) spDistBillForm.getSelectedView()).setTextColor(ContextCompat.getColor(ProfileActivity.this,R.color.darkgrey));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if(distId>0)
            spDistBillForm.setSelection(distId);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
          /*  case R.id.ivNameErase:
                etNameDet.setText("");
                etNameDet.requestFocus();
                break;

            case R.id.ivCareOfErase:
                etLastNameDet.setText("");
                etLastNameDet.requestFocus();
                break;

            case R.id.ivPhnErase:
                etPhnDet.setText("");
                etPhnDet.requestFocus();
                break;

            case R.id.ivEntiyNameErase:
                etEntityName.setText("");
                etEntityName.requestFocus();
                break;

            case R.id.ivRepresentativeNameErase:
                etRepresentativeName.setText("");
                etRepresentativeName.requestFocus();
                break;

            case R.id.ivRepresentativeContactNoErase:
                etRepresentativeContactNo.setText("");
                etRepresentativeContactNo.requestFocus();
                break;

            case R.id.ivEmailErase:
                etEmailDet.setText("");
                etEmailDet.requestFocus();
                break;

            case R.id.ivAddDet1Erase:
                etAddDet1.setText("");
                etAddDet1.requestFocus();
                break;

            case R.id.ivAdd2Erase:
                etAdd2.setText("");
                etAdd2.requestFocus();
                break;

            case R.id.ivCityDetErase:
                etCityDet.setText("");
                etCityDet.requestFocus();
                break;

            case R.id.ivZipDetErase:
                etZipDet.setText("");
                etZipDet.requestFocus();
                break;

*/


        }
    }

    private void setEditackGround(EditText editBack)
    {/*
        etRepresentativeName.setBackgroundResource(R.drawable.edittextunfocused);
        etRepresentativeContactNo.setBackgroundResource(R.drawable.edittextunfocused);
        etEntityName.setBackgroundResource(R.drawable.edittextunfocused);
        etNameDet.setBackgroundResource(R.drawable.edittextunfocused);
        etAddDet1.setBackgroundResource(R.drawable.edittextunfocused);
        etAdd2.setBackgroundResource(R.drawable.edittextunfocused);
        etZipDet.setBackgroundResource(R.drawable.edittextunfocused);
        etPhnDet.setBackgroundResource(R.drawable.edittextunfocused);
        etEmailDet.setBackgroundResource(R.drawable.edittextunfocused);
        etCityDet.setBackgroundResource(R.drawable.edittextunfocused);
        etLastNameDet.setBackgroundResource(R.drawable.edittextunfocused);

        editBack.setBackgroundResource(R.drawable.edittectfocused);*/
    }


    @Override
    protected void onResume() {
        super.onResume();
        try {

            if(mPrefs.getString(Constants.qrKyc,"").equals("YES"))
            {
                mPrefs.save(Constants.qrKyc,"NO");
                kycExternal=new Gson().fromJson(mPrefs.getString(Constants.qrData,""),new TypeToken<Kyc>() {}.getType());
                setDatafromQr();
            }
            //setEditListeners();
            setFocusListeners();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void setDatafromQr() {
        String firstname="",lastname="";
        yob="";
        if(kycExternal.poi.name!=null)
        {
            etNameDet.setText(kycExternal.poi.name);

        }
        else
            etNameDet.setText("");

        if(kycExternal.poa.co!=null)
        {
            if(kycExternal.poa.co.length()>5) {
                try {
                    String cOf = "";
                    String cOfStr = "";
                    String ss = kycExternal.poa.co.trim();
                    for (int i = 0; i < ss.length(); i++) {
                        if (ss.charAt(i) == ' ') {
                            cOf = ss.substring(0, i);
                            if(i+1<ss.length())
                                cOfStr = ss.substring(i + 1, ss.length());
                            break;
                        }
                    }
                    if(cOfStr.length() > 0)
                        etLastNameDet.setText(cOfStr);

                    else {
                        etLastNameDet.setText(kycExternal.poa.gname);
                    }

                    //String s=kycExternal.poa.co.substring(0,1);

                    if (cOf.substring(0,1).equalsIgnoreCase("S"))
                        spCareOf.setSelection(mPrefs.getInt(Constants.CareOfPos, 0));

                    else if (cOf.substring(0,1).equalsIgnoreCase("D"))
                        spCareOf.setSelection(mPrefs.getInt(Constants.CareOfPos, 1));

                    else if (cOf.substring(0,1).equalsIgnoreCase("W"))
                        spCareOf.setSelection(mPrefs.getInt(Constants.CareOfPos, 2));

                    else {
                        spCareOf.setSelection(mPrefs.getInt(Constants.CareOfPos, 3));
                    }
                } catch (Exception e) {
                }
            }
            else {

                spCareOf.setSelection(mPrefs.getInt(Constants.CareOfPos, 3));
                etLastNameDet.setText(kycExternal.poa.gname);
            }




        }

        if(kycExternal.poa.vtc!=null)
        {
            etCityDet.setText(kycExternal.poa.vtc );
        }
        else
            etCityDet.setText("");

        if(kycExternal.poi.gender!=null)
        {
            try {
                String s=kycExternal.poi.gender.substring(0,1);
                if(s.equalsIgnoreCase("M"))
                    spSpinNoAadhaar.setSelection(mPrefs.getInt(Constants.GenderPos,0));

                else if(s.equalsIgnoreCase("F"))
                    spSpinNoAadhaar.setSelection(mPrefs.getInt(Constants.GenderPos,1));

                else
                    spSpinNoAadhaar.setSelection(mPrefs.getInt(Constants.GenderPos,2));
            }
            catch (Exception e)
            {

            }

        }

        if(kycExternal.poa.lm!=null)
        {
            if(kycExternal.poa.lc!=null)
                etAddDet1.setText(kycExternal.poa.lm+" "+
                        kycExternal.poa.lc);

            else {
                etAddDet1.setText(kycExternal.poa.lm);
            }
        }
        else if(kycExternal.poa.lc!=null)
        {
            etAddDet1.setText(kycExternal.poa.lc);
        }
        else
            etAddDet1.setText("");

        if(kycExternal.poa.subdist!=null)
        {
            if(kycExternal.poa.subdist.length()>0)
                etCityDet.setText(kycExternal.poa.subdist);
            else
                etCityDet.setText(kycExternal.poa.dist);

        }
        else
            etCityDet.setText("");

        /*if(kycExternal.poi!=null)
        {
            etPhnDet.setText(mPrefs.getString(Constants.Phone,""));
        }*/

       /* if(kycExternal.poa.pc!=null)
        {
            etZipDet.setText(kycExternal.poa.pc);
        }*/

       /* if(mPrefs.getString(Constants.StaticEmail,"")!=null)
        {
            etEmailDet.setText(mPrefs.getString(Constants.StaticEmail,""));
        }*/

       /* if(kycExternal.poi.yob!=null)
        {
            tvDobStatic.setText(kycExternal.poi.yob);
        }*/

        if(kycExternal.poi.dob!=null && ! kycExternal.poi.dob.isEmpty())
        {
            tvDobStatic.setText(kycExternal.poi.dob);
        }
        else
            tvDobStatic.setText("");

        if(kycExternal.poi.dob!=null && kycExternal.poi.dob.isEmpty()  &&
                kycExternal.poi.yob!=null && ! kycExternal.poi.yob.isEmpty())
        {
            yob=kycExternal.poi.yob;
        }
        else
            yob="";

        if(kycExternal.poa.state!=null)
        {
            for(int i=0;i<states.size();i++)
            {
                if(states.get(i).state_name.equalsIgnoreCase(kycExternal.poa.state))
                {
                    //Toast.makeText(this, "State mil gaya"+states.get(i).state_name, Toast.LENGTH_SHORT).show();
                    fromQr=1;
                    SpStateBillForm.setSelection(i+1);
                }
            }
        }


/*
        etRepresentativeContactNo.clearFocus();
        etRepresentativeName.clearFocus();
        etEntityName.clearFocus();
        etNameDet.clearFocus();
        etAddDet1.clearFocus();
        etAdd2.clearFocus();
        etZipDet.clearFocus();
        etPhnDet.clearFocus();
        etEmailDet.clearFocus();
        etCityDet.clearFocus();
        etLastNameDet.clearFocus();
        etEntityName.setBackgroundResource(R.drawable.edittextunfocused);
        etRepresentativeName.setBackgroundResource(R.drawable.edittextunfocused);
        etRepresentativeContactNo.setBackgroundResource(R.drawable.edittextunfocused);
        etNameDet.setBackgroundResource(R.drawable.edittextunfocused);
        etAddDet1.setBackgroundResource(R.drawable.edittextunfocused);
        etAdd2.setBackgroundResource(R.drawable.edittextunfocused);
        etZipDet.setBackgroundResource(R.drawable.edittextunfocused);
        etPhnDet.setBackgroundResource(R.drawable.edittextunfocused);
        etEmailDet.setBackgroundResource(R.drawable.edittextunfocused);
        etCityDet.setBackgroundResource(R.drawable.edittextunfocused);
        etLastNameDet.setBackgroundResource(R.drawable.edittextunfocused);*/

    }

   /* @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case CAMERA_CAPTURE_IMAGE_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent intent = new Intent(ProfileActivity.this, QrReadActivity.class);
                    startActivity(intent);

                } else {
                    // GeneralFunctions.makeSnackbar(etNameDet,getResources().getString(R.string.givepermission));
                    GeneralFunctions.PleaseGrantPermission(etNameDet,this);
                }
                return;
            }
        }
    }*/


}
