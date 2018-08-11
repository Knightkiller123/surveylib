package easygov.saral.harlabh.fragments.checkout_pager_fragments;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import easygov.saral.harlabh.R;
import easygov.saral.harlabh.models.responsemodels.statesndistricts.DistrictDetails;
import easygov.saral.harlabh.models.responsemodels.statesndistricts.DistrictsModel;
import easygov.saral.harlabh.models.responsemodels.statesndistricts.StateDetails;
import easygov.saral.harlabh.models.responsemodels.statesndistricts.StatesModel;
import easygov.saral.harlabh.utils.Constants;
import easygov.saral.harlabh.utils.GeneralFunctions;
import easygov.saral.harlabh.utils.MyApplication;
import easygov.saral.harlabh.utils.Prefs;
import easygov.saral.harlabh.utils.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static easygov.saral.harlabh.fragments.storyboardfrags.CheckoutFragment.billingMap;

/**
 * Created by apoorv on 15/11/17.
 */

public class CommunicationFragment extends Fragment implements View.OnFocusChangeListener, View.OnClickListener {
    private List<StateDetails> states;
    private EditText etFirstMeet , etLastMeet , etContactMeet,etStreet1Meet,etStreet2Meet,etCityMeet,etZipMeet;
    private EditText etBillFirstMeet , etBillLastMeet , etBillContactMeet,etBillStreet1Meet,etBillStreet2Meet,
            etBillCityMeet,etBillZipMeet;
    //  private int first,last,contact,street1,street2,city,state,dist,zip;

