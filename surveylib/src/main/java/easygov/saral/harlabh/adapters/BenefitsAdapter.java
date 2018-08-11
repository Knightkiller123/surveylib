package easygov.saral.harlabh.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import easygov.saral.harlabh.R;
import easygov.saral.harlabh.models.surveypaging.CustomBenefits;


/**
 * Created by apoorv on 15/01/18.
 */

public class BenefitsAdapter extends RecyclerView.Adapter<BenefitsAdapter.Holder> {
    private List<CustomBenefits> listBenefits;
    private Context context;

    public BenefitsAdapter(Context context, List<CustomBenefits> customBenefits) {
        listBenefits=customBenefits;
        this.context=context;}

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.adapter_benefits,parent,false);
        Holder holder=new Holder(view);
        return holder;}

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        String s="";
        if(listBenefits.get(position).frequency!=null) {
            if (listBenefits.get(position).frequency.equals("y")) {
                s = context.getResources().getString(R.string.yearly);
            } else if (listBenefits.get(position).frequency.equals("hy")) {
                s = context.getResources().getString(R.string.halfyearly);
            } else if (listBenefits.get(position).frequency.equals("q")) {
                s = context.getResources().getString(R.string.quarterly);
            } else if (listBenefits.get(position).frequency.equals("m")) {
                s = context.getResources().getString(R.string.monthly);
            } else if (listBenefits.get(position).frequency.equals("w")) {
                s = context.getResources().getString(R.string.weekly);
            } else if (listBenefits.get(position).frequency.equals("d")) {
                s = context.getResources().getString(R.string.daily);
            } else if (listBenefits.get(position).frequency.equals("h")) {
                s = context.getResources().getString(R.string.hourly);}
            else if (listBenefits.get(position).frequency.equals("ot")) {
                s = context.getResources().getString(R.string.onetime);}
            else if (listBenefits.get(position).frequency.equals("o")) {
                if(listBenefits.get(position).other_benefit_unit!=null &&
                        listBenefits.get(position).other_benefit_unit.length()>0)
                s = context.getResources().getString(R.string.fortext) +" "+listBenefits.get(position).other_benefit_unit;

                else s="";
            }}

        String benefit=" ";
        if(listBenefits.get(position).benefit!=null&& listBenefits.get(position).benefit.length()>0)
            benefit=listBenefits.get(position).benefit;

        String condition =" ";
        if(listBenefits.get(position).condition!=null&& listBenefits.get(position).condition.length()>0)
            condition=listBenefits.get(position).condition;

        double value=0.0;
        if(listBenefits.get(position).value!=null)
            value=listBenefits.get(position).value;

        String valunit =" ";
        if(listBenefits.get(position).value_unit!=null&& listBenefits.get(position).value_unit.length()>0)
            valunit=listBenefits.get(position).value_unit;

        condition="";
        if(value!=0.0)
            holder.tvBenefitsCustom.setText(benefit+" "+String.format( "%.2f",value)+" "
                    +valunit+" "+s+" "+condition);

        else holder.tvBenefitsCustom.setText(benefit+" "
                +" "+s+" "+condition);

    }

    @Override
    public int getItemCount() {
        return listBenefits.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private TextView tvBenefitsCustom;
        public Holder(View itemView) {
            super(itemView);
            tvBenefitsCustom=itemView.findViewById(R.id.tvBenefitsCustom);
        }
    }
}
