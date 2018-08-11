package easygov.saral.harlabh.fragments.storyboardfrags;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import easygov.saral.harlabh.R;
import easygov.saral.harlabh.activity.EsignDoneActivity;
import easygov.saral.harlabh.activity.SuccessFulPaymentActivity;
import easygov.saral.harlabh.adapters.CheckoutPagerAdapter;
import easygov.saral.harlabh.fragments.PaymentWebviewFragment;
import easygov.saral.harlabh.fragments.checkout_pager_fragments.CommunicationFragment;
import easygov.saral.harlabh.models.responsemodels.couponmodel.CouponModel;
import easygov.saral.harlabh.models.responsemodels.generalmodel.GeneralModel;
import easygov.saral.harlabh.utils.Constants;
import easygov.saral.harlabh.utils.CustomViewPager;
import easygov.saral.harlabh.utils.GeneralFunctions;
import easygov.saral.harlabh.utils.MyApplication;
import easygov.saral.harlabh.utils.Prefs;
import easygov.saral.harlabh.utils.RestClient;
import retrofit2.Call;

import static easygov.saral.harlabh.fragments.checkout_pager_fragments.CommunicationFragment.distSelected;
import static easygov.saral.harlabh.fragments.checkout_pager_fragments.CommunicationFragment.stateSelected;

