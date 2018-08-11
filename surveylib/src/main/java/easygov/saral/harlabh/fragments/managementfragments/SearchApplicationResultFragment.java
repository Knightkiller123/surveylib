package easygov.saral.harlabh.fragments.managementfragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import easygov.saral.harlabh.R;
import easygov.saral.harlabh.adapters.FilterApplicationAdapter;
import easygov.saral.harlabh.models.responsemodels.filter.FilterData;


public class SearchApplicationResultFragment extends Fragment {
    private FilterData filteredData;
    private ImageView ivSearchResultBack;
    private RecyclerView rvSearchResult;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_search_application_result, container, false);
        init(view);


        return view;


    }

    private void init(View view) {
        ivSearchResultBack= view.findViewById(R.id.ivSearchResultBack);
        rvSearchResult= view.findViewById(R.id.rvSearchResult);
        Bundle bundle=new Bundle();
        bundle=getArguments();
        if(bundle!=null)
        {
            filteredData=new Gson().fromJson(bundle.getString("searchresult"),new TypeToken<FilterData>() {}.getType());
            rvSearchResult.setLayoutManager(new LinearLayoutManager(getActivity()));
            rvSearchResult.setAdapter(new FilterApplicationAdapter(getActivity(),filteredData.objects));

        }

        ivSearchResultBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
    }

}
