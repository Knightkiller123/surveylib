package easygov.saral.harlabh.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import easygov.saral.harlabh.fragments.StaticSurveyFragment;
import easygov.saral.harlabh.fragments.storyboardfrags.YourStoryDetailsFragment;
import easygov.saral.harlabh.models.responsemodels.kycmodel.KycModel;
import easygov.saral.harlabh.models.surveypaging.StartSurveyPaging;
import easygov.saral.harlabh.utils.GeneralFunctions;
import easygov.saral.harlabh.utils.MyApplication;
import easygov.saral.harlabh.utils.Prefs;
import easygov.saral.harlabh.utils.RestClient;
import easygov.saral.harlabh.R;
import easygov.saral.harlabh.adapters.PagerAdapter;
import easygov.saral.harlabh.models.responsemodels.generalmodel.GeneralModel;
import easygov.saral.harlabh.utils.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FirstSurveyActivity extends AppCompatActivity  {
    private ViewPager vpSurvey;
    private StartSurveyPaging object ;
    private Prefs mPrefs;
    private String s="";
    private PagerAdapter adapter;
    private ProgressBar pbSet;
    private List<StartSurveyPaging> list=new ArrayList<>();
    private RelativeLayout rlStaticSurveyForm,rlPager,rlFirstContainer,rlNoElgbleFirst;
    public static Activity inst;
    private ImageView ivSurveyBack;
    private TextView tvFirstSurvry,tvTryAgainEligibleFirst;
    private StaticSurveyFragment fragment;
    private String wadh ="";

    private PopupWindow selectMethotPop,selectOtpPop;
    String q,w,e,knowing,applyingFor;
    private boolean onResume=true;
    //todo:  0- kyc ; 1 - auth
    private int kycFailed=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        init();
        hitGenderApi();
        Bundle bundle =getIntent().getExtras();
        if(bundle!=null)
        {
            MyApplication.dataBundle=bundle;
            q=bundle.getString("is_category");
            w= bundle.getString("is_scheme");
            e =bundle.getString("selected_id");
            knowing=bundle.getString("knowing");
            applyingFor=bundle.getString("applyingfr");
            // getSurveyId();

            fragment=new StaticSurveyFragment();
            Bundle bundle1 =new Bundle();
            bundle1.putString("applyin",knowing);
            bundle1.putString("aplFor",applyingFor);
            bundle1.putBoolean("triggerYes",getIntent().hasExtra("triggerYes"));
            fragment.setArguments(bundle1);
            getSupportFragmentManager().beginTransaction().add(R.id.rlStaticSurveyForm,fragment).addToBackStack(null).commit();
            //String r=bundle.getString("geography_id");
            //  hitApi(w,q,e,mPrefs.getString(Constants.GeographyId,""));
        }

    }

    BroadcastReceiver broadcastReceiver =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
       registerReceiver(broadcastReceiver,new IntentFilter("closeACT"));
    }

    private void hitGenderApi() {
        Call<GeneralModel> cal =RestClient.get().getGenderId();
        cal.enqueue(new Callback<GeneralModel>() {
            @Override
            public void onResponse(Call<GeneralModel> call, Response<GeneralModel> response) {
                if(response.isSuccessful()) {
                    if(response.body().code.equals("401"))
                    {
                        GeneralFunctions.tokenExpireAction(FirstSurveyActivity.this);
                    }
                    else
                    if (response.body().success == 1)
                    {

                        if (response.body().data.options != null && response.body().data.options.size() > 0) {
                            for (int i = 0; i < response.body().data.options.size(); i++) {
                                String s = response.body().data.options.get(i).value;
                                if (s.equalsIgnoreCase("Male")) {
                                    mPrefs.save(Constants.MaleId, response.body().data.options.get(i).id);
                                } else if (s.equalsIgnoreCase("Female")) {
                                    mPrefs.save(Constants.FemaleId, response.body().data.options.get(i).id);
                                } else if (s.equalsIgnoreCase("Transgender")) {
                                    mPrefs.save(Constants.TransGenId, response.body().data.options.get(i).id);
                                }
                            }
                        }
                    }


                }
            }

            @Override
            public void onFailure(Call<GeneralModel> call, Throwable t) {

                Toast.makeText(FirstSurveyActivity.this, "fail", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void init() {
        object=new StartSurveyPaging();
        inst=this;
        mPrefs=Prefs.with(this);

        rlFirstContainer= findViewById(R.id.rlFirstContainer);
        rlStaticSurveyForm= findViewById(R.id.rlStaticSurveyForm);
        rlPager= findViewById(R.id.rlPager);
        ivSurveyBack= findViewById(R.id.ivSurveyBack);
        vpSurvey= findViewById(R.id.vpSurvey);
        rlNoElgbleFirst= findViewById(R.id.rlNoElgbleFirst);
        tvTryAgainEligibleFirst= findViewById(R.id.tvTryAgainEligibleFirst);
        vpSurvey.setOffscreenPageLimit(20);
        vpSurvey.setCurrentItem(0);
        pbSet= findViewById(R.id.pbSet);
        vpSurvey.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        adapter=new PagerAdapter(getSupportFragmentManager());
        tvFirstSurvry= findViewById(R.id.tvFirstSurvry);
        tvFirstSurvry.setText(mPrefs.getString(Constants.SecontActivityText,""));



        ivSurveyBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rlPager.getVisibility()==View.VISIBLE||rlNoElgbleFirst.getVisibility()==View.VISIBLE)
                {
                    // fragment.backHandling();
                    //GeneralFunctions.backPopup(rlApplyingFor.getRootView(),getActivity(),0);
                    GeneralFunctions.backPopup(rlPager.getRootView(),FirstSurveyActivity.this,0);
                    // finish();
                }
                else
                    try {
                        fragment.backHandling();

                    }
                    catch (Exception e)
                    {
                        finish();
                    }
            }
        });


        //if(count==0)
        //hitApi();
        tvTryAgainEligibleFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void hitSurveyApiForMyself()
    {
        rlStaticSurveyForm.setVisibility(View.GONE);
        rlPager.setVisibility(View.VISIBLE);
        hitStartSurveyApi(w,q,e,mPrefs.getString(Constants.GeographyId,""));
    }

    public void noSchemeAvailable()
    {   rlPager.setVisibility(View.GONE);
        rlNoElgbleFirst.setVisibility(View.VISIBLE);
        rlStaticSurveyForm.setVisibility(View.GONE);
    }



    @Override
    protected void onResume() {
        super.onResume();

            //onResume = true;
            if(onResume) {
                if (mPrefs.getString(Constants.FromStaticBack, "").equals("yes") && (mPrefs.getString(Constants.AadhaarBackendEnable, "").equals("0") || mPrefs.getString(Constants.FromAadhar, "").equals("YES"))) {
                    mPrefs.save(Constants.FromStaticBack, "no");
                    finish();
                } else if (mPrefs.getString(Constants.FormFilled, "").equals("YES")) {

                    mPrefs.save(Constants.FormFilled, "NO");
                    mPrefs.save(Constants.FromStaticBack, "no");
                    if (!mPrefs.getString(Constants.FromSomeoneElseGo, "").equals("yes")) {
                        hitSurveyApiForMyself();
                    }
                } else if (mPrefs.getString(Constants.FromOtpCanceled, "").equals("YES")) {
                    {

                        mPrefs.save(Constants.FromOtpCanceled, "NO");
                        mPrefs.save(Constants.FromStaticBack, "no");
                        rlPager.setVisibility(View.GONE);
                        rlStaticSurveyForm.setVisibility(View.VISIBLE);
                        // getSurveyId();

                        fragment = new StaticSurveyFragment();
                        getSupportFragmentManager().beginTransaction().add(R.id.rlStaticSurveyForm, fragment).addToBackStack(null).commit();

                    }


                }
            }

    }

    public void  checkData() {

            onResume=false;
            if (mPrefs.getString(Constants.FromStaticBack, "").equals("yes") && (mPrefs.getString(Constants.AadhaarBackendEnable, "").equals("0") || mPrefs.getString(Constants.FromAadhar, "").equals("YES"))) {
                mPrefs.save(Constants.FromStaticBack, "no");
                finish();
            } else if (mPrefs.getString(Constants.FormFilled, "").equals("YES")) {

                mPrefs.save(Constants.FormFilled, "NO");
                mPrefs.save(Constants.FromStaticBack, "no");
                if (!mPrefs.getString(Constants.FromSomeoneElseGo, "").equals("yes")) {
                    hitSurveyApiForMyself();
                }
            } else if (mPrefs.getString(Constants.FromOtpCanceled, "").equals("YES")) {
                {

                    mPrefs.save(Constants.FromOtpCanceled, "NO");
                    mPrefs.save(Constants.FromStaticBack, "no");
                    rlPager.setVisibility(View.GONE);
                    rlStaticSurveyForm.setVisibility(View.VISIBLE);
                    // getSurveyId();

                    fragment = new StaticSurveyFragment();
                    getSupportFragmentManager().beginTransaction().add(R.id.rlStaticSurveyForm, fragment).addToBackStack(null).commit();

                }

            }


    }

    /*private void getSurveyId()
    {
        mPrefs.remove(Constants.UserServiceID);
        GeneralFunctions.showDialog(this);
        Call<GeneralModel> call= RestClient.get().getSurveyId();
        call.enqueue(new Callback<GeneralModel>() {
            @Override
            public void onResponse(Call<GeneralModel> call, Response<GeneralModel> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful())
                {
                    if(response.body().code.equals("401"))
                    {
                        Intent intent = new Intent(FirstSurveyActivity.this,
                                SplashActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mPrefs.removeAll();
                        startActivity(intent);
                    }

                    if(response.body().success==1)
                    {
                        mPrefs.save(Constants.SurveyId,response.body().data.survey_id);
                        fragment=new StaticSurveyFragment();
                        getSupportFragmentManager().beginTransaction().add(R.id.rlStaticSurveyForm,fragment).addToBackStack(null).commit();
                    }
                    if(response.body().success==0)
                    {
                    }
                }
            }

            @Override
            public void onFailure(Call<GeneralModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
            }
        });
    }*/

    private void hitStartSurveyApi(String is_scheme, String is_category , String selected_id, String geo_id) {
        GeneralFunctions.showDialog(this);

        String pid="";
        String applying=mPrefs.getString(Constants.Applyingfor,"");
        if(applying.equalsIgnoreCase("myself"))
        {
            pid="";
        }
        else {
            pid=mPrefs.getString(Constants.ProfileId,"")  ;
        }
        Call<StartSurveyPaging> call= RestClient.get().startSurvey(is_scheme,is_category,selected_id,geo_id,mPrefs.getString(Constants.SurveyId,"")
        ,pid,applying);

        call.enqueue(new Callback<StartSurveyPaging>() {
            @Override
            public void onResponse(Call<StartSurveyPaging> call, Response<StartSurveyPaging> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful()) {
                    if(response.body().code==401)
                    {
                        GeneralFunctions.tokenExpireAction(FirstSurveyActivity.this);
                    }
                    else if (response.body().success == 1)
                    {
                        object = response.body();
                    list.add(object);
                    s = Constants.savedResponses + "" + YourStoryDetailsFragment.count;
                    mPrefs.save(s, new Gson().toJson(list));
                    if (response.body().data.has_survey) {
                        vpSurvey.setAdapter(adapter);
                    } else {
                        Intent intent = new Intent(FirstSurveyActivity.this, SecondSchemesActivity.class);
                        Bundle bundle = new Bundle();
                        // bundle.putString("qualifiedList", new Gson().toJson(response.body()));
                        mPrefs.save("qualifiedList", new Gson().toJson(response.body()));
                        intent.putExtras(bundle);
                        startActivity(intent);
                        YourStoryDetailsFragment.count = 0;
                        FirstSurveyActivity.this.finish();
                    }
                }
                    else {
                        GeneralFunctions.makeSnackbar(rlFirstContainer,response.body().message);

                    }

                } else GeneralFunctions.makeSnackbar(rlFirstContainer,getResources().getString(R.string.serverIssue));
            }

            @Override
            public void onFailure(Call<StartSurveyPaging> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(rlFirstContainer,getResources().getString(R.string.netIssue));
            }
        });
    }
    public  void setPagerPage(int index)
    {

        //adapter=new PagerAdapter(getFragmentManager());
        adapter.setCount();
        adapter.notifyDataSetChanged();
        vpSurvey.setCurrentItem(index);


    }

    public void setProgress()
    {
        int pro=pbSet.getProgress();
        pbSet.setProgress(pro+10);

    }

    @Override
    public void onBackPressed() {

        if(rlPager.getVisibility()==View.VISIBLE)
        {
            GeneralFunctions.backPopup(rlPager.getRootView(),this,0);
            //finish();
            // super.onBackPressed();
        }
        else
        {
            try {
                fragment.backHandling();
            }
            catch (Exception e)
            {}
        }

    }

    RadioButton rbOtp,rbIris,rbFinger,rbQrcode;
    private int type;
    private String aadhaar;
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
            RelativeLayout rlAuthMethodNextPopUp= layout.findViewById(R.id.rlAuthMethodNextPopUp);
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
                            GeneralFunctions.makeSnackbar(rlFirstContainer,getResources().getString(R.string.irisnotsupported));
                            break;

                        case 4:
                            Bundle bundle =new Bundle();
                            //bundle.putString("isqr","yes");
                            Intent intent=new Intent(FirstSurveyActivity.this, StaticFormActivity.class);
                            intent.putExtra("isqr","yes");
                            startActivity(intent);
                            FirstSurveyActivity.this.finish();
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
        GeneralFunctions.showDialog(FirstSurveyActivity.this);
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
                        GeneralFunctions.makeSnackbar(rlFirstContainer,response.body().message);
                        //getFragmentManager().popBackStack();
                        //getFragmentManager().popBackStack();
                    }
                }
                else
                {
                    GeneralFunctions.makeSnackbar(rlFirstContainer,getResources().getString(R.string.serverIssue));
                }
            }

            @Override
            public void onFailure(Call<GeneralModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(rlFirstContainer,getResources().getString(R.string.netIssue));
            }
        });
    }

    private PopupWindow otpPopup;
    private EditText etOtpNew;
    private TextView tvTimer,tvResendOtp;
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
            RelativeLayout rlOtpStatic = layout.findViewById(R.id.rlOtpStatic);
            etOtpNew = layout.findViewById(R.id.etOtpNew);
            TextView tvNextOtpPopUp= layout.findViewById(R.id.tvNextOtpPopUp);
            ImageView ivVerifyOtpBack= layout.findViewById(R.id.ivVerifyOtpBack);
            tvTimer = layout.findViewById(R.id.tvTimer);


            rlOtpStatic.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    GeneralFunctions.hideSoftKeyboard(FirstSurveyActivity.this);
                    return false;
                }
            });
            tvResendOtp = layout.findViewById(R.id.tvResendOtp);


            setTimer();

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
                        GeneralFunctions.makeSnackbar(rlFirstContainer,getResources().getString(R.string.validotp));
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
                    GeneralFunctions.makeSnackbar(rlFirstContainer,getResources().getString(R.string.fingercaptured));
                    if(kycFailed==0)
                        hitGetKycApi(s2,"fmr_kyc","");

                    else {
                        hitGetKycApi(s2,"fmr_auth","");
                    }
                }
                else {
                    GeneralFunctions.makeSnackbar(rlFirstContainer,getResources().getString(R.string.nofingerprint));
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
                        GeneralFunctions.makeSnackbar(rlFirstContainer,getResources().getString(R.string.nodevice));
                    }

                }
            }
            catch (Exception e)
            {
                GeneralFunctions.installmorpho(rlFirstContainer,this);
            }

        }
    }
    //FB49E3BD9962E089246F11BDC8D29258D306ED1358E8E87258151FFE7E436A0E
    //fb49e3bd9962e089246f11bdc8d29258d306ed1358e8e87258151ffe7e436a0e


    //b22d3ae8454b63d70db2140d2dfc982f438c52480e3c38502698e0bbae983a72
    //2.1FYYNN

    //AE156E2FB4A7262D96D949ACCAE915A98EDCF77256C8BF4EFE454A81D34C7EFF
    //2.0FYYNN  - 8898E5AADD5941891DDCDD2B5F26E966E05C005CBFAE94556B7D3B2C922FEBA5
    private void startCapture(String s, String s1)
    {

        wadh="MglkKtwVbnGyKvqcYO4XXIfaxYmXgxev2R2B3nqeLnc=";
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
                        GeneralFunctions.tokenExpireAction(FirstSurveyActivity.this);
                    }
                    else if(response.body().success==1)
                    {
                        GeneralFunctions.makeSnackbar(rlFirstContainer,response.body().data.kyc.poi.name);
                        mPrefs.save(Constants.IS_AADHAR_AUTHENTICATED,true);
                        Intent intent=new Intent(FirstSurveyActivity.this, StaticFormActivity.class).putExtra("adharAuthenticated",true);
                        startActivity(intent);
                        overridePendingTransition( R.anim.animateup_activity, R.anim.animatedown_activity );
                        // hitSurveyApiForMyself();
                    }
                    else{
                        //todo:  revert 0 to 1 to swith to auth if kyc failed
                        //kycFailed=1;
                        kycFailed=0;
                        GeneralFunctions.makeSnackbar(rlFirstContainer,response.body().message);
                    }
                }
                else {
                    GeneralFunctions.makeSnackbar(rlFirstContainer,getResources().getString(R.string.serverIssue));
                }
            }

            @Override
            public void onFailure(Call<KycModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(rlFirstContainer,getResources().getString(R.string.netIssue));
            }
        });

    }

    public void setFamily(int i)
    {
        fragment.setFamilyFurther(i);
    }

    public void setSomeoneElse()
    {
        fragment.setSomeoneElse();
    }
}
