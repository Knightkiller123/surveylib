package easygov.saral.harlabh.fragments.managementfragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;


import easygov.saral.harlabh.R;
import easygov.saral.harlabh.adapters.StatusApplicationAdapter;
import easygov.saral.harlabh.models.applicationstatus.ApplicationStatus;
import easygov.saral.harlabh.utils.GeneralFunctions;
import easygov.saral.harlabh.utils.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class StatusApplicationFragment extends Fragment {

    private RecyclerView rvAppStatus;
    private RelativeLayout rlStatusFrag;
    private ImageView ivApplicationStatusBack;
    private TextView tvStatusFilter,tvStatusTime;
    private PopupWindow pw;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_status_application, container, false);

        init(view);
        return view;
    }

    private void init(View view) {
        rvAppStatus= view.findViewById(R.id.rvAppStatus);
        //rvAppStatus.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvAppStatus.setLayoutManager(new GridLayoutManager(getActivity(),2));
        tvStatusFilter= view.findViewById(R.id.tvStatusFilter);
        tvStatusTime= view.findViewById(R.id.tvStatusTime);
        ivApplicationStatusBack= view.findViewById(R.id.ivApplicationStatusBack);
        rlStatusFrag = view.findViewById(R.id.rlStatusFrag);
        ivApplicationStatusBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();

            }
        });

        tvStatusFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiatePopupWindow(v);
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        hitCountApi();
    }

    private void hitCountApi() {
        GeneralFunctions.showDialog(getActivity());
        Call<ApplicationStatus> call= RestClient.get().applicationStatus();
        call.enqueue(new Callback<ApplicationStatus>() {
            @Override
            public void onResponse(Call<ApplicationStatus> call, Response<ApplicationStatus> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful())
                {
                    if(response.body().code==401)
                    {
                        GeneralFunctions.tokenExpireAction(getActivity());
                    }
                    else if(response.body().success==1)
                    {
                        rvAppStatus.setAdapter(new StatusApplicationAdapter(getActivity(),response.body().data.objects));
                    }
                    else {
                        GeneralFunctions.makeSnackbar(rlStatusFrag,response.body().message);

                    }
                }
                else {
                    GeneralFunctions.makeSnackbar(rlStatusFrag,getResources().getString(R.string.serverIssue));
                }
            }

            @Override
            public void onFailure(Call<ApplicationStatus> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(rlStatusFrag,getResources().getString(R.string.netIssue));

            }
        });
    }

    private void hitCountApiDate(String startdate, String enddate) {
        GeneralFunctions.showDialog(getActivity());
        Call<ApplicationStatus> call= RestClient.get().applicationStatuswithDate(startdate,enddate);
        call.enqueue(new Callback<ApplicationStatus>() {
            @Override
            public void onResponse(Call<ApplicationStatus> call, Response<ApplicationStatus> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful())
                {
                    if(response.body().code==401)
                    {
                        GeneralFunctions.tokenExpireAction(getActivity());
                    }
                    else if(response.body().success==1)
                    {
                        rvAppStatus.setAdapter(new StatusApplicationAdapter(getActivity(),response.body().data.objects));
                    }
                    else {
                        GeneralFunctions.makeSnackbar(rlStatusFrag,response.body().message);

                    }
                }
                else {
                    GeneralFunctions.makeSnackbar(rlStatusFrag,getResources().getString(R.string.serverIssue));
                }
            }

            @Override
            public void onFailure(Call<ApplicationStatus> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(rlStatusFrag,getResources().getString(R.string.netIssue));

            }
        });
    }

    private void initiatePopupWindow(View view) {

        try {
            LayoutInflater inflater = (LayoutInflater)getContext()
                    .getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.info_day,
                    (ViewGroup)view.findViewById(R.id.search_days));
            pw = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
            pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
            TextView tvOneDay= layout.findViewById(R.id.tvOneDay);
            TextView tvSevenDay= layout.findViewById(R.id.tvSevenDay);
            TextView tvFourteenDays= layout.findViewById(R.id.tvFourteenDays);
            TextView tvThirtyDays= layout.findViewById(R.id.tvThirtyDays);
            TextView tvLifetime= layout.findViewById(R.id.tvLifetime);
            RelativeLayout rlDaysClose= layout.findViewById(R.id.rlDaysClose);


            rlDaysClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pw.dismiss();
                }
            });

            tvOneDay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDAte(0,0,1);
                    pw.dismiss();
                    tvStatusFilter.setText(getResources().getString(R.string.oneday));
                }
            });

            tvSevenDay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDAte(0,0,7);
                    pw.dismiss();
                    tvStatusFilter.setText(getResources().getString(R.string.sevenday));
                }
            });

            tvFourteenDays.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDAte(0,0,14);
                    pw.dismiss();
                    tvStatusFilter.setText(getResources().getString(R.string.forteenday));
                }
            });


            tvThirtyDays.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDAte(0,0,30);
                    pw.dismiss();
                    tvStatusFilter.setText(getResources().getString(R.string.thirtyday));
                }
            });

            tvLifetime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDAte(0,0,0);
                    pw.dismiss();
                    tvStatusFilter.setText(getResources().getString(R.string.lifetime));
                }
            });


            pw.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    pw.dismiss();

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void  getDAte(int yy,int mm,int dd)
    {

        if(dd!=0) {
            //Date currentTime = Calendar.getInstance().getTime();
            Calendar myCalendar = Calendar.getInstance();

            int currentYear = myCalendar.get(Calendar.YEAR);
            int currentMonth = myCalendar.get(Calendar.MONTH) + 1;
            int currentDay = myCalendar.get(Calendar.DAY_OF_MONTH);

            String enddate = currentDay + "-" + currentMonth + "-" + currentYear;

            int reqyr = currentYear;
            int reqmonth = 0;
            int reqday = 0;
            if (currentDay - dd > 0) {
                reqday = currentDay - dd;
                reqmonth = currentMonth;
            } else {
                reqday = currentDay;
                reqmonth = currentMonth - 1;
                if(reqmonth==0) {
                    reqmonth = 12;
                    reqyr=currentYear-1;
                }
            }

            if(currentMonth==3)
            {
                if(currentDay>28)
                    reqday=28;
            }


            else if(currentDay==31)
                reqday=30;


            String startDate = reqday + "-" + reqmonth + "-" + reqyr;
            hitCountApiDate(startDate, enddate);
        }

        else {
            hitCountApi();
        }
    }
}
