package easygov.saral.harlabh.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import easygov.saral.harlabh.adapters.BenefitsAdapter;
import easygov.saral.harlabh.models.BenefitList;
import easygov.saral.harlabh.models.responsemodels.generalmodel.GeneralModel;
import easygov.saral.harlabh.models.responsemodels.kycmodel.KycModel;
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

public class ThirdEligibilityActivity extends AppCompatActivity  implements View.OnClickListener{


    private ViewPager vpSurveyScheme;
    private Prefs mPrefs;
    public static int counts=0;
    private RecyclerView rvBenefits,rvBenefitsMonetory,rvBenefitsNon;
    private SeconPagerAdapter adapter;
    private EditText etAadhaarMandatory;
    private StartSurveyPaging object ;
    Map<String,String> map=new HashMap<>();
    private PopupWindow selectMethotPop,selectOtpPop;
    private ImageView ivEligibilityBack,ivBenefitsBack;
    private RelativeLayout rlNoElgble,rlEligibleWithBenefits,rlElgblTop,rlEligibleCircles,rlHavingAadhaar,rlEnterAadhar;
    private LinearLayout llEligibleTexts;
    private TextView tvNo,tvYes,tvNextHavingAadhar,tvNextAadharStatic;
    private String havingAadhaar="";
    private List<StartSurveyPaging> list=new ArrayList<>();
    private TextView tvSchemeDisplay;
    private TextView tvThirdScheme,tvTryAgainEligible,tvApplyScheme,tvMonetory,tvNonMonetory,tvDbt;
    RadioButton rbOtp,rbIris,rbFinger,rbQrcode;
    private int type,kycFailed=0;
    private String aadhaar;
    private PopupWindow otpPopup;
    private EditText etOtpNew;
    private TextView tvTimer,tvResendOtp,tvIndicative;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eligibility);
        init();
        clickHandlers();
        Bundle bundle=new Bundle();
        bundle=getIntent().getExtras();
        if(bundle!=null)
        {
            object=new Gson().fromJson(bundle.getString("final"),new TypeToken<StartSurveyPaging>() {}.getType());
            list.add(object);
            mPrefs.save(Constants.savedResponses,new Gson().toJson(list));

            vpSurveyScheme.setAdapter(adapter);
        }
    }

    private void init() {
        vpSurveyScheme= findViewById(R.id.vpSurveyScheme);
        etAadhaarMandatory= findViewById(R.id.etAadhaarMandatory);
        mPrefs=Prefs.with(this);
        rlNoElgble= findViewById(R.id.rlNoElgble);
        rlHavingAadhaar= findViewById(R.id.rlHavingAadhaar);
        tvTryAgainEligible= findViewById(R.id.tvTryAgainEligible);
        ivEligibilityBack= findViewById(R.id.ivEligibilityBack);
        ivBenefitsBack= findViewById(R.id.ivBenefitsBack);
        tvSchemeDisplay=findViewById(R.id.tvSchemeDisplay);
        rvBenefits= findViewById(R.id.rvBenefits);
        tvNextHavingAadhar= findViewById(R.id.tvNextHavingAadhar);
        tvNextAadharStatic= findViewById(R.id.tvNextAadharStatic);
        rlEnterAadhar= findViewById(R.id.rlEnterAadhar);
        tvIndicative= findViewById(R.id.tvIndicative);
        tvNo= findViewById(R.id.tvNo);
        tvYes= findViewById(R.id.tvYes);


        tvMonetory= findViewById(R.id.tvMonetory);
        tvNonMonetory= findViewById(R.id.tvNonMonetory);
        tvDbt= findViewById(R.id.tvDbt);



        rvBenefitsMonetory= findViewById(R.id.rvBenefitsMonetory);
        rvBenefitsNon= findViewById(R.id.rvBenefitsNon);
        vpSurveyScheme.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        vpSurveyScheme.setCurrentItem(0);

        rlElgblTop= findViewById(R.id.rlElgblTop);
        tvThirdScheme= findViewById(R.id.tvThirdScheme);
        rlEligibleWithBenefits= findViewById(R.id.rlEligibleWithBenefits);
        tvApplyScheme= findViewById(R.id.tvApplyScheme);

        tvThirdScheme.setText(mPrefs.getString(Constants.SecontActivityText,""));
        rlEligibleCircles= findViewById(R.id.rlEligibleCircles);
        llEligibleTexts= findViewById(R.id.llEligibleTexts);
        adapter=new SeconPagerAdapter(getSupportFragmentManager());


        if(mPrefs.getString(Constants.Applyingfor,"").equals("myself"))
        {
            ((TextView)findViewById(R.id.tvAadhaarQues)).setText(getString(R.string.havingaadharmyself));
        }
        else
            ((TextView)findViewById(R.id.tvAadhaarQues)).setText(getString(R.string.havingaadhar));


        ivBenefitsBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ivEligibilityBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvTryAgainEligible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        rlEnterAadhar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GeneralFunctions.hideSoftKeyboard(ThirdEligibilityActivity.this);
            }
        });

        tvApplyScheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  vpSurveyScheme.setVisibility(View.GONE);
                vpSurveyScheme.setVisibility(View.GONE);
                rlEligibleWithBenefits.setVisibility(View.GONE);

                rlHavingAadhaar.setVisibility(View.VISIBLE);
                rlElgblTop.setVisibility(View.VISIBLE);
                rlEligibleCircles.setVisibility(View.VISIBLE);
                llEligibleTexts.setVisibility(View.VISIBLE);
            /*    Intent intent = new Intent(ThirdEligibilityActivity.this, FourthApplicationActivity.class);
                startActivity(intent);
                ThirdEligibilityActivity.counts = 0;
               finish();
*/

                /*if(mPrefs.getString(Constants.Applyingfor,"").equals("myself") &&
                        mPrefs.getString(Constants.FromAadhar,"").equalsIgnoreCase("yes"))
                {
                    Intent intent = new Intent(ThirdEligibilityActivity.this, FourthApplicationActivity.class);
                    startActivity(intent);
                    ThirdEligibilityActivity.counts = 0;
                    finish();
                }

               else if(!mPrefs.getBoolean(Constants.IS_AADHAR_AUTHENTICATED,false))
                {

                    Intent intent = new Intent(ThirdEligibilityActivity.this, FirstSurveyActivity.class)
                            .putExtras(MyApplication.dataBundle).putExtra("triggerYes",true);
                    startActivity(intent);
                    sendBroadcast(new Intent("closeACT"));
                    finish();

                }
                else
                {

                    if(mPrefs.getBoolean(Constants.ENABLE_APPLY_NON_AADHAR,false))
                    {
                        Intent intent = new Intent(ThirdEligibilityActivity.this, FourthApplicationActivity.class);
                        startActivity(intent);
                        ThirdEligibilityActivity.counts = 0;
                        finish();
                    }
                    else
                    {
                        if(!mPrefs.getBoolean(Constants.IS_KYC_DATA_CHANGED,false))
                        {
                            Intent intent = new Intent(ThirdEligibilityActivity.this, FourthApplicationActivity.class);
                            startActivity(intent);
                            ThirdEligibilityActivity.counts = 0;
                            finish();

                        }
                        else
                            GeneralFunctions.makeSnackbar(tvApplyScheme,getString(R.string.sry_kyc_detail_changed));
                        //todo :check frombackend
                    }
                }*/
            }
        });
        // hitApi();
    }
    public void setPagerPage(int index)
    {
        adapter.setCount();
        adapter.notifyDataSetChanged();
        vpSurveyScheme.setCurrentItem(index);



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void notEligible()
    {
        vpSurveyScheme.setVisibility(View.GONE);
        rlNoElgble.setVisibility(View.VISIBLE);
    }

    public void eligibleWithBenefits(String analysis, String servBenefits, List<String> benefits, List<CustomBenefits> customBenefits)
    {
        vpSurveyScheme.setVisibility(View.GONE);
        rlEligibleWithBenefits.setVisibility(View.VISIBLE);
        llEligibleTexts.setVisibility(View.GONE);
        rlEligibleCircles.setVisibility(View.GONE);
        rlElgblTop.setVisibility(View.GONE);
        tvSchemeDisplay.setText(mPrefs.getString(Constants.SchemeName,""));
        rvBenefits.setLayoutManager(new LinearLayoutManager(this));
        rvBenefitsMonetory.setLayoutManager(new LinearLayoutManager(this));
        rvBenefitsNon.setLayoutManager(new LinearLayoutManager(this));


        if(mPrefs.getString(Constants.SchemeActive,"").equals("1")&&mPrefs.getBoolean(Constants.IsSchemeOpen,false))
        {
            tvApplyScheme.setVisibility(View.VISIBLE);
            tvIndicative.setVisibility(View.GONE);
        }
        else {
            tvApplyScheme.setVisibility(View.GONE);
            tvIndicative.setVisibility(View.VISIBLE);
        }

        //Todo: Live Check
        enforcedApply();


        BenefitList benefitList =new BenefitList();

        for(int i=0;i<customBenefits.size();i++)
        {
            if(customBenefits.get(i).benefit_type.equals("dbt"))
            {
                benefitList.dbt.add(customBenefits.get(i));
            }
            else if(customBenefits.get(i).benefit_type.equals("om"))
            {
                benefitList.monetory.add(customBenefits.get(i));
            }

            else if(customBenefits.get(i).benefit_type.equals("onm"))
            {
                benefitList.nonmonetory.add(customBenefits.get(i));
            }
        }
        if(benefitList.dbt!=null&&benefitList.dbt.size()>0)
        {
            rvBenefits.setAdapter(new BenefitsAdapter(this,benefitList.dbt));
        }
        else {
            tvDbt.setVisibility(View.GONE);
            rvBenefits.setVisibility(View.GONE);
        }

        if(benefitList.monetory!=null&&benefitList.monetory.size()>0)
        {
            rvBenefitsMonetory.setAdapter(new BenefitsAdapter(this,benefitList.monetory));
        }
        else {
            tvMonetory.setVisibility(View.GONE);
            rvBenefitsMonetory.setVisibility(View.GONE);
        }

        if(benefitList.nonmonetory!=null&&benefitList.nonmonetory.size()>0)
        {
            rvBenefitsNon.setAdapter(new BenefitsAdapter(this,benefitList.nonmonetory));
        }
        else {
            tvNonMonetory.setVisibility(View.GONE);
            rvBenefitsNon.setVisibility(View.GONE);
        }



    }

    private void enforcedApply() {
        tvApplyScheme.setVisibility(View.VISIBLE);
    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.tvYes) {
            tvNextHavingAadhar.setBackgroundResource(R.color.Sign);
            tvNextHavingAadhar.setClickable(true);
            tvYes.setBackgroundResource(R.drawable.green_curved);
            tvNo.setBackgroundResource(R.drawable.white_curved);
            tvYes.setTextColor(ContextCompat.getColor(this, R.color.white));
            tvNo.setTextColor(ContextCompat.getColor(this, R.color.black));
            havingAadhaar = "yes";

        } else if (i == R.id.tvNo) {
            mPrefs.save(Constants.IS_AADHAR_AUTHENTICATED, false);
            tvNextHavingAadhar.setBackgroundResource(R.color.Sign);
            tvNextHavingAadhar.setClickable(true);
            tvNo.setBackgroundResource(R.drawable.green_curved);
            tvYes.setBackgroundResource(R.drawable.white_curved);
            tvYes.setTextColor(ContextCompat.getColor(this, R.color.black));
            tvNo.setTextColor(ContextCompat.getColor(this, R.color.white));
            havingAadhaar = "no";

        } else if (i == R.id.tvNextHavingAadhar) {
            if (havingAadhaar.equals("yes")) {
                vpSurveyScheme.setVisibility(View.GONE);
                rlHavingAadhaar.setVisibility(View.GONE);
                rlEnterAadhar.setVisibility(View.VISIBLE);
            } else if (havingAadhaar.equals("no")) {
                showInfo();
            }

        } else if (i == R.id.tvNextAadharStatic) {
            if (validateAadharNumber(etAadhaarMandatory.getText().toString())) {
                mPrefs.save(Constants.AadhaarNo, etAadhaarMandatory.getText().toString());

                //Todo: Fix removed finger only otp
                //initiatePopupWindow(rlElgblTop,etAadhaarMandatory.getText().toString());
                aadhaar = etAadhaarMandatory.getText().toString();


                //todo:Tester Mode
                //MyApplication.Tester=1;
                if (MyApplication.Tester == 0) {
                    if (ContextCompat.checkSelfPermission(ThirdEligibilityActivity.this, Manifest.permission.READ_SMS)
                            == PackageManager.PERMISSION_GRANTED) {

                        hitgetOtpApi(aadhaar, rlElgblTop);

                    } else {
                        requestPermissions(
                                new String[]{Manifest.permission.READ_SMS},
                                120);
                    }
                } else {
                    mPrefs.save(Constants.IS_AADHAR_AUTHENTICATED, true);
                    Intent intent = new Intent(ThirdEligibilityActivity.this, StaticFormActivity.class).putExtra("adharAuthenticated", true);
                    startActivity(intent);
                    overridePendingTransition(R.anim.animateup_activity, R.anim.animatedown_activity);
                }

            } else
                GeneralFunctions.makeSnackbar(rlElgblTop, getResources().getString(R.string.plvalidaadhaar));


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case 120:
                hitgetOtpApi(aadhaar,rlElgblTop);
        }
    }

    public  boolean validateAadharNumber(String aadharNumber) {
        Pattern aadharPattern = Pattern.compile("\\d{12}");
        boolean isValidAadhar = aadharPattern.matcher(aadharNumber).matches();
        if (isValidAadhar) {
            isValidAadhar = VerhoeffAlgorithm.validateVerhoeff(aadharNumber);
        }
        return isValidAadhar;
    }
    private void clickHandlers() {
        tvYes.setOnClickListener(this);
        tvNo.setOnClickListener(this);
        tvNextAadharStatic.setOnClickListener(this);
        tvNextHavingAadhar.setOnClickListener(this);
    }

    public void initiatePopupWindow(final View view, final String string) {

        try {
            aadhaar=string;
            //We need to get the instance of the LayoutInflater, use the context of this activity
            LayoutInflater inflater = (LayoutInflater) this
                    .getSystemService(LAYOUT_INFLATER_SERVICE);
            //Inflate the view from a predefined XML layout
            View layout = inflater.inflate(R.layout.info_aadhaarinsidepopup,
                    (ViewGroup)view.findViewById(R.id.popup_selectMethod12));
            // create a 300px width and 470px height PopupWindow
            selectMethotPop = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
            // display the popup in the center
            selectMethotPop.showAtLocation(layout, Gravity.CENTER, 0, 0);
            // TextView tvGot= (TextView) layout.findViewById(R.id.tvGot);
            final RelativeLayout rlAuthMethodNextPopUp= layout.findViewById(R.id.rlAuthMethodNextPopUp);
            //TextView tvMethodDesc= (TextView) layout.findViewById(R.id.tvMethodDesc);

            ImageView ivqrinfo = layout.findViewById(R.id.ivqrinfo);
            ImageView ivVerifyBack= layout.findViewById(R.id.ivVerifyBack);

            ImageView ivOtpInfo= layout.findViewById(R.id.ivOtpInfo);
            ImageView ivFingerInfo= layout.findViewById(R.id.ivFingerInfo);


            ivOtpInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initiateInfoMethod(view,getString(R.string.otpmethod),getString(R.string.otpinfo),R.drawable.otp_01);
                }
            });

            ivFingerInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initiateInfoMethod(view,getString(R.string.fingerprint),getString(R.string.fingerInfo),R.drawable.finger_print_01);
                }
            });

            type=1;
            RadioGroup rgBiometric = layout.findViewById(R.id.rgBiometric);
            rbOtp = layout.findViewById(R.id.rbOtp);
            rbIris = layout.findViewById(R.id.rbIris);
            rbFinger = layout.findViewById(R.id.rbFinger);
            rbQrcode= layout.findViewById(R.id.rbQrcode);

            rbQrcode.setVisibility(View.GONE);
            ivqrinfo.setVisibility(View.GONE);
            rbOtp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        type = 1;
                        rbFinger.setChecked(false);
                        rbIris.setChecked(false);
                        rbQrcode.setChecked(false);
                    }
                }
            });

            rbFinger.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        type = 2;
                        rbIris.setChecked(false);
                        rbOtp.setChecked(false);
                        rbQrcode.setChecked(false);
                    }

                }
            });

            rbIris.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        type = 3;
                        rbFinger.setChecked(false);
                        rbOtp.setChecked(false);
                        rbQrcode.setChecked(false);
                    }
                }
            });

            rbQrcode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        type = 4;
                        rbFinger.setChecked(false);
                        rbOtp.setChecked(false);
                        rbIris.setChecked(false);
                    }
                }
            });
            ivVerifyBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectMethotPop.dismiss();
                }
            });
            rlAuthMethodNextPopUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    switch (type)
                    {
                        case 1:
                            hitgetOtpApi(string,view);
                           /*
                            // TODO: 14/05/18
                              Intent intent=new Intent(ThirdEligibilityActivity.this, StaticFormActivity.class).putExtra("adharAuthenticated",true);
                            startActivity(intent);
                            overridePendingTransition( R.anim.animateup_activity, R.anim.animatedown_activity );
*/
                            // onBackPressed();
                            break;

                        case 2:
                            try {
                                Intent intent1 = new Intent("in.gov.uidai.rdservice.fp.INFO");
                                intent1.setPackage("com.scl.rdservice");
                                startActivityForResult(intent1, 1200);
                                // onBackPressed();
                            }
                            catch (Exception e)
                            {
                                //onBackPressed();
                                // GeneralFunctions.installmorpho(view,FirstSurveyActivity.this);
                                // GeneralFunctions.makeSnackbar(rlFirstContainer,"Install morpho SCL app");

                            }
                            break;

                        case 3:
                            //  String value = "<PidOptions ver=\"1.0\"><Opts env=\"S\" iType=\"0\" format=\"0\" pidVer=\"2.0\" iCount=\"1\"></Opts><Demo></Demo><CustOpts></CustOpts></PidOptions>";
                            // callApnaPayRDService(FirstSurveyActivity.this,value);
                            GeneralFunctions.makeSnackbar(rlAuthMethodNextPopUp,getResources().getString(R.string.irisnotsupported));
                            break;

                        case 4:
                            Bundle bundle =new Bundle();
                            //bundle.putString("isqr","yes");
                            Intent intent1=new Intent(ThirdEligibilityActivity.this, StaticFormActivity.class);
                            intent1.putExtra("isqr","yes");
                            startActivity(intent1);
                            //overridePendingTransition( R.anim.animateup_activity, R.anim.animatedown_activity );
                            // onBackPressed();
                            break;
                        default:

                    }
                    selectMethotPop.dismiss();
                }
            });
            selectMethotPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    selectMethotPop.dismiss();

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void initiateInfoMethod(View view,String title,String text,int why_img) {
        final PopupWindow pw;
        try {
            //We need to get the instance of the LayoutInflater, use the context of this activity
            LayoutInflater inflater = (LayoutInflater) this
                    .getSystemService(LAYOUT_INFLATER_SERVICE);
            //Inflate the view from a predefined XML layout
            View layout = inflater.inflate(R.layout.info_popup,
                    (ViewGroup)view.findViewById(R.id.popup_element));
            // create a 300px width and 470px height PopupWindow
            pw = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
            // display the popup in the center
            pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
            TextView tvGot= layout.findViewById(R.id.tvGot);
            TextView tvInfoMethod= layout.findViewById(R.id.tvInfoMethod);
            TextView tvMethodDesc= layout.findViewById(R.id.tvMethodDesc);
            ImageView ivWhyImg= layout.findViewById(R.id.ivWhyImg);

            tvMethodDesc.setText(text);

            tvInfoMethod.setText(title);
            ivWhyImg.setBackgroundResource(why_img);
            tvGot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pw.dismiss();
                }
            });
            pw.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    pw.dismiss();

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* public static void callApnaPayRDService(Activity context, String value) {
         try {
             Intent intent = new Intent(Intent.ACTION_MAIN);
             intent.setComponent(new ComponentName("com.gtid.apnapay_rdservice","com.gtid.apnapay_rdservice.CaptureActivity"));
             intent.putExtra("PID_OPTIONS",value);
             //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_CLEAR_TOP);
             context.startActivityForResult(intent,111);

         } catch (ActivityNotFoundException e) {
             //Toast.makeText(getApplicationContext(), "Did not find Aadhaar RDService Activity. Please install AadhaarIrisCaptureActivity.apk to use this application.", Toast.LENGTH_LONG).show();
             e.printStackTrace();
         }
     }*/
    private void hitgetOtpApi(String aadhaarId, final View view) {
        GeneralFunctions.showDialog(ThirdEligibilityActivity.this);
        Call<GeneralModel> call = RestClient.get().getAadhaarOtpforkyc(aadhaarId);
        call.enqueue(new Callback<GeneralModel>() {
            @Override
            public void onResponse(Call<GeneralModel> call, Response<GeneralModel> response) {
                GeneralFunctions.dismissDialog();
                if (response.isSuccessful()) {

                    if (response.body().success == 1) {


                        mPrefs.save(Constants.OtpTransId,response.body().data.transaction_id);
                        initiateOtpPopupWindow(view);
                       /* String postData = null;
                        try {
                            postData = "hash=" + URLEncoder.encode(response.body().data.hash, "UTF-8") +
                                    "&aadhaarId=" + URLEncoder.encode(response.body().data.aadhaarId, "UTF-8")
                                    + "&purpose=" + URLEncoder.encode(response.body().data.purpose, "UTF-8") +
                                    "&channel=" + URLEncoder.encode(response.body().data.channel, "UTF-8")
                                    + "&failureUrl=" + URLEncoder.encode(response.body().data.failureUrl, "UTF-8")
                                    + "&modality=" + URLEncoder.encode(response.body().data.modality, "UTF-8")
                                    + "&saCode=" + URLEncoder.encode(response.body().data.saCode, "UTF-8")
                                    + "&requestId=" + URLEncoder.encode(response.body().data.requestId, "UTF-8")
                                    + "&successUrl=" + URLEncoder.encode(response.body().data.successUrl, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        //  wbWebView.postUrl(response.body().data.url, postData.getBytes());*/
                    } else {
                        aadhaarNotWorking(getString(R.string.aadhaarnotworking));
                        //GeneralFunctions.makeSnackbar(rlElgblTop,response.body().message);
                        //getFragmentManager().popBackStack();
                        //getFragmentManager().popBackStack();
                    }
                }
                else
                {
                    GeneralFunctions.makeSnackbar(rlElgblTop,getResources().getString(R.string.serverIssue));
                }
            }

            @Override
            public void onFailure(Call<GeneralModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(rlElgblTop,getResources().getString(R.string.netIssue));
            }
        });
    }



    public void initiateOtpPopupWindow(final View view) {

        try {
            //We need to get the instance of the LayoutInflater, use the context of this activity
            LayoutInflater inflater = (LayoutInflater) this
                    .getSystemService(LAYOUT_INFLATER_SERVICE);
            //Inflate the view from a predefined XML layout
            final View layout = inflater.inflate(R.layout.info_select_otp_popup,
                    (ViewGroup)view.findViewById(R.id.rlOtpPopUp));
            // create a 300px width and 470px height PopupWindow
            otpPopup = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
            // display the popup in the center
            otpPopup.showAtLocation(layout, Gravity.CENTER, 0, 0);
            // TextView tvGot= (TextView) layout.findViewById(R.id.tvGot);
            final RelativeLayout rlOtpStatic = layout.findViewById(R.id.rlOtpStatic);
            etOtpNew = layout.findViewById(R.id.etOtpNew);
            TextView tvNextOtpPopUp= layout.findViewById(R.id.tvNextOtpPopUp);
            ImageView ivVerifyOtpBack= layout.findViewById(R.id.ivVerifyOtpBack);
            tvTimer = layout.findViewById(R.id.tvTimer);


            rlOtpStatic.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    GeneralFunctions.hideSoftKeyboard(ThirdEligibilityActivity.this);
                    return false;
                }
            });
            tvResendOtp = layout.findViewById(R.id.tvResendOtp);


            setTimer();
            SmsReceiver.bindListener(new SmsListener() {
                @Override
                public void messageReceived(String messageText) {

                    try {
                        if(messageText.length()==6)
                        {
                            etOtpNew.setText(messageText);
                            hitGetKycApi("","otp_kyc",etOtpNew.getText().toString());
                        }
                    }
                    catch (Exception e)
                    {

                    }

                }
            });

            tvResendOtp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hitgetOtpApi(aadhaar,view);
                    timer.cancel();

                    otpPopup.dismiss();

                }
            });
            ivVerifyOtpBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    otpPopup.dismiss();

                    timer.cancel();
                }
            });
            otpPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    timer.cancel();
                    otpPopup.dismiss();

                }
            });

            tvNextOtpPopUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    otpPopup.dismiss();
                    if(etOtpNew.getText().toString().length()==6)
                    {
                        hitGetKycApi("","otp_kyc",etOtpNew.getText().toString());
                    }
                    else {
                        GeneralFunctions.makeSnackbar(rlOtpStatic,getResources().getString(R.string.validotp));
                    }


                }
            });

        } catch (Exception e) {
            e.printStackTrace();

        }
    }



    private CountDownTimer timer;
    private void setTimer() {
        timer=   new CountDownTimer(61000, 1000) {

            public void onTick(long millisUntilFinished) {
                tvTimer.setVisibility(View.VISIBLE);
                if (millisUntilFinished / 1000 >= 60) {
                    tvTimer.setText("1:00");
                } else if (millisUntilFinished / 1000 < 10) {
                    tvTimer.setText("0:0" + millisUntilFinished / 1000);
                } else
                    tvTimer.setText("0:" + millisUntilFinished / 1000);
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                tvTimer.setVisibility(View.GONE);
                tvResendOtp.setVisibility(View.VISIBLE);
            }

        }.start();


    }

    private void hitGetKycApi(String pidData,String via,String otp) {

        String profileid="";
        String applyingFor="";
        if(mPrefs.getString(Constants.IS_USER_FIRST_TIME,"").equals("yes"))
        {
            mPrefs.save(Constants.IS_USER_FIRST_TIME,"no");
            applyingFor ="myself";
            profileid="";
        }

        else {
            applyingFor = mPrefs.getString(Constants.Applyingfor, "");
            profileid=mPrefs.getString(Constants.ProfileId, "");
        }

        GeneralFunctions.showDialog(this);
        Call<KycModel> call =RestClient.get().authenticateUser(aadhaar,via,otp,mPrefs.getString(Constants.OtpTransId,"")
                ,pidData,mPrefs.getString(Constants.SurveyId,""),applyingFor,profileid);
        call.enqueue(new Callback<KycModel>() {
            @Override
            public void onResponse(Call<KycModel> call, Response<KycModel> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful())
                {
                    if(response.body().code==401)
                    {
                        GeneralFunctions.tokenExpireAction(ThirdEligibilityActivity.this);
                    }
                    else if(response.body().success==1)
                    {
                        //mPrefs.save(Constants.Auth,response.body().data.token);
                        // getFragmentManager().beginTransaction().add(R.id.rlAadhaarSignIn,new CreatePassword()).addToBackStack(null).commit();
                        // getFragmentManager().beginTransaction().replace(R.id.rlAadhaarSignIn, new ConfirmationFragment()).addToBackStack(null).commit();
                        // mPrefs.save(Constants.OtpFrom, "aadhar");

                        GeneralFunctions.makeSnackbar(rlElgblTop,response.body().data.kyc.poi.name);
                        mPrefs.save(Constants.IS_AADHAR_AUTHENTICATED,true);
                        Intent intent=new Intent(ThirdEligibilityActivity.this, StaticFormActivity.class).putExtra("adharAuthenticated",true);
                        startActivity(intent);
                        overridePendingTransition( R.anim.animateup_activity, R.anim.animatedown_activity );
                        // hitSurveyApiForMyself();
                    }
                    else{
                        //todo:  revert 0 to 1 to swith to auth if kyc failed
                        //kycFailed=1;

                        if(response.body().code==540)
                            showInfoChangedData();
                        else
                        {      kycFailed=0;
                            GeneralFunctions.makeSnackbar(rvBenefits,response.body().message);
                            //aadhaarNotWorking(response.body().message);

                        }
                    }


                }
                else {
                    GeneralFunctions.makeSnackbar(rlElgblTop,getResources().getString(R.string.serverIssue));
                }
            }

            @Override
            public void onFailure(Call<KycModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(rlElgblTop,getResources().getString(R.string.netIssue));
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       /* if (resultCode == RESULT_OK)
        {*/
        if(requestCode==1000) {
            selectMethotPop.dismiss();
            Bundle b = data.getExtras();
   /* if (b != null) {
        String deviceInfo = b.getString("DEVICE_INFO", "");
        String rdServiceInfo = b.getString("RD_SERVICE_INFO", "");

    }*/


            if (b != null)
            {
// in this variable you will get Pid data
                String pidData = b.getString("PID_DATA");
                String s2= pidData.replaceAll("DeviceInfo","DeviceData");
                Log.d("finger",pidData);

                s2=GeneralFunctions.removeStr(s2);
// you will get value in this variable when your finger print device not connected
                String dnc = b.getString("DNC", "");
// you will get value in this variable when your finger print device not registered.
                String dnr = b.getString("DNR", "");

                if(dnc.equals(""))
                {
                    GeneralFunctions.makeSnackbar(rlElgblTop,getResources().getString(R.string.fingercaptured));
                    if(kycFailed==0)
                        hitGetKycApi(s2,"fmr_kyc","");

                    else {
                        hitGetKycApi(s2,"fmr_auth","");
                    }
                }
                else {
                    GeneralFunctions.makeSnackbar(rlElgblTop,getResources().getString(R.string.nofingerprint));
                }
            }
        }



        if(requestCode==1200)
        {
            try {
                Bundle b = data.getExtras();

                if (b != null)
                {
                    String deviceInfo = b.getString("DEVICE_INFO", "");
                    String rdServiceInfo = b.getString("RD_SERVICE_INFO", "");
                    ArrayList<String> asd=new ArrayList<>();
                    if(!rdServiceInfo.contains("info=\"Device not")) {
                        asd= GeneralFunctions.createAuthXMLRegistered(deviceInfo);

                        if (asd.size() > 1) {
                            if (!asd.get(0).equals("error")) {
                                if (asd.get(0) != null && asd.get(1) != null && asd.get(0).length() > 1 && asd.get(1).length() > 1) {
                                    startCapture(asd.get(0), asd.get(1));
                                }
                            }
                        }
                    }
                    else {
                        GeneralFunctions.makeSnackbar(rlElgblTop,getResources().getString(R.string.nodevice));
                    }

                }
            }
            catch (Exception e)
            {
                GeneralFunctions.installmorpho(rlElgblTop,this);
            }

        }
    }

    private void startCapture(String s, String s1)
    {

        String wadh="MglkKtwVbnGyKvqcYO4XXIfaxYmXgxev2R2B3nqeLnc=";
        //wadh="3209642ADC156E71B22AFA9C60EE175C87DAC589978317AFD91D81DE7A9E2E77";
        //  wadh="AE156E2FB4A7262D96D949ACCAE915A98EDCF77256C8BF4EFE454A81D34C7EFF";//2.1FYNNN
//wadh="ae156e2fb4a7262d96d949accae915a98edcf77256c8bf4efe454a81d34c7eff";
        //wadh="FB49E3BD9962E089246F11BDC8D29258D306ED1358E8E87258151FFE7E436A0E"; // 2/1FYYNN
        // wadh="B22D3AE8454B63D70DB2140D2DFC982F438C52480E3C38502698E0BBAE983A72"; // 2.1FYYYN

        String responseXml="";
        if(kycFailed==0)
            responseXml = "<PidOptions ver=\"1.0\"><Opts env=\"P\" fCount=\"1\" fType=\"0\" " +
                    "format=\"0\" pidVer=\"2.0\" timeout=\"10000\" wadh=\""+wadh+"\" posh=\"UNKNOWN\"/><Demo></Demo>" +
                    "<CustOpts><Param name=\""+s+"\" value=\""+s1+"\"/></CustOpts></PidOptions>";

        else {
            responseXml = "<PidOptions ver=\"1.0\"><Opts env=\"P\" fCount=\"1\" fType=\"0\" " +
                    "format=\"0\" pidVer=\"2.0\" timeout=\"10000\" posh=\"UNKNOWN\"/><Demo></Demo>" +
                    "<CustOpts><Param name=\""+s+"\" value=\""+s1+"\"/></CustOpts></PidOptions>";
        }
        Intent intent = new Intent("in.gov.uidai.rdservice.fp.CAPTURE");
        intent.setPackage("com.scl.rdservice");
        intent.putExtra("PID_OPTIONS", responseXml);
        startActivityForResult(intent, 1000);
    }

    private void showInfo() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.sorry_you_cant_apply_without_aadhar_card))
                .setPositiveButton(this.getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                .setNegativeButton(this.getString(R.string.gotohome),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                sendBroadcast(new Intent("closeACT"));
                                finish();

                            }
                        }).show();


    }



    private void showInfoChangedData() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.sorry_you_cant_apply_aadhar_data_changed))
                .setPositiveButton(this.getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(mPrefs.getString(Constants.Applyingfor,"").equalsIgnoreCase("myself"))
                                    mPrefs.save(Constants.RestartSurvey,"no");

                                else {
                                    mPrefs.save(Constants.RestartSurvey,"yes");
                                }

                                if(mPrefs.getString(Constants.FamilyMyselfCase,"").equalsIgnoreCase("yes"))
                                {
                                    // mPrefs.save(Constants.FamilyMyselfCase,"no");
                                    mPrefs.save(Constants.RestartSurvey,"yes");

                                }
                                Intent intent = new Intent(ThirdEligibilityActivity.this, FirstSurveyActivity.class)
                                        .putExtras(MyApplication.dataBundle).putExtra("triggerYes",true);
                                startActivity(intent);
                                sendBroadcast(new Intent("closeACT"));
                                finish();

                            }
                        }).show();


    }

    private void aadhaarNotWorking(String msg) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg)
                .setPositiveButton(this.getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                dialogInterface.dismiss();
                            }
                        }).show();


    }



}
