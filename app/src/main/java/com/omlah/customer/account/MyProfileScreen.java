package com.omlah.customer.account;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.omlah.customer.R;
import com.omlah.customer.base.BaseActivity;
import com.omlah.customer.common.LoginSession;
import com.omlah.customer.service.ServerRequestwithHeader;

import net.glxn.qrgen.android.QRCode;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Admin on 05-04-2018.
 */

public class MyProfileScreen extends BaseActivity implements View.OnClickListener {

    //Create class objects
    ServerRequestwithHeader serverRequestwithHeader;
    private LoginSession loginSession;

    //Create xml file
    @BindView(R.id.backIconImageView)ImageView backIconImageView;
    @BindView(R.id.qrImageView)ImageView qrImageView;
    @BindView(R.id.userNameQRTextView)TextView userNameQRTextView;
    @BindView(R.id.editProfileLayout)RelativeLayout editProfileLayout;
    @BindView(R.id.updateKYCLayout)RelativeLayout updateKYCLayout;
    @BindView(R.id.savedCardsLayout)RelativeLayout savedCardsLayout;
    @BindView(R.id.bankAccountLayout)RelativeLayout bankAccountLayout;
    @BindView(R.id.refferLayout)RelativeLayout refferLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_profile);
        hideActionBar();

        //Initialize xml file
        ButterKnife.bind(this);

        //Initialize class objects
        loginSession   = LoginSession.getInstance(this);
        serverRequestwithHeader = ServerRequestwithHeader.getInstance(this);

        //Set Name
        userNameQRTextView.setText(loginSession.getname());

        //Show qr image
        String QRCODE = loginSession.getQRCODE();
        Bitmap myBitmap = QRCode.from(QRCODE).withSize(512,512).withColor(getResources().getColor(R.color.black),getResources().getColor(R.color.QRCodeWhiteColor)).bitmap();
        qrImageView.setImageBitmap(myBitmap);


        editProfileLayout.setOnClickListener(this);
        updateKYCLayout.setOnClickListener(this);
        savedCardsLayout.setOnClickListener(this);
        bankAccountLayout.setOnClickListener(this);
        refferLayout.setOnClickListener(this);

        backIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }

        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.editProfileLayout:

                Intent intent = new Intent(MyProfileScreen.this,ProfileScreen.class);
                startActivity(intent);

                break;

            case R.id.updateKYCLayout:


                break;

            case R.id.savedCardsLayout:

                Intent savedCardsintent = new Intent(MyProfileScreen.this,SavedCardsScreen.class);
                startActivity(savedCardsintent);

                break;

            case R.id.bankAccountLayout:

                Intent bankAccountintent = new Intent(MyProfileScreen.this,BankAccountsScreen.class);
                startActivity(bankAccountintent);

                break;

            case R.id.refferLayout:

                Intent refferLayoutintent = new Intent(MyProfileScreen.this, ReferaFriendScreen.class);
                startActivity(refferLayoutintent);

                break;


        }
    }
}
