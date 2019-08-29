package com.omlah.customer.tabhome.recharge;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.omlah.customer.R;
import com.omlah.customer.base.BaseActivity;
import com.omlah.customer.base.BaseScreen;
import com.omlah.customer.common.CircleTransform;
import com.omlah.customer.common.LoginSession;
import com.omlah.customer.model.recharge.RechargeSuccessResponse;
import com.omlah.customer.service.RequestID;
import com.omlah.customer.service.ServerListener;
import com.omlah.customer.service.ServerRequestwithHeader;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RechargeSuccessScreen extends BaseActivity implements ServerListener {

    //Create xml file
    LoginSession loginSession;
    ServerRequestwithHeader serverRequestwithHeader;

    //Create xml objects
    @BindView(R.id.backIconImageView)ImageView backIconImageView;
    @BindView(R.id.tickImageView)ImageView tickImageView;
    @BindView(R.id.progrssBar)ProgressBar progrssBar;
    @BindView(R.id.totalAmountTextView)TextView totalAmountTextView;
    @BindView(R.id.paymentMessage)TextView paymentMessage;
    @BindView(R.id.datetextView)TextView datetextView;

    @BindView(R.id.operatorImageView)ImageView operatorImageView;
    @BindView(R.id.operatorNameTextView)TextView operatorNameTextView;
    @BindView(R.id.orderIdTextView)TextView orderIdTextView;
    @BindView(R.id.confirmedTextView)TextView confirmedTextView;
    @BindView(R.id.backToHomeTextView)TextView backToHomeTextView;

    String phoneCode="",phoneNumber="",rechargeAmount ="",operatorImage="",operatorName="",currencyCode="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recharge_success);
        hideActionBar();

        //Initialize xml file
        ButterKnife.bind(this);
        loginSession = LoginSession.getInstance(this);
        serverRequestwithHeader = ServerRequestwithHeader.getInstance(this);

        //Get intent values
        final Intent intent = getIntent();
        if (intent != null) {

            Map<String, String> params = new HashMap<>();
            rechargeAmount = intent.getStringExtra("rechargeAmount");
            currencyCode = intent.getStringExtra("selectCountryCurrency");
            phoneCode = intent.getStringExtra("phoneCode");
            operatorImage = intent.getStringExtra("operatorImage");
            operatorName = intent.getStringExtra("operatorName");
            phoneNumber = intent.getStringExtra("phoneNumber");
            params.put("phone_number",intent.getStringExtra("phone_number"));
            params.put("country_code",intent.getStringExtra("country_code"));
            params.put("operator_id",intent.getStringExtra("operator_id"));
            params.put("amount",intent.getStringExtra("amount"));
            params.put("serviceImage",intent.getStringExtra("operatorImage"));
            params.put("serviceProvider",intent.getStringExtra("operatorName"));
            params.put("flag",intent.getStringExtra("selectedCountryFlag"));
            params.put("callingCodes",phoneCode);
            progrssBar.setVisibility(View.VISIBLE);
            tickImageView.setVisibility(View.GONE);
            serverRequestwithHeader.createRequest(RechargeSuccessScreen.this, params, RequestID.REQ_COMPLETE_RECHARGE, "POST","");


            String userAvatarUrl = operatorImage;
            try {
                String prevURL="";
                String decodeURL=userAvatarUrl;
                while(!prevURL.equals(decodeURL))
                {
                    prevURL=decodeURL;
                    decodeURL= URLDecoder.decode( decodeURL, "UTF-8" );
                }
                userAvatarUrl = decodeURL;
                Picasso.with(this).load(userAvatarUrl).transform(new CircleTransform()).into(operatorImageView);
            } catch (UnsupportedEncodingException e) {

            }

            operatorNameTextView.setText(operatorName);
            totalAmountTextView.setText(currencyCode+" "+rechargeAmount);

        }


        backToHomeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                onBackPressed();
            }
        });
    }

    @Override
    public void onSuccess(Object result, RequestID requestID) {

        progrssBar.setVisibility(View.GONE);
        tickImageView.setVisibility(View.VISIBLE);
        RechargeSuccessResponse rechargeSuccessResponse = (RechargeSuccessResponse)result;
        if(rechargeSuccessResponse.message.equalsIgnoreCase("Recharge successfully") || rechargeSuccessResponse.message.contains("successfully")){
            paymentMessage.setText(rechargeSuccessResponse.message);
            datetextView.setText(rechargeSuccessResponse.data.transactionDate);
            orderIdTextView.setText(rechargeSuccessResponse.data.transactionId);
            confirmedTextView.setText("Recharge confirmed");
        }else{
            tickImageView.setBackground(getResources().getDrawable(R.drawable.red_corner));
            tickImageView.setImageDrawable(getResources().getDrawable(R.drawable.close_red));
            paymentMessage.setText(rechargeSuccessResponse.message);
            datetextView.setText("");
            orderIdTextView.setText("");
            confirmedTextView.setText("Recharge failed");
        }

    }

    @Override
    public void onFailure(String error, RequestID requestID) {

        tickImageView.setBackground(getResources().getDrawable(R.drawable.red_corner));
        tickImageView.setImageDrawable(getResources().getDrawable(R.drawable.close_red));
        paymentMessage.setText("Recharge failed");
        datetextView.setText("");
        orderIdTextView.setText("");
        confirmedTextView.setText("Recharge failed");
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RechargeSuccessScreen.this,BaseScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
