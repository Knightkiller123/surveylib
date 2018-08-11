package easygov.saral.harlabh.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import easygov.saral.harlabh.R;
import easygov.saral.harlabh.adapters.PaymentsAdapter;
import easygov.saral.harlabh.models.responsemodels.paymentinternal.InternalPayment;
import easygov.saral.harlabh.utils.GeneralFunctions;
import easygov.saral.harlabh.utils.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by apoorv on 22/01/18.
 */

public class PaymentStatusActivity extends AppCompatActivity {

    private RecyclerView rvPayments;
    private ImageView ivPaymentBack;
    private RelativeLayout rlPaymentsContainers,rlNoPayments;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        ivPaymentBack= findViewById(R.id.ivPaymentBack);
        rvPayments= findViewById(R.id.rvPayments);
        rlPaymentsContainers= findViewById(R.id.rlPaymentsContainers);
        ivPaymentBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rlNoPayments= findViewById(R.id.rlNoPayments);
        rvPayments.setLayoutManager(new LinearLayoutManager(this));

        hitPaymentApi();
    }

    private void hitPaymentApi() {
        GeneralFunctions.showDialog(this);
        Date currentTime = Calendar.getInstance().getTime();


        java.util.Date date ;
        String newFormat="";
        try
        {

            SimpleDateFormat form = new SimpleDateFormat("dd-MM-yyyy");
            // SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            newFormat = form.format(currentTime);



        }
        catch (Exception e)
        {

            e.printStackTrace();
            GeneralFunctions.makeSnackbar(rlPaymentsContainers,getResources().getString(R.string.parseerror));
        }
        Call<InternalPayment> call = RestClient.get().getPaymentDetails();
        call.enqueue(new Callback<InternalPayment>() {
            @Override
            public void onResponse(Call<InternalPayment> call, Response<InternalPayment> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful())
                {
                    if(response.body().code==401)
                    {
                        GeneralFunctions.tokenExpireAction(PaymentStatusActivity.this);
                    }
                    else if(response.body().success==1)
                    {
                        if(response.body().data.objects!=null&&response.body().data.objects.size()>0) {
                            rlNoPayments.setVisibility(View.GONE);
                            rvPayments.setAdapter(new PaymentsAdapter(PaymentStatusActivity.this, response.body().data.objects));
                        }
                        else  rlNoPayments.setVisibility(View.VISIBLE);
                    }
                    else {
                        rlNoPayments.setVisibility(View.VISIBLE);
                        GeneralFunctions.makeSnackbar(rlPaymentsContainers,response.body().message);
                    }
                }

                else {
                    rlNoPayments.setVisibility(View.VISIBLE);
                    GeneralFunctions.makeSnackbar(rlPaymentsContainers,getResources().getString(R.string.serverIssue));}
            }

            @Override
            public void onFailure(Call<InternalPayment> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                rlNoPayments.setVisibility(View.VISIBLE);
                GeneralFunctions.makeSnackbar(rlPaymentsContainers,getResources().getString(R.string.netIssue));
            }
        });

    }
}
