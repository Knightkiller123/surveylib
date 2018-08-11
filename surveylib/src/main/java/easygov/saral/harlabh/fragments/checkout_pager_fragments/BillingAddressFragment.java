package easygov.saral.harlabh.fragments.checkout_pager_fragments;


import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import easygov.saral.harlabh.activity.FifthPaymentActivity;
import easygov.saral.harlabh.models.responsemodels.statesndistricts.DistrictsModel;
import easygov.saral.harlabh.utils.GeneralFunctions;
import easygov.saral.harlabh.utils.MyApplication;
import easygov.saral.harlabh.utils.Prefs;
import easygov.saral.harlabh.utils.RestClient;
import easygov.saral.harlabh.R;
import easygov.saral.harlabh.models.responsemodels.statesndistricts.DistrictDetails;
import easygov.saral.harlabh.models.responsemodels.statesndistricts.StateDetails;
import easygov.saral.harlabh.models.responsemodels.statesndistricts.StatesModel;
import easygov.saral.harlabh.utils.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



import static easygov.saral.harlabh.fragments.storyboardfrags.CheckoutFragment.billingMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class BillingAddressFragment extends Fragment implements View.OnFocusChangeListener, View.OnClickListener {

    private List<StateDetails> states;
    private EditText etFirstMeet , etLastMeet , etContactMeet,etStreet1Meet,etStreet2Meet,etCityMeet,etZipMeet;
  //  private int first,last,contact,street1,street2,city,state,dist,zip;
    private Spinner SpStateBill,spDistBill;
    private ScrollView svCom;
    private int stateSelected=0,distSelected=0;
    List<String> spinnerStates=new ArrayList<>();
    List<Integer> stateIds=new ArrayList<>();
    private List<Integer> distIds=new ArrayList<>();
    private List<String> spinDistricts=new ArrayList<>();
    private List<DistrictDetails> districts =new ArrayList<>();
    private Prefs mPrefs;
    private String stateSelectedString="",districtSelectedString="";
    private CheckBox cbSame;
    private RadioGroup rgCommAdd;
    private LinearLayout llMeetingCheck,llMeetingAdd;
    private RadioButton rbSame;
    private RelativeLayout rlBillingFrag;

    private ImageView ivNameErase, ivAddDet1Erase,ivAdd2Erase,ivZipDetErase,ivCityDetErase ,ivCareOfErase,ivPhnErase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_meeting_date_time, container, false);

        init(view);

        return view;
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible) {
            setAddress();
            getStates();
            setEditListeners();

            setFocusListeners();
           editTextIssueResolve();
        }
    }






    private void setAddress() {
        etFirstMeet.setText(mPrefs.getString(Constants.FirstName,""));
        etLastMeet.setText(mPrefs.getString(Constants.LastName,""));
        etContactMeet.setText(mPrefs.getString(Constants.Phone,""));
        etStreet1Meet.setText(mPrefs.getString(Constants.Street1,""));
        etStreet2Meet.setText(mPrefs.getString(Constants.Street2,""));
        etCityMeet.setText(mPrefs.getString(Constants.City,""));
        etZipMeet.setText(mPrefs.getString(Constants.Zip,""));



    }

    private void setBillingAddressModel()
    {
        mPrefs.save(Constants.BillFirst,etFirstMeet.getText().toString());
        mPrefs.save(Constants.BillLastName,etLastMeet.getText().toString());
        mPrefs.save(Constants.BillStreet1,etStreet1Meet.getText().toString());
        mPrefs.save(Constants.BillStreet2,etStreet2Meet.getText().toString());
        mPrefs.save(Constants.BillCity,etCityMeet.getText().toString());
        mPrefs.save(Constants.BillZip,etZipMeet.getText().toString());
        mPrefs.save(Constants.BillContact,etContactMeet.getText().toString());
        mPrefs.save(Constants.BillCountry,"India");
        mPrefs.save(Constants.BillState,stateSelectedString);
        mPrefs.save(Constants.BillDistrict,districtSelectedString);

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
                        //statesObj = response.body();
                        //rlStates.setAdapter(new StatesAdapter(StateSelectActivity.this, statesObj.data.objects));
/*
                        DbHelper db=new DbHelper(StateSelectActivity.this);
                        db.addStates(statesObj.data.objects);*/
                        //getAllDistricts();
                    } else {

                    }

                }
            }


            @Override
            public void onFailure(Call<StatesModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(rlBillingFrag,getResources().getString(R.string.netIssue));
            }
        });
    }

    private void setStates() {
        int stateId=0;
        stateIds.add(0);
        spinnerStates.add(getResources().getString(R.string.select));
        for (int i=0;i<states.size();i++)
        {
            spinnerStates.add(states.get(i).state_name);
            stateIds.add(states.get(i).states_id);
            /*if(Integer.parseInt(mPrefs.getString(Constants.StateId,""))==states.get(i).states_id)
                stateId=i+1;*/

            if(MyApplication.stateId==states.get(i).states_id)
                stateId=i+1;
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
        SpStateBill.setAdapter(adapter);



        SpStateBill.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0) {
                    String selected = (String) parent.getItemAtPosition(position);
                    int pos = spinnerStates.indexOf(selected);
                    stateSelected=1;
                    stateSelectedString=selected;
                    billingMap.put("billing_state",stateIds.get(pos).toString());
                    ((TextView) SpStateBill.getSelectedView()).setTextColor(ContextCompat.getColor(getActivity(),R.color.black));
                    spDistBill.setAdapter(null);
                    spinDistricts.clear();
                    spinDistricts=new ArrayList<String>();
                    getDistrictsbyId(stateIds.get(pos));
                }

                else {
                    ((TextView) SpStateBill.getSelectedView()).setTextColor(ContextCompat.getColor(getActivity(),R.color.darkgrey));

                    /*if(mPrefs.getInt(Constants.StateId,0)>0)
                    {

                    }*/
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if(stateId>=0) {
            SpStateBill.setSelection(stateId);
            //  getDistrictsbyId(Integer.parseInt(mPrefs.getString(Constants.StateId,"")));
        }





    }


    private void init(View view) {
        etFirstMeet = view.findViewById(R.id.etFirstMeet);
        etLastMeet = view.findViewById(R.id.etLastMeet);
        etContactMeet = view.findViewById(R.id.etContactMeet);
        etStreet1Meet = view.findViewById(R.id.etStreet1Meet);
        etStreet2Meet = view.findViewById(R.id.etStreet2Meet);
        etCityMeet = view.findViewById(R.id.etCityMeet);
        etZipMeet = view.findViewById(R.id.etZipMeet);
        SpStateBill= view.findViewById(R.id.SpStateBill);
        spDistBill= view.findViewById(R.id.spDistBill);
        mPrefs=Prefs.with(getActivity());
        rlBillingFrag = view.findViewById(R.id.rlBillingFrag);
        svCom= view.findViewById(R.id.svCom);
        llMeetingCheck= view.findViewById(R.id.llMeetingCheck);
        llMeetingAdd= view.findViewById(R.id.llMeetingAdd);
        rgCommAdd= view.findViewById(R.id.rgCommAdd);


        ivNameErase= view.findViewById(R.id.ivNameErase);
        ivCareOfErase= view.findViewById(R.id.ivCareOfErase);
        ivPhnErase= view.findViewById(R.id.ivPhnErase);
        ivAddDet1Erase= view.findViewById(R.id.ivAddDet1Erase);
        ivAdd2Erase= view.findViewById(R.id.ivAdd2Erase);
        ivZipDetErase= view.findViewById(R.id.ivZipDetErase);
        ivCityDetErase= view.findViewById(R.id.ivCityDetErase);


        rbSame= view.findViewById(R.id.rbSame);
        cbSame= view.findViewById(R.id.cbSame);





        llMeetingAdd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                GeneralFunctions.hideSoftKeyboard(getActivity());
                return false;
            }
        });

        rgCommAdd.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.rbSame) {
                    llMeetingCheck.setVisibility(View.VISIBLE);
                    cbSame.setVisibility(View.GONE);
                    llMeetingAdd.setVisibility(View.GONE);
                    svCom.setVisibility(View.GONE);

                } else if (checkedId == R.id.rbDiff) {
                    llMeetingCheck.setVisibility(View.GONE);
                    cbSame.setVisibility(View.VISIBLE);
                    llMeetingAdd.setVisibility(View.VISIBLE);
                    svCom.setVisibility(View.VISIBLE);

                }
            }
        });
        cbSame.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    llMeetingCheck.setVisibility(View.VISIBLE);
                    llMeetingAdd.setVisibility(View.GONE);
                    svCom.setVisibility(View.GONE);
                    cbSame.setVisibility(View.GONE);
                    cbSame.setChecked(false);
                    rbSame.setChecked(true);
                    ((FifthPaymentActivity)getContext()).scrollTo();

                }
            }
        });
        //etStateMeet= (EditText) view.findViewById(R.id.etStateMeet);
       // etDistMeet= (EditText) view.findViewById(R.id.etDistMeet);

    }

    public static Fragment newInstance(String s) {
        BillingAddressFragment frag=new BillingAddressFragment();
        return frag;
    }

    public Boolean checkBlank()
    {
        billingMap.put("hdnIsAskForApplicantOfBilling","0");
        billingMap.put("billing_country","1");
        if(etFirstMeet.getText().toString().trim().isEmpty())
        {
            GeneralFunctions.makeSnackbar(rlBillingFrag,getResources().getString(R.string.plentername));
            return false;
        }

        else {
            billingMap.put("billing_first_name",etFirstMeet.getText().toString());
        }

        if(etLastMeet.getText().toString().trim().isEmpty())
        {
            GeneralFunctions.makeSnackbar(rlBillingFrag,getResources().getString(R.string.plcareof));
            return false;
        }
        else {
            billingMap.put("billing_last_name",etLastMeet.getText().toString());
        }

        if(etContactMeet.getText().toString().trim().isEmpty())
        {
            GeneralFunctions.makeSnackbar(rlBillingFrag,getResources().getString(R.string.enterNumber));
            return false;
        }
        else {}


        if(etStreet1Meet.getText().toString().trim().isEmpty())
        {
            GeneralFunctions.makeSnackbar(rlBillingFrag,getResources().getString(R.string.pladd1));
            return false;
        }
        else {
            billingMap.put("billing_street1",etStreet1Meet.getText().toString());
        }

        if(etStreet2Meet.getText().toString().trim().isEmpty())
        {
            GeneralFunctions.makeSnackbar(rlBillingFrag,getResources().getString(R.string.pladd2));
            return false;
        }
        else {
            billingMap.put("billing_street2",etStreet2Meet.getText().toString());

        }


        if(etCityMeet.getText().toString().trim().isEmpty())
        {
            GeneralFunctions.makeSnackbar(rlBillingFrag,getResources().getString(R.string.plcity));

            return false;
        }
        else {
            billingMap.put("billing_city",etCityMeet.getText().toString());

        }

       if(stateSelected==0)
        {
            GeneralFunctions.makeSnackbar(rlBillingFrag,getResources().getString(R.string.plstate));

            return false;
        }


        if(distSelected==0)
        {
            GeneralFunctions.makeSnackbar(rlBillingFrag,getResources().getString(R.string.pldist));
            return false;
        }


        if(etZipMeet.getText().toString().trim().isEmpty())
        {
            GeneralFunctions.makeSnackbar(rlBillingFrag,getResources().getString(R.string.plzip));

            return false;
        }
        else {
            billingMap.put("billing_zip",etZipMeet.getText().toString());

        }

        setBillingAddressModel();

        return true;
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
                    else  if(response.body().success==1)
                    {


                        districts=response.body().data.objects;
                        setDistricts();

                    }
                }



            }

            @Override
            public void onFailure(Call<DistrictsModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(rlBillingFrag,getResources().getString(R.string.netIssue));

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
        spDistBill.setAdapter(adapters);


        spDistBill.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0) {
                    String selected = (String) parent.getItemAtPosition(position);
                    int pos = spinDistricts.indexOf(selected);
                    distSelected=1;
                    districtSelectedString=selected;
                    billingMap.put("billing_district",distIds.get(pos).toString());
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

    public Boolean toValidateBilling()
    {
        return llMeetingCheck.getVisibility() == View.VISIBLE;
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

        editBack.setBackgroundResource(R.drawable.edittectfocused);
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
        ivPhnErase.setVisibility(View.GONE);

    }

    private void setEditListeners() {




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
}
