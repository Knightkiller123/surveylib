package easygov.saral.harlabh.fragments.survey;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import easygov.saral.harlabh.models.surveypaging.StartSurveyPaging;
import easygov.saral.harlabh.utils.Prefs;
import easygov.saral.harlabh.R;
import easygov.saral.harlabh.adapters.SurveyAdapter;
import easygov.saral.harlabh.utils.Constants;
import easygov.saral.harlabh.utils.GeneralFunctions;

import static easygov.saral.harlabh.fragments.storyboardfrags.YourStoryDetailsFragment.count;

/**
 * Created by apoorv on 31/08/17.
 */

public class SurveyFragment extends Fragment {

    private RecyclerView rvEntitlement;
    private Prefs mPrefs;
    private static int position;
    private TextView tvNextPager;
    private SurveyAdapter surveyAdapter;
    private StartSurveyPaging object =new StartSurveyPaging();
    private List<StartSurveyPaging> list =new ArrayList<>();
    private String s="";
    private View view;
    private TextView tvSurveyNext;
    private CardView cvFirstSurvey;
    private TextView tvBenName;
    private RelativeLayout rlSurveyKeyClose;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_survey,container,false);
        init(view);
        return view;
    }

    private void init(View view) {
        rvEntitlement= view.findViewById(R.id.rvEntitlement);
        rvEntitlement.setLayoutManager(new LinearLayoutManager(getActivity()));
        tvSurveyNext= view.findViewById(R.id.tvSurveyNext);
        // tvNextPager= (TextView) view.findViewById(R.id.tvNextPager);
        mPrefs=Prefs.with(getActivity());
        rlSurveyKeyClose= view.findViewById(R.id.rlSurveyKeyClose);
        tvBenName= view.findViewById(R.id.tvBenName);
        cvFirstSurvey= view.findViewById(R.id.cvFirstSurvey);
        setAdap();

        if(mPrefs.getString(Constants.Applyingfor,"").equalsIgnoreCase("myself"))
        {
            tvBenName.setText(getResources().getString(R.string.beneficiary)+" "+": "+getResources().getString(R.string.myself));
        }
        else
        tvBenName.setText(getResources().getString(R.string.beneficiary)+" "+": "+mPrefs.getString(Constants.FirstName,""));

        rlSurveyKeyClose.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                GeneralFunctions.hideSoftKeyboard(getActivity());
                return false;
            }
        });
        rvEntitlement.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                GeneralFunctions.hideSoftKeyboard(getActivity());
                return false;
            }
        });
        cvFirstSurvey.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                GeneralFunctions.hideSoftKeyboard(getActivity());
                return false;
            }
        });
        tvSurveyNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                surveyAdapter.setValues();
            }
        });

    }

    /*public void setNextVisible()
    {
        tvSurveyNext.setVisibility(View.VISIBLE);
    }

    public void setnextInvisible()
    {
        tvSurveyNext.setVisibility(View.GONE);
    }*/

    public static SurveyFragment newInstance(int page) {
        SurveyFragment fragmentFirst = new SurveyFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public void onResume() {
        super.onResume();
       //setAdap();

    }

   /* @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if(menuVisible)
        {
            setAdap();
        }
    }*/

    private void setAdap()
    {
        try {
            s = Constants.savedResponses + "" + count;
            list = new Gson().fromJson(mPrefs.getString(s, ""), new TypeToken<List<StartSurveyPaging>>() {
            }.getType());


       /* surveyAdapter = new SurveyAdapter(getActivity(), object);
        surveyAdapter.notifyDataSetChanged();
        rvEntitlement.setAdapter(surveyAdapter);*/
            object=new StartSurveyPaging();
            object = list.get(0);

            surveyAdapter = new   SurveyAdapter(getActivity(), object,SurveyFragment.this);
            surveyAdapter.notifyDataSetChanged();
            rvEntitlement.setItemViewCacheSize(object.data.field_data.size());
            rvEntitlement.setAdapter(surveyAdapter);
        }
        catch (Exception e)
        {
        }

    }




}
