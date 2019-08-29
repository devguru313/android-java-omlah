package com.omlah.customer.account;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.hbb20.CountryCodePicker;
import com.omlah.customer.R;
import com.omlah.customer.base.BaseActivity;
import com.omlah.customer.common.CountryAdapter;
import com.omlah.customer.common.LoginSession;
import com.omlah.customer.common.Utility;
import com.omlah.customer.model.CountryList;
import com.omlah.customer.model.UserDetails;
import com.omlah.customer.otp.LuncherPassCodeCheck;
import com.omlah.customer.otp.OtpVerificationScreen;
import com.omlah.customer.otp.PassCodeCreateScreen;
import com.omlah.customer.service.RequestID;
import com.omlah.customer.service.ServerListener;
import com.omlah.customer.service.ServerRequest;
import com.omlah.customer.service.ServerRequestwithHeader;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 20-09-2017.
 */

public class LoginScreen extends BaseActivity implements ServerListener{

    //Create class objects
    private ServerRequest serverRequest;
    private ServerRequestwithHeader serverRequestwithHeader;
    private LoginSession loginSession;
    Dialog alertDialog;

    //Create xml file
    @BindView(R.id.ccpPicker) CountryCodePicker ccpPicker;
    @BindView(R.id.continueButton)Button continueButton;
    @BindView(R.id.numberEditText)EditText numberEditText;
    @BindView(R.id.passwordEditText)TextInputEditText passwordEditText;
    @BindView(R.id.passwordText)TextInputLayout passwordText;
    @BindView(R.id.forgotPasswordTextView)TextView forgotPasswordTextView;
    @BindView(R.id.errorTextView)TextView errorTextView;
    @BindView(R.id.backIconImageView) ImageView backIconImageView;
    @BindView(R.id.contactImageView) ImageView contactImageView;
    @BindView(R.id.numberCodeSpinner) Spinner numberCodeSpinner;

