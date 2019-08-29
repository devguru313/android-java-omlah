package com.omlah.customer.otp;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.omlah.customer.R;
import com.omlah.customer.account.GetStartedScreen;
import com.omlah.customer.base.BaseActivity;
import com.omlah.customer.base.BaseScreen;
import com.omlah.customer.base.Utility;
import com.omlah.customer.common.LoginSession;
import com.omlah.customer.common.fingerprintlib.FingerPrintAuthCallback;
import com.omlah.customer.common.fingerprintlib.FingerPrintAuthHelper;
import com.omlah.customer.service.RequestID;
import com.omlah.customer.service.ServerListener;
import com.omlah.customer.service.ServerRequestwithHeader;
import com.omlah.customer.tabfeed.FeedScreen;
import com.omlah.customer.tabhome.MyTransactionScreen;
import com.omlah.customer.tabmore.MoneyRequestListScreen;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 10-11-2017.
 */

public class LuncherPassCodeCheck extends BaseActivity implements View.OnClickListener,ServerListener, FingerPrintAuthCallback {

    Utility utility;
    LoginSession loginSession;
    ServerRequestwithHeader serverRequestwithHeader;

    //Create xml objects
    @BindView(R.id.homeTextView) TextView homeTextView;
    @BindView(R.id.codeImageView1) ImageView codeImageView1;
    @BindView(R.id.codeImageView2) ImageView codeImageView2;
    @BindView(R.id.codeImageView3) ImageView codeImageView3;
    @BindView(R.id.codeImageView4) ImageView codeImageView4;
    @BindView(R.id.fingerPrintImageView) ImageView fingerPrintImageView;
    @BindView(R.id.passcodeSection)
    LinearLayout passcodeSection;

    @BindView(R.id.button0) Button button0;
    @BindView(R.id.button1) Button button1;
    @BindView(R.id.button2) Button button2;
    @BindView(R.id.button3) Button button3;
    @BindView(R.id.button4) Button button4;
    @BindView(R.id.button5) Button button5;
    @BindView(R.id.button6) Button button6;
    @BindView(R.id.button7) Button button7;
    @BindView(R.id.button8) Button button8;
    @BindView(R.id.button9) Button button9;
    @BindView(R.id.forgetButton) Button forgetButton;
    @BindView(R.id.buttonDelete) Button buttonDelete;

    String createPasscode = "";
    boolean goNextScreen = false;

