package easygov.saral.harlabh.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import easygov.saral.harlabh.activity.FourthApplicationActivity;
import easygov.saral.harlabh.fragments.AdditionalInfoFragment;
import easygov.saral.harlabh.models.responsemodels.documentsubtype.SubType;
import easygov.saral.harlabh.utils.GeneralFunctions;
import easygov.saral.harlabh.utils.RestClient;
import easygov.saral.harlabh.R;
import easygov.saral.harlabh.fragments.application_pager_fragments.AttachmentFragment;
import easygov.saral.harlabh.models.responsemodels.filefieldsupdated.FieldsUpdated;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by apoorv on 01/08/17.
 */

//not in use
public class AttachmentsAdapter extends RecyclerView.Adapter<AttachmentsAdapter.Holder> {
    private Context context;
    // private List<String> docs;
    private List<FieldsUpdated> list;
    public static AttachmentFragment fragmentinstance;
    Holder holders;
    private int post;
    private int pos;

    public AttachmentsAdapter(Context context, List<FieldsUpdated> docs, AttachmentFragment fragment)
    {
        this.context=context;
        this.list=docs;
        fragmentinstance=fragment;

    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.adapter_attachments,parent,false);
        Holder holder=new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final Holder holder, final int position) {
        holder.cvSpinner.setVisibility(View.VISIBLE);
        holder.rlAttachDoc.setVisibility(View.GONE);
        //holder.tvSpinHeaders.setText(list.get(position).display_name);
        holder.tvSpinHeaders.setText(list.get(position).display_name);
        //holder.rvgetPages.setLayoutManager(new GridLayoutManager(context,2));
        holder.rvgetPages.setLayoutManager(new LinearLayoutManager(context));
        holder.tvDocHeading.setText(context.getResources().getString(R.string.upload)+" "+list.get(position).display_name);

        try {
            if(list.get(position).validate)
                fragmentinstance.setinnitialcheckarray(list.get(position).docTypes.get(0).docs,position);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        holder.tvDocSelectionSpin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holders=holder;
                fragmentinstance.initiatePopupWindowNoSign(list.get(position).docTypes, holder,position,list.get(position));
                pos = position;
            }
        });

        /*holder.ivSingleCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.tvSingleDoc.setBackgroundResource(R.drawable.attachment_unselected);
                holder.ivSingleCross.setVisibility(View.GONE);
            }
        });*/

        holder.rlAttachDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(list.get(position).formfilled==1) {
                    fragmentinstance.innitiateFilePopUp(list.get(position).field__id.toString() + "_", list.get(position).field__system_name, "yes",
                            list.get(0).docTypes.get(0).docs.get(0).id, holder, null, list.get(0), position, 0,true);
                }
                else {
                    holders=holder;
                    gotoAdditional(position);
                }
            }
        });

        holder.tvSingleEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holders=holder;
                gotoAdditional(position);
            }
        });

        holder.tvSingleDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holders=holder;
                gotoAdditional(position);
            }
        });
    }

    private void gotoAdditional(int position)
    {
        ((FourthApplicationActivity)context).makeVisible();
        AdditionalInfoFragment frag=new AdditionalInfoFragment();
        Bundle bundle=new Bundle();
        bundle.putString("nameshead",list.get(position).docTypes.get(0).doc_number_label);
        bundle.putString("authority",new Gson().toJson(list.get(position).docTypes.get(0).authority_type));
        bundle.putInt("post",position);
        frag.setArguments(bundle);
        ((FourthApplicationActivity) context).getSupportFragmentManager().beginTransaction().add(R.id.rlSelfecContainer, frag).addToBackStack(null).commit();
    }

    public void updateUiUpdateMap(int positionOfForm, String docnum, String stateId, String distId, String authId)
    {
        holders.tvSingleEdit.setVisibility(View.VISIBLE);
        //holders.ivSingleSuccess.setVisibility(View.VISIBLE);
        //holders.tvExtraStatic.setVisibility(View.VISIBLE);
        list.get(positionOfForm).formfilled=1;
        //todo: copy line 126 accordingly for map
        String s=list.get(positionOfForm).field__id.toString()+"_"+ list.get(positionOfForm).field__system_name+"_";
        fragmentinstance.setmapForAdditionalInfo(s+"state_id",stateId,s+"district_id",distId,s+"doc_num",docnum,s+"authority_id",authId);

    }

    public void setAdditionalInfo()
    {
        ((FourthApplicationActivity)context).makeVisible();
        AdditionalInfoFragment frag=new AdditionalInfoFragment();
        Bundle bundle=new Bundle();
        bundle.putString("nameshead",list.get(pos).docTypes.get(0).doc_number_label);
        bundle.putString("authority",new Gson().toJson(list.get(pos).docTypes.get(0).authority_type));
        bundle.putInt("post",pos);
        frag.setArguments(bundle);
        ((FourthApplicationActivity) context).getSupportFragmentManager().beginTransaction().add(R.id.rlSelfecContainer, frag).addToBackStack(null).commit();
    }

    public void hitApiFromFragment(int position, Holder previousHolder)
    {
        //hitSubDocsApi(list.get(pos).id.toString(),previousHolder);
        previousHolder.rvgetPages.setVisibility(View.VISIBLE);
        /* if(list.get(pos).validate)
            fragmentinstance.setinnitialcheckarray(list.get(pos).docTypes.get(position).docs,pos);*/
        previousHolder.rvgetPages.setAdapter(new DocPagesAdapter(context,list.get(pos).docTypes.get(position).docs,fragmentinstance,list.get(pos),pos));


    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        public TextView tvDocHeading,tvSpinHeaders,tvDocSelectionSpin;
        private RelativeLayout rlAttachDoc;
        private RelativeLayout cvSpinner;
        private RecyclerView rvgetPages;
        public TextView tvSingleDoc,tvMultipleEdit,tvSingleEdit,tvExtraStatic;
        public ImageView ivSingleCross,ivSingleSuccess,ivMultipleSuccess;

        public Holder(View itemView) {
            super(itemView);
            tvDocHeading= itemView.findViewById(R.id.tvDocHeading);
            rlAttachDoc= itemView.findViewById(R.id.rlAttachDoc);
            cvSpinner= itemView.findViewById(R.id.cvSpinner);
            tvSpinHeaders= itemView.findViewById(R.id.tvSpinHeaders);
            tvDocSelectionSpin= itemView.findViewById(R.id.tvDocSelectionSpin);
            rvgetPages= itemView.findViewById(R.id.rvgetPages);
            tvSingleDoc= itemView.findViewById(R.id.tvSingleDoc);
            tvSingleEdit= itemView.findViewById(R.id.tvSingleEdit);

            /*
            tvMultipleEdit= (TextView) itemView.findViewById(R.id.tvMultipleEdit);
            tvExtraStatic= (TextView) itemView.findViewById(R.id.tvExtraStatic);
            ivSingleSuccess= (ImageView) itemView.findViewById(R.id.ivSingleSuccess);
            */






            // ivMultipleSuccess= (ImageView) itemView.findViewById(R.id.ivMultipleSuccess);
            ivSingleCross= itemView.findViewById(R.id.ivSingleCross);

        }
    }
}
