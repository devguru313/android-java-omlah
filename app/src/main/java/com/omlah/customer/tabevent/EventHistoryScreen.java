package com.omlah.customer.tabevent;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.omlah.customer.R;
import com.omlah.customer.base.BaseActivity;
import com.omlah.customer.model.EventList;
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
 * Created by admin on 28-10-2017.
 */

public class EventHistoryScreen extends BaseActivity implements ServerListener{

    ServerRequestwithHeader serverRequestwithHeader;
    EventListAdapter eventListAdapter;

    //Create xml files
    @BindView(R.id.tabRadioGroup)RadioGroup tabRadioGroup;
    @BindView(R.id.upComingButton)RadioButton upComingButton;
    @BindView(R.id.completedButton)RadioButton completedButton;
    @BindView(R.id.eventListView)ListView eventListView;
    @BindView(R.id.errorImageView)TextView errorImageView;
    @BindView(R.id.searchEditText)EditText searchEditText;

    //Create string files
    String currentTab="upcome";
    String mSearchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_list_screen);
        showBackArrow();
        setActionBarTitle(getResources().getString(R.string.BookingHistory));

        //Initialize xml file
        ButterKnife.bind(this);

        //Initialize class objects
        serverRequestwithHeader = ServerRequestwithHeader.getInstance(this);

        //Default method
        currentTab = "upcome";
        getResponseMethod();

        //set font
        Typeface font= Typeface.createFromAsset(getAssets(), "font/GothamRounded-Medium.ttf");
        upComingButton.setTypeface(font);
        completedButton.setTypeface(font);

        //Switching event
        tabRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {

                switch (i){

                    case R.id.upComingButton:

                        if(!currentTab.equalsIgnoreCase("upcome")){
                            currentTab = "upcome";
                            eventListAdapter.searchFilter("upcome");

                        }

                        break;

                    case R.id.completedButton:

                        if(!currentTab.equalsIgnoreCase("completed")){
                            currentTab = "completed";
                            eventListAdapter.searchFilter("completed");

                        }

                        break;

                }
            }
        });

    }

    //getEventList
    private void getResponseMethod() {

        //Get basic deatils
        if(isConnectingToInternet()){

            Map<String, String> params = new HashMap<>();
            showProgressDialog();
            serverRequestwithHeader.createRequest(EventHistoryScreen.this,params, RequestID.REQ_BOOKING_EVENT_LIST,"GET","");

        }else{

            noInternetAlertDialog();
        }
    }

    @Override
    public void onSuccess(Object result, RequestID requestID) {

        hideProgressDialog();

        EventList eventList = (EventList)result;
        if(eventList.data == null){

            eventListView.setVisibility(View.GONE);
            errorImageView.setVisibility(View.VISIBLE);
            tabRadioGroup.setVisibility(View.GONE);

        }else{

            eventListView.setVisibility(View.VISIBLE);
            errorImageView.setVisibility(View.GONE);
            tabRadioGroup.setVisibility(View.VISIBLE);

            ArrayList<EventList.Booking> datas = eventList.data.booking;
            eventListAdapter = new EventListAdapter(this,datas);
            eventListView.setAdapter(eventListAdapter);
            eventListAdapter.notifyDataSetChanged();

            eventListAdapter.searchFilter("upcome");
        }


    }

    @Override
    public void onFailure(String error, RequestID requestID) {

        hideProgressDialog();
        eventListView.setVisibility(View.GONE);
        errorImageView.setVisibility(View.VISIBLE);
        tabRadioGroup.setVisibility(View.GONE);
    }

    private class EventListAdapter extends BaseAdapter {

        Activity activity;
        ArrayList<EventList.Booking>eventListOriginal;
        ArrayList<EventList.Booking>eventListDummy;
        private LayoutInflater inflater;

        public EventListAdapter(Activity activity, ArrayList<EventList.Booking> eventList) {
            this.activity = activity;
            this.eventListOriginal = eventList;
            this.eventListDummy = new ArrayList<>();
            this.eventListDummy.addAll(eventList);
            this.inflater = LayoutInflater.from(activity);
        }

        @Override
        public int getCount() {
            return eventListOriginal.size();
        }

        @Override
        public Object getItem(int i) {
            return eventListOriginal.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {

            ViewHolder viewHolder;

            if (view == null) {

                viewHolder = new ViewHolder();
                view = inflater.inflate(R.layout.custom_event_list,null);

                //Initialize xml object
                viewHolder.monthTextView         = (TextView) view.findViewById(R.id.monthTextView);
                viewHolder.dateTextView          = (TextView) view.findViewById(R.id.dateTextView);
                viewHolder.dayTextView           = (TextView) view.findViewById(R.id.dayTextView);
                viewHolder.eventNameTextView     = (TextView) view.findViewById(R.id.eventNameTextView);
                viewHolder.placeNameTextView     = (TextView) view.findViewById(R.id.placeNameTextView);
                viewHolder.eventTimeTextView     = (TextView) view.findViewById(R.id.eventTimeTextView);
                viewHolder.verifiedLayout        = (RelativeLayout) view.findViewById(R.id.verifiedLayout);

                view.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            //Show Event time
            if(!eventListOriginal.get(i).booking_date.isEmpty()){

                String createDate = "";
                try {
                    String date = eventListOriginal.get(i).booking_date;
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+00:00'", Locale.ENGLISH);
                    SimpleDateFormat sdf2 = new SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.ENGLISH);
                    Date d = sdf.parse(date);
                    Log.e("dateintial", d.toString());
                    Log.e("date", sdf2.format(d));
                    createDate = d.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String splitDate[] = createDate.split("\\s");

                viewHolder.monthTextView.setText(splitDate[1]);
                viewHolder.dateTextView.setText(splitDate[2]);
                viewHolder.dayTextView.setText(splitDate[0]);

            }

            //Show event details
            viewHolder.eventNameTextView.setText(eventListOriginal.get(i).booking_no);
            viewHolder.placeNameTextView.setText(eventListOriginal.get(i).event_name);

            if(eventListOriginal.get(i).verified.equals("0")){
                viewHolder.verifiedLayout.setVisibility(View.GONE);
            }else{
                viewHolder.verifiedLayout.setVisibility(View.VISIBLE);
            }

            //set booking time
            String splitDate="";

            try {
                String date = eventListOriginal.get(i).event_time;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+00:00'", Locale.ENGLISH);
                SimpleDateFormat sdf2 = new SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.ENGLISH);
                Date d = sdf.parse(date);
                Log.e("dateintial", d.toString());
                Log.e("date", sdf2.format(d));
                splitDate = sdf2.format(d);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String eventTime[] = splitDate.split("\\s");
            viewHolder.eventTimeTextView.setText(eventTime[3]+" "+eventTime[4].replace(".",""));

            //Book button click event
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(EventHistoryScreen.this,TicketSuccessScreen.class);
                    intent.putExtra("bookingID",eventListOriginal.get(i).id);
                    intent.putExtra("Screen","eventHistory");
                    intent.putExtra("Status",eventListOriginal.get(i).verified);
                    startActivity(intent);

                }
            });

            return view;
        }

        public void searchFilter(String filterText) {

            Log.e("filterText",filterText);
            Log.e("contactListsOriginal",""+eventListOriginal.size());
            Log.e("contactListsDummy",""+eventListDummy.size());

            mSearchText = filterText;
            filterText = filterText.toLowerCase(Locale.getDefault());
            eventListOriginal.clear();

            if (filterText.length() == 0) {
                eventListOriginal.addAll(eventListDummy);
            } else {
                for (EventList.Booking contactList : eventListDummy) {
                    if (contactList.event_tab.toLowerCase(Locale.getDefault()).contains(filterText) || contactList.event_tab.toLowerCase(Locale.getDefault()).contains(filterText)) {
                        eventListOriginal.add(contactList);
                    }
                }
                if (eventListOriginal.size() == 0) {
                    toast(getResources().getString(R.string.Noeventsfound));
                }
            }

            notifyDataSetChanged();

        }

        private class ViewHolder {

            private TextView monthTextView,statusTextView,
                    dateTextView,dayTextView,eventNameTextView,placeNameTextView,eventTimeTextView;
            RelativeLayout verifiedLayout;

        }
    }
}
