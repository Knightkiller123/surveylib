package easygov.saral.harlabh.fragments.application_pager_fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import easygov.saral.harlabh.activity.FifthPaymentActivity;
import easygov.saral.harlabh.adapters.ApplicationDetailAdapter;
import easygov.saral.harlabh.models.responsemodels.mandatoryfields.MandatoryDetails;
import easygov.saral.harlabh.models.responsemodels.savemandatoryfields.SaveMandatoryFields;
import easygov.saral.harlabh.utils.GeneralFunctions;
import easygov.saral.harlabh.utils.Prefs;
import easygov.saral.harlabh.utils.RestClient;
import easygov.saral.harlabh.R;
import easygov.saral.harlabh.activity.SplashActivity;
import easygov.saral.harlabh.utils.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ApplicationDetailFragment extends Fragment {


    private RecyclerView rlDetails;
    private TextView tvNextUserDetails;
    private Prefs mPrefs;
    private List<MandatoryDetails> details;
    private LinearLayoutManager manager;
    private ApplicationDetailAdapter adapter;
    private Pattern ePat;
    private RelativeLayout rlApplicaionDetail;
    private String bankAccuntNo="",confirmBankAccuntNo="";

    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_application_detail, container, false);
        //((FourthApplicationActivity)getActivity()).setDetailsTag("form");
        // rootView = view;
        innitialize(view);


        return view;
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if(menuVisible) {
            // if(details!=null)
            setData();
        }
    }

    private void innitialize(View view) {
        rlDetails= view.findViewById(R.id.rlDetails);
        manager=new LinearLayoutManager(getActivity());
        rlDetails.setLayoutManager(manager);
        mPrefs=Prefs.with(getActivity());
        rlApplicaionDetail= view.findViewById(R.id.rlApplicaionDetail);
        tvNextUserDetails= view.findViewById(R.id.tvNextUserDetails);
        details = new Gson().fromJson(mPrefs.getString(Constants.MandatoryList,""), new TypeToken<List<MandatoryDetails>>() {}.getType());

        ePat=android.util.Patterns.EMAIL_ADDRESS;
        tvNextUserDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkValidate())
                    hitSaveMandatoryFields();
            }
        });


        rlDetails.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                GeneralFunctions.hideSoftKeyboard(getActivity());
                return false;
            }
        });
    }

    public void setData()
    {
        details = new Gson().fromJson(mPrefs.getString(Constants.MandatoryList,""), new TypeToken<List<MandatoryDetails>>() {}.getType());
        if(details!=null)
        {
            rlDetails.setItemViewCacheSize(details.size());
            adapter=new ApplicationDetailAdapter(getActivity(),details,manager);
            rlDetails.setAdapter(adapter);

        }
    }



    private boolean checkValidate()
    {
        bankAccuntNo="";
        confirmBankAccuntNo="";
        for(int i=0;i<details.size();i++)
        {
            if(details.get(i).validate==true)
            {
                if(mPrefs.getString(details.get(i).id+"_"+details.get(i).field__system_name,"").isEmpty())
                {
                    GeneralFunctions.makeSnackbar(rlApplicaionDetail,getResources().getString(R.string.plenter)+" "+details.get(i).display_name);
                    rlDetails.getLayoutManager().smoothScrollToPosition(rlDetails,new RecyclerView.State(),i);
                    //   adapter.setErrorBox(i);
                    return false;
                }
            }

            if(details.get(i).rule_type!=null)
            {
                if(details.get(i).rule_type.equals("contact"))
                {
                    if(mPrefs.getString(details.get(i).id+"_"+details.get(i).field__system_name,"").length()!=10)
                    {
                        GeneralFunctions.makeSnackbar(rlApplicaionDetail,getResources().getString(R.string.invalidnumber));
                        rlDetails.getLayoutManager().smoothScrollToPosition(rlDetails,new RecyclerView.State(),i);
                        return false;
                    }
                }

                if(details.get(i).rule_type.equals("pincode"))
                {
                    if(mPrefs.getString(details.get(i).id+"_"+details.get(i).field__system_name,"").length()!=6)
                    {
                        GeneralFunctions.makeSnackbar(rlApplicaionDetail,getResources().getString(R.string.plzipvalid));

                        rlDetails.getLayoutManager().smoothScrollToPosition(rlDetails,new RecyclerView.State(),i);
                        return false;
                    }
                }

                if(details.get(i).rule_type.equals("height"))
                {
                    if(mPrefs.getString(details.get(i).id+"_"+details.get(i).field__system_name,"").length()<=2&&mPrefs.getString(details.get(i).id+"_"+details.get(i).field__system_name,"").length()>=3)
                    {
                        GeneralFunctions.makeSnackbar(rlApplicaionDetail,getResources().getString(R.string.plvalidheight));
                        rlDetails.getLayoutManager().smoothScrollToPosition(rlDetails,new RecyclerView.State(),i);
                        return false;
                    }
                }

                if(details.get(i).rule_type.equals("age"))
                {
                    if(mPrefs.getString(details.get(i).id+"_"+details.get(i).field__system_name,"").length()>2)
                    {
                        GeneralFunctions.makeSnackbar(rlApplicaionDetail,getResources().getString(R.string.plvalidage));

                        rlDetails.getLayoutManager().smoothScrollToPosition(rlDetails,new RecyclerView.State(),i);
                        return false;
                    }
                }

                if(details.get(i).rule_type.equals("email"))
                {

                    if(!ePat.matcher(mPrefs.getString(details.get(i).id+"_"+details.get(i).field__system_name,"")).matches())
                    {
                        GeneralFunctions.makeSnackbar(rlApplicaionDetail,getResources().getString(R.string.plemail));
                        return false;
                    }
                }
                if(details.get(i).rule_type.equals("bankaccount"))
                {
                   /* hfhj
                    if(!ePat.matcher(mPrefs.getString(details.get(i).id+"_"+details.get(i).field__system_name,"")).matches())
                    {
                        GeneralFunctions.makeSnackbar(rlApplicaionDetail,getResources().getString(R.string.placontNo));
                        return false;
                    }
                    else
                    {*/
                        if(!bankAccuntNo.isEmpty())
                            confirmBankAccuntNo=mPrefs.getString(details.get(i).id+"_"+details.get(i).field__system_name,"");
                        else
                            bankAccuntNo=mPrefs.getString(details.get(i).id+"_"+details.get(i).field__system_name,"");
                   // }
                }

            }
        }


        if (!bankAccuntNo.isEmpty() && !bankAccuntNo.matches(confirmBankAccuntNo))
        {
            GeneralFunctions.makeSnackbar(rlApplicaionDetail,getString(R.string.account_mismatch));
            return false;
        }

        return true;
    }

    public boolean emailValidator(String email)
    {
        Pattern pattern;
        Matcher matcher;
        // String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        pattern = Pattern.compile(emailPattern);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void hitSaveMandatoryFields() {
        GeneralFunctions.showDialog(getActivity());
        final Map<String ,String> map =new HashMap<>();
        for(int i=0;i<details.size();i++)
        {
            if(details.get(i).field__system_name!=null)
                map.put(details.get(i).id+"_"+details.get(i).field__system_name,mPrefs.getString(details.get(i).id+"_"+details.get(i).field__system_name,""));
        }
        map.put("serviceid",mPrefs.getString(Constants.ServiceId,""));
        map.put("userserviceid",mPrefs.getString(Constants.UserServiceID,""));
        map.put("survey_id",mPrefs.getString(Constants.SurveyId,""));

        Call<SaveMandatoryFields> call= RestClient.get().saveMandatoryFields(map);
        call.enqueue(new Callback<SaveMandatoryFields>() {
            @Override
            public void onResponse(Call<SaveMandatoryFields> call, Response<SaveMandatoryFields> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful())
                {
                    if(response.body().code.equals("401"))
                    {
                        GeneralFunctions.tokenExpireAction(getActivity());
                    }
                    else if(response.body().success==1)
                    {

                        //Todo: Haryana remove below two line
                        mPrefs.save(Constants.Automated,"YES");
                        mPrefs.save(Constants.FormExist,"YES");
                        if(response.body().data.next_link.equals("payment_gateway")&&response.body().data.payment_detail!=null)
                        {
                            mPrefs.save(Constants.MerchantId, response.body().data.payment_detail.merchantTxnId);
                            mPrefs.save(Constants.Signature, response.body().data.payment_detail.signature);
                            mPrefs.save(Constants.ReturnUrl, response.body().data.payment_detail.returnUrl);
                            mPrefs.save(Constants.NotifyUrl, response.body().data.payment_detail.notifyUrl);

                            if(response.body().data.payment_detail.contact!=null)
                            {mPrefs.save(Constants.Contact, response.body().data.payment_detail.contact);}

                            else {mPrefs.save(Constants.Contact,"");}

                            if(response.body().data.payment_detail.email!=null)
                            { mPrefs.save(Constants.Email, response.body().data.payment_detail.email);}

                            else { mPrefs.save(Constants.Email, "");}

                            mPrefs.save(Constants.PaymentUrl, response.body().data.payment_detail.action);
                            mPrefs.save(Constants.Currency, response.body().data.payment_detail.currency);
                            mPrefs.save(Constants.OrderAmount, response.body().data.payment_detail.amount.toString());
                        }
                        else if(response.body().data.next_link.equals("payment_gateway")){
                            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                        }




                        //Todo: next link hardcoded , instructions needed , backend unable to deciphr what is the bug
                        //mPrefs.save(Constants.NextLink,response.body().data.next_link);
                        mPrefs.save(Constants.NextLink,"meeting_address");
                        Intent intent =new Intent(getActivity(), FifthPaymentActivity.class);
                        //clearApplicantDetails();
                        startActivity(intent);
                        //getActivity().finish();
                    }
                    else {
                        GeneralFunctions.makeSnackbar(rlApplicaionDetail,response.body().message);

                    }
                }
                else GeneralFunctions.makeSnackbar(rlApplicaionDetail,getResources().getString(R.string.serverIssue));
            }

            @Override
            public void onFailure(Call<SaveMandatoryFields> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(rlApplicaionDetail,getResources().getString(R.string.netIssue));
            }
        });
    }

    public void clearApplicantDetails() {
        try {
            for(int i=0;i<details.size();i++)
            {
                mPrefs.remove(details.get(i).id+"_"+details.get(i).field__system_name);
            }
        }
        catch (Exception e)
        {}

    }


    public static Fragment newInstance(String s) {
        ApplicationDetailFragment frag=new ApplicationDetailFragment();
        return frag;
    }


    /*@Override
    public void onDestroyView() {
        if (rootView.getParent() != null) {
            ((ViewGroup)rootView.getParent()).removeView(rootView);
        }
        super.onDestroyView();
    }*/

    /*@Override
    public void onResume() {
        super.onResume();

       // if(mPrefs.getString(Constants.OnDetailsPage,"").equals("yes"))
       // hitReadFieldsforUser();
    }*/


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        clearApplicantDetails();
        super.onDetach();
    }
}
