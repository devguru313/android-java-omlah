package com.omlah.customer.account;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.omlah.customer.model.SignUpDetail;
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

public class SignUpScreen extends BaseActivity implements ServerListener{

    //Create class objects
    private ServerRequest serverRequest;
    private ServerRequestwithHeader serverRequestwithHeader;
    private LoginSession loginSession;

    //Create xml file
    @BindView(R.id.ccpPicker)
    CountryCodePicker ccpPicker;
    @BindView(R.id.continueButton)Button continueButton;
    @BindView(R.id.firstNameEditText)EditText firstNameEditText;
    @BindView(R.id.lastNameEditText)EditText lastNameEditText;
    @BindView(R.id.numberEditText)EditText numberEditText;
    @BindView(R.id.emailEditText)EditText emailEditText;
    @BindView(R.id.countryEditText)EditText countryEditText;
    @BindView(R.id.referCodeEditText)EditText referCodeEditText;
    @BindView(R.id.passwordText)TextInputLayout passwordText;
    @BindView(R.id.passwordEditText)TextInputEditText passwordEditText;
    @BindView(R.id.numberCodeSpinner) Spinner numberCodeSpinner;
    @BindView(R.id.errorTextView) TextView errorTextView;
    @BindView(R.id.contactImageView) ImageView contactImageView;

