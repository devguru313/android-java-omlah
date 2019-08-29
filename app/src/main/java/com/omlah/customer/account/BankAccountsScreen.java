package com.omlah.customer.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.omlah.customer.R;
import com.omlah.customer.base.BaseActivity;
import com.omlah.customer.common.LoginSession;
import com.omlah.customer.model.BankList;
import com.omlah.customer.service.RequestID;
import com.omlah.customer.service.ServerListener;
import com.omlah.customer.service.ServerRequestwithHeader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Admin on 06-04-2018.
 */

public class BankAccountsScreen extends BaseActivity implements ServerListener{

    //Create class objects
    ServerRequestwithHeader serverRequestwithHeader;
    private LoginSession loginSession;
    BankAccountAdapter bankAccountAdapter;

    //Create xml file
    @BindView(R.id.backIconImageView)
    ImageView backIconImageView;@BindView(R.id.bankaccountAddButton)
    ImageView bankaccountAddButton;
    @BindView(R.id.bankAccountListView)
    ListView bankAccountListView;
    @BindView(R.id.errorImageView)
    TextView errorImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bank_account_screen);

        hideActionBar();

        //Initialize xml file
        ButterKnife.bind(this);

        //Initialize class objects
        loginSession = LoginSession.getInstance(this);
        serverRequestwithHeader = ServerRequestwithHeader.getInstance(this);

        bankaccountAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(BankAccountsScreen.this, AddNewBankAccountScreen.class);
                startActivity(intent);
            }
        });

        backIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
    }

    @Override
    public void onSuccess(Object result, RequestID requestID) {
        hideProgressDialog();

        try{
            BankList bankList = (BankList)result;
            if(!bankList.data.isEmpty()){
                bankAccountListView.setVisibility(View.VISIBLE);
                errorImageView.setVisibility(View.GONE);
                bankAccountAdapter = new BankAccountAdapter(BankAccountsScreen.this,bankList.data);
                bankAccountListView.setAdapter(bankAccountAdapter);
            }else{
                bankAccountListView.setVisibility(View.GONE);
                errorImageView.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            e.printStackTrace();
            bankAccountListView.setVisibility(View.GONE);
            errorImageView.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public void onFailure(String error, RequestID requestID) {
        hideProgressDialog();
        bankAccountListView.setVisibility(View.GONE);
        errorImageView.setVisibility(View.VISIBLE);
    }

    private class BankAccountAdapter extends BaseAdapter {

        Activity activity;
        ArrayList<BankList.Data> dataArrayList;

        public BankAccountAdapter(Activity activity, ArrayList<BankList.Data> dataArrayList) {
            this.activity = activity;
            this.dataArrayList = dataArrayList;
        }

        @Override
        public int getCount() {
            return dataArrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return dataArrayList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View convertView, ViewGroup viewGroup) {

            LayoutInflater inflater = activity.getLayoutInflater();

            if (convertView == null)

                convertView = inflater.inflate(R.layout.custom_bankaccount, null);

            final TextView bankNameText = (TextView)convertView.findViewById(R.id.bankNameText);
            TextView acctNoText = (TextView)convertView.findViewById(R.id.acctNoText);
            TextView selectButton = (TextView)convertView.findViewById(R.id.selectButton);
            selectButton.setVisibility(View.GONE);
            bankNameText.setText(dataArrayList.get(i).bank_name);
            acctNoText.setText(dataArrayList.get(i).account_no);

            return convertView;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //getBank list
        if(isConnectingToInternet()){
            final Map<String, String> param = new HashMap<String, String>();
            showProgressDialog();
            serverRequestwithHeader.createRequest(BankAccountsScreen.this,param, RequestID.REQ_BANK_LIST,"GET","");
        }else{
            noInternetAlertDialog();
        }
    }
}
