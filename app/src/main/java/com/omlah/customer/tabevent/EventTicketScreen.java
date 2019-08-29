package com.omlah.customer.tabevent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.omlah.customer.R;
import com.omlah.customer.base.BaseActivity;
import com.omlah.customer.base.Utility;
import com.omlah.customer.common.LoginSession;
import com.omlah.customer.database.DatabaseManager;
import com.omlah.customer.model.CartDetails;
import com.omlah.customer.model.EventModelDetails;
import com.omlah.customer.service.RequestID;
import com.omlah.customer.service.ServerListener;
import com.omlah.customer.service.ServerRequestwithHeader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 16-10-2017.
 */

public class EventTicketScreen extends BaseActivity implements ServerListener{

    //Create class objects
    Utility utility;
    LoginSession loginSession;
    DatabaseManager databaseManager;
    ServerRequestwithHeader serverRequestwithHeader;

    //Create xml objects
    @BindView(R.id.eventPlaceNameTextView)TextView eventPlaceNameTextView;
    @BindView(R.id.eventAddressTextView)TextView eventAddressTextView;
    @BindView(R.id.ticketListView)RecyclerView ticketListView;
    @BindView(R.id.payButton)Button payButton;
    @BindView(R.id.dateRecylerView)RecyclerView dateRecylerView;
    @BindView(R.id.timeRecylerView)RecyclerView timeRecylerView;
    @BindView(R.id.timeLayout)RelativeLayout timeLayout;

    String  eventId="",
            eventImage="",
            eventCurrencyCode="",
            eventCurrencySymbol="",
            eventName="",
            eventPlaceName="",
            eventAddress="",
            bookingDate="",
            eventTime="",
            serviceTax="",
            popPayfeeAmount="",
            conversionAmount="";

    String TICKET_TYPE = "";

    private Integer time_selected_position = -1;
    private Integer date_selected_position = -1;

