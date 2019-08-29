package com.omlah.customer.base;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.omlah.customer.R;
import com.omlah.customer.common.LoginSession;
import com.omlah.customer.qrscanner.QrcodeScreen;
import com.omlah.customer.tabevent.EventScreen;
import com.omlah.customer.tabfeed.FeedScreen;
import com.omlah.customer.tabhome.NewHome;
import com.omlah.customer.tabmore.MoreScreen;
import com.omlah.customer.tabnearby.NearbyScreen;

/**
 * Created by admin on 06-07-2017.
 */

public class BaseScreen extends BaseActivity implements View.OnClickListener {

    //create class objects
    public static Fragment fragment;
    LoginSession loginSession;

    //Create xml objects
    Toolbar toolbar;
    public static TextView actionBarTitleTextview;
    public static ImageView bellImageView;
    public static TextView feedCountTextView;
    public static ImageView searchImageView;
    public static RelativeLayout searchLayout;
    public static EditText searchEditText;

    FrameLayout frameLayout;
    public static RelativeLayout qrCodeButton;
    public static RelativeLayout homeButton,nearbyButton,eventButton,moreButton;
    public static ImageView homeImageView,nearbyImageView,eventImageView,moreImageView;
    public static TextView homeTextView,nearbyTextView,eventTextView,moreTextView;

    //CameraRequest
    int SHORT_REQUEST_PHONE_CAMERA;

