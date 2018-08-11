package easygov.saral.harlabh.fragments.bottomfrags;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import easygov.saral.harlabh.R;
import easygov.saral.harlabh.activity.WebViewActivity;
import easygov.saral.harlabh.fragments.StaticPagesFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoreFragment extends Fragment implements View.OnClickListener {


    public MoreFragment() {
        // Required empty public constructor
    }
private ImageView ivCross;
    private RelativeLayout rlMore1;
    private TextView tvAbout,tvContact,tvFaq,tvPrivacy,tvTnc;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_more, container, false);
        init(view);
        clickHandlers();
        return view;
    }

    private void clickHandlers() {
        tvAbout.setOnClickListener(this);

        tvContact.setOnClickListener(this);
        tvFaq.setOnClickListener(this);
        tvPrivacy.setOnClickListener(this);
        tvTnc.setOnClickListener(this);


    }

    private void init(View view) {
        tvAbout= view.findViewById(R.id.tvAbout);
        ivCross= view.findViewById(R.id.ivCross);
        rlMore1= view.findViewById(R.id.rlMore1);
        tvContact= view.findViewById(R.id.tvContact);
        tvFaq= view.findViewById(R.id.tvFaq);
        tvPrivacy= view.findViewById(R.id.tvPrivacy);
        tvTnc= view.findViewById(R.id.tvTnc);
        ivCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getFragmentManager().popBackStack();
            }
        });

    }

    public static Fragment newInstance() {
        MoreFragment fragment =new MoreFragment();
        return fragment;
    }


    private void gotoWebview( String heading,String s)
    {
        Bundle bundle = new Bundle();
        bundle.putString("url", s);
        bundle.putString("heading", heading);
        Intent intent =new Intent(getActivity(), WebViewActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {

        int i = v.getId();
        if (i == R.id.tvAbout) {
            gotoWebview("About Us", "https://www.easygov.co.in/about/");

        } else if (i == R.id.tvContact) {
            gotoWebview("Contact Us", "https://www.easygov.co.in/contact-us/");

        } else if (i == R.id.tvFaq) {
            gotoWebview("FAQ", "https://www.easygov.co.in/faq/");

        } else if (i == R.id.tvPrivacy) {
            gotoWebview("Privacy Policy", "https://www.easygov.co.in/privacy/");

        } else if (i == R.id.tvTnc) {// gotoWebview("Terms and Conditions","https://www.easygov.co.in/terms-and-conditions/");
            openStaticPages("tnc");

        }
    }

    private void openStaticPages(String a)
    {
        StaticPagesFragment fragment =new StaticPagesFragment();
        Bundle bundle = new Bundle();
        bundle.putString("stringToMatch", a);
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction().add(R.id.rlMore1, fragment).addToBackStack(null).commit();
    }
}
