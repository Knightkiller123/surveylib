package easygov.saral.harlabh.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.text.Line;

import java.util.ArrayList;
import java.util.List;

import easygov.saral.harlabh.R;
import easygov.saral.harlabh.activity.FirstSurveyActivity;
import easygov.saral.harlabh.activity.StaticFormActivity;
import easygov.saral.harlabh.adapters.FamilyDetailAdapter;
import easygov.saral.harlabh.adapters.SomeoneElseDetailAdapter;
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

/**
 * Created by apoorv on 27/02/18.
 */

public class SomeoneElseDetailFragment extends Fragment implements SomeoneElseDetailAdapter.OnUpdateData {

    private CardView cvAddMore;
    private RecyclerView rvData;
    private LinearLayout llFamCont;
    private List<Family_profile> family = new ArrayList<>();
    private SomeoneElseDetailAdapter adapter;
    private Prefs mPrefs;
    private ImageView ivDetailBack;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_someoneelse_details, container, false);
        initialize(view);
        return view;
    }

    /*@Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialize(view);
    }*/

    private void initialize(View view) {
        cvAddMore = view.findViewById(R.id.cvAddMore);
        rvData = view.findViewById(R.id.rvData);
        llFamCont = view.findViewById(R.id.llFamCont);
        rvData.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new SomeoneElseDetailAdapter(getActivity(), family, this);
        rvData.setAdapter(adapter);
        mPrefs=Prefs.with(getContext());
        ivDetailBack= view.findViewById(R.id.ivDetailBack);

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
                   /* Intent intent=new Intent(getActivity(), StaticFormActivity.class);
                    startActivity(intent);
                    mPrefs.save(Constants.SomeoneAdded,"yes");
                mPrefs.save(Constants.ProfileId,"");

                    getActivity().overridePendingTransition( R.anim.animateup_activity, R.anim.animatedown_activity );*/

                    addFamilyMember(v);


            }
        });
    }

    private PopupWindow pw;
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
            TextView tvfamtop = layout.findViewById(R.id.tvfamtop);
            ImageView ivCrossFamily = layout.findViewById(R.id.ivCrossFamily);

            LinearLayout llFamily = layout.findViewById(R.id.llFamily);

            tvfamtop.setText(getResources().getString(R.string.addsomeone));

            //ArrayList<String> care=new ArrayList<>();
            /*care.add(getResources().getString(R.string.son));
            care.add(getResources().getString(R.string.daughter));
            care.add(getResources().getString(R.string.father));
            care.add(getResources().getString(R.string.mother));
            care.add(getResources().getString(R.string.brother));

            care.add(getResources().getString(R.string.sister));
            care.add(getResources().getString(R.string.spouse));
            care.add(getResources().getString(R.string.dependent));
            ArrayAdapter<String> adapter=new ArrayAdapter<String>(getContext(), R.layout.spinner_title_text,care);
            adapter.setDropDownViewResource(R.layout.spinner_adapter_value);

            spSpinFamily.setAdapter(adapter);*/

           /* spSpinFamily.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
            });*/

            //spSpinFamily.setVisibility(View.GONE);
            llFamily.setVisibility(View.GONE);

            tvSaveRelative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(etNameRelative.getText().toString().trim().length()>0)
                    {
                        hitsaverel(etNameRelative.getText().toString());
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


    private void hitsaverel( String s) {
        GeneralFunctions.showDialog(getContext());
        Call<GeneralModel> call =RestClient.get().addRelative(s,"someoneelse");

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
        Call<FamilyModel> call= RestClient.get().familyModel("1");
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
                    else if(response.body().success==1)
                    {
                        family.clear();
                        Family_profile asd=new Family_profile();
                       /* asd.id=-1;
                        asd.name="Myself";
                        asd.relationship="myself";*/

                        // family.add(asd);
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
        //rvData.setAdapter(new FamilyDetailAdapter());
        adapter.notifyItemRemoved(position);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void dismissDialogg() {
        if(mPrefs.getString(Constants.FromSomeoneElseGo,"").equals("yes"))
        {
            try {

               // hitApi();
                mPrefs.save(Constants.FromSomeoneElseGo,"no");
                getFragmentManager().popBackStack();
                ((FirstSurveyActivity)getActivity()).setSomeoneElse();
            }
            catch (Exception e)
            {

            }

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if(mPrefs.getString(Constants.SomeoneAdded,"").equals("yes"))
        {
            try {
                //mPrefs.save(Constants.FromSomeoneElseGo,"no");
                mPrefs.save(Constants.SomeoneAdded,"no");
              //  mPrefs.save(Constants.FromSomeoneElseGo,"yes");
               hitApi();
            }
            catch (Exception e)
            {}

        }

        if(mPrefs.getString(Constants.RestartSurvey,"").equalsIgnoreCase("yes"))
        {
            mPrefs.save(Constants.RestartSurvey,"no");
           //adapter.restart();
            mPrefs.save(Constants.FromSomeoneElseGo,"no");
            getFragmentManager().popBackStack();
            ((FirstSurveyActivity)getActivity()).setSomeoneElse();
        }
    }

    @Override
    public void updateUi() {

    }
}
