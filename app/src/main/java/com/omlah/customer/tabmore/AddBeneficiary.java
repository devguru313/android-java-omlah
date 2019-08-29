package com.omlah.customer.tabmore;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;
import com.omlah.customer.R;
import com.omlah.customer.base.BaseActivity;
import com.omlah.customer.common.CountryAdapter;
import com.omlah.customer.common.LoginSession;
import com.omlah.customer.common.Utility;
import com.omlah.customer.model.CountryList;
import com.omlah.customer.service.RequestID;
import com.omlah.customer.service.ServerListener;
import com.omlah.customer.service.ServerRequest;
import com.omlah.customer.service.ServerRequestwithHeader;
import com.omlah.customer.tabhome.ContactListScreen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 07-10-2017.
 */

public class AddBeneficiary extends BaseActivity implements ServerListener{

    //Create class objects
    Dialog dialog;
    LoginSession loginSession;
    ServerRequestwithHeader serverRequest;
    ServerRequest serverRequestcountry;

    //Create xml objects
    @BindView(R.id.ccpPicker) CountryCodePicker ccpPicker;
    @BindView(R.id.backIconImageView)ImageView backIconImageView;
    @BindView(R.id.submitButton)Button submitButton;
    @BindView(R.id.nameEditText)EditText nameEditText;
    @BindView(R.id.contactImageView)ImageView contactImageView;
    @BindView(R.id.numberEditText)EditText numberEditText;
    @BindView(R.id.errorTextView) TextView errorTextView;
    @BindView(R.id.numberCodeSpinner) Spinner numberCodeSpinner;

    private boolean checkNumber;
    CountryAdapter customAdapter;
    private CountryList countryList;
    String SELECTED_SPINNER_NUMBER="";

    HashMap<String,Integer> list = new HashMap<>();
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_beneficiary_screen);

        //hideAction Bar
        hideActionBar();

        //Initialize xml objects
        ButterKnife.bind(this);
        registerCarrierEditText();

        //Initialize class objects
        loginSession = LoginSession.getInstance(this);
        serverRequest = ServerRequestwithHeader.getInstance(this);
        serverRequestcountry = ServerRequest.getInstance(this);

        //GetCountryCode
        getCountryCodeResponse();

        numberCodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SELECTED_SPINNER_NUMBER = countryList.data.countryList.get(position).phone_code.trim();
                if(SELECTED_SPINNER_NUMBER!=null && !SELECTED_SPINNER_NUMBER.isEmpty()){
                    if(SELECTED_SPINNER_NUMBER.equalsIgnoreCase("+1")){
                        ccpPicker.setDefaultCountryUsingNameCode("US");
                        ccpPicker.resetToDefaultCountry();
                    }else{
                        ccpPicker.setDefaultCountryUsingPhoneCode(Integer.parseInt(SELECTED_SPINNER_NUMBER.replace("+","")));
                        ccpPicker.resetToDefaultCountry();
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //contact normal image view click event
        contactImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent1 = new Intent(AddBeneficiary.this,ContactListScreen.class);
                intent1.putExtra("Screen","AddContact");
                startActivityForResult(intent1,1);
            }
        });

        //backicon image view
        backIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        //click event for submit
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String beneficiaryName = nameEditText.getText().toString();

                errorTextView.setText("");
                if(beneficiaryName.isEmpty()){
                    nameEditText.setError("Please enter your name");
                }else if(!checkNumber){
                    errorTextView.setText(getResources().getString(R.string.EnterValidNumber));
                } else if(loginSession.getphoneNumber().equalsIgnoreCase(ccpPicker.getFullNumber())){
                    toast("You cannot add from yourself");
                }else{

                    if(isConnectingToInternet()){

                        Map<String, String> params = new HashMap<>();
                        params.put("phone_number",ccpPicker.getFullNumber());
                        params.put("beneficiary_name",nameEditText.getText().toString().trim());
                        showProgressDialog();
                        serverRequest.createRequest(AddBeneficiary.this,params, RequestID.REQ_ADD_BENEFICIARY,"POST","");


                    }else{
                        noInternetAlertDialog();
                    }
                }
            }
        });


        if (checkAndRequestPermissions()) {
            // carry on the normal flow, as the case of  permissions  granted.
        }
        /*numberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(!s.toString().isEmpty()){
                    errorTextView.setText("");
                    if(SELECTED_SPINNER_NUMBER.equalsIgnoreCase("+251")){
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
                serverRequestcountry.createRequest(AddBeneficiary.this,param,RequestID.REQ_COUNTRYLIST,"GET","");
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
            if(resultCode == 1){

                //Get money request response
                String message = data.getStringExtra("NUMBER");
                String NAME = data.getStringExtra("NAME");
                String CODE = data.getStringExtra("CODE");

                try {
                    numberCodeSpinner.setSelection(list.get(CODE));
                    numberEditText.setText(message);
                    nameEditText.setText(NAME);
                } catch (Exception e) {
                    e.printStackTrace();
                    toast("Please choose a valid number");
                }

            }
        }
    }

    @Override
    public void onSuccess(Object result, RequestID requestID) {

        hideProgressDialog();

        switch (requestID){

            case REQ_COUNTRYLIST:


                countryList = (CountryList) result;
                Utility.COUNTRY_LIST = result;
                if (countryList.data.countryList.size() > 0) {

                    customAdapter = new CountryAdapter(getApplicationContext(), countryList.data.countryList);
                    numberCodeSpinner.setAdapter(customAdapter);

                    list.clear();
                    try{
                        int index = 0;
                        for(CountryList.CountriesList list12 : countryList.data.countryList){
                            list.put(list12.phone_code,index);
                            index = index + 1;
                        }
                        numberCodeSpinner.setSelection(list.get(loginSession.getCustomerCountryCode()));
                    }catch (Exception e){e.printStackTrace();}

                }


                break;

            case REQ_ADD_BENEFICIARY:

                try {

                    if(result.toString().equalsIgnoreCase("Requested beneficiary not available")){

                        toast("This customer didn't have RPay account");

                    }else{

                        toast(result.toString());
                        setResult(1);
                        finish();
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
        try {

            toast(error.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private  boolean checkAndRequestPermissions() {

        //READ_CONTACTS
        int READCONTACTS = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (READCONTACTS != PackageManager.PERMISSION_GRANTED) { listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS); }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }
}
