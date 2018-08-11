package easygov.saral.harlabh.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;

import easygov.saral.harlabh.activity.BeneficiaryActivity;
import easygov.saral.harlabh.activity.StateSelectActivity;
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
public class CreatePassword extends Fragment implements View.OnClickListener {


private TextInputEditText etPass,etCnfrmPass;
    private TextView tvFinalNext,tvStartIntoApp;
    private ImageView ivPassBAck;
    private RelativeLayout rlChangePassword ,rlAwesome;
    private Prefs mPrefs;
    private Snackbar bar;

private Response<GeneralModel> response1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_create_password, container, false);
        init(view);
        clickHandlers();
        return view;
    }

    private void clickHandlers() {
        tvFinalNext.setOnClickListener(this);
        rlChangePassword.setOnClickListener(this);
        tvStartIntoApp.setOnClickListener(this);
        ivPassBAck.setOnClickListener(this);
    }

    private void init(View view) {
        etPass= view.findViewById(R.id.etPass);
        etCnfrmPass= view.findViewById(R.id.etCnfrmPass);
        tvFinalNext= view.findViewById(R.id.tvFinalNext);
        tvStartIntoApp= view.findViewById(R.id.tvStartIntoApp);

        ivPassBAck= view.findViewById(R.id.ivPassBAck);
        rlChangePassword= view.findViewById(R.id.rlChangePassword);
        rlAwesome= view.findViewById(R.id.rlAwesome);


        mPrefs=Prefs.with(getActivity());
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tvFinalNext) {
            if (etPass.getText().toString().isEmpty()) {
                GeneralFunctions.makeSnackbar(rlChangePassword, getResources().getString(R.string.plpass));
            } else if (etCnfrmPass.getText().toString().isEmpty()) {
                GeneralFunctions.makeSnackbar(rlChangePassword, getResources().getString(R.string.confirmpass));
            } else if (etPass.getText().toString().trim().isEmpty()) {
                GeneralFunctions.makeSnackbar(rlChangePassword, getResources().getString(R.string.invalid_password));
            } else if (etPass.getText().toString().length() < 6) {
                GeneralFunctions.makeSnackbar(rlChangePassword, getResources().getString(R.string.short_pwd));
            } else if (!etPass.getText().toString().equals(etCnfrmPass.getText().toString())) {
                GeneralFunctions.makeSnackbar(rlChangePassword, getResources().getString(R.string.passnomatch));
            } else {
                setPassword();
            }


            if (!etPass.getText().toString().isEmpty()) {
                if (!etCnfrmPass.getText().toString().isEmpty()) {
                    if (etPass.getText().toString().equals(etCnfrmPass.getText().toString())) {
                        mPrefs.save(Constants.Password, etPass.getText().toString());
                        setPassword();
                    } else {
                        GeneralFunctions.makeSnackbar(rlChangePassword, getResources().getString(R.string.passnomatch));

                    }
                } else {
                    GeneralFunctions.makeSnackbar(rlChangePassword, getResources().getString(R.string.confirmpass));

                }
            } else {
                GeneralFunctions.makeSnackbar(rlChangePassword, getResources().getString(R.string.plpass));
            }


        } else if (i == R.id.ivPassBAck) {
            getFragmentManager().popBackStack();

        } else if (i == R.id.rlChangePassword) {
            GeneralFunctions.hideSoftKeyboard(getActivity());

        } else if (i == R.id.tvStartIntoApp) {
            gotoApp(response1);

            // rlAwesome.setVisibility(View.GONE);

        }
    }

    private void setPassword()
    {
        GeneralFunctions.showDialog(getActivity());
        Call<GeneralModel> call = RestClient.get().setPassword(etPass.getText().toString());
        call.enqueue(new Callback<GeneralModel>() {
            @Override
            public void onResponse(Call<GeneralModel> call, Response<GeneralModel> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful())
                {
                    if(response.body().success==1)
                    {
                        response1=response;
                        rlAwesome.setVisibility(View.VISIBLE);

                        mPrefs.save(Constants.ISEmailMandatory,response.body().data.is_email_mandatory);

                    }
                    else {
                        bar=Snackbar.make(rlChangePassword,response.body().message,Snackbar.LENGTH_LONG).setAction("dismiss", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                bar.dismiss();
                            }
                        });
                        bar.show();
                    }
                }
                else {

                    bar=Snackbar.make(rlChangePassword,getResources().getString(R.string.serverIssue),Snackbar.LENGTH_LONG).setAction("dismiss", new View.OnClickListener() {
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
                bar=Snackbar.make(rlChangePassword,getResources().getString(R.string.netIssue),Snackbar.LENGTH_LONG).setAction("Dismiss", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bar.dismiss();
                    }
                });
                bar.show();
            }
        });
    }

    private void gotoApp(Response<GeneralModel> response) {
        if(response.body().data.next_link.equals("set_phone_email"))
        {
            SetPhoneEmailFragment frag=new SetPhoneEmailFragment();
            Bundle bundle=new Bundle();
            try {
               // GeneralFunctions.firstCheck(mPrefs,mPrefs.getString(Constants.proFileDisplayNumber,""));
                if(response.body().data.phone!=null)
                    bundle.putString("phone",response.body().data.phone);
                if(response.body().data.email!=null)
                    bundle.putString("email",response.body().data.email);

                frag.setArguments(bundle);
            }
            catch (Exception e)
            {

            }

            getFragmentManager().beginTransaction().add(R.id.rlChangePassword,frag).addToBackStack(null).commit();
            mPrefs.save(Constants.SetPhone,"YES");
            mPrefs.save(Constants.isSigned, true);
        }
        else {
            mPrefs.save(Constants.isSigned, true);
            Intent intent = new Intent(getActivity(), StateSelectActivity.class);
            startActivity(intent);
            getActivity().finish();
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

                return event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK;
            }
        });
    }


}