import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class CheckoutFragment extends Fragment  {


    private CustomViewPager vpCheckout;
    private TabLayout tabLayoutCheckout;
    private TextView tvCheckoutNext,tvApply, tvServiceNameChk,tvGovFee,tvAssistanceFee,tvTotalFee
            ,tvServiceLevelDays,tvCouponApplied;
    private Prefs mPrefs;
    private LinearLayout llCoupon;
    private RelativeLayout rlCheckoutLast;
    private EditText etCoupon;
    private LinearLayout llCheckout;
    private RelativeLayout rlRemoveCoupon;
    private ImageView ivRemoveCoupon;
    private String couponAppliedString="";
    private int removed=0;
    private ScrollView scCheckoutScroll;
    private CheckoutPagerAdapter adapter;
    private String nextLink;
    public static Map<String,String> billingMap=new HashMap<>();
    private CardView cvPayDetails,cvPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.sample_checkout, container, false);
        init(view);


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Todo: remove below function only for haryana
       /* if(mPrefs.getString(Constants.Automated,"").equals("YES")&& MyApplication.Client=="haryana")
         automationForHaryana();*/

    }

    private void automationForHaryana() {
        rlCheckoutLast.setVisibility(View.GONE);
        final Handler handler = new Handler();
        GeneralFunctions.showDialog(getActivity());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //  getSupportFragmentManager().beginTransaction().addToBackStack(null).add(R.id.rlSplashMain,new LanguageFragment()).commit();

                if(vpCheckout.getCurrentItem()==0)
                {
                    //if( ((BillingAddressFragment)adapter.getFragment(0)).checkBlank()) {
                    GeneralFunctions.dismissDialog();
                    setBillingAddressModel();
                    hitSaveBillAddress();
                    //}
                }
                else {
                    hitApplyCoupon("test");
                }

            }
        }, 1000);


    }


    private void setBillingAddressModel()
    {



       /* billingMap.put("first_name",mPrefs.getString(Constants.FirstName,""));
        billingMap.put("last_name",mPrefs.getString(Constants.LastName,""));
        billingMap.put("street1",mPrefs.getString(Constants.Street1,""));
        billingMap.put("street2",mPrefs.getString(Constants.Street2,""));
        billingMap.put("city",mPrefs.getString(Constants.City,""));
        billingMap.put("zip",mPrefs.getString(Constants.Zip,""));
        billingMap.put("state",String.valueOf(MyApplication.stateId));
        billingMap.put("district",String.valueOf(MyApplication.distId));*/



       /* billingMap.put("billing_first_name",mPrefs.getString(Constants.FirstName,""));
        billingMap.put("billing_last_name",mPrefs.getString(Constants.LastName,""));
        billingMap.put("billing_street1",mPrefs.getString(Constants.Street1,""));
        billingMap.put("billing_street2",mPrefs.getString(Constants.Street2,""));
        billingMap.put("billing_city",mPrefs.getString(Constants.City,""));
        billingMap.put("billing_zip",mPrefs.getString(Constants.Zip,""));
        billingMap.put("billing_state",String.valueOf(stateSelected));
        billingMap.put("billing_district",String.valueOf(distSelected));*/
        //etContactMeet.setText(mPrefs.getString(Constants.Phone,""));


        billingMap.put("billing_first_name",billingMap.get("first_name"));
        billingMap.put("billing_last_name",billingMap.get("last_name"));
        billingMap.put("billing_street1",billingMap.get("street1"));
        billingMap.put("billing_street2",billingMap.get("street2"));
        billingMap.put("billing_city",billingMap.get("city"));
        billingMap.put("billing_zip",billingMap.get("zip"));
        billingMap.put("billing_state",String.valueOf(stateSelected));
        billingMap.put("billing_district",String.valueOf(distSelected));

    }
    private void init(View view) {
        vpCheckout= view.findViewById(R.id.vpCheckout);
        vpCheckout.setPagingEnabled(false);
        tabLayoutCheckout= view.findViewById(R.id.tabLayoutCheckout);
        tvCheckoutNext= view.findViewById(R.id.tvCheckoutNext);
        mPrefs=Prefs.with(getActivity());
        llCheckout= view.findViewById(R.id.llCheckout);
        rlCheckoutLast= view.findViewById(R.id.rlCheckoutLast);
        rlRemoveCoupon= view.findViewById(R.id.rlRemoveCoupon);
        tvCouponApplied= view.findViewById(R.id.tvCouponApplied);
        ivRemoveCoupon= view.findViewById(R.id.ivRemoveCoupon);

        scCheckoutScroll=view.findViewById(R.id.scCheckoutScroll);

        cvPager= view.findViewById(R.id.cvPager);
        cvPayDetails= view.findViewById(R.id.cvPayDetails);

        cvPayDetails.setVisibility(View.GONE);
        scCheckoutScroll.setVisibility(View.GONE);

        ivRemoveCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hitApplyCoupon("");
            }
        });

        tvServiceNameChk= view.findViewById(R.id.tvServiceNameChk);
        tvGovFee= view.findViewById(R.id.tvGovFee);
        tvAssistanceFee= view.findViewById(R.id.tvAssistanceFee);
        tvTotalFee= view.findViewById(R.id.tvTotalFee);
        tvServiceLevelDays= view.findViewById(R.id.tvServiceLevelDays);

        tvServiceNameChk.setText(mPrefs.getString(Constants.Name,""));
        tvGovFee.setText(mPrefs.getString(Constants.GovtFee,""));
        tvAssistanceFee.setText(mPrefs.getString(Constants.AssistanceCharge,""));
        amount= Double.parseDouble(mPrefs.getString(Constants.TotalCharge,""));
        tvTotalFee.setText(getResources().getString(R.string.Rs)+" "+mPrefs.getString(Constants.TotalCharge,""));
        tvServiceLevelDays.setText(mPrefs.getString(Constants.ServiceLevel,""));



        tabLayoutCheckout.addTab(tabLayoutCheckout.newTab().setText(getString(R.string.address)));
        // tabLayoutCheckout.addTab(tabLayoutCheckout.newTab().setText(getResources().getString(R.string.billing)));
        tabLayoutCheckout.addTab(tabLayoutCheckout.newTab().setText(getResources().getString(R.string.payments)));


        tvApply= view.findViewById(R.id.tvApply);
        etCoupon= view.findViewById(R.id.etCoupon);
        tabLayoutCheckout.setTabGravity(TabLayout.GRAVITY_FILL);
        llCoupon= view.findViewById(R.id.llCoupon);

        LinearLayout tabStrip = ((LinearLayout)tabLayoutCheckout.getChildAt(0));
        tabStrip.setEnabled(false);
        for(int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setClickable(false);

        /*    ((TextView)tabStrip.getChildAt(i).findViewById(android.R.id.text1)).setTextSize
                    (TypedValue.COMPLEX_UNIT_DIP,10);
        */    //((TextView) tabStrip.getChildAt(i)).setTextSize(TypedValue.COMPLEX_UNIT_DIP,10);



            //  <item name="android:textSize">10sp</item>

        }
        adapter=new CheckoutPagerAdapter(getFragmentManager(),tabLayoutCheckout.getTabCount(),getActivity());
        vpCheckout.setAdapter(adapter);


        // tabLayoutCheckout.addOnTabSelectedListener(this);

        tvApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etCoupon.getText().toString().isEmpty()) {
                    hitApplyCoupon(etCoupon.getText().toString());
                }
                else GeneralFunctions.makeSnackbar(rlCheckoutLast, getResources().getString(R.string.plcopon));
            }
        });

        if(mPrefs.getString(Constants.NextLink,"").equals("payment_gateway")) {
            scCheckoutScroll.setVisibility(View.VISIBLE);
            cvPayDetails.setVisibility(View.VISIBLE);
            cvPager.setVisibility(View.GONE);
            vpCheckout.setVisibility(View.GONE);
            vpCheckout.setCurrentItem(1);
            tvCheckoutNext.setText(getResources().getString(R.string.pay) +getResources().getString(R.string.Rs)+mPrefs.getString(Constants.TotalCharge,""));
            llCoupon.setVisibility(View.VISIBLE);
        }

        tvCheckoutNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (vpCheckout.getCurrentItem())
                {
                    case 0:

                        // Fragment page = getFragmentManager().findFragmentByTag("android:switcher:" + R.id.vpCheckout + ":" + vpCheckout.getCurrentItem());
                        if( ((CommunicationFragment)adapter.getFragment(0)).checkBlank()){
                            {
                                setBillingAddressModel();
                                hitSaveBillAddress();
                            }
                          /*  scrollPager();
                            vpCheckout.setCurrentItem(1);*/
                        }
                        break;

                   /* case 1:
                        if(!((BillingAddressFragment)adapter.getFragment(1)).toValidateBilling()) {
                            if (((BillingAddressFragment) adapter.getFragment(1)).checkBlank())
                                hitSaveBillAddress();
                        }
                        else {
                            setBillingAddressModel();
                            hitSaveBillAddress();
                        }
                        break;
                    */
                    case 1:

                        verifyCoupon();


                        //tvCheckoutNext.setText("PAY");
                        //llCoupon.setVisibility(View.VISIBLE);
                        // hitApplyCoupon("");

                        break;
                }


            }
        });

        vpCheckout.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        tvCheckoutNext.setText(getResources().getString(R.string.next));
                        llCoupon.setVisibility(View.GONE);
                        tabLayoutCheckout.getTabAt(position).select();
                        break;
                    /*case 1:

                        tvCheckoutNext.setText(getResources().getString(R.string.next));
                        llCoupon.setVisibility(View.GONE);
                        break;
                    */default:

                        tvCheckoutNext.setText(getResources().getString(R.string.pay) +
                                getResources().getString(R.string.Rs) + " " +
                                mPrefs.getString(Constants.OrderAmount, ""));
                        llCoupon.setVisibility(View.VISIBLE);
                        break;
                }

                tabLayoutCheckout.getTabAt(position).select();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });




        /*rlCheckoutLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneralFunctions.hideSoftKeyboard(getActivity());

            }
        });*/
