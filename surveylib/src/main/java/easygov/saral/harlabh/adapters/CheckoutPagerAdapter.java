package easygov.saral.harlabh.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


import easygov.saral.harlabh.R;
import easygov.saral.harlabh.fragments.checkout_pager_fragments.CommunicationFragment;
import easygov.saral.harlabh.fragments.checkout_pager_fragments.PayFragment;
import easygov.saral.harlabh.fragments.checkout_pager_fragments.BillingAddressFragment;

/**
 * Created by apoorv on 01/08/17.
 */

public class CheckoutPagerAdapter extends FragmentStatePagerAdapter{
    String[] title = {
            "Communication Address", "Billing Address","Payment",};
    Fragment fragment,fragment1;
    private Context context;
    public CheckoutPagerAdapter(FragmentManager fm, int tabCount, Context context) {
        super(fm);
        this.context=context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                fragment= CommunicationFragment.newInstance("FirstFragment, Instance 1");
                return fragment;

            case 1: fragment1=  PayFragment.newInstance("SecondFragment, Instance 1");
                return fragment1;
/*
            case 1: fragment1=  BillingAddressFragment.newInstance("SecondFragment, Instance 1");
                    return fragment1;

            case 2: return PayFragment.newInstance("FirstFragment, Instance 1");*/

           // case 2: return BillingFragment.newInstance("SecondFragment, Instance 1");

            default:return PayFragment.newInstance("FirstFragment, Instance 1") ;

        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(position==0)
        {
            return context.getResources().getString(R.string.commaddress);
        }
       else if(position==1)
        {
            return context.getResources().getString(R.string.billing);
        }

        else
        {
            return context.getResources().getString(R.string.payments);
        }


        //return title[position];
    }

    public Fragment getFragment(int key) {
        if (key==0)
        return fragment;

        else return fragment1;
    }
}
