package easygov.saral.harlabh.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import org.json.JSONObject;

import easygov.saral.harlabh.fragments.managementfragments.FilterAppFragment;
import easygov.saral.harlabh.R;
import easygov.saral.harlabh.fragments.managementfragments.SearchApplicationFragment;
import easygov.saral.harlabh.fragments.managementfragments.StatusApplicationFragment;

/**
 * Created by apoorv on 26/09/17.
 */

public class ManagementActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView ivManagementBack;
    private RelativeLayout llManagement;
    private RelativeLayout rlFilterApplication,rlSearchApplication,rlApplicationStatus;
    private String backDecisions;
    private FilterAppFragment fragment;
    private JSONObject jsonPrint;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management);
        init();
        backDecisions=getIntent().getExtras().getString("frompayment");

        if(backDecisions.equals("yes"))
        {
            getSupportFragmentManager().beginTransaction().addToBackStack(null).
                    add(R.id.llManagement,new FilterAppFragment()).commit();
        }
        clickHandlers();
    }

    private void clickHandlers() {
        ivManagementBack.setOnClickListener(this);
        rlFilterApplication.setOnClickListener(this);
        rlSearchApplication.setOnClickListener(this);
        rlApplicationStatus.setOnClickListener(this);
    }

    private void init() {
        ivManagementBack= findViewById(R.id.ivManagementBack);
        llManagement= findViewById(R.id.llManagement);
        rlFilterApplication= findViewById(R.id.rlFilterApplication);
        rlSearchApplication= findViewById(R.id.rlSearchApplication);
        rlApplicationStatus= findViewById(R.id.rlApplicationStatus);
        fragment=new FilterAppFragment();


    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.ivManagementBack) {
            backHandling();

        } else if (i == R.id.rlFilterApplication) {
            getSupportFragmentManager().beginTransaction().addToBackStack(null).
                    add(R.id.llManagement, fragment).commit();


        } else if (i == R.id.rlSearchApplication) {
            getSupportFragmentManager().beginTransaction().addToBackStack(null).
                    add(R.id.llManagement, new SearchApplicationFragment()).commit();


        } else if (i == R.id.rlApplicationStatus) {//printJSON();
            getSupportFragmentManager().beginTransaction().addToBackStack(null).
                    add(R.id.llManagement, new StatusApplicationFragment()).commit();


        }
    }

    private void backHandling()
    {

        if(backDecisions.equals("yes"))
        {
            Intent intent = new Intent(this,
                    HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
        }
        else finish();
    }

    @Override
    public void onBackPressed() {
        backHandling();
    }

    public void hideUi() {
        fragment.hideSortUi();}


    /*private void printJSON(){
        jsonPrint = new JSONObject();
        JSONArray jsonArrayPrint =new JSONArray();
        JSONObject lineOne;
        String text ="";
        try{
            text =  "===============================" + "\n"+
                    "           HARLABH             " + "\n"+
                    "===============================" + "\n"+
                    "          THANKYOU!            " + "\n"+
                    " YOUR APPLICATION DETAILS ARE: " + "\n"+
                    " 				                " + "\n"+
                    " TRANSACTION ID:               " + "\n"+
                    "  test id                      " + "\n"+
                    " APPLICATION ID:               " + "\n"+
                    "  test id                      " + "\n"+
                    " TOTAL AMOUNT PAID:            " + "\n"+
                    "  test id                      " + "\n"+
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

        Intent launchIntent =  ManagementActivity.this.getPackageManager().getLaunchIntentForPackage("com.apnapay.szzt");
        if (launchIntent != null) {
            launchIntent.putExtra("utility_name","print");
            launchIntent.putExtra("print_data",jsonPrint.toString());
            launchIntent.putExtra("activity_path","easygov.saral.harlabh.activity.ManagementActivity");
            launchIntent.putExtra("activity", "easygov.saral.harlabh.activity.ManagementActivity");

            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK *//*| Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
                    Intent.FLAG_ACTIVITY_CLEAR_TOP*//*);

            startActivity(launchIntent);
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }*/
}
