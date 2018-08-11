package easygov.saral.harlabh.fragments;


import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import easygov.saral.harlabh.activity.SignActivity;
import easygov.saral.harlabh.models.responsemodels.aadhaarwebviewmodels.AadhaarWebviewModel;
import easygov.saral.harlabh.utils.GeneralFunctions;
import easygov.saral.harlabh.utils.MyApplication;
import easygov.saral.harlabh.utils.Prefs;
import easygov.saral.harlabh.utils.RestClient;
import easygov.saral.harlabh.R;
import easygov.saral.harlabh.models.responsemodels.generalmodel.GeneralModel;
import easygov.saral.harlabh.utils.Constants;
import easygov.saral.harlabh.utils.SmsListener;
import easygov.saral.harlabh.utils.SmsReceiver;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class OtpVerifyFragment extends Fragment implements View.OnClickListener{


    private EditText etOtp1, etOtp2, etOtp3, etOtp4, etOtp5, etOtp6,etOtpNew;
    private TextView tvResendOtp, tvVerify, tvTimer, tvSignAdh,tvOtpText, tvSignAdhHyper,tvOr,tvProblem;
    private StringBuilder sb;
    private ImageView ivVerifyOtpBack;
    private RelativeLayout rlOtp,rlProb;
    private Snackbar bar;
    private Prefs mPrefs;
    private String isReset;
    private int isAadhaar=0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up_phone, container, false);

        init(view);


       // otpVerifiedLogic();
        Bundle bundle =new Bundle();
        bundle=getArguments();
        if(bundle!=null)
        {
            if(bundle.getString("phone")!=null&&bundle.getString("phone").length()==10) {
                String phone = bundle.getString("phone");
                isAadhaar=0;
                tvOtpText.setText(getResources().getString(R.string.otpsents) + " "+phone);
                if(mPrefs.getString(Constants.proFileDisplayNumber,"").length()==10 && mPrefs.getString(Constants.enable_signup_aadhaar,"").equals("1")) {
                    rlProb.setVisibility(View.VISIBLE);
                    tvOr.setVisibility(View.VISIBLE);
                    tvProblem.setVisibility(View.VISIBLE);
                }
            }

            else if(bundle.getString("aadhaar_id")!=null&&bundle.getString("aadhaar_id").length()==12)
            {

                isAadhaar=1;
                rlProb.setVisibility(View.GONE);
                tvOr.setVisibility(View.GONE);
                tvProblem.setVisibility(View.GONE);
            }
        }
        clickHandlers();
        return view;
    }




    private void clickHandlers() {
        tvVerify.setOnClickListener(this);
        ivVerifyOtpBack.setOnClickListener(this);
        rlOtp.setOnClickListener(this);
       /* tvResendOtp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    tvResendOtp.setTextColor(ContextCompat.getColor(getActivity(), R.color.grey));
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    tvResendOtp.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                }
                return false;
            }
        });*/

       tvResendOtp.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
              // mobileVerification(1);

                if(mPrefs.getString(Constants.isReset,"").equals("Y"))
                resetapi();
                    //hitConfirmPassApi();

               else
                   if(mPrefs.getString(Constants.proFileDisplayNumber,"").length()==10)
                   getOtpApi();
               else {
                   getAadhaarOtpapi();
                   }

           }
       });

    }

    private void getAadhaarOtpapi() {
        GeneralFunctions.showDialog(getActivity());
        Call<AadhaarWebviewModel> call = RestClient.get().signUpAadhaar(mPrefs.getString(Constants.proFileDisplayNumber,""));
        call.enqueue(new Callback<AadhaarWebviewModel>() {
            @Override
            public void onResponse(Call<AadhaarWebviewModel> call, Response<AadhaarWebviewModel> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful())
                {
                    if(response.body().success==1)
                    {
                        tvResendOtp.setVisibility(View.GONE);
                        tvTimer.setVisibility(View.VISIBLE);
                        setTimer();
                        // mPrefs.save(Constants.MobileNumber,etphnNo.getText().toString());
                        //mPrefs.save(Constants.proFileDisplayNumber,etphnNo.getText().toString().toString());
                        //getFragmentManager().beginTransaction().add(R.id.rlPhoneSignIn,new OtpVerifyFragment()).addToBackStack(null).commit();

                    }

                    else  {

                        bar = Snackbar.make(rlOtp, response.body().message, Snackbar.LENGTH_LONG)
                                .setAction("Dismiss", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // Handle user action
                                        bar.dismiss();
                                    }
                                });

                        bar.show();}
                }

                else {

                    bar = Snackbar.make(rlOtp, getResources().getString(R.string.serverIssue), Snackbar.LENGTH_LONG)
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
            public void onFailure(Call<AadhaarWebviewModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();

                bar = Snackbar.make(rlOtp, getResources().getString(R.string.netIssue), Snackbar.LENGTH_LONG)
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


    private void init(View view) {
        sb = new StringBuilder();
        etOtp1 = view.findViewById(R.id.etOtp1);
        etOtp2 = view.findViewById(R.id.etOtp2);
        etOtp3 = view.findViewById(R.id.etOtp3);
        etOtp4 = view.findViewById(R.id.etOtp4);
        etOtp5 = view.findViewById(R.id.etOtp5);
        etOtp6 = view.findViewById(R.id.etOtp6);
        etOtpNew= view.findViewById(R.id.etOtpNew);
        ivVerifyOtpBack = view.findViewById(R.id.ivVerifyOtpBack);
        tvResendOtp = view.findViewById(R.id.tvResendOtp);
        tvVerify = view.findViewById(R.id.tvVerify);
        tvTimer = view.findViewById(R.id.tvTimer);
        tvOtpText= view.findViewById(R.id.tvOtpText);
        tvSignAdh = view.findViewById(R.id.tvSignAdh);
        tvOr= view.findViewById(R.id.tvOr);
        tvProblem= view.findViewById(R.id.tvProblem);
        tvSignAdhHyper= view.findViewById(R.id.tvSignAdhHyper);
        rlOtp = view.findViewById(R.id.rlOtp);
        rlProb = view.findViewById(R.id.rlProb);


        mPrefs=Prefs.with(getActivity());
        tvTimer.setText("1:00");
       //setTextViewColor(tvSignAdh, tvSignAdh.getText().toString(), "Aadhar number", ContextCompat.getColor(getActivity(), R.color.Sign));
        isReset=mPrefs.getString(Constants.isReset,"");

        tvSignAdhHyper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPrefs.getString(Constants.enable_signup_aadhaar,"").equals("1"))
                ((SignActivity)getActivity()).phoneToAadhaar();

                else {
                }
            }
        });




    }

   /* private void setTextViewColor(TextView view, String fulltext, String subtext, int color) {
        view.setText(fulltext, TextView.BufferType.SPANNABLE);
        Spannable str = (Spannable) view.getText();
        int i = fulltext.indexOf(subtext);
        str.setSpan(new ForegroundColorSpan(color), 14, 28, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 14, 28, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }*/

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTimer();
        SmsReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                try {
                    if(messageText.length()==6)
                    {
                        etOtpNew.setText(messageText);
                    }
                }
                catch (Exception e)
                {

                }

            }
        });


    }

    private void setTimer() {
        new CountDownTimer(61000, 1000) {

            public void onTick(long millisUntilFinished) {

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
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tvVerify) {
            if (etOtpNew.getText().toString().length() == 6) {

                if (isAadhaar == 0) {
                    tvVerify.setClickable(false);
                    mobileVerification(0);
                } else {

                    if (isReset.equals("Y")) {
                        hitOtpResetVerify(etOtpNew.getText().toString());
                    } else
                        hitConfirmPassApi();
                }
            } else {
                tvVerify.setClickable(true);
                bar = Snackbar.make(rlOtp, getResources().getString(R.string.validotp), Snackbar.LENGTH_LONG)
                        .setAction("Dismiss", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Handle user action
                                bar.dismiss();
                            }
                        });

                bar.show();
            }

        } else if (i == R.id.ivVerifyOtpBack) {
            getFragmentManager().popBackStack();

        } else if (i == R.id.rlOtp) {
            GeneralFunctions.hideSoftKeyboard(getActivity());

        }
    }

    private void mobileVerification(final int i)
    {
        GeneralFunctions.showDialog(getActivity());
        Call<GeneralModel> call = RestClient.get().mobileVerification(GeneralFunctions.getIMEINumber(getActivity()),GeneralFunctions.getDeviceId(getActivity()),mPrefs.getString(Constants.MobileNumber,""),etOtpNew.getText().toString(),isReset, MyApplication.Client);
        call.enqueue(new Callback<GeneralModel>() {
            @Override
            public void onResponse(Call<GeneralModel> call, Response<GeneralModel> response) {

                tvVerify.setClickable(true);
                GeneralFunctions.dismissDialog();
                if (response.isSuccessful()) {
                    if (response.body().success == 1) {
                        mPrefs.save(Constants.Auth, String.valueOf(response.body().data.token));
                        getFragmentManager().beginTransaction().replace(R.id.rlOtp, new CreatePassword()).addToBackStack(null).commit();



                    } else {

                        bar = Snackbar.make(rlOtp, response.body().data.message, Snackbar.LENGTH_LONG)
                                .setAction("Dismiss", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        bar.dismiss();
                                    }
                                });
                        bar.show();
                    }

                } else {
                    String s= null;
                    try {
                        s = getResources().getString(R.string.serverIssue);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    bar = Snackbar.make(rlOtp,s, Snackbar.LENGTH_LONG)
                            .setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    bar.dismiss();
                                }
                            });
                    bar.show();
                }
            }

            @Override
            public void onFailure(Call<GeneralModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                tvVerify.setClickable(true);
                bar=Snackbar.make(rlOtp,getResources().getString(R.string.netIssue),Snackbar.LENGTH_LONG).setAction("Dismiss", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bar.dismiss();
                    }
                });
                bar.show();
            }
        });

        }


    private void getOtpApi()
    {

        GeneralFunctions.showDialog(getActivity());
        Call<GeneralModel> call = RestClient.get().signup(mPrefs.getString(Constants.proFileDisplayNumber,""));
        call.enqueue(new Callback<GeneralModel>() {
            @Override
            public void onResponse(Call<GeneralModel> call, Response<GeneralModel> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful())
                {
                    if(response.body().success==1)
                    {
                        tvResendOtp.setVisibility(View.GONE);
                        tvTimer.setVisibility(View.VISIBLE);
                        setTimer();
                       // mPrefs.save(Constants.MobileNumber,etphnNo.getText().toString());
                        //mPrefs.save(Constants.proFileDisplayNumber,etphnNo.getText().toString().toString());
                        //getFragmentManager().beginTransaction().add(R.id.rlPhoneSignIn,new OtpVerifyFragment()).addToBackStack(null).commit();

                    }

                    else  {

                        bar = Snackbar.make(rlOtp, response.body().message, Snackbar.LENGTH_LONG)
                                .setAction("Dismiss", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // Handle user action
                                        bar.dismiss();
                                    }
                                });

                        bar.show();}
                }

                else {

                    bar = Snackbar.make(rlOtp, getResources().getString(R.string.serverIssue), Snackbar.LENGTH_LONG)
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

                bar = Snackbar.make(rlOtp, getResources().getString(R.string.netIssue), Snackbar.LENGTH_LONG)
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


    private Map<String,String> map;
    private void hitConfirmPassApi()
    {
        map=new HashMap<>();
        //tvWrongForgot.setVisibility(View.INVISIBLE);
       // mPrefs.save(Constants.MobileNumber,etAadharOrPhn.getText().toString());
        /*if(isAadhaar==0)
        {
            map.put("mobile",mPrefs.getString(Constants.proFileDisplayNumber,""));
        }
        else {
            map.put("aadhaar_id",mPrefs.getString(Constants.proFileDisplayNumber,""));
            map.put("via","otp_auth");
            map.put("otp",etOtpNew.getText().toString());
            map.put("transaction_id",mPrefs.getString(Constants.OtpTransId,""));
            map.put("enc_pid","");
        }*/
        GeneralFunctions.showDialog(getActivity());
        Call<GeneralModel> call = RestClient.get().aadhaarworkaround(mPrefs.getString(Constants.proFileDisplayNumber,""),
                "otp_kyc","",mPrefs.getString(Constants.OtpTransId,""),etOtpNew.getText().toString());
        call.enqueue(new Callback<GeneralModel>() {
            @Override
            public void onResponse(Call<GeneralModel> call, Response<GeneralModel> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful())
                {
                    if(response.body().success==1)
                    {
                        tvResendOtp.setVisibility(View.GONE);
                        tvTimer.setVisibility(View.VISIBLE);
                        mPrefs.save(Constants.Auth,response.body().data.token);
                        setTimer();
                        getFragmentManager().beginTransaction().replace(R.id.rlOtp, new CreatePassword()).addToBackStack(null).commit();
                        // mPrefs.save(Constants.MobileNumber,etphnNo.getText().toString());
                        //mPrefs.save(Constants.proFileDisplayNumber,etphnNo.getText().toString().toString());
                        //getFragmentManager().beginTransaction().add(R.id.rlPhoneSignIn,new OtpVerifyFragment()).addToBackStack(null).commit();

                    }

                    else  {

                        bar = Snackbar.make(rlOtp, response.body().message, Snackbar.LENGTH_LONG)
                                .setAction("Dismiss", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // Handle user action
                                        bar.dismiss();
                                    }
                                });

                        bar.show();}
                }

                else {

                    bar = Snackbar.make(rlOtp, getResources().getString(R.string.serverIssue), Snackbar.LENGTH_LONG)
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

                bar = Snackbar.make(rlOtp, getResources().getString(R.string.netIssue), Snackbar.LENGTH_LONG)
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


    private void hitOtpResetVerify(String otp) {
        GeneralFunctions.showDialog(getActivity());
        Call<GeneralModel> call =RestClient.get().reset(mPrefs.getString(Constants.proFileDisplayNumber,""),"verify_otp","",mPrefs.getString(Constants.OtpTransId,""),otp);
        call.enqueue(new Callback<GeneralModel>() {
            @Override
            public void onResponse(Call<GeneralModel> call, Response<GeneralModel> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful())
                {
                    if(response.body().success==1)
                    {
                        mPrefs.save(Constants.Auth,response.body().data.token);

                        getFragmentManager().beginTransaction().add(R.id.rlOtp,new CreatePassword()).addToBackStack(null).commit();
                        // getFragmentManager().beginTransaction().replace(R.id.rlAadhaarSignIn, new ConfirmationFragment()).addToBackStack(null).commit();
                        // mPrefs.save(Constants.OtpFrom, "aadhar");
                    }
                    else GeneralFunctions.makeSnackbar(rlOtp,response.body().message);
                }
                else {
                    GeneralFunctions.makeSnackbar(rlOtp,getResources().getString(R.string.serverIssue));
                }
            }

            @Override
            public void onFailure(Call<GeneralModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(rlOtp,getResources().getString(R.string.netIssue));

            }
        });

    }

    private void resetapi() {

        GeneralFunctions.showDialog(getActivity());
        Map<String,String> map1 =new HashMap<>();
        if(isAadhaar==0)
        {
            map1.put("mobile",mPrefs.getString(Constants.proFileDisplayNumber,""));
        }
        else {
            map1.put("aadhaar_id",mPrefs.getString(Constants.proFileDisplayNumber,""));
            map1.put("via","otp_auth");
            map1.put("otp",etOtpNew.getText().toString());
            map1.put("transaction_id",mPrefs.getString(Constants.OtpTransId,""));
            map1.put("enc_pid","");
        }
        Call<GeneralModel> call = RestClient.get().resetPassword(map1);
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
                    else if (response.body().success==1)
                    {
                       /* tvResendOtp.setVisibility(View.GONE);
                        tvTimer.setVisibility(View.VISIBLE);
                        mPrefs.save(Constants.Auth,response.body().data.token);*/
                        tvResendOtp.setVisibility(View.GONE);
                        tvTimer.setVisibility(View.VISIBLE);
                        setTimer();
                        //GeneralFunctions.makeSnackbar(rlOtp,response.body().message);
                        // getFragmentManager().beginTransaction().replace(R.id.rlOtp, new CreatePassword()).addToBackStack(null).commit();
                    }
                    else {
                        GeneralFunctions.makeSnackbar(rlOtp,response.body().message);
                    }
                }
                else {
                    GeneralFunctions.makeSnackbar(rlOtp,getResources().getString(R.string.serverIssue));
                }
            }

            @Override
            public void onFailure(Call<GeneralModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(rlOtp,getResources().getString(R.string.netIssue));
            }
        });
    }


}

