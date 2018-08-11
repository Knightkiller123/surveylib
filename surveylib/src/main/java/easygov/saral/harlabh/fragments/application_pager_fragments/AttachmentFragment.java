package easygov.saral.harlabh.fragments.application_pager_fragments;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import easygov.saral.harlabh.activity.FifthPaymentActivity;
import easygov.saral.harlabh.activity.FourthApplicationActivity;
import easygov.saral.harlabh.activity.MyDocumentActivity;
import easygov.saral.harlabh.activity.SplashActivity;
import easygov.saral.harlabh.adapters.AttachmentsAdapter;
import easygov.saral.harlabh.adapters.DocListAdapter;
import easygov.saral.harlabh.adapters.DocPagesAdapter;
import easygov.saral.harlabh.models.AdditionalInfoModel;
import easygov.saral.harlabh.models.responsemodels.filefieldsupdated.DocTypeUpdated;
import easygov.saral.harlabh.models.responsemodels.filefieldsupdated.DocUpdated;
import easygov.saral.harlabh.models.responsemodels.filefieldsupdated.FieldsUpdated;
import easygov.saral.harlabh.models.responsemodels.filefieldsupdated.FileFieldsUpdate;
import easygov.saral.harlabh.models.responsemodels.mandatoryfields.MandatoryDetails;
import easygov.saral.harlabh.models.responsemodels.mandatoryfields.MandatoryFields;
import easygov.saral.harlabh.models.responsemodels.savemandatoryfields.SaveMandatoryFields;
import easygov.saral.harlabh.models.responsemodels.usidmodel.UsidModel;
import easygov.saral.harlabh.utils.Constants;
import easygov.saral.harlabh.utils.GeneralFunctions;
import easygov.saral.harlabh.utils.MyApplication;
import easygov.saral.harlabh.utils.Prefs;
import easygov.saral.harlabh.utils.ProgressRequestBody;
import easygov.saral.harlabh.utils.RestClient;
import easygov.saral.harlabh.utils.RetrofitUtils;
import easygov.saral.harlabh.R;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static easygov.saral.harlabh.activity.FourthApplicationActivity.detailList;

public class AttachmentFragment extends Fragment implements ProgressRequestBody.UploadCallbacks {

    private RecyclerView rvDocuments;
    private List<FieldsUpdated> fileDetailList;
    private View views;
    private RelativeLayout rlAttachment;
    private static AttachmentFragment fragInst;
    private AttachmentsAdapter attachAdapInst;
    private PopupWindow pw;
    private String fieldID="",documentTypeId="";
    private boolean isFirstAttachment;
    private Prefs mPrefs;
    private Uri fileUri;
    private int fileRead=0;
    private Dialog dialogUploadProgress;
    private Map<String, MultipartBody.Part> mapDocFiles=new HashMap<>();
    private int fileWrite=0;
    public static List<AdditionalInfoModel> addModel;
    private DocPagesAdapter.Holder docHolder;
    private AttachmentsAdapter.Holder attachHolder;
    private TextView tvFragNext,tvPercent,tvUploadingAttach;
    private FieldsUpdated forFinal;
    private int positionForFinal;
    private int subDocCheck;
    private List<MultipartBody.Part> docFiles =new ArrayList<>();
    private Map<String, RequestBody> map=new HashMap<>();
    private int[][] innitial ;
    private DonutProgress pbProgress;
    private ImageView ivImageCenter;
    private int[][] finalist;
    private String[][] docMessage;
    private String group_id="";


    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int FILE_SELECT_CODE =0 ;

