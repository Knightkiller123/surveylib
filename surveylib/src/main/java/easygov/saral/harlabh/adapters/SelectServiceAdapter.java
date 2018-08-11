package easygov.saral.harlabh.adapters;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import easygov.saral.harlabh.R;
import easygov.saral.harlabh.activity.FourthApplicationActivity;
import easygov.saral.harlabh.activity.KnowMoreActivity;
import easygov.saral.harlabh.activity.SecondSchemesActivity;
import easygov.saral.harlabh.activity.SplashActivity;
import easygov.saral.harlabh.activity.ThirdEligibilityActivity;
import easygov.saral.harlabh.models.DummyQues;

import easygov.saral.harlabh.models.surveypaging.StartSurveyPaging;
import easygov.saral.harlabh.models.surveypaging.SurveyPagingData;
import easygov.saral.harlabh.utils.Constants;
import easygov.saral.harlabh.utils.GeneralFunctions;
import easygov.saral.harlabh.utils.Prefs;
import easygov.saral.harlabh.utils.RecyclerBottomListener;
import easygov.saral.harlabh.utils.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by apoorv on 31/07/17.
 */


public class SelectServiceAdapter extends RecyclerView.Adapter<SelectServiceAdapter.Holder> {

    private List<DummyQues> services;

    private Context context;
    Map<String,String> map=new HashMap<>();
    private StartSurveyPaging entitlement;
    private Prefs mPrefs;
    private List<SurveyPagingData> schemes=new ArrayList<>();
    private int qlschemes[]=new int[1];
    int schemesarr[]=new int[1];
    RecyclerBottomListener onBottomReachedListener;
    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};


    public SelectServiceAdapter(Context context, List<SurveyPagingData> list, StartSurveyPaging entList)
    {
        this.context=context;
        schemes=list;
        entitlement=entList;
        mPrefs=Prefs.with(context);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.adapter_selectservice,parent,false);
        Holder holder=new Holder(view);
        return holder;
    }


    //use a different approach for selector
    @Override
    public void onBindViewHolder(final Holder holder, final int position) {

        holder.tvLevel.setText(context.getResources().getString(R.string.ServiceLevel)+" : " +schemes.get(position).service__service_level+" "+context.getResources().getString(R.string.days));


        holder.tvSchemeName.setText(schemes.get(position).service__name);
        if(schemes.get(position).service__department__name!=null )
            holder.tvDepartmentName.setText(schemes.get(position).service__department__name);

        String s="";
        if(schemes.get(position).service__benefit_unit!=null) {
            if (schemes.get(position).service__benefit_unit.equals("y")) {
                s = "yearly";
            } else if (schemes.get(position).service__benefit_unit.equals("hy")) {
                s = "half-yearly";
            } else if (schemes.get(position).service__benefit_unit.equals("q")) {
                s = "quarterly";
            } else if (schemes.get(position).service__benefit_unit.equals("m")) {
                s = "monthly";
            } else if (schemes.get(position).service__benefit_unit.equals("w")) {
                s = "weekly";
            } else if (schemes.get(position).service__benefit_unit.equals("d")) {
                s = "daily";
            } else if (schemes.get(position).service__benefit_unit.equals("h")) {
                s = "hourly";
            }
        }

        String money="";
        if(schemes.get(position).service__monetary_benefits!=null)
        {
            money=schemes.get(position).service__monetary_benefits;
        }


        // removeTags(schemes.get(position).service__analysis,"<p>(.*?)</p>",holder.tvDescScheme);

        if(mPrefs.getString(Constants.IsScheme,"").equals("YES"))
        {
            holder.tvSchemeKnowMore.setVisibility(View.GONE);
            holder.tvDescScheme.setText(Html.fromHtml(schemes.get(position).service__benefits)) ;

            holder.tvDescSchemeHead.setText(Html.fromHtml(schemes.get(position).service__analysis));
        }
        else {
            holder.tvSchemeKnowMore.setVisibility(View.VISIBLE);
            holder.tvDescScheme.setText(Html.fromHtml(schemes.get(position).service__analysis)) ;
        }

        double p=0.0;
        try {
            if(schemes.get(position).govt_fee!=null && schemes.get(position).service_charge!=null)
                p=Double.parseDouble(schemes.get(position).govt_fee)+
                        Double.parseDouble(schemes.get(position).service_charge);
        }
        catch (Exception e)
        {

        }

       // holder.tvPrice.setText(context.getResources().getString(R.string.totalfee)+" "+ context.getResources().getString(R.string.Rs)+" "+String.format( "%.2f", p ));
        if(schemes.get(position).has_elgibility_survey)
        {

            holder.tvCheckEligibility.setText(context.getResources().getString(R.string.checkeligible));
        }

        else {
            holder.tvCheckEligibility.setText(context.getResources().getString(R.string.apply));
        }


        try {
            if(position == schemes.size() - 1){

                onBottomReachedListener.onBottomReached(position);

            }
        }
        catch (Exception e)
        {}

        holder.tvSchemeKnowMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, KnowMoreActivity.class);

                double p2=Double.parseDouble(schemes.get(position).govt_fee)+
                        Double.parseDouble(schemes.get(position).service_charge);

                mPrefs.save(Constants.SchemeName,schemes.get(position).service__name);
              /*  mPrefs.save(Constants.Name,schemes.get(position).service__name);
                mPrefs.save(Constants.GovtFee,context.getResources().getString(R.string.Rs)+" "+schemes.get(position).govt_fee);
                mPrefs.save(Constants.AssistanceCharge,context.getResources().getString(R.string.Rs)+" "+schemes.get(position).service_charge);
                mPrefs.save(Constants.TotalCharge,context.getResources().getString(R.string.Rs)+" "+String.valueOf(p2));
                mPrefs.save(Constants.ServiceLevel,schemes.get(position).service__service_level.toString()+" Days");*/
                Bundle bundle =new Bundle();
                bundle.putString("servicename",new Gson().toJson(schemes.get(position)));
                bundle.putString("enlist",new Gson().toJson(entitlement));
                intent.putExtras(bundle);
                //bundle.putString("buttext",holder.tvSchemeKnowMore.getText().toString());
                context.startActivity(intent);
                //  intent.putExtras("servicename",new Gson().toJson(schemes.get(position)));
                ((SecondSchemesActivity)context).overridePendingTransition( R.anim.animateup_activity, R.anim.animatedown_activity );
                //((SecondSchemesActivity)context).finish();
            }
        });
        holder.tvCheckEligibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPrefs.save("sgm_new_id",schemes.get(position).id.toString());
                mPrefs.remove(Constants.UserServiceID);
                mPrefs.save(Constants.SchemeName,schemes.get(position).service__name);
                mPrefs.save(Constants.ServiceId,schemes.get(position).id.toString());
                double p1=Double.parseDouble(schemes.get(position).govt_fee)+
                        Double.parseDouble(schemes.get(position).service_charge);
                mPrefs.save(Constants.Name,schemes.get(position).service__name);
                mPrefs.save(Constants.GovtFee,context.getResources().getString(R.string.Rs)+" "+schemes.get(position).govt_fee);
                mPrefs.save(Constants.AssistanceCharge,context.getResources().getString(R.string.Rs)+" "+schemes.get(position).service_charge);
                mPrefs.save(Constants.TotalCharge,String.valueOf(p1));
                mPrefs.save(Constants.ServiceLevel,schemes.get(position).service__service_level.toString()+context.getResources().getString(R.string.days));
                if(schemes.get(position).has_elgibility_survey) {



                    schemesarr[0] = schemes.get(position).id;
                    map.put("current_scheme_list", new Gson().toJson(schemesarr));
                    map.put("survey_id", mPrefs.getString(Constants.SurveyId,""));
                    map.put("qualified_schemes", new Gson().toJson(qlschemes));
                    map.put("existing_field_list", new Gson().toJson(entitlement.data.existing_field_list));
                    map.put("rule_type", entitlement.data.rule_type);
                    map.put(Constants.PARAM_BENEFICIARY_TYPE_ID,mPrefs.getString(Constants.BeneficiaryID,""));
                    hitNext();
                }

                else {

                    // mPrefs.save(Constants.ServiceId,schemes.get(position).id.toString());
                    if(!hasPermissions(context, PERMISSIONS)){
                        ActivityCompat.requestPermissions(((SecondSchemesActivity)context), PERMISSIONS, PERMISSION_ALL);
                    }
                    else {
                        Intent intent = new Intent(context, FourthApplicationActivity.class);
                        context.startActivity(intent);
                    }


                }
            }
        });


    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void removeTags(String str,String pat,TextView tv)
    {
        String set="";
        Pattern pattern = Pattern.compile(pat);//"<p>(.*?)</p>"
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            set+=matcher.group(1)+"\n";

        }
        tv.setText(set);
    }

    public void hitNext()
    {
        GeneralFunctions.showDialog(context);
        map.put("geography_id",mPrefs.getString(Constants.GeographyId,""));
        Call<StartSurveyPaging> call= RestClient.get().setEnt(map);
        call.enqueue(new Callback<StartSurveyPaging>() {
            @Override
            public void onResponse(Call<StartSurveyPaging> call, Response<StartSurveyPaging> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful()) {
                    if(response.body().code==401)
                    {
                            GeneralFunctions.tokenExpireAction(context);
                    }
                   else if(response.body().success==1) {
                        // StartSurveyPaging object = response.body();

                        mPrefs.save(Constants.GenerateSurveyId,"yes");
                        Intent intent = new Intent(context, ThirdEligibilityActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("final", new Gson().toJson(response.body()));
                        intent.putExtras(bundle);
                        context.startActivity(intent);
                    }
                    else Toast.makeText(context, response.body().message, Toast.LENGTH_SHORT).show();
                    // ((SelectServiceActivity)context).finish();

                }
                else Toast.makeText(context, ""+context.getResources().getString(R.string.serverIssue), Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onFailure(Call<StartSurveyPaging> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                Toast.makeText(context, context.getResources().getString(R.string.netIssue), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return schemes.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private TextView tvLevel,tvDescScheme,tvSchemeKnowMore,tvPrice,tvCheckEligibility,tvSchemeName,tvDepartmentName,tvDescSchemeHead;
        private View vSeps1;
        public Holder(View itemView) {
            super(itemView);
            tvLevel= itemView.findViewById(R.id.tvLevel);
            tvDepartmentName= itemView.findViewById(R.id.tvDepartmentName);
            tvSchemeName= itemView.findViewById(R.id.tvSchemeName);
            tvDescScheme= itemView.findViewById(R.id.tvDescScheme);
            tvSchemeKnowMore= itemView.findViewById(R.id.tvSchemeKnowMore);
            tvPrice= itemView.findViewById(R.id.tvPrice);
            tvDescSchemeHead=itemView.findViewById(R.id.tvDescSchemeHead);
            tvCheckEligibility= itemView.findViewById(R.id.tvCheckEligibility);
        }
    }

    public void setOnBottomReachedListener(RecyclerBottomListener onBottomReachedListener){

        this.onBottomReachedListener = onBottomReachedListener;
    }
}