    //String create
    CountryAdapter customAdapter;
    private CountryList countryList;
    private String customerFirstName,customerLastName,customerEmail,customer_mobilenumber = "",phoneCode="";
    private String countryID="",countryPhoneCode="",token="";
    private boolean checkNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.signup_screen);
        hideActionBar();

        //Initialize xml file
        ButterKnife.bind(this);
        registerCarrierEditText();
        contactImageView.setVisibility(View.GONE);

        //Initialize class objects
        serverRequest  = ServerRequest.getInstance(this);
        loginSession   = LoginSession.getInstance(this);
        serverRequestwithHeader  = ServerRequestwithHeader.getInstance(this);

        //Get Intent values
        final Intent intent = getIntent();

        if (intent != null) {

            customerFirstName = intent.getStringExtra("firstName");
            customerLastName = intent.getStringExtra("lastName");
            customerEmail = intent.getStringExtra("email");
            customer_mobilenumber = intent.getStringExtra("customer_mobilenumber");
            phoneCode = intent.getStringExtra("phoneCode");

            numberEditText.setText(customer_mobilenumber);
            firstNameEditText.setText(customerFirstName);
            lastNameEditText.setText(customerLastName);
            emailEditText.setText(customerEmail);

            getCountryList();
        }

        numberCodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String sp = countryList.data.countryList.get(position).phone_code.trim();
                countryID = countryList.data.countryList.get(position).id.trim();
                countryPhoneCode = countryList.data.countryList.get(position).phone_code.trim();
                countryEditText.setText(countryList.data.countryList.get(position).country_name.trim());
                if (sp.equalsIgnoreCase("+1")) {
                    ccpPicker.setDefaultCountryUsingNameCode("US");
                    ccpPicker.resetToDefaultCountry();
                } else {
                    ccpPicker.setDefaultCountryUsingPhoneCode(Integer.parseInt(sp.replace("+","")));
                    ccpPicker.resetToDefaultCountry();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        numberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(!s.toString().isEmpty()) {
                    errorTextView.setText("");
                }
            }
        });

        //Go to CountrySelectScreen
        countryEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Intent intent = new Intent(SignUpScreen.this, CountrySelectScreen.class);
                //startActivityForResult(intent, 1);
            }
        });

        //continueButton click event
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String firstName = firstNameEditText.getText().toString().trim();
                String lastName  = lastNameEditText.getText().toString().trim();
                String email     = emailEditText.getText().toString().trim();
                String number    = ccpPicker.getFullNumber();
                String country   = countryEditText.getText().toString().trim();
                String code      = referCodeEditText.getText().toString().trim();
                String password  = passwordEditText.getText().toString().trim();

                errorTextView.setText("");

                if(firstName.isEmpty()){
                    firstNameEditText.setError(getResources().getString(R.string.EnterFirstName));

                }else if(lastName.isEmpty()){
                    lastNameEditText.setError(getResources().getString(R.string.EnterLastName));

                }else if(email.isEmpty()){
                    emailEditText.setError(getResources().getString(R.string.EnterEmail));

                }else if(!validEmail(email)){
                    emailEditText.setError(getResources().getString(R.string.EnterValidEmail));

                }else if(!checkNumber){

                    errorTextView.setText(getResources().getString(R.string.EnterValidNumber));

                }/*else if(!number.matches("^[0-9]{11}$")){
                    numberEditText.setError(getResources().getString(R.string.EnterValidNumber));

                }*/else if(country.isEmpty()){
                    countryEditText.setError(getResources().getString(R.string.EnterCountry));

                }else if(password.isEmpty()){
                    passwordText.setError(getResources().getString(R.string.EnterPassword));

                }/*else if(password.length() < 6){
                    passwordText.setError("please enter valid password");

                }else if(password.replaceAll("[0-9]","").length() == 0){
                    passwordText.setError("please enter valid password");

                }else if(password.replaceAll("[^0-9]", "").length() == 0){
                    passwordText.setError("please enter valid password");

                }*/else if(!isConnectingToInternet()){

                    noInternetAlertDialog();

                }else{

                    token = FirebaseInstanceId.getInstance().getToken();

                    try{

                        if(!token.isEmpty()){

                            final Map<String, String> param = new HashMap<String, String>();
                            param.put("name",firstName+" "+lastName);
                            param.put("email",email);
                            param.put("phone_number",number);
                            param.put("password",password);
                            param.put("country_id",countryID);
                            param.put("referral_code",code);
                            param.put("fcm",token);
                            param.put("device_id",loginSession.getDeviceId());
                            param.put("mac_id",loginSession.getMACId());
                            param.put("platform","android");
                            showProgressDialog();
                            serverRequest.createRequest(SignUpScreen.this,param, RequestID.REQ_SIGNUP,"POST","");

                        }else{

                            AlertDialog.Builder alert = new AlertDialog.Builder(SignUpScreen.this);
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


                    }catch (Exception e){

                    }

                }
            }
        });
    }

    private void getCountryList() {

        if(!Utility.COUNTRY_LIST.toString().isEmpty()){
            onSuccess(Utility.COUNTRY_LIST,RequestID.REQ_COUNTRYLIST);
        }else{
            if(!isConnectingToInternet()){
                noInternetAlertDialog();
            }else{
                final Map<String, String> param = new HashMap<String, String>();
                showProgressDialog();
                serverRequest.createRequest(SignUpScreen.this,param,RequestID.REQ_COUNTRYLIST,"GET","");
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



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String strEditText = data.getStringExtra("editTextValue");
                countryID = data.getStringExtra("countryID");
                countryPhoneCode = data.getStringExtra("countryPhoneCode");
                countryEditText.setText(strEditText);
            }

        }
    }

    ///////////////////////////////////// SERVER RESPONSE HANDLE ///////////////////////////////////
    @Override
    public void onSuccess(Object result, RequestID requestID) {

        hideProgressDialog();

        switch (requestID){

            case REQ_SIGNUP:

                try{

                    SignUpDetail signUpDetail = (SignUpDetail)result;

                    if(signUpDetail.success == null){

                        loginSession.saveUserDetails(false,signUpDetail.data.customer_id, signUpDetail.data.auth_token,signUpDetail.data.authentication,false,false);

                        if(signUpDetail.data.otp.isEmpty()){

                            loginSession.set_OTP_VERIFICATION(false);
                            loginSession.setOTPVerified(true);
                            loginSession.setLogin(true);
                            loginSession.setPassCodeVerified(false);
                            Intent intent = new Intent(SignUpScreen.this, PassCodeCreateScreen.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();

                        }else{

                            loginSession.set_OTP_VERIFICATION(true);
                            loginSession.setOTPVerified(false);
                            Intent intent = new Intent(SignUpScreen.this, OtpVerificationScreen.class);
                            intent.putExtra("OTP",signUpDetail.data.otp);
                            startActivity(intent);
                            finish();
                            //goOTPVerifiedScreen();
                        }

                    }else{
                        toast("Invalid referral code");
                        referCodeEditText.setError("Please enter valid refer code");
                    }


                }catch (Exception e){

                    e.printStackTrace();
                }

                break;

            case REQ_RESEND_OTP:

                if(!result.toString().trim().isEmpty()){

                    Intent intent = new Intent(SignUpScreen.this, OtpVerificationScreen.class);
                    intent.putExtra("OTP",result.toString());
                    startActivity(intent);
                    finish();
                }
                break;

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
        }

    }

    @Override
    public void onFailure(String error, RequestID requestID) {

        hideProgressDialog();

        try{

            toast(error);

        }catch (Exception e){

            e.printStackTrace();
        }
    }

    private void goOTPVerifiedScreen() {

        final Map<String, String> param = new HashMap<String, String>();
        showProgressDialog();
        serverRequestwithHeader.createRequest(SignUpScreen.this,param,RequestID.REQ_RESEND_OTP,"POST","");

    }

}