    //Create String
    String customer_mobilenumber="",token="",phoneCode="";
    private boolean checkNumber;
    CountryAdapter customAdapter;
    private CountryList countryList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.login_screen);
        hideActionBar();

        //Initialize xml file
        ButterKnife.bind(this);
        registerCarrierEditText();
        contactImageView.setVisibility(View.GONE);

        //Initialize class objects
        serverRequest            = ServerRequest.getInstance(this);
        serverRequestwithHeader  = ServerRequestwithHeader.getInstance(this);
        loginSession             = LoginSession.getInstance(this);

        //Get Intent values
        final Intent intent = getIntent();
        if(intent!=null){

            customer_mobilenumber = intent.getStringExtra("customer_mobilenumber");
            phoneCode = intent.getStringExtra("phoneCode");
            numberEditText.setText(customer_mobilenumber);

            //GetCountryCode
            getCountryCodeResponse();

        }

        numberCodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCode = countryList.data.countryList.get(position).phone_code.trim();
                if(selectedCode!=null && !selectedCode.isEmpty()){
                    if(selectedCode.equalsIgnoreCase("+1")){
                        ccpPicker.setDefaultCountryUsingNameCode("US");
                        ccpPicker.resetToDefaultCountry();
                    }else{
                        ccpPicker.setDefaultCountryUsingPhoneCode(Integer.parseInt(selectedCode.replace("+","")));
                        ccpPicker.resetToDefaultCountry();
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //forgotPassword click event
        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(loginSession.get_OTP_VERIFICATION()){

                    //getOTP on forget password
                    if (isConnectingToInternet()) {
                        final Map<String, String> param = new HashMap<String, String>();
                        param.put("phone_number",ccpPicker.getFullNumber());
                        showProgressDialog();
                        serverRequest.createRequest(LoginScreen.this, param, RequestID.REQ_FORGET_PASSWORD, "POST", "");
                    } else {
                        noInternetAlertDialog();
                    }

                }else{

                    Intent forgotPasswordIntent = new Intent(LoginScreen.this,ForgetPasswordScreen.class);
                    forgotPasswordIntent.putExtra("mobileNumber",ccpPicker.getFullNumber());
                    forgotPasswordIntent.putExtra("otp","1234 ( test OTP )");
                    startActivity(forgotPasswordIntent);
                }

            }
        });

 /*       numberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(!s.toString().isEmpty()){

                    if(numberCodeSpinner.getSelectedItem().toString().equalsIgnoreCase("+251")){
                        if(s.length() == 11){
                            if(checkNumber){
                                errorTextView.setText("");
                            }else{
                                errorTextView.setText(getResources().getString(R.string.EnterValidNumber));
                            }
                        }else{
                            errorTextView.setText(getResources().getString(R.string.EnterValidNumber));
                        }
                    }else{
                        if(s.length() == 12){
                            if(checkNumber){
                                errorTextView.setText("");
                            }else{
                                errorTextView.setText(getResources().getString(R.string.EnterValidNumber));
                            }
                        }else{
                            errorTextView.setText(getResources().getString(R.string.EnterValidNumber));
                        }
                    }
                }else{

                    errorTextView.setText("");

                }
            }
        });*/

        //continueButton click event
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String emailORnumber = ccpPicker.getFullNumber();
                String number = numberEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                errorTextView.setText("");

                if(!checkNumber){

                    errorTextView.setText(getResources().getString(R.string.EnterValidNumber));

                }else if(password.isEmpty()){

                    passwordText.setError(getResources().getString(R.string.EnterPassword));

                }else if(!isConnectingToInternet()){

                    noInternetAlertDialog();

                }else {

                    try{

                        token = FirebaseInstanceId.getInstance().getToken();
                        if(token!=null){

                            final Map<String, String> param = new HashMap<String, String>();
                            param.put("phone_number",emailORnumber);
                            param.put("password",password);
                            param.put("fcm",token);
                            param.put("device_id",loginSession.getDeviceId());
                            param.put("mac_id",loginSession.getMACId());
                            param.put("platform","android");
                            showProgressDialog();
                            serverRequest.createRequest(LoginScreen.this,param,RequestID.REQ_LOGIN,"POST","");

                        }else{
                            openPlaystoreDialogue();
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                        openPlaystoreDialogue();
                    }

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

    private void getCountryCodeResponse() {

        if(!Utility.COUNTRY_LIST.toString().isEmpty()){
            onSuccess(Utility.COUNTRY_LIST,RequestID.REQ_COUNTRYLIST);
        }else{
            if(!isConnectingToInternet()){
                noInternetAlertDialog();
            }else{
                final Map<String, String> param = new HashMap<String, String>();
                showProgressDialog();
                serverRequest.createRequest(LoginScreen.this,param,RequestID.REQ_COUNTRYLIST,"GET","");
            }
        }
    }

    private void registerCarrierEditText() {

        ccpPicker.registerCarrierNumberEditText(numberEditText);
        ccpPicker.setPhoneNumberValidityChangeListener(new CountryCodePicker.PhoneNumberValidityChangeListener() {
            @Override
            public void onValidityChanged(boolean isValidNumber) {
                checkNumber = isValidNumber;
            }
        });

        ccpPicker.registerCarrierNumberEditText(numberEditText);

    }

    private void openPlaystoreDialogue() {

        AlertDialog.Builder alert = new AlertDialog.Builder(LoginScreen.this);
        alert.setTitle(getResources().getString(R.string.UpdatePlayService));
        alert.setPositiveButton(getResources().getString(R.string.Ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.gms"));
                startActivity(intent);
            }
        })
                .setNegativeButton(getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alert.show();
    }

    private void goOTPVerifiedScreen() {

        final Map<String, String> param = new HashMap<String, String>();
        showProgressDialog();
        serverRequestwithHeader.createRequest(LoginScreen.this,param,RequestID.REQ_RESEND_OTP,"POST","");

    }


    ///////////////////////////////////// SERVER RESPONSE HANDLE ///////////////////////////////////
    @Override
    public void onSuccess(Object result, RequestID requestID) {
        hideProgressDialog();

        switch (requestID){

            case REQ_COUNTRYLIST:

                HashMap<String,Integer> list = new HashMap<>();
                countryList = (CountryList) result;
                Utility.COUNTRY_LIST = result;
                if (countryList.data.countryList.size() > 0) {

                    customAdapter = new CountryAdapter(getApplicationContext(), countryList.data.countryList);
                    numberCodeSpinner.setAdapter(customAdapter);

                    for(int i=0; i < countryList.data.countryList.size(); i++){
                        list.put(countryList.data.countryList.get(i).phone_code,i);
                    }

                    numberCodeSpinner.setSelection(list.get(phoneCode));
                }

                break;

            case REQ_LOGIN:

                try {

                    UserDetails userDetails = (UserDetails) result;
                    if(userDetails.message.contains("Clear exist session to login!")){

                        if (alertDialog == null) {

                            alertDialog = new Dialog(LoginScreen.this);
                            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            alertDialog.setContentView(R.layout.dialog_for_alert);
                            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                            alertDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        }

                        Button cancelButton = (Button) alertDialog.findViewById(R.id.cancelButton);
                        Button okButton = (Button) alertDialog.findViewById(R.id.okButton);

                        cancelButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog.dismiss();
                            }
                        });

                        okButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog.dismiss();
                                //REQUEST TO LOGIN
                                String emailORnumber = ccpPicker.getFullNumber();
                                String password = passwordEditText.getText().toString().trim();
                                final Map<String, String> param = new HashMap<String, String>();
                                param.put("phone_number",emailORnumber);
                                param.put("password",password);
                                param.put("fcm",token);
                                param.put("device_id",loginSession.getDeviceId());
                                param.put("mac_id",loginSession.getMACId());
                                param.put("platform","android");
                                param.put("proceed","YES");
                                showProgressDialog();
                                serverRequest.createRequest(LoginScreen.this,param,RequestID.REQ_LOGIN,"POST","");

                            }
                        });

                        alertDialog.setCancelable(true);
                        alertDialog.show();

                    }else {

                        loginSession.saveUserDetails(false, userDetails.data.customer_id, userDetails.data.auth_token, userDetails.data.authentication, false, false);
                        //Check OTP option
                        if (userDetails.data.otp_option.equalsIgnoreCase("YES")) {
                            loginSession.set_OTP_VERIFICATION(true);
                            //Check OTP verified or not
                            if (userDetails.data.otp_verified.equalsIgnoreCase("YES")) {
                                //Check passCode Verified or not
                                loginSession.setOTPVerified(true);
                                if (userDetails.data.pass_code_verified.equalsIgnoreCase("YES")) {
                                    loginSession.setLogin(true);
                                    loginSession.setPassCodeVerified(true);
                                    Intent intent = new Intent(LoginScreen.this, LuncherPassCodeCheck.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                } else {
                                    loginSession.setLogin(true);
                                    loginSession.setPassCodeVerified(false);
                                    Intent intent = new Intent(LoginScreen.this, PassCodeCreateScreen.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            } else {

                                loginSession.setOTPVerified(false);
                                goOTPVerifiedScreen();
                            }
                        } else {
                            loginSession.set_OTP_VERIFICATION(false);
                            //Check passCode Verified
                            if (userDetails.data.pass_code_verified.equalsIgnoreCase("YES")) {
                                loginSession.setLogin(true);
                                loginSession.setPassCodeVerified(true);
                                Intent intent = new Intent(LoginScreen.this, LuncherPassCodeCheck.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                loginSession.setLogin(true);
                                loginSession.setPassCodeVerified(false);
                                Intent intent = new Intent(LoginScreen.this, PassCodeCreateScreen.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }

                } catch (Exception e) {

                    e.printStackTrace();
                }

                break;

            case REQ_RESEND_OTP:

                if(!result.toString().trim().isEmpty()){

                    Intent intent = new Intent(LoginScreen.this, OtpVerificationScreen.class);
                    intent.putExtra("OTP",result.toString());
                    startActivity(intent);
                    finish();
                }
            break;

            case REQ_FORGET_PASSWORD:

                toast(getResources().getString(R.string.OTPsent));
                Intent forgotPasswordIntent = new Intent(LoginScreen.this,ForgetPasswordScreen.class);
                forgotPasswordIntent.putExtra("mobileNumber",ccpPicker.getFullNumber());
                forgotPasswordIntent.putExtra("otp",result.toString().trim());
                startActivity(forgotPasswordIntent);

                break;
        }

    }

    @Override
    public void onFailure(String error, RequestID requestID) {

        hideProgressDialog();

        switch (requestID){

            case REQ_LOGIN:
                toast(error);
                break;

            case REQ_RESEND_OTP:
                toast(error);
                break;

            case REQ_FORGET_PASSWORD:
                toast(error);
                break;

        }
    }

}
