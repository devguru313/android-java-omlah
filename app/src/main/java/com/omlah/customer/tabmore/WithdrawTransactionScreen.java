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

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 09-11-2017.
 */

public class WithdrawTransactionScreen extends BaseActivity{

    //Create class objects
    LoginSession loginSession;

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
    @BindView(R.id.taxLayout)RelativeLayout taxLayout;
    @BindView(R.id.popPayFeeLayout)RelativeLayout popPayFeeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.withdraw_success);
        showBackArrow();
        setActionBarTitle(getResources().getString(R.string.WithdrawnTransactions));

        //Initialize xml objects
        ButterKnife.bind(this);

        loginSession = LoginSession.getInstance(this);

        Intent intent = getIntent();

        if(intent!=null){

            contentLayout.setVisibility(View.VISIBLE);
            String Date = intent.getStringExtra("Date");
            String Bankname = intent.getStringExtra("Bankname");
            String AccountNumber = intent.getStringExtra("AccountNumber");
            String WithdrawalAmount = intent.getStringExtra("WithdrawalAmount");
            String withdrawalFee = intent.getStringExtra("withdrawalFee");
            String totalAmount = intent.getStringExtra("totalAmount");
            String referenceNo = intent.getStringExtra("referenceNo");
            String status = intent.getStringExtra("status");


            if(status.equalsIgnoreCase("pending")){
                statusTextView.setText(getResources().getString(R.string.Pending));
                statusTextView.setTextColor(getResources().getColor(R.color.yellow));
            }else if(status.equalsIgnoreCase("success")){
                statusTextView.setText(getResources().getString(R.string.Success));
                statusTextView.setTextColor(getResources().getColor(R.color.green));
            }else{
                statusTextView.setText(status);
            }



            shopNameTextView.setText(Bankname+"-"+AccountNumber);
            totalAmountTextView.setText("-"+loginSession.getShowCurrency()+" "+String.format(Locale.ENGLISH,"%.2f",Double.parseDouble(WithdrawalAmount)));

            transactedDateTextView.setText(Date);
            orderidTextView.setText(referenceNo);

            amountTextView.setText(loginSession.getShowCurrency()+" "+String.format(Locale.ENGLISH,"%.2f",Double.parseDouble(totalAmount)));
            taxAmountTextView.setText(loginSession.getShowCurrency()+" "+String.format(Locale.ENGLISH,"%.2f",Double.parseDouble(withdrawalFee)));
            totalPayableTextView.setText(loginSession.getShowCurrency()+" "+String.format(Locale.ENGLISH,"%.2f",Double.parseDouble(WithdrawalAmount)));
        }
    }
}
