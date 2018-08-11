package easygov.saral.harlabh.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import easygov.saral.harlabh.R;
import easygov.saral.harlabh.activity.SplashActivity;
import easygov.saral.harlabh.adapters.ListViewDocumentAdapter;
import easygov.saral.harlabh.models.documents.DocumentDetail;
import easygov.saral.harlabh.models.documents.PojoDocument;
import easygov.saral.harlabh.utils.GeneralFunctions;
import easygov.saral.harlabh.utils.Prefs;
import easygov.saral.harlabh.utils.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by apoorv on 16/05/18.
 */

public class MyDocumentFragment extends Fragment implements ListViewDocumentAdapter.OnUpdateDeletedItem {

    private RecyclerView rvDocuments;
    private boolean isListView=true;
    private ImageView ivDocumentBack;
    private int position;
    private DocumentDetail data=new DocumentDetail();
    private RelativeLayout rlSurveyNoData;
    private List<DocumentDetail> adapterData=new ArrayList<>();


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvDocuments=view.findViewById(R.id.rvDocuments);
        ivDocumentBack=view.findViewById(R.id.ivDocumentBack);
        rlSurveyNoData=view.findViewById(R.id.rlSurveyNoData);

        rvDocuments.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvDocuments.setAdapter(new ListViewDocumentAdapter(getActivity(),adapterData,this
                ,getActivity().getIntent().hasExtra("canDelete")));
        ivDocumentBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        hitApiGetDocument();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.my_document_fragment,container,false);
    }


    private void hitApiGetDocument() {
        try
        {
            if(getActivity()!=null)
            {
                GeneralFunctions.showDialog(getActivity());
                RestClient.get().readDocsForUserByFieldID(getActivity().getIntent().getStringExtra("fieldId"),
                        getActivity().getIntent().getStringExtra("documentTypeId") ).enqueue(new Callback<PojoDocument>() {
                    @Override
                    public void onResponse(Call<PojoDocument> call, Response<PojoDocument> response) {

                        if(response.isSuccessful())
                        {
                            if(response.body().code.equals("401"))
                                GeneralFunctions.tokenExpireAction(getActivity());

                            else if(response.body().success==1)
                            {
                                if(response.body().data.user_service_docs.isEmpty())
                                {
                                    rlSurveyNoData.setVisibility(View.VISIBLE);
                                    rvDocuments.setVisibility(View.GONE);
                                }
                                else
                                {
                                    adapterData.addAll(response.body().data.user_service_docs);
                                    rvDocuments.getAdapter().notifyDataSetChanged();
                                    rlSurveyNoData.setVisibility(View.GONE);
                                    rvDocuments.setVisibility(View.VISIBLE);
                                }
                            }
                            else
                                Toast.makeText(getActivity(), response.body().message, Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(getActivity(), getResources().getString(R.string.serverIssue), Toast.LENGTH_SHORT).show();

                        GeneralFunctions.dismissDialog();
                    }

                    @Override
                    public void onFailure(Call<PojoDocument> call, Throwable t) {
                        GeneralFunctions.dismissDialog();
                        GeneralFunctions.makeSnackbar(rvDocuments,getResources().getString(R.string.netIssue));

                    }
                });
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public void deleteItem(int position) {
        adapterData.remove(position);
        rvDocuments.getAdapter().notifyItemRemoved(position);
        if(adapterData.isEmpty())
        {
            rlSurveyNoData.setVisibility(View.VISIBLE);
            rvDocuments.setVisibility(View.GONE);
        }
        else
            {
            rlSurveyNoData.setVisibility(View.GONE);
            rvDocuments.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void readFile(int position, DocumentDetail document) {
        this.position=position;
        data=document;
        getImage();
    }


    public void getImage()
    {
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED
                &&ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED &&ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
        {
            hitApiViewDocument();        }

        else {
            request();
        }
    }

    private void request() {
        requestPermissions(
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                100);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode) {
            case 100: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        ) {
                    boolean readExternal = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(readExternal)
                        hitApiViewDocument();

                    else {
                        if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE))
                        {
                            //Toast.makeText(inst, "last tym", Toast.LENGTH_SHORT).show();
                            // request();

                            permissionNeeded(getResources().getString(R.string.storagePermissionNeeded));
                        }
                        else {
                            GeneralFunctions.PleaseGrantPermission(rvDocuments,getActivity());
                        }
                    }

                } /*else {
                    GeneralFunctions.makeSnackbar(rvDocuments, getResources().getString(R.string.plPermission));
                    GeneralFunctions.PleaseGrantPermission(rvDocuments, getActivity());

                }*/
                return;
            }

        }
    }

    private void permissionNeeded(String msg) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(msg)
                .setPositiveButton(this.getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                dialogInterface.dismiss();
                                request();
                            }
                        }).show();
    }


    private void hitApiViewDocument() {
        GeneralFunctions.showDialog(getActivity());

        RestClient.get().getFiles("base64",adapterData.get(position).user_service_id,
                adapterData.get(position).field_id,adapterData.get(position).document_type_id).enqueue(new Callback<PojoDocument>() {
            @Override
            public void onResponse(Call<PojoDocument> call, Response<PojoDocument> response) {


                if (response.isSuccessful()) {
                    if(response.body().code.equals("401"))
                    {
                        GeneralFunctions.tokenExpireAction(getActivity());
                    }
                    else if (response.body().success == 1) {
                        Bundle bundle = new Bundle();
                        bundle.putString("pdfUrl", response.body().data.file);
                        bundle.putString("fileExtension", response.body().data.file_extension);
                        PdfFragment fragment = new PdfFragment();
                        fragment.setArguments(bundle);
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .add(R.id.rlContainer, fragment)
                                .addToBackStack(null)
                                .commit();

                    } else {
                        Toast.makeText(getActivity(), response.body().message, Toast.LENGTH_SHORT).show();
                    }

                }
                else

                {
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.serverIssue), Toast.LENGTH_SHORT).show();
                }
                GeneralFunctions.dismissDialog();
            }

            @Override
            public void onFailure(Call<PojoDocument> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(rvDocuments,getActivity().getResources().getString(R.string.netIssue));

            }
        });

    }


}