    private static final int Camera_Group_Permissions=123;
    private static final int Document_Group_Permissions=456;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_attachment, container, false);
        views=view;
        init(view);
        return view;

    }

    private void init(View view) {
        rvDocuments= view.findViewById(R.id.rvDocuments);
        rvDocuments.setLayoutManager(new LinearLayoutManager(getContext()));
        rlAttachment= view.findViewById(R.id.rlAttachment);
        mPrefs=Prefs.with(getActivity());
        tvFragNext= view.findViewById(R.id.tvFragNext);

        tvFragNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (checkDocs()) {
                    saveDocsForUser();
                    showDialogUploadProgress();
                }

            }
        });


    }

    private boolean checkDocs() {

        for(int i=0;i<fileDetailList.size();i++)
        {
            if(finalist[i][0]==0&&finalist[i][1]==0&&finalist[i][2]==0&&finalist[i][3]==0)
            {
                if(innitial[i][0]!=0)
                {
                    GeneralFunctions.makeSnackbar(rlAttachment,getResources().getString(R.string.plselectall) + " " + fileDetailList.get(i).display_name);
                    return false;
                }
            }
            for(int j=0;j<4;j++)
            {
                if(innitial[i][j]!=finalist[i][j]) {

                    if(docMessage[i][j].length()>1) {
                        GeneralFunctions.makeSnackbar(rlAttachment, getResources().getString(R.string.plselect) + " " + docMessage[i][j]);
                        return false;
                    }
                }

            }
        }
        return true;
    }

    public static Fragment newInstance(String s) {
        AttachmentFragment frag=new AttachmentFragment();
        fragInst=frag;
        return frag;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        readFileFields();

    }

    private void readFileFields() {
        GeneralFunctions.showDialog(getActivity());
        Call<FileFieldsUpdate> call= RestClient.get().readFileFields(mPrefs.getString(Constants.ServiceId,""),
                mPrefs.getString(Constants.SurveyId,""),mPrefs.getString(Constants.UserServiceID,""),mPrefs.getString(Constants.GeographyId,""));
        call.enqueue(new Callback<FileFieldsUpdate>() {
            @Override
            public void onResponse(Call<FileFieldsUpdate> call, Response<FileFieldsUpdate> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful())
                {
                    mPrefs.save(Constants.UserServiceID, response.body().data.userServiceId);
                    if(response.body().code==401)
                    {
                        GeneralFunctions.tokenExpireAction(getActivity());
                    }

                    else if(response.body().success==1)
                    {
                        //dcosObj=response.body();

                        if(response.body().data.fields.size()>0) {
                            detailList=response.body().data.fields.size();
                            fileDetailList = response.body().data.fields;
                            //addModel=new AdditionalInfoModel[0];
                            AdditionalInfoModel modObj=new AdditionalInfoModel();
                            addModel=new ArrayList<AdditionalInfoModel>();
                            addModel.add(modObj);addModel.add(modObj);addModel.add(modObj);addModel.add(modObj);addModel.add(modObj);
                            addModel.add(modObj);addModel.add(modObj);addModel.add(modObj);addModel.add(modObj);addModel.add(modObj);
                            addModel.add(modObj);addModel.add(modObj);addModel.add(modObj);addModel.add(modObj);addModel.add(modObj);
                            addModel.add(modObj);addModel.add(modObj);addModel.add(modObj);addModel.add(modObj);addModel.add(modObj);
                            addModel.add(modObj);addModel.add(modObj);addModel.add(modObj);addModel.add(modObj);addModel.add(modObj);
                            addModel.add(modObj);addModel.add(modObj);addModel.add(modObj);addModel.add(modObj);addModel.add(modObj);
                            rvDocuments.setItemViewCacheSize(fileDetailList.size());
                            attachAdapInst = new AttachmentsAdapter(getActivity(), fileDetailList, fragInst);
                            rvDocuments.setAdapter(attachAdapInst);


                            //todo: do in async
                            innitial=new int[response.body().data.fields.size()][4];
                            finalist=new int[response.body().data.fields.size()][4];
                            docMessage=new String[response.body().data.fields.size()][4];
                            for (int i=0;i<response.body().data.fields.size();i++)
                            {   finalist[i][0]=0;
                                finalist[i][1]=0;
                                finalist[i][2]=0;
                                finalist[i][3]=0;

                                docMessage[i][0]="";
                                docMessage[i][1]="";
                                docMessage[i][2]="";
                                docMessage[i][3]="";
                                if(response.body().data.fields.get(i).validate==true) {
                                    innitial[i][0] = 1;
                                    innitial[i][1] = 1;
                                    innitial[i][2] = 1;
                                    innitial[i][3] = 1;
                                }

                                else {
                                    innitial[i][0] = 0;
                                    innitial[i][1] = 0;
                                    innitial[i][2] = 0;
                                    innitial[i][3] = 0;
                                }

                            }
                        }
                        else {
                            hitReadFieldsforUser();
                            detailList=0;
                        }
                        // checkFiles=new  int[response.body().data.fields.size()];


                    }
                    else  GeneralFunctions.makeSnackbar(rlAttachment,response.body().message);


                }
                else
                {
                    GeneralFunctions.makeSnackbar(rlAttachment,getResources().getString(R.string.serverIssue));

                    // GeneralModel error = ErrorUtils.parseError(response);
                    // Log.d("Sigin Error",error.data.message);
                }
            }

            @Override
            public void onFailure(Call<FileFieldsUpdate> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(rlAttachment,getResources().getString(R.string.netIssue));

            }
        });
    }


    RecyclerView rlDocList;
    List<DocTypeUpdated> listforDocTypes;
    AttachmentsAdapter.Holder holderPop;



    public void initiatepopupafterDetails() {
        attachAdapInst.setAdditionalInfo();

    }

    public void initiatePopupWindowNoSign(List<DocTypeUpdated> list, AttachmentsAdapter.Holder holder,
                                          int positionss, FieldsUpdated fieldsUpdated) {

        try {
            LayoutInflater inflater = (LayoutInflater) getActivity()
                    .getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.selectdoc_dialogue,
                    (ViewGroup)views.findViewById(R.id.rlDocSelect));
            pw = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
            pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
            pw.setOutsideTouchable(true);
            holderPop=holder;
            listforDocTypes=list;
            rlDocList = layout.findViewById(R.id.rlDocList);
            RelativeLayout rlDocSelectorClose= layout.findViewById(R.id.rlDocSelectorClose);
            rlDocList.setLayoutManager(new LinearLayoutManager(getActivity()));

            rlDocList.setAdapter(new DocListAdapter(getActivity(),listforDocTypes,fragInst,holderPop,positionss,fieldsUpdated));
            rlDocSelectorClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pw.dismiss();
                }
            });
            pw.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    pw.dismiss();

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String keyFirstElement,keySecondElement,whtToMake;
    int documentId;




    public void setmapForAdditionalInfo(String key1,String val1,String key2,String val2,String key3,String val3,String key4,String val4)
    {
        try {
            map.put(key1,RetrofitUtils.StringtoRequestBody(val1));
            map.put(key2,RetrofitUtils.StringtoRequestBody(val2));
            map.put(key3,RetrofitUtils.StringtoRequestBody(val3));
            map.put(key4,RetrofitUtils.StringtoRequestBody(val4));
        }
        catch (Exception e)
        {}


    }

    public void innitiateFilePopUp(String key, String name, String isVal, int docId,
                                   AttachmentsAdapter.Holder s, DocPagesAdapter.Holder holder,
                                   FieldsUpdated fileDet
            ,int posForCheck,int subDocPos, boolean isFirstAttachment) {
        // final PopupWindow pw;
        //  getActivity().getSupportFragmentManager().beginTransaction().add(R.id.rlAttachment,new DocPopUpFragment()).addToBackStack(null).commit();

        try {
            //We need to get the instance of the LayoutInflater, use the context of this activity
            LayoutInflater inflater = (LayoutInflater) getActivity()
                    .getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
            //Inflate the view from a predefined XML layout
            View layout = inflater.inflate(R.layout.selectfile_dialog,
                    (ViewGroup)views.findViewById(R.id.rlFileSelect));
            // create a 300px width and 470px height PopupWindow
            positionForFinal=posForCheck;
            subDocCheck=subDocPos;
            this.isFirstAttachment=isFirstAttachment;
            forFinal=fileDet;
            fieldID=fileDet.field__id.toString();
            documentTypeId=fileDet.docTypes.get(0).docs.get(0).id.toString();
            pw = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
            // display the popup in the center
            pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
            //pw.setOutsideTouchable(true);

            keyFirstElement=key;
            keySecondElement=name;
            whtToMake=isVal;
            documentId=docId;
            if(holder!=null) {
                docHolder = holder;
                attachHolder = null;
            }
            if (s!=null) {
                attachHolder = s;
                docHolder=null;
            }
            if(isVal.equals("yes"))
            {
                map.put(key+name+"_docType", RetrofitUtils.StringtoRequestBody(String.valueOf(documentId)));
            }




//position=pos;
            //List<DocUpdated> list1 new ArrayList<>();
            // for(int i =0;i<li)
            TextView tvDocSelection= layout.findViewById(R.id.tvDocSelection);

            RelativeLayout rlDocClose= layout.findViewById(R.id.rlDocClose);
            rlDocClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pw.dismiss();
                }
            });
            TextView tvSaralLibrary= layout.findViewById(R.id.tvSaralLibrary);
            TextView tvCamera= layout.findViewById(R.id.tvCamera);


            tvSaralLibrary.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                        startActivityForResult(new Intent(getActivity(), MyDocumentActivity.class)
                                .putExtra("fieldId",fieldID)
                                .putExtra("documentTypeId",documentTypeId),1100);}
                    else {
                        requestPermissions(
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                120);
                    }
                    pw.dismiss();

                }
            });

            tvCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.CAMERA)==
                            PackageManager.PERMISSION_GRANTED  &&
                            ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)
                                    == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    == PackageManager.PERMISSION_GRANTED)
                    {
                        captureImage();
                    }

                    else {

                        requestPermissions(
                                new String[]{Manifest.permission.CAMERA,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        Manifest.permission.READ_EXTERNAL_STORAGE},
                                Camera_Group_Permissions);

                        /*if(!(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED)) {
                            requestPermissions(
                                    new String[]{Manifest.permission.CAMERA},
                                    CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
                        }

                        else if(!(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)) {
                            requestPermissions(
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    FILE_SELECT_CODE);
                        }
                        else if(!(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED))
                        {
                            requestPermissions(
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    FILE_SELECT_Write_CODE);
                        }*/


                    }
                    pw.dismiss();
                }
            });

            tvDocSelection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)
                                    == PackageManager.PERMISSION_GRANTED) {
                        openFile();
                    }
                    else {
                        requestPermissions(
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        Manifest.permission.READ_EXTERNAL_STORAGE},Document_Group_Permissions
                        );

                        /*if(fileRead==0)
                        {
                        requestPermissions(
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                FILE_SELECT_CODE);}
                        else if(fileWrite==0)
                        {
                            requestPermissions(
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    FILE_SELECT_Write_CODE);
                        }*/
                    }
                    pw.dismiss();
                }
            });



            pw.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    pw.dismiss();

                }
            });



        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            /*case CAMERA_CAPTURE_IMAGE_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED &&ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
                  // captureImage();

                } else {
                    GeneralFunctions.PleaseGrantPermission(rlAttachment,getActivity());
                    GeneralFunctions.makeSnackbar(rlAttachment,getResources().getString(R.string.plPermission));

                }
                return;
            }*/


            case Camera_Group_Permissions:
                if(grantResults.length>0)
                {
                    boolean camera = grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    boolean filewrite =grantResults[1]==PackageManager.PERMISSION_GRANTED;
                    boolean fileread=grantResults[2]==PackageManager.PERMISSION_GRANTED;


                    if(camera&&fileread&&filewrite)
                        captureImage();

                    if(!camera) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA))
                            permissionNeeded("Camera Permission Needed to send documents using camera",0);

                        else GeneralFunctions.PleaseGrantPermission(rlAttachment, getActivity());

                    }

                    else if(!filewrite) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE))
                            permissionNeeded(getResources().getString(R.string.storagePermissionNeeded),0);

                        else GeneralFunctions.PleaseGrantPermission(rlAttachment, getActivity());
                    }

                    else if(!fileread) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE))
                            permissionNeeded(getResources().getString(R.string.storagePermissionNeeded),0);

                        else  GeneralFunctions.PleaseGrantPermission(rlAttachment, getActivity());
                    }


                }
                break;



            case Document_Group_Permissions:
                if(grantResults.length>0)
                {
                    boolean filewrite =grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    boolean fileread =grantResults[1]==PackageManager.PERMISSION_GRANTED;

                    if(filewrite&&fileread)
                    {
                        openFile();
                    }

                    if(!filewrite) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE))
                            permissionNeeded(getResources().getString(R.string.storagePermissionNeeded),1);

                        else GeneralFunctions.PleaseGrantPermission(rlAttachment, getActivity());
                    }

                    else if(!fileread) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE))
                            permissionNeeded(getResources().getString(R.string.storagePermissionNeeded),1);

                        else  GeneralFunctions.PleaseGrantPermission(rlAttachment, getActivity());
                    }


                }
                break;

            case 120:

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(new Intent(getActivity(), MyDocumentActivity.class)
                                    .putExtra("fieldId",fieldID)
                                    .putExtra("documentTypeId",documentTypeId)
                            , 1100);
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE))
                    {
                        permissionNeeded(getResources().getString(R.string.storagePermissionNeeded),2);

                    }

                    else {
                        GeneralFunctions.PleaseGrantPermission(rlAttachment, getActivity());
                    }

                    /*if (grantResults[0] == PackageManager.PERMISSION_DENIED)
                        GeneralFunctions.makeSnackbar(rlAttachment, getResources().getString(R.string.plPermission));
                    GeneralFunctions.PleaseGrantPermission(rlAttachment, getActivity());*/


                }

        }


    }




    private void permissionNeeded(String msg, final int type) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(msg)
                .setPositiveButton(this.getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                dialogInterface.dismiss();
                                if(type==0)
                                    requestCameraGroup();

                                else if(type==1)
                                    requestFileGroup();

                                else {
                                    requestPermissions(
                                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                            120);
                                }
                            }
                        }).show();


    }

    private void requestFileGroup() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE},Camera_Group_Permissions);
        }
    }

    private void requestCameraGroup()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE},Document_Group_Permissions);
        }
    }

    private int subDocPosition = -1;
    AttachmentsAdapter.Holder dummyHolder;
    public void setSubDocs(int subPos, AttachmentsAdapter.Holder previousHolder)
    {
        subDocPosition=subPos;
        dummyHolder=previousHolder;
        pw.dismiss();
        // attachAdapInst.hitApiFromFragment(subPos,previousHolder);
    }

    private int positionOfform;
    public void updateUI(int positionOfForm, String docnum, String stateId, String distId, String authId)
    {
        positionOfform=positionOfForm;
        attachAdapInst.updateUiUpdateMap(positionOfForm,docnum,stateId,distId,authId);
        attachAdapInst.hitApiFromFragment(subDocPosition,dummyHolder);

        //  initiatepopupafterDetails();
    }

    public void setinnitialcheckarray(List<DocUpdated> docs, int position)
    {

        switch (docs.size())
        {
            case 1:
                innitial[position][0]=1;
                innitial[position][1]=0;
                innitial[position][2]=0;
                innitial[position][3]=0;

                finalist[position][0]=0;
                finalist[position][1]=0;
                finalist[position][2]=0;
                finalist[position][3]=0;

                docMessage[position][0]=docs.get(0).name;

                break;

            case 2:
                innitial[position][0]=1;
                innitial[position][1]=1;
                innitial[position][2]=0;
                innitial[position][3]=0;

                finalist[position][0]=0;
                finalist[position][1]=0;
                finalist[position][2]=0;
                finalist[position][3]=0;
                docMessage[position][0]=docs.get(0).name;
                docMessage[position][1]=docs.get(1).name;
                break;

            case 3:
                innitial[position][0]=1;
                innitial[position][1]=1;
                innitial[position][2]=1;
                innitial[position][3]=0;

                finalist[position][0]=0;
                finalist[position][1]=0;
                finalist[position][2]=0;
                finalist[position][3]=0;
                docMessage[position][0]=docs.get(0).name;
                docMessage[position][1]=docs.get(1).name;
                docMessage[position][2]=docs.get(2).name;
                break;

            case 4:
                innitial[position][0]=1;
                innitial[position][1]=1;
                innitial[position][2]=1;
                innitial[position][3]=1;

                finalist[position][0]=0;
                finalist[position][1]=0;
                finalist[position][2]=0;
                finalist[position][3]=0;
                docMessage[position][0]=docs.get(0).name;
                docMessage[position][1]=docs.get(1).name;
                docMessage[position][2]=docs.get(2).name;
                docMessage[position][3]=docs.get(3).name;
                break;

        }
    }

    private void setfinalCheckArray()
    {

        finalist[positionForFinal][subDocCheck]=1;
        /*switch (forFinal.docTypes.get(positionForFinal).docs.size())
        {
            case 1:
                finalist[positionForFinal][0]=1;
                finalist[positionForFinal][1]=0;
                finalist[positionForFinal][2]=0;
                finalist[positionForFinal][3]=0;
                break;

            case 2:
                finalist[positionForFinal][0]=1;
                finalist[positionForFinal][1]=1;
                finalist[positionForFinal][2]=0;
                finalist[positionForFinal][3]=0;
                break;

            case 3:
                finalist[positionForFinal][0]=1;
                finalist[positionForFinal][1]=1;
                finalist[positionForFinal][2]=1;
                finalist[positionForFinal][3]=0;
                break;

            case 4:
                innitial[position][0]=1;
                innitial[position][1]=1;
                innitial[position][2]=1;
                innitial[position][3]=1;
                break;

        }*/
    }

    public  void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static final String IMAGE_DIRECTORY_NAME = "Harlabh_Docs";
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");return null;}}
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else {return null;}return mediaFile;}

    private String displayName;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case FILE_SELECT_CODE:
                if (resultCode == Activity.RESULT_OK && data!= null ) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    String uriString = uri.toString();
                    File myFile = new File(uriString);
                    String path = myFile.getAbsolutePath();
                    setfinalCheckArray();

                    if (uriString.startsWith("content://")) {
                        Cursor cursor = null;
                        try {
                            cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                            }
                        } finally {
                            cursor.close();
                        }
                    } else if (uriString.startsWith("file://")) {
                        displayName = myFile.getName();
                    }

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;

                   /* final Bitmap bitmap = BitmapFactory.decodeFile(path,
                            options);*/

                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                    } catch (IOException e) {

                        e.printStackTrace();
                    }

                  /*  ByteArrayOutputStream out = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 1, out);
                    Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));*/

                    File f=null;
                    if(displayName.contains("pdf")) {
                        try {
                            f = GeneralFunctions.savePdf(uri, getActivity());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        // f = myFile;
                        RequestBody requestFile;

                        if (f != null) {
                          /*  try {
                                requestFile =RequestBody.create(MediaType.parse(getActivity().getContentResolver().getType(uri)),f);
                            } catch (Exception e) {
                                requestFile =RequestBody.create(MediaType.parse("multipart/form-data"), myFile);
                            }
                            map.put(keyFirstElement + keySecondElement, RetrofitUtils.PDFtoRequestBody(f));
                            MultipartBody.Part body =
                                    MultipartBody.Part.createFormData(keyFirstElement + keySecondElement, displayName, requestFile);
                            docFiles.add(body);*/

                            ProgressRequestBody fileBody;
                            try {
                                fileBody = new ProgressRequestBody(f, this,getActivity().getContentResolver().getType(uri));
                            }
                            catch (Exception e)
                            {
                                fileBody = new ProgressRequestBody(f, this,"multipart/form-data");

                            }
                            map.put(keyFirstElement + keySecondElement, RetrofitUtils.PDFtoRequestBody(f));

                            MultipartBody.Part body = MultipartBody.Part.createFormData(keyFirstElement + keySecondElement
                                    ,displayName, fileBody);

                            mapDocFiles.put(keyFirstElement+keySecondElement,body);
                        }
                        else Toast.makeText(getActivity(), getResources().getString(R.string.limitexceeded), Toast.LENGTH_SHORT).show();
                    }
                    else {
                        f = GeneralFunctions.saveFileExternalStorage(bitmap);
                       /* map.put(keyFirstElement + keySecondElement, RetrofitUtils.ImagetoRequestBody(f));
                        RequestBody requestFile =RequestBody.create(MediaType.parse("multipart/form-data"),f);
                        MultipartBody.Part body =
                                MultipartBody.Part.createFormData(keyFirstElement + keySecondElement, displayName, requestFile);
                        docFiles.add(body);*/

                        try {
                            fileDetailList.get(positionOfform).docTypes.get(subDocPosition).docs.get(subDocCheck).isSelected=true;
                        }
                        catch (Exception e){

                        }








                        f = GeneralFunctions.saveFileExternalStorage(bitmap);

                        ProgressRequestBody fileBody = new ProgressRequestBody(f, this,"multipart/form-data");
                        MultipartBody.Part filePart = MultipartBody.Part.createFormData(keyFirstElement + keySecondElement
                                ,displayName, fileBody);

                        map.put(keyFirstElement + keySecondElement, RetrofitUtils.ImagetoRequestBody(f));

                        mapDocFiles.put(keyFirstElement+keySecondElement,filePart);
                    }

                    // fileDetailList.get(0).docTypes.get(0).docs.get(subDocPos).isSelected=true;
                    if(f!=null) {
                       /* if (docHolder != null) {
                            docHolder.cvDocFurtherSelect.setBackgroundResource(R.drawable.fileselected);
                            //docHolder.ivCrossDocs.setVisibility(View.VISIBLE);
                        }

                        if (attachHolder != null) {
                            attachHolder.tvSingleDoc.setBackgroundResource(R.drawable.fileselected);
                            //attachHolder.ivSingleCross.setVisibility(View.VISIBLE);
                        }*/


                        setHolderSelectable();
                    }
                }

                break;
            case CAMERA_CAPTURE_IMAGE_REQUEST_CODE:
                if (resultCode != Activity.RESULT_CANCELED)
                {
                    if (resultCode == Activity.RESULT_OK) {
                        // successfully captured the image
                        // display it in image view
                        setfinalCheckArray();
                        previewCapturedImage();
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // user cancelled Image capture
                    GeneralFunctions.makeSnackbar(rlAttachment,getResources().getString(R.string.imagecancelled));
                } else {
                    // failed to capture image
                    GeneralFunctions.makeSnackbar(rlAttachment,getResources().getString(R.string.capturefailed));

                   /* if(subDocCheck.length()>1)
                    {
                        setListCheck(position,1);
                    }*/
                }

                break;


            case 1100:
                if (resultCode == Activity.RESULT_OK && data!= null )
                {
                    setfinalCheckArray();

                    if (isFirstAttachment) {
                        if (map.containsKey(keyFirstElement + keySecondElement + "_docType")) {
                            map.remove(keyFirstElement + keySecondElement + "_docType");
                            map.put(keyFirstElement + keySecondElement + "_availDoc",
                                    RetrofitUtils.StringtoRequestBody(data.getStringExtra("id")));
                        }
                    } else
                        map.put(keyFirstElement + keySecondElement, RetrofitUtils.StringtoRequestBody(data.getStringExtra("id")));
                    /*  {
                        if((keyFirstElement).contains("_"))
                            map.put(keyFirstElement + keySecondElement , RetrofitUtils.StringtoRequestBody(data.getStringExtra("id")));


                       else if(map.containsKey(keyFirstElement+keySecondElement+"_docType")) {
                            map.remove(keyFirstElement + keySecondElement + "_docType");
                            map.put(keyFirstElement + keySecondElement + "_availDoc", RetrofitUtils.StringtoRequestBody(data.getStringExtra("id")));
                        }
                        // map.put(keyFirstElement+keySecondElement+"_availDoc", RetrofitUtils.StringtoRequestBody(""));

                        if(docHolder!=null) {
                            docHolder.cvDocFurtherSelect.setBackgroundResource(R.drawable.fileselected);
                            //docHolder.ivCrossDocs.setVisibility(View.VISIBLE);
                        }

                        if(attachHolder!=null) {
                            attachHolder.tvSingleDoc.setBackgroundResource(R.drawable.fileselected);
                            //attachHolder.ivSingleCross.setVisibility(View.VISIBLE);
                        }

                    }*/
                  /*  if(docHolder!=null) {
                        docHolder.cvDocFurtherSelect.setBackgroundResource(R.drawable.fileselected);
                        //docHolder.ivCrossDocs.setVisibility(View.VISIBLE);
                    }

                    if(attachHolder!=null) {
                        attachHolder.tvSingleDoc.setBackgroundResource(R.drawable.fileselected);
                        //attachHolder.ivSingleCross.setVisibility(View.VISIBLE);
                    }*/
                    setHolderSelectable();

                }
        }
    }

    public void openFile()
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");  //intent.setType("image/*");'
        String[] mimetypes = {"image/*", "application/pdf"};
        // intent.setType("image/*|application/pdf");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);




        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            GeneralFunctions.makeSnackbar(rlAttachment,getResources().getString(R.string.installfilemanager));

        }
    }






    private void previewCapturedImage() {
        try {
            // hide video preview



            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;

            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
                    options);


            String displayName=fileUri.getLastPathSegment();
            /* Bitmap original = BitmapFactory.decodeStream(getAssets().open("1024x768.jpg")); */
            /*ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 1, out);
            Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));*/



            File compressed = GeneralFunctions.saveImageToExternalStorage(bitmap,1);
           /* map.put(keyFirstElement+keySecondElement,RetrofitUtils.ImagetoRequestBody(compressed));

            File file = new File(fileUri.getPath());
            RequestBody requestFile =RequestBody.create(MediaType.parse("multipart/form-data"),compressed);

            MultipartBody.Part body;

                body =
                        MultipartBody.Part.createFormData(keyFirstElement+keySecondElement, displayName, requestFile);
            docFiles.add(body);


*/



            ProgressRequestBody
                    fileBody = new ProgressRequestBody(compressed, this,"multipart/form-data");
            MultipartBody.Part body = MultipartBody.Part.createFormData(keyFirstElement+keySecondElement
                    ,displayName, fileBody);
            mapDocFiles.put(keyFirstElement+keySecondElement,body);
            // docFiles.add(body);


            map.put(keyFirstElement+keySecondElement,
                    RetrofitUtils.ImagetoRequestBody(GeneralFunctions.saveImageToExternalStorage(bitmap,1)));





            fileDetailList.get(positionOfform).docTypes.get(subDocPosition).docs.get(subDocCheck).isSelected=true;
            // fileMapList.add(fileMap);
            //fileIdList.add(fieldId);
          /*  if(docHolder!=null) {
                docHolder.cvDocFurtherSelect.setBackgroundResource(R.drawable.fileselected);
              //  docHolder.ivCrossDocs.setVisibility(View.VISIBLE);
            }

            if(attachHolder!=null) {
                attachHolder.tvSingleDoc.setBackgroundResource(R.drawable.fileselected);
              //  attachHolder.ivSingleCross.setVisibility(View.VISIBLE);
            }*/



            setHolderSelectable();

        } catch (NullPointerException e) {
            // long length = file.length();
            //length = length/1024000;
            // Toast.makeText(getActivity(), "cam fail " , Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void saveDocsForUser() {
        //  GeneralFunctions.showDialog(getActivity());
        /*try {
            getBytesFromList(docFiles);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "moduleerror", Toast.LENGTH_SHORT).show();
        }*/
        map.put("serviceid", RetrofitUtils.StringtoRequestBody(mPrefs.getString(Constants.ServiceId,"")));
        map.put("userserviceid",RetrofitUtils.StringtoRequestBody(mPrefs.getString(Constants.UserServiceID,"")));
        Log.d("Token",mPrefs.getString(Constants.Auth,""));
        List<MultipartBody.Part> docFiles1 = new ArrayList<>();
        if(!mapDocFiles.isEmpty()) {

            for (Object aData : mapDocFiles.keySet().toArray()) {
                docFiles1.add(mapDocFiles.get(aData.toString()));
            }
        }
        Call<UsidModel> call =RestClient.get().saveDocGetId(map,docFiles1);
        call.enqueue(new Callback<UsidModel>() {
            @Override
            public void onResponse(Call<UsidModel> call, Response<UsidModel> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful())
                {
                    if(response.body().code.equals("401"))
                    {
                        GeneralFunctions.tokenExpireAction(getActivity());
                    }
                    else if(response.body().success==1)
                    {
                        //  mPrefs.save(Constants.UserServiceID,response.body().data.userServiceId.toString());

                        hitReadFieldsforUser();
                        if(dialogUploadProgress!=null) {
                            pbProgress.setProgress(100);
                            tvUploadingAttach.setText("Completed!");
                            tvPercent.setText("100%");
                            ivImageCenter.setImageResource(R.drawable.selected_tick);
                        }
                    }
                    else {
                        GeneralFunctions.makeSnackbar(rlAttachment, response.body().message);
                        if(dialogUploadProgress!=null)
                            dialogUploadProgress.dismiss();
                    }
                }
            }

            @Override
            public void onFailure(Call<UsidModel> call, Throwable t) {
                if(dialogUploadProgress!=null)
                    dialogUploadProgress.dismiss();
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(rlAttachment,getResources().getString(R.string.netIssue));

            }
        });
    }

    private void saveDocsIndividually() {
        GeneralFunctions.showDialog(getActivity());
        map.put("serviceid", RetrofitUtils.StringtoRequestBody(mPrefs.getString(Constants.ServiceId,"")));
        map.put("userserviceid",RetrofitUtils.StringtoRequestBody(mPrefs.getString(Constants.UserServiceID,"")));
        Log.d("Token",mPrefs.getString(Constants.Auth,""));
        Call<UsidModel> call =RestClient.get().saveDocGetId(map,docFiles);
        call.enqueue(new Callback<UsidModel>() {
            @Override
            public void onResponse(Call<UsidModel> call, Response<UsidModel> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful())
                {
                    if(response.body().code.equals("401"))
                    {
                        GeneralFunctions.tokenExpireAction(getActivity());
                    }

                    else if(response.body().success==1)
                    {
                        //  mPrefs.save(Constants.UserServiceID,response.body().data.userServiceId.toString());

                        hitReadFieldsforUser();
                    }
                    else
                        GeneralFunctions.makeSnackbar(rlAttachment,response.body().message);

                }
            }

            @Override
            public void onFailure(Call<UsidModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(rlAttachment,getResources().getString(R.string.netIssue));

            }
        });
    }


    private List<MandatoryDetails> details;
    public void hitReadFieldsforUser()
    {
        GeneralFunctions.showDialog(getActivity());
        // mPrefs.getString(Constants.OnDetailsPage,"").equals("no");

        Call<MandatoryFields> call= RestClient.get().readMandatoryFields(mPrefs.getString(Constants.UserServiceID,""),mPrefs.getString(Constants.ServiceId,""),mPrefs.getString(Constants.SurveyId,""));
        call.enqueue(new Callback<MandatoryFields>() {
            @Override
            public void onResponse(Call<MandatoryFields> call, Response<MandatoryFields> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful())
                {
                    if(response.body().code.equals("401"))
                    {
                        GeneralFunctions.tokenExpireAction(getActivity());
                    }
                  else if (response.body().success==1)
                    {

                        details=response.body().data.fields;
                        List<MandatoryDetails> manList=new ArrayList<MandatoryDetails>();
                        if(details!=null && details.size()>0) {
                            mPrefs.save(Constants.Automated,"NO");
                            if (details.get(0).field__field_group__id != null)
                                group_id = details.get(0).field__field_group__id;
                            else group_id = "";

                            for (int i = 0; i < details.size(); i++) {
                                if (details.get(i).field__field_group__id != null) {
                                    if (!details.get(i).field__field_group__id.equals(group_id)) {
                                        MandatoryDetails obj = new MandatoryDetails();
                                        //obj = details.get(i);
                                        obj.is_label = true;
                                        obj.display_name=details.get(i).field__field_group__name;
                                        obj.field__field_type="secondhead";
                                        group_id = details.get(i).field__field_group__id;
                                        manList.add(obj);
                                        // val="grouphead";
                                    }
                                }
                                manList.add(details.get(i));


                            }
                            mPrefs.save(Constants.MandatoryList, new Gson().toJson(details));

                            ((FourthApplicationActivity) getActivity()).setPosition(1);
                        }
                        else {
                            mPrefs.save(Constants.Automated,"YES");
                            mPrefs.save(Constants.FormExist,"NO");

                            //Todo:Haryana remove this code
                            if(MyApplication.Client=="haryana")
                            {
                                hitSaveMandatoryFields();
                            }

                            else {
                                Intent intent =new Intent(getActivity(), FifthPaymentActivity.class);

                                //mPrefs.save(Constants.NextLink,"payment_gateway");
                                startActivity(intent);
                                //getActivity().finish();
                            }
                        }
                    }
                }
                else {
                    GeneralFunctions.makeSnackbar(rlAttachment,getResources().getString(R.string.serverIssue));

                }
                if (dialogUploadProgress!=null)
                    dialogUploadProgress.dismiss();
            }

            @Override
            public void onFailure(Call<MandatoryFields> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(rlAttachment,getResources().getString(R.string.netIssue));
            }
        });
    }



    private void hitSaveMandatoryFields() {
        GeneralFunctions.showDialog(getActivity());
        final Map<String ,String> map =new HashMap<>();
        /*for(int i=0;i<details.size();i++)
        {
            if(details.get(i).field__system_name!=null)
                map.put(details.get(i).id+"_"+details.get(i).field__system_name,mPrefs.getString(details.get(i).id+"_"+details.get(i).field__system_name,""));
        }*/
        map.put("serviceid",mPrefs.getString(Constants.ServiceId,""));
        map.put("userserviceid",mPrefs.getString(Constants.UserServiceID,""));
        map.put("survey_id",mPrefs.getString(Constants.SurveyId,""));

        Call<SaveMandatoryFields> call= RestClient.get().saveMandatoryFields(map);
        call.enqueue(new Callback<SaveMandatoryFields>() {
            @Override
            public void onResponse(Call<SaveMandatoryFields> call, Response<SaveMandatoryFields> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful())
                {
                    if(response.body().code.equals("401"))
                    {
                        GeneralFunctions.tokenExpireAction(getActivity());
                    }

                   else if(response.body().success==1)
                    {

                        if(response.body().data.next_link.equals("payment_gateway")&&response.body().data.payment_detail!=null)
                        {
                            mPrefs.save(Constants.MerchantId, response.body().data.payment_detail.merchantTxnId);
                            mPrefs.save(Constants.Signature, response.body().data.payment_detail.signature);
                            mPrefs.save(Constants.ReturnUrl, response.body().data.payment_detail.returnUrl);
                            mPrefs.save(Constants.NotifyUrl, response.body().data.payment_detail.notifyUrl);

                            if(response.body().data.payment_detail.contact!=null)
                            {mPrefs.save(Constants.Contact, response.body().data.payment_detail.contact);}

                            else {mPrefs.save(Constants.Contact,"");}

                            if(response.body().data.payment_detail.email!=null)
                            { mPrefs.save(Constants.Email, response.body().data.payment_detail.email);}

                            else { mPrefs.save(Constants.Email, "");}

                            mPrefs.save(Constants.PaymentUrl, response.body().data.payment_detail.action);
                            mPrefs.save(Constants.Currency, response.body().data.payment_detail.currency);
                            mPrefs.save(Constants.OrderAmount, response.body().data.payment_detail.amount.toString());
                        }
                        else if(response.body().data.next_link.equals("payment_gateway")){
                            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                        }


                        //Todo: next link hardcoded , instructions needed , backend unable to deciphr what is the bug
                        //mPrefs.save(Constants.NextLink,response.body().data.next_link);
                        mPrefs.save(Constants.NextLink,"meeting_address");
                        Intent intent =new Intent(getActivity(), FifthPaymentActivity.class);
                        //clearApplicantDetails();
                        startActivity(intent);
                        // getActivity().finish();
                    }
                    else {
                        GeneralFunctions.makeSnackbar(rlAttachment,response.body().message);

                    }
                }
                else  GeneralFunctions.makeSnackbar(rlAttachment,getResources().getString(R.string.serverIssue));

            }

            @Override
            public void onFailure(Call<SaveMandatoryFields> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(rlAttachment,getResources().getString(R.string.netIssue));
            }
        });
    }

    @Override
    public void onProgressUpdate(int percentage,String imageName) {
        tvPercent.setText( percentage+"%");
        tvUploadingAttach.setText("Uploading "+imageName);
        pbProgress.setProgress(percentage);
    }

    @Override
    public void onError() {
        Toast.makeText(getActivity(),"Error",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFinish() {
        Toast.makeText(getActivity(),"Finish",Toast.LENGTH_LONG).show();
    }

    public void showDialogUploadProgress() {
        try {
            dialogUploadProgress = new Dialog(getActivity(), R.style.MyDialog);
            dialogUploadProgress.requestWindowFeature(Window.FEATURE_NO_TITLE);
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.popup_attachment_progress, null);
            dialogUploadProgress.setContentView(layout);

            pbProgress= layout.findViewById(R.id.pbProgress);
            ivImageCenter= layout.findViewById(R.id.ivImageCenter);
            tvUploadingAttach= layout.findViewById(R.id.tvUploadingAttach);
            tvPercent= layout.findViewById(R.id.tvPercent);
            tvPercent.setText( getString(R.string.percent_0));
            tvUploadingAttach.setText(getString(R.string.uploading_attachments));
            pbProgress.setProgress(0);
            ivImageCenter.setImageResource(R.drawable.doc_icon_01);
            dialogUploadProgress.setCancelable(false);
            dialogUploadProgress.setCanceledOnTouchOutside(false);
            dialogUploadProgress.show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void setHolderSelectable()
    {
        if(docHolder!=null) {
            docHolder.tvDocFurther.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.green_round_tick_01,0);
            docHolder.tvDocFurther.setText(getString(R.string.doc_attached));
            docHolder.tvDocFurther.setBackgroundResource(R.drawable.fileselected);
        }

        if(attachHolder!=null) {
            attachHolder.tvSingleDoc.setText(getString(R.string.doc_attached));
            attachHolder.tvSingleDoc.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.green_round_tick_01,0);
            attachHolder.tvSingleDoc.setBackgroundResource(R.drawable.fileselected);
        }
    }

   /* public  void getBytesFromList(List list) throws IOException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(baos);
            out.writeObject(list);
            out.close();
            Toast.makeText(getActivity(), ""+baos.toByteArray().length, Toast.LENGTH_SHORT).show();


        }
        catch (Exception e)
        {
            Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
        }

    }*/

}
