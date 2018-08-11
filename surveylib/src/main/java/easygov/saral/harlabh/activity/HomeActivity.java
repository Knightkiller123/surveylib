package easygov.saral.harlabh.activity;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import easygov.saral.harlabh.fragments.bottomfrags.CategoriesFragment;
import easygov.saral.harlabh.fragments.bottomfrags.HomeFragment;
import easygov.saral.harlabh.R;
import easygov.saral.harlabh.fragments.bottomfrags.ProfileFragment;
import easygov.saral.harlabh.utils.Constants;
import easygov.saral.harlabh.utils.LocaleHelper;
import easygov.saral.harlabh.utils.Prefs;
import static easygov.saral.harlabh.utils.MyApplication.latitude;
import static easygov.saral.harlabh.utils.MyApplication.longitude;


/**
 * Created by apoorv on 27/07/17.
 */

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener ,LocationListener {
    private FrameLayout frameLayout;
    private BottomNavigationView bottomNavigation;
    public static HomeActivity homeActivity;
    private String bottomCheck;
    private Prefs mPrefs;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private PendingResult<LocationSettingsResult> result;
    final static int REQUEST_LOCATION = 199;
    LocationCallback mLocationCallback;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
        setListeners();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation();
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void getLocation() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(HomeActivity.this).build();
        mGoogleApiClient.connect();
    }

    private void init() {
        homeActivity = this;
        frameLayout = findViewById(R.id.frameLayout);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomCheck = "item1";
        mPrefs = Prefs.with(this);
        mPrefs.save(Constants.FormFilled, "NO");
        mPrefs.save(Constants.RestartSurvey,"no");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, new HomeFragment());
        transaction.commit();
    }

    private void setListeners() {
        bottomNavigation.setOnNavigationItemSelectedListener(this);
    }

    HomeFragment home = null;

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment selectedFragment = null;
        Menu menu = bottomNavigation.getMenu();

        mPrefs.save("sgm_new_id","00");
        int i = item.getItemId();
        if (i == R.id.action_item1) {
            if (!bottomCheck.equals("item1")) {
                bottomCheck = "item1";
                selectedFragment = HomeFragment.newInstance();
                home = (HomeFragment) selectedFragment;
                menu.findItem(R.id.action_item1).setIcon(R.drawable.home_selected);
                menu.findItem(R.id.action_item2).setIcon(R.drawable.categories_unselected);
                menu.findItem(R.id.action_item3).setIcon(R.drawable.profile_grey_01);
                //      menu.findItem(R.id.action_item4).setIcon(R.drawable.more_unselected);
            }

        } else if (i == R.id.action_item2) {
            if (!bottomCheck.equals("item2")) {
                bottomCheck = "item2";
                selectedFragment = CategoriesFragment.newInstance();
                menu.findItem(R.id.action_item2).setIcon(R.drawable.categories_selected);
                menu.findItem(R.id.action_item1).setIcon(R.drawable.home_unselected);
                menu.findItem(R.id.action_item3).setIcon(R.drawable.profile_grey_01);
            }

        } else if (i == R.id.action_item3) {
            if (!bottomCheck.equals("item3")) {
                bottomCheck = "item3";
                selectedFragment = ProfileFragment.newInstance();
                menu.findItem(R.id.action_item2).setIcon(R.drawable.categories_unselected);

                menu.findItem(R.id.action_item1).setIcon(R.drawable.home_unselected);
                menu.findItem(R.id.action_item3).setIcon(R.drawable.profile_colourful_01);

            }

        }

        if (selectedFragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayout, selectedFragment);
            transaction.commitAllowingStateLoss();
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                             getLocation();
                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, 500);

                        Toast.makeText(HomeActivity.this, "Can not start app without enabling gps", Toast.LENGTH_LONG).show();
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;

            case 500:
                getLocation();
                break;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }


    public void updateViews(String languageCode) {
        LocaleHelper.setLocale(this, languageCode);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        try {
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(30 * 1000);
            mLocationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest);
            builder.setAlwaysShow(true);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

            result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    //final LocationSettingsStates state = result.getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                status.startResolutionForResult(
                                        HomeActivity.this,
                                        REQUEST_LOCATION);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            //...
                            break;
                    }
                }
            });

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            // mFusedLocationClient.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        }
        catch (Exception e )
        {
            e.printStackTrace();
        }

    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if(location!=null) {
            latitude = location.getLatitude();
            longitude=location.getLongitude();
        }
    }









}
