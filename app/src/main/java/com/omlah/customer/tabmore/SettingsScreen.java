package com.omlah.customer.tabmore;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.omlah.customer.R;
import com.omlah.customer.account.ChangePasswordScreen;
import com.omlah.customer.base.BaseActivity;
import com.omlah.customer.common.LoginSession;
import com.omlah.customer.otp.OldPassCodeVerified;
import com.omlah.customer.otp.PassCodeChangeScreen;
import com.omlah.customer.service.ServerRequestwithHeader;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 26-09-2017.
 */

public class SettingsScreen extends BaseActivity{

    //Create class objects
    LoginSession loginSession;
    ServerRequestwithHeader serverRequestwithHeader;

    //Create xml files
    @BindView(R.id.changePassCodeLayout)RelativeLayout changePassCodeLayout;
    @BindView(R.id.changePasswordLayout)RelativeLayout changePasswordLayout;
    @BindView(R.id.englishSetButton)RelativeLayout englishSetButton;
    @BindView(R.id.arabicSetButton)RelativeLayout arabicSetButton;
    @BindView(R.id.portugueseButton)RelativeLayout portugueseButton;
    @BindView(R.id.hindiButton)RelativeLayout hindiButton;
    @BindView(R.id.englishTickImageView)ImageView englishTickImageView;
    @BindView(R.id.arabicTickImageView)ImageView arabicTickImageView;
    @BindView(R.id.portugueseTickImageView)ImageView portugueseTickImageView;
    @BindView(R.id.hindiTickImageView)ImageView hindiTickImageView;
    @BindView(R.id.notificationBlockLayout)RelativeLayout notificationBlockLayout;
    @BindView(R.id.notificationOnOffTextView)TextView notificationOnOffTextView;
    @BindView(R.id.fingerprintAuthLayout)RelativeLayout fingerprintAuthLayout;
    @BindView(R.id.fingerOnOffTextView)TextView fingerOnOffTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_screen);
        showBackArrow();
        setActionBarTitle(getResources().getString(R.string.Settings));

        //initialize xml file
        ButterKnife.bind(this);

        //Initialize class objects
        loginSession = LoginSession.getInstance(this);
        serverRequestwithHeader = ServerRequestwithHeader.getInstance(this);

        //Check fingerprint option
        if(loginSession.getFingerOption().equalsIgnoreCase("ON")){
            fingerOnOffTextView.setText(getResources().getString(R.string.On));
        }else{
            fingerOnOffTextView.setText(getResources().getString(R.string.Off));
        }

        //Check fingerprint option
        if(loginSession.getFingerOption().equalsIgnoreCase("ON")){
            fingerOnOffTextView.setText(getResources().getString(R.string.On));
        }else{
            fingerOnOffTextView.setText(getResources().getString(R.string.Off));
        }


        //Check notification switch
        //Check notification option
        if(loginSession.isNotificationEnabled()){
            notificationOnOffTextView.setText(getResources().getString(R.string.On));
        }else{
            notificationOnOffTextView.setText(getResources().getString(R.string.Off));
        }


        //changePassCode event
        changePassCodeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(SettingsScreen.this, OldPassCodeVerified.class);
                startActivityForResult(intent,4);

            }
        });

        //changePassword event
        changePasswordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent changePasswordIntent = new Intent(SettingsScreen.this, ChangePasswordScreen.class);
                startActivity(changePasswordIntent);

            }
        });
    Log.e("Language", Locale.getDefault().getLanguage());
      if(Locale.getDefault().getLanguage().equalsIgnoreCase("en")){

             englishTickImageView.setVisibility(View.VISIBLE);
           /*  arabicTickImageView.setVisibility(View.INVISIBLE);
             portugueseTickImageView.setVisibility(View.INVISIBLE);
             hindiTickImageView.setVisibility(View.INVISIBLE);*/

         }else if(Locale.getDefault().getLanguage().equalsIgnoreCase("ar")){

             arabicTickImageView.setVisibility(View.VISIBLE);
         /* portugueseTickImageView.setVisibility(View.INVISIBLE);
          hindiTickImageView.setVisibility(View.INVISIBLE);
          englishTickImageView.setVisibility(View.INVISIBLE);*/
         }
        else if(Locale.getDefault().getLanguage().equalsIgnoreCase("por")){

            portugueseTickImageView.setVisibility(View.VISIBLE);
         /* hindiTickImageView.setVisibility(View.INVISIBLE);
          englishTickImageView.setVisibility(View.INVISIBLE);
          arabicTickImageView.setVisibility(View.INVISIBLE);*/
        }else if (Locale.getDefault().getLanguage().equalsIgnoreCase("hi")){

            hindiTickImageView.setVisibility(View.VISIBLE);
          /*portugueseTickImageView.setVisibility(View.INVISIBLE);
          englishTickImageView.setVisibility(View.INVISIBLE);
          arabicTickImageView.setVisibility(View.INVISIBLE);*/
        }

      /* switch(4)
        {
            case 0:
                Locale.getDefault().getDisplayLanguage().equalsIgnoreCase("English");
                englishTickImageView.setVisibility(View.VISIBLE);
                arabicTickImageView.setVisibility(View.INVISIBLE);
                portugueseTickImageView.setVisibility(View.INVISIBLE);
                hindiTickImageView.setVisibility(View.INVISIBLE);
                break;
            case 1:
                Locale.getDefault().getDisplayLanguage().equalsIgnoreCase("Arabic");
                arabicTickImageView.setVisibility(View.VISIBLE);
                portugueseTickImageView.setVisibility(View.INVISIBLE);
                hindiTickImageView.setVisibility(View.INVISIBLE);
                englishTickImageView.setVisibility(View.INVISIBLE);
                break;
            case 2:
                Locale.getDefault().getDisplayLanguage().equalsIgnoreCase("Portuguese");
                portugueseTickImageView.setVisibility(View.VISIBLE);
                arabicTickImageView.setVisibility(View.INVISIBLE);
                hindiTickImageView.setVisibility(View.INVISIBLE);
                englishTickImageView.setVisibility(View.INVISIBLE);
                break;
            case 3:
                Locale.getDefault().getDisplayLanguage().equalsIgnoreCase("Hindi");
                hindiTickImageView.setVisibility(View.VISIBLE);
                portugueseTickImageView.setVisibility(View.INVISIBLE);
                arabicTickImageView.setVisibility(View.INVISIBLE);
                englishTickImageView.setVisibility(View.INVISIBLE);
                break;
                default:
                    break;
        }*/
        englishSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLanguage("en");

                englishTickImageView.setVisibility(View.VISIBLE);
                //arabicTickImageView.setVisibility(View.INVISIBLE);
                //portugueseTickImageView.setVisibility(View.INVISIBLE);
                //hindiTickImageView.setVisibility(View.INVISIBLE);
            }
        });

        arabicSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLanguage("ar");

                arabicTickImageView.setVisibility(View.VISIBLE);
               /* arabicTickImageView.setVisibility(View.INVISIBLE);
                hindiTickImageView.setVisibility(View.INVISIBLE);
                englishTickImageView.setVisibility(View.INVISIBLE);*/
            }
        });
       portugueseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLanguage("por");

               portugueseTickImageView.setVisibility(View.VISIBLE);
               /* arabicTickImageView.setVisibility(View.INVISIBLE);
                hindiTickImageView.setVisibility(View.INVISIBLE);
                englishTickImageView.setVisibility(View.INVISIBLE);*/
            }
        });
       hindiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLanguage("hi");
                hindiTickImageView.setVisibility(View.VISIBLE);
               /* portugueseTickImageView.setVisibility(View.INVISIBLE);
                arabicTickImageView.setVisibility(View.INVISIBLE);
                englishTickImageView.setVisibility(View.INVISIBLE);*/
            }
        });
        fingerprintAuthLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(loginSession.getFingerOption().equalsIgnoreCase("ON")){
                    loginSession.setFingerOption("OFF");
                    fingerOnOffTextView.setText(getResources().getString(R.string.Off));
                }else{
                    loginSession.setFingerOption("ON");
                    fingerOnOffTextView.setText(getResources().getString(R.string.On));
                }
            }
        });


        notificationBlockLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (loginSession.isNotificationEnabled()) {
                    loginSession.setNotificationSetting(false);
                    notificationOnOffTextView.setText(getResources().getString(R.string.Off));
                } else {
                    loginSession.setNotificationSetting(true);
                    notificationOnOffTextView.setText(getResources().getString(R.string.On));
                }

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 4){

            if(resultCode == 4){

                 Intent intent = new Intent(SettingsScreen.this, PassCodeChangeScreen.class);
                startActivity(intent);
            }
        }
    }
}
