package com.omlah.customer.tabmore;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.omlah.customer.R;
import com.omlah.customer.base.BaseActivity;
import com.omlah.customer.common.LoginSession;
import com.omlah.customer.model.SentMoneySuccess;
import com.omlah.customer.otp.PassCodeVerifiedScreen;
import com.omlah.customer.service.RequestID;
import com.omlah.customer.service.ServerListener;
import com.omlah.customer.service.ServerRequestwithHeader;
import com.omlah.customer.tabhome.SuccessScreen;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.omlah.customer.service.RequestID.REQ_PAYMONEY;
import static com.omlah.customer.service.RequestID.REQ_STATUS_UPDATE;

/**
 * Created by admin on 04-10-2017.
 */

public class SendRequestMoneyScreen extends BaseActivity implements ServerListener {

    //Create class objects
    LoginSession loginSession;
    ServerRequestwithHeader serverRequest;

    //Create xml objects
    @BindView(R.id.payNowButton)Button payNowButton;
    @BindView(R.id.nameEditText)TextView nameEditText;
    @BindView(R.id.backIconImageView)ImageView backIconImageView;
    @BindView(R.id.feeLayout)RelativeLayout feeLayout;
    @BindView(R.id.feeTextView)TextView feeTextView;
    @BindView(R.id.feeText)TextView feeText;
    @BindView(R.id.payableAmountTextView)TextView payableAmountTextView;
    @BindView(R.id.amountEditText)EditText amountEditText;
    @BindView(R.id.descriptionEditText)EditText descriptionEditText;
    @BindView(R.id.requestNowButton)Button requestNowButton;
    @BindView(R.id.uparrow)ImageView uparrow;
    @BindView(R.id.payableLayout)LinearLayout payableLayout;
    @BindView(R.id.actualAmountTextView)TextView actualAmountTextView;

    @BindView(R.id.feeAmountLayout)RelativeLayout feeAmountLayout;
    @BindView(R.id.feeAmountText)TextView feeAmountText;
    @BindView(R.id.feeAmountTextView)TextView feeAmountTextView;

    @BindView(R.id.extraFeeAmountLayout)RelativeLayout extraFeeAmountLayout;
    @BindView(R.id.extraFeeText)TextView extraFeeText;
    @BindView(R.id.extraFeeAmountTextView)TextView extraFeeAmountTextView;

    //Create string
    String transactionId = "",name = "",
            number = "",amount="",
            description="",taxamount="0",taxPercentage="",
            totalpayable="",currency="",createdDate="",transactionNo="",
            extra_fees="0",fee_amount="0",fee_option="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_details_screen);
        hideActionBar();

        //Initialize xml objects
        ButterKnife.bind(this);


        //Initialize class objects
        loginSession = LoginSession.getInstance(this);
        serverRequest = ServerRequestwithHeader.getInstance(this);

        //Get Intent values
        final Intent intent = getIntent();

