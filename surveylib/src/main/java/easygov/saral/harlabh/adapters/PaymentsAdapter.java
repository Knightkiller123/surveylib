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
import easygov.saral.harlabh.activity.PaymentStatusActivity;
import easygov.saral.harlabh.models.responsemodels.paymentinternal.InternalPaymentResponse;

/**
 * Created by apoorv on 22/01/18.
 */

public class PaymentsAdapter extends RecyclerView.Adapter<PaymentsAdapter.Holder> {

    private Context context;
    private List<InternalPaymentResponse> list;
    public PaymentsAdapter(Context context, List<InternalPaymentResponse> objects) {
        this.context=context;
        list=objects;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.adapter_payments,parent,false);
        Holder holder=new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {

        //Todo: note -> string to date and date to desired
        SimpleDateFormat formats = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            Date date = formats.parse(list.get(position).created_on);
            SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
            SimpleDateFormat formatter1= new SimpleDateFormat("HH:mm");


            holder.tvPaymentDate.setText(formatter.format(date)+" | "+formatter1.format(date));
        } catch (ParseException e) {
            Log.d("date",""+e);
            e.printStackTrace();
        }

        holder.tvPaymentAmount.setText(list.get(position).action);
        holder.tvPaymentName.setText(list.get(position).scheme_and_service_category);
        holder.tvPaymentSubName.setText(list.get(position).scheme_and_service_name);
        // if(date.toString()!=null)

        holder.tvPaymentStatus.setText(list.get(position).status);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private TextView tvPaymentName,tvPaymentAmount,tvPaymentSubName,tvPaymentDate,tvPaymentTransaction,tvPaymentStatus;
        public Holder(View itemView) {
            super(itemView);

            tvPaymentName= itemView.findViewById(R.id.tvPaymentName);

            tvPaymentAmount= itemView.findViewById(R.id.tvPaymentAmount);
            tvPaymentSubName= itemView.findViewById(R.id.tvPaymentSubName);
            tvPaymentDate= itemView.findViewById(R.id.tvPaymentDate);
            tvPaymentTransaction= itemView.findViewById(R.id.tvPaymentTransaction);
            tvPaymentStatus= itemView.findViewById(R.id.tvPaymentStatus);

        }
    }
}

