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
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RefundTransactionScreen extends BaseActivity implements ServerListener {

    //Create class objects
    LoginSession loginSession;
    ServerRequestwithHeader serverRequestwithHeader;

    //Create xml files
    @BindView(R.id.contentLayout)LinearLayout contentLayout;
    @BindView(R.id.typeImageView)ImageView typeImageView;
    @BindView(R.id.typeTextView)TextView typeTextView;
    @BindView(R.id.totalAmountTextView)TextView totalAmountTextView;
    @BindView(R.id.samountTextView)TextView samountTextView;
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
    @BindView(R.id.refundamountTextView)TextView refundamountTextView;
    @BindView(R.id.refundTypeTextView)TextView refundTypeTextView;
    @BindView(R.id.taxLayout)RelativeLayout taxLayout;
    @BindView(R.id.tipLayout)RelativeLayout tipLayout;
    @BindView(R.id.popPayFeeLayout)RelativeLayout popPayFeeLayout;
    @BindView(R.id.offerLayout)RelativeLayout offerLayout;
    @BindView(R.id.redeemAmountLayout)RelativeLayout redeemAmountLayout;

    @BindView(R.id.refundamountText)TextView refundamountText;
    @BindView(R.id.refundTypeText)TextView refundTypeText;
    @BindView(R.id.transactedText)TextView transactedText;

    @BindView(R.id.refundTLayout)RelativeLayout refundTLayout;
    @BindView(R.id.refundTAmountTextView)TextView refundTAmountTextView;


    String id="",type="",typeAmount="";

    //boolean
    boolean b1 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.refund_transaction_screen);
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
            type = intent.getStringExtra("type");
            typeAmount = intent.getStringExtra("typeAmount");

            totalAmountTextView.setText(typeAmount);
        }

        //Get user details
        if(isConnectingToInternet()){

            Map<String, String> params = new HashMap<>();
            showProgressDialog();
            serverRequestwithHeader.createRequest(RefundTransactionScreen.this,params, RequestID.REQ_TRANSACTION_DEATILS,"GET",id);

        }else{
            noInternetAlertDialog();
        }
    }

    @Override
    public void onSuccess(Object result, RequestID requestID) {

        hideProgressDialog();
        contentLayout.setVisibility(View.VISIBLE);
        TransactionDetails transactionDetails = (TransactionDetails)result;

        refundamountTextView.setText("+ " + transactionDetails.data.sender_currency + " " + loginSession.getcurrencySymbol() + String.format(Locale.ENGLISH,"%.2f", Double.parseDouble(transactionDetails.data.refund_transaction.refund_amount)));
       // totalAmountTextView.setText("+ " + transactionDetails.data.sender_currency + " " + loginSession.getcurrencySymbol() + String.format(Locale.ENGLISH,"%.2f", Double.parseDouble(transactionDetails.data.refund_transaction.refund_amount)));
        refundTypeTextView.setText(transactionDetails.data.refund_transaction.refund_type);


        if(!transactionDetails.data.reward_transactions.isEmpty()){
            if(Double.parseDouble(transactionDetails.data.reward_transactions.get(0).rewards_earned) > 0){
                earnPointsTextView.setVisibility(View.GONE); //Visible
                earnPointsTextView.setText(transactionDetails.data.reward_transactions.get(0).rewards_earned+" "+getResources().getString(R.string.PointsReduced));
            }else{
                earnPointsTextView.setVisibility(View.GONE);
                rewardLine.setVisibility(View.GONE);
            }

        }else{
            earnPointsTextView.setVisibility(View.GONE);
            rewardLine.setVisibility(View.GONE);
        }

        double offerAmount = 0;

        if(Double.parseDouble(transactionDetails.data.offer_amount) > 0){
            offerLayout.setVisibility(View.VISIBLE);
            offerText.setText(getResources().getString(R.string.Offer)+" ( "+transactionDetails.data.offer_percentage+"% )");
            offerAmount = Double.parseDouble(transactionDetails.data.offer_amount);
        }else{
            offerLayout.setVisibility(View.GONE);
        }

        typeImageView.setImageDrawable(getResources().getDrawable(R.drawable.received));
        taxLayout.setVisibility(View.GONE);
        popPayFeeLayout.setVisibility(View.GONE);

        //Set transaction status
        if(transactionDetails.data.refund_transaction.refund_status.equalsIgnoreCase("success")){
            statusTextView.setText(getResources().getString(R.string.Success));
            typeTextView.setText(transactionDetails.data.refund_transaction.refund_type+" "+"Refunded");
            refundamountText.setText("Refunded Amount");
            refundTypeText.setText("Refunded Type");
            transactedText.setText("Refunded Date");
        }else{
            statusTextView.setText(transactionDetails.data.status);
            typeTextView.setText(getResources().getString(R.string.Refund));
            refundamountText.setText("Refund Amount");
            refundTypeText.setText("Refund Type");
            transactedText.setText("Refund Date");
        }

        //Set transaction status
        if(Double.parseDouble(transactionDetails.data.reward_offer_amount) > 0){
            redeemAmountLayout.setVisibility(View.VISIBLE);
            redeemAmountTextView.setText(transactionDetails.data.sender_currency + " " + loginSession.getcurrencySymbol() + String.format(Locale.ENGLISH,"%.2f",Double.parseDouble(transactionDetails.data.reward_offer_amount)));
            redeemAmountText.setText(getResources().getString(R.string.Reward)+" ( "+transactionDetails.data.reward_offer_percentage+"% )");
        }else{
            redeemAmountLayout.setVisibility(View.GONE);

        }


        //tip show
        if(Double.parseDouble(transactionDetails.data.tip_amount) > 0){
            tipLayout.setVisibility(View.VISIBLE);
        }else{
            tipLayout.setVisibility(View.GONE);
        }

        tipAmountTextView.setText(transactionDetails.data.sender_currency + " " + loginSession.getcurrencySymbol() + String.format(Locale.ENGLISH,"%.2f", Double.parseDouble(transactionDetails.data.tip_amount)));


        //set Amount section
        if(transactionDetails.data.sender_id.equalsIgnoreCase(loginSession.getCustomerID())){

            //set tax fee
            if (Double.parseDouble(transactionDetails.data.tax_amount) > 0) {
                taxLayout.setVisibility(View.VISIBLE);
                serviceFeeText.setText(getResources().getString(R.string.Tax)+" ( " + transactionDetails.data.tax_percentage + "% )");
                taxAmountTextView.setText(transactionDetails.data.sender_currency + " " + loginSession.getcurrencySymbol() + String.format(Locale.ENGLISH,"%.2f", Double.parseDouble(transactionDetails.data.tax_amount)));
            } else {
                taxLayout.setVisibility(View.GONE);
            }

            //set poppay fee
            if (Double.parseDouble(transactionDetails.data.poppay_fee) > 0) {
                popPayFeeLayout.setVisibility(View.VISIBLE);
                popPayFeeTextView.setText(transactionDetails.data.sender_currency + " " + loginSession.getcurrencySymbol() + String.format(Locale.ENGLISH,"%.2f", Double.parseDouble(transactionDetails.data.poppay_fee)));
            } else {
                popPayFeeLayout.setVisibility(View.GONE);
            }

            //Set total Amount

           /* if(transactionDetails.data.sender_id.equalsIgnoreCase(loginSession.getCustomerID()) && transactionDetails.data.receiver_id.equalsIgnoreCase(loginSession.getCustomerID())){
                totalAmountTextView.setText("+ " + transactionDetails.data.sender_currency + " " + loginSession.getcurrencySymbol() + String.format(Locale.ENGLISH,"%.2f", Double.parseDouble(transactionDetails.data.sender_amount)));
            }else{
                totalAmountTextView.setText("- " + transactionDetails.data.sender_currency + " " + loginSession.getcurrencySymbol() + String.format(Locale.ENGLISH,"%.2f", Double.parseDouble(transactionDetails.data.sender_amount)));
            }*/


            offerTextView.setText(transactionDetails.data.sender_currency+" "+String.format(Locale.ENGLISH,"%.2f",offerAmount));
            double sAm = Double.parseDouble(transactionDetails.data.send_amount);
            double rewardd = Double.parseDouble(transactionDetails.data.reward_offer_amount);
            double newA = sAm+offerAmount+rewardd;
            int nf = Math.round(Float.parseFloat(String.valueOf(newA)));
            amountTextView.setText(loginSession.getShowCurrency()+" "+String.format(Locale.ENGLISH,"%.2f",Double.parseDouble(String.valueOf(nf))));
            totalPayableTextView.setText(transactionDetails.data.sender_currency + " " + loginSession.getcurrencySymbol() + String.format(Locale.ENGLISH,"%.2f", Double.parseDouble(transactionDetails.data.sender_amount)));

            if(transactionDetails.data.refund_transaction!=null){
                if(transactionDetails.data.refund_transaction.refund_status.equalsIgnoreCase("success")){
                    if(transactionDetails.data.refund_transaction.refund_type.equalsIgnoreCase("Partial")){

                        double total = Double.parseDouble(transactionDetails.data.sender_amount);
                        double refund_total = Double.parseDouble(transactionDetails.data.refund_transaction.refund_amount);
                        double finalAmount = total - refund_total;
                        refundTAmountTextView.setText(transactionDetails.data.sender_currency + " " +loginSession.getcurrencySymbol()+" "+ String.format(Locale.ENGLISH,"%.2f", refund_total));
                        totalPayableTextView.setText(transactionDetails.data.sender_currency + " " +loginSession.getcurrencySymbol()+" "+ String.format(Locale.ENGLISH,"%.2f", finalAmount));
                        refundTLayout.setVisibility(View.VISIBLE);
                    }
                }
            }

            //Set customer or merchant name
            if(transactionDetails.data.receiver.business_name!=null){
                shopNameTextView.setText(transactionDetails.data.receiver.business_name);
            }else{
                if(transactionDetails.data.sender_id.equalsIgnoreCase(loginSession.getCustomerID()) && transactionDetails.data.receiver_id.equalsIgnoreCase(loginSession.getCustomerID())){

                    if(transactionDetails.data.transaction_type.equalsIgnoreCase("f-c")){
                        shopNameTextView.setText(getResources().getString(R.string.BonusCredited));
                    }else if(transactionDetails.data.transaction_type.equalsIgnoreCase("b-c")){
                        shopNameTextView.setText(getResources().getString(R.string.Creditwallet));
                    }

                }else{
                    shopNameTextView.setText(transactionDetails.data.receiver.name);
                }

            }


        } else {

            //Set total amount
           // totalAmountTextView.setText("+ " + transactionDetails.data.receive_currency + " "+loginSession.getcurrencySymbol() + String.format(Locale.ENGLISH,"%.2f", Double.parseDouble(transactionDetails.data.receive_amount)));

            offerTextView.setText(transactionDetails.data.sender_currency+" "+String.format(Locale.ENGLISH,"%.2f",offerAmount));
            double sAm = Double.parseDouble(transactionDetails.data.receive_amount);
            double rewardd = Double.parseDouble(transactionDetails.data.reward_offer_amount);
            double newA = sAm+offerAmount+rewardd;
            int nf = Math.round(Float.parseFloat(String.valueOf(newA)));
            amountTextView.setText(loginSession.getShowCurrency()+" "+String.format(Locale.ENGLISH,"%.2f", Double.parseDouble(String.valueOf(nf))));
            totalPayableTextView.setText(transactionDetails.data.receive_currency + " "+loginSession.getcurrencySymbol() + String.format(Locale.ENGLISH,"%.2f", Double.parseDouble(transactionDetails.data.receive_amount)));

            if(transactionDetails.data.refund_transaction!=null){
                if(transactionDetails.data.refund_transaction.refund_status.equalsIgnoreCase("success")){
                    if(transactionDetails.data.refund_transaction.refund_type.equalsIgnoreCase("Partial")){

                        double total = Double.parseDouble(transactionDetails.data.receive_amount);
                        double refund_total = Double.parseDouble(transactionDetails.data.refund_transaction.refund_amount);
                        double finalAmount = total - refund_total;
                        refundTAmountTextView.setText(transactionDetails.data.sender_currency + " " +loginSession.getcurrencySymbol()+" "+ String.format(Locale.ENGLISH,"%.2f", refund_total));
                        totalPayableTextView.setText(transactionDetails.data.sender_currency + " " +loginSession.getcurrencySymbol()+" "+ String.format(Locale.ENGLISH,"%.2f", finalAmount));
                        refundTLayout.setVisibility(View.VISIBLE);
                    }
                }
            }

            //Set customer or merchant name
            if(transactionDetails.data.sender.business_name!=null){
                shopNameTextView.setText(transactionDetails.data.sender.business_name);
            }else{
                shopNameTextView.setText(transactionDetails.data.sender.name);
            }

        }

        //Set Description
        if(transactionDetails.data.description!=null){
            if(!transactionDetails.data.description.isEmpty()){
                descriptionTextView.setText(transactionDetails.data.description);
                descriptionText.setVisibility(View.VISIBLE);
                descriptionLine.setVisibility(View.VISIBLE);
                descriptionTextView.setVisibility(View.VISIBLE);
            }else{
                descriptionText.setVisibility(View.GONE);
                descriptionLine.setVisibility(View.GONE);
                descriptionTextView.setVisibility(View.GONE);

            }
        }else{
            descriptionText.setVisibility(View.GONE);
            descriptionLine.setVisibility(View.GONE);
            descriptionTextView.setVisibility(View.GONE);

        }

        //Set OrderId
        orderidTextView.setText(transactionDetails.data.transaction_no);

        //Set Transaction date
        transactedDateTextView.setText(timeZoneConverter(transactionDetails.data.created,loginSession.gettimeZone()));

    }

    @Override
    public void onFailure(String error, RequestID requestID) {

        hideProgressDialog();
        toast(error);
    }


}
