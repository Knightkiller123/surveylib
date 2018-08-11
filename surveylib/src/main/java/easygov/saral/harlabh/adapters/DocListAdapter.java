package easygov.saral.harlabh.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import easygov.saral.harlabh.R;
import easygov.saral.harlabh.fragments.application_pager_fragments.AttachmentFragment;
import easygov.saral.harlabh.models.responsemodels.filefieldsupdated.DocTypeUpdated;
import easygov.saral.harlabh.models.responsemodels.filefieldsupdated.FieldsUpdated;

/**
 * Created by apoorv on 29/08/17.
 */

public class DocListAdapter extends RecyclerView.Adapter<DocListAdapter.Holder> {

    private Context context ;
    private List<DocTypeUpdated> list;
    private  AttachmentFragment frag;
    private AttachmentsAdapter.Holder previousHolder ;
    private int docPos=0;
    private FieldsUpdated fieldsUpdateds;
    public DocListAdapter(Activity activity, List<DocTypeUpdated> list, AttachmentFragment attachAdapInst, AttachmentsAdapter.Holder holder, int positionss, FieldsUpdated fieldsUpdated) {
    context=activity;
        this.list=list;
        frag=attachAdapInst;
        previousHolder=holder;
        docPos=positionss;
        fieldsUpdateds=fieldsUpdated;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater= LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.adapter_doclist,parent,false);
        Holder holder=new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, final int position) {
        holder.tvDocMainItem.setText(list.get(position).docs.get(0).name);
        holder.tvDocMainItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousHolder.tvDocSelectionSpin.setText(list.get(position).docs.get(0).name);
                previousHolder.tvDocSelectionSpin.setTextColor(ContextCompat.getColor(context,R.color.black));
                frag.setSubDocs(position,previousHolder);
                frag.initiatepopupafterDetails();
                /*try {
                    if(fieldsUpdateds.validate)
                        frag.setinnitialcheckarray(fieldsUpdateds.docTypes.get(position).docs,position);
                }
                catch (Exception e)
                {

                }*/
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private TextView tvDocMainItem;
        public Holder(View itemView) {
            super(itemView);
            tvDocMainItem= itemView.findViewById(R.id.tvDocMainItem);
        }
    }
}
