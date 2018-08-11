package easygov.saral.harlabh.fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.io.IOException;

import easygov.saral.harlabh.activity.SignActivity;
import easygov.saral.harlabh.utils.Prefs;
import easygov.saral.harlabh.utils.RestClient;
import easygov.saral.harlabh.R;
import easygov.saral.harlabh.models.responsemodels.generalmodel.GeneralModel;
import easygov.saral.harlabh.utils.Constants;
import easygov.saral.harlabh.utils.GeneralFunctions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpPhoneFragment extends Fragment implements View.OnClickListener {


    private EditText etphnNo;
    private TextView tvNextPhone,tvTncEasyPhn,tvPrivacyEasyPhn,tvExist,tvHint,tvExist1;
    private ImageView ivPhoneSignBack;
    private TextInputLayout tilPhn;
    private RelativeLayout rlPhoneSignIn;
    private Snackbar bar;
    private Prefs mPrefs;
   // private int code=0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_sign_up_phone2, container, false);
        init(view);


        clickHandlers();
        return view;
    }

    private void clickHandlers() {
        tvNextPhone.setOnClickListener(this);
        ivPhoneSignBack.setOnClickListener(this);
        rlPhoneSignIn.setOnClickListener(this);
        tvTncEasyPhn.setOnClickListener(this);
        tvPrivacyEasyPhn.setOnClickListener(this);
        tvExist.setOnClickListener(this);
    }



    private void init(View view) {
        tvNextPhone= view.findViewById(R.id.tvNextPhone);
        tilPhn= view.findViewById(R.id.tilPhn);
        tvTncEasyPhn= view.findViewById(R.id.tvTncEasyPhn);
        tvPrivacyEasyPhn= view.findViewById(R.id.tvPrivacyEasyPhn);
        ivPhoneSignBack= view.findViewById(R.id.ivPhoneSignBack);
        tvExist= view.findViewById(R.id.tvExist);
        tvExist1= view.findViewById(R.id.tvExist1);
        etphnNo= view.findViewById(R.id.etphnNo);
        rlPhoneSignIn= view.findViewById(R.id.rlPhoneSignIn);
        tvHint= view.findViewById(R.id.tvHint);
        etphnNo.requestFocus();
        mPrefs=Prefs.with(getActivity());
        mPrefs.save(Constants.IS_USER_FIRST_TIME,"yes");
    }

    private void setPhoneListener()
    {
        //tilPhn.setEnabled(true);
       // tilPhn.setError("Invalid Number");
        etphnNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()!=10&&s.length()>0)
                {
                    tilPhn.setEnabled(true);
                    tilPhn.setError(getResources().getString(R.string.invalidnumber));
                   /* if(code==1)
                    {
                        tilPhn.setEnabled(true);
                        tilPhn.setError("User Already Exist");
                        code=0;
                    }
                    else {
                        tilPhn.setEnabled(true);
                        tilPhn.setError("Invalid Number");
                    }*/
                }
                else
                {
                    tilPhn.setErrorEnabled(false);

                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tvNextPhone) {
            if (!etphnNo.getText().toString().isEmpty()) {
                if (etphnNo.getText().toString().length() == 10) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS)
                            == PackageManager.PERMISSION_GRANTED) {

                        getOtpApi();
                    } else {
                        requestPermissions(
                                new String[]{Manifest.permission.READ_SMS},
                                120);
                    }

                } else {
                    tilPhn.setEnabled(true);
                    tilPhn.setError(getResources().getString(R.string.invalidnumber));
                    setPhoneListener();
                }

            } else {
                GeneralFunctions.makeSnackbar(rlPhoneSignIn, getResources().getString(R.string.enterNumber));
            }

        } else if (i == R.id.ivPhoneSignBack) {
            GeneralFunctions.hideSoftKeyboard(getActivity());
            getFragmentManager().popBackStack();

        } else if (i == R.id.rlPhoneSignIn) {
            GeneralFunctions.hideSoftKeyboard(getActivity());
            etphnNo.clearFocus();

        } else if (i == R.id.tvTncEasyPhn) {//setWebview("https://www.easygov.co.in/terms-and-conditions/","Terms and Conditions");

        } else if (i == R.id.tvPrivacyEasyPhn) {//setWebview("https://www.easygov.co.in/privacy/","Privacy Policy");

        } else if (i == R.id.tvExist) {
            getFragmentManager().popBackStack();
            ((SignActivity) getContext()).alreadyExistSignIn();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case 120:
                getOtpApi();
        }
    }
    private void getOtpApi()
    {

        GeneralFunctions.showDialog(getActivity());
        Call<GeneralModel> call = RestClient.get().signup(etphnNo.getText().toString());
        call.enqueue(new Callback<GeneralModel>() {
            @Override
            public void onResponse(Call<GeneralModel> call, Response<GeneralModel> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful())
                {
                    if(response.body().success==1)
                    {
                        Bundle bundle = new Bundle();
                        bundle.putString("phone",etphnNo.getText().toString().toString() );
                        bundle.putString("aadhaar_id","none" );
                        mPrefs.save(Constants.isReset,"N");
                        OtpVerifyFragment frag=new OtpVerifyFragment();
                        frag.setArguments(bundle);
                       // GeneralFunctions.firstCheck(mPrefs,etphnNo.getText().toString());
                        mPrefs.save(Constants.MobileNumber,etphnNo.getText().toString());
                        mPrefs.save(Constants.proFileDisplayNumber,etphnNo.getText().toString().toString());
                        getFragmentManager().beginTransaction().add(R.id.rlPhoneSignIn,frag).addToBackStack(null).commit();

                    }

                    else  {
                        if(response.body().data.next_link.equals("forget_password"))
                        {
                            tvHint.setVisibility(View.GONE);
                            tvExist.setVisibility(View.VISIBLE);
                            tvExist1.setVisibility(View.VISIBLE);
                            tilPhn.setEnabled(true);
                            tilPhn.setError(response.body().message);

                            setPhoneListener();
                             }
                        else if(response.body().data.next_link.equals("create_password")){
                            ((SignActivity)getContext()).resetFlow(etphnNo.getText().toString());
                        }
                    }
                }

                    else {

                    bar = Snackbar.make(rlPhoneSignIn, getResources().getString(R.string.serverIssue), Snackbar.LENGTH_LONG)
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

                    bar = Snackbar.make(rlPhoneSignIn, getResources().getString(R.string.netIssue), Snackbar.LENGTH_LONG)
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

    private void setWebview(String s,String heading) {
        Bundle bundle = new Bundle();
        bundle.putString("url", s);
        bundle.putString("heading", heading);
        StaticWebFragment fragobj = new StaticWebFragment();
        fragobj.setArguments(bundle);
        getFragmentManager().beginTransaction().add(R.id.rlPhoneSignIn, fragobj).addToBackStack(null).commit();
        // animatemenuUp();


    }
}
