package easygov.saral.harlabh.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import easygov.saral.harlabh.models.responsemodels.generalmodel.GeneralModel;
import easygov.saral.harlabh.models.responsemodels.getkyc.GetKycData;
import easygov.saral.harlabh.models.responsemodels.getkyc.GetKycModel;
import easygov.saral.harlabh.models.responsemodels.kycmodel.Kyc;
import easygov.saral.harlabh.models.responsemodels.statesndistricts.DistrictDetails;
import easygov.saral.harlabh.models.responsemodels.statesndistricts.DistrictsModel;
import easygov.saral.harlabh.models.responsemodels.statesndistricts.StatesModel;
import easygov.saral.harlabh.utils.Constants;
import easygov.saral.harlabh.utils.GeneralFunctions;
import easygov.saral.harlabh.utils.MyApplication;
import easygov.saral.harlabh.utils.Prefs;
import easygov.saral.harlabh.utils.RestClient;
import easygov.saral.harlabh.R;
import easygov.saral.harlabh.models.responsemodels.statesndistricts.StateDetails;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;




public class StaticFormActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

    private TextView tvNextForm, tvDobStatic, tvCareOf;
    private List<StateDetails> states;
    private List<DistrictDetails> districts = new ArrayList<>();
    private Spinner spSpinNoAadhaar, SpStateBillForm, spDistBillForm, spCareOf;
    private RelativeLayout llStaticForm, rlStaticForm;
    private Map<String, String> map = new HashMap<>();
    private Prefs mPrefs;
    private ImageView ivFormBack, ivqr;

    private Calendar myCalendar;
    private String yob = "";
    private List<String> spinnerStates = new ArrayList<>();
    private List<Integer> stateIds = new ArrayList<>();
    private List<Integer> distIds = new ArrayList<>();
    private List<String> spinDistricts = new ArrayList<>();

    private String stateSelectedString, districtSelectedString, careof = "";
    public int stateSelected = 0, distSelected = 0, fromQr = 0;
    private Pattern ePat;
    private Kyc kycExternal;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private GetKycData kycData;
    private String prefilDist = "";
    private int onceOnly = 1;
    private EditText etNameDet, etAddDet1, etAdd2, etZipDet, etPhnDet, etEmailDet,
            etCityDet, etLastNameDet, etLandMark, etLocality, etSubDist, etPost;
    private ImageView ivNameErase, ivAddDet1Erase, ivAdd2Erase, ivZipDetErase, ivCityDetErase,
            ivCareOfErase, ivEmailErase, ivPhnErase, ivLandErase, ivLocalErase, ivSubDistErase, ivPostErase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static_form);
        init();

        getStates();

        setCareofSpinner();
    }


    private void setClickHandlers() {
        ivNameErase.setOnClickListener(this);
        ivCareOfErase.setOnClickListener(this);
        ivEmailErase.setOnClickListener(this);
        ivPhnErase.setOnClickListener(this);
        ivAddDet1Erase.setOnClickListener(this);
        ivAdd2Erase.setOnClickListener(this);
        ivZipDetErase.setOnClickListener(this);
        ivCityDetErase.setOnClickListener(this);
        ivLandErase.setOnClickListener(this);
        ivLocalErase.setOnClickListener(this);
        ivSubDistErase.setOnClickListener(this);
        ivPostErase.setOnClickListener(this);

       }

    private void setFocusListeners() {
        etNameDet.setOnFocusChangeListener(this);
        etAddDet1.setOnFocusChangeListener(this);
        etAdd2.setOnFocusChangeListener(this);
        etZipDet.setOnFocusChangeListener(this);
        etPhnDet.setOnFocusChangeListener(this);
        etEmailDet.setOnFocusChangeListener(this);
        etCityDet.setOnFocusChangeListener(this);
        etLastNameDet.setOnFocusChangeListener(this);
        etLandMark.setOnFocusChangeListener(this);
        etLocality.setOnFocusChangeListener(this);
        etSubDist.setOnFocusChangeListener(this);
        etPost.setOnFocusChangeListener(this);
    }

    private void getKyc(String id, final String type) {
        GeneralFunctions.showDialog(StaticFormActivity.this);
        Call<GetKycModel> call = RestClient.get().getKyc(id, type);
        call.enqueue(new Callback<GetKycModel>() {
            @Override
            public void onResponse(Call<GetKycModel> call, Response<GetKycModel> response) {
                GeneralFunctions.dismissDialog();
                if (response.isSuccessful()) {
                    if (response.body().success == 1) {
                        if (response.body().data != null) {
                            kycData = response.body().data;
                            setKycData(response.body().data);
                            if (type.equals("myself"))
                                mPrefs.save(Constants.SelfName, response.body().data.name);
                            //\ else  mPrefs.save(Constants.SelfName,"");
                        }
                    } else {
                        if ((response.body().code) == 404) {
                        } else
                            GeneralFunctions.makeSnackbar(rlStaticForm, response.body().message);
                    }
                } else {
                    GeneralFunctions.makeSnackbar(rlStaticForm, getResources().getString(R.string.serverIssue));
                }
            }

            @Override
            public void onFailure(Call<GetKycModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(rlStaticForm, getResources().getString(R.string.netIssue));
            }
        });
    }




   /* private void setEditListeners() {
        etNameDet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0)
                {ivNameErase.setVisibility(View.VISIBLE);}
                else
                    ivNameErase.setVisibility(View.GONE);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        etLastNameDet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0)
                {
                    ivCareOfErase.setVisibility(View.VISIBLE);
                }
                else
                    ivCareOfErase.setVisibility(View.GONE);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        etPhnDet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0)
                {
                    ivPhnErase.setVisibility(View.VISIBLE);
                }
                else
                    ivPhnErase.setVisibility(View.GONE);
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etEmailDet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0)
                {
                    ivEmailErase.setVisibility(View.VISIBLE);
                }
                else
                    ivEmailErase.setVisibility(View.GONE);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        etAddDet1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0)
                {
                    ivAddDet1Erase.setVisibility(View.VISIBLE);
                }
                else
                    ivAddDet1Erase.setVisibility(View.GONE);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        etAdd2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0)
                {
                    ivAdd2Erase.setVisibility(View.VISIBLE);
                }
                else
                    ivAdd2Erase.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etZipDet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0)
                {
                    ivZipDetErase.setVisibility(View.VISIBLE);
                }
                else
                    ivZipDetErase.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etCityDet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0)
                {
                    ivCityDetErase.setVisibility(View.VISIBLE);
                }
                else
                    ivCityDetErase.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }*/

    private void setCareofSpinner() {
        ArrayList<String> care = new ArrayList<>();
        care.add(" " + getResources().getString(R.string.sonof));
        care.add(" " + getResources().getString(R.string.daughterof));
        care.add(" " + getResources().getString(R.string.wifeof));
        care.add(" " + getResources().getString(R.string.careoff));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_title_text, care);
        adapter.setDropDownViewResource(R.layout.spinner_adapter_value);


        spCareOf.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) parent.getItemAtPosition(position);
                if (position == 0) {
                    tvCareOf.setText(getResources().getString(R.string.sonoff));
                    careof = "S/O: ";
                    mPrefs.save(Constants.CareOf, selected);
                } else if (position == 1) {
                    tvCareOf.setText(getResources().getString(R.string.daughteroff));
                    careof = "D/O: ";
                    mPrefs.save(Constants.CareOf, selected);
                } else if (position == 2) {
                    tvCareOf.setText(getResources().getString(R.string.wifeoff));
                    careof = "W/O: ";
                    mPrefs.save(Constants.CareOf, selected);
                } else if (position == 3) {
                    tvCareOf.setText(getResources().getString(R.string.careoff));
                    careof = "C/O: ";
                    mPrefs.save(Constants.CareOf, selected);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spCareOf.setAdapter(adapter);


    }


    private void setKycData(GetKycData data) {

        if (data.name != null)
            etNameDet.setText(data.name.trim());


        if (data != null)
            etLastNameDet.setText(data.relative_name.trim());

        if (data.house != null)
            etAddDet1.setText(data.house.trim());


        if (data.street != null)
            etAdd2.setText(data.street.trim());


        if (data.lm != null)
            etLandMark.setText(data.lm.trim());


        if (data.loc != null)
            etLocality.setText(data.loc.trim());


        if (data.vtc != null)
            etCityDet.setText(data.vtc.trim());

        if (data.phone != null)
            etPhnDet.setText(data.phone.trim());

        if (data.email != null)
            etEmailDet.setText(data.email.trim());

        if (data.dob != null)
            tvDobStatic.setText(data.dob);


        if (data.co != null && data.co.length() > 0) {
            if (data.co.substring(0, 1).equalsIgnoreCase("S"))
                spCareOf.setSelection(0);

            else if (data.co.substring(0, 1).equalsIgnoreCase("D"))
                spCareOf.setSelection(1);

            else if (data.co.substring(0, 1).equalsIgnoreCase("W"))
                spCareOf.setSelection(2);

            else {
                spCareOf.setSelection(3);
            }
        }

        if (data.subdist != null)
            etSubDist.setText(data.subdist.trim());

        if (data.po != null)
            etPost.setText(data.po.trim());

        if (data.gender != null) {
            if (data.gender.equals(mPrefs.getString(Constants.MaleId, ""))) {
                spSpinNoAadhaar.setSelection(0);
                map.put("gender", mPrefs.getString(Constants.MaleId, ""));
            } else if (data.gender.equals(mPrefs.getString(Constants.FemaleId, ""))) {
                spSpinNoAadhaar.setSelection(1);
                map.put("gender", mPrefs.getString(Constants.FemaleId, ""));

            } else if (data.gender.equals(mPrefs.getString(Constants.TransGenId, ""))) {
                spSpinNoAadhaar.setSelection(2);
                map.put("gender", mPrefs.getString(Constants.FemaleId, ""));

            }
        }

        if (data.state != null && data.state.length() > 1) {
            for (int i = 0; i < states.size(); i++) {
                if (states.get(i).state_name.equalsIgnoreCase(data.state)) {
                    if (data.dist != null && data.dist.length() > 1)
                        prefilDist = data.dist;
                    //Toast.makeText(this, "State mil gaya"+states.get(i).state_name, Toast.LENGTH_SHORT).show();
                    fromQr = 2;
                    SpStateBillForm.setSelection(i + 1);
                }
            }
        }

    }


    private void init() {
        tvNextForm = findViewById(R.id.tvNextForm);
        llStaticForm = findViewById(R.id.llStaticForm);
        rlStaticForm = findViewById(R.id.rlStaticForm);
        mPrefs = Prefs.with(this);
        ivFormBack = findViewById(R.id.ivFormBack);
        ivqr = findViewById(R.id.ivqr);
        etLandMark = findViewById(R.id.etLandMark);
        etLocality = findViewById(R.id.etLocality);
        etSubDist = findViewById(R.id.etSubDist);
        etPost = findViewById(R.id.etPost);
        ivLandErase = findViewById(R.id.ivLandErase);
        ivLocalErase = findViewById(R.id.ivLocalErase);
        ivSubDistErase = findViewById(R.id.ivSubDistErase);
        ivPostErase = findViewById(R.id.ivPostErase);
        spSpinNoAadhaar = findViewById(R.id.spSpinNoAadhaar);
        SpStateBillForm = findViewById(R.id.SpStateBillForm);
        spDistBillForm = findViewById(R.id.spDistBillForm);
        tvCareOf = findViewById(R.id.tvCareOf);
        spCareOf = findViewById(R.id.spCareOf);
        etLastNameDet = findViewById(R.id.etLastNameDet);
        ePat = android.util.Patterns.EMAIL_ADDRESS;
        setCustomDate();
        tvDobStatic = findViewById(R.id.tvDobStatic);
        //map.put("dob",tvDobStatic.getText().toString());
        ivNameErase = findViewById(R.id.ivNameErase);
        ivCareOfErase = findViewById(R.id.ivCareOfErase);
        ivEmailErase = findViewById(R.id.ivEmailErase);
        ivPhnErase = findViewById(R.id.ivPhnErase);
        ivAddDet1Erase = findViewById(R.id.ivAddDet1Erase);
        ivAdd2Erase = findViewById(R.id.ivAdd2Erase);
        ivZipDetErase = findViewById(R.id.ivZipDetErase);
        ivCityDetErase = findViewById(R.id.ivCityDetErase);
        ivqr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(StaticFormActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(StaticFormActivity.this, QrReadActivity.class);
                    // Intent intent = new Intent(StaticFormActivity.this, ReadQrActivity.class);
                    startActivity(intent);
                } else {
                    requestPermissions(
                            new String[]{Manifest.permission.CAMERA},
                            CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
                }
            }
        });

        tvDobStatic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!yob.isEmpty())
                    myCalendar.set(Calendar.YEAR, Integer.parseInt(yob));

                new SpinnerDatePickerDialogBuilder()
                        .context(StaticFormActivity.this)
                        .callback(dates)
                        .build()
                        .show();
            }
        });


        try {
            String s = getIntent().getStringExtra("isqr");
            if (s.equals("yes")) {
                if (ContextCompat.checkSelfPermission(StaticFormActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(StaticFormActivity.this, QrReadActivity.class);
                    startActivity(intent);
                } else {
                    requestPermissions(
                            new String[]{Manifest.permission.CAMERA},
                            CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
                }
            }
        } catch (Exception e) {
        }

        llStaticForm.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                GeneralFunctions.hideSoftKeyboard(StaticFormActivity.this);
                return false;
            }
        });

        ivFormBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPrefs.save(Constants.FromStaticBack, "yes");
                mPrefs.save(Constants.FormFilled, "NO");
                finish();

            }
        });
        final ArrayList<String> gender = new ArrayList<>();
        gender.add("   " + getResources().getString(R.string.male));
        gender.add("   " + getResources().getString(R.string.femlae));
        gender.add("   " + getResources().getString(R.string.transgender));
        map.put("gender", mPrefs.getString(Constants.MaleId, ""));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_title_text, gender);
        adapter.setDropDownViewResource(R.layout.spinner_adapter_value);


        spSpinNoAadhaar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0)
                    map.put("gender", mPrefs.getString(Constants.MaleId, ""));

                else if (position == 1)
                    map.put("gender", mPrefs.getString(Constants.FemaleId, ""));

                else if (position == 2)
                    map.put("gender", mPrefs.getString(Constants.TransGenId, ""));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spSpinNoAadhaar.setAdapter(adapter);
        etNameDet = findViewById(R.id.etNameDet);
        etAddDet1 = findViewById(R.id.etAddDet1);
        etAdd2 = findViewById(R.id.etAdd2);
        etPhnDet = findViewById(R.id.etPhnDet);
        etEmailDet = findViewById(R.id.etEmailDet);
        etCityDet = findViewById(R.id.etCityDet);
        etZipDet = findViewById(R.id.etZipDet);


        //todo:Tester Mode Live Check
       //MyApplication.Tester=1;
        tvNextForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidate()) {
                    //getSurveyId();
                    map.put("relation_type", mPrefs.getString(Constants.Applyingfor, ""));

                   if (MyApplication.Tester == 0) {
                        if (mPrefs.getBoolean(Constants.IS_AADHAR_AUTHENTICATED, false)) {
                            if (kycData.name != null && !kycData.name.equalsIgnoreCase(etNameDet.getText().toString().trim()))
                                mPrefs.save(Constants.IS_KYC_DATA_CHANGED, true);

                            else if (kycData.relative_name != null && !kycData.relative_name.equalsIgnoreCase(etLastNameDet.getText().toString().trim()))
                                mPrefs.save(Constants.IS_KYC_DATA_CHANGED, true);

                            else if (kycData.dob != null && !kycData.dob.equals(tvDobStatic.getText().toString().trim()))
                                mPrefs.save(Constants.IS_KYC_DATA_CHANGED, true);


                            else if (kycData.house != null && !kycData.house.equals(etAddDet1.getText().toString().trim()))
                                mPrefs.save(Constants.IS_KYC_DATA_CHANGED, true);

                            else if (kycData.street != null && !kycData.street.equalsIgnoreCase(etAdd2.getText().toString().trim()))
                                mPrefs.save(Constants.IS_KYC_DATA_CHANGED, true);

                            else if (kycData.loc != null && !kycData.loc.equalsIgnoreCase(etLocality.getText().toString().trim()))
                                mPrefs.save(Constants.IS_KYC_DATA_CHANGED, true);

                            else if (kycData.lm != null && !kycData.lm.equalsIgnoreCase(etLandMark.getText().toString().trim()))
                                mPrefs.save(Constants.IS_KYC_DATA_CHANGED, true);

                            else if (kycData.vtc != null && !kycData.vtc.equalsIgnoreCase(etCityDet.getText().toString().trim()))
                                mPrefs.save(Constants.IS_KYC_DATA_CHANGED, true);


                            else if (kycData.subdist != null && !kycData.subdist.equalsIgnoreCase(etSubDist.getText().toString().trim()))
                                mPrefs.save(Constants.IS_KYC_DATA_CHANGED, true);

                            else if (kycData.state != null && !kycData.state.equalsIgnoreCase(stateSelectedString))
                                mPrefs.save(Constants.IS_KYC_DATA_CHANGED, true);

                            else if (kycData.dist != null && !kycData.dist.equalsIgnoreCase(mPrefs.getString(Constants.DistrictSelectedStart, "")))
                                mPrefs.save(Constants.IS_KYC_DATA_CHANGED, true);

                            else if (kycData.po != null && !kycData.po.equalsIgnoreCase(etPost.getText().toString().trim()))
                                mPrefs.save(Constants.IS_KYC_DATA_CHANGED, true);

                            else if (kycData.gender != null && !map.get("gender").toString().equalsIgnoreCase(kycData.gender))
                                mPrefs.save(Constants.IS_KYC_DATA_CHANGED, true);

                            else
                                mPrefs.save(Constants.IS_KYC_DATA_CHANGED, false);
                        }
                        hitKycData();
                    } else {
                        hitKycData();
                    }
                }
            }

        });

    }


    com.tsongkha.spinnerdatepicker.DatePickerDialog.OnDateSetListener dates;

    private void setCustomDate() {
        myCalendar = Calendar.getInstance();
        dates = new com.tsongkha.spinnerdatepicker.DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(com.tsongkha.spinnerdatepicker.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String s = year + "-" + monthOfYear + "-" + dayOfMonth;
                yob = "";
                mPrefs.save("date_of_survey_birth", s);
                updateLabel(year, monthOfYear + 1, dayOfMonth);
            }
        };


    }

    private boolean checkValidate() {

        map.put("survey_id", mPrefs.getString(Constants.SurveyId, ""));

        /*if(mPrefs.getString(Constants.FamilyMember,"").equals("yes"))
        {
            if(familyrelation.toString().isEmpty())
            {
                GeneralFunctions.makeSnackbar(rlStaticForm,getResources().getString(R.string.plenter)+getResources().getString(R.string.familymemberques));
            }

            else  map.put("relation_with_applicant",familyrelation);
        }*/
        if (!etNameDet.getText().toString().trim().isEmpty()) {
            map.put("name", etNameDet.getText().toString());
            // firstname=etNameDet.getText().toString();
            mPrefs.save(Constants.FirstName, etNameDet.getText().toString());

            // StaticSurveyPrefsModel sspm = new StaticSurveyPrefsModel();

            // sspm.FirstName=etNameDet.getText().toString();

            // mPrefs.save("StaticSurveyPrefsModel",new Gson().toJson(sspm));
        } else {
            GeneralFunctions.makeSnackbar(rlStaticForm, getResources().getString(R.string.plentername));
            return false;
        }

        if (!etLastNameDet.getText().toString().trim().isEmpty()) {
            map.put("co", careof + etLastNameDet.getText().toString());
            //lastname=etLastNameDet.getText().toString();
            mPrefs.save(Constants.LastName, etLastNameDet.getText().toString());


        } else {
            GeneralFunctions.makeSnackbar(rlStaticForm, getResources().getString(R.string.plcareof));
            return false;
        }


        if (!etPhnDet.getText().toString().trim().isEmpty()) {
            map.put("phone", etPhnDet.getText().toString());
            // phone=etPhnDet.getText().toString();
            mPrefs.save(Constants.Phone, etPhnDet.getText().toString());
        } else {
            GeneralFunctions.makeSnackbar(rlStaticForm, getResources().getString(R.string.plphone));
            return false;
        }


        mPrefs.save(Constants.Street1, etAddDet1.getText().toString() + " " + etAdd2.getText().toString() + " " + " " + etLocality.getText().toString());
        mPrefs.save(Constants.Street2, etCityDet.getText().toString());
       /* if(!etAddDet1.getText().toString().trim().isEmpty())
        {
            map.put("house",etAddDet1.getText().toString());
            //street1=etAddDet1.getText().toString();
            mPrefs.save(Constants.Street1,etAddDet1.getText().toString());
        }
        else {
            GeneralFunctions.makeSnackbar(rlStaticForm,getResources().getString(R.string.house));
            return false;
        }

        if(!etAdd2.getText().toString().trim().isEmpty())
        {
            map.put("street",etAdd2.getText().toString());
            // street2=etAdd2.getText().toString();
            mPrefs.save(Constants.Street2,etAdd2.getText().toString());
        }
        else {
            GeneralFunctions.makeSnackbar(rlStaticForm,getResources().getString(R.string.plenter)+" "+getResources().getString(R.string.street));
            return false;
        }*/
        if (!etCityDet.getText().toString().trim().isEmpty()) {
            map.put("vtc", etCityDet.getText().toString());
            //city=etCityDet.getText().toString();
            mPrefs.save(Constants.City, etCityDet.getText().toString());
        } else {
            GeneralFunctions.makeSnackbar(rlStaticForm, getResources().getString(R.string.plenter) + " " + getResources().getString(R.string.vtc));
            return false;
        }

        if (stateSelected > 0) {
            map.put("state", stateSelectedString);
            mPrefs.save(Constants.Declarationstate, stateSelectedString);
        } else {
            GeneralFunctions.makeSnackbar(rlStaticForm, getResources().getString(R.string.plstate));
            return false;
        }

        if (distSelected > 0) {
            map.put("dist", districtSelectedString);
            mPrefs.save(Constants.Declarationdist, districtSelectedString);
        } else {
            GeneralFunctions.makeSnackbar(rlStaticForm, getResources().getString(R.string.pldist));
            return false;
        }


        if (!etEmailDet.getText().toString().trim().isEmpty()) {
            if (ePat.matcher(etEmailDet.getText().toString()).matches()) {
                map.put("email", etEmailDet.getText().toString());
                mPrefs.save(Constants.StaticEmail, etEmailDet.getText().toString());
            } else {
                GeneralFunctions.makeSnackbar(rlStaticForm, getResources().getString(R.string.plemail));
                return false;
            }
        }


        if (tvDobStatic.getText().toString().isEmpty()) {
            GeneralFunctions.makeSnackbar(rlStaticForm, getResources().getString(R.string.pldob));

            return false;
            //map.put("dob",etDobDet.getText().toString());
        } else {
            map.put("dob", tvDobStatic.getText().toString());
            mPrefs.save(Constants.StaticDob, tvDobStatic.getText().toString());
        }



        /*if(!etZipDet.getText().toString().trim().isEmpty())
        {
            map.put("pc",etZipDet.getText().toString());
            //zip=etZipDet.getText().toString();
            mPrefs.save(Constants.Zip,etZipDet.getText().toString());
      }
        else {
            GeneralFunctions.makeSnackbar(rlStaticForm,getResources().getString(R.string.plzip));
            return false;
        }*/


        if (etAddDet1.getText().toString() != null)
            map.put("house", etAddDet1.getText().toString());

        if (etLandMark.getText().toString() != null)
            map.put("lm", etLandMark.getText().toString().trim());


        if (etLocality.getText().toString() != null)
            map.put("loc", etLocality.getText().toString().trim());


        if (etSubDist.getText().toString() != null)
            map.put("subdist", etSubDist.getText().toString().trim());

        if (etPost.getText().toString() != null)
            map.put("po", etPost.getText().toString().trim());


        return true;
    }

    private void hitKycData() {
        GeneralFunctions.showDialog(this);

        if (mPrefs.getString(Constants.Applyingfor, "").equals("someoneelse") || mPrefs.getString(Constants.Applyingfor, "").equals("family")) {
            map.put("profile_id", mPrefs.getString(Constants.ProfileId, ""));
            if (mPrefs.getString(Constants.Applyingfor, "").equals("someoneelse") && mPrefs.getString(Constants.SomeoneAdded, "").equals("yes"))
                mPrefs.save(Constants.FromSomeoneElseGo, "yes");
        } else {
            mPrefs.save(Constants.SurveyFilled,"yes");
            map.put("profile_id", "");
        }

        Call<GeneralModel> call = RestClient.get().savekycData(map);
        call.enqueue(new Callback<GeneralModel>() {
            @Override
            public void onResponse(Call<GeneralModel> call, Response<GeneralModel> response) {
                GeneralFunctions.dismissDialog();
                if (response.isSuccessful()) {

                    if(response.body().code.equals("401"))
                    {
                        GeneralFunctions.tokenExpireAction(StaticFormActivity.this);
                    }

                    else if (response.body().success == 1) {
                        mPrefs.save(Constants.FormFilled, "YES");
                        mPrefs.save(Constants.FromOtpCanceled, "NO");
                        if (mPrefs.getString(Constants.Applyingfor, "").equals("myself"))
                            mPrefs.save(Constants.SelfName, etNameDet.getText().toString());
                        Intent intent = new Intent(StaticFormActivity.this, FourthApplicationActivity.class);
                        startActivity(intent);
                        ThirdEligibilityActivity.counts = 0;
                    } else {

                        GeneralFunctions.makeSnackbar(rlStaticForm, response.body().message);
                    }
                } else {
                    GeneralFunctions.makeSnackbar(rlStaticForm, getResources().getString(R.string.serverIssue));
                }
            }

            @Override
            public void onFailure(Call<GeneralModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(rlStaticForm, getResources().getString(R.string.netIssue));
            }
        });
    }


    private void updateLabel(int year, int i, int dayOfMonth) {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);


        SimpleDateFormat myFormats = new SimpleDateFormat("dd MM yyyy");

        String firstDate = currentDay + " " + currentMonth + " " + currentYear;
        String secondDate = dayOfMonth + " " + i + " " + year;
        if (MyApplication.compareDate(Calendar.getInstance().getTime(), myCalendar.getTime())) {
            // tvDobStatic.setText(sdf.format(myCalendar.getTime()));
            tvDobStatic.setText(dayOfMonth + "/" + i + "/" + year);
            map.put("dob", tvDobStatic.getText().toString());
            mPrefs.save(Constants.StaticDob, tvDobStatic.getText().toString());
        } else {
            GeneralFunctions.makeSnackbar(rlStaticForm, getResources().getString(R.string.validDate));
        }
    }


    private void getStates() {
        //  GeneralFunctions.showDialog(this);
        Call<StatesModel> call = RestClient.get().getAllStates("1000");
        call.enqueue(new Callback<StatesModel>() {
            @Override
            public void onResponse(Call<StatesModel> call, Response<StatesModel> response) {
                // GeneralFunctions.dismissDialog();
                if (response.isSuccessful()) {
                    if(response.body().code.equals("401"))
                    {
                        GeneralFunctions.tokenExpireAction(StaticFormActivity.this);
                    }
                    else  if (response.body().success == 1) {

                        states = response.body().data.objects;
                        setStates();
                        //statesObj = response.body();
                        //rlStates.setAdapter(new StatesAdapter(StateSelectActivity.this, statesObj.data.objects));
/*
                        DbHelper db=new DbHelper(StateSelectActivity.this);
                        db.addStates(statesObj.data.objects);*/
                        //getAllDistricts();
                    } else {
                        GeneralFunctions.makeSnackbar(rlStaticForm, response.body().message);
                    }

                } else {
                    GeneralFunctions.makeSnackbar(rlStaticForm, getResources().getString(R.string.serverIssue));
                }
            }


            @Override
            public void onFailure(Call<StatesModel> call, Throwable t) {
                //  GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(rlStaticForm, getResources().getString(R.string.netIssue));
            }
        });
    }

    private void setStates() {
        int stateId = 0;
        stateIds.add(0);
        spinnerStates.add(getResources().getString(R.string.select));
        //  if(Integer.parseInt(mPrefs.getString(Constants.StateId,""))>0)
        //SpStateBillForm.setSelection(mPrefs.getInt(Constants.StateId,0));
        for (int i = 0; i < states.size(); i++) {
            spinnerStates.add(states.get(i).state_name);
            stateIds.add(states.get(i).states_id);
            if (mPrefs.getString(Constants.StateId, "").length() > 0)
                if (Integer.parseInt(mPrefs.getString(Constants.StateId, "")) == states.get(i).states_id)
                    stateId = i + 1;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(StaticFormActivity.this, R.layout.spinner_title_text, spinnerStates) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the disable item text color
                    tv.setTextColor(Color.GRAY);
                } else {
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
                if (position != 0) {
                    previousSelected = position;
                    String selected = (String) parent.getItemAtPosition(position);
                    stateSelectedString = (String) parent.getItemAtPosition(position);
                    int pos = spinnerStates.indexOf(selected);
                    MyApplication.stateId = stateIds.get(position);
                    stateSelected = 1;
                  /*  CheckoutFragment.billingMap.put("state",stateIds.get(pos).toString());
                    CheckoutFragment.billingMap.put("billing_state",stateIds.get(pos).toString());*/
                    ((TextView) SpStateBillForm.getSelectedView()).setTextColor(ContextCompat.getColor(StaticFormActivity.this, R.color.black));
                    spDistBillForm.setAdapter(null);
                    spinDistricts.clear();
                    spinDistricts = new ArrayList<String>();
                    getDistrictsbyId(stateIds.get(pos));
                } else {
                    ((TextView) SpStateBillForm.getSelectedView()).setTextColor(ContextCompat.getColor(StaticFormActivity.this, R.color.darkgrey));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (stateId >= 0) {
            SpStateBillForm.setSelection(stateId);
            //  getDistrictsbyId(Integer.parseInt(mPrefs.getString(Constants.StateId,"")));
        }


    }

    private void getDistrictsbyId(Integer id) {
        //GeneralFunctions.showDialog(StaticFormActivity.this);
        Call<DistrictsModel> call = RestClient.get().getSelectedDistricts(id.toString(), "10000");
        call.enqueue(new Callback<DistrictsModel>() {
            @Override
            public void onResponse(Call<DistrictsModel> call, Response<DistrictsModel> response) {
                GeneralFunctions.dismissDialog();
                if (response.isSuccessful()) {
                    if(response.body().code.equals("401"))
                    {
                        GeneralFunctions.tokenExpireAction(StaticFormActivity.this);
                    }
                    else if (response.body().success == 1) {
                        districts = new ArrayList<DistrictDetails>();
                        spDistBillForm.setAdapter(null);
                        districts = response.body().data.objects;
                        distSelected = 0;
                        setDistricts(fromQr);

                    } else {
                        GeneralFunctions.makeSnackbar(rlStaticForm, response.body().message);
                    }
                } else {
                    GeneralFunctions.makeSnackbar(rlStaticForm, getResources().getString(R.string.serverIssue));
                }


            }

            @Override
            public void onFailure(Call<DistrictsModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(rlStaticForm, getResources().getString(R.string.netIssue));

            }
        });
    }

    private void setDistricts(int fromQr) {
        int distId = 0;
        distIds.add(0);
        spinDistricts = new ArrayList<>();

        spinDistricts.add(getResources().getString(R.string.select));
        for (int i = 0; i < districts.size(); i++) {
            spinDistricts.add(districts.get(i).district_name);
            distIds.add(districts.get(i).districts_id);
            if (mPrefs.getString(Constants.DistId, "").length() > 0) {
                if (fromQr == 0) {
                    if (Integer.parseInt(mPrefs.getString(Constants.DistId, "")) == districts.get(i).districts_id)
                        distId = i + 1;
                } else if (fromQr == 1) {
                    if (kycExternal.poa.dist != null && districts.get(i).district_name.equalsIgnoreCase(kycExternal.poa.dist)) {
                        distId = i + 1;
                        fromQr = 0;
                    }
                } else if (fromQr == 2) {
                    if (prefilDist != null && districts.get(i).district_name.equalsIgnoreCase(prefilDist)) {
                        distId = i + 1;
                        fromQr = 0;
                    }
                }
            }

        }
        ArrayAdapter<String> adapters = new ArrayAdapter<String>(StaticFormActivity.this, R.layout.spinner_title_text, spinDistricts) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the disable item text color
                    tv.setTextColor(Color.GRAY);
                } else {
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
                if (position != 0) {
                    String selected = (String) parent.getItemAtPosition(position);
                    districtSelectedString = (String) parent.getItemAtPosition(position);
                    int pos = spinDistricts.indexOf(selected);
                    distSelected = 1;
                    MyApplication.distId = distIds.get(position);

                   /* CheckoutFragment.billingMap.put("district",distIds.get(pos).toString());
                    CheckoutFragment.billingMap.put("billing_district",distIds.get(pos).toString());*/
                    try {
                        ((TextView) spDistBillForm.getSelectedView()).setTextColor(ContextCompat.getColor(StaticFormActivity.this, R.color.black));

                    } catch (Exception e) {
                    }

                } else {
                    try {
                        ((TextView) spDistBillForm.getSelectedView()).setTextColor(ContextCompat.getColor(StaticFormActivity.this, R.color.darkgrey));

                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if (distId > 0)
            spDistBillForm.setSelection(distId);


        if (onceOnly == 1) {
            onceOnly = 0;
            if (mPrefs.getString(Constants.IS_USER_FIRST_TIME, "").equals("yes")) {
                mPrefs.save(Constants.IS_USER_FIRST_TIME, "no");
                if(mPrefs.getString(Constants.GenerateSurveyId,"").equalsIgnoreCase("yes")||mPrefs.getString(Constants.GenerateSurveyId,"").equalsIgnoreCase(""))
                getSurveyId("myself");

                else {
                    getKyc("", "myself");
                }
            } else
                getKyc(mPrefs.getString(Constants.ProfileId, ""), mPrefs.getString(Constants.Applyingfor, ""));

        }

    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.ivNameErase) {
            etNameDet.setText("");
            etNameDet.requestFocus();

        } else if (i == R.id.ivCareOfErase) {
            etLastNameDet.setText("");
            etLastNameDet.requestFocus();

        } else if (i == R.id.ivPhnErase) {
            etPhnDet.setText("");
            etPhnDet.requestFocus();

        } else if (i == R.id.ivEmailErase) {
            etEmailDet.setText("");
            etEmailDet.requestFocus();

        } else if (i == R.id.ivAddDet1Erase) {
            etAddDet1.setText("");
            etAddDet1.requestFocus();

        } else if (i == R.id.ivAdd2Erase) {
            etAdd2.setText("");
            etAdd2.requestFocus();

        } else if (i == R.id.ivCityDetErase) {
            etCityDet.setText("");
            etCityDet.requestFocus();

        } else if (i == R.id.ivZipDetErase) {
            etZipDet.setText("");
            etZipDet.requestFocus();

        } else if (i == R.id.ivLandErase) {
            etLandMark.setText("");
            etLandMark.requestFocus();

        } else if (i == R.id.ivLocalErase) {
            etLocality.setText("");
            etLocality.requestFocus();

        } else if (i == R.id.ivSubDistErase) {
            etSubDist.setText("");
            etSubDist.requestFocus();

        } else if (i == R.id.ivPostErase) {
            etPost.setText("");
            etPost.requestFocus();

        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int i = v.getId();
        if (i == R.id.etNameDet) {
            if (hasFocus) {

                setEditackGround(etNameDet);
                if (etNameDet.getText().toString().length() > 0)
                    ivNameErase.setVisibility(View.VISIBLE);
            } else ivNameErase.setVisibility(View.GONE);


        } else if (i == R.id.etAddDet1) {
            if (hasFocus) {
                setEditackGround(etAddDet1);
                if (etAddDet1.getText().toString().length() > 0)
                    ivAddDet1Erase.setVisibility(View.VISIBLE);
            } else ivAddDet1Erase.setVisibility(View.GONE);

        } else if (i == R.id.etAdd2) {
            if (hasFocus) {
                setEditackGround(etAdd2);
                if (etAdd2.getText().toString().length() > 0)
                    ivAdd2Erase.setVisibility(View.VISIBLE);
            } else ivAdd2Erase.setVisibility(View.GONE);

        } else if (i == R.id.etZipDet) {
            if (hasFocus) {
                setEditackGround(etZipDet);
                if (etZipDet.getText().toString().length() > 0)
                    ivZipDetErase.setVisibility(View.VISIBLE);

            } else
                ivZipDetErase.setVisibility(View.GONE);

        } else if (i == R.id.etPhnDet) {
            if (hasFocus) {
                setEditackGround(etPhnDet);
                if (etPhnDet.getText().toString().length() > 0)
                    ivPhnErase.setVisibility(View.VISIBLE);
            } else
                ivPhnErase.setVisibility(View.GONE);

        } else if (i == R.id.etEmailDet) {
            if (hasFocus) {
                setEditackGround(etEmailDet);
                if (etEmailDet.getText().toString().length() > 0)
                    ivEmailErase.setVisibility(View.VISIBLE);
            } else ivEmailErase.setVisibility(View.GONE);

        } else if (i == R.id.etCityDet) {
            if (hasFocus) {
                setEditackGround(etCityDet);
                if (etCityDet.getText().toString().length() > 0)
                    ivCityDetErase.setVisibility(View.VISIBLE);
            } else ivCityDetErase.setVisibility(View.GONE);

        } else if (i == R.id.etLastNameDet) {
            if (hasFocus) {
                setEditackGround(etLastNameDet);
                if (etLastNameDet.getText().toString().length() > 0)
                    ivCareOfErase.setVisibility(View.VISIBLE);

            } else
                ivCareOfErase.setVisibility(View.GONE);

        } else if (i == R.id.etLandMark) {
            if (hasFocus) {
                setEditackGround(etLandMark);
                if (etLandMark.getText().toString().length() > 0)
                    ivLandErase.setVisibility(View.VISIBLE);
            } else {
                ivLandErase.setVisibility(View.GONE);
            }


        } else if (i == R.id.etLocality) {
            if (hasFocus) {
                setEditackGround(etLocality);
                if (etLocality.getText().toString().length() > 0)
                    ivLocalErase.setVisibility(View.VISIBLE);
            } else {
                ivLocalErase.setVisibility(View.GONE);
            }


        } else if (i == R.id.etSubDist) {
            if (hasFocus) {
                setEditackGround(etSubDist);
                if (etSubDist.getText().toString().length() > 0)
                    ivSubDistErase.setVisibility(View.VISIBLE);
            } else {
                ivSubDistErase.setVisibility(View.GONE);
            }


        } else if (i == R.id.etPost) {
            if (hasFocus) {
                setEditackGround(etPost);
                if (etPost.getText().toString().length() > 0)
                    ivPostErase.setVisibility(View.VISIBLE);
            } else {
                ivPostErase.setVisibility(View.VISIBLE);
            }


        }
    }

    private void setEditackGround(EditText editBack) {
        etNameDet.setBackgroundResource(R.drawable.edittextunfocused);
        etAddDet1.setBackgroundResource(R.drawable.edittextunfocused);
        etAdd2.setBackgroundResource(R.drawable.edittextunfocused);
        etZipDet.setBackgroundResource(R.drawable.edittextunfocused);
        etPhnDet.setBackgroundResource(R.drawable.edittextunfocused);
        etEmailDet.setBackgroundResource(R.drawable.edittextunfocused);
        etCityDet.setBackgroundResource(R.drawable.edittextunfocused);
        etLastNameDet.setBackgroundResource(R.drawable.edittextunfocused);

        etPost.setBackgroundResource(R.drawable.edittextunfocused);
        etLandMark.setBackgroundResource(R.drawable.edittextunfocused);
        etLocality.setBackgroundResource(R.drawable.edittextunfocused);
        etSubDist.setBackgroundResource(R.drawable.edittextunfocused);
        editBack.setBackgroundResource(R.drawable.edittectfocused);
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (mPrefs.getString(Constants.qrKyc, "").equals("YES")) {
            mPrefs.save(Constants.qrKyc, "NO");
            kycExternal = new Gson().fromJson(mPrefs.getString(Constants.qrData, ""), new TypeToken<Kyc>() {
            }.getType());
            if (kycExternal.poa.dist != null && kycExternal.poa.dist.length() > 1)
                prefilDist = kycExternal.poa.dist;
            setDatafromQr();
        }
        //setEditListeners();

        setFocusListeners();
        setClickHandlers();
    }



    private int previousSelected = 0;

    private void setDatafromQr() {
        String firstname = "", lastname = "";
        yob = "";
        if (kycExternal.poi.name != null) {
            etNameDet.setText(kycExternal.poi.name);

        } else {
            etNameDet.setText("");
        }

        if (kycExternal.poa.co != null) {
            if (kycExternal.poa.co.length() > 5) {
                try {
                    String cOf = "";
                    String cOfStr = "";
                    String ss = kycExternal.poa.co.trim();
                    for (int i = 0; i < ss.length(); i++) {
                        if (ss.charAt(i) == ' ') {
                            cOf = ss.substring(0, i);
                            if (i + 1 < ss.length())
                                cOfStr = ss.substring(i + 1, ss.length());
                            break;
                        }
                    }
                    if (cOfStr != null && cOfStr.length() > 0)
                        etLastNameDet.setText(cOfStr);

                    else if (kycExternal.poa.gname != null && kycExternal.poa.gname.length() > 0) {
                        etLastNameDet.setText(kycExternal.poa.gname);
                    } else {
                        etLastNameDet.setText("");
                    }

                    //String s=kycExternal.poa.co.substring(0,1);

                    if (cOf.substring(0, 1).equalsIgnoreCase("S"))
                        spCareOf.setSelection(0);

                    else if (cOf.substring(0, 1).equalsIgnoreCase("D"))
                        spCareOf.setSelection(1);

                    else if (cOf.substring(0, 1).equalsIgnoreCase("W"))
                        spCareOf.setSelection(2);

                    else {
                        spCareOf.setSelection(3);
                    }
                } catch (Exception e) {
                }
            } else {

                spCareOf.setSelection(3);

                if (kycExternal.poa.gname != null && kycExternal.poa.gname.length() > 0)
                    etLastNameDet.setText(kycExternal.poa.gname);

                else etLastNameDet.setText("");
            }


        }

        if (kycExternal.poa.vtc != null) {
            etAdd2.setText(kycExternal.poa.vtc + " - " + kycExternal.poa.pc);
            // etCityDet.setText(kycExternal.poa.vtc );
        } else {
            etAdd2.setText("");
        }

        if (kycExternal.poi.gender != null) {
            try {
                String s = kycExternal.poi.gender.substring(0, 1);
                if (s.equalsIgnoreCase("M"))
                    spSpinNoAadhaar.setSelection(0);

                else if (s.equalsIgnoreCase("F"))
                    spSpinNoAadhaar.setSelection(1);

                else
                    spSpinNoAadhaar.setSelection(2);
            } catch (Exception e) {

            }

        }

        if (kycExternal.poa.lm != null && kycExternal.poa.lm.length() > 0) {
            etLandMark.setText(kycExternal.poa.lm);
        } else {
            etLandMark.setText("");
        }

        if (kycExternal.poa.lc != null && kycExternal.poa.lc.length() > 0) {
            etLocality.setText(kycExternal.poa.lm);
        } else {
            etLocality.setText("");
        }


        if (kycExternal.poa.lm != null) {
            if (kycExternal.poa.lc != null)
                etAddDet1.setText(kycExternal.poa.lm + " " +
                        kycExternal.poa.lc);

            else {
                etAddDet1.setText(kycExternal.poa.lm);
            }
        } else if (kycExternal.poa.lc != null) {
            etAddDet1.setText(kycExternal.poa.lc);
        } else {
            etAddDet1.setText(kycExternal.poa.lc);
        }

        if (kycExternal.poa.subdist != null) {
            if (kycExternal.poa.subdist.length() > 0)
                etCityDet.setText(kycExternal.poa.subdist);
            else etCityDet.setText(kycExternal.poa.dist);

        }
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

        if (kycExternal.poi.dob != null && !kycExternal.poi.dob.isEmpty()) {
            tvDobStatic.setText(kycExternal.poi.dob);
        } else
            tvDobStatic.setText("");

        if (kycExternal.poi.dob != null && kycExternal.poi.dob.isEmpty() &&
                kycExternal.poi.yob != null && !kycExternal.poi.yob.isEmpty()) {
            yob = kycExternal.poi.yob;
        } else
            yob = "";

        if (kycExternal.poa.state != null) {
            for (int i = 0; i < states.size(); i++) {
                if (states.get(i).state_name.equalsIgnoreCase(kycExternal.poa.state)) {
                    fromQr = 1;


                    if (previousSelected == i) {
                        stateSelected = 1;
                  /*  CheckoutFragment.billingMap.put("state",stateIds.get(pos).toString());
                    CheckoutFragment.billingMap.put("billing_state",stateIds.get(pos).toString());*/
                        ((TextView) SpStateBillForm.getSelectedView()).setTextColor(ContextCompat.getColor(StaticFormActivity.this, R.color.black));
                        spDistBillForm.setAdapter(null);
                        spinDistricts.clear();
                        spinDistricts = new ArrayList<String>();
                        getDistrictsbyId(states.get(i).states_id);
                    } else {
                        previousSelected = i;
                        SpStateBillForm.setSelection(i + 1);
                    }
                }
            }
        }


        etNameDet.clearFocus();
        etAddDet1.clearFocus();
        etAdd2.clearFocus();
        etZipDet.clearFocus();
        etPhnDet.clearFocus();
        etEmailDet.clearFocus();
        etCityDet.clearFocus();
        etLastNameDet.clearFocus();

        etLandMark.clearFocus();
        etLocality.clearFocus();
        etSubDist.clearFocus();
        etPost.clearFocus();


        etLandMark.setBackgroundResource(R.drawable.edittextunfocused);
        etLocality.setBackgroundResource(R.drawable.edittextunfocused);
        etSubDist.setBackgroundResource(R.drawable.edittextunfocused);
        etPost.setBackgroundResource(R.drawable.edittextunfocused);
        etNameDet.setBackgroundResource(R.drawable.edittextunfocused);
        etAddDet1.setBackgroundResource(R.drawable.edittextunfocused);
        etAdd2.setBackgroundResource(R.drawable.edittextunfocused);
        etZipDet.setBackgroundResource(R.drawable.edittextunfocused);
        etPhnDet.setBackgroundResource(R.drawable.edittextunfocused);
        etEmailDet.setBackgroundResource(R.drawable.edittextunfocused);
        etCityDet.setBackgroundResource(R.drawable.edittextunfocused);
        etLastNameDet.setBackgroundResource(R.drawable.edittextunfocused);
        ivNameErase.setVisibility(View.GONE);
        ivAddDet1Erase.setVisibility(View.GONE);
        ivAdd2Erase.setVisibility(View.GONE);
        ivZipDetErase.setVisibility(View.GONE);
        ivCityDetErase.setVisibility(View.GONE);
        ivCareOfErase.setVisibility(View.GONE);
        ivEmailErase.setVisibility(View.GONE);
        ivPhnErase.setVisibility(View.GONE);
        ivLandErase.setVisibility(View.GONE);
        ivLocalErase.setVisibility(View.GONE);
        ivSubDistErase.setVisibility(View.GONE);
        ivPostErase.setVisibility(View.GONE);


    }


    //Todo: create camera permission if qr opens up
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_CAPTURE_IMAGE_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent intent = new Intent(StaticFormActivity.this, QrReadActivity.class);
                    startActivity(intent);

                } else {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED)
                        GeneralFunctions.makeSnackbar(rlStaticForm, getResources().getString(R.string.plPermission));
                    GeneralFunctions.PleaseGrantPermission(rlStaticForm, this);


                }
                return;
            }
        }
    }


    private void getSurveyId(final String myself) {
        mPrefs.remove(Constants.UserServiceID);
        GeneralFunctions.showDialog(StaticFormActivity.this);
        Call<GeneralModel> call = RestClient.get().getSurveyId(myself, mPrefs.getString(Constants.BeneficiaryID, ""));
        call.enqueue(new Callback<GeneralModel>() {
            @Override
            public void onResponse(Call<GeneralModel> call, Response<GeneralModel> response) {
                GeneralFunctions.dismissDialog();

                if (response.isSuccessful()) {
                    if(response.body().code.equals("401"))
                    {
                        GeneralFunctions.tokenExpireAction(StaticFormActivity.this);
                    }
                    else if (response.body().code.equals("401")) {
                        Intent intent = new Intent(StaticFormActivity.this,
                                SplashActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        GeneralFunctions.clearPrefs(mPrefs);
                        startActivity(intent);
                        Toast.makeText(StaticFormActivity.this, "Session Expired! Please login again", Toast.LENGTH_SHORT).show();
                    }

                    if (response.body().success == 1) {
                        mPrefs.save(Constants.GenerateSurveyId,"no");
                        mPrefs.save(Constants.SurveyId, response.body().data.survey_id);
                        getKyc("", "myself");
                    } else {
                        Toast.makeText(StaticFormActivity.this, response.body().message, Toast.LENGTH_SHORT).show();


                    }

                } else {
                    Toast.makeText(StaticFormActivity.this, getResources().getString(R.string.serverIssue), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeneralModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                // rlStart.setVisibility(View.GONE);
                Toast.makeText(StaticFormActivity.this, getResources().getString(R.string.netIssue), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mPrefs.save(Constants.FromStaticBack, "yes");
        mPrefs.save(Constants.FormFilled, "NO");
    }












}