    //Check gps settings
    protected LocationManager locationManager;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_screen);
        hideActionBar();

        //Initialize xml objects
        toolbar                  = (Toolbar)findViewById(R.id.toolbar);

        frameLayout              = (FrameLayout) findViewById(R.id.frameLayout);

        searchImageView          = (ImageView) findViewById(R.id.searchImageView);
        searchLayout             = (RelativeLayout) findViewById(R.id.searchLayout);
        searchEditText           = (EditText) findViewById(R.id.searchEditText);

        bellImageView            = (ImageView) findViewById(R.id.bellImageView);
        feedCountTextView        = (TextView) findViewById(R.id.feedCountTextView);
        homeButton               = (RelativeLayout) findViewById(R.id.homeButton);
        nearbyButton             = (RelativeLayout) findViewById(R.id.nearbyButton);
        eventButton              = (RelativeLayout) findViewById(R.id.eventButton);
        moreButton               = (RelativeLayout) findViewById(R.id.moreButton);

        qrCodeButton             = (RelativeLayout) findViewById(R.id.qrCodeButton);

        homeImageView            = (ImageView) findViewById(R.id.homeImageView);
        nearbyImageView          = (ImageView) findViewById(R.id.nearbyImageView);
        eventImageView           = (ImageView) findViewById(R.id.eventImageView);
        moreImageView            = (ImageView) findViewById(R.id.moreImageView);

        homeTextView             = (TextView) findViewById(R.id.homeTextView);
        nearbyTextView           = (TextView) findViewById(R.id.nearbyTextView);
        eventTextView            = (TextView) findViewById(R.id.eventTextView);
        moreTextView             = (TextView) findViewById(R.id.moreTextView);
        actionBarTitleTextview   = (TextView) findViewById(R.id.actionBarTitleTextview);


        //initialize click event
        homeButton.setOnClickListener(this);
        nearbyButton.setOnClickListener(this);
        eventButton.setOnClickListener(this);
        moreButton.setOnClickListener(this);

        loginSession = LoginSession.getInstance(this);
        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        //Load default fragment
        actionBarTitleTextview.setText(getResources().getString(R.string.appname));
        actionBarTitleTextview.setVisibility(View.VISIBLE);
        fragment = new NewHome();
        fragmentChange(fragment);
        loginSession.saveScreenName("HOME");
        bellImageView.setVisibility(View.VISIBLE);
        feedCountTextView.setVisibility(View.GONE);
        searchImageView.setVisibility(View.GONE);
        searchLayout.setVisibility(View.GONE);


        qrCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (ContextCompat.checkSelfPermission(BaseScreen.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

                        loginSession.saveScreenName("QRCODE");
                        Intent intent = new Intent(BaseScreen.this,QrcodeScreen.class);
                        startActivity(intent);

                    }else {
                        ActivityCompat.requestPermissions(BaseScreen.this, new String[]{Manifest.permission.CAMERA}, SHORT_REQUEST_PHONE_CAMERA);
                        //request permisson
                        return;
                    }
                }else{

                    loginSession.saveScreenName("QRCODE");
                    Intent intent = new Intent(BaseScreen.this,QrcodeScreen.class);
                    startActivity(intent);
                }



            }
        });

        bellImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(BaseScreen.this,FeedScreen.class);
                startActivity(intent);
            }
        });
    }

    //Fragment changes method
    public void fragmentChange(Fragment fragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frameLayout,fragment).commit();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.homeButton:

                if(!loginSession.getScreenName().equals("HOME")){

                    loginSession.saveScreenName("HOME");

                    fragment = new NewHome();
                    fragmentChange(fragment);

                    callScreenRefreshMethod();
                    homeImageView.setColorFilter(ContextCompat.getColor(BaseScreen.this, R.color.red));
                    homeTextView.setTextColor(getResources().getColor(R.color.red));

                    actionBarTitleTextview.setText(getResources().getString(R.string.appname));
                    actionBarTitleTextview.setVisibility(View.VISIBLE);

                    bellImageView.setVisibility(View.VISIBLE);
                    feedCountTextView.setVisibility(View.GONE);
                    searchImageView.setVisibility(View.GONE);
                    searchLayout.setVisibility(View.GONE);

                }

                break;

            case R.id.nearbyButton:

                if (!loginSession.getScreenName().equals("NEARBY")) {

                    if (!isGPSEnabled && !isNetworkEnabled) {

                        showSettingsAlert();

                    } else {

                        loginSession.saveScreenName("NEARBY");

                        fragment = new NearbyScreen();
                        fragmentChange(fragment);

                        callScreenRefreshMethod();
                        nearbyImageView.setColorFilter(ContextCompat.getColor(BaseScreen.this, R.color.red));
                        nearbyTextView.setTextColor(getResources().getColor(R.color.red));

                        actionBarTitleTextview.setText(getResources().getString(R.string.Nearby));

                        actionBarTitleTextview.setVisibility(View.VISIBLE);

                        bellImageView.setVisibility(View.GONE);
                        feedCountTextView.setVisibility(View.GONE);
                        searchImageView.setVisibility(View.GONE);
                        searchLayout.setVisibility(View.GONE);
                    }

                }

                break;

            case R.id.eventButton:

                if(!loginSession.getScreenName().equals("EVENT")){

                    loginSession.saveScreenName("EVENT");

                    fragment = new EventScreen();
                    fragmentChange(fragment);

                    callScreenRefreshMethod();

                    eventImageView.setColorFilter(ContextCompat.getColor(BaseScreen.this, R.color.red));
                    eventTextView.setTextColor(getResources().getColor(R.color.red));

                    actionBarTitleTextview.setText(getResources().getString(R.string.Events));

                    actionBarTitleTextview.setVisibility(View.VISIBLE);

                    bellImageView.setVisibility(View.GONE);
                    feedCountTextView.setVisibility(View.GONE);
                    searchImageView.setVisibility(View.VISIBLE);
                    searchLayout.setVisibility(View.GONE);
                }

                break;

            case R.id.moreButton:

                    if(!loginSession.getScreenName().equals("MORE")){

                        loginSession.saveScreenName("MORE");

                        fragment = new MoreScreen();
                        fragmentChange(fragment);

                        callScreenRefreshMethod();

                        moreImageView.setColorFilter(ContextCompat.getColor(BaseScreen.this, R.color.red));
                        moreTextView.setTextColor(getResources().getColor(R.color.red));

                        actionBarTitleTextview.setText(getResources().getString(R.string.More));

                        actionBarTitleTextview.setVisibility(View.VISIBLE);
                        bellImageView.setVisibility(View.GONE);

                        feedCountTextView.setVisibility(View.GONE);
                        searchImageView.setVisibility(View.GONE);
                        searchLayout.setVisibility(View.GONE);
                    }

                break;

        }

    }

    private void callScreenRefreshMethod() {

        homeImageView.setColorFilter(ContextCompat.getColor(BaseScreen.this, R.color.gray));
        homeTextView.setTextColor(getResources().getColor(R.color.gray));

        nearbyImageView.setColorFilter(ContextCompat.getColor(BaseScreen.this, R.color.gray));
        nearbyTextView.setTextColor(getResources().getColor(R.color.gray));

        eventImageView.setColorFilter(ContextCompat.getColor(BaseScreen.this, R.color.gray));
        eventTextView.setTextColor(getResources().getColor(R.color.gray));

        moreImageView.setColorFilter(ContextCompat.getColor(BaseScreen.this, R.color.gray));
        moreTextView.setTextColor(getResources().getColor(R.color.gray));
    }

    @Override
    public void onBackPressed() {

        if(!loginSession.getScreenName().equals("HOME")){

            loginSession.saveScreenName("HOME");

            fragment = new NewHome();
            fragmentChange(fragment);
            
            callScreenRefreshMethod();

            homeImageView.setColorFilter(ContextCompat.getColor(BaseScreen.this, R.color.red));
            homeTextView.setTextColor(getResources().getColor(R.color.red));

            actionBarTitleTextview.setText(getResources().getString(R.string.RPay));
            actionBarTitleTextview.setVisibility(View.VISIBLE);
            bellImageView.setVisibility(View.VISIBLE);
            feedCountTextView.setVisibility(View.GONE);
            searchImageView.setVisibility(View.GONE);
            searchLayout.setVisibility(View.GONE);

        }else{

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

            alertDialog.setTitle(getResources().getString(R.string.AreYouSureWantToExit));

            alertDialog.setPositiveButton(getResources().getString(R.string.Yes), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    finish();
                }
            });

            alertDialog.setNegativeButton(getResources().getString(R.string.No), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            alertDialog.show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

       if(requestCode == SHORT_REQUEST_PHONE_CAMERA){

            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                loginSession.saveScreenName("QRCODE");
                Intent intent = new Intent(BaseScreen.this,QrcodeScreen.class);
                startActivity(intent);

            }else{

                loginSession.saveScreenName("QRCODE");
                Intent intent = new Intent(BaseScreen.this,QrcodeScreen.class);
                startActivity(intent);
            }
        }
    }

    public void showSettingsAlert()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle("Location is disabled");

        alertDialog.setMessage("Location service is disabled please enable location");

        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();

        try{
            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }catch (Exception e){e.printStackTrace();}

    }

}
