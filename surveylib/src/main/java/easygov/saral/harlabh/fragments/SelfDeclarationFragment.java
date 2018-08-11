package easygov.saral.harlabh.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import easygov.saral.harlabh.R;
import easygov.saral.harlabh.activity.FourthApplicationActivity;
import easygov.saral.harlabh.activity.SplashActivity;
import easygov.saral.harlabh.adapters.AttachmentsAdapter;
import easygov.saral.harlabh.models.AdditionalInfoModel;
import easygov.saral.harlabh.models.responsemodels.filefieldsupdated.FileFieldsUpdate;
import easygov.saral.harlabh.utils.Constants;
import easygov.saral.harlabh.utils.GeneralFunctions;
import easygov.saral.harlabh.utils.Prefs;
import easygov.saral.harlabh.utils.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static easygov.saral.harlabh.activity.FourthApplicationActivity.detailList;

/**
 * Created by apoorv on 16/11/17.
 */

public class SelfDeclarationFragment extends Fragment {
    private TextView tvNoAccept,tvAccept,tvSelfName,tvDeclarationPlace,tvSelfName1;
    private   String selfDec,selfPlace;
    private Prefs mPrefs;
    private RelativeLayout rlSelfDec;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_declaration,container,false);
        init(view);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        readFileFields();
    }

    private void readFileFields() {
        GeneralFunctions.showDialog(getActivity());
        Call<FileFieldsUpdate> call= RestClient.get().readFileFields(mPrefs.getString(Constants.ServiceId,""),
                mPrefs.getString(Constants.SurveyId,""),mPrefs.getString(Constants.UserServiceID,""),mPrefs.getString(Constants.GeographyId,""));
        call.enqueue(new Callback<FileFieldsUpdate>() {
            @Override
            public void onResponse(Call<FileFieldsUpdate> call, Response<FileFieldsUpdate> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful())
                {
                    if(response.body().code==401)
                    {
                        GeneralFunctions.tokenExpireAction(getActivity());
                    }
                    else if(response.body().success==1)
                    {
                        if(response.body().data.declaration!=null&& response.body().data.declaration.length()>0)
                            tvSelfName.setText(Html.fromHtml(response.body().data.declaration));
                        //dcosObj=response.body();
                    }
                    else
                    {
                        Toast.makeText(getContext(), response.body().message, Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                        // GeneralFunctions.makeSnackbar(rlSelfDec, response.body().message);
                    }
                }
                else
                {
                    //GeneralFunctions.makeSnackbar(rlSelfDec,getResources().getString(R.string.serverIssue));
                    Toast.makeText(getContext(), getResources().getString(R.string.serverIssue), Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                    // GeneralModel error = ErrorUtils.parseError(response);
                    // Log.d("Sigin Error",error.data.message);
                }
            }

            @Override
            public void onFailure(Call<FileFieldsUpdate> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                //  GeneralFunctions.makeSnackbar(rlSelfDec,getResources().getString(R.string.netIssue));
                Toast.makeText(getContext(), getResources().getString(R.string.netIssue), Toast.LENGTH_SHORT).show();
                getActivity().finish();

            }
        });
    }

    private void init(View view) {
        tvNoAccept= view.findViewById(R.id.tvNoAccept);
        tvAccept= view.findViewById(R.id.tvAccept);
        tvSelfName= view.findViewById(R.id.tvSelfName);
        tvSelfName1= view.findViewById(R.id.tvSelfName1);
        tvDeclarationPlace= view.findViewById(R.id.tvDeclarationPlace);
        rlSelfDec= view.findViewById(R.id.rlSelfDec);
        mPrefs=Prefs.with(getActivity());

        //String s=" <u>Branch Name: </u> ";
        selfDec=getResources().getString(R.string.I)+ " "+"<u>"+
                mPrefs.getString(Constants.FirstName,"")+"</u>"+" "+
                mPrefs.getString(Constants.CareOf,"") +" "+"<u>"+
                mPrefs.getString(Constants.LastName,"")+"</u>"+" "+
                getResources().getString(R.string.residentof)+" "+"<u>"+
                mPrefs.getString(Constants.Street1,"")+"</u>"+" "+"<u>"+mPrefs.getString(Constants.Street2,"")+"</u>"+" "+"<u>"+mPrefs.getString(Constants.City,"")+"</u>"+" "+
                getResources().getString(R.string.selfDec);

        // tvSelfName.setText(Html.fromHtml(selfDec));

        //tvSelfName1.setText(getResources().getString(R.string.selfDec1));
       /* selfPlace =getResources().getString(R.string.place) + " "+"<u>"+ mPrefs.getString(Constants.Declarationdist,"")+" , "+
                mPrefs.getString(Constants.Declarationstate,"")+"</u>";
        tvDeclarationPlace.setText(Html.fromHtml(selfPlace));*/

        tvAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FourthApplicationActivity)getContext()).setFourthActivity();
            }
        });

        tvNoAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // getContext().finish();
                ((FourthApplicationActivity)getContext()).finish();
            }
        });
    }
}
