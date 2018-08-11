package easygov.saral.harlabh.fragments.bottomfrags;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.TwoLineListItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import easygov.saral.harlabh.models.HeaderList;
import easygov.saral.harlabh.models.responsemodels.categoriesmodel.CategoryDetails;
import easygov.saral.harlabh.utils.Prefs;
import easygov.saral.harlabh.R;
import easygov.saral.harlabh.activity.SplashActivity;
import easygov.saral.harlabh.adapters.ExpandableListAdapter;
import easygov.saral.harlabh.models.CategoriesModel;
import easygov.saral.harlabh.models.ServicesDetails;
import easygov.saral.harlabh.models.responsemodels.categoriesmodel.Categories;
import easygov.saral.harlabh.utils.Constants;
import easygov.saral.harlabh.utils.DbHelper;
import easygov.saral.harlabh.utils.GeneralFunctions;
import easygov.saral.harlabh.utils.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



/**
 * A simple {@link Fragment} subclass.
 */
public class CategoriesFragment extends Fragment {

    private Snackbar bar;
    private RelativeLayout rlCategories;
    private DbHelper dbHelper;
    private ExpandableListView exlvCategories;
    private Prefs mPrefs;
    ExpandableListAdapter listAdapter;
    //ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
   // private  List<CategoriesModel> categories;
      int categoriesIds[];
    private  int lastExpandedPosition=1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_categories, container, false);
        init(view);
        hitCategoriesApi();
        return view;
    }

    private void init(View view) {
        rlCategories= view.findViewById(R.id.rlCategories);
        exlvCategories= view.findViewById(R.id.exlvCategories);
        mPrefs=Prefs.with(getActivity());
        dbHelper=new DbHelper(getActivity());

        //prepareListData();

        //listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);

        // setting list adapter
       // exlvCategories.setAdapter(listAdapter);
    }

    private void hitCategoriesApi() {
        GeneralFunctions.showDialog(getActivity());
        Map<String,String > map =new HashMap<>();
        map.put("size","1000");
        map.put("geography_id",mPrefs.getString(Constants.GeographyId,""));
        map.put(Constants.PARAM_BENEFICIARY_TYPE_ID,mPrefs.getString(Constants.BeneficiaryID,""));
        Call<Categories> call= RestClient.get().getServices(map);
        call.enqueue(new Callback<Categories>() {
            @Override
            public void onResponse(Call<Categories> call, Response<Categories> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful())
                {
                    if(response.body().code.equals("401"))
                    {
                        GeneralFunctions.tokenExpireAction(getActivity());
                    }

                   else if(response.body().success==1)
                    {
                        //categories=response.body();
                        dbHelper.clearDb();
                        dbHelper.addServiceCategoryDetails(response.body().data.objects);

                        checkList();
                    }
                    else {
                        bar = Snackbar.make(rlCategories, response.body().message, Snackbar.LENGTH_LONG)
                                .setAction("Dismiss", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // Handle user action
                                        bar.dismiss();
                                    }
                                });

                        bar.show();
                    }
                }
                else {

                    bar = Snackbar.make(rlCategories, getResources().getString(R.string.serverIssue), Snackbar.LENGTH_LONG)
                            .setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Handle user action
                                    bar.dismiss();
                                }
                            });

                    bar.show();
                }
            }

            @Override
            public void onFailure(Call<Categories> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                bar = Snackbar.make(rlCategories, getResources().getString(R.string.netIssue), Snackbar.LENGTH_LONG)
                        .setAction("Dismiss", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Handle user action
                                bar.dismiss();
                            }
                        });

                bar.show();
            }
        });
    }

    private void checkList() {
        List<CategoryDetails> list =new ArrayList<>();
        list=  dbHelper.getServiceCategoryIds();
        //categoriesIds=new int[list.size()];
        HashMap<String, List<ServicesDetails>> listDataChilds = new HashMap<String, List<ServicesDetails>>();
        //categories = new ArrayList<>();
        List<ServicesDetails> sDetails =new ArrayList<>();

        List<HeaderList> headers=new ArrayList<>();
        List<String> images=new ArrayList<>();
        CategoriesModel catObj=new CategoriesModel();
        for(int i=0; i<list.size();i++)
        {   /*catObj.categoryId=list.get(i).category.id;
            catObj.categoryName=list.get(i).category.name;
            catObj.isScheme=list.get(i).category.scheme;
            catObj.geoDistName=list.get(i).geography_district_name;
            catObj.geoId=list.get(i).geography_id;*/
            HeaderList asd=new HeaderList();
            asd.name=list.get(i).category.name;
            asd.img=list.get(i).image;
            headers.add(asd);
           // images.add(list.get(i).category.name);
            sDetails=dbHelper.getSubCategories(list.get(i).category.id.toString());
           // catObj.list=sDetails;
          //  categories.add(catObj);

            listDataChilds.put(headers.get(i).name, sDetails);

        }



        listAdapter = new ExpandableListAdapter(getActivity(), headers, listDataChilds);

        // setting list adapter
         exlvCategories.setAdapter(listAdapter);

        exlvCategories.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {

                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    exlvCategories.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });
    }


    public static Fragment newInstance() {
        return new CategoriesFragment();
    }



    @Override
    public void onStop() {
        dbHelper.close();
        super.onStop();
    }
}
