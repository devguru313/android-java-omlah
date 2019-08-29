package com.omlah.customer.base;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.omlah.customer.R;
import com.omlah.customer.account.GetStartedScreen;
import com.omlah.customer.common.LoginSession;
import com.omlah.customer.introscreen.IntroActivity;
import com.omlah.customer.otp.LuncherPassCodeCheck;
import com.omlah.customer.otp.PassCodeCreateScreen;

import org.jsoup.Jsoup;

import java.io.IOException;

/**
 * Created by admin on 30-06-2017.
 */

public class SplashScreen extends BaseActivity {

    //Create class objects
    Dialog updatedialog;
    Handler handler;
    LoginSession loginSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setDefaultLanguage("en");
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_screen);
        hideActionBar();

        //Initialize objects
        loginSession = LoginSession.getInstance(SplashScreen.this);

        //Get device id
        String android_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        //getMacID
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        String macAddress = wInfo.getMacAddress();
        loginSession.saveDeviceId(android_id, macAddress);

        if (loginSession.isLoggedIn()) {
            passCodeCheck();
        } else {
            IntroCheck();
        }

        /*if(isConnectingToInternet()){
            try{
                new VersionChecker().execute();
            }catch (Exception e){e.printStackTrace();
                if (loginSession.isLoggedIn()) {
                    passCodeCheck();
                } else {
                    IntroCheck();
                }
            }

        }*/

    }

    //Check intro screen and go to LuncherPassCodeCheck OR PassCodeCreateScreen Activity
    public void passCodeCheck() {
        if (loginSession.isPassCodeSet()) {
            Intent intent = new Intent(SplashScreen.this, LuncherPassCodeCheck.class);//LuncherPassCodeCheck
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            Intent intent = new Intent(SplashScreen.this, PassCodeCreateScreen.class);//PassCodeCreateScreen
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    //Check intro screen and go to Intro OR Started Activity
    public void IntroCheck() {
        if (!loginSession.isIntroIn()) {
            Intent intent = new Intent(SplashScreen.this, IntroActivity.class); //IntroActivity
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            Intent intent = new Intent(SplashScreen.this, GetStartedScreen.class);//GetStartedScreen
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private class VersionChecker extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        String newVersion;
        @Override
        protected String doInBackground(String... params) {

            try {
                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + "com.rpay.customer" + "&hl=en")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div.hAyfc:nth-child(4) > span:nth-child(2) > div:nth-child(1) > span:nth-child(1)")
                        .first()
                        .ownText();
                Log.e("newVersion",newVersion);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return newVersion;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                String version = pInfo.versionName;

                Log.e("version",version);

                if (newVersion != null && !newVersion.isEmpty()) {
                    hideProgressDialog();
                    if(!newVersion.equals(version)){
                        openUpdateDialogue();
                    }else{
                        if (loginSession.isLoggedIn()) {
                            passCodeCheck();
                        } else {
                            IntroCheck();
                        }
                    }
                }else{
                    hideProgressDialog();
                    if (loginSession.isLoggedIn()) {
                        passCodeCheck();
                    } else {
                        IntroCheck();
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                hideProgressDialog();
                if (loginSession.isLoggedIn()) {
                    passCodeCheck();
                } else {
                    IntroCheck();
                }
            }
        }
    }

    private void openUpdateDialogue() {

        try{
            if (updatedialog == null) {
                updatedialog = new Dialog(SplashScreen.this);
                updatedialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                updatedialog.setContentView(R.layout.dialog_for_update);
                updatedialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                updatedialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            }

            Button updateButton = (Button)updatedialog.findViewById(R.id.updateButton);
            Button notnowButton = (Button)updatedialog.findViewById(R.id.notnowButton);

            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // final String appPackageName = getActivity().getPackageName();
                    final String appPackageName = "com.rpay.customer";
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                }
            });

            notnowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updatedialog.dismiss();
                    if (loginSession.isLoggedIn()) {
                        passCodeCheck();
                    } else {
                        IntroCheck();
                    }
                }
            });

            updatedialog.setCancelable(false);
            updatedialog.show();

        }catch (Exception e){e.printStackTrace();}

    }
}