package easygov.saral.harlabh.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import easygov.saral.harlabh.R;
import easygov.saral.harlabh.activity.SecondSchemesActivity;

/**
 * Created by apoorv on 22/02/18.
 */

public class FilterSchemesAdater extends RecyclerView.Adapter<FilterSchemesAdater.Holder> {

    private Context context;
    private List<String> list;
    public FilterSchemesAdater(Context secondSchemesActivity, List<String> filterWord) {
        context=secondSchemesActivity;
        list=filterWord;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.adapter_schemes,parent,false);
        Holder holder=new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final Holder holder, final int position) {

        holder.tvSchemeFilterKeyword.setText(list.get(position).toString());
        holder.tvSchemeFilterKeyword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(list.get(position).equals(context.getResources().getString(R.string.allcategories)))
                    ((SecondSchemesActivity)context).setAll();

                else ((SecondSchemesActivity)context).setFilteredData(list.get(position).toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private TextView tvSchemeFilterKeyword;
        public Holder(View itemView) {
            super(itemView);
            tvSchemeFilterKeyword= itemView.findViewById(R.id.tvSchemeFilterKeyword);
        }
    }
}
