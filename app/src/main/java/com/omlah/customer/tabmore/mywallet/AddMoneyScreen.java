package com.omlah.customer.tabmore.mywallet;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.omlah.customer.R;
import com.omlah.customer.base.BaseFragment;
import com.omlah.customer.common.LoginSession;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 21-10-2017.
 */

public class AddMoneyScreen extends BaseFragment{

    LoginSession loginSession;

    @BindView(R.id.firstLayout)RelativeLayout  firstLayout;
    @BindView(R.id.secondLayout)RelativeLayout secondLayout;
    @BindView(R.id.amountEditText)EditText amountEditText;
    @BindView(R.id.currencySymbol)TextView currencySymbol;
    @BindView(R.id.bottomTextView)TextView bottomTextView;
    @BindView(R.id.bottomTextView2)TextView bottomTextView2;
    @BindView(R.id.enteredAmountTextView)TextView enteredAmountTextView;
    @BindView(R.id.payNowButton)Button payNowButton;
    @BindView(R.id.enterAmountLayout)LinearLayout enterAmountLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_money_screen, container, false);

        ButterKnife.bind(this,rootView);

        firstLayout.setVisibility(View.GONE);
        secondLayout.setVisibility(View.GONE);

        loginSession = LoginSession.getInstance(getActivity());

        currencySymbol.setText(loginSession.getShowCurrency());
        bottomTextView.setText("Per month you can add upto \n"+loginSession.getShowCurrency()+" in your wallet why?");
        bottomTextView2.setText("Per month you can add upto \n"+loginSession.getShowCurrency()+" in your wallet why?");

        amountEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                payNowButton.setVisibility(View.VISIBLE);
            }
        });

        payNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String enteredAmount = amountEditText.getText().toString().trim();

                if(enteredAmount.isEmpty()){

                    toast(getResources().getString(R.string.pleaseenteraamount));

                }else{

                    firstLayout.setVisibility(View.GONE);
                    secondLayout.setVisibility(View.VISIBLE);

                   enteredAmountTextView.setText("Entered Amount : "+loginSession.getShowCurrency()+" "+amountEditText.getText().toString().trim());
                }

            }
        });


        enteredAmountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firstLayout.setVisibility(View.VISIBLE);
                secondLayout.setVisibility(View.GONE);

            }
        });

        return rootView;

    }
}
