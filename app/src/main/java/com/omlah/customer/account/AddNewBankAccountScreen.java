package com.omlah.customer.account;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.omlah.customer.R;
import com.omlah.customer.base.BaseActivity;
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

public class AddNewBankAccountScreen extends BaseActivity implements ServerListener{

    //Create class objects
    ServerRequestwithHeader serverRequestwithHeader;

    //Create xml file
    @BindView(R.id.holderNameEditText)EditText holderNameEditText;
    @BindView(R.id.bankNameEditText)EditText bankNameEditText;
    @BindView(R.id.acountNumberEditText)EditText acountNumberEditText;
    @BindView(R.id.addButton)Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_bank_account);
        showBackArrow();
        setActionBarTitle(getResources().getString(R.string.AddBankAccountScreen));

        //Initialize xml file
        ButterKnife.bind(this);

        //Initialize class objects
        serverRequestwithHeader = ServerRequestwithHeader.getInstance(this);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String holderName     = holderNameEditText.getText().toString().trim();
                String bankname       = bankNameEditText.getText().toString().trim();
                String accountNumber  = acountNumberEditText.getText().toString().trim();

                if(holderName.isEmpty()){
                    holderNameEditText.setError("Please enter a account holder name");
                }else if(bankname.isEmpty()){
                    bankNameEditText.setError("Please enter a bank name");
                }else if(accountNumber.isEmpty()){
                    acountNumberEditText.setError("Please enter a account number");
                }else if(!isConnectingToInternet()){
                    noInternetAlertDialog();
                }else{

                    Map<String, String> params = new HashMap<>();
                    params.put("account_holder_name",holderName);
                    params.put("bank_name",bankname);
                    params.put("account_number",accountNumber);
                    showProgressDialog();
                    serverRequestwithHeader.createRequest(AddNewBankAccountScreen.this,params, RequestID.REQ_ADD_BANK_ACCOUNT,"POST","");

                }
            }
        });
    }

    @Override
    public void onSuccess(Object result, RequestID requestID) {

        hideProgressDialog();
        toast(result.toString());
        setResult(5);
        finish();
    }

    @Override
    public void onFailure(String error, RequestID requestID) {

        hideProgressDialog();
        toast(error);
    }
}
