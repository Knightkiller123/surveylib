package easygov.saral.harlabh.fragments.bottomfrags;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import easygov.saral.harlabh.R;
import easygov.saral.harlabh.activity.FirstSurveyActivity;
import easygov.saral.harlabh.activity.SelectServiceActivity;
import easygov.saral.harlabh.activity.StateSelectActivity;
import easygov.saral.harlabh.utils.Constants;
import easygov.saral.harlabh.utils.Prefs;
import static easygov.saral.harlabh.adapters.SurveyAdapter.list;
import static easygov.saral.harlabh.fragments.storyboardfrags.YourStoryDetailsFragment.count;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {

    private TextView tvSelectedTerritory,tvIWant,tvMyself,tvFamilyMember,tvSomeoneElse;
    private RelativeLayout textViewGetStarted;
    private CardView cvFor;

    private ImageView ivConfused;
    private List<String> services=new ArrayList<>();

    private Prefs mPrefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_home, container, false);
        init(view);
        clickHandlers();
        //setActv();
        return view;
    }



    private void clickHandlers() {
        tvSelectedTerritory.setOnClickListener(this);
        tvIWant.setOnClickListener(this);
        textViewGetStarted.setOnClickListener(this);
        ivConfused.setOnClickListener(this);
        tvMyself.setOnClickListener(this);
        tvFamilyMember.setOnClickListener(this);
        tvSomeoneElse.setOnClickListener(this);


    }

    private void init(View view) {
        mPrefs=Prefs.with(getContext());

        if(mPrefs.getString(Constants.IS_USER_FIRST_TIME,"").equals("yes"))
        {


            mPrefs.save(Constants.Applyingfor,"myself");
            //applyingforStartSurvey("myself");
            mPrefs.save(Constants.FromServices,"no");
            dontKnowStartSurvey();
          /*  Intent a=new Intent(getContext(), StaticFormActivity.class);
            startActivity(a);*/

        }

        tvSelectedTerritory= view.findViewById(R.id.tvSelectedTerritory);
        // actvSurvey= (AutoCompleteTextView) view.findViewById(R.id.actvSurvey);
        tvIWant= view.findViewById(R.id.tvIWant);
        textViewGetStarted= view.findViewById(R.id.textViewGetStarted);
        ivConfused= view.findViewById(R.id.ivConfused);
        tvSelectedTerritory.setText(mPrefs.getString(Constants.GeographySelected,""));
        cvFor= view.findViewById(R.id.cvFor);
        tvMyself= view.findViewById(R.id.tvMyself);
        tvFamilyMember= view.findViewById(R.id.tvFamilyMember);
        tvSomeoneElse= view.findViewById(R.id.tvSomeoneElse);



        //startAnimation();

       /* ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500);
//

        /*FancyShowCaseView fancyShowCaseView1=  new FancyShowCaseView.Builder(getActivity())
                .focusOn(tvSelectedTerritory).closeOnTouch(true).focusShape(FocusShape.ROUNDED_RECTANGLE).focusBorderColor(Color.BLACK)
                .title("Focus on View").disableFocusAnimation()
                .build();

        FancyShowCaseView fancyShowCaseView2=  new FancyShowCaseView.Builder(getActivity())
                .focusOn(tvIWant).closeOnTouch(true).focusShape(FocusShape.ROUNDED_RECTANGLE).focusBorderColor(Color.BLACK)
                .title("Focus on View").disableFocusAnimation()
                .build();

        FancyShowCaseView fancyShowCaseView3=  new FancyShowCaseView.Builder(getActivity())
                .focusOn(cvFor).closeOnTouch(true).focusShape(FocusShape.ROUNDED_RECTANGLE).focusBorderColor(Color.BLACK)
                .title("Focus on View").disableFocusAnimation()
                .build();


        FancyShowCaseQueue mQueue = new FancyShowCaseQueue()
                .add(fancyShowCaseView1)
                .add(fancyShowCaseView2)
                .add(fancyShowCaseView3);

        mQueue.show();*/






    }

   /* private void startAnimation() {
        ShowcaseConfig config = new ShowcaseConfig();

        config.setShape(new RectangleShape(200, 200));
        config.setDelay(1500);
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity(), "116");
        sequence.setConfig(config);
        sequence.addSequenceItem(tvSelectedTerritory,
                "This is button one", "GOT IT");

       *//* new MaterialShowcaseView.Builder(getActivity()).setShape(new RectangleShape(200, 200))
                .setTarget(tvSelectedTerritory)
                .setDismissText("GOT IT")
                .setContentText("This is some amazing feature you should know about")
                .setDelay(1000).show(); *//*// optional but starting animations immediately in onCreate can make them choppy


        sequence.addSequenceItem(tvIWant,"This is button two", "GOT IT");
        sequence.addSequenceItem(cvFor,"This is button three", "GOT IT");
        sequence.start();

    }*/

    public static Fragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onClick(View v) {

        mPrefs.save("sgm_new_id","00");
        Intent intent= null;
        Bundle bundle = new Bundle();

        int i = v.getId();
        if (i == R.id.tvSelectedTerritory) {
            intent = new Intent(getActivity(), StateSelectActivity.class);
            startActivity(intent);
            intent.putExtra("animate", "yes");
            getActivity().overridePendingTransition(R.anim.animateup_activity, R.anim.animatedown_activity);
            getActivity().finish();

        } else if (i == R.id.tvIWant) {
            intent = new Intent(getActivity(), SelectServiceActivity.class);
// Pass data object in the bundle and populate details activity.
            // intent.putExtra("asd", contact);
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(getActivity(), tvIWant, "simple_activity_transition");
            startActivity(intent, options.toBundle());

        } else if (i == R.id.textViewGetStarted) {
            dontKnowStartSurvey();

        } else if (i == R.id.ivConfused) {
            dontKnowStartSurvey();

        } else if (i == R.id.tvMyself) {
            mPrefs.save(Constants.Applyingfor, "myself");
            mPrefs.save(Constants.ProfileId, "");
            mPrefs.save(Constants.RestartSurvey, "no");
            mPrefs.getString(Constants.FamilyMyselfCase, "no");

            applyingforStartSurvey("myself");


        } else if (i == R.id.tvFamilyMember) {
            mPrefs.save(Constants.Applyingfor, "family");
            mPrefs.save(Constants.RestartSurvey, "no");
            mPrefs.getString(Constants.FamilyMyselfCase, "no");

            applyingforStartSurvey("family");


        } else if (i == R.id.tvSomeoneElse) {
            mPrefs.save(Constants.Applyingfor, "someoneelse");
            mPrefs.save(Constants.RestartSurvey, "no");
            mPrefs.getString(Constants.FamilyMyselfCase, "no");

            applyingforStartSurvey("someoneelse");


        }
    }

    private void dontKnowStartSurvey()
    {
        Intent intent= null;
        Bundle bundle = new Bundle();
        intent = new Intent(getActivity(), FirstSurveyActivity.class);
        bundle.putString("is_category","0");
        bundle.putString("is_scheme", "1");
        bundle.putString("knowing","no");
        bundle.putString("applyingfr","none");
        mPrefs.save(Constants.IsScheme, "YES");
        mPrefs.save(Constants.SecontActivityText, getContext().getResources().getString(R.string.schemes));
        if(list!=null)
        {
            list.clear();
            list=new ArrayList<>();
            count=0;
        }
        bundle.putString("selected_id","0");
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void applyingforStartSurvey(String applyingfor)
    {
        mPrefs.save(Constants.FromServices,"no");
        //mPrefs.save(Constants.SurveyId,response.body().data.survey_id);
        Intent intent= null;
        Bundle bundle = new Bundle();
        intent = new Intent(getActivity(), FirstSurveyActivity.class);
        bundle.putString("is_category","0");
        bundle.putString("is_scheme", "1");
        bundle.putString("knowing","yes");
        bundle.putString("applyingfr",applyingfor);
        mPrefs.save(Constants.IsScheme, "YES");
        mPrefs.save(Constants.SecontActivityText, getContext().getResources().getString(R.string.schemes));
        if(list!=null)
        {
            list.clear();
            list=new ArrayList<>();
            count=0;
        }
        bundle.putString("selected_id","0");
        intent.putExtras(bundle);
        startActivity(intent);
        // getSurveyId(applyingfor);

    }

   /* @Override
    public void onResume() {
       // Toast.makeText(getContext(), "resume", Toast.LENGTH_SHORT).show();
        super.onResume();
        if(mPrefs.getString(Constants.FormFilled,"").equals("YES"))
        {
            mPrefs.save(Constants.FormFilled,"NO");

        }
    }*/




   /* private void getSurveyId(final String myself)
    {
        mPrefs.remove(Constants.UserServiceID);
        //GeneralFunctions.showDialog(getActivity());
        Call<GeneralModel> call= RestClient.get().getSurveyId(myself, mPrefs.getString(Constants.BeneficiaryID,""));
        call.enqueue(new Callback<GeneralModel>() {
            @Override
            public void onResponse(Call<GeneralModel> call, Response<GeneralModel> response) {
                GeneralFunctions.dismissDialog();

                if(response.isSuccessful())
                {
                    if(response.body().code.equals("401"))
                    {
                        Intent intent = new Intent(getActivity(),
                                SplashActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        GeneralFunctions.clearPrefs(mPrefs);
                        startActivity(intent);
                        Toast.makeText(getActivity(), "Session Expired! Please login again", Toast.LENGTH_SHORT).show();
                    }

                    if(response.body().success==1)
                    {
                        mPrefs.save(Constants.SurveyId,response.body().data.survey_id);
                        Intent intent= null;
                        Bundle bundle = new Bundle();;
                        intent = new Intent(getActivity(), FirstSurveyActivity.class);
                        bundle.putString("is_category","0");
                        bundle.putString("is_scheme", "1");
                        bundle.putString("knowing","yes");
                        bundle.putString("applyingfr",myself);
                        mPrefs.save(Constants.IsScheme, "YES");
                        mPrefs.save(Constants.SecontActivityText, getContext().getResources().getString(R.string.schemes));
                        if(list!=null)
                        {
                            list.clear();
                            list=new ArrayList<>();
                            count=0;
                        }
                        bundle.putString("selected_id","0");
                        intent.putExtras(bundle);
                        startActivity(intent);

                    }

                    else {
                        Toast.makeText(getActivity(), response.body().message, Toast.LENGTH_SHORT).show();



                    }

                }
                else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.serverIssue), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeneralModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();

                Toast.makeText(getActivity(), getResources().getString(R.string.netIssue), Toast.LENGTH_SHORT).show();
            }
        });
    }
*/

}
