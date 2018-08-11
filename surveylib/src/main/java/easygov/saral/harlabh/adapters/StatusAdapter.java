package easygov.saral.harlabh.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import easygov.saral.harlabh.R;
import easygov.saral.harlabh.models.responsemodels.statusmodel.StatusList;

/**
 * Created by apoorv on 11/12/17.
 */

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.Holder> {

    private Context context;
    List<StatusList> status;
    public StatusAdapter(Context context, List<StatusList> objects)
    {
        this.context=context;
        status=objects;
    }
    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.adapter_status,parent,false);
        Holder holder=new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        holder.tvStatus.setText(status.get(position).status);
        SimpleDateFormat formats = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            Date date = formats.parse(status.get(position).date);
            SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
            SimpleDateFormat formatter1= new SimpleDateFormat("HH:mm");


            holder.tvDate.setText(formatter.format(date)+" | "+formatter1.format(date));
        } catch (ParseException e) {
            Log.d("date",""+e);
            e.printStackTrace();
        }

        if(position==0)
            holder.v1.setVisibility(View.GONE);

        if(position==status.size()-1)
            holder.v2.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return status.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private TextView tvStatus,tvDate;
        private View v1,v2;
        public Holder(View itemView) {
            super(itemView);
            tvStatus= itemView.findViewById(R.id.tvStatus);
            tvDate= itemView.findViewById(R.id.tvDate);
            v1= itemView.findViewById(R.id.v1);
            v2= itemView.findViewById(R.id.v2);

        }
    }
}
