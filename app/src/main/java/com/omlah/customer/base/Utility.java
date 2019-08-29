package com.omlah.customer.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.omlah.customer.R;

import java.util.ArrayList;

/**
 * Created by admin on 07-08-2017.
 */

public class Utility {

    public static Utility utility = null;
    Activity activity;
    Toast toast;
    Dialog progressDialog;
    public static boolean update_check = false;
    public static ArrayList<String>offername = new ArrayList<>();

    public static String eventFee = "0";
    public static String loadmoneyFeeOption = "";
    public static String loadmoneyFeeAmount = "0";
    public static String loadmoneyExtraFeeAmount = "0";

    public static boolean locationUpdate = true;
    public static String SCREEN_NAME   = "HOMESCREEN";
    public static String TRANSACTION_NUMBER = "";

    public static Utility getInstance(Activity activity) {

        if (utility == null) {

            utility = new Utility(activity);
        }

        return utility;
    }

    /***********************************Constructor*************************************/
    public Utility(Activity activity) {
        super();

        this.activity = activity;

    }


    /***********************************Toast Method*************************************/
    public void toast(String message) {
        toast = Toast.makeText(activity, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)

                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }

    /**********************************Alert dialog Method*************************************/
    public void noInternetAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);

        alertDialog.setTitle("No Internet Connection!!!");

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    public void showProgressDialog() {
        try {
            if (progressDialog == null) {

                progressDialog = new Dialog(activity);
                progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                progressDialog.setContentView(R.layout.custom_progressbar);
                progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                progressDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            }

            progressDialog.setCancelable(false);
            progressDialog.show();
        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    public void hideProgressDialog() {

        try {

            if (progressDialog.isShowing()) {

                progressDialog.dismiss();
            }

        } catch (Exception e) {

            e.printStackTrace();

        }

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
}
