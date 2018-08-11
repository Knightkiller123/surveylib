package easygov.saral.harlabh.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import easygov.saral.harlabh.R;
import easygov.saral.harlabh.models.responsemodels.filterstatus.StaticData;


/**
 * Created by vaishali on 16/03/18.
 */

public class AboutUsAdapter extends RecyclerView.Adapter<AboutUsAdapter.Holder> {
    private List<StaticData> listBenefits;
    private Context context;

    public AboutUsAdapter(Context context, List<StaticData> customBenefits) {
        listBenefits=customBenefits;
        this.context=context;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.content_about_us,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
     holder.tvData.setText(Html.fromHtml(listBenefits.get(position).body));
     holder.tvHeader.setText(listBenefits.get(position).subject  );
       // Html.fromHtml(response.body().data.declaration)
    }

    @Override
    public int getItemCount() {
        return listBenefits.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private TextView tvHeader,tvData;
        public Holder(View itemView) {
            super(itemView);
            tvHeader=itemView.findViewById(R.id.tvHeader);
            tvData=itemView.findViewById(R.id.tvData);
        }
    }

}
