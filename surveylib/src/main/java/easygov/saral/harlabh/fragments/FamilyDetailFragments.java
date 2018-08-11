package easygov.saral.harlabh.fragments;

/**
 * Created by apoorv on 26/02/18.
 */


import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import easygov.saral.harlabh.R;
import easygov.saral.harlabh.activity.FirstSurveyActivity;
import easygov.saral.harlabh.adapters.FamilyDetailAdapter;
import easygov.saral.harlabh.models.responsemodels.familymodel.FamilyModel;
import easygov.saral.harlabh.models.responsemodels.familymodel.Family_profile;
import easygov.saral.harlabh.models.responsemodels.generalmodel.GeneralModel;
import easygov.saral.harlabh.utils.Constants;
import easygov.saral.harlabh.utils.GeneralFunctions;
import easygov.saral.harlabh.utils.Prefs;
import easygov.saral.harlabh.utils.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class FamilyDetailFragments extends Fragment implements FamilyDetailAdapter.OnUpdateData {

    private CardView cvAddMore;
    private RecyclerView rvData;
    private RelativeLayout llFamCont;
    private List<Family_profile> family = new ArrayList<>();
    private FamilyDetailAdapter adapter;
    private Prefs mPrefs;
    private ImageView ivDetailBack;
    private TextView tvName,tvRemove,tvTag,tvKnowTheirSchemes;
    private PopupWindow pw;
    private String rel="";



    @NonNull
   /* @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.fragment_family_details, null);

        Dialog dialog = new Dialog(getContext(), R.style.ThemeOverlay_AppCompat_Dialog);
        dialog.setContentView(view);
        return dialog;

    }*/

   /* @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
      //  getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        super.onResume();
    }*/



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_family_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialize(view);
    }

    private void initialize(View view) {
        cvAddMore = view.findViewById(R.id.cvAddMore);
        rvData = view.findViewById(R.id.rvData);
        llFamCont = view.findViewById(R.id.llFamCont);
        rvData.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new FamilyDetailAdapter(getActivity(), family, this);
        rvData.setAdapter(adapter);
        rvData.setItemViewCacheSize(25);
        mPrefs=Prefs.with(getContext());
        ivDetailBack= view.findViewById(R.id.ivDetailBack);
        tvName= view.findViewById(R.id.tvName);
        tvRemove= view.findViewById(R.id.tvRemove);
        tvTag= view.findViewById(R.id.tvTag);
        tvKnowTheirSchemes= view.findViewById(R.id.tvKnowTheirSchemes);

        tvRemove.setVisibility(View.GONE);
        tvKnowTheirSchemes.setText(getResources().getString(R.string.knowyourschemes));

        tvKnowTheirSchemes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPrefs.save(Constants.Applyingfor,"myself");
                mPrefs.save(Constants.ProfileId,"");
                try {
                    getFragmentManager().popBackStack();
                    mPrefs.save(Constants.FromFamilyGo,"no");
                    mPrefs.save(Constants.FamilyMyselfCase,"yes");
                    ((FirstSurveyActivity)getActivity()).setFamily(0);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }


        });

        tvTag.setText(getActivity().getResources().getString(R.string.self));
        if(mPrefs.getString(Constants.SelfName,"")!=null &&mPrefs.getString(Constants.SelfName,"").length()>0)
            tvName.setText(mPrefs.getString(Constants.SelfName,""));

        else {
            tvName.setText(mPrefs.getString(Constants.proFileDisplayNumber,""));
        }
        ivDetailBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneralFunctions.backPopup(llFamCont.getRootView(),getActivity(),0);
            }
        });
        hitApi();

        cvAddMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFamilyMember(v);
            }
        });

    }

    public void addFamilyMember(View view) {

        try {
            LayoutInflater inflater = (LayoutInflater) getActivity()
                    .getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.selectrelativr_dialog,
                    (ViewGroup) view.findViewById(R.id.rlFileSelect));

            pw = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
            pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
            Spinner spSpinFamily= layout.findViewById(R.id.spSpinFamily);
            final EditText etNameRelative = layout.findViewById(R.id.etNameRelative);
            TextView tvSaveRelative = layout.findViewById(R.id.tvSaveRelative);
            ImageView ivCrossFamily = layout.findViewById(R.id.ivCrossFamily);
            ArrayList<String> care=new ArrayList<>();
            care.add(getResources().getString(R.string.son));
            care.add(getResources().getString(R.string.daughter));
            care.add(getResources().getString(R.string.father));
            care.add(getResources().getString(R.string.mother));
            care.add(getResources().getString(R.string.brother));

            care.add(getResources().getString(R.string.sister));
            care.add(getResources().getString(R.string.spouse));
            care.add(getResources().getString(R.string.dependent));
            ArrayAdapter<String> adapter=new ArrayAdapter<String>(getContext(), R.layout.spinner_title_text,care);
            adapter.setDropDownViewResource(R.layout.spinner_adapter_value);

            spSpinFamily.setAdapter(adapter);

            spSpinFamily.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selected = (String) parent.getItemAtPosition(position);
                    switch (position)
                    {
                        case 0:
                            rel="Son";
                            break;
                        case 1:
                            rel="Daughter";
                            break;
                        case 2:
                            rel="Father";
                            break;
                        case 3:
                            rel="Mother";
                            break;
                        case 4:
                            rel="Brother";
                            break;
                        case 5:
                            rel="Sister";
                            break;
                        case 6:
                            rel="Spouse";
                            break;
                        case 7:
                            rel="Dependent";
                            break;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            tvSaveRelative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(rel.length()>1&&etNameRelative.getText().toString().trim().length()>0)
                    {
                        hitsaverel(rel,etNameRelative.getText().toString());
                    }
                }
            });

            pw.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    pw.dismiss();
                }
            });
            ivCrossFamily.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pw.dismiss();
                }
            });


        }
        catch (Exception e)
        {

        }
    }

    private void hitsaverel(String rel, String s) {
        GeneralFunctions.showDialog(getContext());
        Call<GeneralModel> call =RestClient.get().addRelative(s,rel);

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
                    else
                    if(response.body().success==1)
                    {
                        pw.dismiss();
                        hitApi();

                    }
                    else Toast.makeText(getContext(),response.body().message, Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(getContext(),getResources().getString(R.string.serverIssue), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<GeneralModel> call, Throwable t) {
                Toast.makeText(getContext(),getResources().getString(R.string.netIssue), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void hitApi()
    {
        GeneralFunctions.dismissDialog();
        Call<FamilyModel> call= RestClient.get().familyModel("0");
        call.enqueue(new Callback<FamilyModel>() {
            @Override
            public void onResponse(Call<FamilyModel> call, Response<FamilyModel> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful())
                {
                    if(response.body().code==401)
                    {
                        GeneralFunctions.tokenExpireAction(getActivity());
                    }
                    else  if(response.body().success==1)
                    {
                        family.clear();
                        /*Family_profile asd=new Family_profile();
                        asd.id=-1;
                        asd.name=mPrefs.getString(Constants.SelfName,"");
                        asd.relationship=getActivity().getResources().getString(R.string.self);
                        asd.setPos=0;
                        family.add(asd);*/
                        if(response.body().data.family_profiles!=null&&response.body().data.family_profiles.size()>0)
                            family.addAll(response.body().data.family_profiles);

                        adapter.notifyDataSetChanged();
                    }
                    else {
                        GeneralFunctions.makeSnackbar(llFamCont,response.body().message);
                    }
                }
                else {
                    GeneralFunctions.makeSnackbar(llFamCont,getResources().getString(R.string.serverIssue));
                }
            }

            @Override
            public void onFailure(Call<FamilyModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(llFamCont,getResources().getString(R.string.netIssue));

            }
        });

    }


    @Override
    public void removeAtPosition(int position) {
        family.remove(position);

        adapter.notifyItemRemoved(position);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void dismissDialogg() {
        if(mPrefs.getString(Constants.FromFamilyGo,"").equals("yes"))
        {
            try {
                getFragmentManager().popBackStack();
                mPrefs.save(Constants.FromFamilyGo,"no");
                ((FirstSurveyActivity)getActivity()).setFamily(1);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        /*if(mPrefs.getString(Constants.FromFamilyGo,"").equals("yes"))
        {
            try {
                mPrefs.save(Constants.FromFamilyGo,"no");
                ((FirstSurveyActivity)getActivity()).setFamily();
            }
            catch (Exception e)
            {}

        }*/

        if(mPrefs.getString(Constants.RestartSurvey,"").equalsIgnoreCase("yes"))
        {
            mPrefs.save(Constants.RestartSurvey,"no");
            //adapter.restart();
            getFragmentManager().popBackStack();
            mPrefs.save(Constants.FromFamilyGo,"no");
            ((FirstSurveyActivity)getActivity()).setFamily(1);
        }
    }

    @Override
    public void updateUi() {
      /*  if(mPrefs.getString(Constants.AadhaarBackendEnable,"").equals("1")) {
                    rlApplyingFor.setVisibility(View.GONE);
                    rlHavingAadhaar.setVisibility(View.VISIBLE);
                }
                else {
                    Intent intent=new Intent(getActivity(), StaticFormActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition( R.anim.animateup_activity, R.anim.animatedown_activity );
                }
    }*/
    }
}
