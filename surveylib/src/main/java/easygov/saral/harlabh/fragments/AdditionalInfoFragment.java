package easygov.saral.harlabh.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import easygov.saral.harlabh.R;
import easygov.saral.harlabh.activity.FourthApplicationActivity;
import easygov.saral.harlabh.activity.StaticFormActivity;
import easygov.saral.harlabh.fragments.application_pager_fragments.AttachmentFragment;
import easygov.saral.harlabh.models.AdditionalInfoModel;
import easygov.saral.harlabh.models.responsemodels.authoritymodel.Authority;
import easygov.saral.harlabh.models.responsemodels.authoritymodel.AuthorityModel;
import easygov.saral.harlabh.models.responsemodels.filefieldsupdated.Authority_type;
import easygov.saral.harlabh.models.responsemodels.filter.FilterData;
import easygov.saral.harlabh.models.responsemodels.statesndistricts.DistrictDetails;
import easygov.saral.harlabh.models.responsemodels.statesndistricts.DistrictsModel;
import easygov.saral.harlabh.models.responsemodels.statesndistricts.StateDetails;
import easygov.saral.harlabh.models.responsemodels.statesndistricts.StatesModel;
import easygov.saral.harlabh.utils.Constants;
import easygov.saral.harlabh.utils.GeneralFunctions;
import easygov.saral.harlabh.utils.Prefs;
import easygov.saral.harlabh.utils.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static easygov.saral.harlabh.adapters.AttachmentsAdapter.fragmentinstance;
import static easygov.saral.harlabh.fragments.application_pager_fragments.AttachmentFragment.addModel;


/**
 * Created by apoorv on 25/01/18.
 */

public class AdditionalInfoFragment extends Fragment {
    private EditText etAadhaarAdditional;
    private TextView tvHeadAdditional,tvDoneAdditional;
    private Spinner spStateAdditional,spDistrictAdditional,spIssuer,spSpinAuthType;
    private ImageView ivAdditionalBack;
    private RelativeLayout rlAdditionalContainer,rlIssuer,rlAddLayer;
    private AdditionalInfoModel localModel;
    private ScrollView llScrollAdditional;
    private List<StateDetails> states;private List<String> spinnerStates=new ArrayList<>();private List<Integer> stateIds=new ArrayList<>();private List<Integer> distIds=new ArrayList<>();private List<DistrictDetails> districts =new ArrayList<>();private List<String> spinDistricts=new ArrayList<>();private  List<String> authname =new ArrayList<>();private List<Integer> authid=new ArrayList<>();

    private String stateSelectedString,districtSelectedString,head="",authId;
    private List<Authority_type> authority_type;
    private LinearLayout llAuthType,llIssuer;
    private AttachmentFragment frag;
    private int positionOfForm,isHaryanaSet=0,stateSelected=0,distSelected=0,authorityselected=0,statePos=-1,
            distPos=-1,authPos=-1,issuerPos=-1,statePos1=-1,
            distPos1=-1,authPos1=-1,issuerPos1=-1;
    private Prefs mPrefs;



