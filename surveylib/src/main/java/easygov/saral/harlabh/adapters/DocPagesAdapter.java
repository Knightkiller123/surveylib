package easygov.saral.harlabh.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import easygov.saral.harlabh.models.responsemodels.filefieldsupdated.DocUpdated;
import easygov.saral.harlabh.R;
import easygov.saral.harlabh.fragments.application_pager_fragments.AttachmentFragment;
import easygov.saral.harlabh.models.responsemodels.filefieldsupdated.FieldsUpdated;

/**
 * Created by apoorv on 29/08/17.
 */

public class DocPagesAdapter extends RecyclerView.Adapter<DocPagesAdapter.Holder> {
    private Context context;
    private List<DocUpdated> list;
    private AttachmentFragment frag;
    private FieldsUpdated fileDet;
    private int posForCheck;
    public DocPagesAdapter(Context context, List<DocUpdated> docTypes, AttachmentFragment fragment, FieldsUpdated fieldsUpdated, int position) {
        this.context=context;
        list=docTypes;
        frag=fragment;
        fileDet=fieldsUpdated;
        posForCheck=position;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater= LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.adapter_docpages,parent,false);
        Holder holder=new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final Holder holder, final int position) {
      //  holder.tvDocFurther.setText(list.get(position).name);
        holder.tvDocHeading.setText(list.get(position).name);

       /* try {
            if(list.get(position).isSelected)
            {
                holder.cvDocFurtherSelect.setBackgroundResource(R.drawable.fileselected);
            }
        }
        catch (Exception e)
        {}
*/
        holder.cvDocFurtherSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position==0)
                frag.innitiateFilePopUp(fileDet.field__id+"_", fileDet.field__system_name,"yes",list.get(0).id,null,holder,fileDet,posForCheck,position,true);

                else frag.innitiateFilePopUp(list.get(0).id+"_"+list.get(position).id+"_",String.valueOf(fileDet.field__id),"no",list.get(position).id,null,holder,fileDet,posForCheck,position,false);

                //todo: uncomment below and remove above agar code fatta hai toh
              //  else frag.innitiateFilePopUp(list.get(0).id+"_"+list.get(position).id+"_",list.get(position).name,"no",list.get(position).id,null,holder,fileDet,posForCheck,position);

            }
        });

        /*holder.ivCrossDocs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.cvDocFurtherSelect.setBackgroundResource(R.drawable.attachment_unselected);
                holder.ivCrossDocs.setVisibility(View.GONE);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        public TextView tvDocFurther,tvDocHeading;
        public RelativeLayout cvDocFurtherSelect;
        public ImageView ivCrossDocs;
        public Holder(View itemView) {
            super(itemView);
            cvDocFurtherSelect= itemView.findViewById(R.id.cvDocFurtherSelect);
            tvDocFurther= itemView.findViewById(R.id.tvDocFurther);
            tvDocHeading= itemView.findViewById(R.id.tvDocHeading);
            ivCrossDocs= itemView.findViewById(R.id.ivCrossDocs);
        }
    }
}
