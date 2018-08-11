package easygov.saral.harlabh.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import easygov.saral.harlabh.R;
import easygov.saral.harlabh.adapters.BenifitListAdapter;
import easygov.saral.harlabh.models.responsemodels.beneficiarymodel.Beneficiary;
import easygov.saral.harlabh.models.responsemodels.beneficiarymodel.BenificiaryModell;
import easygov.saral.harlabh.utils.Constants;
import easygov.saral.harlabh.utils.GeneralFunctions;
import easygov.saral.harlabh.utils.Prefs;
import easygov.saral.harlabh.utils.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class BeneficiaryActivity extends AppCompatActivity {

    private RecyclerView rvBenificiaryList;
    private TextView tvBeneficiaryNext;
    private RelativeLayout rlBenif;
    public RelativeLayout rlBenifitContainer;
    private List<Beneficiary> list=new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_beneficiary);
        init();
        setAdapter();
    }

    private void setAdapter() {
        rvBenificiaryList.setLayoutManager(new LinearLayoutManager(this));
        rvBenificiaryList.setAdapter(new BenifitListAdapter(BeneficiaryActivity.this,list));
    }


    private void init() {
        rvBenificiaryList= findViewById(R.id.rvBenificiaryList);
        tvBeneficiaryNext= findViewById(R.id.tvBeneficiaryNext);
        rlBenif= findViewById(R.id.rlBenif);
         rlBenifitContainer= findViewById(R.id.rlBenifitContainer);
        hitBeniApi();
        tvBeneficiaryNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!((BenifitListAdapter)rvBenificiaryList.getAdapter()).getSelectedPosition().isEmpty())
                {

                    Prefs.with(BeneficiaryActivity.this).save(Constants.BeneficiaryID,
                            ((BenifitListAdapter)rvBenificiaryList.getAdapter()).getSelectedPosition());
                    if(!getIntent().hasExtra("from")) {
                        Intent intent = new Intent(BeneficiaryActivity.this, StateSelectActivity.class);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                    finish();
                }
            }
        });


        if(getIntent().hasExtra("from"))

            tvBeneficiaryNext.setText(R.string.select);

        else
            tvBeneficiaryNext.setText(R.string.next);







    }

    private void hitBeniApi() {
        GeneralFunctions.showDialog(this);
        Call<BenificiaryModell> call= RestClient.get().getBeneficiary();
        call.enqueue(new Callback<BenificiaryModell>() {
            @Override
            public void onResponse(Call<BenificiaryModell> call, Response<BenificiaryModell> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful())
                {
                    if(response.body().code==401)
                {
                    GeneralFunctions.tokenExpireAction(BeneficiaryActivity.this);
                }
                else if(response.body().success==1)
                    {
                        list.clear();
                        if(Prefs.with(BeneficiaryActivity.this).getString(Constants.BeneficiaryID,"").isEmpty())
                            list.addAll(response.body().data.beneficiaries);
                        else
                        {
                            String id=Prefs.with(BeneficiaryActivity.this).getString(Constants.BeneficiaryID,"");
                            for (int i =0 ;i<response.body().data.beneficiaries.size();i++)
                                if(response.body().data.beneficiaries.get(i).id.toString().equals(id))
                                {
                                    Beneficiary benef=response.body().data.beneficiaries.get(i);
                                    benef.selected=true;
                                    list.add(benef);
                                    ((BenifitListAdapter)rvBenificiaryList.getAdapter()).checkedPosition=i;
                                }
                                else
                                    list.add(response.body().data.beneficiaries.get(i));
                        }
                        rvBenificiaryList.getAdapter().notifyDataSetChanged();
                    }
                    else {
                        GeneralFunctions.dismissDialog();
                        GeneralFunctions.makeSnackbar(rlBenif,response.body().message);
                    }
                }
                else {
                    GeneralFunctions.dismissDialog();
                    GeneralFunctions.makeSnackbar(rlBenif,getResources().getString(R.string.serverIssue));
                }
            }

            @Override
            public void onFailure(Call<BenificiaryModell> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(rlBenif,getResources().getString(R.string.netIssue));
            }
        });
    }
}
