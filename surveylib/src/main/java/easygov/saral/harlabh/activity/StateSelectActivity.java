package easygov.saral.harlabh.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import easygov.saral.harlabh.R;
import easygov.saral.harlabh.adapters.DistrictsAdapter;
import easygov.saral.harlabh.adapters.StatesAdapter;
import easygov.saral.harlabh.models.responsemodels.statesndistricts.DistrictDetails;
import easygov.saral.harlabh.models.responsemodels.statesndistricts.DistrictsModel;
import easygov.saral.harlabh.models.responsemodels.statesndistricts.StatesModel;
import easygov.saral.harlabh.utils.Constants;
import easygov.saral.harlabh.utils.GeneralFunctions;
import easygov.saral.harlabh.utils.MyApplication;
import easygov.saral.harlabh.utils.Prefs;
import easygov.saral.harlabh.utils.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by apoorv on 26/07/17.
 */

public class StateSelectActivity extends AppCompatActivity {

    private RecyclerView rlDist, rlStates;

    private AutoCompleteTextView actvDist;
    private String selected;
    public LinearLayout rlStateSelect;
    private Prefs mPrefs;
    private Snackbar bar;
    private StatesModel statesObj;
    public RelativeLayout rlStateImg;
    private DistrictsModel distObj, distObjAll;
    public List<DistrictDetails> districtMdel;
    private RelativeLayout rlNoGeography;
    // private  List<DistrictDetails> latest;
    private ImageView ivState;
    private TextView tvState, tvSelectText;
    private ImageView ivDistBack;

    // private EditText etDist;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stateselect);

        //animate=getIntent().getExtras().getString("animate");
        init();


        setAutocompleClick();


         //setStates();


        setSearch();

