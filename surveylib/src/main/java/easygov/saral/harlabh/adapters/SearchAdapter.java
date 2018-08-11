package easygov.saral.harlabh.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import easygov.saral.harlabh.R;

/**
 * Created by vaishali on 24/02/18.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolderInnner> {

        Context context;
        List<String> stat = new ArrayList<>();
private final OnItemClickListener listener;


public interface OnItemClickListener {
    void onItemClick(Integer pos,String val);
}

    public SearchAdapter(Context context, List<String> stat, SearchAdapter.OnItemClickListener onItemClickListener) {

        this.context=context;
        listener=onItemClickListener;
        this.stat=stat;

    }

    @Override
    public SearchAdapter.ViewHolderInnner onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.sample, parent, false);
        return new SearchAdapter.ViewHolderInnner(v);

    }

    @Override
    public void onBindViewHolder(SearchAdapter.ViewHolderInnner holder, final int position) {

        holder.tvTexte.setText(stat.get(position));
        holder.tvTexte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(position, stat.get(position));

            }
        });
    }

    @Override
    public int getItemCount() {
        return stat.size();
    }

class ViewHolderInnner extends RecyclerView.ViewHolder {
    TextView tvTexte;
    public ViewHolderInnner(View v) {
        super(v);
        tvTexte=v.findViewById(R.id.tvTexte);
    }
}
    }

