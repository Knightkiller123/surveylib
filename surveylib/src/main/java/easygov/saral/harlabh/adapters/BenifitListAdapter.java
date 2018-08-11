package easygov.saral.harlabh.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import easygov.saral.harlabh.R;
import easygov.saral.harlabh.activity.BeneficiaryActivity;
import easygov.saral.harlabh.models.responsemodels.beneficiarymodel.Beneficiary;
import easygov.saral.harlabh.utils.GeneralFunctions;

/**
 * Created by apoorv on 22/02/18.
 */

public class BenifitListAdapter extends RecyclerView.Adapter<BenifitListAdapter.Holder> {

    private Context context;
    private List<Beneficiary> list;
    public int checkedPosition=-1;

    public BenifitListAdapter(Context activity, List<Beneficiary> beneficiaries) {
        this.context=activity;
        list=beneficiaries;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.adapter_beneficiary,parent,false);
        Holder holder=new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        holder.tvBenificType.setText(list.get(position).name);
        holder.rbBenType.setChecked(list.get(position).selected);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder  {
        private RadioButton rbBenType;
        private TextView tvBenificType;
        private ImageView ivBenInfo;
        public Holder(View itemView) {
            super(itemView);
            rbBenType =itemView.findViewById(R.id.cbBenType);
            tvBenificType= itemView.findViewById(R.id.tvBenificType);
            ivBenInfo= itemView.findViewById(R.id.ivBenInfo);
            ivBenInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            rbBenType.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {




                    if(checkedPosition==-1)
                    {
                        checkedPosition=getAdapterPosition();
                        list.get(getAdapterPosition()).selected=true;

                    }
                    else if(checkedPosition== getAdapterPosition())
                    {
                        list.get(getAdapterPosition()).selected=!
                                list.get(getAdapterPosition()).selected;

                    }
                    else
                    {
                        list.get(checkedPosition).selected=false;
                        notifyItemChanged(checkedPosition);
                        checkedPosition=getAdapterPosition();
                        list.get(getAdapterPosition()).selected=true;
                    }
                    notifyItemChanged(getAdapterPosition());


                }
            });
        }

       }


    public String getSelectedPosition()
       {
           if(checkedPosition==-1) {
               GeneralFunctions.makeSnackbar(((BeneficiaryActivity)context).rlBenifitContainer,context.getResources().getString(R.string.please_select_beneficiary));
               return "";

           }
           else
               return list.get(checkedPosition).id.toString();
       }
}
