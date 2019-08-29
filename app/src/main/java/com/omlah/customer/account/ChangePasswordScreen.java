package com.omlah.customer.account;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;

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

public class ChangePasswordScreen extends BaseActivity implements ServerListener{

    //Create class objects
    private ServerRequestwithHeader serverRequestwithHeader;
    private LoginSession loginSession;

    //Create xml file
    @BindView(R.id.continueButton)Button continueButton;
    @BindView(R.id.oldPasswordEditText)TextInputEditText oldPasswordEditText;
    @BindView(R.id.oldpasswordText)TextInputLayout oldpasswordText;
    @BindView(R.id.newpasswordText)TextInputLayout newpasswordText;
    @BindView(R.id.newPasswordEditText)TextInputEditText newPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);
        showBackArrow();
        setActionBarTitle(getResources().getString(R.string.Changepassword));

        //Initialize xml file
        ButterKnife.bind(this);

        //Initialize class objects
        serverRequestwithHeader  = ServerRequestwithHeader.getInstance(this);
        loginSession             = LoginSession.getInstance(this);

        //continueButton click event
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String oldPassword = oldPasswordEditText.getText().toString().trim();
                String newPassword = newPasswordEditText.getText().toString().trim();

                if(oldPassword.isEmpty()){
                    oldpasswordText.setError(getResources().getString(R.string.EnterOldPassword));

                }else if(newPassword.isEmpty()){
                    newpasswordText.setError(getResources().getString(R.string.EnterNewPassword));

                }/*else if(newPassword.length() < 6){
                    newpasswordText.setError(getResources().getString(R.string.EnterValidPassword));

                }else if(newPassword.replaceAll("[0-9]","").length() == 0){
                    newpasswordText.setError(getResources().getString(R.string.EnterValidPassword));

                }else if(newPassword.replaceAll("[^0-9]", "").length() == 0){
                    newpasswordText.setError(getResources().getString(R.string.EnterValidPassword));

                }*/else if(!isConnectingToInternet()){
                    noInternetAlertDialog();

                } else {

                    final Map<String, String> param = new HashMap<String, String>();
                    param.put("old_password",oldPassword);
                    param.put("password",newPassword);
                    showProgressDialog();
                    serverRequestwithHeader.createRequest(ChangePasswordScreen.this, param, RequestID.REQ_CHANGE_PASSWORD, "POST", "");

                }
            }
        });
    }

    @Override
    public void onSuccess(Object result, RequestID requestID) {

        hideProgressDialog();
        toast(result.toString());
        finish();
    }

    @Override
    public void onFailure(String error, RequestID requestID) {

        hideProgressDialog();
        toast("Old password is wrong");
    }
}
