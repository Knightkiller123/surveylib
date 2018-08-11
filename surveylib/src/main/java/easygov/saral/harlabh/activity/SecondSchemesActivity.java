package easygov.saral.harlabh.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import easygov.saral.harlabh.adapters.FilterSchemesAdater;
import easygov.saral.harlabh.adapters.SelectServiceAdapter;

import easygov.saral.harlabh.models.surveypaging.StartSurveyPaging;
import easygov.saral.harlabh.models.surveypaging.SurveyPagingData;
import easygov.saral.harlabh.utils.GeneralFunctions;
import easygov.saral.harlabh.utils.Prefs;
import easygov.saral.harlabh.R;
import easygov.saral.harlabh.utils.Constants;
import easygov.saral.harlabh.utils.RecyclerBottomListener;
import easygov.saral.harlabh.utils.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static easygov.saral.harlabh.adapters.SecondSurveyAdapter.lists;

public class SecondSchemesActivity extends AppCompatActivity {

    private RecyclerView rvStory;
    //private RecyclerView rvSchemes;
    private List<SurveyPagingData> list=new ArrayList<>();
    private List<SurveyPagingData> filterList=new ArrayList<>();
    private PopupWindow pw;
    private Prefs mPrefs;
    private ProgressBar pbSet1;
    private StartSurveyPaging entList;

