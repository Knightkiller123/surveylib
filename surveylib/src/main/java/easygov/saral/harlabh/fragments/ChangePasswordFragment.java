package easygov.saral.harlabh.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import easygov.saral.harlabh.R;
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
public class ChangePasswordFragment extends Fragment {


    private ImageView ivChangePassBack;
    private EditText etOldPass,etNewPass,etRetypePass;
    private TextView tvChangePass;
    private Prefs mPrefs;
    private RelativeLayout rlPassChange;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=  inflater.inflate(R.layout.fragment_change_password, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        mPrefs=Prefs.with(getActivity());
        ivChangePassBack= view.findViewById(R.id.ivChangePassBack);
        tvChangePass= view.findViewById(R.id.tvChangePass);
        etOldPass= view.findViewById(R.id.etOldPass);
        etNewPass= view.findViewById(R.id.etNewPass);
        etRetypePass= view.findViewById(R.id.etRetypePass);
        rlPassChange= view.findViewById(R.id.rlPassChange);


        rlPassChange.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                GeneralFunctions.hideSoftKeyboard(getActivity());
                return false;
            }
        });
        ivChangePassBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        tvChangePass.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                checkPass();
            }
        });
    }

    private void checkPass() {
      /*  if(etOldPass.getText().toString().trim().isEmpty())
            GeneralFunctions.makeSnackbar(rlPassChange,getResources().getString(R.string.oldpassempty));
        else if(etNewPass.getText().toString().trim().isEmpty())
            GeneralFunctions.makeSnackbar(rlPassChange,getResources().getString(R.string.newpassempty));
        else if(etNewPass.getText().toString().equals(etRetypePass.getText().toString()))
            hitChangePassApi();
        else
            GeneralFunctions.makeSnackbar(rlPassChange,getResources().getString(R.string.passnomatch));
*/

        if(etOldPass.getText().toString().isEmpty())
        {
            GeneralFunctions.makeSnackbar(rlPassChange,getResources().getString(R.string.oldpassempty));
        }
        else  if(etNewPass.getText().toString().isEmpty())
        {
            GeneralFunctions.makeSnackbar(rlPassChange,getResources().getString(R.string.plpass));
        }
        else if(etRetypePass.getText().toString().isEmpty())
        {
            GeneralFunctions.makeSnackbar(rlPassChange,getResources().getString(R.string.confirmpass));
        }
        else if (etNewPass.getText().toString().trim().isEmpty())
        {
            GeneralFunctions.makeSnackbar(rlPassChange,getResources().getString(R.string.invalid_password));
        }
        else if (etNewPass.getText().toString().length()<6)
        {
            GeneralFunctions.makeSnackbar(rlPassChange,getResources().getString(R.string.short_pwd));
        }
        else if(!etNewPass.getText().toString().equals(etRetypePass.getText().toString()))
        {
            GeneralFunctions.makeSnackbar(rlPassChange,getResources().getString(R.string.passnomatch));
        }
        else
        {
            hitChangePassApi();
        }
    }

    private void hitChangePassApi() {
        GeneralFunctions.showDialog(getContext());
        Call<GeneralModel> call= RestClient.get().changePass(etOldPass.getText().toString(),etNewPass.getText().toString(),etRetypePass.getText().toString());
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
                        Toast.makeText(getContext(), getResources().getString(R.string.passupdatet), Toast.LENGTH_SHORT).show();
                        mPrefs.save(Constants.Password,etNewPass.getText().toString());
                        getFragmentManager().popBackStack();
                    }
                    else {
                        GeneralFunctions.makeSnackbar(rlPassChange,response.body().message);

                    }
                }
                else {
                    GeneralFunctions.makeSnackbar(rlPassChange,getResources().getString(R.string.serverIssue));

                }
            }

            @Override
            public void onFailure(Call<GeneralModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(rlPassChange,getResources().getString(R.string.netIssue));

            }
        });
    }

}
