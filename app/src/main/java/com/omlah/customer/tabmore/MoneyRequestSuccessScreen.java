package com.omlah.customer.tabmore;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.omlah.customer.R;
import com.omlah.customer.base.BaseActivity;
import com.omlah.customer.common.LoginSession;
import com.omlah.customer.model.TaxSuccess;
import com.omlah.customer.service.RequestID;
import com.omlah.customer.service.ServerListener;
import com.omlah.customer.service.ServerRequestwithHeader;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.omlah.customer.service.RequestID.REQ_AMOUNT_TAX;
import static com.omlah.customer.service.RequestID.REQ_MONEYREQUEST_REJECT;

/**
 * Created by admin on 20-09-2017.
 */

public class MoneyRequestSuccessScreen extends BaseActivity implements ServerListener{

    //create class objects
    LoginSession loginSession;
    ServerRequestwithHeader serverRequest;

    //Create xml file
    @BindView(R.id.typeTextView)TextView typeTextView;
    @BindView(R.id.totalAmountTextView)TextView totalAmountTextView;
    @BindView(R.id.datetextView)TextView datetextView;
    @BindView(R.id.receiverNameTextView)TextView receiverNameTextView;
    @BindView(R.id.numberTextView)TextView numberTextView;
    @BindView(R.id.descriptionText)TextView descriptionText;
    @BindView(R.id.descriptionTextView)TextView descriptionTextView;
    @BindView(R.id.popPayIdTextView)TextView popPayIdTextView;
    @BindView(R.id.fromTextView)TextView fromTextView;
    @BindView(R.id.sendMoneyButton)Button sendMoneyButton;
    @BindView(R.id.rejectButton)TextView rejectButton;

    //String created
    String id="",type="",amount ="",currency="",createdDate="",receiverName="",receiverNumber="",description="",transaction_no="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.money_request_success);
        showBackArrow();
        setActionBarTitle(getResources().getString(R.string.MoneyRequest));

        //Initialize xml file
        ButterKnife.bind(this);

        //Initialize class objects
        loginSession = LoginSession.getInstance(this);
        serverRequest = ServerRequestwithHeader.getInstance(this);

        //Get intent values
        final Intent intent = getIntent();

        if(intent!=null){

            Log.e("amount",intent.getStringExtra("amount"));

            type = intent.getStringExtra("type");
            id = intent.getStringExtra("id");
            amount = intent.getStringExtra("amount");
            currency = intent.getStringExtra("currency");
            createdDate = intent.getStringExtra("date");
            receiverName = intent.getStringExtra("receiverName");
            receiverNumber = intent.getStringExtra("receiverNumber");
            description = intent.getStringExtra("description");
            transaction_no = intent.getStringExtra("transaction_no");


            typeTextView.setText(type);
            totalAmountTextView.setText(currency+" "+loginSession.getcurrencySymbol()+" "+String.format(Locale.ENGLISH,"%.2f", Double.parseDouble(amount)));
            receiverNameTextView.setText(receiverName);
            numberTextView.setText(receiverNumber);
            popPayIdTextView.setText(transaction_no);

            if(!description.isEmpty() || description!=null){
                descriptionTextView.setText(description);
                descriptionText.setVisibility(View.VISIBLE);
            }else{
                descriptionText.setVisibility(View.GONE);
            }

            datetextView.setText(timeZoneConverter(createdDate,loginSession.gettimeZone()));

            if(type.equalsIgnoreCase("Request received")){

                sendMoneyButton.setVisibility(View.VISIBLE);
                rejectButton.setVisibility(View.VISIBLE);
                fromTextView.setText("FROM");

            }else{

                sendMoneyButton.setVisibility(View.GONE);
                rejectButton.setVisibility(View.GONE);
                fromTextView.setText("TO");

            }

        }

        sendMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try{

                    Map<String, String> params = new HashMap<>();
                    showProgressDialog();
                    serverRequest.createRequest(MoneyRequestSuccessScreen.this,params, REQ_AMOUNT_TAX,"GET",receiverNumber+"/"+amount);

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try{

                    Map<String, String> params = new HashMap<>();
                    showProgressDialog();
                    serverRequest.createRequest(MoneyRequestSuccessScreen.this,params, REQ_MONEYREQUEST_REJECT,"GET",id);

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void onSuccess(Object result, RequestID requestID) {

        hideProgressDialog();

        switch (requestID){

            case REQ_AMOUNT_TAX:

                TaxSuccess taxSuccess = (TaxSuccess)result;

                if(taxSuccess.success == 1){

                    String extra_fees = taxSuccess.customerFees.extra_fees;
                    String fee_amount = taxSuccess.customerFees.fee_amount;
                    String fee_option = taxSuccess.customerFees.fee_option;

                    double tax = Double.parseDouble(taxSuccess.tax.taxAmount);

                    Intent intent1 = new Intent(MoneyRequestSuccessScreen.this, SendRequestMoneyScreen.class);
                    intent1.putExtra("id",id);
                    intent1.putExtra("name",receiverName);
                    intent1.putExtra("number",receiverNumber);
                    intent1.putExtra("amount",amount);
                    intent1.putExtra("taxAmount",String.valueOf(tax));
                    intent1.putExtra("taxPercentage",taxSuccess.tax.taxPercentage);
                    intent1.putExtra("totalPayable",String.valueOf(amount));
                    intent1.putExtra("description",description);
                    intent1.putExtra("currency",currency);
                    intent1.putExtra("createdDate",createdDate);
                    intent1.putExtra("extra_fees",extra_fees);
                    intent1.putExtra("fee_amount",fee_amount);
                    intent1.putExtra("fee_option",fee_option);
                    startActivity(intent1);
                    finish();
                }

                break;

            case REQ_MONEYREQUEST_REJECT:

                toast(result.toString());
                setResult(1);
                finish();

                break;
        }

    }

    @Override
    public void onFailure(String error, RequestID requestID) {

        hideProgressDialog();
        toast(error);
    }
}
