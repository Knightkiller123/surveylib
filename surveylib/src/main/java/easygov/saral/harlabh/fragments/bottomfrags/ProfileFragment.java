package easygov.saral.harlabh.fragments.bottomfrags;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import easygov.saral.harlabh.activity.BeneficiaryActivity;
import easygov.saral.harlabh.activity.ManagementActivity;
import easygov.saral.harlabh.activity.MyDocumentActivity;
import easygov.saral.harlabh.activity.PaymentStatusActivity;
import easygov.saral.harlabh.activity.ProfileActivity;
import easygov.saral.harlabh.fragments.ChangeLanguageFragment;
import easygov.saral.harlabh.fragments.ChangePasswordFragment;
import easygov.saral.harlabh.fragments.LanguageFragment;
import easygov.saral.harlabh.utils.Constants;
import easygov.saral.harlabh.utils.GeneralFunctions;
import easygov.saral.harlabh.utils.Prefs;
import easygov.saral.harlabh.utils.RestClient;
import easygov.saral.harlabh.R;
import easygov.saral.harlabh.activity.SplashActivity;
import easygov.saral.harlabh.models.responsemodels.generalmodel.GeneralModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {


    private TextView tvLogout,tvUserNumber ,tvManagement ,tvPayment ,tvDocument ,tvChangePassword
            ,tvChangeLanguage ,tvChangeBeneficiaryType ,tvAbout,tvProfile;
    private Prefs mPrefs;
    private RelativeLayout llProfileHeader;
    private Snackbar bar;
    private CardView cvProfile;

    private RelativeLayout rlProfile;/*
    private RelativeLayout rlProfile,rlManagement,rlDocProfile,rlChangePassProfile;
*/    private int s=1;
    // private ImageView ivDynamic;
    // private EditText tvCoupon;
    PackageInfo pInfo;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_profiles, container, false);
        init(view);
        clickHandlers();
        return view;
    }


    private void init(View view) {
        mPrefs=Prefs.with(getActivity());
        tvLogout= view.findViewById(R.id.tvLogout);
        tvManagement= view.findViewById(R.id.tvManagement);
        tvPayment= view.findViewById(R.id.tvPayment);
        tvDocument= view.findViewById(R.id.tvDocument);
        llProfileHeader= view.findViewById(R.id.llProfileHeader);
        tvChangePassword= view.findViewById(R.id.tvChangePassword);
        tvChangeLanguage= view.findViewById(R.id.tvChangeLanguage);
        tvChangeBeneficiaryType= view.findViewById(R.id.tvChangeBeneficiaryType);
        rlProfile= view.findViewById(R.id.rlProfile);
        tvUserNumber= view.findViewById(R.id.tvUserNumber);
        tvAbout= view.findViewById(R.id.tvAbout);
        tvProfile= view.findViewById(R.id.tvProfile);
        cvProfile= view.findViewById(R.id.cvProfile);
        if(mPrefs.getString(Constants.SelfName,"")!=null&&mPrefs.getString(Constants.SelfName,"").length()>0)
            tvUserNumber.setText(mPrefs.getString(Constants.SelfName,""));
        else

        tvUserNumber.setText(mPrefs.getString(Constants.proFileDisplayNumber,""));
        try {
            pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


    }

    private void clickHandlers()
    {
        tvLogout.setOnClickListener(this);
        tvPayment.setOnClickListener(this);
        tvManagement.setOnClickListener(this);
        tvDocument.setOnClickListener(this);
        tvChangePassword.setOnClickListener(this);
        tvChangeLanguage.setOnClickListener(this);
        tvChangeBeneficiaryType.setOnClickListener(this);
        tvAbout.setOnClickListener(this);
        llProfileHeader.setOnClickListener(this);
        tvProfile.setOnClickListener(this);
        cvProfile.setOnClickListener(this);

    }

    public static Fragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tvChangeBeneficiaryType) {
            Intent intents = new Intent(getActivity(), BeneficiaryActivity.class);
            intents.putExtra("from", "profile");
            startActivity(intents);

        } else if (i == R.id.tvLogout) {
            hitLogoutApi();

        } else if (i == R.id.tvManagement) {
            Intent intent = new Intent(getActivity(), ManagementActivity.class);
            intent.putExtra("frompayment", "no");
            startActivity(intent);

        } else if (i == R.id.tvDocument) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(getActivity(), MyDocumentActivity.class)
                        .putExtra("canDelete", true)
                        .putExtra("fieldId", "")
                        .putExtra("documentTypeId", "")
                );
            } else {

                requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        100);
            }

        } else if (i == R.id.tvProfile) {
            if (s % 8 == 1) {
                if (pInfo != null) {
                    GeneralFunctions.makeSnackbar(rlProfile, pInfo.versionCode + " : " + pInfo.versionName + " : " + Constants.BASE_URL);
                }
            }
            s++;

        } else if (i == R.id.llProfileHeader) {
            if (s % 8 == 0) {
                if (pInfo != null) {
                    GeneralFunctions.makeSnackbar(rlProfile, pInfo.versionCode + " : " + pInfo.versionName + " : " + Constants.BASE_URL);
                }
                s = 1;
            }
            s++;


        } else if (i == R.id.tvChangePassword) {
            getFragmentManager().beginTransaction().add(R.id.rlProfile, new ChangePasswordFragment()).addToBackStack(null).commit();

        } else if (i == R.id.tvPayment) {
            Intent intent1s = new Intent(getActivity(), PaymentStatusActivity.class);
            startActivity(intent1s);

        } else if (i == R.id.tvAbout) {
            getFragmentManager().beginTransaction().add(R.id.frameLayout, MoreFragment.newInstance())
                    .addToBackStack("").commit();

        } else if (i == R.id.tvChangeLanguage) {
            getFragmentManager().beginTransaction().add(R.id.rlLangHolder, new ChangeLanguageFragment())
                    .addToBackStack("").commit();

        } else if (i == R.id.cvProfile) {// startActivity(new Intent(getActivity(), ProfileActivity.class));

        }
    }

    private void hitLogoutApi() {
        GeneralFunctions.showDialog(getActivity());
        Call<GeneralModel> call = RestClient.get().logOut();
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
                        GeneralFunctions.clearPrefs(mPrefs);
                        Intent intent =new Intent(getActivity(),SplashActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                    else
                    {
                        bar=Snackbar.make(rlProfile,response.body().code,Snackbar.LENGTH_LONG).setAction("Dismiss", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                bar.dismiss();
                            }
                        });
                        bar.show();
                    }
                }
                else {
                    bar=Snackbar.make(rlProfile,getResources().getString(R.string.serverIssue),Snackbar.LENGTH_LONG).setAction("Dismiss", new View.OnClickListener() {
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
                bar=Snackbar.make(rlProfile,getResources().getString(R.string.netIssue),Snackbar.LENGTH_LONG).setAction("Dismiss", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bar.dismiss();
                    }
                });
                bar.show();
            }
        });
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode) {
            case 100: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startActivity(new Intent(getActivity(), MyDocumentActivity.class)
                            .putExtra("canDelete",true)
                            .putExtra("fieldId","")
                            .putExtra("documentTypeId","")
                    );


                } else {

                   /* getFragmentManager().popBackStack();
                    ((MyDocumentActivity)getActivity()).docIssueResolve();*/
                    GeneralFunctions.makeSnackbar(rlProfile, getResources().getString(R.string.plPermission));
                    GeneralFunctions.PleaseGrantPermission(rlProfile, getActivity());

                }
                return;
            }

        }
    }


}
