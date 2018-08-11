package easygov.saral.harlabh.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import easygov.saral.harlabh.R;
import easygov.saral.harlabh.activity.SplashActivity;
import easygov.saral.harlabh.fragments.PdfFragment;
import easygov.saral.harlabh.models.documents.DocumentDetail;
import easygov.saral.harlabh.models.documents.PojoDocument;
import easygov.saral.harlabh.models.responsemodels.generalmodel.GeneralModel;
import easygov.saral.harlabh.utils.GeneralFunctions;
import easygov.saral.harlabh.utils.Prefs;
import easygov.saral.harlabh.utils.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by apoorv on 16/05/18.
 */

public class ListViewDocumentAdapter extends RecyclerView.Adapter<ListViewDocumentAdapter.ViewHolder> {
    private List<DocumentDetail> adapterData=new ArrayList<>();
    private Context context;
    private OnUpdateDeletedItem onUpdateDeletedItem;
    private boolean canDelete;


    public ListViewDocumentAdapter(Context context, List<DocumentDetail> adapterData,OnUpdateDeletedItem onUpdateDeletedItem
            ,boolean canDelete) {
        this.adapterData=adapterData;
        this.context=context;
        this.onUpdateDeletedItem=onUpdateDeletedItem;
        this.canDelete=canDelete;
    }



    public interface OnUpdateDeletedItem
    {
        void deleteItem(int position);
        void readFile(int position,DocumentDetail document);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.content_list_view_document,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (adapterData.get(position).field__display_name!=null && !adapterData.get(position).field__display_name.isEmpty()&&
                !adapterData.get(position).field__display_name.equals("null"))
            holder.tvDocName.setText(adapterData.get(position).field__display_name+"-"+adapterData.get(position).document_type__name);
        else
            holder.tvDocName.setText(adapterData.get(position).document_type__name);
        if(canDelete)
            if(adapterData.get(position).is_removable.matches("1"))
                holder.ivDelete.setImageResource(R.drawable.bin_selected);
            else
                holder.ivDelete.setImageResource(R.drawable.info);
        else
            holder.ivDelete.setImageResource(R.drawable.select_active_01);

        String str=adapterData.get(position).file_name.substring(
                adapterData.get(position).file_name.lastIndexOf("."),
                adapterData.get(position).file_name.length());

        holder.tvDocImage.setText(str);

        switch (str)
        {
            case ".pdf":
                holder.tvDocImage.setBackgroundResource(R.drawable.background_pdf);
                break;
            case ".png":
                holder.tvDocImage.setBackgroundResource(R.drawable.background_png);
                break;
            case ".jpg":
                holder.tvDocImage.setBackgroundResource(R.drawable.background_jpg);
                break;
            default:
                holder.tvDocImage.setBackgroundResource(R.drawable.background_pdf);
        }


        holder.tvDocSize.setText(getFormatedDate(adapterData.get(position).created_on));
    }