    private FingerPrintAuthHelper mFingerPrintAuthHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.passcode_create_screen);
        hideActionBar();

        ButterKnife.bind(this);
        homeTextView.setText(getResources().getString(R.string.EnterPin));

        //Initialize class objects
        loginSession = LoginSession.getInstance(this);
        utility = Utility.getInstance(this);
        serverRequestwithHeader = ServerRequestwithHeader.getInstance(this);

        button0.setOnClickListener(this);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        button6.setOnClickListener(this);
        button7.setOnClickListener(this);
        button8.setOnClickListener(this);
        button9.setOnClickListener(this);
        buttonDelete.setOnClickListener(this);
        forgetButton.setOnClickListener(this);

        //Check fingerPrint option
        try{
            if(loginSession.getFingerOption().equalsIgnoreCase("ON")){

                if(isFingerPrintEnabled()){
                    fingerPrintImageView.setVisibility(View.VISIBLE);
                    homeTextView.setText(getResources().getString(R.string.TouchIDPasscode));
                }else{
                    fingerPrintImageView.setVisibility(View.INVISIBLE);
                    homeTextView.setText(getResources().getString(R.string.EnterPin));
                }

            }else{

                fingerPrintImageView.setVisibility(View.INVISIBLE);
                homeTextView.setText(getResources().getString(R.string.EnterPin));

            }

            mFingerPrintAuthHelper = FingerPrintAuthHelper.getHelper(this, this);
        }catch (Exception e){e.printStackTrace();}

    }

    private boolean isFingerPrintEnabled() {
        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                FingerprintManager fingerprintManager = (FingerprintManager) this.getSystemService(Context.FINGERPRINT_SERVICE);
                if (!fingerprintManager.isHardwareDetected()) {
                    return false;
                } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                    return false;
                } else {
                    return true;
                }
            }
        }catch (Exception e){e.printStackTrace();
            return false;
        }

        return false;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.button0:

                createPasscode += "0";
                checkPassCode();

                break;

            case R.id.button1:

                createPasscode += "1";
                checkPassCode();

                break;

            case R.id.button2:

                createPasscode += "2";
                checkPassCode();

                break;

            case R.id.button3:

                createPasscode += "3";
                checkPassCode();

                break;

            case R.id.button4:

                createPasscode += "4";
                checkPassCode();

                break;

            case R.id.button5:

                createPasscode += "5";
                checkPassCode();

                break;

            case R.id.button6:

                createPasscode += "6";
                checkPassCode();

                break;

            case R.id.button7:

                createPasscode += "7";
                checkPassCode();

                break;

            case R.id.button8:

                createPasscode += "8";
                checkPassCode();

                break;

            case R.id.button9:

                createPasscode += "9";
                checkPassCode();

                break;

            case R.id.buttonDelete:

                try{
                    createPasscode = createPasscode.substring(0,createPasscode.length()-1);
                    Log.e("createPasscode",createPasscode);
                    checkPassCode();
                }catch (Exception e){e.printStackTrace();}

                break;

            case R.id.forgetButton:

                Intent forgetPassCodeintent = new Intent(LuncherPassCodeCheck.this, ForgetPassCode.class);
                forgetPassCodeintent.putExtra("OTP","1234 ( test OTP )");
                startActivity(forgetPassCodeintent);

               /* final Map<String, String> param = new HashMap<String, String>();
                showProgressDialog();
                serverRequestwithHeader.createRequest(LuncherPassCodeCheck.this,param, RequestID.REQ_RESEND_OTP,"POST","");
*/
                break;
        }
    }

    private void checkPassCode() {

        if(createPasscode.length() == 0){
            codeImageView1.setImageDrawable(getResources().getDrawable(R.drawable.gray_round));
            codeImageView2.setImageDrawable(getResources().getDrawable(R.drawable.gray_round));
            codeImageView3.setImageDrawable(getResources().getDrawable(R.drawable.gray_round));
            codeImageView4.setImageDrawable(getResources().getDrawable(R.drawable.gray_round));

        }else if(createPasscode.length() == 1){
            codeImageView1.setImageDrawable(getResources().getDrawable(R.drawable.gradient_round));
            codeImageView2.setImageDrawable(getResources().getDrawable(R.drawable.gray_round));
            codeImageView3.setImageDrawable(getResources().getDrawable(R.drawable.gray_round));
            codeImageView4.setImageDrawable(getResources().getDrawable(R.drawable.gray_round));

        }else if(createPasscode.length() == 2){
            codeImageView1.setImageDrawable(getResources().getDrawable(R.drawable.gradient_round));
            codeImageView2.setImageDrawable(getResources().getDrawable(R.drawable.gradient_round));
            codeImageView3.setImageDrawable(getResources().getDrawable(R.drawable.gray_round));
            codeImageView4.setImageDrawable(getResources().getDrawable(R.drawable.gray_round));

        }else if(createPasscode.length() == 3){
            codeImageView1.setImageDrawable(getResources().getDrawable(R.drawable.gradient_round));
            codeImageView2.setImageDrawable(getResources().getDrawable(R.drawable.gradient_round));
            codeImageView3.setImageDrawable(getResources().getDrawable(R.drawable.gradient_round));
            codeImageView4.setImageDrawable(getResources().getDrawable(R.drawable.gray_round));

        }else if(createPasscode.length() == 4) {
            codeImageView1.setImageDrawable(getResources().getDrawable(R.drawable.gradient_round));
            codeImageView2.setImageDrawable(getResources().getDrawable(R.drawable.gradient_round));
            codeImageView3.setImageDrawable(getResources().getDrawable(R.drawable.gradient_round));
            codeImageView4.setImageDrawable(getResources().getDrawable(R.drawable.gradient_round));


            final Map<String, String> param = new HashMap<String, String>();
            param.put("pass_code", createPasscode);
            showProgressDialog();
            serverRequestwithHeader.createRequest(LuncherPassCodeCheck.this, param, RequestID.REQ_VERIFY_PASSCODE, "POST", "");

        }

    }

    @Override
    public void onSuccess(Object result, RequestID requestID) {
        hideProgressDialog();
        switch (requestID) {

            case REQ_RESEND_OTP:
                toast(getResources().getString(R.string.OTPsentsuccessfully));
                Intent forgetPassCodeintent = new Intent(LuncherPassCodeCheck.this, ForgetPassCode.class);
                forgetPassCodeintent.putExtra("OTP", result.toString().trim());
                startActivity(forgetPassCodeintent);
                break;

            case REQ_VERIFY_PASSCODE:

                if(utility.SCREEN_NAME.equalsIgnoreCase("MyTransactionScreen")){
                    Intent intent = new Intent(LuncherPassCodeCheck.this, MyTransactionScreen.class);
                    intent.putExtra("ID",utility.TRANSACTION_NUMBER);
                    intent.putExtra("type","RECEIVED");
                    startActivity(intent);
                    utility.SCREEN_NAME = "HOMESCREEN";
                    goNextScreen = true;

                }else if(utility.SCREEN_NAME.equalsIgnoreCase("MoneyRequestListScreen")){
                    Intent intent = new Intent(LuncherPassCodeCheck.this, MoneyRequestListScreen.class);
                    startActivity(intent);
                    utility.SCREEN_NAME = "HOMESCREEN";
                    goNextScreen = true;

                }else if(utility.SCREEN_NAME.equalsIgnoreCase("FeedScreen")){
                    Intent intent = new Intent(LuncherPassCodeCheck.this,FeedScreen.class);
                    startActivity(intent);
                    utility.SCREEN_NAME = "HOMESCREEN";
                    goNextScreen = true;
                }else{
                    Intent intent = new Intent(LuncherPassCodeCheck.this, BaseScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }

                break;

        }

    }

    @Override
    public void onFailure(String error, RequestID requestID) {
        hideProgressDialog();

        switch (requestID) {

            case REQ_RESEND_OTP:

                try {
                    toast(error);
                } catch (Exception e) {
                }

                break;

            case REQ_VERIFY_PASSCODE:

                if (error.equalsIgnoreCase("logout")) {
                    loginSession.logout();
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancelAll();
                    Intent intent = new Intent(this, GetStartedScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {

                    passcodeSection.startAnimation( AnimationUtils.loadAnimation(LuncherPassCodeCheck.this,R.anim.shake));
                    Snackbar.make(findViewById(android.R.id.content), error, Snackbar.LENGTH_SHORT).show();
                    codeImageView1.setImageDrawable(getResources().getDrawable(R.drawable.gray_round));
                    codeImageView2.setImageDrawable(getResources().getDrawable(R.drawable.gray_round));
                    codeImageView3.setImageDrawable(getResources().getDrawable(R.drawable.gray_round));
                    codeImageView4.setImageDrawable(getResources().getDrawable(R.drawable.gray_round));
                    createPasscode = "";
                }

                break;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mFingerPrintAuthHelper.startAuth();

        if(goNextScreen){
            utility.SCREEN_NAME = "HOMESCREEN";
            Intent intent = new Intent(LuncherPassCodeCheck.this, BaseScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        goNextScreen = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFingerPrintAuthHelper.stopAuth();
    }

    @Override
    public void onNoFingerPrintHardwareFound() {
        //Your device does not have finger print scanner
    }

    @Override
    public void onNoFingerPrintRegistered() {
        toast("Here are no finger prints registered on this device");
        //There are no finger prints registered on this device. Please register your finger from settings.
    }

    @Override
    public void onBelowMarshmallow() {
        //You are running older version of android that does not support finger print authentication.
    }

    @Override
    public void onAuthSuccess(FingerprintManager.CryptoObject cryptoObject) {

        if(loginSession.getFingerOption().equalsIgnoreCase("ON")){

            //toast(result.toString());
            if(utility.SCREEN_NAME.equalsIgnoreCase("MyTransactionScreen")){
                Intent intent = new Intent(LuncherPassCodeCheck.this, MyTransactionScreen.class);
                intent.putExtra("ID",utility.TRANSACTION_NUMBER);
                intent.putExtra("type","RECEIVED");
                startActivity(intent);
                utility.SCREEN_NAME = "HOMESCREEN";
                goNextScreen = true;

            }else if(utility.SCREEN_NAME.equalsIgnoreCase("MoneyRequestListScreen")){
                Intent intent = new Intent(LuncherPassCodeCheck.this, MoneyRequestListScreen.class);
                startActivity(intent);
                utility.SCREEN_NAME = "HOMESCREEN";
                goNextScreen = true;

            }else if(utility.SCREEN_NAME.equalsIgnoreCase("FeedScreen")){
                Intent intent = new Intent(LuncherPassCodeCheck.this,FeedScreen.class);
                startActivity(intent);
                utility.SCREEN_NAME = "HOMESCREEN";
                goNextScreen = true;
            }else{
                Intent intent = new Intent(LuncherPassCodeCheck.this, BaseScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

        }

    }

    @Override
    public void onAuthFailed(int errorCode, String errorMessage) {

        Snackbar.make(findViewById(android.R.id.content),
                "Cannot recognize your finger print", Snackbar.LENGTH_SHORT).show();
    }


}