    private Spinner SpStateBill,spDistBill;
    private Spinner SpBillStateBill,spBillDistBill;
    public static int stateSelected=0,distSelected=0;
    public static int stateBillSelected=0,distBillSelected=0;
    List<String> spinnerStates=new ArrayList<>();
    List<String> spinnerBillStates=new ArrayList<>();
    List<Integer> stateIds=new ArrayList<>();
    private List<Integer> distIds=new ArrayList<>();
    private List<String> spinDistricts=new ArrayList<>();
    private List<String> spinBillDistricts=new ArrayList<>();
    private List<DistrictDetails> districts =new ArrayList<>();
    private Prefs mPrefs;
    private RelativeLayout rlCom;
    private String stateSelectedString="",districtSelectedString="";
    public static int sameAsBilling=0;
    private boolean isBilling=false;
    private CheckBox cbSame;
    private CardView cvBillAddress;
    private ImageView ivNameErase, ivAddDet1Erase,ivAdd2Erase,ivZipDetErase,ivCityDetErase ,ivCareOfErase,ivPhnErase;
    private ImageView ivBillNameErase, ivBillAddDet1Erase,ivBillAdd2Erase,ivBillZipDetErase,ivBillCityDetErase
            ,ivBillCareOfErase,ivBillPhnErase;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_communication,container,false);
        init(view);

        setAddress();

        getStates();

        //setEditListeners();

        setFocusListeners();
        return view;
    }

   /* @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible) {
            editTextIssueResolve();
        }
    }*/

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setFocusListeners() {
        etFirstMeet.setOnFocusChangeListener(this);
        etLastMeet.setOnFocusChangeListener(this);
        etContactMeet.setOnFocusChangeListener(this);
        etStreet1Meet.setOnFocusChangeListener(this);
        etStreet2Meet.setOnFocusChangeListener(this);
        etCityMeet.setOnFocusChangeListener(this);
        etZipMeet.setOnFocusChangeListener(this);

        ivNameErase.setOnClickListener(this);
        ivCareOfErase.setOnClickListener(this);
        ivPhnErase.setOnClickListener(this);
        ivAddDet1Erase.setOnClickListener(this);
        ivAdd2Erase.setOnClickListener(this);
        ivZipDetErase.setOnClickListener(this);
        ivCityDetErase.setOnClickListener(this);

        etBillFirstMeet.setOnFocusChangeListener(this);
        etBillLastMeet.setOnFocusChangeListener(this);
        etBillContactMeet.setOnFocusChangeListener(this);
        etBillStreet1Meet.setOnFocusChangeListener(this);
        etBillStreet2Meet.setOnFocusChangeListener(this);
        etBillCityMeet.setOnFocusChangeListener(this);
        etBillZipMeet.setOnFocusChangeListener(this);

        ivBillNameErase.setOnClickListener(this);
        ivBillCareOfErase.setOnClickListener(this);
        ivBillPhnErase.setOnClickListener(this);
        ivBillAddDet1Erase.setOnClickListener(this);
        ivBillAdd2Erase.setOnClickListener(this);
        ivBillZipDetErase.setOnClickListener(this);
        ivBillCityDetErase.setOnClickListener(this);



    }
   /* private void setEditListeners() {




        etFirstMeet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0)
                {
                    ivNameErase.setVisibility(View.VISIBLE);
                }
                else
                    ivNameErase.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        etLastMeet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0)
                {
                    ivCareOfErase.setVisibility(View.VISIBLE);
                }
                else
                    ivCareOfErase.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etContactMeet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0)
                {
                    ivPhnErase.setVisibility(View.VISIBLE);
                }
                else
                    ivPhnErase.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        etStreet1Meet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0)
                {
                    ivAddDet1Erase.setVisibility(View.VISIBLE);
                }
                else
                    ivAddDet1Erase.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etStreet2Meet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0)
                {
                    ivAdd2Erase.setVisibility(View.VISIBLE);
                }
                else
                    ivAdd2Erase.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etZipMeet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0)
                {
                    ivZipDetErase.setVisibility(View.VISIBLE);
                }
                else
                    ivZipDetErase.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etCityMeet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0)
                {
                    ivCityDetErase.setVisibility(View.VISIBLE);
                }
                else
                    ivCityDetErase.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
*/

    private void init(View view) {
        etFirstMeet = view.findViewById(R.id.etFirstMeet);
        etLastMeet = view.findViewById(R.id.etLastMeet);
        etContactMeet = view.findViewById(R.id.etContactMeet);
        etStreet1Meet = view.findViewById(R.id.etStreet1Meet);
        etStreet2Meet = view.findViewById(R.id.etStreet2Meet);
        etCityMeet = view.findViewById(R.id.etCityMeet);
        etZipMeet = view.findViewById(R.id.etZipMeet);
        etBillFirstMeet = view.findViewById(R.id.etBillFirstMeet);
        etBillLastMeet = view.findViewById(R.id.etBillLastMeet);
        etBillContactMeet = view.findViewById(R.id.etBillContactMeet);
        etBillStreet1Meet = view.findViewById(R.id.etBillStreet1Meet);
        etBillStreet2Meet = view.findViewById(R.id.etBillStreet2Meet);
        etBillCityMeet = view.findViewById(R.id.etBillCityMeet);
        etBillZipMeet = view.findViewById(R.id.etBillZipMeet);
        cbSame = view.findViewById(R.id.cbSame);
        cvBillAddress = view.findViewById(R.id.cvBillAddress);



        SpStateBill= view.findViewById(R.id.SpStateBill);
        spDistBill= view.findViewById(R.id.spDistBill);
        SpBillStateBill= view.findViewById(R.id.SpBillStateBill);
        spBillDistBill= view.findViewById(R.id.spBillDistBill);
        rlCom= view.findViewById(R.id.rlCom);



        mPrefs=Prefs.with(getActivity());

        ivNameErase= view.findViewById(R.id.ivNameErase);
        ivCareOfErase= view.findViewById(R.id.ivCareOfErase);
        ivPhnErase= view.findViewById(R.id.ivPhnErase);
        ivAddDet1Erase= view.findViewById(R.id.ivAddDet1Erase);
        ivAdd2Erase= view.findViewById(R.id.ivAdd2Erase);
        ivZipDetErase= view.findViewById(R.id.ivZipDetErase);
        ivCityDetErase= view.findViewById(R.id.ivCityDetErase);
        ivBillNameErase= view.findViewById(R.id.ivBillNameErase);
        ivBillCareOfErase= view.findViewById(R.id.ivBillCareOfErase);
        ivBillPhnErase= view.findViewById(R.id.ivBillPhnErase);
        ivBillAddDet1Erase= view.findViewById(R.id.ivBillAddDet1Erase);
        ivBillAdd2Erase= view.findViewById(R.id.ivBillAdd2Erase);
        ivBillZipDetErase= view.findViewById(R.id.ivBillZipDetErase);
        ivBillCityDetErase= view.findViewById(R.id.ivBillCityDetErase);

        cbSame.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                    cvBillAddress.setVisibility(View.GONE);
                else
                    cvBillAddress.setVisibility(View.VISIBLE);
            }
        });
    }

    public static Fragment newInstance(String s) {
        CommunicationFragment frag=new CommunicationFragment();
        return frag;
    }

    private void setAddress() {
        if(mPrefs.getString(Constants.Applyingfor,"myself").equals("myself"))
            etFirstMeet.setText("");
        else
            etFirstMeet.setText(mPrefs.getString(Constants.FirstName,""));
        etLastMeet.setText(mPrefs.getString(Constants.LastName,""));
        etContactMeet.setText(mPrefs.getString(Constants.Phone,""));
        etStreet1Meet.setText(mPrefs.getString(Constants.Street1,""));
        etStreet2Meet.setText(mPrefs.getString(Constants.Street2,""));
        etCityMeet.setText(mPrefs.getString(Constants.City,""));
        etZipMeet.setText(mPrefs.getString(Constants.Zip,""));
    }

    private void getStates() {
        GeneralFunctions.showDialog(getActivity());
        Call<StatesModel> call= RestClient.get().getAllStates("1000");
        call.enqueue(new Callback<StatesModel>() {
            @Override
            public void onResponse(Call<StatesModel> call, Response<StatesModel> response) {
                GeneralFunctions.dismissDialog();
                if (response.isSuccessful()) {
                    if(response.body().code.equals("401"))
                    {
                        GeneralFunctions.tokenExpireAction(getActivity());
                    }
                    else if (response.body().success == 1) {

                        states=response.body().data.objects;
                        setStates();
                        setBillingState();
                        //statesObj = response.body();
                        //rlStates.setAdapter(new StatesAdapter(StateSelectActivity.this, statesObj.data.objects));
/*
                        DbHelper db=new DbHelper(StateSelectActivity.this);
                        db.addStates(statesObj.data.objects);*/
                        //getAllDistricts();
                    } else {
                        GeneralFunctions.makeSnackbar(rlCom,response.body().message);
                    }

                }
                else  GeneralFunctions.makeSnackbar(rlCom,getResources().getString(R.string.serverIssue));
            }


            @Override
            public void onFailure(Call<StatesModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(rlCom,getResources().getString(R.string.netIssue));
            }
        });
    }

    private void setStates() {
        int stateId = 0;
        stateIds.add(0);
        spinnerStates.add(getResources().getString(R.string.select));
        for (int i = 0; i < states.size(); i++) {
            spinnerStates.add(states.get(i).state_name);
            stateIds.add(states.get(i).states_id);


            if (MyApplication.stateId == states.get(i).states_id)
                stateId = i + 1;
        }

        // CheckedTextView asd=new CheckedTextView(getActivity());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_title_text, spinnerStates) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the second item from Spinner
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the disable item text color
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        adapter.setDropDownViewResource(R.layout.spinner_adapter_value);
        SpStateBill.setAdapter(adapter);
        SpStateBill.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    isBilling=false;
                    String selected = (String) parent.getItemAtPosition(position);
                    int pos = spinnerStates.indexOf(selected);

                    stateSelected = 1;
                    stateSelectedString = selected;
                    billingMap.put("state", stateIds.get(pos).toString());
                    ((TextView) SpStateBill.getSelectedView()).setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                    spDistBill.setAdapter(null);
                    spinDistricts.clear();
                    spinDistricts = new ArrayList<>();
                    getDistrictsbyId(stateIds.get(pos));
                } else {
                    ((TextView) SpStateBill.getSelectedView()).setTextColor(ContextCompat.getColor(getActivity(), R.color.darkgrey));

                    /*if(mPrefs.getInt(Constants.StateId,0)>0)
                    {

                    }*/
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (stateId >= 0) {
            SpStateBill.setSelection(stateId);
            //  getDistrictsbyId(Integer.parseInt(mPrefs.getString(Constants.StateId,"")));
        }
    }



    private void getDistrictsbyId(Integer id)
    {
        GeneralFunctions.showDialog(getActivity());
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
                        districts=response.body().data.objects;
                        if (isBilling)
                            setBillDistricts();
                        else
                            setDistricts();
                    }
                    else GeneralFunctions.makeSnackbar(rlCom,response.body().message);
                }
                else GeneralFunctions.makeSnackbar(rlCom,getResources().getString(R.string.serverIssue));



            }

            @Override
            public void onFailure(Call<DistrictsModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(rlCom,getResources().getString(R.string.netIssue));
            }
        });
    }



    private void setBillingState()
    {
        int stateId = 0;
        stateIds.add(0);
        spinnerBillStates.add(getResources().getString(R.string.select));
        for (int i = 0; i < states.size(); i++) {
            spinnerBillStates.add(states.get(i).state_name);
            stateIds.add(states.get(i).states_id);


            if (MyApplication.stateId == states.get(i).states_id)
                stateId = i + 1;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_title_text, spinnerBillStates) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the second item from Spinner
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the disable item text color
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        adapter.setDropDownViewResource(R.layout.spinner_adapter_value);
        SpBillStateBill.setAdapter(adapter);
        SpBillStateBill.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    String selected = (String) parent.getItemAtPosition(position);
                    int pos = spinnerBillStates.indexOf(selected);

                    stateBillSelected = 1;
                    stateSelectedString = selected;
                    //   billingMap.put("state", stateIds.get(pos).toString());
                    ((TextView) SpBillStateBill.getSelectedView()).setTextColor
                            (ContextCompat.getColor(getActivity(), R.color.black));
                    spBillDistBill.setAdapter(null);
                    spinBillDistricts.clear();
                    spinBillDistricts = new ArrayList<>();
                    isBilling=true;
                    getDistrictsbyId(stateIds.get(pos));
                } else {
                    ((TextView) SpBillStateBill.getSelectedView()).setTextColor
                            (ContextCompat.getColor(getActivity(), R.color.darkgrey));

                    /*if(mPrefs.getInt(Constants.StateId,0)>0)
                    {

                    }*/
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setDistricts() {
        int distId=0;
        distIds.add(0);
        spinDistricts.add(getResources().getString(R.string.select));
        for(int i=0;i<districts.size();i++)
        {
            spinDistricts.add(districts.get(i).district_name);
            distIds.add(districts.get(i).districts_id);
            /*if(Integer.parseInt(mPrefs.getString(Constants.DistId,""))==districts.get(i).districts_id)
                distId=i+1;*/

            if(MyApplication.distId==districts.get(i).districts_id)
                distId=i+1;
        }
        ArrayAdapter<String> adapters=new ArrayAdapter<String>(getActivity(), R.layout.spinner_title_text,spinDistricts)
        {
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the second item from Spinner
                    return false;
                }
                else
                {
                    return true;
                }
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
        spDistBill.setAdapter(adapters);


        spDistBill.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0) {
                    String selected = (String) parent.getItemAtPosition(position);
                    int pos = spinDistricts.indexOf(selected);
                    distSelected=1;
                    districtSelectedString=selected;
                    billingMap.put("district",distIds.get(pos).toString());
                    ((TextView) spDistBill.getSelectedView()).setTextColor(ContextCompat.getColor(getActivity(),R.color.black));

                }

                else {
                    ((TextView) spDistBill.getSelectedView()).setTextColor(ContextCompat.getColor(getActivity(),R.color.darkgrey));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if(distId>0)
            spDistBill.setSelection(distId);

    }

    private void setBillDistricts() {
        int distId=0;
        distIds.add(0);
        spinBillDistricts.add(getResources().getString(R.string.select));
        for(int i=0;i<districts.size();i++)
        {
            spinBillDistricts.add(districts.get(i).district_name);
            distIds.add(districts.get(i).districts_id);
            /*if(Integer.parseInt(mPrefs.getString(Constants.DistId,""))==districts.get(i).districts_id)
                distId=i+1;*/

            if(MyApplication.distId==districts.get(i).districts_id)
                distId=i+1;
        }
        ArrayAdapter<String> adapters=new ArrayAdapter<String>(getActivity(), R.layout.spinner_title_text,spinBillDistricts)
        {
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the second item from Spinner
                    return false;
                }
                else
                {
                    return true;
                }
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
        spBillDistBill.setAdapter(adapters);


        spBillDistBill.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0) {
                    String selected = (String) parent.getItemAtPosition(position);
                    int pos = spinBillDistricts.indexOf(selected);
                    distBillSelected=1;
                    districtSelectedString=selected;
                    billingMap.put("district",distIds.get(pos).toString());
                    ((TextView) spBillDistBill.getSelectedView()).setTextColor(ContextCompat.getColor(getActivity(),R.color.black));

                }

                else {
                    ((TextView) spBillDistBill.getSelectedView())
                            .setTextColor(ContextCompat.getColor(getActivity(),R.color.darkgrey));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if(distId>0)
            spBillDistBill.setSelection(distId);

    }

    public boolean checkBlank() {
        billingMap.put("country","1");
        if(etFirstMeet.getText().toString().trim().isEmpty())
        {
            GeneralFunctions.makeSnackbar(rlCom,getResources().getString(R.string.plentername));
            return false;
        }
        else {
            billingMap.put("first_name",etFirstMeet.getText().toString());

        }

        if(etLastMeet.getText().toString().trim().isEmpty())
        {
            GeneralFunctions.makeSnackbar(rlCom,getResources().getString(R.string.plcareof));
            return false;
        }
        else {
            billingMap.put("last_name",etLastMeet.getText().toString());

        }

        if(etContactMeet.getText().toString().trim().isEmpty())
        {
            GeneralFunctions.makeSnackbar(rlCom,getResources().getString(R.string.enterNumber));
            return false;
        }
        else {}

        if(etStreet1Meet.getText().toString().trim().isEmpty())
        {
            GeneralFunctions.makeSnackbar(rlCom,getResources().getString(R.string.pladd1));
            return false;
        }
        else {
            billingMap.put("street1",etStreet1Meet.getText().toString());

        }

        if(etStreet2Meet.getText().toString().trim().isEmpty())
        {
            GeneralFunctions.makeSnackbar(rlCom,getResources().getString(R.string.pladd2));
            return false;
        }
        else {
            billingMap.put("street2",etStreet2Meet.getText().toString());

        }


        if(etCityMeet.getText().toString().trim().isEmpty())
        {
            GeneralFunctions.makeSnackbar(rlCom,getResources().getString(R.string.plcity));
            return false;
        }
        else {
            billingMap.put("city",etCityMeet.getText().toString());

        }

        if(stateSelected==0)
        {
            GeneralFunctions.makeSnackbar(rlCom,getResources().getString(R.string.plstate));
            return false;
        }


        if(distSelected==0)
        {
            GeneralFunctions.makeSnackbar(rlCom,getResources().getString(R.string.pldist));
            return false;
        }


        if(etZipMeet.getText().toString().trim().isEmpty())
        {
            GeneralFunctions.makeSnackbar(rlCom,getResources().getString(R.string.plzip));
            return false;
        }
        else {
            billingMap.put("zip",etZipMeet.getText().toString());

        }









    /*    if(etBillFirstMeet.getText().toString().trim().isEmpty())
        {
            GeneralFunctions.makeSnackbar(rlCom,getResources().getString(R.string.plentername));
            return false;
        }
        if(etBillLastMeet.getText().toString().trim().isEmpty())
        {
            GeneralFunctions.makeSnackbar(rlCom,getResources().getString(R.string.plcareof));
            return false;
        }
        else {
            billingMap.put("last_name",etLastMeet.getText().toString());

        }

        if(etBillContactMeet.getText().toString().trim().isEmpty())
        {
            GeneralFunctions.makeSnackbar(rlCom,getResources().getString(R.string.enterNumber));
            return false;
        }
        else {}

        if(etBillStreet1Meet.getText().toString().trim().isEmpty())
        {
            GeneralFunctions.makeSnackbar(rlCom,getResources().getString(R.string.pladd1));
            return false;
        }
        else {
            billingMap.put("street1",etStreet1Meet.getText().toString());

        }

        if(etBillStreet2Meet.getText().toString().trim().isEmpty())
        {
            GeneralFunctions.makeSnackbar(rlCom,getResources().getString(R.string.pladd2));
            return false;
        }
        else {
            billingMap.put("street2",etStreet2Meet.getText().toString());

        }


        if(etBillCityMeet.getText().toString().trim().isEmpty())
        {
            GeneralFunctions.makeSnackbar(rlCom,getResources().getString(R.string.plcity));
            return false;
        }
        else {
            billingMap.put("city",etCityMeet.getText().toString());

        }

        if(stateSelected==0)
        {
            GeneralFunctions.makeSnackbar(rlCom,getResources().getString(R.string.plstate));
            return false;
        }


        if(distSelected==0)
        {
            GeneralFunctions.makeSnackbar(rlCom,getResources().getString(R.string.pldist));
            return false;
        }


        if(etBillZipMeet.getText().toString().trim().isEmpty())
        {
            GeneralFunctions.makeSnackbar(rlCom,getResources().getString(R.string.plzip));
            return false;
        }
        else {
            billingMap.put("zip",etZipMeet.getText().toString());

        }*/
        return true;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int i = v.getId();
        if (i == R.id.etFirstMeet) {
            if (hasFocus) {
                setEditackGround(etFirstMeet);
                if (etFirstMeet.getText().toString().length() > 0)
                    ivNameErase.setVisibility(View.VISIBLE);
            } else ivNameErase.setVisibility(View.GONE);


        } else if (i == R.id.etLastMeet) {
            if (hasFocus) {
                setEditackGround(etLastMeet);
                if (etLastMeet.getText().toString().length() > 0)
                    ivCareOfErase.setVisibility(View.VISIBLE);
            } else ivCareOfErase.setVisibility(View.GONE);

        } else if (i == R.id.etStreet2Meet) {
            if (hasFocus) {
                setEditackGround(etStreet2Meet);
                if (etStreet2Meet.getText().toString().length() > 0)
                    ivAdd2Erase.setVisibility(View.VISIBLE);
            } else ivAdd2Erase.setVisibility(View.GONE);

        } else if (i == R.id.etStreet1Meet) {
            if (hasFocus) {
                setEditackGround(etStreet1Meet);
                if (etStreet1Meet.getText().toString().length() > 0)
                    ivAddDet1Erase.setVisibility(View.VISIBLE);
            } else ivAddDet1Erase.setVisibility(View.GONE);

        } else if (i == R.id.etZipMeet) {
            if (hasFocus) {
                setEditackGround(etZipMeet);
                if (etZipMeet.getText().toString().length() > 0)
                    ivZipDetErase.setVisibility(View.VISIBLE);

            } else
                ivZipDetErase.setVisibility(View.GONE);

        } else if (i == R.id.etContactMeet) {
            if (hasFocus) {
                setEditackGround(etContactMeet);
                if (etContactMeet.getText().toString().length() > 0)
                    ivPhnErase.setVisibility(View.VISIBLE);
            } else
                ivPhnErase.setVisibility(View.GONE);

        } else if (i == R.id.etCityMeet) {
            if (hasFocus) {
                setEditackGround(etCityMeet);
                if (etCityMeet.getText().toString().length() > 0)
                    ivCityDetErase.setVisibility(View.VISIBLE);
            } else ivCityDetErase.setVisibility(View.GONE);


        } else if (i == R.id.etBillFirstMeet) {
            if (hasFocus) {
                setEditackGround(etBillFirstMeet);
                if (etBillFirstMeet.getText().toString().length() > 0) {

                    ivBillNameErase.setVisibility(View.VISIBLE);

                }
            } else ivBillNameErase.setVisibility(View.GONE);


        } else if (i == R.id.etBillLastMeet) {
            if (hasFocus) {
                setEditackGround(etBillLastMeet);
                if (etBillLastMeet.getText().toString().length() > 0) {
                    ivBillCareOfErase.setVisibility(View.VISIBLE);

                }
            } else ivBillCareOfErase.setVisibility(View.GONE);

        } else if (i == R.id.etBillStreet2Meet) {
            if (hasFocus) {
                setEditackGround(etBillStreet2Meet);
                if (etBillStreet2Meet.getText().toString().length() > 0) {
                    ivBillAdd2Erase.setVisibility(View.VISIBLE);

                }
            } else
                ivBillAdd2Erase.setVisibility(View.GONE);

        } else if (i == R.id.etBillStreet1Meet) {
            if (hasFocus) {
                setEditackGround(etBillStreet1Meet);
                if (etBillStreet1Meet.getText().toString().length() > 0) {
                    ivBillAddDet1Erase.setVisibility(View.VISIBLE);

                }
            } else ivBillAddDet1Erase.setVisibility(View.GONE);

        } else if (i == R.id.etBillZipMeet) {
            if (hasFocus) {
                setEditackGround(etBillZipMeet);
                if (etBillZipMeet.getText().toString().length() > 0) {
                    ivBillZipDetErase.setVisibility(View.VISIBLE);

                }
            } else
                ivBillZipDetErase.setVisibility(View.GONE);

        } else if (i == R.id.etBillContactMeet) {
            if (hasFocus) {
                setEditackGround(etBillContactMeet);
                if (etBillContactMeet.getText().toString().length() > 0) {
                    ivBillPhnErase.setVisibility(View.VISIBLE);

                }
            } else
                ivBillPhnErase.setVisibility(View.GONE);

        } else if (i == R.id.etBillCityMeet) {
            if (hasFocus) {
                setEditackGround(etBillCityMeet);
                if (etBillCityMeet.getText().toString().length() > 0) {
                    ivBillCityDetErase.setVisibility(View.VISIBLE);

                }
            } else ivBillCityDetErase.setVisibility(View.GONE);

        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.ivNameErase) {
            etFirstMeet.setText("");
            etFirstMeet.requestFocus();

        } else if (i == R.id.ivCareOfErase) {
            etLastMeet.setText("");
            etLastMeet.requestFocus();

        } else if (i == R.id.ivPhnErase) {
            etContactMeet.setText("");
            etContactMeet.requestFocus();

        } else if (i == R.id.ivAddDet1Erase) {
            etStreet1Meet.setText("");
            etStreet1Meet.requestFocus();

        } else if (i == R.id.ivAdd2Erase) {
            etStreet2Meet.setText("");
            etStreet2Meet.requestFocus();

        } else if (i == R.id.ivCityDetErase) {
            etCityMeet.setText("");
            etContactMeet.requestFocus();

        } else if (i == R.id.ivZipDetErase) {
            etZipMeet.setText("");
            etZipMeet.requestFocus();

        } else if (i == R.id.ivBillNameErase) {
            etBillFirstMeet.setText("");
            etBillFirstMeet.requestFocus();

        } else if (i == R.id.ivBillCareOfErase) {
            etBillLastMeet.setText("");
            etBillLastMeet.requestFocus();

        } else if (i == R.id.ivBillPhnErase) {
            etBillContactMeet.setText("");
            etBillContactMeet.requestFocus();

        } else if (i == R.id.ivBillAddDet1Erase) {
            etBillStreet1Meet.setText("");
            etBillStreet1Meet.requestFocus();

        } else if (i == R.id.ivBillAdd2Erase) {
            etBillStreet2Meet.setText("");
            etBillStreet2Meet.requestFocus();

        } else if (i == R.id.ivBillCityDetErase) {
            etBillCityMeet.setText("");
            etBillContactMeet.requestFocus();

        } else if (i == R.id.ivBillZipDetErase) {
            etBillZipMeet.setText("");
            etBillZipMeet.requestFocus();

        }
    }

    private void setEditackGround(EditText editBack)
    {
        etFirstMeet.setBackgroundResource(R.drawable.edittextunfocused);
        etLastMeet.setBackgroundResource(R.drawable.edittextunfocused);
        etContactMeet.setBackgroundResource(R.drawable.edittextunfocused);
        etStreet1Meet.setBackgroundResource(R.drawable.edittextunfocused);
        etStreet2Meet.setBackgroundResource(R.drawable.edittextunfocused);
        etCityMeet.setBackgroundResource(R.drawable.edittextunfocused);
        etZipMeet.setBackgroundResource(R.drawable.edittextunfocused);
        etBillFirstMeet.setBackgroundResource(R.drawable.edittextunfocused);
        etBillLastMeet.setBackgroundResource(R.drawable.edittextunfocused);
        etBillContactMeet.setBackgroundResource(R.drawable.edittextunfocused);
        etBillStreet1Meet.setBackgroundResource(R.drawable.edittextunfocused);
        etBillStreet2Meet.setBackgroundResource(R.drawable.edittextunfocused);
        etBillCityMeet.setBackgroundResource(R.drawable.edittextunfocused);
        etBillZipMeet.setBackgroundResource(R.drawable.edittextunfocused);

        editBack.setBackgroundResource(R.drawable.edittectfocused);
    }

    /*private boolean checkWhetherAddressIsSame()
    {
        if(!etFirstMeet.getText().toString().matches(etBillFirstMeet.getText().toString()))
        {
            return false;
        }
        else if(!etLastMeet.getText().toString().matches(etBillLastMeet.getText().toString()))
        {
            return false;
        }
        else if(!etContactMeet.getText().toString().matches(etBillContactMeet.getText().toString()))
        {
            return false;
        }
        else if(!etStreet1Meet.getText().toString().matches(etBillStreet1Meet.getText().toString()))
        {
            return false;
        }
        else if(!etStreet2Meet.getText().toString().matches(etBillStreet2Meet.getText().toString()))
        {
            return false;
        }
        else if(!etCityMeet.getText().toString().matches(etBillCityMeet.getText().toString()))
        {
            return false;
        }
        else if(!etZipMeet.getText().toString().matches(etBillZipMeet.getText().toString()))
        {
            return false;
        }
        else
        {
            return true;
        }
    }


     private void editTextIssueResolve()
    {
        etFirstMeet.clearFocus();
        etLastMeet.clearFocus();
        etContactMeet.clearFocus();
        etStreet1Meet.clearFocus();
        etStreet2Meet.clearFocus();
        etCityMeet.clearFocus();
        etZipMeet.clearFocus();

        ivNameErase.setVisibility(View.GONE);
        ivAddDet1Erase.setVisibility(View.GONE);
        ivAdd2Erase.setVisibility(View.GONE);
        ivZipDetErase.setVisibility(View.GONE);
        ivCityDetErase.setVisibility(View.GONE);
        ivCareOfErase.setVisibility(View.GONE);
        ivPhnErase.setVisibility(View.GONE);;

    }*/
}
