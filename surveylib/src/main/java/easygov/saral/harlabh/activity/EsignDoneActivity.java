package easygov.saral.harlabh.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import easygov.saral.harlabh.R;;
import easygov.saral.harlabh.models.responsemodels.esignformmodel.EsignFormModel;
import easygov.saral.harlabh.models.responsemodels.generalmodel.GeneralModel;
import easygov.saral.harlabh.utils.Constants;
import easygov.saral.harlabh.utils.GeneralFunctions;
import easygov.saral.harlabh.utils.MyApplication;
import easygov.saral.harlabh.utils.Prefs;
import easygov.saral.harlabh.utils.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EsignDoneActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvEsignApll,tvViewApp1,tvOrSign,tvAppliedDate,tvPrint,tvImproveWhat,tvSubmitReview,tvRateText;
    private ImageView ivGotoHome;
    private RelativeLayout rlEsignDone;
    private Prefs mPrefs;;
    private JSONObject jsonPrint;
    private String newFormat="";

    private RatingBar rbRating;
    private EditText etFeedBack;
    private View viewImproveWhat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esign_done);
        init();
    }

    private void init() {
        tvEsignApll= (TextView) findViewById(R.id.tvEsignApll);
        mPrefs=Prefs.with(this);
        tvViewApp1= (TextView) findViewById(R.id.tvViewApp1);
        tvEsignApll.setOnClickListener(this);
        rlEsignDone=(RelativeLayout) findViewById(R.id.rlEsignDone);
        tvAppliedDate= (TextView) findViewById(R.id.tvAppliedDate);
        tvOrSign= (TextView) findViewById(R.id.tvOrSign);
        ivGotoHome=(ImageView) findViewById(R.id.ivGotoHome);
        ivGotoHome.setOnClickListener(this);
        tvPrint=(TextView)findViewById(R.id.tvPrint);
        tvImproveWhat= findViewById(R.id.tvImproveWhat);
        tvSubmitReview= findViewById(R.id.tvSubmitReview);
        tvRateText= findViewById(R.id.tvRateText);
        etFeedBack= findViewById(R.id.etFeedBack);
        viewImproveWhat= findViewById(R.id.viewImproveWhat);
        rbRating= findViewById(R.id.rbRating);


        Date currentTime = Calendar.getInstance().getTime();

        try
        {

            SimpleDateFormat form = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
           // SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            newFormat = form.format(currentTime);
            tvAppliedDate.setText(newFormat);


        }
        catch (Exception e)
        {

            e.printStackTrace();
            GeneralFunctions.makeSnackbar(rlEsignDone,getResources().getString(R.string.parseerror));
        }




        if(mPrefs.getString(Constants.IsScheme,"").equals("YES")||
                mPrefs.getString(Constants.FromAadhar,"").equals("YES"))
        {
            tvEsignApll.setVisibility(View.GONE);
            tvViewApp1.setVisibility(View.GONE);
            tvOrSign.setVisibility(View.GONE);

        }
        else {
            tvEsignApll.setVisibility(View.VISIBLE);
            //TODO: remove these comments
            //tvViewApp1.setVisibility(View.VISIBLE);
           // tvOrSign.setVisibility(View.VISIBLE);
        }

        if(MyApplication.Client=="haryana"&&mPrefs.getString(Constants.FormExist,"").equals("NO"))
        {
            tvEsignApll.setVisibility(View.GONE);
            tvViewApp1.setVisibility(View.GONE);
            tvOrSign.setVisibility(View.GONE);
        }

        tvPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printJSON();

            }
        });

        etFeedBack.setVisibility(View.GONE);
        tvSubmitReview.setVisibility(View.GONE);
        tvImproveWhat.setVisibility(View.GONE);
        viewImproveWhat.setVisibility(View.GONE);

        rbRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                switch ((int) rating)
                {
                    case 1:
                        tvRateText.setText(getString(R.string.very_bad));
                        break;
                    case 2:
                        tvRateText.setText(getString(R.string.bad));
                        break;
                    case 3:
                        tvRateText.setText(getString(R.string.ok_ok));
                        break;
                    case 4:
                        tvRateText.setText(getString(R.string.good));
                        break;
                    case 5:
                        tvRateText.setText(getString(R.string.amazing));
                        break;
                    default:
                }
                etFeedBack.setVisibility(View.VISIBLE);
                tvSubmitReview.setVisibility(View.VISIBLE);
                tvImproveWhat.setVisibility(View.VISIBLE);
                viewImproveWhat.setVisibility(View.VISIBLE);
            }
        });
        tvSubmitReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hitRatingApi();
            }
        });

    }

    private void hitRatingApi() {
        GeneralFunctions.showDialog(this);

        Call<GeneralModel> call = RestClient.get().userServiceFeedback(mPrefs.getString(Constants.UserServiceID,""),
                String.valueOf((int)rbRating.getRating()),etFeedBack.getText().toString());
        call.enqueue(new Callback<GeneralModel>() {
            @Override
            public void onResponse(Call<GeneralModel> call, Response<GeneralModel> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful())
                {

                    if (response.body().code.equals("401")) {
                         GeneralFunctions.tokenExpireAction(EsignDoneActivity.this);
                    }
                    else if(response.body().success==1)
                    {
                        Intent intent = new Intent(EsignDoneActivity.this,
                                HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                    else
                        GeneralFunctions.makeSnackbar(etFeedBack,response.body().message);
                }
                else {
                    GeneralFunctions.makeSnackbar(etFeedBack,getResources().getString(R.string.serverIssue));
                }
            }

            @Override
            public void onFailure(Call<GeneralModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(etFeedBack,getResources().getString(R.string.netIssue));
            }
        });
    }

    private void hitEsignApi() {
        GeneralFunctions.showDialog(this);

        Call<EsignFormModel> call = RestClient.get().getEsignForm(mPrefs.getString(Constants.UserServiceID,""));
        call.enqueue(new Callback<EsignFormModel>() {
            @Override
            public void onResponse(Call<EsignFormModel> call, Response<EsignFormModel> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful())
                {
                    if(response.body().code.equals("401"))
                    {
                        GeneralFunctions.tokenExpireAction(EsignDoneActivity.this);
                    }
                    else if(response.body().success==1)
                    {
                        //esignModel=response.body();
                        Intent intent =new Intent(EsignDoneActivity.this,EsignFormActivity.class);
                        Bundle bundle=new Bundle();
                        bundle.putString("EsignForm", new Gson().toJson(response.body()));
                        intent.putExtras(bundle);
                        startActivity(intent);

                    }
                    else {
                        GeneralFunctions.makeSnackbar(rlEsignDone,response.body().message);
                    }
                }
                else {
                    GeneralFunctions.makeSnackbar(rlEsignDone,getResources().getString(R.string.serverIssue));
                }
            }

            @Override
            public void onFailure(Call<EsignFormModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(rlEsignDone,getResources().getString(R.string.netIssue));
            }
        });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tvEsignApll) {
            hitEsignApi();

        } else if (i == R.id.ivGotoHome) {
            Intent intent = new Intent(this,
                    HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        }
    }


    private void printJSON(){
        jsonPrint = new JSONObject();
        JSONArray jsonArrayPrint =new JSONArray();
        JSONObject lineOne;
        String text ="";
        try{
            text =  "==============================="+"\n" +
                    "           HARLABH             "+"\n" +
                    "==============================="+"\n" +
                    "          THANK YOU!           "+"\n" +
                    " 				                "+"\n" +
                    "YOUR APPLICATION DETAILS ARE:  "+"\n" +
                    " 				                "+"\n" +
                    "TRANSACTION ID:                "+"\n" +
                    mPrefs.getString(Constants.MerchantId,"")+"\n" +
                    " 				                "+"\n" +
                    "DATE & TIME:                   "+"\n" +
                    ""+newFormat+"\n" +
                    " 				                "+"\n" +
                    "APPLICATION ID:                " +"\n"+
                    mPrefs.getString(Constants.UserServiceID,"")+"\n" +
                    " 				                "+"\n" +
                    "TOTAL AMOUNT PAID:             " + "\n"+
                    "INR "+mPrefs.getString(Constants.OrderAmount,"")+"\n" +
                    " 				                "+"\n" +
                    "===============================";
            lineOne = new JSONObject();
            lineOne.put("text",text);
            lineOne.put("font","MEDIUM");
            lineOne.put("align","LEFT");
            lineOne.put("line",true);
            jsonArrayPrint.put(lineOne);
            //add all array to main object
            jsonPrint.put("print_receipt_array",jsonArrayPrint);
            callPrintApp();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();
        }
    }

    private void callPrintApp() {

        Intent launchIntent =  EsignDoneActivity.this.getPackageManager().getLaunchIntentForPackage("com.apnapay.szzt");
        if (launchIntent != null) {
            launchIntent.putExtra("utility_name","print");
            launchIntent.putExtra("print_data",jsonPrint.toString());
            launchIntent.putExtra("activity_path","easygov.saral.harlabh.activity.EsignDoneActivity");
            launchIntent.putExtra("activity", "easygov.saral.harlabh.activity.EsignDoneActivity");

            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK /*| Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
                    Intent.FLAG_ACTIVITY_CLEAR_TOP*/);

            startActivity(launchIntent);
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,
                HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}
