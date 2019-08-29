package com.omlah.customer.tabmore;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.omlah.customer.R;
import com.omlah.customer.base.BaseActivity;
import com.omlah.customer.common.LoginSession;
import com.omlah.customer.model.TransactionDetails;
import com.omlah.customer.service.RequestID;
import com.omlah.customer.service.ServerListener;
import com.omlah.customer.service.ServerRequestwithHeader;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TransactionDetailsScreen extends BaseActivity implements ServerListener {

    //Create class objects
    LoginSession loginSession;
    ServerRequestwithHeader serverRequestwithHeader;

    //Create xml files
    @BindView(R.id.contentLayout)LinearLayout contentLayout;
    @BindView(R.id.typeImageView)ImageView typeImageView;
    @BindView(R.id.typeTextView)TextView typeTextView;
    @BindView(R.id.totalAmountTextView)TextView totalAmountTextView;
    @BindView(R.id.statusTextView)TextView statusTextView;
    @BindView(R.id.shopNameTextView)TextView shopNameTextView;

    @BindView(R.id.amountTextView)TextView amountTextView;
    @BindView(R.id.taxAmountTextView)TextView taxAmountTextView;
    @BindView(R.id.serviceFeeText)TextView serviceFeeText;
    @BindView(R.id.popPayFeeTextView)TextView popPayFeeTextView;
    @BindView(R.id.totalPayableTextView)TextView totalPayableTextView;
    @BindView(R.id.descriptionLine)TextView descriptionLine;
    @BindView(R.id.descriptionText)TextView descriptionText;
    @BindView(R.id.descriptionTextView)TextView descriptionTextView;
    @BindView(R.id.transactedDateTextView)TextView transactedDateTextView;
    @BindView(R.id.orderidTextView)TextView orderidTextView;
    @BindView(R.id.tipAmountTextView)TextView tipAmountTextView;
    @BindView(R.id.offerText)TextView offerText;
    @BindView(R.id.offerTextView)TextView offerTextView;
    @BindView(R.id.redeemAmountTextView)TextView redeemAmountTextView;
    @BindView(R.id.redeemAmountText)TextView redeemAmountText;
    @BindView(R.id.rewardLine)TextView rewardLine;
    @BindView(R.id.earnPointsTextView)TextView earnPointsTextView;
    @BindView(R.id.taxLayout)
    RelativeLayout taxLayout;
    @BindView(R.id.tipLayout)RelativeLayout tipLayout;
    @BindView(R.id.popPayFeeLayout)RelativeLayout popPayFeeLayout;
    @BindView(R.id.offerLayout)RelativeLayout offerLayout;
    @BindView(R.id.redeemAmountLayout)RelativeLayout redeemAmountLayout;
    @BindView(R.id.voucherLayout)RelativeLayout voucherLayout;
    @BindView(R.id.voucherFeeText)TextView voucherFeeText;
    @BindView(R.id.voucherAmountTextView)TextView voucherAmountTextView;
    @BindView(R.id.refundLine)TextView refundLine;
    @BindView(R.id.refundinfoText)TextView refundinfoText;
    @BindView(R.id.refundTypeText)TextView refundTypeText;
    @BindView(R.id.refundAmountText)TextView refundAmountText;
    @BindView(R.id.refundStatusText)TextView refundStatusText;
    @BindView(R.id.refundTypeTextView)TextView refundTypeTextView;
    @BindView(R.id.refundAmountTextView)TextView refundAmountTextView;
    @BindView(R.id.refundStatusTextView)TextView refundStatusTextView;
    @BindView(R.id.refundLayout)LinearLayout refundLayout;

    String id="",type="",typeAmount="",businessName="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_transaction_screen);
        showBackArrow();
        setActionBarTitle(getResources().getString(R.string.MyTransactions));

        //Initialize xml objects
        ButterKnife.bind(this);

        //Initialize class objects
        loginSession = LoginSession.getInstance(this);
        serverRequestwithHeader = ServerRequestwithHeader.getInstance(this);

        //Get intent values
        Intent intent = getIntent();
        if(intent!=null){
            id = intent.getStringExtra("ID");
            type = intent.getStringExtra("typeName");
            typeAmount = intent.getStringExtra("typeAmount");
            businessName = intent.getStringExtra("businessName");

            typeTextView.setText(type);
            totalAmountTextView.setText(typeAmount);
            shopNameTextView.setText(businessName);

            if(type.equalsIgnoreCase("Topup")){
                typeImageView.setImageDrawable(getResources().getDrawable(R.drawable.topup));
            }else if(type.equalsIgnoreCase("Paid")){
                typeImageView.setImageDrawable(getResources().getDrawable(R.drawable.paid));
            }else if(type.equalsIgnoreCase("Refund")){
                typeImageView.setImageDrawable(getResources().getDrawable(R.drawable.received));
            }else if(type.equalsIgnoreCase("Refunded")){
                typeImageView.setImageDrawable(getResources().getDrawable(R.drawable.received));
            }else{
                typeImageView.setImageDrawable(getResources().getDrawable(R.drawable.received));
            }
        }

        //Get user details
        if(isConnectingToInternet()){

            Map<String, String> params = new HashMap<>();
            showProgressDialog();
            serverRequestwithHeader.createRequest(TransactionDetailsScreen.this,params, RequestID.REQ_TRANSACTION_DEATILS,"GET",id);

        }else{
            noInternetAlertDialog();
        }
    }

    @Override
    public void onSuccess(Object result, RequestID requestID) {

        hideProgressDialog();
        contentLayout.setVisibility(View.VISIBLE);
        try{
            TransactionDetails transactionDetails = (TransactionDetails)result;

            //Set status
            //Set transaction status
            if(transactionDetails.data.status.equalsIgnoreCase("success")){
                statusTextView.setText(getResources().getString(R.string.Success));
                statusTextView.setTextColor(getResources().getColor(R.color.green));
            }else{

                try{
                    if(transactionDetails.data.refund_transaction!=null){
                        statusTextView.setText(transactionDetails.data.refund_transaction.refund_status);
                    }else{
                        statusTextView.setText(transactionDetails.data.status);
                    }
                }catch (Exception e){e.printStackTrace();
                    statusTextView.setText(transactionDetails.data.status);}

                statusTextView.setTextColor(getResources().getColor(R.color.green));
            }



        }catch (Exception e){e.printStackTrace();}
    }

    @Override
    public void onFailure(String error, RequestID requestID) {

        hideProgressDialog();
        toast(error);
    }
}
