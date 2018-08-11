package easygov.saral.harlabh.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import easygov.saral.harlabh.activity.HomeActivity;
import easygov.saral.harlabh.activity.StateSelectActivity;
import easygov.saral.harlabh.models.responsemodels.statesndistricts.DistrictDetails;
import easygov.saral.harlabh.utils.Constants;
import easygov.saral.harlabh.utils.Prefs;
import easygov.saral.harlabh.R;

/**
 * Created by apoorv on 26/07/17.
 */

public class DistrictsAdapter extends RecyclerView.Adapter<DistrictsAdapter.ViewHolder> {
    private List<DistrictDetails> dist;
    private Context context;
    private Prefs mPrefs;
    public DistrictsAdapter(StateSelectActivity stateSelect, List<DistrictDetails> dist) {
        this.context=stateSelect;
        this.dist=dist;
        mPrefs=Prefs.with(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.adapter_district,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        if(mPrefs.getString(Constants.Direct,"").equals("yes")||mPrefs.getString(Constants.Direct,"").isEmpty())
        {
            ((StateSelectActivity)context).rlStateImg.setVisibility(View.GONE);
            holder.tvDistSelect.setText(dist.get(position).district_name+" ( " +dist.get(position).state_name+" )");
        }
        else {
            //((StateSelectActivity)context).rlStateImg.setVisibility(View.VISIBLE);
            ((StateSelectActivity)context).setStateIconIndividual(dist.get(position).state_id);
        holder.tvDistSelect.setText(dist.get(position).district_name);}
        holder.tvDistSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  String s= mPrefs.getString(Constants.GeographySelected,"");
                mPrefs.save(Constants.GeographySelected,dist.get(position).state_name+", "+dist.get(position).district_name);
                mPrefs.save(Constants.Location,true);
                mPrefs.save(Constants.DistrictSelectedStart,""+dist.get(position).district_name);
                mPrefs.save(Constants.GeographyId,dist.get(position).geography_id.toString());
                StatesAdapter.stateClicked=0;
                mPrefs.save(Constants.StateId,dist.get(position).state_id);
                mPrefs.save(Constants.DistId,dist.get(position).districts_id);

                /*MyApplication.stateId=dist.get(position).state_id;
                MyApplication.distId=dist.get(position).districts_id;*/
                Intent intent =new Intent(context, HomeActivity.class);
                mPrefs.save(Constants.Direct,"yes");
                context.startActivity(intent);
                ((StateSelectActivity)context).finish();



            }
        });

    }

    @Override
    public int getItemCount() {
        return dist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDistSelect;
        public ViewHolder(View itemView) {
            super(itemView);
            tvDistSelect= itemView.findViewById(R.id.tvDistSelect);
        }
    }
}
