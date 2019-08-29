package com.omlah.customer.account;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;
import com.omlah.customer.R;
import com.omlah.customer.base.BaseActivity;
import com.omlah.customer.common.CountryAdapter;
import com.omlah.customer.common.Utility;
import com.omlah.customer.model.CheckPhoneNumber;
import com.omlah.customer.model.CountryList;
import com.omlah.customer.service.RequestID;
import com.omlah.customer.service.ServerListener;
import com.omlah.customer.service.ServerRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 20-09-2017.
 */

public class GetStartedScreen extends BaseActivity implements ServerListener{

    //Create class objects
    private ServerRequest serverRequest;

    //Create xml file
    @BindView(R.id.ccpPicker)CountryCodePicker ccpPicker;
    @BindView(R.id.numberEditText)EditText numberEditText;
    @BindView(R.id.continueButton)Button continueButton;
    @BindView(R.id.numberCodeSpinner) Spinner numberCodeSpinner;
    @BindView(R.id.contentLayout) RelativeLayout contentLayout;
    @BindView(R.id.contactImageView) ImageView contactImageView;
    @BindView(R.id.errorTextView) TextView errorTextView;

    //String create
    private CountryList countryList;
    private String customerMobileNumber="",customerPhoneCode="";
    private boolean checkNumber;

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.getstarted_screen);
        hideActionBar();

        if (checkAndRequestPermissions()) {
            // carry on the normal flow, as the case of  permissions  granted.
        }

        //Initialize xml file
        ButterKnife.bind(this);
        contactImageView.setVisibility(View.GONE);
        registerCarrierEditText();

        //Initialize class objects
        serverRequest  = ServerRequest.getInstance(this);

        //GetCountryCode
        getCountryCodeResponse();

        numberCodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                customerPhoneCode = countryList.data.countryList.get(position).phone_code.trim();
                if(customerPhoneCode!=null && !customerPhoneCode.isEmpty()){
                    if(customerPhoneCode.equalsIgnoreCase("+1")){
                        ccpPicker.setDefaultCountryUsingNameCode("US");
                        ccpPicker.resetToDefaultCountry();
                    }else{
                        ccpPicker.setDefaultCountryUsingPhoneCode(Integer.parseInt(customerPhoneCode.replace("+","")));
                        ccpPicker.resetToDefaultCountry();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        numberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().trim().isEmpty()){
                    errorTextView.setText("");
                }
            }
        });

        //continueButton click event
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                customerMobileNumber = numberEditText.getText().toString().trim();
                if (!checkNumber) {
                    errorTextView.setText(getResources().getString(R.string.EnterValidNumber));
                } else if (!isConnectingToInternet()) {
                    noInternetAlertDialog();
                } else {
                    final Map<String, String> param = new HashMap<String, String>();
                    showProgressDialog();
                    serverRequest.createRequest(GetStartedScreen.this, param, RequestID.REQ_NUMBER_CHECK, "GET", ccpPicker.getFullNumber());
                }

            }
        });

        contentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboard(GetStartedScreen.this);
            }
        });
    }

    private void getCountryCodeResponse() {

        if(!isConnectingToInternet()){
            noInternetAlertDialog();
        }else{
            final Map<String, String> param = new HashMap<String, String>();
            showProgressDialog();
            serverRequest.createRequest(GetStartedScreen.this,param,RequestID.REQ_COUNTRYLIST,"GET","");
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

    ///////////////////////////////////// SERVER RESPONSE HANDLE ///////////////////////////////////
    @Override
    public void onSuccess(Object result, RequestID requestID) {

        hideProgressDialog();

        switch (requestID){

            case REQ_COUNTRYLIST:

                countryList = (CountryList) result;
                Utility.COUNTRY_LIST = result;
                if (countryList.data.countryList.size() > 0) {
                    CountryAdapter customAdapter = new CountryAdapter(getApplicationContext(), countryList.data.countryList);
                    numberCodeSpinner.setAdapter(customAdapter);
                }

                break;

            case REQ_NUMBER_CHECK:

                try {
                    CheckPhoneNumber checkPhoneNumber = (CheckPhoneNumber) result;
                    if (checkPhoneNumber.data.user) {
                        Intent intent = new Intent(GetStartedScreen.this, SignUpScreen.class);
                        intent.putExtra("firstName", "");
                        intent.putExtra("lastName", "");
                        intent.putExtra("email", "");
                        intent.putExtra("phoneCode",customerPhoneCode);
                        intent.putExtra("customer_mobilenumber", customerMobileNumber);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(GetStartedScreen.this, LoginScreen.class);
                        intent.putExtra("customer_mobilenumber", customerMobileNumber);
                        intent.putExtra("phoneCode", customerPhoneCode);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
        }

    }

    @Override
    public void onFailure(String error, RequestID requestID) {
        hideProgressDialog();
        toast(error);
    }

    private  boolean checkAndRequestPermissions() {

        //Location
        int COARSE_LOCATION = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int FINE_LOCATION = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        //READ_CONTACTS
        int READCONTACTS = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        int CAMERA = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int READEXTERNALSTORAGE = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int WRITEEXTERNALSTORAGE = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        //Permission getting
        List<String> listPermissionsNeeded = new ArrayList<>();

        //Location
        if (COARSE_LOCATION != PackageManager.PERMISSION_GRANTED) { listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION); }
        if (FINE_LOCATION != PackageManager.PERMISSION_GRANTED) { listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION); }

        //READ CONTACT
        if (READCONTACTS != PackageManager.PERMISSION_GRANTED) { listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS); }
        if (CAMERA != PackageManager.PERMISSION_GRANTED) { listPermissionsNeeded.add(Manifest.permission.CAMERA); }
        if (READEXTERNALSTORAGE != PackageManager.PERMISSION_GRANTED) { listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE); }
        if (WRITEEXTERNALSTORAGE != PackageManager.PERMISSION_GRANTED) { listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE); }

        //Final Step
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

}
