package easygov.saral.harlabh.fragments.managementfragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import easygov.saral.harlabh.activity.SplashActivity;
import easygov.saral.harlabh.adapters.FilterApplicationAdapter;
import easygov.saral.harlabh.R;
import easygov.saral.harlabh.models.responsemodels.filter.FilterApplicationModel;
import easygov.saral.harlabh.models.responsemodels.filter.FilterListData;
import easygov.saral.harlabh.models.responsemodels.filterstatus.FilterResponse;
import easygov.saral.harlabh.models.responsemodels.filterstatus.FilterStateList;
import easygov.saral.harlabh.utils.GeneralFunctions;
import easygov.saral.harlabh.utils.MyApplication;
import easygov.saral.harlabh.utils.Prefs;
import easygov.saral.harlabh.utils.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by apoorv on 26/09/17.
 */

public class FilterAppFragment extends Fragment implements View.OnClickListener {

    private RecyclerView rvFilterApp;
    private ImageView ivFilterBack,ivCrossFilter,ivFilterCross;
    private RelativeLayout rlSortApplications,rlFilterApplication,rlFilterApp,rlNoListFound,rlFilterLayout,rlOverlay,rlEtSelect,rlSortFilters;
    private LinearLayout llSort;
    private EditText etSelect;
    private TextView tvOnlySchemes,tvOnlyServices,tvToday,tvFromDate,tvToDate,tvFilterApply;
    private List<FilterListData> list;
    private List<FilterListData> onlySchemes;
    private List<FilterListData> onlyServices;
    private List<FilterListData> todays;
    private Prefs mPrefs;
    private Date currentTime;
    private SimpleDateFormat form;
    private String currentDate;
    private Calendar myCalendar;
    private Spinner spStatusDisplay;
    private List<FilterStateList> statusData;
    private String startDate,endDate;
    private String value;
    com.tsongkha.spinnerdatepicker.DatePickerDialog.OnDateSetListener dates;
    private List<FilterListData> filteredList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_filterapp,container,false);
        init(view);
        clickHandlers();
        return view;
    }

    private void clickHandlers() {
        rlSortApplications.setOnClickListener(this);
        tvOnlySchemes.setOnClickListener(this);
        tvOnlyServices.setOnClickListener(this);
        tvToday.setOnClickListener(this);
        rlFilterApplication.setOnClickListener(this);
        tvToDate.setOnClickListener(this);
        tvFromDate.setOnClickListener(this);

        tvFilterApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(startDate.equals(getResources().getString(R.string.fromdate)))
                {
                    GeneralFunctions.makeSnackbar(rlFilterApp,getResources().getString(R.string.plselect)+" "+getResources().getString(R.string.fromdate));
                }
                else if(endDate.equals(getResources().getString(R.string.todate)))
                {
                    GeneralFunctions.makeSnackbar(rlFilterApp,getResources().getString(R.string.plselect)+" "+getResources().getString(R.string.todate));

                }
                else
                hitFilterListApiwithDate();
            }
        });
    }


    private void init(View view) {
        rvFilterApp= view.findViewById(R.id.rvFilterApp);
        rvFilterApp.setLayoutManager(new LinearLayoutManager(getActivity()));
        ivFilterBack= view.findViewById(R.id.ivFilterBack);
        rlFilterApp= view.findViewById(R.id.rlFilterApp);
        rlNoListFound= view.findViewById(R.id.rlNoListFound);
        rlSortApplications= view.findViewById(R.id.rlSortApplications);
        rlFilterApplication= view.findViewById(R.id.rlFilterApplication);
        rlFilterLayout= view.findViewById(R.id.rlFilterLayout);
        ivFilterCross= view.findViewById(R.id.ivFilterCross);
        rlSortFilters= view.findViewById(R.id.rlSortFilters);
        rlEtSelect= view.findViewById(R.id.rlEtSelect);
        myCalendar = Calendar.getInstance();
        etSelect= view.findViewById(R.id.etSelect);
        rlOverlay= view.findViewById(R.id.rlOverlay);
        onlySchemes=new ArrayList<>();
        onlyServices=new ArrayList<>();
        todays=new ArrayList<>();
        startDate=getResources().getString(R.string.fromdate);
        endDate=getResources().getString(R.string.todate);
        spStatusDisplay= view.findViewById(R.id.spStatusDisplay);
        currentTime = Calendar.getInstance().getTime();
        form = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        currentDate="";
        try
        {
            //date = form.parse(filterList.get(position).created_on);
            SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");

            currentDate = formatter.format(currentTime);


        }
        catch (Exception e)
        {

        }
        tvFilterApply= view.findViewById(R.id.tvFilterApply);
        tvFromDate= view.findViewById(R.id.tvFromDate);
        tvToDate= view.findViewById(R.id.tvToDate);
        tvToday= view.findViewById(R.id.tvToday);
        tvOnlyServices= view.findViewById(R.id.tvOnlyServices);
        tvOnlySchemes= view.findViewById(R.id.tvOnlySchemes);
        mPrefs=Prefs.with(getActivity());
        llSort= view.findViewById(R.id.llSort);

        ivCrossFilter= view.findViewById(R.id.ivCrossFilter);

        ivCrossFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlFilterLayout.setVisibility(View.GONE);
                tvFromDate.setText(getResources().getString(R.string.fromdate));
                tvToDate.setText(getResources().getString(R.string.todate));
            }
        });
        ivFilterBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
        hitFilterListApi();

        rvFilterApp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(llSort.getVisibility()==View.VISIBLE)
                {
                    llSort.setVisibility(View.GONE);
                    rlOverlay.setVisibility(View.GONE);
                    // slideToBottom();
                }
                return false;
            }
        });



        try {
            rvFilterApp.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    GeneralFunctions.hideSoftKeyboard(getActivity());
                }
            });
        }
        catch (Exception e)
        {}

        rvFilterApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llSort.setVisibility(View.GONE);
                rlOverlay.setVisibility(View.GONE);
                GeneralFunctions.hideSoftKeyboard(getActivity());
            }
        });

        ivFilterCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                etSelect.setText("");
                GeneralFunctions.hideSoftKeyboard(getActivity());

            }
        });
        setSearch();
    }
    private List<FilterListData> filterServiceCategories=new ArrayList<>();

    private void setSearch() {
        etSelect.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()>0)
                {
                    ivFilterCross.setVisibility(View.VISIBLE);
                }
                else {
                    filterServiceCategories.clear();
                    filterServiceCategories.addAll(filteredList);
                    rvFilterApp.setAdapter(new FilterApplicationAdapter(getActivity(), filterServiceCategories));
                    rlSortFilters.setVisibility(View.VISIBLE);
                    rlEtSelect.setVisibility(View.VISIBLE);
                    rlNoListFound.setVisibility(View.GONE);
                    ivFilterCross.setVisibility(View.GONE);

                }
                if(String.valueOf(charSequence).length()>1)
                    filter(String.valueOf(charSequence));}

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    public void filter(String str)
    {
        filterServiceCategories.clear();


        if (TextUtils.isEmpty(str)) {

            filterServiceCategories.addAll(filteredList);

        } else {

            for (FilterListData item : filteredList) {
                if (item.service_name.toLowerCase().contains(str.toLowerCase()) ) {
                    // Adding Matched items
                    filterServiceCategories.add(item);
                }
            }

        }
        if(filterServiceCategories.size()>0)
        {
            rvFilterApp.setAdapter(new FilterApplicationAdapter(getActivity(), filterServiceCategories));
            rlSortFilters.setVisibility(View.VISIBLE);
            rlEtSelect.setVisibility(View.VISIBLE);
            rlNoListFound.setVisibility(View.GONE);

            /*rlNoData.setVisibility(View.GONE);
            rvWatServices.setVisibility(View.VISIBLE);
            rvWatServices.setAdapter(new WantServicesAdapter(this, filterServiceCategories));*/
        }
        else {
            rvFilterApp.setAdapter(new FilterApplicationAdapter(getActivity(), filterServiceCategories));
            rlSortFilters.setVisibility(View.GONE);
            // rlEtSelect.setVisibility(View.GONE);
            rlNoListFound.setVisibility(View.VISIBLE);

            //rvWatServices.setAdapter(new WantServicesAdapter(this, filterServiceCategories))
           /* rvWatServices.setVisibility(View.GONE);
            rlNoData.setVisibility(View.VISIBLE);
            GeneralFunctions.hideSoftKeyboard(this);*/

        }



        //adapter.notifyDataSetChanged();
        //this.notifyDataSetChanged();
    }


    private void hitFilterListApi() {
        GeneralFunctions.showDialog(getActivity());
        Call<FilterApplicationModel> call= RestClient.get().getFilterServicesList("1000");
        call.enqueue(new Callback<FilterApplicationModel>() {
            @Override
            public void onResponse(Call<FilterApplicationModel> call, Response<FilterApplicationModel> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful())
                {

                    if(response.body().code==401)
                    {
                        GeneralFunctions.tokenExpireAction(getActivity());
                    }

                    else if(response.body().success==1) {
                        list=response.body().data.objects;
                        if(list!=null&&list.size()>0) {
                            filteredList=response.body().data.objects;
                            rlSortFilters.setVisibility(View.VISIBLE);
                            rlEtSelect.setVisibility(View.VISIBLE);
                            rvFilterApp.setAdapter(new FilterApplicationAdapter(getActivity(), response.body().data.objects));
                        }
                        else {
                            filteredList=new ArrayList<>();
                            // rlEtSelect.setVisibility(View.GONE);
                            rlSortFilters.setVisibility(View.GONE);
                            rlNoListFound.setVisibility(View.VISIBLE);
                            List <FilterListData> temp=new ArrayList<FilterListData>();
                            rvFilterApp.setAdapter(new FilterApplicationAdapter(getActivity(),temp ));
                            GeneralFunctions.makeSnackbar(rlFilterApp,getResources().getString(R.string.nodata));}
                    }
                    else {
                        // rlEtSelect.setVisibility(View.GONE);
                        rlSortFilters.setVisibility(View.GONE);
                        rlNoListFound.setVisibility(View.VISIBLE);
                        List <FilterListData> temp=new ArrayList<FilterListData>();
                        rvFilterApp.setAdapter(new FilterApplicationAdapter(getActivity(),temp ));
                        GeneralFunctions.makeSnackbar(rlFilterApp,response.body().message);
                    }

                }
                else GeneralFunctions.makeSnackbar(rlFilterApp,getResources().getString(R.string.serverIssue));
            }

            @Override
            public void onFailure(Call<FilterApplicationModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(rlFilterApp,getResources().getString(R.string.netIssue));
            }
        });
    }



    private void hitFilterListApiwithDate() {
        GeneralFunctions.showDialog(getActivity());
        Call<FilterApplicationModel> call= RestClient.get().getFilterServicesListDate("1000",startDate,endDate,value);
        call.enqueue(new Callback<FilterApplicationModel>() {
            @Override
            public void onResponse(Call<FilterApplicationModel> call, Response<FilterApplicationModel> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful())
                {
                    if(response.body().code==401)
                    {
                        GeneralFunctions.tokenExpireAction(getActivity());
                    }

                   else if(response.body().success==1) {

                       // list=response.body().data.objects;
                        if(response.body().data.objects!=null&&response.body().data.objects.size()>0) {
                            rlFilterLayout.setVisibility(View.GONE);
                            filteredList=response.body().data.objects;
                            rvFilterApp.setAdapter(new FilterApplicationAdapter(getActivity(), response.body().data.objects));
                            rlSortFilters.setVisibility(View.VISIBLE);
                            rlEtSelect.setVisibility(View.VISIBLE);
                        }

                        else {
                            //  rlEtSelect.setVisibility(View.GONE);
                            /*rlSortFilters.setVisibility(View.GONE);
                            rlNoListFound.setVisibility(View.VISIBLE);
                            List <FilterListData> temp=new ArrayList<FilterListData>();
                            rvFilterApp.setAdapter(new FilterApplicationAdapter(getActivity(),temp ));*/
                            GeneralFunctions.makeSnackbar(rlFilterApp,getResources().getString(R.string.nodata));}
                    }
                    else {
                        // rlEtSelect.setVisibility(View.GONE);
                        /*rlSortFilters.setVisibility(View.GONE);
                        rlNoListFound.setVisibility(View.VISIBLE);
                        List <FilterListData> temp=new ArrayList<FilterListData>();
                        rvFilterApp.setAdapter(new FilterApplicationAdapter(getActivity(),temp ));*/
                        GeneralFunctions.makeSnackbar(rlFilterApp,response.body().message);}
                }
                else GeneralFunctions.makeSnackbar(rlFilterApp,getResources().getString(R.string.serverIssue));
            }

            @Override
            public void onFailure(Call<FilterApplicationModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(rlFilterApp,getResources().getString(R.string.netIssue));
            }
        });
    }


    public void slideToAbove() {
        Animation slide = null;
        // llSort.setVisibility(View.INVISIBLE);
        /*slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, -5.0f);*/

        slide = new TranslateAnimation(0,0,400,-50);

        slide.setDuration(400);
        slide.setFillAfter(true);
        slide.setFillEnabled(true);
        llSort.startAnimation(slide);

        slide.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                llSort.setVisibility(View.VISIBLE);
                rlOverlay.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

               /* llSort.clearAnimation();

                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        llSort.getWidth(), llSort.getHeight());
                // lp.setMargins(0, 0, 0, 0);
                lp.addRule(RelativeLayout.ALIGN_BOTTOM);
                llSort.setLayoutParams(lp);*/

            }

        });

    }

    private void slideToBottom() {
        Animation slide = null;
        // llSort.setVisibility(View.INVISIBLE);
        /*slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, -5.0f);*/

        slide = new TranslateAnimation(0,0,-50,450);


        slide.setDuration(400);
        slide.setFillAfter(true);
        slide.setFillEnabled(true);
        llSort.startAnimation(slide);

        slide.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                llSort.setVisibility(View.GONE);
                rlOverlay.setVisibility(View.GONE);
               /* llSort.clearAnimation();

                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        llSort.getWidth(), llSort.getHeight());
                // lp.setMargins(0, 0, 0, 0);
                lp.addRule(RelativeLayout.ALIGN_BOTTOM);
                llSort.setLayoutParams(lp);*/

            }

        });
    }

    @Override
    public void onClick(View v) {
        int i1 = v.getId();
        if (i1 == R.id.rlSortApplications) {
            if (llSort.getVisibility() == View.GONE) {
                llSort.setVisibility(View.VISIBLE);
                rlOverlay.setVisibility(View.VISIBLE);
            } else {
                llSort.setVisibility(View.GONE);
                rlOverlay.setVisibility(View.VISIBLE);
            }

        } else if (i1 == R.id.tvOnlyServices) {
            if (onlyServices != null && onlyServices.size() > 0) {
                customFilter(onlyServices);
            } else {
                for (int i = 0; i < list.size(); i++) {
                    if (!list.get(i).is_scheme) {
                        onlyServices.add(list.get(i));
                    }
                }
                customFilter(onlyServices);
            }

        } else if (i1 == R.id.tvOnlySchemes) {
            if (onlySchemes != null && onlySchemes.size() > 0) {
                customFilter(onlySchemes);
            } else {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).is_scheme) {
                        onlySchemes.add(list.get(i));
                    }
                }
                customFilter(onlySchemes);
            }

        } else if (i1 == R.id.tvToday) {
            Date date;
            if (todays != null && todays.size() > 0) {
                customFilter(todays);
            } else {
                for (int i = 0; i < list.size(); i++) {
                    try {
                        date = form.parse(list.get(i).created_on);
                        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");

                        if (formatter.format(date).equals(currentDate)) {
                            todays.add(list.get(i));
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
                customFilter(todays);
            }

        } else if (i1 == R.id.rlFilterApplication) {
            llSort.setVisibility(View.GONE);
            rlOverlay.setVisibility(View.GONE);
            getStatusApi();


        } else if (i1 == R.id.tvFromDate) {
            setCustomDate(tvFromDate, 0);
            openDateDialogue();


        } else if (i1 == R.id.tvToDate) {
            setCustomDate(tvToDate, 1);
            openDateDialogue();


        }
    }

    private void customFilter(List<FilterListData> filterListDatas)
    {
        if(filterListDatas!=null&&filterListDatas.size()>0)
        {
            filterServiceCategories=filterListDatas;
            filteredList=filterListDatas;
            rvFilterApp.setAdapter(new FilterApplicationAdapter(getActivity(), filterListDatas));
            llSort.setVisibility(View.GONE);
            rlOverlay.setVisibility(View.GONE);

        }
        else {
            GeneralFunctions.makeSnackbar(rlFilterApp,getResources().getString(R.string.nodata));
        }
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

                    if(rlFilterLayout.getVisibility()==View.VISIBLE) {
                        rlFilterLayout.setVisibility(View.GONE);
                        tvFromDate.setText(getResources().getString(R.string.fromdate));
                        tvToDate.setText(getResources().getString(R.string.todate));
                    }

                    else getFragmentManager().popBackStack();
                    return true;
                }
                return false;
            }
        });
    }

    private void openDateDialogue()
    {
        new SpinnerDatePickerDialogBuilder()
                .context(getActivity())
                .callback(dates)
                .build()
                .show();
    }

    private void setCustomDate(final TextView tv, final int selector)
    {

        dates= new com.tsongkha.spinnerdatepicker.DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(com.tsongkha.spinnerdatepicker.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String s=dayOfMonth+"-"+(monthOfYear+1)+"-"+year;
                if(MyApplication.compareDate(Calendar.getInstance().getTime(),myCalendar.getTime())) {
                    tv.setText(s);
                    if(selector==0)
                        startDate=s;

                    else
                        endDate=s;
                }
                else GeneralFunctions.makeSnackbar(rlFilterApp,getActivity().getResources().getString(R.string.validDate));
            }
        };



    }

    private void getStatusApi()
    {
        GeneralFunctions.showDialog(getActivity());
        Call<FilterResponse> call=RestClient.get().getStatus();

        call.enqueue(new Callback<FilterResponse>() {
            @Override
            public void onResponse(Call<FilterResponse> call, Response<FilterResponse> response) {
                if(response.isSuccessful())
                {
                    GeneralFunctions.dismissDialog();
                    if(response.body().code==401)
                    {
                        GeneralFunctions.tokenExpireAction(getActivity());
                    }
                    else if(response.body().success==1)
                    {
                        if(response.body().data.status_data!=null&&response.body().data.status_data.size()>0)
                        {
                            statusData=response.body().data.status_data;
                            setFilterData();
                        }
                        else GeneralFunctions.makeSnackbar(rlFilterApp,getActivity().getResources().getString(R.string.nodata));
                    }
                    else GeneralFunctions.makeSnackbar(rlFilterApp,response.body().message);
                }
                else GeneralFunctions.makeSnackbar(rlFilterApp,getActivity().getResources().getString(R.string.serverIssue));
            }

            @Override
            public void onFailure(Call<FilterResponse> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(rlFilterApp,getActivity().getResources().getString(R.string.netIssue));
            }
        });
    }

    private void setFilterData()
    {
        List<String> stat=new ArrayList<>();
        final List<String> values =new ArrayList();

        for(int i=0;i<statusData.size();i++)
        {
            stat.add(statusData.get(i).display_name);
            values.add(String.valueOf(statusData.get(i).value));
        }
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(), R.layout.spinner_title_text,stat);
        adapter.setDropDownViewResource(R.layout.spinner_adapter_value);
        spStatusDisplay.setAdapter(adapter);

        rlFilterLayout.setVisibility(View.VISIBLE);

        spStatusDisplay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                value=values.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    public void hideSortUi()
    {
        try {
            if(llSort.getVisibility()==View.VISIBLE)
                llSort.setVisibility(View.GONE);
            rlOverlay.setVisibility(View.GONE);
        }
        catch (Exception e)
        {}

    }



}