    private String distId="",authType="",stateId,docnum="";
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_additionalinfo,container,false);
        init(view);
        return view;
    }

    private void init(View view) {
        Bundle bundle=new Bundle();
        bundle=getArguments();
        mPrefs=Prefs.with(getActivity());
        localModel=new AdditionalInfoModel();
        etAadhaarAdditional= view.findViewById(R.id.etAadhaarAdditional);
        spStateAdditional= view.findViewById(R.id.spStateAdditional);
        tvHeadAdditional= view.findViewById(R.id.tvHeadAdditional);
        ivAdditionalBack= view.findViewById(R.id.ivAdditionalBack);
        llScrollAdditional= view.findViewById(R.id.llScrollAdditional);
        spDistrictAdditional= view.findViewById(R.id.spDistrictAdditional);
        spIssuer= view.findViewById(R.id.spIssuer);
        spSpinAuthType= view.findViewById(R.id.spSpinAuthType);
        rlAdditionalContainer= view.findViewById(R.id.rlAdditionalContainer);
        rlAddLayer= view.findViewById(R.id.rlAddLayer);
        llAuthType= view.findViewById(R.id.llAuthType);
        llIssuer= view.findViewById(R.id.llIssuer);
        rlIssuer= view.findViewById(R.id.rlIssuer);
        tvDoneAdditional= view.findViewById(R.id.tvDoneAdditional);
        if(bundle!=null)
        {
            positionOfForm = getArguments().getInt("post");
            authority_type=new Gson().fromJson(bundle.getString("authority"),new TypeToken<List<Authority_type>>() {}.getType());
            head=getArguments().getString("nameshead");

            try {
                if(addModel.get(positionOfForm).number!=null&&addModel.get(positionOfForm).number.length()>0)
                {
                    etAadhaarAdditional.setText(addModel.get(positionOfForm).number);
                }
                statePos=addModel.get(positionOfForm).addStatId;
                distPos=addModel.get(positionOfForm).addDistId;
                authPos=addModel.get(positionOfForm).addAuthId;
                issuerPos=addModel.get(positionOfForm).addIssuerId;
            }
            catch (Exception e)
            {}

            llScrollAdditional.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    GeneralFunctions.hideSoftKeyboard(getActivity());
                    return false;
                }
            });

        }

        tvHeadAdditional.setText(head);
        getStates();

        if(authority_type!=null&&authority_type.size()>0)
        {
            //setAuthority();
        }
        else {
            llAuthType.setVisibility(View.GONE);
            llIssuer.setVisibility(View.GONE);
            authorityselected=1;
        }

        ivAdditionalBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FourthApplicationActivity)getActivity()).makeInvisible();
                getFragmentManager().popBackStack();
            }
        });

        tvDoneAdditional.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkValidate())
                {
                    ((FourthApplicationActivity)getActivity()).makeInvisible();
                    getFragmentManager().popBackStack();
                    localModel.number=etAadhaarAdditional.getText().toString();
                    localModel.addAuthId=authPos1;
                    localModel.addStatId=statePos1;
                    localModel.addDistId=distPos1;
                    localModel.addIssuerId=issuerPos1;
                    addModel.set(positionOfForm,localModel);

                    fragmentinstance.updateUI(positionOfForm,docnum,stateId,distId,authId);
                }
            }
        });

    }

    private boolean checkValidate() {
        docnum=etAadhaarAdditional.getText().toString();
        if(etAadhaarAdditional.getText().toString().trim().isEmpty())
        {
            GeneralFunctions.makeSnackbar(rlAdditionalContainer,getResources().getString(R.string.plenter) +" "+head);
            return false;
        }

        if(stateSelected==0)
        {
            GeneralFunctions.makeSnackbar(rlAdditionalContainer,getResources().getString(R.string.plstate));
            return false;
        }

        if(distSelected==0)
        {
            GeneralFunctions.makeSnackbar(rlAdditionalContainer,getResources().getString(R.string.pldist));
            return false;
        }

        if(authorityselected==0)
        {
            GeneralFunctions.makeSnackbar(rlAdditionalContainer,getResources().getString(R.string.plAuthority));
            return false;
        }
        return true;
    }

    private void setAuthority() {
        authname=new ArrayList<>();
        authid=new ArrayList<>();
        authname.add(getResources().getString(R.string.select));
        authid.add(0);
        for (int i=0;i<authority_type.size();i++)
        {
            authname.add(authority_type.get(i).name);
            authid.add(authority_type.get(i).value);

        }


        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(), R.layout.spinner_title_text,authname)
        {
            @Override
            public boolean isEnabled(int position){
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position==0) {
                    // Set the disable item text color
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        adapter.setDropDownViewResource(R.layout.spinner_adapter_value);
        spIssuer.setAdapter(adapter);

        /*ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(), R.layout.spinner_title_text,authname);
        adapter.setDropDownViewResource(R.layout.spinner_adapter_value);
        spIssuer.setAdapter(adapter);*/

        spIssuer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0) {
                    String selected = (String) parent.getItemAtPosition(position);
                    String a = (String) parent.getItemAtPosition(position);
                    int pos = authname.indexOf(selected);
                    authPos1=pos;
                    authType = String.valueOf(authid.get(pos));
                    authorityselected = 1;
                    setIssuer();
                }
                else {
                    ((TextView) spIssuer.getSelectedView()).setTextColor(ContextCompat.getColor(getActivity(),R.color.darkgrey));
                }

            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if(authPos!=-1) {
            spIssuer.setSelection(authPos);
            rlAddLayer.setVisibility(View.GONE);
            authPos=-1;
        }

    }


    private void getStates() {
        GeneralFunctions.showDialog(getActivity());
        Call<StatesModel> call= RestClient.get().getAllStates("1000");
        call.enqueue(new Callback<StatesModel>() {
            @Override
            public void onResponse(Call<StatesModel> call, Response<StatesModel> response) {

                if (response.isSuccessful()) {
                    if(response.body().code.equals("401"))
                    {
                        GeneralFunctions.tokenExpireAction(getActivity());
                    }
                    else if (response.body().success == 1) {

                        states=response.body().data.objects;
                        setStates();
                        //statesObj = response.body();
                        //rlStates.setAdapter(new StatesAdapter(StateSelectActivity.this, statesObj.data.objects));
/*
                        DbHelper db=new DbHelper(StateSelectActivity.this);
                        db.addStates(statesObj.data.objects);*/
                        //getAllDistricts();
                    } else {
                        GeneralFunctions.dismissDialog();
                        GeneralFunctions.makeSnackbar(rlAdditionalContainer,response.body().message);
                    }

                }
                else {
                    GeneralFunctions.dismissDialog();
                    GeneralFunctions.makeSnackbar(rlAdditionalContainer,getResources().getString(R.string.serverIssue));
                }
            }


            @Override
            public void onFailure(Call<StatesModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(rlAdditionalContainer,getResources().getString(R.string.netIssue));
            }
        });
    }
    private int harPos;
    private void setStates() {

        spinnerStates=new ArrayList<>();
        spinnerStates.add(getResources().getString(R.string.select));

        stateIds=new ArrayList<>();
        stateIds.add(0);
        harPos=-1;
        for (int i=0;i<states.size();i++)
        {
            spinnerStates.add(states.get(i).state_name);
            stateIds.add(states.get(i).states_id);

            if(Integer.parseInt(mPrefs.getString(Constants.StateId,""))==states.get(i).states_id)
                harPos=i+1;


        }

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(), R.layout.spinner_title_text,spinnerStates)
        {
            @Override
            public boolean isEnabled(int position){
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position==0) {
                    // Set the disable item text color
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        adapter.setDropDownViewResource(R.layout.spinner_adapter_value);
        spStateAdditional.setAdapter(adapter);


        spStateAdditional.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position!=0) {
                    String selected = (String) parent.getItemAtPosition(position);
                    stateSelectedString = (String) parent.getItemAtPosition(position);
                    int pos = spinnerStates.indexOf(selected);

                    stateSelected = 1;
                    stateId = String.valueOf(stateIds.get(pos));
                    ((TextView) spStateAdditional.getSelectedView()).setTextColor(ContextCompat.getColor(getActivity(),R.color.black));
                    // mPrefs.save(Constants.AdditionalState,String.valueOf(stateIds.get(pos)));
                  /*  CheckoutFragment.billingMap.put("state",stateIds.get(pos).toString());
                    CheckoutFragment.billingMap.put("billing_state",stateIds.get(pos).toString());*/
                    spDistrictAdditional.setAdapter(null);
                    spinDistricts.clear();
                    spinDistricts = new ArrayList<String>();

                    statePos1=pos;
                    getDistrictsbyId(stateIds.get(pos));
                }
                else {
                    ((TextView) spStateAdditional.getSelectedView()).setTextColor(ContextCompat.getColor(getActivity(),R.color.darkgrey));
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if(harPos>=0&&statePos==-1) {
            spStateAdditional.setSelection(harPos);
            //  getDistrictsbyId(Integer.parseInt(mPrefs.getString(Constants.StateId,"")));
        }
        else {
            spStateAdditional.setSelection(statePos);

            if(statePos!=-1)
                rlAddLayer.setVisibility(View.GONE);
            statePos=-1;}
    }


    private void getDistrictsbyId(final Integer id)
    {
        //GeneralFunctions.showDialog(getActivity());
        Call<DistrictsModel> call = RestClient.get().getSelectedDistricts(id.toString(),"10000");
        call.enqueue(new Callback<DistrictsModel>() {
            @Override
            public void onResponse(Call<DistrictsModel> call, Response<DistrictsModel> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful())
                {
                    if(response.body().code.equals("401"))
                    {
                        GeneralFunctions.tokenExpireAction(getActivity());
                    }
                    else if(response.body().success==1)
                    {

                        districts=new ArrayList<DistrictDetails>();
                        spDistrictAdditional.setAdapter(null);
                        districts=response.body().data.objects;
                        distSelected=0;
                        setDistricts(id);

                    }
                    else {

                        GeneralFunctions.makeSnackbar(rlAdditionalContainer,response.body().message);
                    }
                }
                else {

                    GeneralFunctions.makeSnackbar(rlAdditionalContainer,getResources().getString(R.string.serverIssue));
                }



            }

            @Override
            public void onFailure(Call<DistrictsModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(rlAdditionalContainer,getResources().getString(R.string.netIssue));

            }
        });
    }

    private int harDist=-1;
    private void setDistricts(int id) {

        spinDistricts=new ArrayList<>();
        distIds=new ArrayList<>();
        spinDistricts.add(getResources().getString(R.string.select));
        distIds.add(0);
        rlAddLayer.setVisibility(View.GONE);
        for(int i=0;i<districts.size();i++)
        {
            spinDistricts.add(districts.get(i).district_name);
            distIds.add(districts.get(i).districts_id);
            if(Integer.parseInt(mPrefs.getString(Constants.DistId,""))==districts.get(i).districts_id&&id==20)
                harDist=i+1;

        }

        ArrayAdapter<String> adapters=new ArrayAdapter<String>(getActivity(), R.layout.spinner_title_text,spinDistricts)
        {
            @Override
            public boolean isEnabled(int position){
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position==0) {
                    // Set the disable item text color
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        adapters.setDropDownViewResource(R.layout.spinner_adapter_value);
        spDistrictAdditional.setAdapter(adapters);


        spDistrictAdditional.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0) {
                    String selected = (String) parent.getItemAtPosition(position);
                    districtSelectedString = (String) parent.getItemAtPosition(position);
                    int pos = spinDistricts.indexOf(selected);
                    distId = String.valueOf(distIds.get(pos));
                    distSelected = 1;
                    isHaryanaSet = 1;
                    distPos1=pos;
                    //mPrefs.save(Constants.AdditionalDistrict,String.valueOf(distId));
                    spIssuer.setAdapter(null);
                    authname.clear();

                    if (authority_type != null && authority_type.size() > 0) {
                        setAuthority();
                    }
                    ((TextView) spDistrictAdditional.getSelectedView()).setTextColor(ContextCompat.getColor(getActivity(),R.color.black));

                }
                else {
                    ((TextView) spDistrictAdditional.getSelectedView()).setTextColor(ContextCompat.getColor(getActivity(),R.color.darkgrey));
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        if (harDist >= 0 && distPos == -1) {
            spDistrictAdditional.setSelection(harDist);
            harDist=-1;
        }

        else {spDistrictAdditional.setSelection(distPos);
            if(distPos!=-1)
                rlAddLayer.setVisibility(View.GONE);
            distPos=-1;}



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
                    ((FourthApplicationActivity)getActivity()).makeInvisible();
                    getFragmentManager().popBackStack();
                    return true;
                }
                return false;
            }
        });
    }


    private void setIssuer()
    {
        Call<AuthorityModel> call=RestClient.get().getAuthorities(distId,authType);
        call.enqueue(new Callback<AuthorityModel>() {
            @Override
            public void onResponse(Call<AuthorityModel> call, Response<AuthorityModel> response) {
                if(response.isSuccessful())
                {
                    if(response.body().code==401)
                    {
                        GeneralFunctions.tokenExpireAction(getActivity());
                    }
                    else if(response.body().success==1)
                    {
                        if(response.body().data.authorities!=null&&response.body().data.authorities.size()>0) {
                            //rlIssuer.setVisibility(View.VISIBLE);
                            llAuthType.setVisibility(View.VISIBLE);
                            makeIssuer(response.body().data.authorities);
                        }

                        else {
                            llAuthType.setVisibility(View.GONE);
                            authId="0";

                        }
                    }
                    else {
                        GeneralFunctions.makeSnackbar(rlAdditionalContainer,response.body().message);
                    }

                }
                else  GeneralFunctions.makeSnackbar(rlAdditionalContainer,getResources().getString(R.string.serverIssue));
            }

            @Override
            public void onFailure(Call<AuthorityModel> call, Throwable t) {
                GeneralFunctions.makeSnackbar(rlAdditionalContainer,getResources().getString(R.string.netIssue));
            }
        });
    }
    private   List<String> name=new ArrayList<>();
    private List<Integer> ids=new ArrayList<>();
    private void makeIssuer(List<Authority> authorities) {

        // List<String> name=new ArrayList<>();
        name=new ArrayList<>();
        ids=new ArrayList<>();

        for(int i=0;i<authorities.size();i++)
        {
            if(authorities.get(i).display_name!=null)
                name.add(authorities.get(i).display_name);
            if(authorities.get(i).value!=null)
                ids.add(authorities.get(i).value);
        }

        ArrayAdapter<String> adapters=new ArrayAdapter<String>(getActivity(), R.layout.spinner_title_text,name);
        adapters.setDropDownViewResource(R.layout.spinner_adapter_value);
        spSpinAuthType.setAdapter(adapters);

        spSpinAuthType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selected = (String) parent.getItemAtPosition(position);
                // districtSelectedString=(String) parent.getItemAtPosition(position);
                int pos = name.indexOf(selected);
                authId=String.valueOf(ids.get(pos));
                if(pos>0)
                    issuerPos1=pos;
                // mPrefs.save(Constants.AuthorityId,String.valueOf(authId));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if(issuerPos1==-1)
            if(issuerPos!=-1)
                spSpinAuthType.setSelection(issuerPos);


    }


}
