package easygov.saral.harlabh.fragments.survey;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import easygov.saral.harlabh.models.surveypaging.StartSurveyPaging;
import easygov.saral.harlabh.utils.Prefs;
import easygov.saral.harlabh.R;
import easygov.saral.harlabh.adapters.SecondSurveyAdapter;
import easygov.saral.harlabh.utils.Constants;
import easygov.saral.harlabh.utils.GeneralFunctions;

/**
 * Created by apoorv on 31/08/17.
 */

public class SecondSurveyFragment extends Fragment {

    private RecyclerView rvEntitlement;
    private Prefs mPrefs;
    private static int position;
    private TextView tvSecondNext;
    SecondSurveyAdapter surveyAdapter;
    private CardView cvSecond;
    private View popView;
    private TextView tvBenName;
    PopupWindow pw;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_second_survey,container,false);
        popView=view;
        init(view);
        return view;
    }

    private void init(View view) {
        rvEntitlement= view.findViewById(R.id.rvEntitlement);
        rvEntitlement.setLayoutManager(new LinearLayoutManager(getActivity()));
        tvSecondNext= view.findViewById(R.id.tvSecondNext);
        mPrefs=Prefs.with(getActivity());
        cvSecond= view.findViewById(R.id.cvSecond);
        tvBenName= view.findViewById(R.id.tvBenName);
        StartSurveyPaging object =new StartSurveyPaging();
        List<StartSurveyPaging> list =new ArrayList<>();
        list=new Gson().fromJson(mPrefs.getString(Constants.savedResponses,""),new TypeToken<List<StartSurveyPaging>>(){}.getType());


        if(mPrefs.getString(Constants.Applyingfor,"").equalsIgnoreCase("myself"))
        {
            tvBenName.setText(getResources().getString(R.string.beneficiary)+" "+": "+getResources().getString(R.string.myself));
        }
        else
            tvBenName.setText(getResources().getString(R.string.beneficiary)+" "+": "+mPrefs.getString(Constants.FirstName,""));

        rvEntitlement.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                GeneralFunctions.hideSoftKeyboard(getActivity());
                return false;
            }
        });
        object = list.get(0);
        if(object!=null) {
            surveyAdapter = new SecondSurveyAdapter(getActivity(), object,popView,this);
            rvEntitlement.setItemViewCacheSize(object.data.field_data.size());

            rvEntitlement.setAdapter(surveyAdapter);
            if(object.data.field_data.size()==0)
            {
                surveyAdapter.setValues();
            }
        }


        tvSecondNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                surveyAdapter.setValues();
            }
        });

        cvSecond.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                GeneralFunctions.hideSoftKeyboard(getActivity());
                return false;
            }
        });

    }

public void hideKeyBoard()
{
    GeneralFunctions.hideSoftKeyboard(getActivity());
}

    public static SecondSurveyFragment newInstance(int page) {
        SecondSurveyFragment fragmentFirst = new SecondSurveyFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        fragmentFirst.setArguments(args);
        //position =page;

        return fragmentFirst;
    }

    public void noSchemeFound() {
        // final PopupWindow pw;
        //  getActivity().getSupportFragmentManager().beginTransaction().add(R.id.rlAttachment,new DocPopUpFragment()).addToBackStack(null).commit();

        try {
            //We need to get the instance of the LayoutInflater, use the context of this activity
            LayoutInflater inflater = (LayoutInflater) getActivity()
                    .getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
            //Inflate the view from a predefined XML layout
            View layout = inflater.inflate(R.layout.eligible_dialog,
                    (ViewGroup)popView.findViewById(R.id.rlnotEligible));
            // create a 300px width and 470px height PopupWindow


            pw = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
            // display the popup in the center
            pw.showAtLocation(layout, Gravity.CENTER, 0, 0);






            TextView tvNotEligible= layout.findViewById(R.id.tvNotEligible);

            tvNotEligible.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });



           /* pw.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    pw.dismiss();

                }
            });*/



        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
