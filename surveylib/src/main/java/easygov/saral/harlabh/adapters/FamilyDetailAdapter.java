package easygov.saral.harlabh.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import easygov.saral.harlabh.R;
import easygov.saral.harlabh.models.responsemodels.familymodel.Family_profile;
import easygov.saral.harlabh.models.responsemodels.generalmodel.GeneralModel;
import easygov.saral.harlabh.utils.Constants;
import easygov.saral.harlabh.utils.GeneralFunctions;
import easygov.saral.harlabh.utils.Prefs;
import easygov.saral.harlabh.utils.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vaishali on 26/02/18.
 */

public class FamilyDetailAdapter extends RecyclerView.Adapter<FamilyDetailAdapter.ViewHolder> {
    private Context context;
    private List<Family_profile> list;
    private Prefs mPrefs;
    private OnUpdateData onUpdateData;
    public FamilyDetailAdapter(Context context, List<Family_profile> family,OnUpdateData onUpdateData) {
        this.context=context;
        list=family;
        this.onUpdateData=onUpdateData;
        mPrefs=Prefs.with(context);
    }


    public  interface  OnUpdateData
    {
        void removeAtPosition(int position);
        void dismissDialogg();
        void updateUi();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.content_family_detail,parent,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {


        if(holder.tvName!=null)
            holder.tvName.setText(list.get(position).name);

        if(holder.tvTag!=null)
            holder.tvTag.setText(list.get(position).relationship);



        holder.tvRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                delete(position);

            }
        });


        holder.tvKnowTheirSchemes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(position==0)
                {
                    mPrefs.save(Constants.Applyingfor,"myself");
                    mPrefs.save(Constants.ProfileId,"");
                }
                else {*/
                mPrefs.save(Constants.Applyingfor,"family");
                mPrefs.save(Constants.ProfileId,list.get(position).id);
                mPrefs.save(Constants.FirstName,list.get(position).name);
                //  }
                mPrefs.save(Constants.FromFamilyGo,"yes");
                onUpdateData.dismissDialogg();


            }
        });





    }

    private void delete(final int position)
    {
        GeneralFunctions.showDialog(context);
        Call<GeneralModel> call =RestClient.get().getdele(String.valueOf(list.get(position).id));
        call.enqueue(new Callback<GeneralModel>() {
            @Override
            public void onResponse(Call<GeneralModel> call, Response<GeneralModel> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful())
                {
                    if(response.body().code.equals("401"))
                    {
                        GeneralFunctions.tokenExpireAction(context);
                    }
                    else if(response.body().success==1)
                    {
                        onUpdateData.removeAtPosition(position);
                    }
                    else {
                        Toast.makeText(context,response.body().message, Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(context,context.getResources().getString(R.string.serverIssue), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeneralModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                Toast.makeText(context,context.getResources().getString(R.string.netIssue), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvRemove,tvKnowTheirSchemes,tvName,tvPhoneNumber,tvTag;
        public ViewHolder(View itemView) {
            super(itemView);
            tvRemove=itemView.findViewById(R.id.tvRemove);
            tvKnowTheirSchemes=itemView.findViewById(R.id.tvKnowTheirSchemes);
            tvPhoneNumber=itemView.findViewById(R.id.tvPhoneNumber);
            tvName=itemView.findViewById(R.id.tvName);
            tvTag=itemView.findViewById(R.id.tvTag);


        }
    }

    public void remove(List<Family_profile> familyss)
    {

    }
}

