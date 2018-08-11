package easygov.saral.harlabh.fragments.storyboardfrags;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


import easygov.saral.harlabh.models.surveypaging.StartSurveyPaging;
import easygov.saral.harlabh.utils.Prefs;
import easygov.saral.harlabh.R;
import easygov.saral.harlabh.activity.FirstSurveyActivity;
import easygov.saral.harlabh.adapters.PagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class YourStoryDetailsFragment extends Fragment {


private RecyclerView rvStoryDetails;

   // private List<DummyModel> list;
    private ViewPager vpSurvey;

    private StartSurveyPaging object ;
    private Prefs mPrefs;
    public static int count=0;
    String s="";
    private PagerAdapter adapter;
    private List<StartSurveyPaging> list=new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_your_story_details, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
       /* list=new ArrayList<>();
        DummyModel obj = new DummyModel();
        obj.question="Your tanent is";

        List<String> gender=new ArrayList<>();
        gender.add("Male");
        gender.add("female");

        obj.options=gender;
        obj.type=0;
        list.add(obj);

        DummyModel obj1 = new DummyModel();
        obj1.question="Your tanent is";

        List<String> states=new ArrayList<>();
        states.add("asd");
        states.add("dfg");
        states.add("qwe");
        states.add("cvb");
        states.add("rty");
        states.add("tyu");*/
       // LayoutInflater inflater =(LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //View views = inflater.inflate(R.layout.activity_start_story, null);
        //view.setLayoutParams(new LayoutParams(ViewPager.LayoutParams.WRAP_CONTENT, ViewPager.LayoutParams.WRAP_CONTENT));
        Intent i = new Intent();
        i.setClass(view.getContext(), FirstSurveyActivity.class);
        view.getContext().startActivity(i);

        /*object=new StartSurvey();
        mPrefs=Prefs.with(getActivity());
        vpSurvey= (ViewPager) view.findViewById(R.id.vpSurvey);
        vpSurvey.setOffscreenPageLimit(20);
        vpSurvey.setCurrentItem(0);
        vpSurvey.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        adapter=new PagerAdapter(getFragmentManager());*/
       // if(count==0)
       // hitApi();
        /*obj1.options=states;
        obj1.type=1;
        list.add(obj1);*/

        //rvStoryDetails= (RecyclerView) view.findViewById(R.id.rvStoryDetails);
        //rvStoryDetails.setLayoutManager(new LinearLayoutManager(getActivity()));
        //rvStoryDetails.setAdapter(new YourStoryDetailsAdapter(getContext(),list));
    }

   /* private void hitApi() {
        GeneralFunctions.showDialog(getActivity());
        Call<StartSurvey> call= RestClient.get().startSurvey("true","false","1","1");

        call.enqueue(new Callback<StartSurvey>() {
            @Override
            public void onResponse(Call<StartSurvey> call, Response<StartSurvey> response) {
                GeneralFunctions.dismissDialog();
                if(response!=null)
                {
                    object=response.body();
                    list.add(object);
                    s= Constants.savedResponses+""+count;
                    mPrefs.save(s,new Gson().toJson(list));
                    vpSurvey.setAdapter(adapter);

                    //vpSurvey.setOffscreenPageLimit(1);
                    //rvEntitlement.setAdapter(surveyAdapter);
                }
            }

            @Override
            public void onFailure(Call<StartSurvey> call, Throwable t) {
                GeneralFunctions.dismissDialog();
            }
        });
    }*/

   /* public  void setPagerPage(int index)
    {

        adapter=new PagerAdapter(getFragmentManager());
        adapter.setCount();
        adapter.notifyDataSetChanged();
        vpSurvey.setCurrentItem(index);


    }*/


   /* @Override
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
                    // handle back button's click listener
                    if (vpSurvey.getCurrentItem() == 0) {
                        return false;
                    } else {

                        count--;

                        adapter.notifyDataSetChanged();
                        vpSurvey.setCurrentItem(vpSurvey.getCurrentItem() - 1);



                    }
                    return true;
                }
                return false;
            }
        });

    }*/
}