    private String getFormatedDate(String created_on) {
        SimpleDateFormat spf=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date newDate= null;
        try {
            newDate = spf.parse(created_on);
            spf= new SimpleDateFormat("dd-MM-yyyy");
            return spf.format(newDate);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public int getItemCount() {
        return adapterData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDocName,tvDocImage,tvDocSize;
        ImageView ivDelete,ivView;
        public ViewHolder(View itemView) {
            super(itemView);
            tvDocSize=itemView.findViewById(R.id.tvDocSize);
            tvDocName=itemView.findViewById(R.id.tvDocName);
            tvDocImage=itemView.findViewById(R.id.tvDocImage);
            ivDelete=itemView.findViewById(R.id.ivDelete);
            ivView=itemView.findViewById(R.id.ivView);
            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(canDelete)
                    {
                        if(adapterData.get(getAdapterPosition()).is_removable.matches("1"))
                            showDialog(getAdapterPosition());
                        else
                            showInfo();
                        // GeneralFunctions.makeSnackbar(view,context.getString(R.string.sorry_you_cant_delete_this_document));

                    }
                    else
                    {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("field_id",adapterData.get(getAdapterPosition()).field_id);
                        returnIntent.putExtra("id",adapterData.get(getAdapterPosition()).id);
                        returnIntent.putExtra("user_service_id",adapterData.get(getAdapterPosition()).user_service_id);
                        returnIntent.putExtra("document_type_id",adapterData.get(getAdapterPosition()).document_type_id);
                        ((AppCompatActivity)context).setResult(Activity.RESULT_OK,returnIntent);
                        ((AppCompatActivity)context).finish();

                    }
                }
            });
            ivView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //     onUpdateDeletedItem.readFile(getAdapterPosition(),adapterData.get(getAdapterPosition()));
                    hitApiGetDocument(view,getAdapterPosition());
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//                    onUpdateDeletedItem.readFile(getAdapterPosition(),adapterData.get(getAdapterPosition()));
                    hitApiGetDocument(view,getAdapterPosition());
                }
            });

        }
    }

    private void showInfo() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.sorry_you_cant_delete_this_document))
                .setPositiveButton(context.getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        }).show();


    }


    private void hitApiGetDocument(final View view, int position) {
        GeneralFunctions.showDialog(context);

        RestClient.get().getFiles("base64",adapterData.get(position).user_service_id,
                adapterData.get(position).field_id,adapterData.get(position).document_type_id).enqueue(new Callback<PojoDocument>() {
            @Override
            public void onResponse(Call<PojoDocument> call, Response<PojoDocument> response) {


                if (response.isSuccessful()) {
                    if(response.body().code.equals("401"))
                    {
                        GeneralFunctions.tokenExpireAction(context);
                    }

                    else if (response.body().success == 1) {
                        Bundle bundle = new Bundle();
                        bundle.putString("pdfUrl", response.body().data.file);
                        bundle.putString("fileExtension", response.body().data.file_extension);

                        //Todo: imprt compile 'com.github.barteksc:android-pdf-viewer:2.5.1'
                        PdfFragment fragment = new PdfFragment();
                        fragment.setArguments(bundle);
                        ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction()
                                .add(R.id.rlContainer, fragment)
                                .addToBackStack(null)
                                .commitAllowingStateLoss();

                    } else {
                        Toast.makeText(context, response.body().message, Toast.LENGTH_SHORT).show();
                    }

                }
                else

                {
                    Toast.makeText(context, context.getResources().getString(R.string.serverIssue), Toast.LENGTH_SHORT).show();
                }
                GeneralFunctions.dismissDialog();
            }

            @Override
            public void onFailure(Call<PojoDocument> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(view,context.getResources().getString(R.string.netIssue));

            }
        });

    }



    private void hitApiDeleteDocument(final View view, final int position) {
        GeneralFunctions.showDialog(context);

        RestClient.get().deleteFiles(adapterData.get(position).user_service_id,
                adapterData.get(position).field_id,adapterData.get(position).document_type_id).enqueue(new Callback<GeneralModel>() {
            @Override
            public void onResponse(Call<GeneralModel> call, Response<GeneralModel> response) {


                if (response.isSuccessful()) {
                    if(response.body().code.equals("401"))
                    {
                        GeneralFunctions.tokenExpireAction(context);
                    }
                    else if (response.body().success == 1) {
                        onUpdateDeletedItem.deleteItem(position);
                    }
                    else {
                        Toast.makeText(context, response.body().message, Toast.LENGTH_SHORT).show();
                    }

                }
                else

                {
                    Toast.makeText(context, context.getResources().getString(R.string.serverIssue), Toast.LENGTH_SHORT).show();
                }
                GeneralFunctions.dismissDialog();
            }

            @Override
            public void onFailure(Call<GeneralModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(view,context.getResources().getString(R.string.netIssue));

            }
        });

    }

    public void showDialog( final int position){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_delete_document);

        dialog.findViewById(R.id.tvDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hitApiDeleteDocument(view,position);
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.tvCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

}

