package easygov.saral.harlabh.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import easygov.saral.harlabh.utils.Prefs;
import easygov.saral.harlabh.R;
import easygov.saral.harlabh.fragments.storyboardfrags.CheckoutFragment;
import easygov.saral.harlabh.utils.Constants;

public class FifthPaymentActivity extends AppCompatActivity {
    private RelativeLayout rlCheckoutContainer;
    CheckoutFragment fragment;
    private ImageView ivCheckoutBack;
    private TextView ivFifthSchemes;
    private Prefs mPrefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fifth_payment);
        rlCheckoutContainer= findViewById(R.id.rlCheckoutContainer);
        fragment=new CheckoutFragment();
        mPrefs=Prefs.with(this);
      //  mPrefs.save(Constants.Automated,"YES");
        getSupportFragmentManager().beginTransaction().add(R.id.rlCheckoutContainer, fragment).addToBackStack("checkout").commit();
        ivCheckoutBack= findViewById(R.id.ivCheckoutBack);
        ivFifthSchemes= findViewById(R.id.ivFifthSchemes);
        ivFifthSchemes.setText(mPrefs.getString(Constants.SecontActivityText,""));
        ivCheckoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleBackPress();
            }
        });
    }

    @Override
    public void onBackPressed() {
       // super.onBackPressed();
        handleBackPress();
    }

    private void handleBackPress() {
        if(fragment.returnPagerPosition()==0)
            finish();

        else {
           if(mPrefs.getString(Constants.NextLink,"").equals("payment_gateway"))
           {
               mPrefs.save(Constants.NextLink,"");
               finish();
           }


            else {
               fragment.setPagerPosition(0);


           }

        }
    }

    public void scrollTo()
    {
        fragment.scrollPager();
    }
}
