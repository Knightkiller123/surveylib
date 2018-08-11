package easygov.saral.harlabh.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import easygov.saral.harlabh.fragments.survey.SecondSurveyFragment;

/**
 * Created by apoorv on 31/08/17.
 */

public class SeconPagerAdapter extends FragmentStatePagerAdapter {
    private static int NUM_ITEMSS = 1;
    public SeconPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        return SecondSurveyFragment.newInstance(position);

    }

    public void setCount()
    {
        NUM_ITEMSS++;
        notifyDataSetChanged();
    }



    @Override
    public int getCount() {
        //
        return NUM_ITEMSS;
    }
}
