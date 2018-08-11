package easygov.saral.harlabh.fragments;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import easygov.saral.harlabh.activity.SignActivity;
import easygov.saral.harlabh.models.responsemodels.generalmodel.GeneralModel;
import easygov.saral.harlabh.utils.Constants;
import easygov.saral.harlabh.utils.GeneralFunctions;
import easygov.saral.harlabh.utils.Prefs;
import easygov.saral.harlabh.utils.RestClient;
import easygov.saral.harlabh.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */

//todo: etAadharOrPhn length set to 10 statically change it to 12
public class ForgotPasswordFragment extends Fragment implements View.OnClickListener {


    private ImageView ivForgotBack;
    private TextInputEditText etAadharOrPhn;
    private TextView tvForgotContinue,tvWrongForgot;
    private Snackbar bar;
    private RelativeLayout rlForgotCont;
    private TextInputLayout tilForgot;
    private Prefs mPrefs;
    private Map<String,String> map;
    private int kycFailed=0;
    private List<String> user =new ArrayList<>();
    private PopupWindow selectMethotPop;
    private RadioButton rbOtp,rbIris,rbFinger;
    private int type=1;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_forgot_password, container, false);
        init(view);
        clickHandlers();
        return view;
    }

    private void clickHandlers() {
        ivForgotBack.setOnClickListener(this);
        tvForgotContinue.setOnClickListener(this);
        rlForgotCont.setOnClickListener(this);
    }

    private void init(View view)
    {
        ivForgotBack= view.findViewById(R.id.ivForgotBack);
        tvWrongForgot= view.findViewById(R.id.tvWrongForgot);
        etAadharOrPhn= view.findViewById(R.id.etAadharOrPhn);
        tvForgotContinue= view.findViewById(R.id.tvForgotContinue);
        rlForgotCont= view.findViewById(R.id.rlForgotCont);
        tilForgot= view.findViewById(R.id.tilForgot);
        etAadharOrPhn.requestFocus();
        map=new HashMap<>();
        mPrefs=Prefs.with(getActivity());
        mPrefs.save(Constants.IS_USER_FIRST_TIME,"no");
        if(!mPrefs.getString(Constants.enable_signup_aadhaar,"").equals("1"))
        {
            etAadharOrPhn.setHint(getResources().getString(R.string.phn));
            // etAadharOrPhn.max
        }

        else {
            etAadharOrPhn.setHint(getResources().getString(R.string.ad_phn));
        }


        etAadharOrPhn.setHint(getResources().getString(R.string.phn));


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case 120:
                hitConfirmPassApi();
        }
    }



    /*@Override
    public void onStart() {
        super.onStart();
        smsVerifyCatcher.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        smsVerifyCatcher.onStart();
    }*/

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.ivForgotBack) {
            getFragmentManager().popBackStack();

        } else if (i == R.id.tvForgotContinue) {
            if (etAadharOrPhn.getText().length() == 10) {
                map.clear();
                map.put("mobile", etAadharOrPhn.getText().toString());
                mPrefs.save(Constants.proFileDisplayNumber, etAadharOrPhn.getText().toString());
                // GeneralFunctions.firstCheck(mPrefs,etAadharOrPhn.getText().toString());
                mPrefs.save(Constants.FromAadhar, "NO");
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS)
                        == PackageManager.PERMISSION_GRANTED) {

                    hitConfirmPassApi();
                } else {
                    requestPermissions(
                            new String[]{Manifest.permission.READ_SMS},
                            120);
                }

            } else if (etAadharOrPhn.getText().length() == 12 && mPrefs.getString(Constants.enable_signup_aadhaar, "").equals("1")) {
                map.clear();
                map.put("aadhaar_id", etAadharOrPhn.getText().toString());
                mPrefs.save(Constants.proFileDisplayNumber, etAadharOrPhn.getText().toString());
                //GeneralFunctions.firstCheck(mPrefs,etAadharOrPhn.getText().toString());
                mPrefs.save(Constants.FromAadhar, "YES");
                initiatePopupWindow(v);
                // hitConfirmPassApi();
            } else {
                tilForgot.setErrorEnabled(true);
                tilForgot.setError(getResources().getString(R.string.invalidnumber));
                setListener();
            }

        } else if (i == R.id.rlForgotCont) {
            GeneralFunctions.hideSoftKeyboard(getActivity());

        }
    }

    private void setListener() {
        etAadharOrPhn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()==10||s.length()==12)
                {
                    tilForgot.setErrorEnabled(false);
                }

                else {
                    tilForgot.setErrorEnabled(true);
                    tilForgot.setError(getResources().getString(R.string.invalidnumber));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void hitConfirmPassApi()
    {
        tvWrongForgot.setVisibility(View.INVISIBLE);
        mPrefs.save(Constants.MobileNumber,etAadharOrPhn.getText().toString());
        GeneralFunctions.showDialog(getActivity());
        Call<GeneralModel> call = RestClient.get().resetPassword(map);
        call.enqueue(new Callback<GeneralModel>() {
            @Override
            public void onResponse(Call<GeneralModel> call, Response<GeneralModel> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful())
                {
                    if(response.body().code.equals("401"))
                    {
                        GeneralFunctions.tokenExpireAction(getActivity());
                    }
                    else if(response.body().success==1)
                    {
                        getFragmentManager().popBackStack();
                        mPrefs.save(Constants.proFileDisplayNumber,etAadharOrPhn.getText().toString().toString());
                        // GeneralFunctions.firstCheck(mPrefs,etAadharOrPhn.getText().toString());
                        ((SignActivity) getActivity()).resetFlow(etAadharOrPhn.getText().toString());


                    }
                    else {
                        tvWrongForgot.setVisibility(View.VISIBLE);
                        tvWrongForgot.setText(response.body().message);
                    }

                }
                else {

                    bar = Snackbar.make(rlForgotCont, getResources().getString(R.string.serverIssue), Snackbar.LENGTH_LONG)
                            .setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Handle user action
                                    bar.dismiss();
                                }
                            });

                    bar.show();
                }

            }

            @Override
            public void onFailure(Call<GeneralModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                bar = Snackbar.make(rlForgotCont, getResources().getString(R.string.netIssue), Snackbar.LENGTH_LONG)
                        .setAction("Dismiss", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Handle user action
                                bar.dismiss();
                            }
                        });

                bar.show();
            }

        });
    }


    private void hitAadhaarcaseotp()
    {
        tvWrongForgot.setVisibility(View.INVISIBLE);
        map.put("via","generate_otp");
        map.put("otp","");
        map.put("transaction_id","");
        map.put("enc_pid","");


        mPrefs.save(Constants.MobileNumber,etAadharOrPhn.getText().toString());
        GeneralFunctions.showDialog(getActivity());
        Call<GeneralModel> call = RestClient.get().resetPassword(map);
        call.enqueue(new Callback<GeneralModel>() {
            @Override
            public void onResponse(Call<GeneralModel> call, Response<GeneralModel> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful())
                {
                    if(response.body().code.equals("401"))
                    {
                        GeneralFunctions.tokenExpireAction(getActivity());
                    }
                    else if(response.body().success==1) {
                        getActivity().getSupportFragmentManager().popBackStack();
                        mPrefs.save(Constants.OtpTransId,response.body().data.transaction_id);

                        mPrefs.save(Constants.proFileDisplayNumber,etAadharOrPhn.getText().toString().toString());
                        //GeneralFunctions.firstCheck(mPrefs,etAadharOrPhn.getText().toString());
                        mPrefs.save(Constants.OtpTransId,response.body().data.transaction_id);
                        //Todo: try to remember why same conditions are there , what is missing

                        ((SignActivity) getActivity()).resetFlow(etAadharOrPhn.getText().toString());

                    }
                    else {
                        tvWrongForgot.setVisibility(View.VISIBLE);
                        tvWrongForgot.setText(response.body().message);
                    }

                }
                else {

                    bar = Snackbar.make(rlForgotCont, getResources().getString(R.string.serverIssue), Snackbar.LENGTH_LONG)
                            .setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Handle user action
                                    bar.dismiss();
                                }
                            });

                    bar.show();
                }

            }

            @Override
            public void onFailure(Call<GeneralModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                bar = Snackbar.make(rlForgotCont, getResources().getString(R.string.netIssue), Snackbar.LENGTH_LONG)
                        .setAction("Dismiss", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Handle user action
                                bar.dismiss();
                            }
                        });

                bar.show();
            }

        });
    }



    public void initiatePopupWindow(final View view) {

        try {
            //We need to get the instance of the LayoutInflater, use the context of this activity
            type=1;
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
            //Inflate the view from a predefined XML layout
            View layout = inflater.inflate(R.layout.info_selectmethod_popup,
                    (ViewGroup)view.findViewById(R.id.popup_selectMethod));
            // create a 300px width and 470px height PopupWindow
            selectMethotPop = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
            // display the popup in the center
            selectMethotPop.showAtLocation(layout, Gravity.CENTER, 0, 0);
            // TextView tvGot= (TextView) layout.findViewById(R.id.tvGot);
            RelativeLayout rlAuthMethodNextPopUp= layout.findViewById(R.id.rlAuthMethodNextPopUp);
            //TextView tvMethodDesc= (TextView) layout.findViewById(R.id.tvMethodDesc);
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
            // tvMethodDesc.setText(text);

            // tvInfoMethod.setText(title);

            RadioGroup rgBiometric = layout.findViewById(R.id.rgBiometric);
            rbOtp = layout.findViewById(R.id.rbOtp);
            rbIris = layout.findViewById(R.id.rbIris);
            rbFinger = layout.findViewById(R.id.rbFinger);

            rbOtp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        type = 1;
                        rbFinger.setChecked(false);
                        rbIris.setChecked(false);
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
                    }
                }
            });
            /*rgBiometric.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                    switch (checkedId)
                    {
                        case R.id.rbIris:
                            rbOtp.setChecked(false);

                            type=2;
                            break;
                        case R.id.rbFinger:
                            rbOtp.setChecked(false);

                            type=3;
                            break;
                    }
                }
            });*/
            ivVerifyBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectMethotPop.dismiss();
                }
            });
            rlAuthMethodNextPopUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //setWebview(3);
                    switch (type)
                    {
                        case 1:
                            //initiateOtpPopupWindow(view);
                            //hitConfirmPassApi();
                            if (etAadharOrPhn.getText().toString().length()==12)
                            {
                                selectMethotPop.dismiss();
                                hitAadhaarcaseotp();

                            }
                            else
                                hitConfirmPassApi();
                            break;

                        case 2:
                            try {
                                Intent intent1 = new Intent("in.gov.uidai.rdservice.fp.INFO");
                                intent1.setPackage("com.scl.rdservice");
                                startActivityForResult(intent1, 1200);
                                selectMethotPop.dismiss();
                            }
                            catch (Exception e)
                            {
                                GeneralFunctions.installmorpho(view,getActivity());
                            }
                            break;

                        case 3:
                            GeneralFunctions.makeSnackbar(rlForgotCont,getResources().getString(R.string.irisnotsupported));
                            break;

                        default:

                    }
                    //selectMethotPop.dismiss();
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
            LayoutInflater inflater = (LayoutInflater) getActivity()
                    .getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            if(requestCode==1000) {
                selectMethotPop.dismiss();
                Bundle b = data.getExtras();


                if (b != null)
                {
// in this variable you will get Pid data
                    String pidData = b.getString("PID_DATA");
                    String s2= pidData.replaceAll("DeviceInfo","DeviceData");
// you will get value in this variable when your finger print device not connected
                    String dnc = b.getString("DNC", "");
                    s2=GeneralFunctions.removeStr(s2);
// you will get value in this variable when your finger print device not registered.
                    String dnr = b.getString("DNR", "");

                    if(dnc.equals(""))
                    {
                        GeneralFunctions.makeSnackbar(rlForgotCont,getResources().getString(R.string.fingercaptured));
                        if(kycFailed==0)
                            hitFingerApiKyc(s2);

                        else
                            hitFingerApiAuth(s2);
                    }
                    else
                        GeneralFunctions.makeSnackbar(rlForgotCont,getResources().getString(R.string.nofingerprint));
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
                            GeneralFunctions.makeSnackbar(rlForgotCont,getResources().getString(R.string.nodevice));
                        }
                    }
                }
                catch (Exception e)
                {
                    GeneralFunctions.installmorpho(rlForgotCont,getActivity());
                }


            }
        }}


    // MglkKtwVbnGyKvqcYO4XXIfaxYmXgxev2R2B3nqeLnc= // 2.1FYYNY  sha256 - base64
    private void startCapture(String s, String s1)
    {
        String responseXml="";
        if(kycFailed==0)
            responseXml = "<PidOptions ver=\"1.0\"><Opts env=\"P\" fCount=\"1\" fType=\"0\" " +
                    "format=\"0\" pidVer=\"2.0\" timeout=\"10000\" wadh=\"MglkKtwVbnGyKvqcYO4XXIfaxYmXgxev2R2B3nqeLnc=\" posh=\"UNKNOWN\"/><Demo></Demo>" +
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

    private void hitFingerApiKyc(String pidData) {
        GeneralFunctions.showDialog(getActivity());
        Call<GeneralModel> call =RestClient.get().reset(etAadharOrPhn.getText().toString(),"fmr_kyc",pidData,"","");
        call.enqueue(new Callback<GeneralModel>() {
            @Override
            public void onResponse(Call<GeneralModel> call, Response<GeneralModel> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful())
                {
                    if(response.body().success==1)
                    {
                        mPrefs.save(Constants.Auth,response.body().data.token);
                        mPrefs.save(Constants.MyAadhaar,new Gson().toJson(response.body().data.kyc));
                        getFragmentManager().beginTransaction().add(R.id.rlForgotCont,new CreatePassword()).addToBackStack(null).commit();
                        // getFragmentManager().beginTransaction().replace(R.id.rlAadhaarSignIn, new ConfirmationFragment()).addToBackStack(null).commit();
                        // mPrefs.save(Constants.OtpFrom, "aadhar");
                    }
                    else {
                        kycFailed=0;
                        GeneralFunctions.makeSnackbar(rlForgotCont,response.body().message);

                    }
                }
                else {
                    GeneralFunctions.makeSnackbar(rlForgotCont,getResources().getString(R.string.serverIssue));

                }
            }

            @Override
            public void onFailure(Call<GeneralModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(rlForgotCont,getResources().getString(R.string.netIssue));
            }
        });

    }

    private void hitFingerApiAuth(String pidData) {
        GeneralFunctions.showDialog(getActivity());
        Call<GeneralModel> call =RestClient.get().reset(etAadharOrPhn.getText().toString(),"fmr",pidData,"","");
        call.enqueue(new Callback<GeneralModel>() {
            @Override
            public void onResponse(Call<GeneralModel> call, Response<GeneralModel> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful())
                {
                    if(response.body().success==1)
                    {
                        mPrefs.save(Constants.Auth,response.body().data.token);
                        mPrefs.save(Constants.MyAadhaar,new Gson().toJson(response.body().data.kyc));
                        getFragmentManager().beginTransaction().add(R.id.rlForgotCont,new CreatePassword()).addToBackStack(null).commit();
                        // getFragmentManager().beginTransaction().replace(R.id.rlAadhaarSignIn, new ConfirmationFragment()).addToBackStack(null).commit();
                        // mPrefs.save(Constants.OtpFrom, "aadhar");
                    }
                    else {
                        kycFailed=0;
                        GeneralFunctions.makeSnackbar(rlForgotCont,response.body().message);
                    }
                }
                else {
                    GeneralFunctions.makeSnackbar(rlForgotCont,getResources().getString(R.string.serverIssue));
                }
            }

            @Override
            public void onFailure(Call<GeneralModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(rlForgotCont, getResources().getString(R.string.netIssue));
            }
        });

    }

}
