package com.omlah.customer.otp;

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
 * Created by admin on 09-11-2017.
 */

public class PassCodeChangeScreen extends BaseActivity implements View.OnClickListener,ServerListener {

    LoginSession loginSession;
    ServerRequestwithHeader serverRequestwithHeader;

    //Create xml objects
    @BindView(R.id.homeTextView)
    TextView homeTextView;
    @BindView(R.id.secondTextView) TextView secondTextView;
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
    String generatePassCode="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.passcode_create_screen);
        hideActionBar();

        ButterKnife.bind(this);

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


        forgetButton.setVisibility(View.INVISIBLE);
        forgetButton.setEnabled(false);

        homeTextView.setText(getResources().getString(R.string.NewPin));

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
        }

    }

    private void checkPassCode() {

        Log.e("passcode lenth",""+createPasscode.length());

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

        }else if(createPasscode.length() == 4){
            codeImageView1.setImageDrawable(getResources().getDrawable(R.drawable.gradient_round));
            codeImageView2.setImageDrawable(getResources().getDrawable(R.drawable.gradient_round));
            codeImageView3.setImageDrawable(getResources().getDrawable(R.drawable.gradient_round));
            codeImageView4.setImageDrawable(getResources().getDrawable(R.drawable.gradient_round));

            if(generatePassCode.length() == 0){

                generatePassCode = createPasscode;
                codeImageView1.setImageDrawable(getResources().getDrawable(R.drawable.gray_round));
                codeImageView2.setImageDrawable(getResources().getDrawable(R.drawable.gray_round));
                codeImageView3.setImageDrawable(getResources().getDrawable(R.drawable.gray_round));
                codeImageView4.setImageDrawable(getResources().getDrawable(R.drawable.gray_round));
                createPasscode = "";
                homeTextView.setText(getResources().getString(R.string.NewPin));

            }else{

                if(generatePassCode.equals(createPasscode)){

                    final Map<String, String> param = new HashMap<String, String>();
                    param.put("pass_code",generatePassCode);
                    showProgressDialog();
                    serverRequestwithHeader.createRequest(PassCodeChangeScreen.this,param, RequestID.REQ_ADD_PASSCODE,"POST","");

                }else{

                    toast(getResources().getString(R.string.Reenter));
                    codeImageView1.setImageDrawable(getResources().getDrawable(R.drawable.gray_round));
                    codeImageView2.setImageDrawable(getResources().getDrawable(R.drawable.gray_round));
                    codeImageView3.setImageDrawable(getResources().getDrawable(R.drawable.gray_round));
                    codeImageView4.setImageDrawable(getResources().getDrawable(R.drawable.gray_round));
                    createPasscode = "";
                    homeTextView.setText(getResources().getString(R.string.NewPin));

                }
            }
        }

    }

    @Override
    public void onSuccess(Object result, RequestID requestID) {

        hideProgressDialog();
        loginSession.setLogin(true);
        loginSession.setOTPVerified(true);
        loginSession.setPassCodeVerified(true);
        toast("Security pin changed successfully");
        this.finish();

    }

    @Override
    public void onFailure(String error, RequestID requestID) {

        hideProgressDialog();
        toast(error);
        codeImageView1.setImageDrawable(getResources().getDrawable(R.drawable.gray_round));
        codeImageView2.setImageDrawable(getResources().getDrawable(R.drawable.gray_round));
        codeImageView3.setImageDrawable(getResources().getDrawable(R.drawable.gray_round));
        codeImageView4.setImageDrawable(getResources().getDrawable(R.drawable.gray_round));
        createPasscode = "";

    }
}
