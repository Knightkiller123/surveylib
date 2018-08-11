package easygov.saral.harlabh.fragments;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.regex.Pattern;

import easygov.saral.harlabh.models.responsemodels.aadhaarwebviewmodels.AadhaarWebviewModel;
import easygov.saral.harlabh.models.responsemodels.generalmodel.GeneralModel;
import easygov.saral.harlabh.utils.Constants;
import easygov.saral.harlabh.utils.GeneralFunctions;
import easygov.saral.harlabh.utils.Prefs;
import easygov.saral.harlabh.utils.RestClient;
import easygov.saral.harlabh.utils.VerhoeffAlgorithm;
import easygov.saral.harlabh.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class SignUpAadhar extends Fragment implements View.OnClickListener {

    private TextInputEditText etAadharNo;
    private TextView tvNextAadhar,tvTncEasy,tvPrivacyEasy,tvHint,tvExistaadhaar;
    private String userType,loginType;
    private TextInputLayout tilAadhar;
    private ImageView ivAadharBack;
    private RelativeLayout rlAadhaarSignIn,rlWebviewContainer;
    private Prefs mPrefs;
    private View popView;
    private int kycFailed=0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_sign_in_aadhar, container, false);
        popView=view;
        init(view);
        Bundle bundle =new Bundle();
        bundle=getArguments();
        if(bundle!=null)
        {
            userType =bundle.getString("userType");
            loginType=bundle.getString("loginType");

           /* if(loginType.equals("aadhaar"))
            {
                tvHint.setText(getResources().getString(R.string.enteraadhaar));
                etAadharNo.setHint("Aadhar Number");
            }
            else if(loginType.equals("phone"))
            {
                tvHint.setText(getResources().getString(R.string.enterphone));
                etAadharNo.setHint("Phone Number");
            }*/


        }
        clickHandlers();
        setOtherListeners();
        return view;
    }



    private void setOtherListeners() {
        etAadharNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(etAadharNo, InputMethodManager.SHOW_IMPLICIT);
                    if(etAadharNo.getText().toString().isEmpty())
                    {
                        //tilNo.setHint("Enter Aadhaar/Phone Number");
                        tilAadhar.setHint(getString(R.string.aadhaarno));
                        Drawable drawable = etAadharNo.getBackground(); // get current EditText drawable
                        drawable.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
                        etAadharNo.setBackground(drawable);

                        etAadharNo.setHint("");
                    }
                    else {
                        tilAadhar.setHint(getString(R.string.ad_phn));
                        etAadharNo.setHint("");
                    }

                }
                else {
                    if(etAadharNo.getText().toString().isEmpty())
                    {
                        tilAadhar.setHint("");
                        etAadharNo.setHint(getResources().getString(R.string.enterad_phn));
                        Drawable drawable = etAadharNo.getBackground(); // get current EditText drawable
                        drawable.setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                        etAadharNo.setHintTextColor(ContextCompat.getColor(getContext(),R.color.red));
                        etAadharNo.setBackground(drawable);
                    }

                    else {
                        tilAadhar.setHint("");
                    }

                }
            }
        });
    }

    private void clickHandlers() {
        tvNextAadhar.setOnClickListener(this);
        ivAadharBack.setOnClickListener(this);
        rlAadhaarSignIn.setOnClickListener(this);

        tvTncEasy.setOnClickListener(this);
        tvPrivacyEasy.setOnClickListener(this);
    }

    private void init(View view) {
        etAadharNo= view.findViewById(R.id.etAadharNo);
        tvNextAadhar= view.findViewById(R.id.tvNextAadhar);
        tvTncEasy= view.findViewById(R.id.tvTncEasy);

        tvExistaadhaar= view.findViewById(R.id.tvExistaadhaar);

        tvPrivacyEasy= view.findViewById(R.id.tvPrivacyEasy);
        tvHint= view.findViewById(R.id.tvHint);
        rlWebviewContainer= view.findViewById(R.id.rlWebviewContainer);
        ivAadharBack= view.findViewById(R.id.ivAadharBack);
        tilAadhar= view.findViewById(R.id.tilAadhar);
        rlAadhaarSignIn= view.findViewById(R.id.rlAadhaarSignIn);
        etAadharNo.requestFocus();
        mPrefs=Prefs.with(getActivity());
        mPrefs.save(Constants.IS_USER_FIRST_TIME,"yes");

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tvNextAadhar) {
            if (validateAadharNumber(etAadharNo.getText().toString())) {

               /* getFragmentManager().beginTransaction().replace(R.id.rlAadhaarSignIn, new ConfirmationFragment()).addToBackStack(null).commit();
                mPrefs.save(Constants.OtpFrom, "aadhar");*/

                //Todo: FLOW uncomment below line to resume flow
                // hitOtpApi(etAadharNo.getText().toString());

                initiatePopupWindow(popView);

                //   setWebview("https://www.google.com",1,etAadharNo.getText().toString());
            } else {
                setAadharListener();
            }

        } else if (i == R.id.ivAadharBack) {
            getFragmentManager().popBackStack();

        } else if (i == R.id.rlAadhaarSignIn) {
            GeneralFunctions.hideSoftKeyboard(getActivity());

        } else if (i == R.id.tvTncEasy) {//setWebview("https://www.easygov.co.in/terms-and-conditions/","Terms and Conditions");

        } else if (i == R.id.tvPrivacyEasy) {//setWebview("https://www.easygov.co.in/privacy/","Privacy Policy");

        }
    }


    private void setWebview(String s,String heading) {

        Bundle bundle = new Bundle();
        bundle.putString("url", s);
        bundle.putString("heading", heading);
        StaticWebFragment fragobj = new StaticWebFragment();
        fragobj.setArguments(bundle);
        getFragmentManager().beginTransaction().add(R.id.rlAadhaarSignIn, fragobj).addToBackStack(null).commit();
        // animatemenuUp();


    }

    public  boolean validateAadharNumber(String aadharNumber){
        Pattern aadharPattern = Pattern.compile("\\d{12}");
        boolean isValidAadhar = aadharPattern.matcher(aadharNumber).matches();
        if(isValidAadhar){
            isValidAadhar = VerhoeffAlgorithm.validateVerhoeff(aadharNumber);
        }

        if(isValidAadhar)
        {
            tilAadhar.setErrorEnabled(false);
            /*tilAadhar.setHint("");
            etAadharNo.setText("");
            etAadharNo.setHint("Enter Valid Aadhar Number");
            Drawable drawable = etAadharNo.getBackground(); // get current EditText drawable
            drawable.setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            etAadharNo.setHintTextColor(ContextCompat.getColor(getContext(),R.color.red));
            etAadharNo.setBackground(drawable);*/
        }
        mPrefs.save(Constants.proFileDisplayNumber,etAadharNo.getText().toString().toString());
       // GeneralFunctions.firstCheck(mPrefs,etAadharNo.getText().toString());
        return isValidAadhar;
    }

    private void setAadharListener()
    {
        tilAadhar.setEnabled(true);
        tilAadhar.setError(getResources().getString(R.string.plvalidaadhaar));
        etAadharNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()!=12)
                {
                    tilAadhar.setEnabled(true);
                    tilAadhar.setError(getResources().getString(R.string.plvalidaadhaar));
                }

                else {
                    validateAadharNumber(s.toString());
                  //  tilAadhar.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void hitOtpApi(String aadhaarId) {
        GeneralFunctions.showDialog(getActivity());
        Call<AadhaarWebviewModel> call = RestClient.get().signUpAadhaar(aadhaarId);
        call.enqueue(new Callback<AadhaarWebviewModel>() {
            @Override
            public void onResponse(Call<AadhaarWebviewModel> call, Response<AadhaarWebviewModel> response) {
                GeneralFunctions.dismissDialog();
                if (response.isSuccessful()) {

                    if (response.body().success == 1) {

                        selectMethotPop.dismiss();
                        mPrefs.save(Constants.proFileDisplayNumber,etAadharNo.getText().toString().toString());
                        //GeneralFunctions.firstCheck(mPrefs,etAadharNo.getText().toString());
                        mPrefs.save(Constants.AadhaarNo,etAadharNo.getText().toString());
                        GeneralFunctions.hideSoftKeyboard(getActivity());
                        mPrefs.save(Constants.OtpTransId,response.body().data.transaction_id);
                        Bundle bundle = new Bundle();
                        bundle.putString("aadhaar_id",etAadharNo.getText().toString().toString() );
                        bundle.putString("phone","none" );
                        mPrefs.save(Constants.isReset,"N");
                        OtpVerifyFragment frag=new OtpVerifyFragment();
                        frag.setArguments(bundle);
                        getFragmentManager().beginTransaction().add(R.id.rlAadhaarSignIn,frag).addToBackStack(null).commit();

                    }
                    else if(response.body().code.equals("406"))
                    {
                        GeneralFunctions.makeSnackbar(rlAadhaarSignIn,response.body().message);
                        selectMethotPop.dismiss();
                    }

                    else {
                        selectMethotPop.dismiss();
                        GeneralFunctions.otherMethodsSign(popView,getActivity(),"Getting Stuck ? \n Sign Up using Phone Number",getFragmentManager());

                    }
                }
                else GeneralFunctions.makeSnackbar(rlAadhaarSignIn,getResources().getString(R.string.serverIssue));
            }

            @Override
            public void onFailure(Call<AadhaarWebviewModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(rlAadhaarSignIn,getResources().getString(R.string.netIssue));
            }
        });
    }
    private PopupWindow  selectMethotPop;
    RadioButton rbOtp,rbIris,rbFinger;
    private int type=1;
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
                    initiatePopupWindow(view,getString(R.string.otpmethod),getString(R.string.otpinfo),R.drawable.otp_01);
                }
            });

            ivFingerInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initiatePopupWindow(view,getString(R.string.fingerprint),getString(R.string.fingerInfo),R.drawable.finger_print_01);
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

                            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS)
                                    == PackageManager.PERMISSION_GRANTED) {

                                hitOtpApi(etAadharNo.getText().toString());
                            }
                            else {
                                requestPermissions(
                                        new String[]{Manifest.permission.READ_SMS},
                                        120);
                            }
                            break;

                        case 2:
//                            String dynamic_wadh = ""
                            try {
                                String wadh= "MglkKtwVbnGyKvqcYO4XXIfaxYmXgxev2R2B3nqeLnc=";
                                // String s= "1.0";

                                //wadh="AE156E2FB4A7262D96D949ACCAE915A98EDCF77256C8BF4EFE454A81D34C7EFF"
                                String responseXml="";
                                Intent intent1 = new Intent("in.gov.uidai.rdservice.fp.INFO");
                                intent1.setPackage("com.scl.rdservice");
                                startActivityForResult(intent1, 1200);
                            }
                            catch (Exception e)
                            {
                                GeneralFunctions.installmorpho(view,getActivity());

                            }




                            selectMethotPop.dismiss();
                            break;

                        case 3:
                            GeneralFunctions.makeSnackbar(rlAadhaarSignIn,getResources().getString(R.string.irisnotsupported));

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



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case 120:

                hitOtpApi(etAadharNo.getText().toString());
        }
    }

    private PopupWindow otpPopup;
    private EditText etOtpNew;



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            if(requestCode==1000) {
               // selectMethotPop.dismiss();
                Bundle b = data.getExtras();



                if (b != null)
                {
                    String pidData = b.getString("PID_DATA");
                    String s2= pidData.replaceAll("DeviceInfo","DeviceData");
                    String dnc = b.getString("DNC", "");
                    String dnr = b.getString("DNR", "");
                    s2=GeneralFunctions.removeStr(s2);
                    if(dnc.equals(""))
                    {
                        GeneralFunctions.makeSnackbar(rlAadhaarSignIn,getResources().getString(R.string.fingercaptured));

                        if(kycFailed==0)
                            hitFingerApiKyc(s2);

                        else
                        hitFingerApiAuth(s2);
                    }
                    else
                        GeneralFunctions.makeSnackbar(rlAadhaarSignIn,getResources().getString(R.string.nofingerprint));

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
                    asd = GeneralFunctions.createAuthXMLRegistered(deviceInfo);

                    if (asd.size() > 1) {
                        if (!asd.get(0).equals("error")) {
                            if (asd.get(0) != null && asd.get(1) != null && asd.get(0).length() > 1 && asd.get(1).length() > 1) {
                                startCapture(asd.get(0), asd.get(1));
                            }
                        }
                    }
                }
                else {
                    GeneralFunctions.makeSnackbar(rlAadhaarSignIn,getResources().getString(R.string.nodevice));

                }

            }
        }
        catch (Exception e)
        {
            GeneralFunctions.installmorpho(rlAadhaarSignIn,getActivity());
        }

    }
        }}

    private void startCapture(String s, String s1)
    {
       String wadh="MglkKtwVbnGyKvqcYO4XXIfaxYmXgxev2R2B3nqeLnc=";
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

    private void hitFingerApiAuth(String pidData) {
        GeneralFunctions.showDialog(getActivity());
        Call<GeneralModel> call =RestClient.get().aadhaarworkaround(etAadharNo.getText().toString(),"fmr_auth",pidData,
                mPrefs.getString(Constants.OtpTransId,""),"");
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
                        getFragmentManager().beginTransaction().add(R.id.rlAadhaarSignIn,new CreatePassword()).addToBackStack(null).commit();

                    }
                    else {
                        selectMethotPop.dismiss();
                        GeneralFunctions.otherMethodsSign(popView,getActivity(),"Getting Stuck ? \n Sign Up using Phone Number", getFragmentManager());

                    }
                }
                else {
                    GeneralFunctions.makeSnackbar(rlAadhaarSignIn,getResources().getString(R.string.serverIssue));
                }
            }

            @Override
            public void onFailure(Call<GeneralModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(rlAadhaarSignIn,getResources().getString(R.string.netIssue));
            }
        });

    }

    private void hitFingerApiKyc(String pidData) {
        GeneralFunctions.showDialog(getActivity());
        Call<GeneralModel> call =RestClient.get().aadhaarworkaround(etAadharNo.getText().toString(),"fmr_kyc",pidData,
                mPrefs.getString(Constants.OtpTransId,""),"");
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
                        getFragmentManager().beginTransaction().add(R.id.rlAadhaarSignIn,new CreatePassword()).addToBackStack(null).commit();

                    }
                    else {
                        //todo:  revert 1 to 0 to swith to auth if kyc failed
                        kycFailed=0;
                        GeneralFunctions.makeSnackbar(rlAadhaarSignIn,response.body().message);
                    }
                }
                else {
                    GeneralFunctions.makeSnackbar(rlAadhaarSignIn,getResources().getString(R.string.serverIssue));
                }
            }

            @Override
            public void onFailure(Call<GeneralModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(rlAadhaarSignIn,getResources().getString(R.string.netIssue));
            }
        });

    }


    private void initiatePopupWindow(View view,String title,String text,int why_img) {
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


}
