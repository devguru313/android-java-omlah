package com.omlah.customer.otp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
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

/**
 * Created by admin on 10-11-2017.
 */

public class OldPassCodeVerified extends BaseActivity implements View.OnClickListener,ServerListener {

    LoginSession loginSession;
    ServerRequestwithHeader serverRequestwithHeader;

    //Create xml objects
    @BindView(R.id.homeTextView)
    TextView homeTextView;
    @BindView(R.id.codeImageView1)
    ImageView codeImageView1;
    @BindView(R.id.codeImageView2) ImageView codeImageView2;
    @BindView(R.id.codeImageView3) ImageView codeImageView3;
    @BindView(R.id.codeImageView4) ImageView codeImageView4;

    @BindView(R.id.button0)
    Button button0;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.passcode_create_screen);
        hideActionBar();

        ButterKnife.bind(this);
        homeTextView.setText(getResources().getString(R.string.OldPin));

        //Initialize class objects
        loginSession = LoginSession.getInstance(this);
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

                if(loginSession.get_OTP_VERIFICATION()){
                    final Map<String, String> param = new HashMap<String, String>();
                    showProgressDialog();
                    serverRequestwithHeader.createRequest(OldPassCodeVerified.this, param, RequestID.REQ_RESEND_OTP, "POST", "");
                }else{
                    Intent forgetPassCodeintent = new Intent(OldPassCodeVerified.this,ForgetPassCode.class);
                    forgetPassCodeintent.putExtra("OTP","1234 ( test OTP )");
                    startActivity(forgetPassCodeintent);
                }

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

            //Check pin
            if(isConnectingToInternet()){
                final Map<String, String> param = new HashMap<String, String>();
                param.put("pass_code", createPasscode);
                showProgressDialog();
                serverRequestwithHeader.createRequest(OldPassCodeVerified.this, param, RequestID.REQ_VERIFY_PASSCODE, "POST", "");
            }else{
                noInternetAlertDialog();
            }

        }

    }

    @Override
    public void onSuccess(Object result, RequestID requestID) {

        hideProgressDialog();

        switch (requestID){

            case REQ_RESEND_OTP:

                Intent forgetPassCodeintent = new Intent(OldPassCodeVerified.this,ForgetPassCode.class);
                forgetPassCodeintent.putExtra("OTP",result.toString().trim());
                startActivity(forgetPassCodeintent);

                break;

            case REQ_VERIFY_PASSCODE:
                    setResult(4);
                    finish();
                break;
        }
    }

    @Override
    public void onFailure(String error, RequestID requestID) {

        hideProgressDialog();

        switch (requestID) {

            case REQ_RESEND_OTP:

                toast(error);

                break;

            case REQ_VERIFY_PASSCODE:

                toast(error);
                codeImageView1.setImageDrawable(getResources().getDrawable(R.drawable.gray_round));
                codeImageView2.setImageDrawable(getResources().getDrawable(R.drawable.gray_round));
                codeImageView3.setImageDrawable(getResources().getDrawable(R.drawable.gray_round));
                codeImageView4.setImageDrawable(getResources().getDrawable(R.drawable.gray_round));
                createPasscode = "";

                break;
        }
    }
}

