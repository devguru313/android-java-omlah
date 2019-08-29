package com.omlah.customer.base;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.omlah.customer.common.language.core.LocalizationActivityDelegate;
import com.omlah.customer.common.language.core.OnLocaleChangedListener;
import com.google.gson.Gson;
import com.omlah.customer.R;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

/**
 * Created by admin on 30-06-2017.
 */

public class BaseActivity extends AppCompatActivity implements OnLocaleChangedListener{

    ActionBar actionBar;
    Dialog progressDialog,progressDialogCustom;

    //Location Objects
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    LocationManager locationManager;

    ImageView backIconImageView;
    TextView actionBarTitleTextView;

    private LocalizationActivityDelegate localizationDelegate = new LocalizationActivityDelegate(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        localizationDelegate.addOnLocaleChangedListener(this);
        localizationDelegate.onCreate(savedInstanceState);
        actionBar=getSupportActionBar();
        locationManager = (LocationManager)this.getSystemService(LOCATION_SERVICE);
    }

    public void showBackArrow(){

        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        //Initializes the custom action bar layout
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient_file));
        Toolbar parent = (Toolbar) mCustomView.getParent();
        parent.setContentInsetsAbsolute(0,0);

        backIconImageView = (ImageView) findViewById(R.id.backIconImageView);
        actionBarTitleTextView = (TextView) findViewById(R.id.actionBarTitleTextView);

        backIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
    }



    public void hideActionBar(){

        actionBar.hide();

    }


    //Set ActionBar Title
    public void setActionBarTitle(String Title)
    {
        actionBarTitleTextView.setText(Title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home :

                finish();
                break;

        }
        return super.onOptionsItemSelected(item);

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////COMMON METHODS ////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**********************************Toast Method*************************************/
    public void toast(String message)
    {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

    /********************************Check Internet Connection Method*********************************/
    public boolean isConnectingToInternet()
    {
        ConnectivityManager connectivity = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)

                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }

    /*********************************Check valid email Method**********************************/
    public boolean validEmail(String email)
    {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }


    /**********************************Alert dialog Method*************************************/
    public void noInternetAlertDialog()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle("No Internet Connection!!!");

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    /**********************************Alert dialog Method*************************************/
    public void showAlertDialog(String message)
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle(message);

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    public void showSettingsAlert()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle(getResources().getString(R.string.Locationisdisabled));

        alertDialog.setMessage(getResources().getString(R.string.LocationisdisabledDEC));

        alertDialog.setPositiveButton(getResources().getString(R.string.Settings), new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    //play store dialog
    public void ConnectPlaystore()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Please update your google play service!!!");
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.gms"));
                startActivity(intent);
            }
        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alert.show();

    }

    public void showProgressDialog() {
        try{
            if (progressDialog == null) {
                progressDialog = new Dialog(this);
                progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                progressDialog.setContentView(R.layout.custom_progressbar);
                progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                progressDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            }
            progressDialog.setCancelable(false);
            progressDialog.show();
        }catch (Exception e){e.printStackTrace();}

    }
    public  void hideProgressDialog() {
        try{
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /*************************************Progress Dialogue with close*************************************************/
    public void showCustomProgressDialog() {
        try {
            if (progressDialogCustom == null) {
                progressDialogCustom = new Dialog(this);
                progressDialogCustom.requestWindowFeature(Window.FEATURE_NO_TITLE);
                progressDialogCustom.setContentView(R.layout.custom_progressbar);
                progressDialogCustom.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                progressDialogCustom.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            }
            progressDialogCustom.setCancelable(false);
            progressDialogCustom.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideCustomProgressDialog() {
        try {
            if (progressDialogCustom.isShowing()) {
                progressDialogCustom.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /********************************************************************************************************************/


    public boolean checkLocationService()
    {
        // getting GPS status
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (isGPSEnabled && isNetworkEnabled) {

            return true;
        }

        return false;
    }

    public static void getTotalHeightofListView(ListView listView)
    {
        ListAdapter mAdapter = listView.getAdapter();

        int totalHeight = 0;

        for (int i = 0; i < mAdapter.getCount(); i++)
        {
            View mView = mAdapter.getView(i, null, listView);

            mView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),

                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

            totalHeight += mView.getMeasuredHeight();
            Log.e("HEIGHT" + i, String.valueOf(totalHeight));

        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (mAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();

    }

    //Date Conversion
    public String dayDateMonthConversion(String serverDate){

        String returnDate = "";
        try {
            String date = serverDate;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+00:00'", Locale.ENGLISH);
            SimpleDateFormat sdf2 = new SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.ENGLISH);
            Date d = sdf.parse(date);
            Log.e("dateintial", d.toString());
            Log.e("date", sdf2.format(d));
            returnDate = d.toString();
        }catch (Exception e){
        }
        String splitStartDate[] = returnDate.split("\\s");
        returnDate = splitStartDate[0]+", "+splitStartDate[2]+" "+splitStartDate[1];

        return returnDate;
    }

    public String timeConversion(String serverDate){

        String returnDate = "";
        try {
            String date = serverDate;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+00:00'", Locale.ENGLISH);
            SimpleDateFormat sdf2 = new SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.ENGLISH);
            Date d = sdf.parse(date);
            Log.e("dateintial", d.toString());
            Log.e("date", sdf2.format(d));
            returnDate = sdf2.format(d).toString();
        }catch (Exception e){
        }
        String splitStartDate[] = returnDate.split("\\s");
        returnDate = splitStartDate[3]+" "+splitStartDate[4];

        return returnDate;
    }


    //For MoneyRequest Screen
    //Convert time zone
    // date return format Dec 21, 2017 2:01 am
    public String timeZoneConverter(String serverDate,String timeZone){

        String returnDate = "";
        try {
            SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+00:00'",Locale.ENGLISH);
            sourceFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date parsed = sourceFormat.parse(serverDate);
            TimeZone tz = TimeZone.getTimeZone(timeZone);
            SimpleDateFormat destFormat = new SimpleDateFormat("MMM dd, yyyy h:mm a",Locale.ENGLISH);
            destFormat.setTimeZone(tz);
            returnDate = destFormat.format(parsed);

        }catch (Exception e){}

        return returnDate;

    }


    public final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean checkPermission(final Context context)
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>=android.os.Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle(getResources().getString(R.string.Permissionnecessary));
                    alertBuilder.setMessage(getResources().getString(R.string.ExternalStoragePermissionNecessary));
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();

                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public final int MY_PERMISSIONS_REQUEST_READ_CAMERA = 321;
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean checkCameraPermission(final Context context)
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>=android.os.Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.CAMERA)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle(getResources().getString(R.string.Permissionnecessary));
                    alertBuilder.setMessage(getResources().getString(R.string.CameraPermissionNecessary));
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_READ_CAMERA);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();

                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_READ_CAMERA);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public Object getCacheData(String URL,Class model){

        Object response ="empty";
        RequestQueue queue = null;
        queue = Volley.newRequestQueue(getApplicationContext());
        queue.getCache().invalidate(URL,true);
        Cache cache = queue.getCache();
        Cache.Entry entry = cache.get(URL);
        if(entry != null){
            //Cache data available.
            try {
                response = new String(entry.data, "UTF-8");
                Log.e("CACHE DATE",""+response);

                try {
                    Gson gson = new Gson();
                    response = gson.fromJson(response.toString(),model);

                } catch (Exception e) {
                    e.printStackTrace();;
                }

            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        else{
            // Cache data not exist.
            response ="empty";
        }
        return response;
    }

    @Override
    public void onResume() {
        super.onResume();
        localizationDelegate.onResume(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(localizationDelegate.attachBaseContext(newBase));
    }

    @Override
    public Context getApplicationContext() {
        return localizationDelegate.getApplicationContext(super.getApplicationContext());
    }

    @Override
    public Resources getResources() {
        return localizationDelegate.getResources(super.getResources());
    }

    public final void setLanguage(String language) {
        localizationDelegate.setLanguage(this, language);
    }

    public final void setLanguage(Locale locale) {
        localizationDelegate.setLanguage(this, locale);
    }

    public final void setDefaultLanguage(String language) {
        localizationDelegate.setDefaultLanguage(language);
    }

    public final void setDefaultLanguage(Locale locale) {
        localizationDelegate.setDefaultLanguage(locale);
    }

    public final Locale getCurrentLanguage() {
        return localizationDelegate.getLanguage(this);
    }

    // Just override method locale change event
    @Override
    public void onBeforeLocaleChanged() {
    }

    @Override
    public void onAfterLocaleChanged() {
    }


    public String changeCustomerCurrency(String value,String sender_currency_difference){
        double inputAmount = Double.parseDouble(value);
        double currencyDifference = Double.parseDouble(sender_currency_difference);
        double overallAmount = inputAmount * currencyDifference;
        String returnAmount = String.format(Locale.ENGLISH,"%.2f",overallAmount);
        return returnAmount;
    }

    public String changePayCurrency(String value,String sender_currency_difference){
        double inputAmount  = Double.parseDouble(value);
        double currencyDifference = Double.parseDouble(sender_currency_difference);
        double overallAmount = inputAmount / currencyDifference;
        String returnAmount = String.format(Locale.ENGLISH,"%.2f",overallAmount);
        return returnAmount;
    }


    public String normalizePhoneNumber(String number, String countryCode) {

        countryCode = countryCode.replace("+", "").trim();
        number = number.substring(countryCode.length(),number.length());
        String NUMNBER = number.substring(countryCode.length(), number.length());
        return "+" + countryCode + " " + NUMNBER;

    }

    public static void hideKeyboard(Activity activity) {
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            //Find the currently focused view, so we can grab the correct window token from it.
            View view = activity.getCurrentFocus();
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = new View(activity);
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
