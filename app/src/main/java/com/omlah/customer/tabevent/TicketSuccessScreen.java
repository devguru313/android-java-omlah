package com.omlah.customer.tabevent;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.omlah.customer.R;
import com.omlah.customer.base.BaseActivity;
import com.omlah.customer.base.BaseScreen;
import com.omlah.customer.base.Utility;
import com.omlah.customer.common.LoginSession;
import com.omlah.customer.fcm.Config;
import com.omlah.customer.model.TicketBookingDetails;
import com.omlah.customer.service.RequestID;
import com.omlah.customer.service.ServerListener;
import com.omlah.customer.service.ServerRequestwithHeader;
import com.squareup.picasso.Picasso;

import net.glxn.qrgen.android.QRCode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 17-10-2017.
 */

public class TicketSuccessScreen extends BaseActivity implements ServerListener{

    Utility utility;
    LoginSession loginSession;
    ServerRequestwithHeader serverRequestwithHeader;
    TicketAdapter ticketAdapter;

    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.backIconImageView)ImageView backIconImageView;
    @BindView(R.id.actionBarTitleTextview)TextView actionBarTitleTextview;
    @BindView(R.id.eventErrorImage)RelativeLayout eventErrorImage;
    @BindView(R.id.contentLayout)LinearLayout contentLayout;
    @BindView(R.id.successMessageView)TextView successMessageView;
    @BindView(R.id.eventImageView)ImageView eventImageView;
    @BindView(R.id.eventNameTextView)TextView eventNameTextView;
    @BindView(R.id.eventPlaceNameTextView)TextView eventPlaceNameTextView;
    @BindView(R.id.eventAddressNameTextView)TextView eventAddressNameTextView;
    @BindView(R.id.eventDateTextView)TextView eventDateTextView;
    @BindView(R.id.eventTimeTextView)TextView eventTimeTextView;
    @BindView(R.id.grandTotalTextView)TextView grandTotalTextView;
    @BindView(R.id.serviceFeeTextView)TextView serviceFeeTextView;
    @BindView(R.id.serviceFee)TextView serviceFee;
    @BindView(R.id.rpayFee)TextView rpayFee;
    @BindView(R.id.rpayFeeTextView)TextView rpayFeeTextView;
    @BindView(R.id.gFee)TextView gFee;
    @BindView(R.id.gFeeTextView)TextView gFeeTextView;
    @BindView(R.id.bookingIdTextView)TextView bookingIdTextView;
    @BindView(R.id.ticketListView)ListView ticketListView;
    @BindView(R.id.qrImageView)ImageView qrImageView;
    @BindView(R.id.verifiedText)TextView verifiedText;
    @BindView(R.id.backToHomeTextView)
    TextView backToHomeTextView;


    String bookingID = "",screenType="",ticketStatus;

    //BroadcastReceiver method
    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ticket_success_screen);
        hideActionBar();


        ButterKnife.bind(this);

        utility = Utility.getInstance(this);
        loginSession = LoginSession.getInstance(this);
        serverRequestwithHeader = ServerRequestwithHeader.getInstance(this);

        //Get Intent values
        final Intent intent = getIntent();
        if(intent!=null) {
            bookingID = intent.getStringExtra("bookingID");
            screenType = intent.getStringExtra("Screen");
            ticketStatus = intent.getStringExtra("Status");

            if(screenType.equalsIgnoreCase("eventHistory")){
                actionBarTitleTextview.setText(getResources().getString(R.string.BookingDetails));
                successMessageView.setVisibility(View.GONE);
            }else{
                actionBarTitleTextview.setText(getResources().getString(R.string.BookingSuccess));
                successMessageView.setVisibility(View.VISIBLE);
            }

            if(isConnectingToInternet()){
                Map<String, String> params = new HashMap<>();
                showProgressDialog();
                serverRequestwithHeader.createRequest(TicketSuccessScreen.this,params, RequestID.REQ_BOOKING_DETAILS,"GET",bookingID);
            }else{
                noInternetAlertDialog();
            }
        }

        backIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(screenType.equalsIgnoreCase("eventHistory")){
                    finish();
                }else{
                    Intent intent = new Intent(TicketSuccessScreen.this,BaseScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }

            }
        });

        //Notification message received method
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                boolean check_update = intent.getBooleanExtra("update", false);

                if(check_update){

                    String QRCODE = loginSession.getCustomerID()+"Dummy data";
                    Bitmap myBitmap = QRCode.from(QRCODE).withSize(512,512).withColor(getResources().getColor(R.color.dull),getResources().getColor(R.color.QRCodeWhiteColor)).bitmap();
                    qrImageView.setImageBitmap(myBitmap);
                    verifiedText.setVisibility(View.VISIBLE);

                }
            }
        };

        backToHomeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {

        if(screenType.equalsIgnoreCase("eventHistory")){
            finish();
        }else{
            Intent intent = new Intent(TicketSuccessScreen.this,BaseScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onSuccess(Object result, RequestID requestID) {

        hideProgressDialog();

        contentLayout.setVisibility(View.VISIBLE);

        TicketBookingDetails ticketBookingDetails = (TicketBookingDetails)result;

        if(ticketStatus.equalsIgnoreCase("0")){
            String QRCODE = loginSession.getUser_id()+"-"+ticketBookingDetails.data.booking_details.booking_no;
            Bitmap myBitmap = QRCode.from(QRCODE).withSize(512,512).withColor(getResources().getColor(R.color.QRCodeBlackColor),getResources().getColor(R.color.QRCodeWhiteColor)).bitmap();
            qrImageView.setImageBitmap(myBitmap);
            verifiedText.setVisibility(View.GONE);
        }else{
            String QRCODE = loginSession.getUser_id()+"-"+ticketBookingDetails.data.booking_details.booking_no;
            Bitmap myBitmap = QRCode.from(QRCODE).withSize(512,512).withColor(getResources().getColor(R.color.dull),getResources().getColor(R.color.QRCodeWhiteColor)).bitmap();
            qrImageView.setImageBitmap(myBitmap);
            verifiedText.setVisibility(View.VISIBLE);
        }

        //Event Image show
        if(ticketBookingDetails.data.booking_details.event_image!=null) {
            if (ticketBookingDetails.data.booking_details.event_image.isEmpty() || !ticketBookingDetails.data.booking_details.event_image.equals("")) {
                eventErrorImage.setVisibility(View.GONE);
                eventImageView.setVisibility(View.VISIBLE);
                Picasso.with(this).load(ticketBookingDetails.data.booking_details.event_image).into(eventImageView);
            } else {
                eventErrorImage.setVisibility(View.VISIBLE);
                eventImageView.setVisibility(View.GONE);
            }
        }

        //Event Date show
        String createDate = "";
        try {
            String date = ticketBookingDetails.data.booking_details.booking_date;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+00:00'", Locale.ENGLISH);
            SimpleDateFormat sdf2 = new SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.ENGLISH);
            Date d = sdf.parse(date);
            Log.e("dateintial", d.toString());
            Log.e("date", sdf2.format(d));
            createDate = d.toString();
            String splitShowDate[] = createDate.split("\\s");
            createDate = splitShowDate[0]+", "+splitShowDate[1]+" "+splitShowDate[2]+", "+splitShowDate[5];
        } catch (Exception e) {
            e.printStackTrace();
        }
        eventDateTextView.setText(createDate);

        //Event Time show
        String createTime = "";
        try {
            String date = ticketBookingDetails.data.booking_details.event_time;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+00:00'", Locale.ENGLISH);
            SimpleDateFormat sdf2 = new SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.ENGLISH);
            Date d = sdf.parse(date);
            Log.e("dateintial", d.toString());
            Log.e("date", sdf2.format(d));
            createTime = sdf2.format(d).toString();
            String splitShowTime[] = createTime.split("\\s");
            createTime = splitShowTime[3]+" "+splitShowTime[4].replace(".","");

        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e("createTime",createTime);
        eventTimeTextView.setText(createTime);

        eventNameTextView.setText(ticketBookingDetails.data.booking_details.event_name);
        eventPlaceNameTextView.setText(ticketBookingDetails.data.booking_details.event_place);
        eventAddressNameTextView.setText(ticketBookingDetails.data.booking_details.event_address);

        if(Double.parseDouble(ticketBookingDetails.data.booking_details.service_tax_amount) > 0){
            serviceFeeTextView.setText(loginSession.getShowCurrency()+" "+changeCustomerCurrency(ticketBookingDetails.data.booking_details.service_tax_amount,ticketBookingDetails.data.booking_details.transaction.sender_currency_difference));
            serviceFeeTextView.setVisibility(View.VISIBLE);
            serviceFee.setVisibility(View.VISIBLE);
        }else{
            serviceFeeTextView.setVisibility(View.GONE);
            serviceFee.setVisibility(View.GONE);
        }


        if(Double.parseDouble(ticketBookingDetails.data.booking_details.transaction.poppay_fee) > 0){
            rpayFeeTextView.setText(loginSession.getShowCurrency()+" "+changeCustomerCurrency(ticketBookingDetails.data.booking_details.transaction.poppay_fee,ticketBookingDetails.data.booking_details.transaction.sender_currency_difference));
            rpayFeeTextView.setVisibility(View.VISIBLE);
            rpayFee.setVisibility(View.VISIBLE);
        }else{
            rpayFeeTextView.setVisibility(View.GONE);
            rpayFee.setVisibility(View.GONE);
        }

        if(Double.parseDouble(ticketBookingDetails.data.booking_details.sub_total) > 0){
            grandTotalTextView.setText(loginSession.getShowCurrency()+" "+changeCustomerCurrency(ticketBookingDetails.data.booking_details.sub_total,ticketBookingDetails.data.booking_details.transaction.sender_currency_difference));
        }else{
            grandTotalTextView.setText(ticketBookingDetails.data.booking_details.total_coin+" Rewards");
        }

        Double fee = Double.parseDouble(ticketBookingDetails.data.booking_details.transaction.poppay_fee);
        Double sfee = Double.parseDouble(ticketBookingDetails.data.booking_details.grand_total);
        Double gt = fee+sfee;
        gFeeTextView.setText(loginSession.getShowCurrency()+" "+changeCustomerCurrency(String.valueOf(gt),ticketBookingDetails.data.booking_details.transaction.sender_currency_difference));


        bookingIdTextView.setText(ticketBookingDetails.data.booking_details.booking_no);

        ArrayList<TicketBookingDetails.Booking_carts>booking_cartses = ticketBookingDetails.data.booking_details.booking_carts;
        ticketAdapter = new TicketAdapter(this, booking_cartses);
        ticketListView.setAdapter(ticketAdapter);
        ticketAdapter.notifyDataSetChanged();
        utility.getTotalHeightofListView(ticketListView);

    }

    @Override
    public void onFailure(String error, RequestID requestID) {

        hideProgressDialog();
        toast(error);

    }

    private class TicketAdapter extends BaseAdapter{

        Activity activity;
        ArrayList<TicketBookingDetails.Booking_carts> bookingCartses;
        private LayoutInflater inflater;

        public TicketAdapter(Activity activity, ArrayList<TicketBookingDetails.Booking_carts> booking_cartses) {
            this.activity = activity;
            this.bookingCartses = booking_cartses;
            this.inflater = LayoutInflater.from(activity);
        }

        @Override
        public int getCount() {
            return bookingCartses.size();
        }

        @Override
        public Object getItem(int i) {
            return bookingCartses.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            inflater = activity.getLayoutInflater();

            if (view == null)
                view = inflater.inflate(R.layout.booked_ticketlist, null);

            TextView ticketNameTextView = (TextView)view.findViewById(R.id.ticketNameTextView);
            TextView ticketCountTextView = (TextView)view.findViewById(R.id.ticketCountTextView);


            ticketNameTextView.setText(bookingCartses.get(i).ticket_title);
            ticketCountTextView.setText(bookingCartses.get(i).ticket_quantity);

            return view;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Utility.update_check = false;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utility.update_check = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        Utility.update_check = true;
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(Config.PUSH_NOTIFICATION));

    }

}
