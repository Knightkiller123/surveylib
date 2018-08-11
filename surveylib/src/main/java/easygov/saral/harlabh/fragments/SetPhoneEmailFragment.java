package easygov.saral.harlabh.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.regex.Pattern;

import easygov.saral.harlabh.R;
import easygov.saral.harlabh.activity.BeneficiaryActivity;
import easygov.saral.harlabh.activity.StateSelectActivity;
import easygov.saral.harlabh.models.responsemodels.generalmodel.GeneralModel;
import easygov.saral.harlabh.utils.Constants;
import easygov.saral.harlabh.utils.GeneralFunctions;
import easygov.saral.harlabh.utils.Prefs;
import easygov.saral.harlabh.utils.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SetPhoneEmailFragment extends Fragment {



    private EditText etNumber,etEmail;
    private TextView etDone;
    private Prefs mPrefs;
    private ImageView ivDetails;
    private Pattern ePat;
    private String phone,email;
    private LinearLayout llSetEmailPhn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_set_phone_email, container, false);
        Bundle bundle =new Bundle();
        bundle=getArguments();
        if(bundle!=null)
        {
            phone="";
            email="";
            try {
                phone =bundle.getString("phone");
                email=bundle.getString("email");
            }
            catch (Exception e)
            {}


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
        init(view);
        return view;
    }

    private void init(View view) {
        etNumber= view.findViewById(R.id.etNumber);
        etEmail= view.findViewById(R.id.etEmail);
        etDone= view.findViewById(R.id.etDone);
        ivDetails= view.findViewById(R.id.ivDetails);
        llSetEmailPhn= view.findViewById(R.id.llSetEmailPhn);
        ePat=android.util.Patterns.EMAIL_ADDRESS;
        ivDetails.setVisibility(View.GONE);

        if(email!=null&&email.length()>1)
        {
            etEmail.setText(email);
        }

        if(phone!=null&& phone.length()>1)
        {
            etNumber.setText(phone);
        }
       /* ivDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiatePopupWindow(v);
            }
        });*/
        mPrefs=Prefs.with(getActivity());

        if(Integer.parseInt(mPrefs.getString(Constants.ISEmailMandatory,""))==0)
        {
        etEmail.setHint(getResources().getString(R.string.emailnonmandatory));
        }
        llSetEmailPhn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                GeneralFunctions.hideSoftKeyboard(getActivity());
                return false;
            }
        });
        etDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkValidate())
                {
                    mPrefs.save(Constants.StaticEmail,etEmail.getText().toString());
                    mPrefs.save(Constants.Phone,etNumber.getText().toString());
                    setPhnEmail(etNumber.getText().toString(),etEmail.getText().toString());
                }
            }
        });

        ivDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private boolean checkValidate() {
        if(etNumber.getText().toString().isEmpty())
        {
            GeneralFunctions.makeSnackbar(llSetEmailPhn,getResources().getString(R.string.plphone));
            return false;
        }
        else if(etNumber.getText().toString().length()!=10)
        {
            GeneralFunctions.makeSnackbar(llSetEmailPhn,getResources().getString(R.string.invalidnumber));
            return false;
        }

        if(!etEmail.getText().toString().isEmpty())
        {
            if(!android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches())
            {
                GeneralFunctions.makeSnackbar(llSetEmailPhn,getResources().getString(R.string.plemail));
                return false;
            }
        }

        if(Integer.parseInt(mPrefs.getString(Constants.ISEmailMandatory,""))==1)
        {
            if(etEmail.getText().toString().isEmpty())
            {
                GeneralFunctions.makeSnackbar(llSetEmailPhn,getResources().getString(R.string.plem));
                return false;
            }
            if(!android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches())
            {
                GeneralFunctions.makeSnackbar(llSetEmailPhn,getResources().getString(R.string.plemail));
                return false;
            }

        }
        return true;

    }

    private void setPhnEmail(String phn,String email)
    {
        GeneralFunctions.showDialog(getActivity());
        Call<GeneralModel> call= RestClient.get().setPhoneEmail(phn,email);
        call.enqueue(new Callback<GeneralModel>() {
            @Override
            public void onResponse(Call<GeneralModel> call, Response<GeneralModel> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful())
                {
                  if (response.body().success==1)
                  {
                      mPrefs.save(Constants.SetPhone,"NO");
                      Intent intent = new Intent(getActivity(), StateSelectActivity.class);
                      startActivity(intent);
                      getActivity().finish();
                  }
                  else {
                      GeneralFunctions.makeSnackbar(llSetEmailPhn,response.body().message);
                  }
                }
                else
                {
                    GeneralFunctions.makeSnackbar(llSetEmailPhn,getResources().getString(R.string.serverIssue));
                }
            }

            @Override
            public void onFailure(Call<GeneralModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(llSetEmailPhn,getResources().getString(R.string.netIssue));
            }
        });
    }

   private PopupWindow selectMethotPop;
    public void initiatePopupWindow(View view) {

        try {
            //We need to get the instance of the LayoutInflater, use the context of this activity
            LayoutInflater inflater = (LayoutInflater) getActivity()
                    .getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
            //Inflate the view from a predefined XML layout
            View layout = inflater.inflate(R.layout.infomoveout,
                    (ViewGroup)view.findViewById(R.id.rlnotEligible1));
            // create a 300px width and 470px height PopupWindow
            selectMethotPop = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
            // display the popup in the center
            selectMethotPop.showAtLocation(layout, Gravity.CENTER, 0, 0);
            // TextView tvGot= (TextView) layout.findViewById(R.id.tvGot);
           // RelativeLayout rlAuthMethodNextPopUp= (RelativeLayout) layout.findViewById(R.id.rlAuthMethodNextPopUp);
            TextView tvOkLeave= layout.findViewById(R.id.tvOkLeave);
            TextView tvCancel= layout.findViewById(R.id.tvCancel);
           // ImageView ivVerifyBack= (ImageView) layout.findViewById(R.id.ivVerifyBack);

            // tvMethodDesc.setText(text);

            // tvInfoMethod.setText(title);
          /*  ivVerifyBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectMethotPop.dismiss();
                }
            });*/
            /*rlAuthMethodNextPopUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //setWebview(3);

                    selectMethotPop.dismiss();
                }
            });*/

            tvOkLeave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), StateSelectActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            });
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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

     @Override
    public void onResume() {
        super.onResume();

        if(getView() == null){
            return;
        }

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){

                   // initiatePopupWindow(v);
                   getActivity().finish();
                    return true;
                }
                return false;
            }
        });
    }

}
