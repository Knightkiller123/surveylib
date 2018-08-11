package easygov.saral.harlabh.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import easygov.saral.harlabh.models.responsemodels.categoriesmodel.Categories;
import easygov.saral.harlabh.models.responsemodels.categoriesmodel.CategoryDetails;
import easygov.saral.harlabh.utils.Prefs;
import easygov.saral.harlabh.R;
import easygov.saral.harlabh.adapters.WantServicesAdapter;
import easygov.saral.harlabh.models.ServicesDetails;
import easygov.saral.harlabh.utils.Constants;
import easygov.saral.harlabh.utils.DbHelper;
import easygov.saral.harlabh.utils.GeneralFunctions;
import easygov.saral.harlabh.utils.RecyclerBottomListener;
import easygov.saral.harlabh.utils.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static easygov.saral.harlabh.adapters.SurveyAdapter.list;
import static easygov.saral.harlabh.fragments.storyboardfrags.YourStoryDetailsFragment.count;

//import static easygovdemo.easygov.com.easygov.R.id.actvSurvey;

public class SelectServiceActivity extends AppCompatActivity implements View.OnClickListener {
    private List<String> services=new ArrayList<>();
    private ImageView ivCrossSelect;
    private AutoCompleteTextView actvSelect;
    private RecyclerView rvWatServices;
    private Prefs mPrefs;
    private DbHelper dbHelper;
    private EditText etSelect;
    private ImageView ivSchemeCross;
    private RelativeLayout rlWantSelect,rlNoData;
    private ProgressBar pbSet1;
    private TextView tvCatSelect,tvgetStarted;
    private List<CategoryDetails> lists=new ArrayList<>();
    private Categories catObj;
    private WantServicesAdapter wantadapter;

    private int TOTAL_PAGES=0;
    private int CURRENT_PAGE=0;

