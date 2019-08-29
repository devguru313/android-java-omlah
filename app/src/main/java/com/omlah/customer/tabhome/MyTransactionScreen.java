package com.omlah.customer.tabhome;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

/**
 * Created by admin on 18-09-2017.
 */

public class MyTransactionScreen extends BaseActivity implements ServerListener{

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
    @BindView(R.id.taxLayout)RelativeLayout taxLayout;
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

    @BindView(R.id.refundTLayout)RelativeLayout refundTLayout;
    @BindView(R.id.refundTAmountTextView)TextView refundTAmountTextView;

    String id="",type="",typeAmount="";

    double FEE_TOTAL_AMOUNT=0;
    //boolean
    boolean b1 = false;

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
            totalAmountTextView.setText(typeAmount);
        }

        //Get user details
        if(isConnectingToInternet()){

            Map<String, String> params = new HashMap<>();
            showProgressDialog();
            serverRequestwithHeader.createRequest(MyTransactionScreen.this,params, RequestID.REQ_TRANSACTION_DEATILS,"GET",id);

        }else{
            noInternetAlertDialog();
        }
    }

    @Override
    public void onSuccess(Object result, RequestID requestID) {

        hideProgressDialog();
        contentLayout.setVisibility(View.VISIBLE);
        TransactionDetails transactionDetails = (TransactionDetails)result;


        String feePrice = transactionDetails.data.additional_fees;

        if(!feePrice.isEmpty()){

            String splitFeePrice[] = feePrice.split(",");

            for (String feeprice : splitFeePrice){
                if(!feeprice.trim().isEmpty()){
                    FEE_TOTAL_AMOUNT =+Double.parseDouble(feeprice);
                }
            }
        }

        try{
            if(transactionDetails.data.refund_transaction!=null){
                refundLine.setVisibility(View.VISIBLE);
                refundinfoText.setVisibility(View.VISIBLE);
                refundLayout.setVisibility(View.VISIBLE);
                refundTypeTextView.setText(transactionDetails.data.refund_transaction.refund_type);
                refundAmountTextView.setText(loginSession.getcurrencyCodee() +" "+ String.format(Locale.ENGLISH,"%.2f", Double.parseDouble(transactionDetails.data.refund_transaction.refund_amount)));
                refundStatusTextView.setText(transactionDetails.data.refund_transaction.refund_status.substring(0,1).toUpperCase()+transactionDetails.data.refund_transaction.refund_status.substring(1,transactionDetails.data.refund_transaction.refund_status.length()));
            }else{
                refundLine.setVisibility(View.GONE);
                refundinfoText.setVisibility(View.GONE);
                refundLayout.setVisibility(View.GONE);
            }
        }catch (Exception e){}

        try{
            if(!transactionDetails.data.reward_transactions.isEmpty()){
                earnPointsTextView.setVisibility(View.VISIBLE);
                earnPointsTextView.setText(getResources().getString(R.string.YouHaveEarned)+" "+transactionDetails.data.reward_transactions.get(0).rewards_earned+" points.");
            }else{
                earnPointsTextView.setVisibility(View.GONE);
                rewardLine.setVisibility(View.GONE);
            }
        }catch (Exception e){e.printStackTrace();
            earnPointsTextView.setVisibility(View.GONE);
            rewardLine.setVisibility(View.GONE);}


        double offerAmount = 0;

        if(Double.parseDouble(transactionDetails.data.offer_amount) > 0){
            offerLayout.setVisibility(View.VISIBLE);
            offerText.setText(getResources().getString(R.string.Offer)+" ( "+transactionDetails.data.offer_percentage+"% )");
            offerAmount = Double.parseDouble(transactionDetails.data.offer_amount);
        }else{
            offerLayout.setVisibility(View.GONE);
        }

        if(type.equalsIgnoreCase("Topup")){

            typeImageView.setImageDrawable(getResources().getDrawable(R.drawable.topup));
            typeTextView.setText(getResources().getString(R.string.Topup));
            taxLayout.setVisibility(View.GONE);
            popPayFeeLayout.setVisibility(View.GONE);

        }else if(type.equalsIgnoreCase("Paid")){

            typeImageView.setImageDrawable(getResources().getDrawable(R.drawable.paid));
            typeTextView.setText(getResources().getString(R.string.Paid));

        }else if(type.equalsIgnoreCase("Refund")){

            if(transactionDetails.data.refund_transaction!=null){
                typeTextView.setText(transactionDetails.data.refund_transaction.refund_type+" "+"Refund");
            }else{
                typeTextView.setText(getResources().getString(R.string.Refund));
            }

            refundTypeText.setText("Refund Type");
            refundAmountText.setText("Refund Amount");
            refundStatusText.setText("Refund Status");
            typeImageView.setImageDrawable(getResources().getDrawable(R.drawable.received));


        }else if(type.equalsIgnoreCase("Refunded")){

            if(transactionDetails.data.refund_transaction!=null){
                typeTextView.setText(transactionDetails.data.refund_transaction.refund_type+" "+"Refunded");
            }else{
                typeTextView.setText(getResources().getString(R.string.Refund));
            }

            refundTypeText.setText("Refunded Type");
            refundAmountText.setText("Refunded Amount");
            refundStatusText.setText("Refunded Status");
            typeImageView.setImageDrawable(getResources().getDrawable(R.drawable.received));

        }else{

            typeImageView.setImageDrawable(getResources().getDrawable(R.drawable.received));
            typeTextView.setText(getResources().getString(R.string.Received));
            taxLayout.setVisibility(View.GONE);
            popPayFeeLayout.setVisibility(View.GONE);

        }

        //Set transaction status
        if(transactionDetails.data.status.equalsIgnoreCase("success")){
            statusTextView.setText(getResources().getString(R.string.Success));
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

        //Set transaction status
        try {
            if (transactionDetails.data.reward_transactions.size() > 0) {
                if (Double.parseDouble(transactionDetails.data.reward_transactions.get(0).discount_amount) > 0) {
                    redeemAmountLayout.setVisibility(View.VISIBLE);
                    redeemAmountTextView.setText(loginSession.getShowCurrency() + " " + changeCustomerCurrency(transactionDetails.data.reward_transactions.get(0).discount_amount,transactionDetails.data.sender_currency_difference));
                    redeemAmountText.setText(getResources().getString(R.string.Reward));
                } else {
                    redeemAmountLayout.setVisibility(View.GONE);

                }
            } else {
                redeemAmountLayout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            redeemAmountLayout.setVisibility(View.GONE);
        }

        //tip show
        if(!(transactionDetails.data.tip_amount == null)){

            if(Double.parseDouble(transactionDetails.data.tip_amount) > 0){
                tipLayout.setVisibility(View.VISIBLE);
                tipAmountTextView.setText(loginSession.getShowCurrency() +" "+ changeCustomerCurrency(transactionDetails.data.tip_amount,transactionDetails.data.sender_currency_difference));

            }else{
                tipLayout.setVisibility(View.GONE);
            }
        } else{
            tipLayout.setVisibility(View.GONE);
        }



        //set Amount section
        if(transactionDetails.data.sender_id.equalsIgnoreCase(loginSession.getCustomerID())){

            if(transactionDetails.data.transaction_type.equalsIgnoreCase("c-r")){

                taxLayout.setVisibility(View.GONE);
                popPayFeeLayout.setVisibility(View.GONE);
                offerLayout.setVisibility(View.GONE);
                redeemAmountLayout.setVisibility(View.GONE);
                tipLayout.setVisibility(View.GONE);
                voucherLayout.setVisibility(View.GONE);
                descriptionText.setVisibility(View.GONE);
                descriptionTextView.setVisibility(View.GONE);
                descriptionLine.setVisibility(View.GONE);
                rewardLine.setVisibility(View.GONE);
                earnPointsTextView.setVisibility(View.GONE);
                typeTextView.setText("Paid for Recharge");
                shopNameTextView.setText("RPay-Recharge");

                if(transactionDetails.data.status.equalsIgnoreCase("success")){
                    Double TT = Double.parseDouble(transactionDetails.data.send_amount);
                    Double FINALAMOUNT = TT - FEE_TOTAL_AMOUNT;
                    //totalAmountTextView.setText("-"+transactionDetails.data.sender_currency + " " + loginSession.getcurrencySymbol() +" "+ String.format(Locale.ENGLISH,"%.2f", Double.parseDouble(transactionDetails.data.send_amount)));
                    amountTextView.setText(loginSession.getShowCurrency() +" "+ changeCustomerCurrency(String.valueOf(FINALAMOUNT),transactionDetails.data.sender_currency_difference));
                    totalPayableTextView.setText(loginSession.getShowCurrency() +" "+ changeCustomerCurrency(transactionDetails.data.send_amount,transactionDetails.data.sender_currency_difference));

                }else{
                    //totalAmountTextView.setText("0.00");
                    amountTextView.setText(("0.00"));
                    totalPayableTextView.setText(("0.00"));

                }

            }else{

                //set tax fee
                if (Double.parseDouble(transactionDetails.data.tax_amount) > 0) {
                    taxLayout.setVisibility(View.VISIBLE);
                    serviceFeeText.setText(getResources().getString(R.string.Tax)+" ( " + transactionDetails.data.tax_percentage + "% )");
                    taxAmountTextView.setText(loginSession.getShowCurrency()+" " + changeCustomerCurrency(transactionDetails.data.tax_amount,transactionDetails.data.sender_currency_difference));
                } else {
                    taxLayout.setVisibility(View.GONE);
                }

                //set poppay fee
                if (Double.parseDouble(transactionDetails.data.poppay_fee) > 0) {
                    popPayFeeLayout.setVisibility(View.VISIBLE);
                    popPayFeeTextView.setText(loginSession.getShowCurrency()+" "+ changeCustomerCurrency(transactionDetails.data.poppay_fee,transactionDetails.data.sender_currency_difference));
                } else {
                    popPayFeeLayout.setVisibility(View.GONE);
                }

                //Set total Amount

                //totalAmountTextView.setText("- " + transactionDetails.data.sender_currency + " " + loginSession.getcurrencySymbol() +" "+ String.format(Locale.ENGLISH,"%.2f", Double.parseDouble(transactionDetails.data.sender_amount)));

                //voucher
                if(Double.parseDouble(transactionDetails.data.voucher_amount) > 0 ){
                    voucherLayout.setVisibility(View.VISIBLE);
                    voucherFeeText.setText("Voucher ( "+transactionDetails.data.voucher_percentage+"% )");
                    voucherAmountTextView.setText(loginSession.getShowCurrency()+" "+changeCustomerCurrency(transactionDetails.data.voucher_amount,transactionDetails.data.sender_currency_difference));
                }else{
                    voucherLayout.setVisibility(View.GONE);
                }


                offerTextView.setText(loginSession.getShowCurrency()+" "+changeCustomerCurrency(String.valueOf(offerAmount),transactionDetails.data.sender_currency_difference));
                double sAm = Double.parseDouble(transactionDetails.data.send_amount);
                double vou = Double.parseDouble(transactionDetails.data.voucher_amount);
                double rewardd = Double.parseDouble(transactionDetails.data.reward_offer_amount);
                double newA = sAm+offerAmount+rewardd+vou-FEE_TOTAL_AMOUNT;
                Log.e("newA",""+newA);
                //int nf = Math.round(Float.parseFloat(String.valueOf(newA)));
                amountTextView.setText(loginSession.getShowCurrency()+" "+changeCustomerCurrency(String.valueOf(newA),transactionDetails.data.sender_currency_difference));

                totalPayableTextView.setText(loginSession.getShowCurrency()+" "+ changeCustomerCurrency(transactionDetails.data.sender_amount,transactionDetails.data.sender_currency_difference));

                if(transactionDetails.data.refund_transaction!=null){
                    if(transactionDetails.data.refund_transaction.refund_status.equalsIgnoreCase("success")){
                        if(transactionDetails.data.refund_transaction.refund_type.equalsIgnoreCase("Partial")){

                            double total = Double.parseDouble(transactionDetails.data.sender_amount);
                            double refund_total = Double.parseDouble(transactionDetails.data.refund_transaction.refund_amount);
                            double finalAmount = total - refund_total;
                            refundTAmountTextView.setText(loginSession.getShowCurrency()+" "+ changeCustomerCurrency(String.valueOf(refund_total),transactionDetails.data.sender_currency_difference));
                            totalPayableTextView.setText(loginSession.getShowCurrency()+" "+ changeCustomerCurrency(String.valueOf(finalAmount),transactionDetails.data.sender_currency_difference));
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


            }

        } else {

            //voucher
            if(Double.parseDouble(transactionDetails.data.voucher_amount) > 0 ){
                voucherLayout.setVisibility(View.VISIBLE);
                voucherFeeText.setText("Voucher ( "+transactionDetails.data.voucher_percentage+"% )");
                voucherAmountTextView.setText(loginSession.getShowCurrency()+" "+changeCustomerCurrency(transactionDetails.data.voucher_amount,transactionDetails.data.sender_currency_difference));
            }else{
                voucherLayout.setVisibility(View.GONE);
            }

            //Set total amount

            //totalAmountTextView.setText("+ " + transactionDetails.data.receive_currency + " "+loginSession.getcurrencySymbol()+" " + String.format(Locale.ENGLISH,"%.2f", Double.parseDouble(transactionDetails.data.receive_amount)));

            offerTextView.setText(loginSession.getShowCurrency()+" "+changeCustomerCurrency(String.valueOf(offerAmount),transactionDetails.data.sender_currency_difference));
            double sAm = Double.parseDouble(transactionDetails.data.receive_amount);
            double vou = Double.parseDouble(transactionDetails.data.voucher_amount);
            double rewardd = Double.parseDouble(transactionDetails.data.reward_offer_amount);
            double newA = sAm+offerAmount+rewardd+vou-FEE_TOTAL_AMOUNT;
           // int nf = Math.round(Float.parseFloat(String.valueOf(newA)));
            amountTextView.setText(loginSession.getShowCurrency()+" "+changeCustomerCurrency(String.valueOf(newA),transactionDetails.data.sender_currency_difference));
            totalPayableTextView.setText(loginSession.getShowCurrency()+" "+ changeCustomerCurrency(transactionDetails.data.receive_amount,transactionDetails.data.sender_currency_difference));

            if(transactionDetails.data.refund_transaction!=null){
                if(transactionDetails.data.refund_transaction.refund_status.equalsIgnoreCase("success")){
                    if(transactionDetails.data.refund_transaction.refund_type.equalsIgnoreCase("Partial")){

                        double total = Double.parseDouble(transactionDetails.data.receive_amount);
                        double refund_total = Double.parseDouble(transactionDetails.data.refund_transaction.refund_amount);
                        double finalAmount = total - refund_total;
                        refundTAmountTextView.setText(loginSession.getShowCurrency()+" "+ changeCustomerCurrency(String.valueOf(refund_total),transactionDetails.data.sender_currency_difference));
                        totalPayableTextView.setText(loginSession.getShowCurrency()+" "+ changeCustomerCurrency(String.valueOf(finalAmount),transactionDetails.data.sender_currency_difference));
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
            }
        }

        //Set OrderId
        orderidTextView.setText(transactionDetails.data.transaction_no);

        //Set Transaction date
        transactedDateTextView.setText(timeZoneConverter(transactionDetails.data.created,loginSession.gettimeZone()));

    }

    @Override
    public void onFailure(String error, RequestID requestID) {

        hideProgressDialog();
    }
}