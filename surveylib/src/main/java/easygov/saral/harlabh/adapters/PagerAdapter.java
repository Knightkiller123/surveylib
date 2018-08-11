package easygov.saral.harlabh.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import easygov.saral.harlabh.fragments.survey.SurveyFragment;

/**
 * Created by apoorv on 31/08/17.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    private static int NUM_ITEMS = 1;
    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        return SurveyFragment.newInstance(position);

    }

    public void setCount()
    {
        NUM_ITEMS++;
        notifyDataSetChanged();
    }



    @Override
    public int getCount() {
        //
        return NUM_ITEMS;
    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