    ArrayList<String>additioanlFeesList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_payment_screen);
        showBackArrow();
        //Initialize xml objects
        ButterKnife.bind(this);

        //Initialize class objects
        loginSession = LoginSession.getInstance(this);
        utility = Utility.getInstance(this);
        serverRequestwithHeader = ServerRequestwithHeader.getInstance(this);
        databaseManager = DatabaseManager.getInstance(this);

        //Get Intent value
        final Intent intent = getIntent();
        if(intent!=null){

            eventId = intent.getStringExtra("EventID");
            if(isConnectingToInternet()){
                Map<String, String> params = new HashMap<>();
                showProgressDialog();
                serverRequestwithHeader.createRequest(EventTicketScreen.this,params, RequestID.REQ_EVENT_DETAILS,"GET",eventId);
            }else{
                noInternetAlertDialog();
            }
        }

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(bookingDate.isEmpty()){

                    toast(getResources().getString(R.string.pleaseselectadate));

                }else if(eventTime.isEmpty()){

                    toast(getResources().getString(R.string.pleaseselectatime));

                }else{

                    if(isConnectingToInternet()){

                        final Map<String, String> param = new HashMap<String, String>();
                        param.put("amount","1");
                        param.put("from",eventCurrencyCode);
                        param.put("to",loginSession.getcurrencyCodee());
                        showProgressDialog();
                        serverRequestwithHeader.createRequest(EventTicketScreen.this,param,RequestID.REQ_CURRENCY_CONVERSION,"POST","");

                    }else{

                        noInternetAlertDialog();
                    }

                }


            }
        });

    }

    @Override
    public void onSuccess(Object result, RequestID requestID) {

        hideProgressDialog();

        switch (requestID){

            case REQ_CURRENCY_CONVERSION:

                conversionAmount = result.toString();

                Intent intent1 = new Intent(EventTicketScreen.this,ReviewBookingScreen.class);
                intent1.putExtra("TICKET_TYPE",TICKET_TYPE);
                intent1.putExtra("eventId",eventId);
                intent1.putExtra("eventImage",eventImage);
                intent1.putExtra("eventName",eventName);
                intent1.putExtra("eventCurrencyCode",eventCurrencyCode);
                intent1.putExtra("eventCurrencySymbol",eventCurrencySymbol);
                intent1.putExtra("eventPlaceName",eventPlaceName);
                intent1.putExtra("eventAddress",eventAddress);
                intent1.putExtra("bookingDate",bookingDate);
                intent1.putExtra("eventTime",eventTime);
                intent1.putExtra("serviceTax",serviceTax);
                intent1.putExtra("popPayfeeAmount",popPayfeeAmount);
                intent1.putExtra("conversionAmount",conversionAmount);
                intent1.putExtra("additionalFee",additioanlFeesList);
                startActivity(intent1);

                break;

            case REQ_EVENT_DETAILS:

                EventModelDetails eventModelDetails = (EventModelDetails)result;

                eventId    = eventModelDetails.data.events.id;
                eventImage    = eventModelDetails.data.events.event_image;
                eventName = eventModelDetails.data.events.event_name;
                setActionBarTitle(eventName);
                eventPlaceName = eventModelDetails.data.events.event_place;
                eventAddress = eventModelDetails.data.events.event_address;
                serviceTax = eventModelDetails.data.events.service_tax;

                eventCurrencyCode = eventModelDetails.data.events.user.country.currency_code;
                eventCurrencySymbol = eventModelDetails.data.events.user.country.currency_symbol;

                if(eventModelDetails.data.events.poppay_fee_apply.equalsIgnoreCase("NO")){
                    popPayfeeAmount = "0";
                }else{
                    if(eventModelDetails.data.customer.country.poppay_fee.fee_amount!=null && Double.parseDouble(eventModelDetails.data.customer.country.poppay_fee.fee_amount) > 0){
                        popPayfeeAmount = eventModelDetails.data.customer.country.poppay_fee.fee_amount ;
                    }else{
                        popPayfeeAmount = "0";
                    }
                }

                for (EventModelDetails.AdditioanlFees additioanlFees : eventModelDetails.data.events.additioanlFees){
                    additioanlFeesList.add(additioanlFees.fee_name+"<@>"+changeCustomerCurrency(additioanlFees.fees,eventModelDetails.data.events.sender_currency_difference));
                }

                eventPlaceNameTextView.setText(eventModelDetails.data.events.event_place);
                eventAddressTextView.setText(eventModelDetails.data.events.event_address);

                //Event date
                ArrayList<EventModelDetails.Event_ticket_dates> event_ticket_dates = eventModelDetails.data.events.event_ticket_dates;

                if(event_ticket_dates.size() > 0){
                    date_selected_position = 0;
                    bookingDate = eventModelDetails.data.events.event_ticket_dates.get(0).event_date;
                }
                dateRecylerView.setHasFixedSize(true);
                EventDateAdapter adapter = new EventDateAdapter(this, event_ticket_dates);
                dateRecylerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                dateRecylerView.setAdapter(adapter);

                //SetEvent Time
                for(EventModelDetails.Event_ticket_dates event_ticket_dates1 : event_ticket_dates){

                    ArrayList<EventModelDetails.Event_times> event_times = event_ticket_dates1.event_times;
                    if(event_times.size() > 0){
                        time_selected_position = 0;
                        eventTime = event_ticket_dates1.event_times.get(0).event_time;
                    }
                    timeLayout.setVisibility(View.VISIBLE);
                    //Set Time Adapterclass
                    timeRecylerView.setHasFixedSize(true);
                    EventTimeAdapter adapterTime = new EventTimeAdapter(this, event_times);
                    timeRecylerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                    timeRecylerView.setAdapter(adapterTime);
                    break;

                }

                databaseManager.clearTable();
                ArrayList<EventModelDetails.Event_ticket_details> event_ticket_detailses = event_ticket_dates.get(0).event_ticket_details;
                ticketListView.setHasFixedSize(true);
                EventTicketDetailsAdapter eventTicketDetailsAdapter = new EventTicketDetailsAdapter(this, event_ticket_detailses);
                ticketListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                ticketListView.setAdapter(eventTicketDetailsAdapter);
                ticketListView.setVisibility(View.VISIBLE);

                break;
        }


    }

    @Override
    public void onFailure(String error, RequestID requestID) {

        hideProgressDialog();
        toast(error);
    }

    private class EventDateAdapter extends RecyclerView.Adapter<EventDateAdapter.ItemRowHolder>{

        Activity activity;
        ArrayList<EventModelDetails.Event_ticket_dates> eventDateList;


        public EventDateAdapter(Activity activity, ArrayList<EventModelDetails.Event_ticket_dates> eventDateList) {
            this.activity = activity;
            this.eventDateList = eventDateList;
        }

        @Override
        public EventDateAdapter.ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_date_custom, null);
            ItemRowHolder mh = new ItemRowHolder(v);
            return mh;
        }

        @SuppressLint("NewApi")
        @Override
        public void onBindViewHolder(final EventDateAdapter.ItemRowHolder holder, final int position) {

            String splitDate="";

            Log.e("dateee",eventDateList.get(position).event_date);

            try {
                String date = eventDateList.get(position).event_date;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+00:00'", Locale.ENGLISH);
                SimpleDateFormat sdf2 = new SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.ENGLISH);
                Date d = sdf.parse(date);
                Log.e("dateintial", d.toString());
                Log.e("date", sdf2.format(d));
                splitDate = d.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Event Date showing
            String splitedDate[] = splitDate.split("\\s");

            String dd = splitedDate[1]+"\n";
            String dd1 = splitedDate[2]+"\n";
            String dd2 = splitedDate[0];
            //String str1="<big>"+dd1+"</big>";
            holder.eventDateTextView.setText(dd+dd1+dd2);

            //Fonts
            Typeface font= Typeface.createFromAsset(activity.getAssets(), "font/GothamRounded-Book.otf");
            holder.eventDateTextView.setTypeface(font);

            holder.eventDateTextView.setOnCheckedChangeListener(null);

            holder.eventDateTextView.setTag(position);


            if (position == date_selected_position) {

                holder.eventDateTextView.setChecked(true);

            } else {

                holder.eventDateTextView.setChecked(false);

            }

            holder.eventDateTextView.setOnCheckedChangeListener(new CheckListener(holder.eventDateTextView, position));


        }

        @Override
        public int getItemCount() {
            return (null != eventDateList ? eventDateList.size() : 0);
        }

        public class ItemRowHolder extends RecyclerView.ViewHolder {

            private RadioButton eventDateTextView;

            public ItemRowHolder(View v) {
                super(v);
                this.eventDateTextView = (RadioButton) v.findViewById(R.id.eventDateTextView);

            }
        }

        public class CheckListener implements CompoundButton.OnCheckedChangeListener {

            RadioButton cusinelist3;
            int position;

            public CheckListener(RadioButton cusinelist, int position) {


                Log.e("received position",""+position);

                this.cusinelist3 = cusinelist;
                this.position = position;

            }

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    cusinelist3.setChecked(true);
                    date_selected_position = position;

                    Log.e("selected_position",""+position);

                    bookingDate = eventDateList.get(position).event_date;
                    EventDateAdapter.this.notifyDataSetChanged();

                    ArrayList<EventModelDetails.Event_times> event_times = eventDateList.get(position).event_times;
                    timeLayout.setVisibility(View.VISIBLE);
                    //Set Time Adapterclass
                    timeRecylerView.setHasFixedSize(true);
                    EventTimeAdapter adapter = new EventTimeAdapter(activity, event_times);
                    timeRecylerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
                    timeRecylerView.setAdapter(adapter);

                    //Set ticket adapter
                    databaseManager.clearTable();
                    eventTime="";
                    ArrayList<EventModelDetails.Event_ticket_details> event_ticket_detailses = eventDateList.get(position).event_ticket_details;
                    ticketListView.setHasFixedSize(true);
                    EventTicketDetailsAdapter eventTicketDetailsAdapter = new EventTicketDetailsAdapter(activity,event_ticket_detailses);
                    ticketListView.setLayoutManager(new LinearLayoutManager(EventTicketScreen.this, LinearLayoutManager.VERTICAL, false));
                    ticketListView.setAdapter(eventTicketDetailsAdapter);


                    if(Double.parseDouble(databaseManager.getSubTotal()) > 0){
                        payButton.setVisibility(View.VISIBLE);
                    }else{

                        payButton.setVisibility(View.GONE);
                    }

                } else {
                    cusinelist3.setChecked(false);

                }
                compoundButton.setChecked(b);

            }
        }
    }

    private class EventTimeAdapter extends RecyclerView.Adapter<EventTimeAdapter.ItemRowHolder> {

        Activity activity;
        ArrayList<EventModelDetails.Event_times>event_times;


        public EventTimeAdapter(Activity activity, ArrayList<EventModelDetails.Event_times> event_times) {
            this.activity = activity;
            this.event_times = event_times;
        }

        @Override
        public EventTimeAdapter.ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_time_custom, null);
            ItemRowHolder mh = new ItemRowHolder(v);
            return mh;
        }

        @Override
        public void onBindViewHolder(EventTimeAdapter.ItemRowHolder holder, int position) {

            String splitDate="";

            try {
                String date = event_times.get(position).event_time;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+00:00'", Locale.ENGLISH);
                SimpleDateFormat sdf2 = new SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.ENGLISH);
                Date d = sdf.parse(date);
                Log.e("dateintial", d.toString());
                Log.e("date", sdf2.format(d));
                splitDate = String.format(Locale.ENGLISH,sdf2.format(d));
            } catch (Exception e) {
                e.printStackTrace();
            }

            String eventTime[] = splitDate.split("\\s");

            Typeface font= Typeface.createFromAsset(activity.getAssets(), "font/GothamRounded-Book.otf");
            holder.eventTimeTextView.setTypeface(font);

            holder.eventTimeTextView.setText(eventTime[3]+" "+eventTime[4].replace(".",""));

            //in some case, it will prevent unwanted situations;
            holder.eventTimeTextView.setOnCheckedChangeListener(null);
            //if true, your check box will be selected, else unselected

            holder.eventTimeTextView.setTag(position);

            if (position == time_selected_position) {
                holder.eventTimeTextView.setChecked(true);
            } else {
                holder.eventTimeTextView.setChecked(false);
            }
            holder.eventTimeTextView.setOnCheckedChangeListener(new CheckListener(holder.eventTimeTextView, position));

        }

        @Override
        public int getItemCount() {
            return (null != event_times ? event_times.size() : 0);
        }

        public class ItemRowHolder extends RecyclerView.ViewHolder {

            private RadioButton eventTimeTextView;

            public ItemRowHolder(View v) {
                super(v);
                this.eventTimeTextView = (RadioButton) v.findViewById(R.id.eventTimeTextView);

            }
        }

        public class CheckListener implements CompoundButton.OnCheckedChangeListener {

            RadioButton cusinelist3;
            int position;

            public CheckListener(RadioButton cusinelist, int position) {

                this.cusinelist3 = cusinelist;
                this.position = position;
            }

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    cusinelist3.setChecked(true);
                    time_selected_position = position;
                    eventTime = event_times.get(position).event_time;
                    if(ticketListView!=null && !ticketListView.isShown()){
                        ticketListView.setVisibility(View.VISIBLE);
                    }

                    EventTimeAdapter.this.notifyDataSetChanged();
                } else {
                    cusinelist3.setChecked(false);

                }
                compoundButton.setChecked(b);

            }
        }
    }

    private class EventTicketDetailsAdapter extends RecyclerView.Adapter<EventTicketDetailsAdapter.ItemRowHolder>{

        Activity activity;
        ArrayList<EventModelDetails.Event_ticket_details>event_ticket_detailses;

        public EventTicketDetailsAdapter(Activity activity, ArrayList<EventModelDetails.Event_ticket_details> event_ticket_detailses) {
            this.activity = activity;
            this.event_ticket_detailses = event_ticket_detailses;

        }

        @Override
        public EventTicketDetailsAdapter.ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ticketlist_costom, null);
            ItemRowHolder mh = new ItemRowHolder(v);
            return mh;
        }

        @SuppressLint("NewApi")
        @Override
        public void onBindViewHolder(final EventTicketDetailsAdapter.ItemRowHolder holder, final int i) {

            holder.ticketNameTextView.setText(event_ticket_detailses.get(i).ticket_title);


            //Check event proceed in Money OR Coin
            if(Double.parseDouble(event_ticket_detailses.get(i).ticket_price) > 0){
                TICKET_TYPE = "PRICE";
                holder.amountTextView.setText(eventCurrencyCode+" "+eventCurrencySymbol+String.format(Locale.ENGLISH,"%.2f",Double.parseDouble(event_ticket_detailses.get(i).ticket_price)));
            }else{
                TICKET_TYPE = "COIN";
                holder.amountTextView.setText(event_ticket_detailses.get(i).ticket_coin+" Rewards");
            }

            holder.descriptionTextView.setText(event_ticket_detailses.get(i).ticket_description);


            holder.addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(bookingDate.isEmpty()){

                        toast(getResources().getString(R.string.pleaseselectadate));

                    }else if(eventTime.isEmpty()){

                        toast(getResources().getString(R.string.pleaseselectatime));

                    }else{

                        holder.addButton.setVisibility(View.GONE);
                        holder.plusButton.setVisibility(View.VISIBLE);
                        holder.minusButton.setVisibility(View.VISIBLE);
                        holder.menuQtyEditText.setVisibility(View.VISIBLE);
                        holder.plusButton.performClick();
                    }
                }
            });

            holder.plusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(bookingDate.isEmpty()){

                        toast(getResources().getString(R.string.pleaseselectadate));

                    }else if(eventTime.isEmpty()){

                        toast(getResources().getString(R.string.pleaseselectatime));

                    }else{

                        String qty = holder.menuQtyEditText.getText().toString().trim();
                        int increase = Integer.parseInt(qty);

                        double TICKET_QUANTITY = Double.parseDouble(event_ticket_detailses.get(i).ticket_quantity);
                        double BOOKED_QUANTITY = Double.parseDouble(event_ticket_detailses.get(i).ticket_booked);
                        double AVAILABLE_QUANTITY = TICKET_QUANTITY-BOOKED_QUANTITY;
                        if (increase >= AVAILABLE_QUANTITY) {
                            showAlertDialog("Maximum available quantity "+String.valueOf(AVAILABLE_QUANTITY).replace(".0",""));
                        } else {
                            increase += 1;
                            holder.menuQtyEditText.setText(String.valueOf(increase));

                            //Check ticket type coin or price
                            double totalPrice=0;
                            if(TICKET_TYPE.equalsIgnoreCase("PRICE")){
                                totalPrice = (Double.parseDouble(event_ticket_detailses.get(i).ticket_price)) * (Double.parseDouble("1"));
                            }else{
                                totalPrice = (Double.parseDouble(event_ticket_detailses.get(i).ticket_coin)) * (Double.parseDouble("1"));
                            }

                            CartDetails cartDetails = new CartDetails();
                            cartDetails.setTicketID(event_ticket_detailses.get(i).id);
                            cartDetails.setTicketTitle(event_ticket_detailses.get(i).ticket_title);
                            if(TICKET_TYPE.equalsIgnoreCase("PRICE")) {
                                cartDetails.setTicketPrice(event_ticket_detailses.get(i).ticket_price);
                                cartDetails.setTicketType("PRICE");
                            }else{
                                cartDetails.setTicketPrice(event_ticket_detailses.get(i).ticket_coin);
                                cartDetails.setTicketType("COIN");
                            }
                            cartDetails.setTotalPrice(String.valueOf(totalPrice));
                            cartDetails.setTicketQuantity("1");
                            cartDetails.setTicketDes(event_ticket_detailses.get(i).ticket_description);
                            cartDetails.setTicketAddRemove("ADD");

                            //values insert to database
                            databaseManager.openDatabase();
                            databaseManager.insert(cartDetails);
                            databaseManager.closeDatabase();

                            payButton.setVisibility(View.VISIBLE);
                            if(TICKET_TYPE.equalsIgnoreCase("PRICE")) {
                                payButton.setText("PAY " + eventCurrencyCode + " " + eventCurrencySymbol + databaseManager.getSubTotal());
                            }else{
                                payButton.setText("PAY "+databaseManager.getSubTotal().replace(".00","")+" REWARDS");
                            }
                        }
                    }

                }
            });

            holder.minusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String qty1 = holder.menuQtyEditText.getText().toString().trim();

                    int decrease = Integer.parseInt(qty1);

                    if (decrease == 0) {

                        if(Double.parseDouble(databaseManager.getSubTotal()) > 0){
                            payButton.setVisibility(View.VISIBLE);
                        }else{

                            payButton.setVisibility(View.GONE);
                            holder.addButton.setVisibility(View.VISIBLE);
                            holder.plusButton.setVisibility(View.GONE);
                            holder.minusButton.setVisibility(View.GONE);
                            holder.menuQtyEditText.setVisibility(View.GONE);
                        }


                    } else {
                        decrease -= 1;
                        holder.menuQtyEditText.setText(String.valueOf(decrease));

                        //Check ticket type coin or price
                        double totalPrice=0;
                        if(TICKET_TYPE.equalsIgnoreCase("PRICE")){
                            totalPrice = (Double.parseDouble(event_ticket_detailses.get(i).ticket_price)) * (Double.parseDouble("1"));
                        }else{
                            totalPrice = (Double.parseDouble(event_ticket_detailses.get(i).ticket_coin)) * (Double.parseDouble("1"));
                        }

                        CartDetails cartDetails = new CartDetails();
                        cartDetails.setTicketID(event_ticket_detailses.get(i).id);
                        cartDetails.setTicketTitle(event_ticket_detailses.get(i).ticket_title);
                        if(TICKET_TYPE.equalsIgnoreCase("PRICE")) {
                            cartDetails.setTicketType("PRICE");
                            cartDetails.setTicketPrice(event_ticket_detailses.get(i).ticket_price);
                        }else{
                            cartDetails.setTicketType("COIN");
                            cartDetails.setTicketPrice(event_ticket_detailses.get(i).ticket_coin);
                        }
                        cartDetails.setTotalPrice(String.valueOf(totalPrice));
                        cartDetails.setTicketQuantity("1");
                        cartDetails.setTicketDes(event_ticket_detailses.get(i).ticket_description);
                        cartDetails.setTicketAddRemove("REMOVE");

                        //values insert to database
                        databaseManager.openDatabase();
                        databaseManager.insert(cartDetails);
                        databaseManager.closeDatabase();

                        payButton.setVisibility(View.VISIBLE);

                        if(TICKET_TYPE.equalsIgnoreCase("PRICE")) {
                            payButton.setText("PAY " + eventCurrencyCode + " " + eventCurrencySymbol + databaseManager.getSubTotal());
                        }else{
                            payButton.setText("PAY "+databaseManager.getSubTotal().replace(".00","")+" REWARDS");
                        }

                        if(Double.parseDouble(databaseManager.getSubTotal()) > 0){
                            payButton.setVisibility(View.VISIBLE);
                        }else{

                            payButton.setVisibility(View.GONE);
                            holder.addButton.setVisibility(View.VISIBLE);
                            holder.plusButton.setVisibility(View.GONE);
                            holder.minusButton.setVisibility(View.GONE);
                            holder.menuQtyEditText.setVisibility(View.GONE);
                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return (null != event_ticket_detailses ? event_ticket_detailses.size() : 0);
        }

        public class ItemRowHolder extends RecyclerView.ViewHolder {

            TextView ticketNameTextView;
            TextView amountTextView;
            TextView descriptionTextView;
            TextView menuQtyEditText;
            Button minusButton;
            Button plusButton;
            Button addButton;

            public ItemRowHolder(View view) {
                super(view);

                ticketNameTextView = (TextView) view.findViewById(R.id.ticketNameTextView);
                amountTextView = (TextView) view.findViewById(R.id.amountTextView);
                descriptionTextView = (TextView) view.findViewById(R.id.descriptionTextView);
                menuQtyEditText = (TextView) view.findViewById(R.id.menuQtyEditText);
                minusButton = (Button) view.findViewById(R.id.minusButton);
                plusButton = (Button) view.findViewById(R.id.plusButton);
                addButton = (Button) view.findViewById(R.id.addButton);
            }
        }
    }
}
