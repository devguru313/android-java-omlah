package com.omlah.customer.otp;

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

import com.omlah.customer.R;
import com.omlah.customer.base.BaseActivity;
import com.omlah.customer.common.LoginSession;
import com.omlah.customer.service.RequestID;
import com.omlah.customer.service.ServerListener;
import com.omlah.customer.service.ServerRequestwithHeader;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 16-11-2017.
 */

public class ForgetPassCode extends BaseActivity implements ServerListener{

    //Create class objects
    LoginSession loginSession;
    ServerRequestwithHeader serverRequestwithHeader;

    //Create xml file
    @BindView(R.id.securityPinEditText)EditText securityPinEditText;
    @BindView(R.id.otpEditText)EditText otpEditText;
    @BindView(R.id.continueButton)Button continueButton;

    String responseOtp = "";

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.forget_passcode);
        hideActionBar();

        //Initialize xml file
        ButterKnife.bind(this);

        //Initialize class objects
        serverRequestwithHeader = ServerRequestwithHeader.getInstance(this);
        loginSession  = LoginSession.getInstance(this);

        //Get Intent values
        final Intent intent = getIntent();
        if(intent!=null){
            responseOtp  = intent.getStringExtra("OTP");

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

                String pin      = securityPinEditText.getText().toString().trim();
                String otp      = otpEditText.getText().toString().trim();

                if(pin.isEmpty()) {
                    securityPinEditText.setError(getResources().getString(R.string.PleaseEnterNewPin));

                }else if(otp.isEmpty()){
                    otpEditText.setError(getResources().getString(R.string.PleaseEnterOTP));

                }/*else if(!otp.equals(responseOtp)){
                    otpEditText.setError(getResources().getString(R.string.PleaseEnterValidOTP));

                }*/else if(!isConnectingToInternet()){
                    noInternetAlertDialog();

                }else{

                    final Map<String, String> param = new HashMap<String, String>();
                    param.put("pass_code",pin);
                    showProgressDialog();
                    serverRequestwithHeader.createRequest(ForgetPassCode.this,param, RequestID.REQ_ADD_PASSCODE,"POST","");

                }
            }
        });

    }


    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("otp")) {
                final String message = intent.getStringExtra("message");

                Log.e("message",message);
                otpEditText.setText(responseOtp);

            }
        }
    };


    @Override
    public void onSuccess(Object result, RequestID requestID) {

        hideProgressDialog();
        toast(getResources().getString(R.string.PinChanged));
        finish();
    }

    @Override
    public void onFailure(String error, RequestID requestID) {

        hideProgressDialog();
        toast(error.toString());
    }

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
