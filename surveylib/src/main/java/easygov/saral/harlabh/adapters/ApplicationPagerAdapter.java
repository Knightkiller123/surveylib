package easygov.saral.harlabh.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import easygov.saral.harlabh.R;
import easygov.saral.harlabh.fragments.application_pager_fragments.ApplicationDetailFragment;
import easygov.saral.harlabh.fragments.application_pager_fragments.AttachmentFragment;

/**
 * Created by apoorv on 01/08/17.
 */

public class ApplicationPagerAdapter extends FragmentStatePagerAdapter {

    private Fragment attachmentFragmentIns,applicationDetailFragmentIns;
    //private ApplicationDetailFragment ;
    private Context context;
   /* String[] title = {context.getResources().getString(R.string.attachments),context.getResources().getString(R.string.applicationdetails)
            ,};*/
    public ApplicationPagerAdapter(FragmentManager fm, int tabCount, Context context) {
        super(fm);
        this.context=context;

    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:

                return AttachmentFragment.newInstance("FirstFragment, Instance 1");
            case 1:

                return ApplicationDetailFragment.newInstance("SecondFragment, Instance 1");
            default:
                return AttachmentFragment.newInstance("FirstFragment, Instance 1");

        }

    }

    public Fragment returnAttachmentInstance()
    {
        return attachmentFragmentIns;
    }

    public Fragment returnApplicationInstance()
    {
        return applicationDetailFragmentIns;
    }
    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        if(position==0)
        {
            return context.getResources().getString(R.string.attachments);
        }
        else {
            return context.getResources().getString(R.string.applicationdetails);
        }
        //return title[position];
    }
}
