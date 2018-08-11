package easygov.saral.harlabh.fragments;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import easygov.saral.harlabh.R;
import easygov.saral.harlabh.activity.HomeActivity;
import easygov.saral.harlabh.activity.SplashActivity;
import easygov.saral.harlabh.utils.Constants;
import easygov.saral.harlabh.utils.GeneralFunctions;
import easygov.saral.harlabh.utils.MyApplication;
import easygov.saral.harlabh.utils.Prefs;

/**
 * Created by vaishali on 23/02/18.
 */

public class ChangeLanguageFragment extends Fragment {

    private RadioButton rbEnglish, rbHindi;
    private RelativeLayout rlLanguageConti;
    private Prefs mPrefs;
    private TextView tvLangHeading, tvLangContinue;
    private RadioGroup rbLanguage;
    private RelativeLayout rlLangFrag;
    private String langPre;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_language, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        rbEnglish = view.findViewById(R.id.rbEnglish);
        rbHindi = view.findViewById(R.id.rbHindi);
        mPrefs = Prefs.with(getActivity());
        rlLangFrag = view.findViewById(R.id.rlLangFrag);
        rbLanguage = view.findViewById(R.id.rbLanguage);
        rlLanguageConti = view.findViewById(R.id.rlLanguageConti);
        tvLangContinue = view.findViewById(R.id.tvLangContinue);
        tvLangHeading = view.findViewById(R.id.tvLangHeading);
        if (mPrefs.getString(Constants.Language,"en").equals("en")) {
            rbEnglish.setChecked(true);
            ((HomeActivity) getContext()).updateViews("en");
            setStrings();
        }
        else {
            rbHindi.setChecked(true);
            ((HomeActivity) getContext()).updateViews("hi");
            setStrings();
        }


       // mPrefs.save(Constants.Language, "en");

       // ((HomeActivity) getContext()).updateViews("en");

        setStrings();
        rbLanguage.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.rbEnglish) {
                    mPrefs.save(Constants.Language, "en");

                    ((HomeActivity) getContext()).updateViews("en");
                    setStrings();
                } else {
                    mPrefs.save(Constants.Language, "hi");

                    ((HomeActivity) getContext()).updateViews("hi");
                    setStrings();
                }

            }
        });

        rlLanguageConti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
                startActivity(getActivity().getIntent());
               // getActivity().onBackPressed();
            }

        });
    }

    private void setStrings() {
        tvLangContinue.setText(getResources().getString(R.string.contin));
        tvLangHeading.setText(getResources().getString(R.string.selectLanguage));
        MyApplication.dismiss=getResources().getString(R.string.dismiss);
    }
}
