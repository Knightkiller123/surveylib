package easygov.saral.harlabh.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.regex.Pattern;

import easygov.saral.harlabh.activity.SplashActivity;
import easygov.saral.harlabh.utils.Prefs;
import easygov.saral.harlabh.utils.RestClient;
import easygov.saral.harlabh.utils.VerhoeffAlgorithm;
import easygov.saral.harlabh.R;
import easygov.saral.harlabh.activity.FirstSurveyActivity;
import easygov.saral.harlabh.activity.StaticFormActivity;
import easygov.saral.harlabh.models.responsemodels.generalmodel.GeneralModel;
import easygov.saral.harlabh.utils.Constants;
import easygov.saral.harlabh.utils.GeneralFunctions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



/**
 * A simple {@link Fragment} subclass.
 */
public class StaticSurveyFragment extends Fragment implements View.OnClickListener {


    private RelativeLayout rlHavingAadhaar,rlApplyingFor,rlEnterAadhar,rlBackground,rlAadhaarMethod,rlOtpStatic,rlStaticMain;
    private TextView tvNo,tvYes,tvNextHavingAadhar,tvMyself,tvSomeoneElse,tvNextApplyingFor,tvNextAadharStatic,tvNextAdharMethod,tvNextOtpStatic,tvFamilyMember;