/*
        llCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneralFunctions.hideSoftKeyboard(getActivity());
            }
        });*/

        llCheckout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                GeneralFunctions.hideSoftKeyboard(getActivity());
                return false;
            }
        });
    }

    public void scrollPager()
    {
        scCheckoutScroll.smoothScrollTo(0,0);

    }


    private void gotoPaymentWebview() {
        getFragmentManager().beginTransaction().addToBackStack(null).add(R.id.rlCheckoutLast,new PaymentWebviewFragment()).commit();

    }


    private void hitSaveBillAddress() {
       /* hdnIsAskForApplicantOfBilling:0
        userServiceId:1
        first_name:shashank
        last_name:shukla
        middle_name:
        street1:72
        street2:nasscom
        city:noida
        state:1
        country:1
        zip:227105
        district:10
        billing_first_name:bill name
        billing_last_name:bill last name
        billing_middle_name:
        billing_street1:72
        billing_street2:nasscom
        billing_city:noida
        billing_state:1
        billing_country:1
        billing_zip:121
        billing_district:1*/

        //Todo: remove static data and take actual
        GeneralFunctions.showDialog(getActivity());
        Map<String,String> map=new HashMap<>();

       /* map.put("hdnIsAskForApplicantOfBilling","0");

        map.put("first_name","asd");
        map.put("last_name","asd");
        map.put("middle_name","asd");

        map.put("street1","asd");
        map.put("street2","asd");
        map.put("city","asd");
        map.put("state","1");

        map.put("country","1");
        map.put("zip","210705");
        map.put("district","1");

        map.put("billing_first_name","asd");
        map.put("billing_last_name","asd");
        map.put("billing_middle_name","asd");
        map.put("billing_street1","asd");map.put("billing_street2","asd");
        map.put("billing_city","asd");map.put("billing_state","1");
        map.put("billing_country","1");map.put("billing_zip","210707");map.put("billing_district","1");*/

        billingMap.put("userServiceId",mPrefs.getString(Constants.UserServiceID,""));
        billingMap.put("middle_name","asd");
        billingMap.put("country","1");
        billingMap.put("billing_country","1");
        billingMap.put("hdnIsAskForApplicantOfBilling","0");


        Call<GeneralModel> call= RestClient.get().saveBillAddress(billingMap);
        call.enqueue(new Callback<GeneralModel>() {
            @Override
            public void onResponse(Call<GeneralModel> call, Response<GeneralModel> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful())
                {
                    if (response.body().code.equals("401")) {
                        GeneralFunctions.tokenExpireAction(getActivity());
                    }
                    else if(response.body().success==1)
                    {
                        mPrefs.save(Constants.ServiceInvoiceId,response.body().data.serviceInvoiceId);
                        mPrefs.save(Constants.MerchantId, response.body().data.payment_detail.merchantTxnId);
                        mPrefs.save(Constants.OrderAmount,String.format( "%.2f", response.body().data.payment_detail.amount ));

                        tvCheckoutNext.setText(getResources().getString(R.string.pay) +mPrefs+getResources().getString(R.string.Rs)+" "+mPrefs.getString(Constants.OrderAmount,""));

                        vpCheckout.setCurrentItem(1);
                        //llCoupon.setVisibility(View.VISIBLE);
                        llCoupon.setVisibility(View.VISIBLE);
                        rlRemoveCoupon.setVisibility(View.GONE);
                        if(response.body().data.payment_detail!=null) {
                            mPrefs.save(Constants.MerchantId, response.body().data.payment_detail.merchantTxnId);
                            mPrefs.save(Constants.Signature, response.body().data.payment_detail.signature);
                            mPrefs.save(Constants.ReturnUrl, response.body().data.payment_detail.returnUrl);
                            mPrefs.save(Constants.NotifyUrl, response.body().data.payment_detail.notifyUrl);

                            if(response.body().data.payment_detail.contact!=null)
                                mPrefs.save(Constants.Contact, response.body().data.payment_detail.contact);

                            else {
                                mPrefs.save(Constants.Contact, "");
                            }

                            if(response.body().data.payment_detail.email!=null)
                                mPrefs.save(Constants.Email, response.body().data.payment_detail.email);

                            else {
                                mPrefs.save(Constants.Email, "");
                            }
                            mPrefs.save(Constants.PaymentUrl, response.body().data.payment_detail.action);
                            mPrefs.save(Constants.Currency, response.body().data.payment_detail.currency);
                            mPrefs.save(Constants.OrderAmount,String.format( "%.2f", response.body().data.payment_detail.amount )
                            );
                            // llCoupon.setVisibility(View.VISIBLE);
                            if(mPrefs.getString(Constants.Automated,"").equals("YES")&& MyApplication.Client.equals("haryana"))
                            {
                                hitApplyCoupon("test");
                            }
                            else {
                                vpCheckout.setCurrentItem(1);
                                cvPayDetails.setVisibility(View.VISIBLE);
                                scCheckoutScroll.setVisibility(View.VISIBLE);
                                vpCheckout.setVisibility(View.GONE);
                                cvPager.setVisibility(View.GONE);
                            }
                            // tvCheckoutNext.setText("PAY "mPrefs+getResources().getString(R.string.Rs)+);
                        }
                        else {
                            Toast.makeText(getActivity(), ""+response.body().message, Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                        }
                    }

                }
                else GeneralFunctions.makeSnackbar(rlCheckoutLast, getResources().getString(R.string.serverIssue));
            }

            @Override
            public void onFailure(Call<GeneralModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(rlCheckoutLast, getResources().getString(R.string.netIssue));

            }
        });



    }
    private double amount=5;

    private void hitApplyCoupon(String code)
    {
        couponAppliedString=code;

        GeneralFunctions.showDialog(getActivity());
        Call<CouponModel> call =RestClient.get().applyCoupon(code,
                mPrefs.getString(Constants.UserServiceID,""),mPrefs.getString(Constants.MerchantId,""));
        call.enqueue(new Callback<CouponModel>() {
            @Override
            public void onResponse(Call<CouponModel> call, Response<CouponModel> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful())
                {

                    if (response.body().code.equals("401")) {
                        GeneralFunctions.tokenExpireAction(getActivity());
                    }
                    else if(response.body().success==1) {
                        nextLink = response.body().data.next_link;
                        if (mPrefs.getString(Constants.Automated, "").equals("YES") && MyApplication.Client.equals("haryana")) {
                            amount = response.body().data.remaining_amount;
                            verifyCoupon();

                        } else {
                            if (removed == 0) {
                                llCoupon.setVisibility(View.GONE);
                                rlRemoveCoupon.setVisibility(View.VISIBLE);
                                amount = response.body().data.remaining_amount;
                                tvCheckoutNext.setText(getResources().getString(R.string.pay) + getResources().getString(R.string.Rs) + response.body().data.remaining_amount);
                                mPrefs.save(Constants.OrderAmount, String.format("%.2f", response.body().data.remaining_amount));

                                removed = 1;
                            } else if (removed == 1) {
                                llCoupon.setVisibility(View.VISIBLE);
                                rlRemoveCoupon.setVisibility(View.GONE);
                                amount = 1;
                                tvCheckoutNext.setText(getResources().getString(R.string.pay) + getResources().getString(R.string.Rs) + mPrefs.getString(Constants.TotalCharge, ""));
                                mPrefs.save(Constants.OrderAmount, String.format("%.2f", Double.parseDouble(mPrefs.getString(Constants.TotalCharge, ""))));

                                removed = 0;
                            }
                            if (mPrefs.getString(Constants.FromAadhar, "").equals("YES")) {


                                //Todo: set these somewhere
                            /*Intent intent = new Intent(getActivity(), SuccessFulPaymentActivity.class);
                            startActivity(intent);
                            getActivity().finish();*/
                            } else {
                                //Todo: set these somewhere
                           /* Intent intent = new Intent(getActivity(), EsignDoneActivity.class);
                            startActivity(intent);
                            getActivity().finish();*/
                            }
                        }
                    }
                    else {
                        GeneralFunctions.makeSnackbar(rlCheckoutLast, response.body().message);
                    }
                }
                else GeneralFunctions.makeSnackbar(rlCheckoutLast, getResources().getString(R.string.serverIssue));
            }

            @Override
            public void onFailure(Call<CouponModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(rlCheckoutLast, getResources().getString(R.string.netIssue));
            }
        });

    }

    private void verifyCoupon()
    {
        GeneralFunctions.showDialog(getActivity());
        Call<CouponModel> call =RestClient.get().verifyCoupon(couponAppliedString,mPrefs.getString(Constants.MerchantId,""),
                mPrefs.getString(Constants.UserServiceID,""));

        call.enqueue(new Callback<CouponModel>() {
            @Override
            public void onResponse(Call<CouponModel> call, Response<CouponModel> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful())
                {

                    if (response.body().code.equals("401")) {
                        GeneralFunctions.tokenExpireAction(getActivity());
                    }
                    else if(response.body().success==1) {

                        if(amount==0)
                        {

                            //Todo: make a common function
                           /* if(mPrefs.getString(Constants.FromAadhar,"").equals("YES")) {

                            Intent intent = new Intent(getActivity(), SuccessFulPaymentActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                            }
                            else {

                            Intent intent = new Intent(getActivity(), EsignDoneActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                            }*/

                            if(!response.body().data.next_link.equals("success_upload")) {

                                Intent intent = new Intent(getActivity(), SuccessFulPaymentActivity.class);
                                startActivity(intent);
                                getActivity().finish();
                            }
                            else {

                                Intent intent = new Intent(getActivity(), EsignDoneActivity.class);
                                startActivity(intent);
                                getActivity().finish();
                            }
                        }
                        else
                            gotoPaymentWebview();
                    }
                    else {
                        GeneralFunctions.makeSnackbar(rlCheckoutLast, response.body().message);
                    }
                }
                else {
                    GeneralFunctions.makeSnackbar(rlCheckoutLast, getResources().getString(R.string.serverIssue));
                }
            }

            @Override
            public void onFailure(Call<CouponModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(rlCheckoutLast, getResources().getString(R.string.netIssue));
            }
        });
    }

    public void setPagerPosition(int position)
    {
        vpCheckout.setCurrentItem(position);
        scCheckoutScroll.setVisibility(View.GONE);
        cvPayDetails.setVisibility(View.GONE);
        cvPager.setVisibility(View.VISIBLE);
        vpCheckout.setVisibility(View.VISIBLE);
    }

    public int returnPagerPosition()
    {
        return vpCheckout.getCurrentItem();
    }

}
