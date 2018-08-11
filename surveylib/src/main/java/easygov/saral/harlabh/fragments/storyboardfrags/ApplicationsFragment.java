package easygov.saral.harlabh.fragments.storyboardfrags;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import easygov.saral.harlabh.utils.CustomViewPager;
import easygov.saral.harlabh.R;
import easygov.saral.harlabh.adapters.ApplicationPagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class ApplicationsFragment extends Fragment implements TabLayout.OnTabSelectedListener {


    private CustomViewPager vpApplication;
    private TabLayout tabLayout;
    private ApplicationPagerAdapter applicationPagerAdapter;
    String TabFragmentB;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_applications, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        vpApplication= view.findViewById(R.id.vpApplication);
        vpApplication.setPagingEnabled(false);
        tabLayout = view.findViewById(R.id.tabLayout);

        //Adding the tabs using addTab() method
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.attachments)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.applicatiodetails)));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

       // UIUtils.setCustomFont(tabLayout, getActivity().getAssets());


        // tabLayout.setEnabled(false);


        LinearLayout tabStrip = ((LinearLayout)tabLayout.getChildAt(0));
        tabStrip.setEnabled(false);
        for(int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setClickable(false);
           /* if (tabStrip.getChildAt(i) instanceof TextView) {
                ((TextView)tabStrip.getChildAt(i)).setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/montserrat_regular.ttf"));
            }*/
        }


        applicationPagerAdapter=new ApplicationPagerAdapter(getFragmentManager(),tabLayout.getTabCount(),getContext());
        vpApplication.setAdapter(applicationPagerAdapter);

        tabLayout.addOnTabSelectedListener(this);

        /*vpApplication.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position==1)
                {
                    ApplicationDetailFragment fragment = (ApplicationDetailFragment) applicationPagerAdapter.returnApplicationInstance();
                    fragment.hitReadFieldsforUser();
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });*/


    }

    public int returnPagerPosition()
    {
        return vpApplication.getCurrentItem();
    }

    public void setPagerPosition(int position)
    {
        vpApplication.setCurrentItem(position);
    }
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
       // vpApplication.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }





    /*@Override
    public void onResume() {
        super.onResume();

        if(getView() == null){
            return;
        }

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){

                   getActivity().finish();
                    return true;
                }
                return false;
            }
        });
    }*/



}