    private Snackbar bar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_service);

        //  setStates();
        init();
        clickHandlers();

    }





    private void clickHandlers() {
        ivCrossSelect.setOnClickListener(this);
        ivSchemeCross.setOnClickListener(this);
        tvCatSelect.setOnClickListener(this);
        tvgetStarted.setOnClickListener(this);
    }

    private void init() {
        ivCrossSelect= findViewById(R.id.ivCrossSelect);
        actvSelect= findViewById(R.id.actvSelect);
        services.add("Verification");
        services.add("Grievance");
        services.add("Scholarship");
        services.add("anything");
        tvCatSelect= findViewById(R.id.tvCatSelect);
        mPrefs=Prefs.with(this);
        rlNoData= findViewById(R.id.rlNoData);
        ivSchemeCross= findViewById(R.id.ivSchemeCross);
        etSelect= findViewById(R.id.etSelect);
        etSelect.setSelected(false);
        dbHelper=new DbHelper(this);

        tvgetStarted= findViewById(R.id.tvgetStarted);
        ArrayAdapter<String> stradapter = new ArrayAdapter<String>(this,android.R.layout.select_dialog_item,services);
        rlWantSelect= findViewById(R.id.rlWantSelect);


        rvWatServices= findViewById(R.id.rvWatServices);
        rvWatServices.setLayoutManager(new LinearLayoutManager(this));

        mPrefs.save("sgm_new_id","00");

        rvWatServices.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                GeneralFunctions.hideSoftKeyboard(SelectServiceActivity.this);
                return false;
            }
        });
        wantadapter=new WantServicesAdapter(this,lists);

        hitCategoriesApi();

        setSearch();


    }



    private void setSearch() {
        etSelect.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()>0)
                {
                    ivSchemeCross.setVisibility(View.VISIBLE);
                }
                else {
                    ivSchemeCross.setVisibility(View.GONE);
                    tvCatSelect.setVisibility(View.GONE);
                    filterServiceCategories.clear();
                    filterServiceCategories.addAll(lists);
                    rlNoData.setVisibility(View.GONE);
                    rvWatServices.setVisibility(View.VISIBLE);
                    rvWatServices.setAdapter(new WantServicesAdapter(SelectServiceActivity.this,filterServiceCategories));

                }
                if(String.valueOf(charSequence).length()>3)
                    filter(String.valueOf(charSequence));

                if(String.valueOf(charSequence).length()>2)
                    filterHeaders(String.valueOf(charSequence));

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(list!=null)
        {
            list.clear();
            list=new ArrayList<>();
            count=0;
        }



    }

    private void hitCategoriesApi() {
        GeneralFunctions.showDialog(this);
        Map<String,String > map =new HashMap<>();
        map.put("size","1000");
        map.put("geography_id",mPrefs.getString(Constants.GeographyId,""));

        map.put("beneficiary_type_id",mPrefs.getString(Constants.BeneficiaryID,""));
        //map.put("pagenumber","1");
        Call<Categories> call= RestClient.get().getServices(map);
        call.enqueue(new Callback<Categories>() {
            @Override
            public void onResponse(Call<Categories> call, Response<Categories> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful())
                {

                    if(response.body().code.equals("401"))
                    {
                        GeneralFunctions.tokenExpireAction(SelectServiceActivity.this);
                    }

                    else  if(response.body().success==1)
                    {
                        catObj=response.body();
                        CURRENT_PAGE=1;
                        TOTAL_PAGES=response.body().data.total_pages;
                        lists =new ArrayList<>();
                        lists=catObj.data.objects;
                        // lists=  dbHelper.getServiceCategoryDetails();
                        wantadapter=new WantServicesAdapter(SelectServiceActivity.this,lists);
                        rvWatServices.setAdapter(wantadapter);

                        wantadapter.setOnBottomReachedListener(new RecyclerBottomListener() {
                            @Override
                            public void onBottomReached(int position) {
                                try {
                                    if(position==lists.size()-1) {
                                        if(lists.size()>1000)
                                            getFurtherCategoryList(++CURRENT_PAGE);
                                    }
                                }
                                catch (Exception e)
                                {

                                }

                            }
                        });
                        //checkLists();
                    }
                    else {
                        bar = Snackbar.make(rlWantSelect, response.body().code, Snackbar.LENGTH_LONG)
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

                    bar = Snackbar.make(rlWantSelect, getResources().getString(R.string.serverIssue), Snackbar.LENGTH_LONG)
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
                bar = Snackbar.make(rlWantSelect, getResources().getString(R.string.netIssue), Snackbar.LENGTH_LONG)
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

    private void getFurtherCategoryList(int i) {
        GeneralFunctions.showDialog(this);
        Map<String,String > map =new HashMap<>();
        map.put("size","10");
        map.put("geography_id",mPrefs.getString(Constants.GeographyId,""));
        map.put("pagenumber",String.valueOf(i));
        map.put(Constants.PARAM_BENEFICIARY_TYPE_ID ,mPrefs.getString(Constants.BeneficiaryID,""));
        Call<Categories> call= RestClient.get().getServices(map);
        call.enqueue(new Callback<Categories>() {
            @Override
            public void onResponse(Call<Categories> call, Response<Categories> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful())
                {
                    if(response.body().code.equals("401"))
                    {
                        GeneralFunctions.tokenExpireAction(SelectServiceActivity.this);
                    }
                    else if(response.body().success==1)
                    {
                        //categories=response.body();
                       /* catObj=response.body();

                        // dbHelper.clearDb();
                        // dbHelper.addServiceCategoryDetails(response.body().data.objects);

                        //checkList();
                        lists =new ArrayList<>();
                        lists=catObj.data.objects;
                        // lists=  dbHelper.getServiceCategoryDetails();
                        rvWatServices.setAdapter(new WantServicesAdapter(SelectServiceActivity.this,lists));*/

                        lists.addAll(response.body().data.objects);
                        wantadapter.notifyDataSetChanged();

                        //checkLists();
                    }
                    else {
                        bar = Snackbar.make(rlWantSelect, response.body().code, Snackbar.LENGTH_LONG)
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

                    bar = Snackbar.make(rlWantSelect, getResources().getString(R.string.serverIssue), Snackbar.LENGTH_LONG)
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
                bar = Snackbar.make(rlWantSelect, getResources().getString(R.string.netIssue), Snackbar.LENGTH_LONG)
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

   /* private void checkList() {
            lists =new ArrayList<>();
           // lists=  dbHelper.getServiceCategoryDetails();
                rvWatServices.setAdapter(new WantServicesAdapter(this,catObj.data.objects));
        checkLists();
    }*/



    HashMap<String, List<ServicesDetails>> listDataChilds = new HashMap<String, List<ServicesDetails>>();
    List<String> headers=new ArrayList<>();


    /*private void checkLists() {
        List<CategoryDetails> list =new ArrayList<>();
        list=  dbHelper.getServiceCategoryIds();
        //categoriesIds=new int[list.size()];
        List<CategoriesModel> categories = new ArrayList<>();
        List<ServicesDetails> sDetails =new ArrayList<>();


        List<String> listItem=new ArrayList<>();
        CategoriesModel catObj=new CategoriesModel();
        for(int i=0; i<list.size();i++)
        {

            catObj.categoryId=list.get(i).category.id;
            catObj.categoryName=list.get(i).category.name;
            catObj.isScheme=list.get(i).category.scheme;
            catObj.geoDistName=list.get(i).geography_district_name;
            catObj.geoId=list.get(i).geography_id;
            headers.add(list.get(i).category.name);

            sDetails=dbHelper.getSubCategories(list.get(i).category.id.toString());
            catObj.list=sDetails;
            categories.add(catObj);

            listDataChilds.put(headers.get(i), sDetails);


        }




    }*/

    @Override
    public void onClick(View v) {
        int i1 = v.getId();
        if (i1 == R.id.ivCrossSelect) {
            super.onBackPressed();

        } else if (i1 == R.id.ivSchemeCross) {
            etSelect.setText("");
            etSelect.clearFocus();

            ivSchemeCross.setVisibility(View.GONE);
            tvCatSelect.setVisibility(View.GONE);
            Header.clear();
            filterServiceCategories.clear();
            filterServiceCategories.addAll(lists);
            rlNoData.setVisibility(View.GONE);
            rvWatServices.setAdapter(new WantServicesAdapter(this, filterServiceCategories));

        } else if (i1 == R.id.tvCatSelect) {
            Bundle bundle = new Bundle();
            Intent intent = new Intent(this, FirstSurveyActivity.class);
            GeneralFunctions.showDialog(this);
            for (int i = 0; i < catObj.data.objects.size(); i++) {
                if (catObj.data.objects.get(i).category.name.equals(tvCatSelect.getText().toString())) {

                    bundle.putString("is_category", "1");
                    if (catObj.data.objects.get(i).category.scheme) {
                        bundle.putString("is_scheme", "1");
                        mPrefs.save(Constants.IsScheme, "YES");
                        mPrefs.save(Constants.SecontActivityText, getResources().getString(R.string.schemes));
                    } else {
                        bundle.putString("is_scheme", "0");
                        mPrefs.save(Constants.IsScheme, "NO");
                        mPrefs.save(Constants.SecontActivityText, getResources().getString(R.string.Services));
                    }
                    bundle.putString("selected_id", catObj.data.objects.get(i).category.id.toString());
                    //bundle.putString("geography_id",categoryDetails.get(position).geography_id.toString());
                    intent.putExtras(bundle);
                    break;
                }

            }
            GeneralFunctions.dismissDialog();
            startActivity(intent);

        } else if (i1 == R.id.tvgetStarted) {
            Bundle bundles = new Bundle();
            Intent intents = new Intent(this, FirstSurveyActivity.class);
            bundles.putString("is_category", "0");
            bundles.putString("is_scheme", "1");
            mPrefs.save(Constants.IsScheme, "YES");
            mPrefs.save(Constants.SecontActivityText, getResources().getString(R.string.schemes));
            bundles.putString("selected_id", "0");
            intents.putExtras(bundles);
            mPrefs.save(Constants.FromServices, "yes");
            startActivity(intents);

        }
    }

    private List<CategoryDetails> filterServiceCategories=new ArrayList<>();
    public void filter(String str)
    {
        filterServiceCategories.clear();


        if (TextUtils.isEmpty(str)) {

            filterServiceCategories.addAll(lists);

        } else {

            for (CategoryDetails item : lists) {
                if (item.service.name.toLowerCase().contains(str.toLowerCase()) ) {
                    // Adding Matched items
                    filterServiceCategories.add(item);
                }
            }

        }
        if(filterServiceCategories.size()>0)
        {
            rlNoData.setVisibility(View.GONE);
            rvWatServices.setVisibility(View.VISIBLE);
            rvWatServices.setAdapter(new WantServicesAdapter(this, filterServiceCategories));
        }
        else {
            //rvWatServices.setAdapter(new WantServicesAdapter(this, filterServiceCategories))
            rvWatServices.setVisibility(View.GONE);
            rlNoData.setVisibility(View.VISIBLE);
            GeneralFunctions.hideSoftKeyboard(this);

        }

        //adapter.notifyDataSetChanged();
        //this.notifyDataSetChanged();
    }
    private List<String> Header=new ArrayList<>();
    public void filterHeaders(String string)
    {
        Header.clear();
        if (TextUtils.isEmpty(string)) {

            Header.addAll(headers);
            tvCatSelect.setVisibility(View.GONE);

        } else {
            // Iterate in the original List and add it to filter list...
            for (String item : headers) {
                if (item.toLowerCase().contains(string.toLowerCase()) ) {
                    // Adding Matched items
                    Header.add(item);
                }
            }
            if(Header.size()==1)
            {
                tvCatSelect.setVisibility(View.VISIBLE);
                rlNoData.setVisibility(View.GONE);
                tvCatSelect.setText(Header.get(0));
            }

        }
    }
}