    private EditText etAadhaarMandatory;
    private String applyingFor="";
    private String havingAadhaar="";
    private Prefs mPrefs;
    private boolean isVisible;
    private LinearLayout llFamily,llFamilyNo;
    private Spinner spSpinFamily;
    private FamilyDetailFragments frag;
    private SomeoneElseDetailFragment frag1;
    private  String decider="",applied="";
    Bundle bundle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_static_survey, container, false);
        init(view);
        clickHandlers();

        return view;
    }



    private void init(View view) {
        mPrefs=Prefs.with(getActivity());
        rlStaticMain= view.findViewById(R.id.rlStaticMain);

        rlBackground= view.findViewById(R.id.rlBackground);

        rlApplyingFor= view.findViewById(R.id.rlApplyingFor);
        tvMyself= view.findViewById(R.id.tvMyself);
        tvSomeoneElse= view.findViewById(R.id.tvSomeoneElse);
        tvNextApplyingFor= view.findViewById(R.id.tvNextApplyingFor);

        tvFamilyMember= view.findViewById(R.id.tvFamilyMember);
        rlHavingAadhaar= view.findViewById(R.id.rlHavingAadhaar);
        tvNextHavingAadhar= view.findViewById(R.id.tvNextHavingAadhar);
        tvNo= view.findViewById(R.id.tvNo);
        tvYes= view.findViewById(R.id.tvYes);
        llFamily= view.findViewById(R.id.llFamily);
        spSpinFamily= view.findViewById(R.id.spSpinFamily);
        rlEnterAadhar= view.findViewById(R.id.rlEnterAadhar);
        etAadhaarMandatory= view.findViewById(R.id.etAadhaarMandatory);
        tvNextAadharStatic= view.findViewById(R.id.tvNextAadharStatic);

        rlAadhaarMethod= view.findViewById(R.id.rlAadhaarMethod);
        tvNextAdharMethod= view.findViewById(R.id.tvNextAdharMethod);

        rlOtpStatic= view.findViewById(R.id.rlOtpStatic);
        tvNextOtpStatic= view.findViewById(R.id.tvNextOtpStatic);
        llFamilyNo= view.findViewById(R.id.llFamilyNo);
        bundle =new Bundle();
        bundle=getArguments();

        // rlHavingAadhaar.setVisibility(View.GONE);



        try {
            /*if(bundle.getBoolean("triggerYes",false))
            {
                havingAadhaar="yes";
                rlHavingAadhaar.setVisibility(View.GONE);
                rlEnterAadhar.setVisibility(View.VISIBLE);
            }

            else  */{
                if(mPrefs.getString(Constants.GenerateSurveyId,"").equalsIgnoreCase("yes")||
                        mPrefs.getString(Constants.GenerateSurveyId,"").equalsIgnoreCase("")) {
                    getSurveyId("5");


                }

                else flowCasesHandled();
            }
        }

        catch (Exception e)
        {}








    }

    private void clickHandlers() {
        tvMyself.setOnClickListener(this);
        tvSomeoneElse.setOnClickListener(this);
        tvFamilyMember.setOnClickListener(this);
        tvNextApplyingFor.setOnClickListener(this);
        tvYes.setOnClickListener(this);
        tvNo.setOnClickListener(this);
        tvNextHavingAadhar.setOnClickListener(this);
        tvNextAadharStatic.setOnClickListener(this);
        tvNextAdharMethod.setOnClickListener(this);
        tvNextOtpStatic.setOnClickListener(this);
        rlStaticMain.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tvMyself) {
            applyingFor = "myself";
            tvMyself.setBackgroundResource(R.drawable.green_curved);
            tvSomeoneElse.setBackgroundResource(R.drawable.white_curved);
            tvFamilyMember.setBackgroundResource(R.drawable.white_curved);

            tvMyself.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
            tvSomeoneElse.setTextColor(ContextCompat.getColor(getActivity(), R.color.optionColor));
            tvFamilyMember.setTextColor(ContextCompat.getColor(getActivity(), R.color.optionColor));
            tvNextApplyingFor.setBackgroundResource(R.color.Sign);
            tvNextApplyingFor.setClickable(true);
            //todo: remove familymember and someonelese and use applying for
            mPrefs.save(Constants.Applyingfor, "myself");
            mPrefs.save(Constants.ProfileId, "");
            mPrefs.save(Constants.RestartSurvey, "no");
            mPrefs.getString(Constants.FamilyMyselfCase, "no");

        } else if (i == R.id.tvSomeoneElse) {
            tvMyself.setBackgroundResource(R.drawable.white_curved);
            tvSomeoneElse.setBackgroundResource(R.drawable.green_curved);
            tvFamilyMember.setBackgroundResource(R.drawable.white_curved);

            tvMyself.setTextColor(ContextCompat.getColor(getActivity(), R.color.optionColor));
            tvSomeoneElse.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
            tvFamilyMember.setTextColor(ContextCompat.getColor(getActivity(), R.color.optionColor));

            applyingFor = "someoneelse";
            tvNextApplyingFor.setBackgroundResource(R.color.Sign);
            tvNextApplyingFor.setClickable(true);
            mPrefs.save(Constants.Applyingfor, "someoneelse");
            mPrefs.save(Constants.RestartSurvey, "no");
            mPrefs.getString(Constants.FamilyMyselfCase, "no");


        } else if (i == R.id.tvFamilyMember) {
            tvMyself.setBackgroundResource(R.drawable.white_curved);
            tvSomeoneElse.setBackgroundResource(R.drawable.white_curved);
            tvFamilyMember.setBackgroundResource(R.drawable.green_curved);

            tvMyself.setTextColor(ContextCompat.getColor(getActivity(), R.color.optionColor));
            tvSomeoneElse.setTextColor(ContextCompat.getColor(getActivity(), R.color.optionColor));
            tvFamilyMember.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
            applyingFor = "family";

            tvNextApplyingFor.setBackgroundResource(R.color.Sign);
            tvNextApplyingFor.setClickable(true);
            mPrefs.save(Constants.Applyingfor, "family");
            mPrefs.save(Constants.RestartSurvey, "no");
            mPrefs.getString(Constants.FamilyMyselfCase, "no");


        } else if (i == R.id.tvNextApplyingFor) {
            if (applyingFor.equals("myself"))
                myselfCases();

            else if (applyingFor.equals("family"))
                familycase();
            else if (applyingFor.equals("someoneelse"))
                someoneElseCases();


            //else GeneralFunctions.makeSnackbar(rlStaticMain,getString(R.string.plselect));

        } else if (i == R.id.tvYes) {
            tvNextHavingAadhar.setBackgroundResource(R.color.Sign);
            tvNextHavingAadhar.setClickable(true);
            //mPrefs.save(Constants.NoAadhaar,"false");

            tvYes.setBackgroundResource(R.drawable.green_curved);
            tvNo.setBackgroundResource(R.drawable.white_curved);
            tvYes.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
            tvNo.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
            havingAadhaar = "yes";
               /* if(applyingFor.equals("family")) {
                    llFamily.setVisibility(View.VISIBLE);
                    llFamilyNo.setVisibility(View.GONE);
                }*/

        } else if (i == R.id.tvNo) {
            mPrefs.save(Constants.IS_AADHAR_AUTHENTICATED, false);

            tvNextHavingAadhar.setBackgroundResource(R.color.Sign);
            tvNextHavingAadhar.setClickable(true);

            tvNo.setBackgroundResource(R.drawable.green_curved);
            tvYes.setBackgroundResource(R.drawable.white_curved);
            tvYes.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
            tvNo.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
            havingAadhaar = "no";
                /*if(applyingFor.equals("family")) {
                    llFamily.setVisibility(View.VISIBLE);
                    llFamilyNo.setVisibility(View.VISIBLE);
                }*/

        } else if (i == R.id.tvNextHavingAadhar) {
            if (havingAadhaar.equals("yes")) {
                //todo : remove after hartron demo
                rlHavingAadhaar.setVisibility(View.GONE);
                rlEnterAadhar.setVisibility(View.VISIBLE);
                   /* Intent intent=new Intent(getActivity(), StaticFormActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition( R.anim.animateup_activity, R.anim.animatedown_activity );*/

                //mPrefs.save(Constants.FromAadhar,"YES");
                //((FirstSurveyActivity)getActivity()).hitSurveyApiForMyself();

            } else if (havingAadhaar.equals("no")) {
                Intent intent = new Intent(getActivity(), StaticFormActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.animateup_activity, R.anim.animatedown_activity);

            }

        } else if (i == R.id.tvNextAadharStatic) {
            if (validateAadharNumber(etAadhaarMandatory.getText().toString())) {
                  /*rlEnterAadhar.setVisibility(View.GONE);
                    rlBackground.setVisibility(View.VISIBLE);
                    rlAadhaarMethod.setVisibility(View.VISIBLE);*/
                mPrefs.save(Constants.AadhaarNo, etAadhaarMandatory.getText().toString());
                //rlHavingAadhaar.setVisibility(View.GONE);
                // rlEnterAadhar.setVisibility(View.VISIBLE);
                // mPrefs.save(Constants.AadhaarNo,etAadharNo.getText().toString());
                // hitInnitiateKycForOthers(v);
                //setWebview(3);
                ((FirstSurveyActivity) getContext()).initiatePopupWindow(v, etAadhaarMandatory.getText().toString());
            } else
                GeneralFunctions.makeSnackbar(rlStaticMain, getResources().getString(R.string.plvalidaadhaar));


        } else if (i == R.id.tvNextAdharMethod) {// rlEnterAadhar.setVisibility(View.GONE);
                /*rlAadhaarMethod.setVisibility(View.GONE);
                rlOtpStatic.setVisibility(View.VISIBLE);*/

        } else if (i == R.id.tvNextOtpStatic) {
            ((FirstSurveyActivity) getActivity()).hitSurveyApiForMyself();

        } else if (i == R.id.rlStaticMain) {
            GeneralFunctions.hideSoftKeyboard(getActivity());

        }


    }




    public  boolean validateAadharNumber(String aadharNumber) {
        Pattern aadharPattern = Pattern.compile("\\d{12}");
        boolean isValidAadhar = aadharPattern.matcher(aadharNumber).matches();
        if (isValidAadhar) {
            isValidAadhar = VerhoeffAlgorithm.validateVerhoeff(aadharNumber);
        }
        return isValidAadhar;
    }







    public void backHandling()
    {

        GeneralFunctions.hideSoftKeyboard(getActivity());
        try {
            if(rlApplyingFor.getVisibility()==View.VISIBLE)
            {
                GeneralFunctions.backPopup(rlApplyingFor.getRootView(),getActivity(),0);
                // getActivity().finish();
                // MyApplication.resetData();
            }
            else if(rlHavingAadhaar.getVisibility()==View.VISIBLE)
            {
                GeneralFunctions.backPopup(rlApplyingFor.getRootView(),getActivity(),0);
                /*rlHavingAadhaar.setVisibility(View.GONE);
                rlApplyingFor.setVisibility(View.VISIBLE);*/
            }
            else if(rlEnterAadhar.getVisibility()==View.VISIBLE)
            {
                if(!bundle.getBoolean("triggerYes",false))
                {

                    rlEnterAadhar.setVisibility(View.GONE);
                    rlHavingAadhaar.setVisibility(View.VISIBLE);
                }
                else
                    GeneralFunctions.backPopup(rlApplyingFor.getRootView(),getActivity(),0);
            }
            else {
                GeneralFunctions.backPopup(rlApplyingFor.getRootView(),getActivity(),0);
                // getActivity().finish();
            }
        }
        catch (Exception e)
        {
            //  rlApplyingFor.setVisibility(View.VISIBLE);
        }


        /*  else {
         *//*list.clear();
                        list=new ArrayList<>();*//*
                        getActivity().finish();
                    }*/


    }

    private void getSurveyId(final String myself)
    {
        mPrefs.remove(Constants.UserServiceID);
        GeneralFunctions.showDialog(getActivity());
        Call<GeneralModel> call= RestClient.get().getSurveyId(myself, mPrefs.getString(Constants.BeneficiaryID,""));
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
                        mPrefs.save(Constants.GenerateSurveyId,"no");
                        mPrefs.save(Constants.SurveyId,response.body().data.survey_id);

                        flowCasesHandled();
                        //hitKycData();

                    }

                    else {
                        GeneralFunctions.makeSnackbar(rlStaticMain,response.body().message);

                    }

                }
                else {
                    GeneralFunctions.makeSnackbar(rlStaticMain,getResources().getString(R.string.serverIssue));
                }
            }

            @Override
            public void onFailure(Call<GeneralModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(rlStaticMain,getResources().getString(R.string.netIssue));
            }
        });
    }

    private void flowCasesHandled() {
        if(bundle!=null) {
            decider = bundle.getString("applyin");
            applied=bundle.getString("aplFor");

            if(mPrefs.getString(Constants.FromServices,"").equals("yes")) {
                rlApplyingFor.setVisibility(View.VISIBLE);
                mPrefs.save(Constants.FromServices,"no");

            }
            else if(applied!=null&&!applied.equals("none"))
            {


                if(applied.equals("myself"))
                    myselfCases();

                else if(applied.equals("someoneelse"))
                    someoneElseCases();
                else
                    familycase();
            }

            else
                myselfCases();

        }
    }

    private void familycase() {


        frag=new FamilyDetailFragments();
        getFragmentManager().beginTransaction().add(R.id.rlFirstContainer,frag).addToBackStack(null).commit();
        // frag.show(getFragmentManager(),"");
    }


    private void myselfCases()
    {
       /* if (mPrefs.getString(Constants.FromAadhar, "").equals("YES")) {
            //((FirstSurveyActivity) getActivity()).hitSurveyApiForMyself();

            Intent intent=new Intent(getActivity(), StaticFormActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition( R.anim.animateup_activity, R.anim.animatedown_activity );

        } else {
            if(mPrefs.getString(Constants.AadhaarBackendEnable,"").equals("1"))
            {
                if(!bundle.getBoolean("triggerYes",false))
                {
                rlApplyingFor.setVisibility(View.GONE);
                rlHavingAadhaar.setVisibility(View.VISIBLE);
            }}
            else {
                Intent intent=new Intent(getActivity(), StaticFormActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition( R.anim.animateup_activity, R.anim.animatedown_activity );
            }
}
       */
        mPrefs.save(Constants.FormFilled,"YES");

       ((FirstSurveyActivity)getContext()).checkData();
    }

    private void someoneElseCases()
    {
        frag1=new SomeoneElseDetailFragment();
        getFragmentManager().beginTransaction().add(R.id.rlFirstContainer,frag1).addToBackStack(null).commit();
    }

    public void setSomeoneElse() {
        mPrefs.save(Constants.FormFilled,"YES");
        ((FirstSurveyActivity)getContext()).checkData();
    }


    public void setFamilyFurther(int i) {
        mPrefs.save(Constants.FormFilled,"YES");
        ((FirstSurveyActivity)getContext()).checkData();
    }



}
