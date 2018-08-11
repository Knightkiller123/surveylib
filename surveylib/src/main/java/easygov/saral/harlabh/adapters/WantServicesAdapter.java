package easygov.saral.harlabh.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import easygov.saral.harlabh.activity.SplitSchemeActivity;
import easygov.saral.harlabh.models.responsemodels.categoriesmodel.CategoryDetails;
import easygov.saral.harlabh.utils.Prefs;
import easygov.saral.harlabh.R;
import easygov.saral.harlabh.activity.FirstSurveyActivity;
import easygov.saral.harlabh.utils.Constants;
import easygov.saral.harlabh.utils.RecyclerBottomListener;

import static easygov.saral.harlabh.fragments.storyboardfrags.YourStoryDetailsFragment.count;

/**
 * Created by apoorv on 23/08/17.
 */

public class WantServicesAdapter extends RecyclerView.Adapter<WantServicesAdapter.Holder> {

    private List<CategoryDetails> categoryDetails;
    private Context context;
    private Prefs mPrefs;
    RecyclerBottomListener onBottomReachedListener;
    public WantServicesAdapter(Context selectServiceActivity, List<CategoryDetails> list) {
        context=selectServiceActivity;
        categoryDetails=list;
        mPrefs=Prefs.with(context);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.adapter_wantservices,parent,false);
        Holder holder=new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, final int position) {
        holder.tvWantService.setText(categoryDetails.get(position).service.name );
        holder.tvServCat.setText(categoryDetails.get(position).category.name);
        try {
            if(position == categoryDetails.size() - 1){

                onBottomReachedListener.onBottomReached(position);

            }
        }
        catch (Exception e)
        {}

        holder.rlWantAdap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("is_category", "0");
                if (categoryDetails.get(position).category.scheme) {
                    bundle.putString("is_scheme", "1");
                    mPrefs.save(Constants.IsScheme, "YES");
                    mPrefs.save(Constants.SecontActivityText, context.getResources().getString(R.string.schemes));
                } else {
                    bundle.putString("is_scheme", "0");
                    mPrefs.save(Constants.IsScheme, "NO");
                    mPrefs.save(Constants.SecontActivityText, context.getResources().getString(R.string.Services));

                    if (SurveyAdapter.list != null) {
                        SurveyAdapter.list.clear();
                        SurveyAdapter.list = new ArrayList<>();
                        count = 0;
                    }
                }
                mPrefs.save(Constants.FromServices, "yes");


             //   if(categoryDetails.get(position).sgm_id!=7472) {

                    Intent intent = new Intent(context, FirstSurveyActivity.class);
                    mPrefs.save("sgm_new_id",categoryDetails.get(position).sgm_id.toString());
                    bundle.putString("selected_id", categoryDetails.get(position).sgm_id.toString());
                    bundle.putString("geography_id", categoryDetails.get(position).geography_id.toString());
                    intent.putExtras(bundle);
                    context.startActivity(intent);
               /* }

                else
                {

                    Intent intent = new Intent(context, SplitSchemeActivity.class);
                    bundle.putString("selected_id", categoryDetails.get(position).sgm_id.toString());
                    bundle.putString("geography_id", categoryDetails.get(position).geography_id.toString());
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }*/
            }
        });

    }

    @Override
    public int getItemCount() {
        return categoryDetails.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private TextView tvWantService,tvServCat;
        private LinearLayout rlWantAdap;
        public Holder(View itemView) {
            super(itemView);
            tvWantService= itemView.findViewById(R.id.tvWantService);
            tvServCat= itemView.findViewById(R.id.tvServCat);
            rlWantAdap= itemView.findViewById(R.id.rlWantAdap);
        }
    }

    public void setOnBottomReachedListener(RecyclerBottomListener onBottomReachedListener){

        this.onBottomReachedListener = onBottomReachedListener;
    }
}