        setStaticStateHaryana();
    }

    /*@Override
    protected void onResume() {
        super.onResume();
        getAllDistricts();
    }*/

    private void getAllDistricts() {
        GeneralFunctions.showDialog(this);
        Call<DistrictsModel> call = RestClient.get().getAllDistricts("1000");
        call.enqueue(new Callback<DistrictsModel>() {
            @Override
            public void onResponse(Call<DistrictsModel> call, Response<DistrictsModel> response) {
                GeneralFunctions.dismissDialog();
                if (response.isSuccessful()) {
                    if(response.body().code.equals("401"))
                    {
                        GeneralFunctions.tokenExpireAction(StateSelectActivity.this);
                    }
                    else if (response.body().success == 1) {
                        distObj = response.body();
                        distObjAll = response.body();
                        rlDist.setAdapter(new DistrictsAdapter(StateSelectActivity.this, distObj.data.objects));
                        districtMdel = distObj.data.objects;
                        // latest=distObj.data.objects;

                    } else {
                        bar = Snackbar.make(rlStateSelect, response.body().code, Snackbar.LENGTH_LONG)
                                .setAction("Dismiss", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // Handle user action
                                        bar.dismiss();
                                    }
                                });

                        bar.show();
                    }
                } else {


                    bar = Snackbar.make(rlStateSelect, getResources().getString(R.string.serverIssue), Snackbar.LENGTH_LONG)
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
            public void onFailure(Call<DistrictsModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                bar = Snackbar.make(rlStateSelect, getResources().getString(R.string.netIssue), Snackbar.LENGTH_LONG)
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


    private void setStates() {


        GeneralFunctions.showDialog(this);
        Call<StatesModel> call = RestClient.get().getAllStates("1000");
        call.enqueue(new Callback<StatesModel>() {
            @Override
            public void onResponse(Call<StatesModel> call, Response<StatesModel> response) {
                GeneralFunctions.dismissDialog();
                if (response.isSuccessful()) {
                    if(response.body().code.equals("401"))
                    {
                        GeneralFunctions.tokenExpireAction(StateSelectActivity.this);
                    }
                    else  if (response.body().success == 1) {
                        statesObj = response.body();
                        rlStates.setAdapter(new StatesAdapter(StateSelectActivity.this, statesObj.data.objects));
/*
                        DbHelper db=new DbHelper(StateSelectActivity.this);
                        db.addStates(statesObj.data.objects);*/
                        getAllDistricts();
                    } else {
                        bar = Snackbar.make(rlStateSelect, response.body().code, Snackbar.LENGTH_LONG)
                                .setAction("Dismiss", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // Handle user action
                                        bar.dismiss();
                                    }
                                });

                        bar.show();
                    }


                } else {

                    bar = Snackbar.make(rlStateSelect, getResources().getString(R.string.serverIssue), Snackbar.LENGTH_LONG)
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
            public void onFailure(Call<StatesModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                bar = Snackbar.make(rlStateSelect, getResources().getString(R.string.netIssue), Snackbar.LENGTH_LONG)
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

    private void init() {
        rlDist = findViewById(R.id.rlDist);
        rlStates = findViewById(R.id.rlStates);
        mPrefs = Prefs.with(this);
        rlDist.setLayoutManager(new LinearLayoutManager(this));
        rlStates.setLayoutManager(new GridLayoutManager(this, 4));
        rlStateSelect = findViewById(R.id.rlStateSelect);
        actvDist = findViewById(R.id.actvDist);
        tvState = findViewById(R.id.tvState);
        rlNoGeography = findViewById(R.id.rlNoGeography);
        tvSelectText = findViewById(R.id.tvSelectText);
        ivState = findViewById(R.id.ivState);
        rlStateImg = findViewById(R.id.rlStateImg);
        ivDistBack = findViewById(R.id.ivDistBack);

        ivDistBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: HARYANA remove if else condition

                if(MyApplication.Client!="haryana") {
                    showStates();
                    GeneralFunctions.hideSoftKeyboard(StateSelectActivity.this);
                }
                else {

                     if (!mPrefs.getString(Constants.GeographySelected, "").isEmpty()) {
                        Intent intent = new Intent(StateSelectActivity.this, HomeActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.reverseanimationup_activity, R.anim.reverseanimatedown_activity);
                        finish();
                    } else
                        finish();
                }
            }
        });


      /* ArrayAdapter<List<DistrictDetails>> adapter = new ArrayAdapter<List<DistrictDetails>>()
                (this,android.R.layout.select_dialog_item,distObj.data.get(0).objects);

        actvDist.setThreshold(1);
        actvDist.setAdapter(adapter);
        actvDist.setTextColor(Color.GRAY);*/

        rlStateSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actvDist.clearFocus();
                GeneralFunctions.hideSoftKeyboard(StateSelectActivity.this);

            }
        });
    }


    private void setSearch() {
        actvDist.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filter(String.valueOf(charSequence));

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        rlStates.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                GeneralFunctions.hideSoftKeyboard(StateSelectActivity.this);
                return false;
            }
        });

        rlDist.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                GeneralFunctions.hideSoftKeyboard(StateSelectActivity.this);
                return false;
            }
        });
    }

    private List<DistrictDetails> filterServiceCategories = new ArrayList<>();

    public void filter(String str) {
        filterServiceCategories.clear();

        // If there is no search value, then add all original list items to filter list
        if (TextUtils.isEmpty(str)) {

            filterServiceCategories.addAll(distObj.data.objects);

        } else {
            // Iterate in the original List and add it to filter list...
            for (DistrictDetails item : distObj.data.objects) {
                if (item.district_name.toLowerCase().contains(str.toLowerCase())) {
                    // Adding Matched items
                    filterServiceCategories.add(item);
                }
            }
        }


        if(filterServiceCategories.size()>0)
        {
            rlNoGeography.setVisibility(View.GONE);
            rlDist.setVisibility(View.VISIBLE);
            rlDist.setAdapter(new DistrictsAdapter(this, filterServiceCategories));

        }
        else {
            rlDist.setVisibility(View.GONE);
            rlNoGeography.setVisibility(View.VISIBLE);
            GeneralFunctions.hideSoftKeyboard(this);

        }

        //rlDist.setAdapter(new DistrictsAdapter(this, filterServiceCategories));

        //adapter.notifyDataSetChanged();
        //this.notifyDataSetChanged();
    }

    private void setAutocompleClick() {
        actvDist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // mPrefs.save(Constants.StateSelected,parent.getItemAtPosition(position).toString());
                Intent intent = new Intent(StateSelectActivity.this, HomeActivity.class);

                startActivity(intent);
                finish();
            }
        });

        actvDist.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    hideStates();
                    //  rlStateImg.setVisibility(View.GONE);
                    //mPrefs.save(Constants.Direct,"yes");
                } else {
                    // mPrefs.save(Constants.Direct,"no");
                }
            }
        });
    }

    public void showStates() {
        rlStateImg.setVisibility(View.GONE);
        // tvState.setVisibility(View.GONE);
        //ivState.setVisibility(View.GONE);
        rlDist.setVisibility(View.GONE);
        rlStates.setVisibility(View.VISIBLE);
        rlStates.setAdapter(new StatesAdapter(this, statesObj.data.objects));
        actvDist.clearFocus();
        tvSelectText.setText(getResources().getString(R.string.selectState));
        actvDist.setText("");
        ivDistBack.setVisibility(View.INVISIBLE);
        mPrefs.save(Constants.Direct, "yes");
        districtMdel = new ArrayList<>();
        districtMdel = distObjAll.data.objects;
        distObj = distObjAll;

        tvState.setText(getResources().getString(R.string.haryana));
        int s = 0;
        //districtMdel=latest;

    }

    public void hideStates() {

       /* if(stateClicked==1)
        {
            rlStateImg.setVisibility(View.VISIBLE);
        }
        else {
            rlStateImg.setVisibility(View.GONE);
        }*/
        rlStates.setVisibility(View.GONE);
        rlDist.setVisibility(View.VISIBLE);
        rlDist.setAdapter(new DistrictsAdapter(this, districtMdel));
        tvSelectText.setText(getResources().getString(R.string.selectdistrict));
        ivDistBack.setVisibility(View.VISIBLE);
        // rlStateImg.setVisibility(View.VISIBLE);
        //tvState.setVisibility(View.VISIBLE);

        //Todo:remove below line
        tvState.setText(getResources().getString(R.string.haryana));
       // tvState.setText(mPrefs.getString(Constants.StateSelected, ""));
        // ivState.setVisibility(View.VISIBLE);

    }

    public void setFilterList(DistrictsModel body) {
        distObj = body;
    }


    @Override
    public void onBackPressed() {

        if(MyApplication.Client!="haryana") {
            if (rlStates.getVisibility() == View.GONE) {

                showStates();
            } else if (!mPrefs.getString(Constants.GeographySelected, "").isEmpty()) {
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.reverseanimationup_activity, R.anim.reverseanimatedown_activity);
                finish();
            } else
                super.onBackPressed();
        }

        else if (!mPrefs.getString(Constants.GeographySelected, "").isEmpty()) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.reverseanimationup_activity, R.anim.reverseanimatedown_activity);
            finish();
        } else
            super.onBackPressed();
    }

    public void setStateIconIndividual(int position) {
        rlStateImg.setVisibility(View.VISIBLE);
        setImageView(ivState,position);
    }

    private void setImageView(ImageView img, int pos) {
        switch (pos) {
            case 1:
                img.setBackgroundResource(R.drawable.delhi_01);
                break;

            case 2:
                img.setBackgroundResource(R.drawable.up_01);
                break;

            case 3:
                img.setBackgroundResource(R.drawable.punjab_01);
                break;

            case 4:
                img.setBackgroundResource(R.drawable.maharastra_01);
                break;

            case 5:
                img.setBackgroundResource(R.drawable.west_bengal_01);
                break;

            case 6:
                img.setBackgroundResource(R.drawable.karnataka_01);
                break;

            case 7:
                img.setBackgroundResource(R.drawable.gujrat_01);
                break;

            case 8:
                img.setBackgroundResource(R.drawable.rajasthan_01);
                break;

            case 9:
                img.setBackgroundResource(R.drawable.bihar_01);
                break;

            case 10:
                img.setBackgroundResource(R.drawable.telangana_01);

                break;

            case 11:
                img.setBackgroundResource(R.drawable.ap_01);

                break;

            case 12:
                img.setBackgroundResource(R.drawable.tamilnadu_01);

                break;

            case 13:
                img.setBackgroundResource(R.drawable.kerala_01);

                break;

            case 14:
                img.setBackgroundResource(R.drawable.chhattisgarh_01);
                break;

            case 15:
                break;

            case 16:
                img.setBackgroundResource(R.drawable.odisa_01);

                break;

            case 17:
                img.setBackgroundResource(R.drawable.mp_01);

                break;

            case 18:
                img.setBackgroundResource(R.drawable.assam_01);

                break;

            case 19:
                img.setBackgroundResource(R.drawable.uttrakhand_01);

                break;

            case 20:
                img.setBackgroundResource(R.drawable.haryana_01);

                break;


            case 21:
                img.setBackgroundResource(R.drawable.kashmir_01);

                break;

            case 22:
                img.setBackgroundResource(R.drawable.hp_01);

                break;

            case 23:
                img.setBackgroundResource(R.drawable.chandigrah_01);

                break;

            case 24:
                img.setBackgroundResource(R.drawable.puducherry_01);

                break;

            case 25:
                img.setBackgroundResource(R.drawable.tripura_01);

                break;

            case 26:
                img.setBackgroundResource(R.drawable.meghalaya_01);

                break;

            case 27:
                img.setBackgroundResource(R.drawable.goa_01);

                break;

            case 28:
                img.setBackgroundResource(R.drawable.manipur_01);

                break;

            case 29:
                img.setBackgroundResource(R.drawable.mizoram_01);

                break;

            case 30:
                img.setBackgroundResource(R.drawable.dadra_01);

                break;

            case 31:
                img.setBackgroundResource(R.drawable.sikkim_01);

                break;

            case 32:
                img.setBackgroundResource(R.drawable.andaman_01);

                break;

            case 33:
                img.setBackgroundResource(R.drawable.daman_01);

                break;

            case 34:
                img.setBackgroundResource(R.drawable.arunachal_01);

                break;

            case 35:
                img.setBackgroundResource(R.drawable.lakshadeew_01);

                break;

            case 36:
                img.setBackgroundResource(R.drawable.jharkhand_01);

                break;

            case 37:
                img.setBackgroundResource(R.drawable.nagaland_01);

                break;


        }
    }

    private void setStaticStateHaryana()
    {
        rlStates.setAdapter(null);
        rlStateImg.setVisibility(View.VISIBLE);
        setImageView(ivState,20);
        tvState.setText(getResources().getString(R.string.haryana));
       // hideStates();
        getDistrictsbyId(20);
        mPrefs.save(Constants.Direct,"no");
    }


    private void getDistrictsbyId(Integer id)
    {
        GeneralFunctions.showDialog(this);
        Call<DistrictsModel> call = RestClient.get().getSelectedDistricts(id.toString(),"10000");
        call.enqueue(new Callback<DistrictsModel>() {
            @Override
            public void onResponse(Call<DistrictsModel> call, Response<DistrictsModel> response) {
                GeneralFunctions.dismissDialog();
                if(response.isSuccessful())
                {
                    if(response.body().code.equals("401"))
                    {
                        GeneralFunctions.tokenExpireAction(StateSelectActivity.this);
                    }
                    else if(response.body().success==1)
                    {
                        distObj = response.body();
                        districtMdel=response.body().data.objects;
                       /* DbHelper db=new DbHelper();
                        db.addDistricts(districts,integer);*/
                        hideStates();
                       // ((StateSelectActivity)context).setFilterList(response.body());
                        mPrefs.save(Constants.Direct,"no");



                    }
                }
                else {

                    bar = Snackbar.make(rlStateSelect, getResources().getString(R.string.serverIssue), Snackbar.LENGTH_LONG)
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
            public void onFailure(Call<DistrictsModel> call, Throwable t) {
                GeneralFunctions.dismissDialog();
                bar = Snackbar.make(rlStateSelect, getResources().getString(R.string.netIssue), Snackbar.LENGTH_LONG)
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

    public void showNoDataDist()
    {
        rlDist.setVisibility(View.GONE);
        rlNoGeography.setVisibility(View.VISIBLE);

    }

    public void revertNoDataDist()
    {
        rlDist.setVisibility(View.VISIBLE);
        rlNoGeography.setVisibility(View.GONE);
    }
}
