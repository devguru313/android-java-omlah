package com.omlah.customer.base;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
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

public class BaseFragment extends Fragment {

    Dialog progressDialog,progressDialogCustom;

    /**********************************Toast Method*************************************/
    public void toast(String message)
    {
        Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

    /********************************Check Internet Connection Method*********************************/
    public boolean isConnectingToInternet()
    {
        try{
            ConnectivityManager connectivity = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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
        }catch (Exception e){e.printStackTrace();
            return false;}
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
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        alertDialog.setTitle(getResources().getString(R.string.NoInternetConnection));

        alertDialog.setPositiveButton(getResources().getString(R.string.Ok
        ), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    /**********************************Alert dialog Method*************************************/
    public void showAlertDialog(String message)
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        alertDialog.setTitle(message);

        alertDialog.setPositiveButton(getResources().getString(R.string.Ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    public void showSettingsAlert()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

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
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
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

    /*****************************Set List View height based in size Method*************************/
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
            Log.w("HEIGHT" + i, String.valueOf(totalHeight));

        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (mAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();

    }


    public void showProgressDialog() {

        if (progressDialog == null) {

            progressDialog = new Dialog(getActivity());
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setContentView(R.layout.custom_progressbar);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        }

        progressDialog.setCancelable(false);
        progressDialog.show();
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
                progressDialogCustom = new Dialog(getActivity());
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


    //For MoneyRequest Screen
    //Convert time zone
    // date return format Dec 21, 2017 2:01 am
    public String timeZoneConverter(String serverDate,String timeZone){

        String returnDate = "";
        try {
            SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+00:00'", Locale.ENGLISH);
            sourceFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date parsed = sourceFormat.parse(serverDate);
            TimeZone tz = TimeZone.getTimeZone(timeZone);
            SimpleDateFormat destFormat = new SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.ENGLISH);
            destFormat.setTimeZone(tz);
            returnDate = destFormat.format(parsed);

        }catch (Exception e){}

        return returnDate;

    }

    public Object getCacheData(String URL,Class model){

        Object response ="empty";
        RequestQueue queue = null;
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());
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

    private static final String arabic = "\u06f0\u06f1\u06f2\u06f3\u06f4\u06f5\u06f6\u06f7\u06f8\u06f9";
    public static String arabicToDecimal(String number) {
        char[] chars = new char[number.length()];
        for(int i=0;i<number.length();i++) {
            char ch = number.charAt(i);
            if (ch >= 0x0660 && ch <= 0x0669)
                ch -= 0x0660 - '0';
            else if (ch >= 0x06f0 && ch <= 0x06F9)
                ch -= 0x06f0 - '0';
            chars[i] = ch;
        }
        return new String(chars);
    }
}
