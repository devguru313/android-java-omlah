package com.omlah.customer.tabevent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.omlah.customer.R;
import com.omlah.customer.base.BaseActivity;
import com.omlah.customer.base.Utility;
import com.omlah.customer.common.LoginSession;
import com.omlah.customer.database.DatabaseManager;
import com.omlah.customer.model.CartDetails;
import com.omlah.customer.model.TicketSuccess;
import com.omlah.customer.otp.PassCodeVerifiedScreen;
import com.omlah.customer.service.RequestID;
import com.omlah.customer.service.ServerListener;
import com.omlah.customer.service.ServerRequestwithHeader;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 16-10-2017.
 */

public class ReviewBookingScreen extends BaseActivity implements ServerListener{

    //Create class objects
    Cursor cursor;
    Utility utility;
    LoginSession loginSession;
    DatabaseManager databaseManager;
    TicketAdapetr ticketAdapetr;
    ServerRequestwithHeader createRequest;

    //Create xml file
    @BindView(R.id.payButton)Button payButton;
    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.backIconImageView)ImageView backIconImageView;
    @BindView(R.id.currentBalanceTextView)TextView currentBalanceTextView;
    @BindView(R.id.eventNameTextView)TextView eventNameTextView;
    @BindView(R.id.eventImageView)ImageView eventImageView;
    @BindView(R.id.eventErrorImage)RelativeLayout eventErrorImage;
    @BindView(R.id.eventPlaceNameTextView)TextView eventPlaceNameTextView;
    @BindView(R.id.locationTextView)TextView locationTextView;
    @BindView(R.id.eventDateAndTimeTextView)TextView eventDateAndTimeTextView;
    @BindView(R.id.subtotalTextView)TextView subtotalTextView;
    @BindView(R.id.serviceTaxLayout)RelativeLayout serviceTaxLayout;
    @BindView(R.id.serviceTaxTextView)TextView serviceTaxTextView;
    @BindView(R.id.popPayFeeLayout)RelativeLayout popPayFeeLayout;
    @BindView(R.id.popPayFeeTextView)TextView popPayFeeTextView;
    @BindView(R.id.popPayFeeLayoutSecond)RelativeLayout popPayFeeLayoutSecond;
    @BindView(R.id.popPayFeeTextViewSecond)TextView popPayFeeTextViewSecond;
    @BindView(R.id.totalPayableTextView)TextView totalPayableTextView;
    @BindView(R.id.userPhoneNumberTextView)TextView userPhoneNumberTextView;
    @BindView(R.id.userEmailTextView)TextView userEmailTextView;
    @BindView(R.id.bookedTicketListView)ListView bookedTicketListView;
    @BindView(R.id.additionalFeeRecyclerView)RecyclerView additionalFeeRecyclerView;

    String  TICKET_TYPE = "",
            eventId = "",
            eventImage = "",
            eventName = "",
            eventPlaceName = "",
            eventCurrencyCode = "",
            eventCurrencySymbol = "",
            eventAddress = "",
            bookingDate = "",
            eventTime = "",
            serviceTax = "",
            serviceTaxAmount = "",
            popPayfeeAmount = "",
            grandTotal="",
            conversionAmount="";

    double conversionPrice=0;

    String showDate="",showTime="";
    double SHOWTOTAL=0;

    ArrayList<CartDetails> getDbCartdetails = new ArrayList<CartDetails>();
    List<String> totalQuantityList = new ArrayList<String>();
    ArrayList<String>additioanlFees = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_booking_screen);
        hideActionBar();

        ButterKnife.bind(this);

        databaseManager = DatabaseManager.getInstance(this);
        utility = Utility.getInstance(this);
        loginSession = LoginSession.getInstance(this);
        createRequest = ServerRequestwithHeader.getInstance(this);

        //Get Intent values
        final Intent intent = getIntent();
        if(intent!=null){

            TICKET_TYPE       = intent.getStringExtra("TICKET_TYPE");
            eventId           = intent.getStringExtra("eventId");
            eventImage        = intent.getStringExtra("eventImage");
            eventName         = intent.getStringExtra("eventName");
            eventCurrencyCode = intent.getStringExtra("eventCurrencyCode");
            eventCurrencySymbol = intent.getStringExtra("eventCurrencySymbol");
            eventPlaceName    = intent.getStringExtra("eventPlaceName");
            eventAddress      = intent.getStringExtra("eventAddress");
            bookingDate       = intent.getStringExtra("bookingDate");
            eventTime         = intent.getStringExtra("eventTime");
            serviceTax        = intent.getStringExtra("serviceTax");
            popPayfeeAmount   = intent.getStringExtra("popPayfeeAmount");
            conversionAmount   = intent.getStringExtra("conversionAmount");
            additioanlFees   = (ArrayList<String>) getIntent().getSerializableExtra("additionalFee");

            conversionPrice = Double.parseDouble(conversionAmount);

            if(TICKET_TYPE.equalsIgnoreCase("PRICE")){
                currentBalanceTextView.setText(loginSession.getShowCurrency()+" "+String.format(Locale.ENGLISH,"%.2f",Double.parseDouble(loginSession.getbalanceAmount())));
            }else{
                currentBalanceTextView.setText(loginSession.getPopCoin()+" Rewards");
            }

            //Event Image show
            if(eventImage!=null) {
                if (eventImage.isEmpty() || !eventImage.equals("")) {
                    eventErrorImage.setVisibility(View.GONE);
                    eventImageView.setVisibility(View.VISIBLE);

                    Log.e("eventImage",eventImage);

                    Picasso.with(this).load(eventImage).into(eventImageView);
                } else {
                    eventErrorImage.setVisibility(View.VISIBLE);
                    eventImageView.setVisibility(View.GONE);
                }
            }

            //Event Name
            eventNameTextView.setText(eventName);
            eventPlaceNameTextView.setText(eventPlaceName);
            locationTextView.setText(eventAddress);

            //Event Date show
            String createDate = "";
            try {
                String date = bookingDate;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+00:00'", Locale.ENGLISH);
                SimpleDateFormat sdf2 = new SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.ENGLISH);
                Date d = sdf.parse(date);
                Log.e("dateintial", d.toString());
                Log.e("date", sdf2.format(d));
                createDate = d.toString();
                String splitShowDate[] = createDate.split("\\s");
                createDate = splitShowDate[0]+" "+splitShowDate[1]+" "+splitShowDate[2];
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Set showDate
            showDate = createDate;

            //Booking date pass
            try {
                String date[] = bookingDate.split("T");
                bookingDate = date[0];
                Log.e("bookingDate",bookingDate);

            }catch (Exception e){}


            //EventTime
            String EVENTTIME = "";
            String createTime ="";

            try {
                String date = eventTime;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+00:00'", Locale.ENGLISH);
                SimpleDateFormat sdf2 = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.ENGLISH);
                SimpleDateFormat sdf3 = new SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.ENGLISH);
                Date d = sdf.parse(date);
                Log.e("time1", d.toString());
                Log.e("time2", sdf2.format(d));
                Log.e("time3", sdf3.format(d));

                String splitTime[] = sdf2.format(d).toString().split("\\s");
                String splitcreateTime[] = sdf3.format(d).toString().split("\\s");

                createTime = splitcreateTime[3]+splitcreateTime[4];
                EVENTTIME = splitTime[3];

            } catch (Exception e) {
                e.printStackTrace();
            }

            showTime = createTime;
            eventTime = EVENTTIME;
            Log.e("eventTime",eventTime);


            eventDateAndTimeTextView.setText(showDate+" "+showTime);

            //get Database Values
            try {

                cursor = databaseManager.getAll();
                if (cursor.moveToFirst()) {
                    do {

                        CartDetails cartDetails = new CartDetails();
                        cartDetails.setTicketID(cursor.getString(1));
                        cartDetails.setTicketTitle(cursor.getString(2));
                        cartDetails.setTicketPrice(cursor.getString(3));
                        cartDetails.setTicketDes(cursor.getString(4));
                        cartDetails.setTicketQuantity(cursor.getString(5));
                        cartDetails.setTotalPrice(cursor.getString(6));
                        totalQuantityList.add(cursor.getString(5));
                        getDbCartdetails.add(cartDetails);

                    } while (cursor.moveToNext());
                }
                cursor.close();

                ticketAdapetr = new TicketAdapetr(this, getDbCartdetails);
                bookedTicketListView.setAdapter(ticketAdapetr);
                ticketAdapetr.notifyDataSetChanged();
                utility.getTotalHeightofListView(bookedTicketListView);

            } catch (RuntimeException e) {

            }


            if(additioanlFees.size() > 0){

                Log.e("additioanlFees",""+additioanlFees.toString());

                additionalFeeRecyclerView.setVisibility(View.VISIBLE);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
                additionalFeeRecyclerView.setLayoutManager(linearLayoutManager);
                additionalFeeRecyclerView.setItemAnimator(new DefaultItemAnimator());
                additionalFeeRecyclerView.setHasFixedSize(true);
                AdditionalFeeListAdapter copiedCodeListAdapter = new AdditionalFeeListAdapter(this,additioanlFees);
                additionalFeeRecyclerView.setAdapter(copiedCodeListAdapter);
                copiedCodeListAdapter.notifyDataSetChanged();

            }else{
                additionalFeeRecyclerView.setVisibility(View.GONE);
            }

            //Set subtotal
            double subtotal_actualPrice    = Double.parseDouble(databaseManager.getSubTotal());
            double subtotal_convertedPrice = subtotal_actualPrice*conversionPrice;

            if(TICKET_TYPE.equalsIgnoreCase("PRICE")){
                subtotalTextView.setText(loginSession.getShowCurrency()+" "+String.format(Locale.ENGLISH,"%.2f", subtotal_convertedPrice));
            }else{
                subtotalTextView.setText(databaseManager.getSubTotal().replace(".00","")+" Rewards");
            }

            //set service tax
            double subtotal = subtotal_convertedPrice;
            double tax = Double.parseDouble(serviceTax);
            double tax_actualPrice    = tax;
            double tax_convertedPrice = tax_actualPrice;
            double calculatedTax = subtotal * (tax_convertedPrice / 100);
            if(TICKET_TYPE.equalsIgnoreCase("PRICE")) {

                if(calculatedTax > 0){
                    serviceTaxLayout.setVisibility(View.VISIBLE);
                }else{
                    serviceTaxLayout.setVisibility(View.GONE);
                }

                serviceTaxTextView.setText(loginSession.getShowCurrency()+" "+ String.format(Locale.ENGLISH,"%.2f", calculatedTax));
            }else{
                serviceTaxLayout.setVisibility(View.GONE);
            }


            //SetPoppay Fee
            double totalQuantity=0;
            for (int i = 0; i < totalQuantityList.size(); i++) {
                double ticketQuantity = Double.parseDouble(totalQuantityList.get(i));
                totalQuantity += ticketQuantity;
            }
            double popPayFee = Double.parseDouble(popPayfeeAmount);
            double calculatedPopPayFee = subtotal * popPayFee / 100;
            Log.e("calculatedPopPayFee",""+calculatedPopPayFee);
            if (TICKET_TYPE.equalsIgnoreCase("PRICE")) {
                if (calculatedPopPayFee > 0) {

                    popPayFeeLayoutSecond.setVisibility(View.GONE);
                    popPayFeeLayout.setVisibility(View.VISIBLE);
                    popPayFeeTextView.setText(loginSession.getShowCurrency()+" "+ String.format(Locale.ENGLISH,"%.2f", calculatedPopPayFee));

                } else {

                    popPayFeeLayout.setVisibility(View.GONE);
                    popPayFeeLayoutSecond.setVisibility(View.GONE);
                }
            }else{
                popPayFeeLayout.setVisibility(View.GONE);
                if (calculatedPopPayFee > 0) {

                    popPayFeeLayoutSecond.setVisibility(View.VISIBLE);
                    popPayFeeTextViewSecond.setText(loginSession.getShowCurrency()+" "+String.format(Locale.ENGLISH,"%.2f", calculatedPopPayFee));

                }else{

                    popPayFeeLayoutSecond.setVisibility(View.GONE);

                }
            }

            double additionFEE = 0;
            if(additioanlFees.size() > 0){
                for(String s : additioanlFees){
                    String amount[] = s.split("<@>");
                    additionFEE =+Double.parseDouble(amount[1]);
                }
            }

            //Set granttotal without poppay fee
            SHOWTOTAL = subtotal+calculatedTax+calculatedPopPayFee+additionFEE;
            if(TICKET_TYPE.equalsIgnoreCase("PRICE")){
                totalPayableTextView.setText(loginSession.getShowCurrency()+" "+String.format(Locale.ENGLISH,"%.2f", SHOWTOTAL));
            }else{
                totalPayableTextView.setText(databaseManager.getSubTotal().replace(".00","")+" Rewards");
            }

            userEmailTextView.setText(loginSession.getemail());

            if(loginSession.getCustomerCountryCode().equalsIgnoreCase("+251")){
                userPhoneNumberTextView.setText(normalizePhoneNumber(loginSession.getphoneNumber(),loginSession.getCustomerCountryCode().replace("+","")));
            }else{
                userPhoneNumberTextView.setText(normalizePhoneNumber(loginSession.getphoneNumber(),loginSession.getCustomerCountryCode()).replace("+",""));
            }

            if(TICKET_TYPE.equalsIgnoreCase("PRICE")) {
                payButton.setText("PAY " + loginSession.getShowCurrency()+" "+ String.format(Locale.ENGLISH,"%.2f", SHOWTOTAL));
            }else{
                payButton.setText("PAY " +databaseManager.getSubTotal().replace(".00","")+" Rewards");
            }
            //Pass vlues to server
            double Original_subTotal = Double.parseDouble(databaseManager.getSubTotal());
            double Original_tax = Double.parseDouble(serviceTax);
            double Original_calculatedTax = Original_subTotal * (Original_tax / 100);
            double Original_grandTotal = Original_subTotal+Original_calculatedTax;
            grandTotal = String.format(Locale.ENGLISH,"%.2f", Original_grandTotal);
            serviceTaxAmount = String.format(Locale.ENGLISH,"%.2f", Original_calculatedTax);

        }

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TICKET_TYPE.equalsIgnoreCase("PRICE")) {

                    double walletBalance   = Double.parseDouble(loginSession.getbalanceAmount());

                    Log.e("currentTotal",""+SHOWTOTAL);
                    Log.e("walletBalance",""+walletBalance);

                    if(SHOWTOTAL < walletBalance){

                        Intent passCodeIntent = new Intent(ReviewBookingScreen.this, PassCodeVerifiedScreen.class);
                        startActivityForResult(passCodeIntent,4);

                    }else{

                        toast(getResources().getString(R.string.Insufficientwalletbalance));
                    }

                }else{

                    double currentCoin    = Double.parseDouble(databaseManager.getSubTotal().replace(".00", ""));
                    double walletCoin     = Double.parseDouble(loginSession.getPopCoin());

                    if(currentCoin < walletCoin){

                        Intent passCodeIntent = new Intent(ReviewBookingScreen.this, PassCodeVerifiedScreen.class);
                        startActivityForResult(passCodeIntent,4);

                    }else{

                        toast(getResources().getString(R.string.InsufficientRewards));
                    }

                }


            }
        });

        backIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

    }

    @Override
    public void onSuccess(Object result, RequestID requestID) {

        hideProgressDialog();
        TicketSuccess ticketSuccess = (TicketSuccess)result;
        if(ticketSuccess.message.equalsIgnoreCase("No sufficient balance")){
            toast(ticketSuccess.message);
        }else{
            Intent intent = new Intent(ReviewBookingScreen.this,TicketSuccessScreen.class);
            intent.putExtra("bookingID",ticketSuccess.booking_id);
            intent.putExtra("Screen","eventplaced");
            intent.putExtra("Status","0");
            startActivity(intent);
        }

    }

    @Override
    public void onFailure(String error, RequestID requestID) {

        hideProgressDialog();
        toast(error);
    }

    private class TicketAdapetr extends BaseAdapter{

        Activity activity;
        ArrayList<CartDetails>cartDetailses;
        private LayoutInflater inflater;

        public TicketAdapetr(Activity activity, ArrayList<CartDetails> cartDetailses) {
            this.activity = activity;
            this.cartDetailses = cartDetailses;
            this.inflater = LayoutInflater.from(activity);
        }

        @Override
        public int getCount() {
            return cartDetailses.size();
        }

        @Override
        public Object getItem(int i) {
            return cartDetailses.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            inflater = activity.getLayoutInflater();

            if (view == null)
                view = inflater.inflate(R.layout.booked_ticketlist_costom, null);


            TextView ticketNameTextView = (TextView)view.findViewById(R.id.ticketNameTextView);
            TextView ticketQuantityTextView = (TextView)view.findViewById(R.id.ticketQuantityTextView);
            TextView amountTextView = (TextView)view.findViewById(R.id.amountTextView);

            ticketNameTextView.setText(cartDetailses.get(i).getTicketTitle());

            double Ticket_actualPrice    = Double.parseDouble(cartDetailses.get(i).getTicketPrice());
            double Ticket_convertedPrice = Ticket_actualPrice*conversionPrice;

            if(TICKET_TYPE.equalsIgnoreCase("PRICE")) {
                ticketQuantityTextView.setText(cartDetailses.get(i).getTicketQuantity() + "*" + String.format(Locale.ENGLISH,"%.2f",Ticket_convertedPrice));
            }else{
                ticketQuantityTextView.setText(cartDetailses.get(i).getTicketQuantity() + "*" + String.valueOf(Ticket_convertedPrice).replace(".0",""));

            }

            //Set total
            double actualPrice    = Double.parseDouble(cartDetailses.get(i).getTotalPrice());
            double convertedPrice = actualPrice*conversionPrice;

            if(TICKET_TYPE.equalsIgnoreCase("PRICE")){
                amountTextView.setText(loginSession.getShowCurrency()+" "+String.format(Locale.ENGLISH,"%.2f",convertedPrice));
            }else{
                amountTextView.setText(cartDetailses.get(i).getTotalPrice().replace(".0","")+" Rewards");
            }

            return view;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 4) {
            if (resultCode == 4) {

                if (isConnectingToInternet()) {
                    Map<String, String> params = new HashMap<>();
                    params.put("event_id", eventId);
                    params.put("event_time", eventTime);
                    params.put("booking_date", bookingDate);
                    if (TICKET_TYPE.equalsIgnoreCase("PRICE")) {
                        params.put("sub_total", databaseManager.getSubTotal());
                        params.put("service_tax", serviceTax);
                        params.put("service_tax_amount", serviceTaxAmount);
                        params.put("grand_total", grandTotal);
                        params.put("total_coin", "0");
                        params.put("tickets", "" + databaseManager.getCart());
                    } else {
                        params.put("sub_total", "0");
                        params.put("service_tax", "0");
                        params.put("service_tax_amount", "0");
                        params.put("grand_total", "0");
                        params.put("total_coin", databaseManager.getSubTotal().replace(".00", ""));
                        params.put("tickets", "" + databaseManager.getCart());
                    }

                    Log.e("parms", "" + params);
                    showProgressDialog();
                    createRequest.createRequest(ReviewBookingScreen.this, params, RequestID.REQ_EVENT_BOOKING, "POST", "");

                } else {
                    noInternetAlertDialog();
                }

            }
        }
    }

    private class AdditionalFeeListAdapter extends RecyclerView.Adapter<AdditionalFeeListAdapter.MyViewHolder>{

        private Context context;
        private ArrayList<String> additioanlFees;

        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView additionalFeeText,additionalFeeTextView;

            public MyViewHolder(View view) {
                super(view);
                additionalFeeText = view.findViewById(R.id.additionalFeeText);
                additionalFeeTextView = view.findViewById(R.id.additionalFeeTextView);
            }
        }

        public AdditionalFeeListAdapter(Context contextt, ArrayList<String> additioanlFees) {
            this.context = contextt;
            this.additioanlFees = additioanlFees;

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.additinalfee_list, parent, false);


            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {

            String splitValue[] = additioanlFees.get(position).split("<@>");
            holder.additionalFeeText.setText(splitValue[0]);
            holder.additionalFeeTextView.setText(loginSession.getShowCurrency()+" "+String.format("%.2f",Double.parseDouble(splitValue[1])));
        }

        @Override
        public long getItemId(int id) {
            return id;
        }

        @Override
        public int getItemCount() {
            return additioanlFees.size();
        }

    }
}
