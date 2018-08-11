package easygov.saral.harlabh.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;




import easygov.saral.harlabh.R;
import easygov.saral.harlabh.activity.SplashActivity;
import easygov.saral.harlabh.adapters.AboutUsAdapter;
import easygov.saral.harlabh.models.responsemodels.filterstatus.FilterResponse;
import easygov.saral.harlabh.utils.GeneralFunctions;
import easygov.saral.harlabh.utils.Prefs;
import easygov.saral.harlabh.utils.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class StaticPagesFragment extends Fragment {

    private RecyclerView rvStaticForms;
    private TextView tvStaticPageHeading;
    private ImageView ivStaticBack;

    private String stringToMatch="";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_static_pages, container, false);
        init(view);
        return view;
    }

    private void init(View view) {

        rvStaticForms= view.findViewById(R.id.rvStaticForms);
        tvStaticPageHeading= view.findViewById(R.id.tvStaticPageHeading);
        ivStaticBack= view.findViewById(R.id.ivStaticBack);
        rvStaticForms.setLayoutManager(new LinearLayoutManager(getActivity()));
        ivStaticBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });



        Bundle bundle =new Bundle();
        bundle=getArguments();
        if(bundle!=null) {
            try {
                stringToMatch = bundle.getString("stringToMatch");

                if(stringToMatch.equals("tnc"))
                    //  setTnc();tnc
                    hitApiAboutUs("tnc", getResources().getString(R.string.tnc));

                else if (stringToMatch.equals("faq"))
                    //  setfaq();
                    hitApiAboutUs("faq", getResources().getString(R.string.faq));

                else if(stringToMatch.equals("about"))
                {
                    hitApiAboutUs("aboutus",getResources().getString(R.string.aboutus));
                    //setwhowe();
                }

            } catch (Exception e) {
            }
        }
        //setTnc();
//setfaq();
        //setprivacy();


    }

    private void hitApiAboutUs(String str, String title) {
        tvStaticPageHeading.setText(title);

        RestClient.get().getABoutUsData(str).enqueue(new Callback<FilterResponse>() {
            @Override
            public void onResponse(Call<FilterResponse> call, Response<FilterResponse> response) {
                GeneralFunctions.dismissDialog();

                if(response.isSuccessful())
                {
                    if(response.body().code==401)
                    {
                        GeneralFunctions.tokenExpireAction(getActivity());
                    }
                    else if(response.body().success==1)
                    {
                        rvStaticForms.setAdapter(new AboutUsAdapter(getActivity(),response.body().data.objects));
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
            public void onFailure(Call<FilterResponse> call, Throwable t) {

            }
        });
    }








}
