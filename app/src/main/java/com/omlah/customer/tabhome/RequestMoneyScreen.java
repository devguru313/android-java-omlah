package com.omlah.customer.tabhome;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.omlah.customer.R;
import com.omlah.customer.base.BaseActivity;
import com.omlah.customer.common.LoginSession;
import com.omlah.customer.model.MoneyRequestSuccess;
import com.omlah.customer.service.RequestID;
import com.omlah.customer.service.ServerListener;
import com.omlah.customer.service.ServerRequestwithHeader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.omlah.customer.service.RequestID.REQ_REQEST_MONEY;

/**
 * Created by admin on 18-09-2017.
 */

public class RequestMoneyScreen extends BaseActivity implements ServerListener {

    //Create class objects
    LoginSession loginSession;
    ServerRequestwithHeader serverRequest;

    //Create xml objects
    @BindView(R.id.mobileNumberEditText)EditText mobileNumberEditText;
    @BindView(R.id.amountEditText)EditText amountEditText;
    @BindView(R.id.descriptionEditText)EditText descriptionEditText;
    @BindView(R.id.payNowButton)Button payNowButton;
    @BindView(R.id.contactImageView)ImageView contactImageView;

    //Create string
    String number="",name = "",totalAmount="",description="";

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_money_screen);
        showBackArrow();
        setActionBarTitle(getResources().getString(R.string.RequestMoney));

        //Initialize xml objects
        ButterKnife.bind(this);

        //Initialize class objects
        loginSession = LoginSession.getInstance(this);
        serverRequest = ServerRequestwithHeader.getInstance(this);

        //payNowButton click event
        payNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try{

                    number      = mobileNumberEditText.getText().toString().trim();
                    totalAmount = amountEditText.getText().toString().trim();
                    description = descriptionEditText.getText().toString().trim();


                    if(number.isEmpty()){

                        mobileNumberEditText.setError(getResources().getString(R.string.EnterNumber));

                    }/*else if(!number.matches("^[0-9]{11}$")){

                        mobileNumberEditText.setError("Please enter a valid number");

                    }*/else if(totalAmount.isEmpty()){

                        amountEditText.setError(getResources().getString(R.string.PleaseEnterAmount));

                    }else if(!isConnectingToInternet()){

                        noInternetAlertDialog();

                    }else {

                        Map<String, String> params = new HashMap<>();
                        params.put("phone_number",number);
                        params.put("amount",totalAmount);
                        params.put("description",description);
                        showProgressDialog();
                        serverRequest.createRequest(RequestMoneyScreen.this,params, REQ_REQEST_MONEY,"POST","");
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }


            }
        });

        //contactImageView click event --- contact list open
        contactImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent1 = new Intent(RequestMoneyScreen.this,ContactListScreen.class);
                intent1.putExtra("Screen","PayNow");
                startActivityForResult(intent1,1);
            }
        });

        if (checkAndRequestPermissions()) {
            // carry on the normal flow, as the case of  permissions  granted.
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == 1){
                String message = data.getStringExtra("NUMBER");
                name = data.getStringExtra("NAME");
                // set text for your textview
                mobileNumberEditText.setText(message);
            }
        }
    }

    @Override
    public void onSuccess(Object result, RequestID requestID) {

        hideProgressDialog();

        MoneyRequestSuccess moneyRequestSuccess = (MoneyRequestSuccess) result;

        if (!(moneyRequestSuccess.data == null)) {

            Intent intent = new Intent(RequestMoneyScreen.this, SuccessScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            intent.putExtra("screen","RequestMoney");
            intent.putExtra("fromScreen","RequestMoneyScreen");
            intent.putExtra("amount", totalAmount);
            intent.putExtra("currency", loginSession.getcurrencyCodee());
            intent.putExtra("date", moneyRequestSuccess.data.created);

            if (name.isEmpty()) {
                intent.putExtra("receiverName", number);
            } else {
                intent.putExtra("receiverName", name);
            }

            intent.putExtra("description", description);
            intent.putExtra("transaction_no", moneyRequestSuccess.data.request_no);


            startActivity(intent);
            finish();

        } else {
            toast("Invalid recipient to request");
        }
    }

    @Override
    public void onFailure(String error, RequestID requestID) {

        hideProgressDialog();
        toast(error);
    }

    private  boolean checkAndRequestPermissions() {


        //READ_CONTACTS
        int READCONTACTS = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);

        //READ CONTACT
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (READCONTACTS != PackageManager.PERMISSION_GRANTED) { listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS); }

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
