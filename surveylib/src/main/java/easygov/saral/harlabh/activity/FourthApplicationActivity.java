package easygov.saral.harlabh.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.List;
import easygov.saral.harlabh.fragments.SelfDeclarationFragment;
import easygov.saral.harlabh.models.responsemodels.mandatoryfields.MandatoryDetails;
import easygov.saral.harlabh.utils.GeneralFunctions;
import easygov.saral.harlabh.utils.Prefs;
import easygov.saral.harlabh.R;
import easygov.saral.harlabh.fragments.storyboardfrags.ApplicationsFragment;
import easygov.saral.harlabh.utils.Constants;

public class FourthApplicationActivity extends AppCompatActivity {

    private ApplicationsFragment frag;
    private RelativeLayout rlApplication,rlSelfecContainer;
    private ImageView ivAppBack;
    private Prefs mPrefs;
    private LinearLayout llFourth;
    private TextView tvAppHeader,ivFourthScheme;
    private List<MandatoryDetails> details;
    public static int detailList=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);


        rlApplication= findViewById(R.id.rlApplication);
        llFourth= findViewById(R.id.llFourth);
        rlSelfecContainer= findViewById(R.id.rlSelfecContainer);
        mPrefs=Prefs.with(this);
        getSupportFragmentManager().beginTransaction().add(R.id.rlSelfecContainer,new SelfDeclarationFragment()).addToBackStack("info").commit();


        //setFourthActivity() ;

    }

    public void setFourthActivity() {
        getSupportFragmentManager().popBackStack();
        frag= new ApplicationsFragment();
        rlSelfecContainer.setVisibility(View.GONE);
        ivAppBack= findViewById(R.id.ivAppBack);
        tvAppHeader= findViewById(R.id.tvAppHeader);
        ivFourthScheme= findViewById(R.id.ivFourthScheme);
        ivFourthScheme.setText(mPrefs.getString(Constants.SecontActivityText,""));
        ivAppBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(frag.returnPagerPosition()==0) {
                    GeneralFunctions.backPopup(rlSelfecContainer.getRootView(), FourthApplicationActivity.this, 0);
                    //finish();
                }
                else {
                    if(detailList==0)
                        GeneralFunctions.backPopup(rlSelfecContainer.getRootView(),FourthApplicationActivity.this,0);
                        //finish();
                    else
                        frag.setPagerPosition(0);
                }
            }
        });

        getSupportFragmentManager().beginTransaction().add(R.id.rlApplication,frag).addToBackStack("info").commit();
    }

    public void setPosition(int Position)
    {
        frag.setPagerPosition(Position);
    }

    @Override
    public void onBackPressed() {
        if(rlSelfecContainer.getVisibility()==View.VISIBLE)
            GeneralFunctions.backPopup(rlSelfecContainer.getRootView(),FourthApplicationActivity.this,0);
            // finish();
        else if(frag.returnPagerPosition()==0)
            GeneralFunctions.backPopup(rlSelfecContainer.getRootView(),FourthApplicationActivity.this,0);
            //finish();
        else
        {
            if(detailList==0)
            {
                GeneralFunctions.backPopup(rlSelfecContainer.getRootView(),FourthApplicationActivity.this,0);
                finish();
            }
            else
                frag.setPagerPosition(0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void makeVisible()
    {
        rlSelfecContainer.setVisibility(View.VISIBLE);
    }

    public void makeInvisible()
    {
        rlSelfecContainer.setVisibility(View.GONE);
    }
}
