package easygov.saral.harlabh.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.List;

import easygov.saral.harlabh.R;
import easygov.saral.harlabh.activity.ManagementActivity;
import easygov.saral.harlabh.activity.StatusReportActivity;
import easygov.saral.harlabh.models.responsemodels.filter.FilterListData;
import easygov.saral.harlabh.models.responsemodels.statusmodel.StatusResponse;
import easygov.saral.harlabh.utils.GeneralFunctions;
import easygov.saral.harlabh.utils.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by apoorv on 26/09/17.
 */

public class FilterApplicationAdapter extends RecyclerView.Adapter<FilterApplicationAdapter.Holder> {

    private Context context;
    private List<FilterListData> filterList;
    public FilterApplicationAdapter(Context activity, List<FilterListData> objects)
    {
    context=activity;
    filterList=objects;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.adapter_filter,parent,false);
        Holder holder=new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, final int position) {

        //there are two ids usid and status id , which you use ??
        holder.tvFilterId.setText("#"+filterList.get(position).user_services_id);


        SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        java.util.Date date ;
        String newFormat="";
        try
        {
            date = form.parse(filterList.get(position).created_on);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            newFormat = formatter.format(date);
            holder.tvFilterDate.setText(newFormat);

        }
        catch (Exception e)
        {

            e.printStackTrace();
        }




        holder.tvFilterCategory.setText(filterList.get(position).service_category_name);
        holder.tvFilterSubCat.setText(filterList.get(position).service_name);
        holder.tvFilterStatus.setText(filterList.get(position).status_name);

        holder.rlGetStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hitGetStatusApi(filterList.get(position).user_services_id);
            }
        });

    }

    private void hitGetStatusApi(Integer user_services_id) {
        GeneralFunctions.showDialog(context);
        Call<StatusResponse> call= RestClient.get().getstatus(String.valueOf(user_services_id));
        call.enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                if(response.isSuccessful())
                {
                    GeneralFunctions.dismissDialog();
                    if(response.body().code==401)
                    {
                        GeneralFunctions.tokenExpireAction(context);
                    }
                    else
                    if(response.body().success==1)
                    {
                        Bundle bundle=new Bundle();
                        bundle.putString("statuslist",new Gson().toJson(response.body()));
                        Intent intent =new Intent(context, StatusReportActivity.class);
                        intent.putExtras(bundle);
                        context.startActivity(intent);
                        ((ManagementActivity)context).hideUi();
                    }
                    else {
                        Toast.makeText(context, response.body().message, Toast.LENGTH_SHORT).show();

                    }
                }
                else {
                    Toast.makeText(context, context.getResources().getString(R.string.serverIssue), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                Toast.makeText(context, context.getResources().getString(R.string.netIssue), Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public int getItemCount() {
        return filterList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private TextView tvFilterDate,tvFilterId,tvFilterSubCat,tvFilterCategory,tvFilterStatus;
        private RelativeLayout rlGetStatus;
        public Holder(View itemView) {
            super(itemView);
            tvFilterDate= itemView.findViewById(R.id.tvFilterDate);

            tvFilterId= itemView.findViewById(R.id.tvFilterId);
            tvFilterSubCat= itemView.findViewById(R.id.tvFilterSubCat);
            tvFilterCategory= itemView.findViewById(R.id.tvFilterCategory);
            tvFilterStatus= itemView.findViewById(R.id.tvFilterStatus);
            rlGetStatus= itemView.findViewById(R.id.rlGetStatus);


        }
    }
}
