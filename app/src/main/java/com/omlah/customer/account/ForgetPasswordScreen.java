package com.omlah.customer.account;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.omlah.customer.R;
import com.omlah.customer.base.BaseActivity;
import com.omlah.customer.common.LoginSession;
import com.omlah.customer.service.RequestID;
import com.omlah.customer.service.ServerListener;
import com.omlah.customer.service.ServerRequest;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 16-11-2017.
 */

public class ForgetPasswordScreen extends BaseActivity implements ServerListener{

    //Create class objects
    private ServerRequest serverRequest;
    LoginSession loginSession;

    //Create xml file
    @BindView(R.id.passwordEditText)EditText passwordEditText;
    @BindView(R.id.otpEditText)EditText otpEditText;
    @BindView(R.id.continueButton)Button continueButton;
    @BindView(R.id.backIconImageView)
    ImageView backIconImageView;

    String mobileNumber="",responseOtp = "";

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.forget_password);
        hideActionBar();

        //Initialize xml file
        ButterKnife.bind(this);

        //Initialize class objects
        serverRequest            = ServerRequest.getInstance(this);
        loginSession = LoginSession.getInstance(this);


        //Get Intent values
        final Intent intent = getIntent();
        if(intent!=null){
            mobileNumber = intent.getStringExtra("mobileNumber");
            responseOtp  = intent.getStringExtra("otp");
        }

        if(loginSession.get_OTP_VERIFICATION()){
            otpEditText.setText("");
        }else{
            otpEditText.setText("1234 ( test OTP )");
        }

        //continue button click event
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String password = passwordEditText.getText().toString().trim();
                String otp      = otpEditText.getText().toString().trim();

                if(password.isEmpty()){
                    passwordEditText.setError(getString(R.string.ENTERNEWPASSWORD));

                }/*else if(password.length() < 6){
                    passwordEditText.setError("please enter valid password");

                }else if(password.replaceAll("[0-9]","").length() == 0){
                    passwordEditText.setError("please enter valid password");

                }else if(password.replaceAll("[^0-9]", "").length() == 0){
                    passwordEditText.setError("please enter valid password");

                }*/else if(otp.isEmpty()){
                    otpEditText.setError(getString(R.string.ENTEROTP));

                }else if(!otp.equals(responseOtp)){
                    otpEditText.setError(getString(R.string.ENTERVALIDOTP));

                }else if(!isConnectingToInternet()){
                    noInternetAlertDialog();

                }else{

                    final Map<String, String> param = new HashMap<String, String>();
                    param.put("phone_number",mobileNumber);
                    param.put("otp",otp);
                    param.put("password",password);
                    showProgressDialog();
                    serverRequest.createRequest(ForgetPasswordScreen.this,param, RequestID.REQ_SET_NEW_PASSWORD,"POST","");

                }
            }
        });

        backIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });
    }

    @Override
    public void onSuccess(Object result, RequestID requestID) {

        hideProgressDialog();
        toast(result.toString());
        finish();

    }

    @Override
    public void onFailure(String error, RequestID requestID) {

        hideProgressDialog();
        toast(error);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("otp")) {
                final String message = intent.getStringExtra("message");

                Log.e("message",message);

                if(message.toUpperCase().contains("POPPAY")){

                  otpEditText.setText(responseOtp);
                }

            }
        }
    };



    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("otp"));
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }
}
