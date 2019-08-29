package com.omlah.customer.tabmore;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.omlah.customer.R;
import com.omlah.customer.account.GetStartedScreen;
import com.omlah.customer.account.MyProfileScreen;
import com.omlah.customer.account.ReferaFriendScreen;
import com.omlah.customer.base.BaseFragment;
import com.omlah.customer.common.LoginSession;
import com.omlah.customer.service.RequestID;
import com.omlah.customer.service.ServerListener;
import com.omlah.customer.service.ServerRequestwithHeader;
import com.omlah.customer.tabevent.EventHistoryScreen;
import com.omlah.customer.tabmore.mywallet.NewWalletScreen;
import com.omlah.customer.tabmore.rewards.RewardsHistory;
import com.omlah.customer.tabmore.smartcard.SmartCardScreen;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.omlah.customer.service.RequestID.REQ_LOGOUT;

/**
 * Created by admin on 15-09-2017.
 */

public class MoreScreen extends BaseFragment implements View.OnClickListener,ServerListener {

    ServerRequestwithHeader serverRequestwithHeader;
    LoginSession loginSession;

    //Create xml files
    @BindView(R.id.accountName)TextView accountName;
    @BindView(R.id.walletName)TextView walletName;
    @BindView(R.id.transactionName)TextView transactionName;
    @BindView(R.id.popcoinName)TextView popcoinName;
    @BindView(R.id.contactsName)TextView contactsName;
    @BindView(R.id.eventName)TextView eventName;
    @BindView(R.id.baccountName)TextView baccountName;
    @BindView(R.id.settingsName)TextView settingsName;
    @BindView(R.id.logoutName)TextView logoutName;
    @BindView(R.id.moneyRequestName)TextView moneyRequestName;
    @BindView(R.id.rfidName)TextView rfidName;
    @BindView(R.id.myWalletLayout)RelativeLayout myWalletLayout;
    @BindView(R.id.myAccountLayout)RelativeLayout myAccountLayout;
    @BindView(R.id.moneyRequestLayout)RelativeLayout moneyRequestLayout;
    @BindView(R.id.eventListLayout)RelativeLayout eventListLayout;
    @BindView(R.id.viewTransactionLayout)RelativeLayout viewTransactionLayout;
    @BindView(R.id.popcoinLayout)RelativeLayout popcoinLayout;
    @BindView(R.id.myContactsLayout)RelativeLayout myContactsLayout;
    @BindView(R.id.businnessAccountLayout)RelativeLayout businnessAccountLayout;
    @BindView(R.id.rfidLayout)RelativeLayout rfidLayout;
    @BindView(R.id.settingLayout)RelativeLayout settingLayout;
    @BindView(R.id.referLayout)RelativeLayout referLayout;
    @BindView(R.id.logoutLayout)RelativeLayout logoutLayout;
    @BindView(R.id.myCouponsLayout)RelativeLayout myCouponsLayout;
    @BindView(R.id.versionCodeTextView)TextView versionCodeTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.more_screen, container, false);

        //initialize xml file
        ButterKnife.bind(this,rootView);

        loginSession = LoginSession.getInstance(getActivity());
        serverRequestwithHeader = ServerRequestwithHeader.getInstance(getActivity());

        //Set Click event
        myWalletLayout.setOnClickListener(this);
        viewTransactionLayout.setOnClickListener(this);
        myContactsLayout.setOnClickListener(this);
        popcoinLayout.setOnClickListener(this);
        moneyRequestLayout.setOnClickListener(this);
        settingLayout.setOnClickListener(this);
        logoutLayout.setOnClickListener(this);
        eventListLayout.setOnClickListener(this);
        myAccountLayout.setOnClickListener(this);
        rfidLayout.setOnClickListener(this);
        referLayout.setOnClickListener(this);
        businnessAccountLayout.setOnClickListener(this);
        myCouponsLayout.setOnClickListener(this);

        try{
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            String version = pInfo.versionName;
            versionCodeTextView.setText(getResources().getString(R.string.Version)+version);
        }catch (Exception e){e.printStackTrace();}


        return rootView;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.myWalletLayout:

                Intent myWalletLayoutIntent = new Intent(getActivity(),NewWalletScreen.class);
                startActivity(myWalletLayoutIntent);

                break;

            case R.id.viewTransactionLayout:

                Intent viewTransactionIntent = new Intent(getActivity(), TransactionHistoryScreen.class);
                startActivity(viewTransactionIntent);

                break;

            case R.id.myContactsLayout:

                Intent myContactsIntent = new Intent(getActivity(),MyBeneficiaryScreen.class);
                startActivity(myContactsIntent);

                break;

            case R.id.popcoinLayout:

                Intent popcoinLayout = new Intent(getActivity(),RewardsHistory.class);
                startActivity(popcoinLayout);

                break;

            case R.id.moneyRequestLayout:

                Intent moneyRequestLayout = new Intent(getActivity(),MoneyRequestListScreen.class);
                startActivity(moneyRequestLayout);

                break;

            case R.id.eventListLayout:

                Intent eventListLayoutIntent = new Intent(getActivity(), EventHistoryScreen.class);
                startActivity(eventListLayoutIntent);

                break;

            case R.id.myAccountLayout:

                Intent myAccountLayoutIntent = new Intent(getActivity(), MyProfileScreen.class);
                startActivity(myAccountLayoutIntent);

                break;

            case R.id.rfidLayout:

                Intent rfidLayoutIntent = new Intent(getActivity(), SmartCardScreen.class);
                startActivity(rfidLayoutIntent);

                break;

            case R.id.logoutLayout:

                if(isConnectingToInternet()){

                    Map<String, String> params = new HashMap<>();
                    showProgressDialog();
                    serverRequestwithHeader.createRequest(MoreScreen.this,params, REQ_LOGOUT,"DELETE","");
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        loginSession.logout();
                        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.cancelAll();
                        Intent intent = new Intent(getActivity(),GetStartedScreen.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        getActivity().finish();
                    }
                },2000);

                break;

            case R.id.settingLayout:

                Intent settingIntent = new Intent(getActivity(),SettingsScreen.class);
                startActivity(settingIntent);

                break;

            case R.id.businnessAccountLayout:

                Intent businnessAccountIntent = new Intent(getActivity(),BusinessAccountScreen.class);
                startActivity(businnessAccountIntent);

                break;

            case R.id.referLayout:

                Intent referLayoutIntent = new Intent(getActivity(), ReferaFriendScreen.class);
                startActivity(referLayoutIntent);

                break;

            case R.id.myCouponsLayout:

                Intent myCouponsIntent = new Intent(getActivity(),MyVoucherCodeScreen.class);
                startActivity(myCouponsIntent);

                break;
        }
    }

    @Override
    public void onSuccess(Object result, RequestID requestID) {

        //hideProgressDialog();
        /*loginSession.logout();
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        Intent intent = new Intent(getActivity(),GetStartedScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();*/
    }

    @Override
    public void onFailure(String error, RequestID requestID) {

        //hideProgressDialog();
        /*loginSession.logout();
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        Intent intent = new Intent(getActivity(),GetStartedScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();*/
    }
}
