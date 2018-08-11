package easygov.saral.harlabh.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import easygov.saral.harlabh.R;
import easygov.saral.harlabh.fragments.CreatePassword;
import easygov.saral.harlabh.fragments.ForgotPasswordFragment;
import easygov.saral.harlabh.fragments.OtpVerifyFragment;
import easygov.saral.harlabh.fragments.SetPhoneEmailFragment;
import easygov.saral.harlabh.fragments.SignUpAadhar;
import easygov.saral.harlabh.fragments.SignUpPhoneFragment;
import easygov.saral.harlabh.models.responsemodels.generalmodel.GeneralModel;
import easygov.saral.harlabh.utils.Constants;
import easygov.saral.harlabh.utils.GeneralFunctions;
import easygov.saral.harlabh.utils.MyApplication;
import easygov.saral.harlabh.utils.Prefs;
import easygov.saral.harlabh.utils.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SignActivity extends AppCompatActivity implements View.OnClickListener  {


    private int userType=1;
    private String loginType;
    private Prefs mPrefs;
    private TextView tvWrong,tvWhyAadhaar,tvWhyNumber,tvNextSignUp,tvGoSignIn,tvSignUp,tvForgot;
    private TextInputEditText etAdhaar,etPassword;
    private TextView tvSignIn;
    private RadioGroup rgCategory;
    private TextInputLayout tilNo,tilPass;
    private CardView cvSelectAadhaar,cvSelectPhone;
    private RelativeLayout rlSignUp,rlSignIn,rlSignActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        init();
        setInnitialScreen();
        clickHandlers();
    }



    private void setInnitialScreen()
   {
       if(mPrefs.getBoolean(Constants.isSigned,false))
       {

           if(mPrefs.getString(Constants.SetPhone,"").equals("YES"))
           {
               getSupportFragmentManager().beginTransaction().add(R.id.rlSignActivity,new SetPhoneEmailFragment()).addToBackStack(null).commit();
           }
         else   if(!mPrefs.getBoolean(Constants.Location,false) ||
                   !mPrefs.getString(Constants.preLang,"").equals(mPrefs.getString(Constants.Language,"")))
           {
               mPrefs.save(Constants.preLang,mPrefs.getString(Constants.Language,""));

               Intent intent =new Intent(this,StateSelectActivity.class);
               //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
               startActivity(intent);
               SignActivity.this.finish();
           }
           else {
               mPrefs.save(Constants.preLang,mPrefs.getString(Constants.Language,""));
               Intent intent =new Intent(this,HomeActivity.class);
              // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
               startActivity(intent);
               SignActivity.this.finish();
           }
       } 
   }


    private void initiatePopupWindowWhyDescription(View view, String title, String text, int why_img) {
        final PopupWindow pw;
        try {
            //We need to get the instance of the LayoutInflater, use the context of this activity
            LayoutInflater inflater = (LayoutInflater)this
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

    private void init()
    {
        etAdhaar= findViewById(R.id.etAdhaar);
        etPassword= findViewById(R.id.etPassword);
        tvSignIn= findViewById(R.id.tvSignIn);
        tvWrong= findViewById(R.id.tvWrong);
        rgCategory= findViewById(R.id.rgCategory);
        cvSelectAadhaar= findViewById(R.id.cvSelectAadhaar);
        cvSelectPhone= findViewById(R.id.cvSelectPhone);
        tvWhyAadhaar= findViewById(R.id.tvWhyAadhaar);
        tvWhyNumber= findViewById(R.id.tvWhyNumber);
        tvNextSignUp= findViewById(R.id.tvNextSignUp);
        tvGoSignIn= findViewById(R.id.tvGoSignIn);
        rlSignUp= findViewById(R.id.rlSignUp);
        rlSignIn= findViewById(R.id.rlSignIn);
        tvSignUp= findViewById(R.id.tvSignUp);
        tilNo= findViewById(R.id.tilNo);
        tilPass= findViewById(R.id.tilPass);
        mPrefs=Prefs.with(this);
        rlSignActivity= findViewById(R.id.rlSignActivity);
        userType=0;
        tvForgot= findViewById(R.id.tvForgot);
    }

    private void clickHandlers() {
        tvSignIn.setOnClickListener(this);
        tvSignUp.setOnClickListener(this);
        tvGoSignIn.setOnClickListener(this);
        cvSelectAadhaar.setOnClickListener(this);
        cvSelectPhone.setOnClickListener(this);
        tvNextSignUp.setOnClickListener(this);
        tvWhyAadhaar.setOnClickListener(this);
        tvWhyNumber.setOnClickListener(this);
        rlSignActivity.setOnClickListener(this);
        tvForgot.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tvSignIn) {
            if (checkSignInDetails(v)) {
                signInApi();
            }

        } else if (i == R.id.tvSignUp) {
            if (mPrefs.getString(Constants.enable_signup_aadhaar, "").equals("1")) {
                rlSignIn.setVisibility(View.GONE);
                tvWrong.setVisibility(View.INVISIBLE);
                rlSignUp.setVisibility(View.VISIBLE);
                mPrefs.save(Constants.isReset, "N");
                tilNo.setErrorEnabled(false);
                tilPass.setErrorEnabled(false);
            } else {
                mPrefs.save(Constants.isReset, "N");
                tilNo.setErrorEnabled(false);
                tilPass.setErrorEnabled(false);
                getSupportFragmentManager().beginTransaction().add(R.id.rlSignActivity, new SignUpPhoneFragment()).addToBackStack(null).commit();
            }

        } else if (i == R.id.tvGoSignIn) {
            rlSignUp.setVisibility(View.GONE);
            rlSignIn.setVisibility(View.VISIBLE);

        } else if (i == R.id.cvSelectAadhaar) {
            cvSelectAadhaar.setCardBackgroundColor(Color.WHITE);
            cvSelectPhone.setCardBackgroundColor(ContextCompat.getColor(this, R.color.CardUnselected));
            mPrefs.save(Constants.FromAadhar, "YES");
            //mPrefs.save(Constants.FromAadhar,true);
            loginType = "aadhaar";

        } else if (i == R.id.cvSelectPhone) {
            cvSelectAadhaar.setCardBackgroundColor(ContextCompat.getColor(this, R.color.CardUnselected));
            cvSelectPhone.setCardBackgroundColor(Color.WHITE);
            mPrefs.save(Constants.FromAadhar, "NO");
            // mPrefs.save(Constants.FromAadhar,false);
            loginType = "phone";

        } else if (i == R.id.tvNextSignUp) {
            SignUpAadhar frag = new SignUpAadhar();
            Bundle bundle = new Bundle();

            bundle.putString("userType", String.valueOf(userType));
            if (loginType == "aadhaar") {
                bundle.putString("loginType", loginType);
                frag.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().add(R.id.rlSignActivity, frag).addToBackStack(null).commit();
            } else if (loginType == "phone") {

                getSupportFragmentManager().beginTransaction().add(R.id.rlSignActivity, new SignUpPhoneFragment()).addToBackStack(null).commit();
            } else {
                GeneralFunctions.makeSnackbar(rlSignActivity, getResources().getString(R.string.loginmode));
            }

        } else if (i == R.id.tvWhyAadhaar) {
            initiatePopupWindowWhyDescription(v, getString(R.string.whyaadhaar), getString(R.string.whyUseAadhaar), R.drawable.why_aadhaar_01);

        } else if (i == R.id.tvWhyNumber) {
            initiatePopupWindowWhyDescription(v, getString(R.string.whynumber), getString(R.string.whyuseNumber), R.drawable.why_number_01);

        } else if (i == R.id.rlSignActivity) {
            GeneralFunctions.hideSoftKeyboard(SignActivity.this);

        } else if (i == R.id.tvForgot) {
            getSupportFragmentManager().beginTransaction().add(R.id.rlSignActivity, new ForgotPasswordFragment()).addToBackStack(null).commit();

        }

    }

    private void signInApi() {
        tvWrong.setVisibility(View.INVISIBLE);
        GeneralFunctions.showDialog(this);

        if(etAdhaar.getText().toString().length()==12)
        {
            mPrefs.save(Constants.FromAadhar,"YES");
        }
        else {
            mPrefs.save(Constants.FromAadhar,"NO");
        }

        mPrefs.save(Constants.IS_USER_FIRST_TIME,"no");
        Call<GeneralModel> call = RestClient.get().signIn(GeneralFunctions.getIMEINumber(this),GeneralFunctions.getDeviceId(this),etAdhaar.getText().toString().toString(),etPassword.getText().toString(), MyApplication.Client);
        call.enqueue(new Callback<GeneralModel>() {
            @Override
            public void onResponse(Call<GeneralModel> call, Response<GeneralModel> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful())
                {
                    if(response.body().success==1)
                    {
                        Intent intent =new Intent(SignActivity.this,StateSelectActivity.class);
                        mPrefs.save(Constants.isSigned,true);
                        mPrefs.save(Constants.Auth, String.valueOf(response.body().data.token));
                        mPrefs.save(Constants.proFileDisplayNumber,etAdhaar.getText().toString().toString());
                        //GeneralFunctions.firstCheck(mPrefs,etAdhaar.getText().toString());
                        if(response.body().data.is_aadhaar==1)
                            mPrefs.save(Constants.FromAadhar,"YES");

                        else
                            mPrefs.save(Constants.FromAadhar,"NO");
                        startActivity(intent);
                        finish();
                    }
                    else {
                        tvWrong.setVisibility(View.VISIBLE);
                        tvWrong.setVisibility(View.VISIBLE);
                    }
                }
                else
                {
                    GeneralFunctions.makeSnackbar(rlSignActivity, getResources().getString(R.string.serverIssue));
                }
            }

            @Override
            public void onFailure(Call<GeneralModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(rlSignActivity, getResources().getString(R.string.netIssue));
            }
        });
    }

    private boolean checkSignInDetails(View v)
    {
        tvWrong.setVisibility(View.INVISIBLE);
        if(etAdhaar.getText().toString().toString().isEmpty()&&etPassword.getText().toString().isEmpty())
        {
            initiatePopupWindowNoSign(v,"","");
            return false;
        }
      else   if(etAdhaar.getText().toString().toString().isEmpty())
        {
            tilNo.setErrorEnabled(true);
            tilNo.setError(getResources().getString(R.string.phn));
            tilPass.setErrorEnabled(false);


            return false;
        }
      else   if(etPassword.getText().toString().toString().isEmpty())
        {
            tilNo.setErrorEnabled(false);
            tilPass.setErrorEnabled(true);
            tilPass.setError(getResources().getString(R.string.enterPass));
            return false;
        }
        if(etAdhaar.getText().toString().length()==10||etAdhaar.getText().toString().length()==12)
        {
            tvWrong.setVisibility(View.INVISIBLE);
            tvWrong.setText(getResources().getString(R.string.invalid));
            return true;
        }
        else {
            tvWrong.setVisibility(View.VISIBLE);
            tvWrong.setText(getResources().getString(R.string.invalidnumber));
            return false;
        }
    }

    private void initiatePopupWindowNoSign(View view,String title,String text) {
        final PopupWindow pw;
        try {
            LayoutInflater inflater = (LayoutInflater) this
                    .getSystemService(LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.info_no_sign,
                    (ViewGroup)view.findViewById(R.id.rlNoSign));
            pw = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
            pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
            TextView tvIllSign= layout.findViewById(R.id.tvIllSign);
            TextView tvSkipPopUp= layout.findViewById(R.id.tvSkipPopUp);
            TextView tvSignPopUp= layout.findViewById(R.id.tvSignPopUp);
        tvIllSign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pw.dismiss();
                    if(mPrefs.getString(Constants.enable_signup_aadhaar,"").equals("1")) {
                        rlSignIn.setVisibility(View.GONE);
                        tvWrong.setVisibility(View.INVISIBLE);
                        rlSignUp.setVisibility(View.VISIBLE);
                        mPrefs.save(Constants.isReset, "N");
                        tilNo.setErrorEnabled(false);
                        //tilNo.setError("a");
                        tilPass.setErrorEnabled(false);
                        //tilPass.setError("dismiss ");
                    }

                    else {
                        mPrefs.save(Constants.isReset, "N");
                        tilNo.setErrorEnabled(false);
                        //tilNo.setError("a");
                        tilPass.setErrorEnabled(false);
                        //tilPass.setError("dismiss else");
                        getSupportFragmentManager().beginTransaction().add(R.id.rlSignActivity,new SignUpPhoneFragment()).addToBackStack(null).commit();

                    }


                }
            });
            tvSignPopUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pw.dismiss();

                }
            });

            tvSkipPopUp .setOnClickListener(new View.OnClickListener() {
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

    public void resetFlow(String string)
    {
        OtpVerifyFragment fragment=new OtpVerifyFragment();
        Bundle bundle=new Bundle();
        if(string.length()==10) {
            bundle.putString("phone", string);
            bundle.putString("aadhaar_id","none");
        }
        else {
            bundle.putString("phone", "none");
            bundle.putString("aadhaar_id",string);}
        mPrefs.save(Constants.isReset,"Y");
        mPrefs.save(Constants.proFileDisplayNumber,string);
        //GeneralFunctions.firstCheck(mPrefs,string);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.rlSignActivity,fragment).addToBackStack(null).commit();
    }

    public void setPassword()
    {
        mPrefs.save(Constants.FromOtp,"NO");
        getSupportFragmentManager().beginTransaction().add(R.id.rlSignActivity,new CreatePassword()).addToBackStack(null).commit();
    }

    public void phoneToAadhaar()
    {
        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager().popBackStack();

        getSupportFragmentManager().beginTransaction().add(R.id.rlSignActivity,new SignUpAadhar()).addToBackStack(null).commit();

    }

    public void alreadyExistSignIn()
    {
        rlSignUp.setVisibility(View.GONE);
        rlSignIn.setVisibility(View.VISIBLE);
    }


}
