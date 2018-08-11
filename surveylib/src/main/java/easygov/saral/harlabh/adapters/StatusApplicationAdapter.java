package easygov.saral.harlabh.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import easygov.saral.harlabh.R;
import easygov.saral.harlabh.models.applicationstatus.ApplicationCount;

/**
 * Created by apoorv on 07/12/17.
 */

public class StatusApplicationAdapter extends RecyclerView.Adapter<StatusApplicationAdapter.Holder> {

    private Context context;
    private List<ApplicationCount> appcount;
    public StatusApplicationAdapter(Context context, List<ApplicationCount> data)
    {
        this.context=context;
        appcount=data;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view =inflater.inflate(R.layout.adapter_statusapplication,parent,false);
        Holder holder=new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        holder.tvNameApplication.setText(appcount.get(position).status_name);
        holder.tvNoApplication.setText(String.valueOf(appcount.get(position).application_count));

       /* if(appcount.size()/2==0)
        {
            if(position<appcount.size()-1)
            {
               holder.tvNameApplication.setText(appcount.get(position).status_name);
                holder.tvNoApplication.setText(appcount.get(position).application_count);

                holder.tvNameApplication1.setText(appcount.get(position+1).status_name);
                holder.tvNoApplication1.setText(appcount.get(position+1).application_count);
            }
        }
        else {
            if(position<appcount.size()-1)
            {
                holder.tvNameApplication.setText(appcount.get(position).status_name);
                holder.tvNoApplication.setText(appcount.get(position).application_count);

                holder.tvNameApplication1.setText(appcount.get(position+1).status_name);
                holder.tvNoApplication1.setText(appcount.get(position+1).application_count);
            }

            else {
                holder.tvNameApplication.setText(appcount.get(position).status_name);
                holder.tvNoApplication.setText(appcount.get(position).application_count);
            }
        }*/
    }

    @Override
    public int getItemCount() {
        return appcount.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private TextView tvNameApplication,tvNoApplication,tvNameApplication1,tvNoApplication1;
        public Holder(View itemView) {
            super(itemView);
            tvNameApplication= itemView.findViewById(R.id.tvNameApplication);
            tvNoApplication= itemView.findViewById(R.id.tvNoApplication);

            //tvNameApplication1=(TextView) itemView.findViewById(R.id.tvNameApplication1);
            //tvNoApplication1=(TextView) itemView.findViewById(R.id.tvNoApplication1);
        }
    }
}
