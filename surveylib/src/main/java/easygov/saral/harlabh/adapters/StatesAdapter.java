package easygov.saral.harlabh.adapters;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;


import easygov.saral.harlabh.activity.StateSelectActivity;
import easygov.saral.harlabh.models.responsemodels.statesndistricts.DistrictsModel;
import easygov.saral.harlabh.utils.GeneralFunctions;
import easygov.saral.harlabh.utils.Prefs;
import easygov.saral.harlabh.utils.RestClient;
import easygov.saral.harlabh.R;
import easygov.saral.harlabh.models.responsemodels.statesndistricts.StateDetails;
import easygov.saral.harlabh.utils.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by apoorv on 26/07/17.
 */

public class StatesAdapter extends RecyclerView.Adapter<StatesAdapter.ViewHolder> {
    private Context context;
    private List<StateDetails> states;
    private Prefs mPrefs;
    private Snackbar bar;
    public static  int stateClicked=0;
    public StatesAdapter(StateSelectActivity stateSelect, List<StateDetails> states) {
        this.context=stateSelect;
        this.states=states;
        mPrefs=Prefs.with(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.grid_states,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.tvStateTitle.setText(states.get(position).state_name);
            setImageView(holder.cvStates,states.get(position).states_id);
        holder.llStateGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //holder.llStateGrid.setVisibility(View.INVISIBLE);
               // mPrefs.save(Constants.GeographySelected, states.get(position).state_name);
                mPrefs.save(Constants.StateSelected,states.get(position).state_name.toString());
                stateClicked=1;
                getDistrictsbyId(states.get(position).states_id);
                //((StateSelectActivity)context).hideStates();
            }
        });

    }

    private void getDistrictsbyId(Integer id)
    {
        GeneralFunctions.showDialog(context);
        Call<DistrictsModel> call = RestClient.get().getSelectedDistricts(id.toString(),"10000");
        call.enqueue(new Callback<DistrictsModel>() {
            @Override
            public void onResponse(Call<DistrictsModel> call, Response<DistrictsModel> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful())
                {
                    if(response.body().code.equals("401"))
                    {
                        GeneralFunctions.tokenExpireAction(context);
                    }
                    else if(response.body().success==1)
                    {
                        ((StateSelectActivity)context).districtMdel=response.body().data.objects;
                       /* DbHelper db=new DbHelper();
                        db.addDistricts(districts,integer);*/
                        ((StateSelectActivity)context).hideStates();
                        ((StateSelectActivity)context).setFilterList(response.body());
                        mPrefs.save(Constants.Direct,"no");



                    }
                }
                else {

                    Toast.makeText(context, context.getResources().getString(R.string.serverIssue), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<DistrictsModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                Toast.makeText(context, context.getResources().getString(R.string.netIssue), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return states.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView cvStates;
        private LinearLayout llStateGrid;
        private TextView tvStateTitle;
        public ViewHolder(View itemView) {
            super(itemView);

            cvStates= itemView.findViewById(R.id.cvStates);
            tvStateTitle= itemView.findViewById(R.id.tvStateTitle);
            llStateGrid= itemView.findViewById(R.id.llStateGrid);
        }
    }

    private void setImageView(ImageView img ,int pos)
    {
        switch (pos)
        {
            case 1:
                img.setBackgroundResource(R.drawable.delhi_01);
                break;

            case 2:
                img.setBackgroundResource(R.drawable.up_01);
                break;

            case 3:
                img.setBackgroundResource(R.drawable.punjab_01);
                break;

            case 4:
                img.setBackgroundResource(R.drawable.maharastra_01);
                break;

            case 5:
                img.setBackgroundResource(R.drawable.west_bengal_01);
                break;

            case 6:
                img.setBackgroundResource(R.drawable.karnataka_01);
                break;

            case 7:
                img.setBackgroundResource(R.drawable.gujrat_01);
                break;

            case 8:
                img.setBackgroundResource(R.drawable.rajasthan_01);
                break;

            case 9:
                img.setBackgroundResource(R.drawable.bihar_01);
                break;

            case 10:
                img.setBackgroundResource(R.drawable.telangana_01);

                break;

            case 11:
                img.setBackgroundResource(R.drawable.ap_01);

                break;

            case 12:
                img.setBackgroundResource(R.drawable.tamilnadu_01);

                break;

            case 13:
                img.setBackgroundResource(R.drawable.kerala_01);

                break;

            case 14:
                img.setBackgroundResource(R.drawable.chhattisgarh_01);
                break;

            case 15:
                break;

            case 16:
                img.setBackgroundResource(R.drawable.odisa_01);

                break;

            case 17:
                img.setBackgroundResource(R.drawable.mp_01);

                break;

            case 18:
                img.setBackgroundResource(R.drawable.assam_01);

                break;

            case 19:
                img.setBackgroundResource(R.drawable.uttrakhand_01);

                break;

            case 20:
                img.setBackgroundResource(R.drawable.haryana_01);

                break;


            case 21:
                img.setBackgroundResource(R.drawable.kashmir_01);

                break;

            case 22:
                img.setBackgroundResource(R.drawable.hp_01);

                break;

            case 23:
                img.setBackgroundResource(R.drawable.chandigrah_01);

                break;

            case 24:
                img.setBackgroundResource(R.drawable.puducherry_01);

                break;

            case 25:
                img.setBackgroundResource(R.drawable.tripura_01);

                break;

            case 26:
                img.setBackgroundResource(R.drawable.meghalaya_01);

                break;

            case 27:
                img.setBackgroundResource(R.drawable.goa_01);

                break;

            case 28:
                img.setBackgroundResource(R.drawable.manipur_01);

                break;

            case 29:
                img.setBackgroundResource(R.drawable.mizoram_01);

                break;

            case 30:
                img.setBackgroundResource(R.drawable.dadra_01);

                break;

            case 31:
                img.setBackgroundResource(R.drawable.sikkim_01);

                break;

            case 32:
                img.setBackgroundResource(R.drawable.andaman_01);

                break;

            case 33:
                img.setBackgroundResource(R.drawable.daman_01);

                break;

            case 34:
                img.setBackgroundResource(R.drawable.arunachal_01);

                break;

            case 35:
                img.setBackgroundResource(R.drawable.lakshadeew_01);

                break;

            case 36:
                img.setBackgroundResource(R.drawable.jharkhand_01);

                break;

            case 37:
                img.setBackgroundResource(R.drawable.nagaland_01);

                break;









        }
    }
}