        if(intent!=null){

            transactionId = intent.getStringExtra("id");
            name          = intent.getStringExtra("name");
            number        = intent.getStringExtra("number");
            amount        = intent.getStringExtra("amount");
            taxamount     = intent.getStringExtra("taxAmount");
            taxPercentage     = intent.getStringExtra("taxPercentage");
            totalpayable  = intent.getStringExtra("totalPayable");
            description   = intent.getStringExtra("description");

            extra_fees   = intent.getStringExtra("extra_fees");
            fee_amount   = intent.getStringExtra("fee_amount");
            fee_option   = intent.getStringExtra("fee_option");

            feeText.setText("Fee ( "+taxPercentage+"% )");
            actualAmountTextView.setText(String.format("%.2f",Double.parseDouble(amount)));
            descriptionEditText.setText(description);
            if(description.isEmpty()){
                descriptionEditText.setVisibility(View.GONE);
            }else{
                descriptionEditText.setVisibility(View.VISIBLE);
            }
            descriptionEditText.setFocusable(false);
            descriptionEditText.setFocusableInTouchMode(false);

            nameEditText.setText(name);
            feeLayout.setVisibility(View.GONE);

            nameEditText.setFocusableInTouchMode(false);
            nameEditText.setFocusable(false);

            amountEditText.setFocusableInTouchMode(false);
            amountEditText.setFocusable(false);

            if(Double.parseDouble(taxamount) > 0 || Double.parseDouble(extra_fees) > 0 || Double.parseDouble(fee_amount) > 0){

                //TAX LAYOUT
                if(Double.parseDouble(taxamount) > 0){
                    feeText.setText("Tax ( "+taxPercentage+"% )");
                    feeTextView.setText(String.format("%.2f",Double.parseDouble(taxamount)));
                    feeLayout.setVisibility(View.VISIBLE);
                }else{
                    feeLayout.setVisibility(View.GONE);
                }

                //FEE LAYOUT
                if(Double.parseDouble(fee_amount) > 0 || Double.parseDouble(extra_fees) > 0){
                    if(fee_option.equalsIgnoreCase("percentage")){
                     //   feeAmountText.setText("Fee"+" ( "+fee_amount+"% )");
                        feeAmountText.setText("Fee");
                        Double actualAmount = Double.parseDouble(actualAmountTextView.getText().toString().trim());
                        Double feeAmount = ( actualAmount * Double.parseDouble(fee_amount) ) / 100 + Double.parseDouble(extra_fees);
                        feeAmountTextView.setText(String.format(Locale.ENGLISH,"%.2f",feeAmount));
                    }else{
                        feeAmountText.setText("Fee");
                        feeAmountTextView.setText(String.format(Locale.ENGLISH,"%.2f",Double.parseDouble(fee_amount)+Double.parseDouble(extra_fees)));
                    }
                    feeAmountLayout.setVisibility(View.VISIBLE);
                }else{
                    feeAmountLayout.setVisibility(View.GONE);
                }


                //EXTRAA FEE AMOUNT
                if(Double.parseDouble(extra_fees) > 0){
                    extraFeeAmountLayout.setVisibility(View.GONE);
                    extraFeeAmountTextView.setText(String.format(Locale.ENGLISH,"%.2f",Double.parseDouble(extra_fees)));
                }else{
                    extraFeeAmountLayout.setVisibility(View.GONE);
                }


                Double totalPay = Double.parseDouble(totalpayable) +Double.parseDouble(taxamount)+ Double.parseDouble(feeAmountTextView.getText().toString().trim());
                payableAmountTextView.setText(String.format("%.2f",totalPay));
                amountEditText.setText(String.format("%.2f",totalPay));

                requestNowButton.setVisibility(View.GONE);
                uparrow.setVisibility(View.VISIBLE);
                payableLayout.setVisibility(View.VISIBLE);
                payNowButton.setVisibility(View.VISIBLE);

            }else{

                amountEditText.setText(String.format("%.2f",Double.parseDouble(totalpayable)));
                payableAmountTextView.setText(String.format("%.2f",Double.parseDouble(totalpayable)));
                feeLayout.setVisibility(View.GONE);
                requestNowButton.setVisibility(View.VISIBLE);
                requestNowButton.setText("Proceed to pay");

            }
        }

        payNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent payNowButton = new Intent(SendRequestMoneyScreen.this, PassCodeVerifiedScreen.class);
                startActivityForResult(payNowButton,4);

            }
        });

        requestNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent payNowButton = new Intent(SendRequestMoneyScreen.this, PassCodeVerifiedScreen.class);
                startActivityForResult(payNowButton,4);
            }
        });

        backIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });
    }

    @Override
    public void onSuccess(Object result, RequestID requestID) {

        hideProgressDialog();
        switch (requestID){

            case REQ_PAYMONEY:

                SentMoneySuccess sentMoneySuccess = (SentMoneySuccess)result;

                currency      = sentMoneySuccess.data.sender_currency;
                createdDate   = sentMoneySuccess.data.created;
                transactionNo = sentMoneySuccess.data.transaction_no;
                description   = sentMoneySuccess.data.description;

                if(isConnectingToInternet()){

                    Map<String, String> params = new HashMap<>();
                    params.put("transaction_id",sentMoneySuccess.data.id);
                    params.put("request_id",transactionId);
                    params.put("status","success");
                    showProgressDialog();
                    serverRequest.createRequest(SendRequestMoneyScreen.this,params, REQ_STATUS_UPDATE,"POST","");

                }else{

                    noInternetAlertDialog();
                }

                break;

            case REQ_STATUS_UPDATE:

                toast(result.toString());

                Intent intent = new Intent(SendRequestMoneyScreen.this,SuccessScreen.class);

                intent.putExtra("screen","PayMoney");
                intent.putExtra("fromScreen","PaymentDetailsScreen");
                intent.putExtra("amount",payableAmountTextView.getText().toString().trim());
                intent.putExtra("currency",currency);
                intent.putExtra("date",createdDate);
                intent.putExtra("receiverName",name);
                intent.putExtra("description",description);
                intent.putExtra("transaction_no",transactionNo);

                startActivity(intent);
                finish();

                break;
        }


    }

    @Override
    public void onFailure(String error, RequestID requestID) {

        hideProgressDialog();

        switch (requestID){

            case REQ_PAYMONEY:
                toast(error);
                break;
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 4 ){
            if(resultCode == 4){
                try{

                    String description = descriptionEditText.getText().toString().trim();

                    Map<String, String> params = new HashMap<>();
                    params.put("phone_number",number);
                    params.put("amount",amount);
                    params.put("description",description);
                    showProgressDialog();
                    serverRequest.createRequest(SendRequestMoneyScreen.this,params, REQ_PAYMONEY,"POST","");

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
