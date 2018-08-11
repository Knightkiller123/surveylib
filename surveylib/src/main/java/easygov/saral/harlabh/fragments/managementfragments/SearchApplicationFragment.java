package easygov.saral.harlabh.fragments.managementfragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import easygov.saral.harlabh.R;
import easygov.saral.harlabh.models.responsemodels.filter.FilterApplicationModel;
import easygov.saral.harlabh.utils.GeneralFunctions;
import easygov.saral.harlabh.utils.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by apoorv on 07/12/17.
 */

public class SearchApplicationFragment extends Fragment implements View.OnClickListener {
    private ImageView ivSearchApplicationBack;
    private RelativeLayout rlSearchOption,rlSearchContainer;
    private TextView tvSearchApplication,tvSearchOption;
    private EditText etSearchOption;
    private PopupWindow pw;
    private String searchFor,searchOption;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_searchaplication,container,false);
        init(view);
        clickHandlers();
        return view;
    }

    private void clickHandlers() {
        rlSearchOption.setOnClickListener(this);
        tvSearchApplication.setOnClickListener(this);
    }

    private void init(View view) {
        searchFor="";
        searchOption="";
        ivSearchApplicationBack= view.findViewById(R.id.ivSearchApplicationBack);
        rlSearchOption= view.findViewById(R.id.rlSearchOption);
        rlSearchContainer= view.findViewById(R.id.rlSearchContainer);
        tvSearchOption= view.findViewById(R.id.tvSearchOption);
        tvSearchApplication= view.findViewById(R.id.tvSearchApplication);
        etSearchOption= view.findViewById(R.id.etSearchOption);


        ivSearchApplicationBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
        rlSearchContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                GeneralFunctions.hideSoftKeyboard(getActivity());
                return false;
            }
        });
    }


    private void initiatePopupWindow(View view) {

        try {
            LayoutInflater inflater = (LayoutInflater)getContext()
                    .getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.info_search,
                    (ViewGroup)view.findViewById(R.id.search_popup_element));
            pw = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
            pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
            TextView tvSearchByservice= layout.findViewById(R.id.tvSearchByservice);
            TextView tvSearchByscheme= layout.findViewById(R.id.tvSearchByscheme);
            TextView tvSearchByid= layout.findViewById(R.id.tvSearchByid);
            TextView tvSearchByName= layout.findViewById(R.id.tvSearchByName);
            RelativeLayout rlSearchOption= layout.findViewById(R.id.rlSearchOption);

            rlSearchOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pw.dismiss();
                }
            });
            tvSearchByservice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tvSearchOption.setText(getResources().getString(R.string.servicename));
                    searchOption="servicename";
                    pw.dismiss();
                }
            });

            tvSearchByscheme.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tvSearchOption.setText(getResources().getString(R.string.schemename));
                    searchOption="schemename";
                    pw.dismiss();
                }
            });

            tvSearchByid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tvSearchOption.setText(getResources().getString(R.string.applicantid));
                    searchOption="applicantid";
                    pw.dismiss();
                }
            });

            tvSearchByName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tvSearchOption.setText(getResources().getString(R.string.applicantname));
                    searchOption="applicantname";
                    pw.dismiss();
                }
            });


            pw.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    pw.dismiss();

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.rlSearchOption) {
            initiatePopupWindow(v);

        } else if (i == R.id.tvSearchApplication) {
            if (!etSearchOption.getText().toString().isEmpty()) {
                if (searchOption.length() > 2) {
                    hitSearchApi(etSearchOption.getText().toString(), searchOption);
                } else
                    GeneralFunctions.makeSnackbar(rlSearchContainer, getResources().getString(R.string.searchoptnselect));
            } else {
                GeneralFunctions.makeSnackbar(rlSearchContainer, getResources().getString(R.string.searchstr));
            }

        }
    }

    private void hitSearchApi(String string, String searchOption) {
        GeneralFunctions.showDialog(getActivity());
        Call<FilterApplicationModel> call= RestClient.get().searchApplication("10000",string,searchOption);
        call.enqueue(new Callback<FilterApplicationModel>() {
            @Override
            public void onResponse(Call<FilterApplicationModel> call, Response<FilterApplicationModel> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful())
                {
                    if(response.body().code==401)
                    {
                        GeneralFunctions.tokenExpireAction(getActivity());
                    }
                    else if(response.body().success==1)
                    {
                        if(response.body().data.objects!=null&&response.body().data.objects.size()>0) {
                            SearchApplicationResultFragment frag = new SearchApplicationResultFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("searchresult", new Gson().toJson(response.body().data));
                            frag.setArguments(bundle);
                            getFragmentManager().beginTransaction().add(R.id.rlSearchContainer, frag).addToBackStack(null).commit();
                        }
                        else GeneralFunctions.makeSnackbar(rlSearchContainer, getResources().getString(R.string.nodata));

                    }
                    else {
                        GeneralFunctions.makeSnackbar(rlSearchContainer, response.body().message);

                    }
                }
                else {
                    GeneralFunctions.makeSnackbar(rlSearchContainer, getResources().getString(R.string.serverIssue));

                }
            }

            @Override
            public void onFailure(Call<FilterApplicationModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                GeneralFunctions.makeSnackbar(rlSearchContainer, getResources().getString(R.string.netIssue));
            }
        });
    }
}