    private ImageView ivSchemesBack;
    private RelativeLayout rlNoSchemes;
    private LinearLayout llsurveyResult;
    private TextView tvSchemeSecond,tvSecondHeader,tvTryAgain,tvFilterSchemes;
    private SelectServiceAdapter selectServiceAdapter;
    private int CURRENT_PAGE =0;
    private List<String> filterWord=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_result);
        init();
    }

    BroadcastReceiver broadcastReceiver =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(broadcastReceiver,new IntentFilter("closeACT"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(lists!=null)
        {
            lists.clear();
            lists=new ArrayList<>();
            ThirdEligibilityActivity.counts=0;
        }
    }

    public void setFilteredData(String s)
    {
        try {
            pw.dismiss();
            filterList=new ArrayList<>();
            for(int i=0;i<list.size();i++)
            {
                if(list.get(i).service__department__name.equals(s))
                    filterList.add(list.get(i));
            }

            selectServiceAdapter=new SelectServiceAdapter(this, filterList, entList);
            rvStory.setAdapter(selectServiceAdapter);

        }
        catch (Exception e)
        {}
    }

    public void setAll()
    {
        try {
            pw.dismiss();
            selectServiceAdapter=new SelectServiceAdapter(this, list, entList);
            rvStory.setAdapter(selectServiceAdapter);
        }
        catch (Exception e)
        {}

    }

    private void init() {
        rvStory= findViewById(R.id.rvStory);
        rvStory.setLayoutManager(new LinearLayoutManager(this));
        pbSet1= findViewById(R.id.pbSet1);
        mPrefs=Prefs.with(this);
        tvFilterSchemes=findViewById(R.id.tvFilterSchemes);
        llsurveyResult= findViewById(R.id.llsurveyResult);
        ivSchemesBack= findViewById(R.id.ivSchemesBack);
        tvSchemeSecond= findViewById(R.id.tvSchemeSecond);
        tvSecondHeader= findViewById(R.id.tvSecondHeader);
        tvTryAgain= findViewById(R.id.tvTryAgain);
        //ivFilterSchemes= findViewById(R.id.ivFilterSchemes);
        entList=new Gson().fromJson(mPrefs.getString("qualifiedList",""),new TypeToken<StartSurveyPaging>() {}.getType());
        list=entList.data.qualified_schemes.objects;

        rlNoSchemes= findViewById(R.id.rlNoSchemes);
        if(list.size()>0&&list!=null) {
            rvStory.setVisibility(View.VISIBLE);
            rlNoSchemes.setVisibility(View.GONE);
            selectServiceAdapter=new SelectServiceAdapter(this, list, entList);
            rvStory.setAdapter(selectServiceAdapter);



            for(int i=0;i<list.size();i++)
            {
                filterWord.add(list.get(i).service__department__name);
                /*for (int j=0;j<filterWord.size();j++) {
                    if (!filterWord.get(j).contains(list.get(i).service__service_category__name)) {
                        filterWord.add(list.get(i).service__service_category__name);
                        break;
                    }
                }*/
            }
            try {
                Set set = new HashSet(filterWord);

                filterWord=new ArrayList(set);
                filterWord.add(0,getResources().getString(R.string.allcategories));
                if(filterWord.size()==1)
                {
                    tvFilterSchemes.setVisibility(View.GONE);
                }
            }
            catch (Exception e){}

            selectServiceAdapter.setOnBottomReachedListener(new RecyclerBottomListener() {
                @Override
                public void onBottomReached(int position) {
                    try {
                        if(position==list.size()-1) {


//Todo: rework check on basis of total count
                            if(list.size()>1000)
                                hitNext(++CURRENT_PAGE);
                        }
                    }
                    catch (Exception e)
                    {
                        GeneralFunctions.makeSnackbar(llsurveyResult,""+e);
                    }

                }
            });
        }

        else {
            rvStory.setVisibility(View.GONE);
            rlNoSchemes.setVisibility(View.VISIBLE);
        }
        tvSchemeSecond.setText(mPrefs.getString(Constants.SecontActivityText,""));
        tvSecondHeader.setText(mPrefs.getString(Constants.SecontActivityText,""));
        ivSchemesBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneralFunctions.backPopup(ivSchemesBack.getRootView(),SecondSchemesActivity.this,0);
                //finish();
            }
        });

        tvTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        tvFilterSchemes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                innitiateFilterPopUp(v);
            }
        });

    }




    public void hitNext(int i)
    {
        GeneralFunctions.showDialog(this);
        Map<String ,String> map=new HashMap<>();
        map.put("size","10");
        map.put("pagenumber",String.valueOf(i));
        map.put("survey_id",mPrefs.getString(Constants.SurveyId,""));
        map.put("current_scheme_list",new Gson().toJson(entList.data.current_scheme_list));
        map.put("existing_field_list",new Gson().toJson(entList.data.existing_field_list));
        map.put("rule_type",entList.data.rule_type);
        map.put("geography_id",mPrefs.getString(Constants.GeographyId,""));
        map.put(Constants.PARAM_BENEFICIARY_TYPE_ID,mPrefs.getString(Constants.BeneficiaryID,""));
        Call<StartSurveyPaging> call= RestClient.get().setEnt(map);
        call.enqueue(new Callback<StartSurveyPaging>() {
            @Override
            public void onResponse(Call<StartSurveyPaging> call, Response<StartSurveyPaging> response) {
                GeneralFunctions.dismissDialog();

                if(response.isSuccessful()) {
                    if(response.body().code==401)
                    {
                        GeneralFunctions.tokenExpireAction(SecondSchemesActivity.this);
                    }
                    else  if(response.body().success==1) {
                        mPrefs.save(Constants.GenerateSurveyId,"yes");
                        StartSurveyPaging object = response.body();
                        entList = response.body();
                        list.addAll(response.body().data.qualified_schemes.objects);
                        selectServiceAdapter.notifyDataSetChanged();
                        mPrefs.save("qualifiedList", new Gson().toJson(response.body()));
                    }
                    else {
                        GeneralFunctions.makeSnackbar(llsurveyResult,response.body().message);
                    }
                }
                else GeneralFunctions.makeSnackbar(llsurveyResult,getResources().getString(R.string.serverIssue));



            }

            @Override
            public void onFailure(Call<StartSurveyPaging> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(llsurveyResult,getResources().getString(R.string.netIssue));
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        GeneralFunctions.backPopup(ivSchemesBack.getRootView(),SecondSchemesActivity.this,0);
    }


    public void innitiateFilterPopUp( View views) {
        // final PopupWindow pw;
        //  getActivity().getSupportFragmentManager().beginTransaction().add(R.id.rlAttachment,new DocPopUpFragment()).addToBackStack(null).commit();

        try {
            //We need to get the instance of the LayoutInflater, use the context of this activity
            LayoutInflater inflater = (LayoutInflater) this
                    .getSystemService(LAYOUT_INFLATER_SERVICE);
            //Inflate the view from a predefined XML layout
            View layout = inflater.inflate(R.layout.filter_dialog,
                    (ViewGroup)views.findViewById(R.id.rlFileSelect));
            // create a 300px width and 470px height PopupWindow

            pw = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
            // display the popup in the center
            pw.showAtLocation(layout, Gravity.CENTER, 0, 0);

            RecyclerView rvFilterSchemes= layout.findViewById(R.id.rvFilterSchemes);
            rvFilterSchemes.setLayoutManager(new LinearLayoutManager(this));

            rvFilterSchemes.setAdapter(new FilterSchemesAdater(this,filterWord));

            pw.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    pw.dismiss();

                }
            });
            ImageView ivCross= layout.findViewById(R.id.ivCross);
            ivCross.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pw.dismiss();
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
