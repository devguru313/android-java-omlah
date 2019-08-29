package com.omlah.customer.tabhome;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.omlah.customer.R;
import com.omlah.customer.base.BaseActivity;
import com.omlah.customer.base.BaseScreen;
import com.omlah.customer.common.LoginSession;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 19-09-2017.
 */

public class SuccessScreen extends BaseActivity {

    //Create xml file
    LoginSession loginSession;

    //Create xml objects
    @BindView(R.id.backIconImageView)ImageView backIconImageView;
    @BindView(R.id.actionBarTitleTextview)TextView actionBarTitleTextview;
    @BindView(R.id.totalAmountTextView)TextView totalAmountTextView;
    @BindView(R.id.datetextView)TextView datetextView;
    @BindView(R.id.receiverNameTextView)TextView receiverNameTextView;
    @BindView(R.id.descriptionText)TextView descriptionText;
    @BindView(R.id.descriptionTextView)TextView descriptionTextView;
    @BindView(R.id.popPayIdTextView)TextView popPayIdTextView;
    @BindView(R.id.paymentMessage)TextView paymentMessage;
    @BindView(R.id.forTextView)TextView forTextView;
    @BindView(R.id.backToHomeTextView)TextView backToHomeTextView;

    //String created
    String fromScreen="",screen="",amount ="",currency="",createdDate="",receiverName="",description="",transaction_no="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.success_screen);
        hideActionBar();

        //Initialize xml file
        ButterKnife.bind(this);
        loginSession = LoginSession.getInstance(this);

        //Get intent values
        Intent intent = getIntent();

        if(intent!=null){

            Log.e("amount",intent.getStringExtra("amount"));

            fromScreen = intent.getStringExtra("fromScreen");
            screen = intent.getStringExtra("screen");
            amount = intent.getStringExtra("amount");
            currency = intent.getStringExtra("currency");
            createdDate = intent.getStringExtra("date");
            receiverName = intent.getStringExtra("receiverName");
            description = intent.getStringExtra("description");
            transaction_no = intent.getStringExtra("transaction_no");

            if(screen.equalsIgnoreCase("PayMoney")){

                totalAmountTextView.setText(loginSession.getShowCurrency()+" "+String.format(Locale.ENGLISH,"%.2f", Double.parseDouble(amount)));
                actionBarTitleTextview.setText("Paid to "+receiverName);
                paymentMessage.setText("Paid Successfully");
                forTextView.setText("TO");

            }else if(screen.equalsIgnoreCase("SendCoin")){

                totalAmountTextView.setText((amount));
                actionBarTitleTextview.setText("Rewards Sent to "+receiverName);
                paymentMessage.setText("Rewards sent Successfully");
                forTextView.setText("TO");
                descriptionText.setVisibility(View.GONE);
                descriptionTextView.setVisibility(View.GONE);

            }else{

                totalAmountTextView.setText(loginSession.getShowCurrency()+" "+String.format(Locale.ENGLISH,"%.2f", Double.parseDouble(amount)));
                actionBarTitleTextview.setText("Request for "+receiverName);
                paymentMessage.setText("Request sent Successfully");
                forTextView.setText("FOR");
            }


            receiverNameTextView.setText(receiverName);
            popPayIdTextView.setText("Txn ID : "+transaction_no);

            if(!description.isEmpty()){
                descriptionTextView.setText(description);
                descriptionText.setVisibility(View.VISIBLE);
                descriptionTextView.setVisibility(View.VISIBLE);
            }else{
                descriptionText.setVisibility(View.GONE);
                descriptionTextView.setVisibility(View.GONE);
            }

            datetextView.setText(timeZoneConverter(createdDate, loginSession.gettimeZone()));

        }

        backIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(fromScreen.equalsIgnoreCase("MoneyRequestList")){
                    finish();
                }else if(fromScreen.equalsIgnoreCase("PayMoney")){
                    finish();
                }else{
                    Intent intent = new Intent(SuccessScreen.this,BaseScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }

            }
        });


        backToHomeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {

        if(fromScreen.equalsIgnoreCase("MoneyRequestList")){
            finish();
        }else if(fromScreen.equalsIgnoreCase("PayMoney")){
            finish();
        }else{
            Intent intent = new Intent(SuccessScreen.this,BaseScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }
}
