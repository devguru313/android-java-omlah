package com.omlah.customer.otp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

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

public class OtpVerificationScreen extends BaseActivity implements ServerListener{

    LoginSession loginSession;
    ServerRequestwithHeader serverRequestwithHeader;

    //Create xml objects
    @BindView(R.id.optVerification)TextView optVerification;
    @BindView(R.id.topTextView)TextView topTextView;
    @BindView(R.id.codeEditText1)EditText codeEditText1;
    @BindView(R.id.codeEditText2)EditText codeEditText2;
    @BindView(R.id.codeEditText3)EditText codeEditText3;
    @BindView(R.id.codeEditText4)EditText codeEditText4;
    @BindView(R.id.resendOtpButton)TextView resendOtpButton;

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    String OTP = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.otp_screen);
        hideActionBar();

        ButterKnife.bind(this);

        //Initialize class objects
        loginSession = LoginSession.getInstance(this);
        serverRequestwithHeader  = ServerRequestwithHeader.getInstance(this);


        //get intent vlues
        Intent intent = getIntent();
        if(intent!=null){

            OTP = intent.getStringExtra("OTP");
        }

        codeEditText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {

                if(codeEditText1.getText().toString().trim().length() > 0){
                    codeEditText2.requestFocus();
                }else{
                    codeEditText1.requestFocus();
                }

            }
        });


        codeEditText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {

                if(codeEditText2.getText().toString().trim().length() > 0){
                    codeEditText3.requestFocus();
                }else{
                    codeEditText2.requestFocus();
                }

            }
        });

        codeEditText3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {

                if(codeEditText3.getText().toString().trim().length() > 0){
                    codeEditText4.requestFocus();
                }else{
                    codeEditText3.requestFocus();
                }

            }
        });

        codeEditText4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {

                if(codeEditText4.getText().toString().trim().length() > 0){

                    String one   = codeEditText1.getText().toString().trim();
                    String two   = codeEditText2.getText().toString().trim();
                    String three = codeEditText3.getText().toString().trim();
                    String four  = codeEditText4.getText().toString().trim();

                    String FINALOTP = one+two+three+four;

                    if(FINALOTP.equals(OTP)){

                        final Map<String, String> param = new HashMap<String, String>();
                        param.put("otp",OTP);
                        showProgressDialog();
                        serverRequestwithHeader.createRequest(OtpVerificationScreen.this,param, RequestID.REQ_VERIFY_OTP,"POST","");

                    }else{

                        toast("Please enter a correct otp");
                        codeEditText1.setText("");
                        codeEditText2.setText("");
                        codeEditText3.setText("");
                        codeEditText4.setText("");
                        codeEditText1.requestFocus();

                    }

                }else{
                    codeEditText4.requestFocus();
                }

            }
        });

        //Call resendOtpButton
        resendOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Map<String, String> param = new HashMap<String, String>();
                showProgressDialog();
                serverRequestwithHeader.createRequest(OtpVerificationScreen.this,param, RequestID.REQ_RESEND_OTP,"POST","");

            }
        });

    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("otp")) {
                final String message = intent.getStringExtra("message");

                Log.e("message",message);

                if(message.toUpperCase().contains("POPPAY")){

                    codeEditText1.setText(OTP.substring(0,1));
                    codeEditText2.setText(OTP.substring(1,2));
                    codeEditText3.setText(OTP.substring(2,3));
                    codeEditText4.setText(OTP.substring(3,4));
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

    @Override
    public void onSuccess(Object result, RequestID requestID) {

        hideProgressDialog();
        switch (requestID){

            case REQ_VERIFY_OTP:

                Log.e("result.toString()",result.toString());

                toast(result.toString());
                loginSession.setLogin(true);
                loginSession.setOTPVerified(true);
                Intent intent = new Intent(OtpVerificationScreen.this, PassCodeCreateScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                break;

            case REQ_RESEND_OTP:

                OTP = result.toString().trim();
                codeEditText1.setText("");
                codeEditText2.setText("");
                codeEditText3.setText("");
                codeEditText4.setText("");
                codeEditText1.requestFocus();

                break;
        }

    }

    @Override
    public void onFailure(String error, RequestID requestID) {

        hideProgressDialog();
        toast(error);
    }
}
