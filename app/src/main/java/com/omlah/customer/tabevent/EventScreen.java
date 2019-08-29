package com.omlah.customer.tabevent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.omlah.customer.R;
import com.omlah.customer.account.GetStartedScreen;
import com.omlah.customer.base.BaseFragment;
import com.omlah.customer.base.BaseScreen;
import com.omlah.customer.common.LoginSession;
import com.omlah.customer.model.EventModelList;
import com.omlah.customer.service.RequestID;
import com.omlah.customer.service.ServerListener;
import com.omlah.customer.service.ServerRequestwithHeader;
import com.squareup.picasso.Picasso;

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

public class EventScreen extends BaseFragment implements ServerListener{

    //Create class objects
    EventListAdapter eventListAdapter;
    ServerRequestwithHeader serverRequestwithHeader;

    //Create xml objects
    @BindView(R.id.eventListView)ListView eventListView;
    @BindView(R.id.errorImageView)TextView errorImageView;

    String globalSearchName = "";
    String eventNameSearchText,eventPlaceSearchText;

    boolean searchopen = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.event_screen, container, false);

        //Initialize xml objects
        ButterKnife.bind(this,rootView);

        //Initialize class objects
        serverRequestwithHeader = ServerRequestwithHeader.getInstance(getActivity());

        //getFeed response
        getEventListResponse();

        //filter method
        BaseScreen.searchEditText.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try {

                    globalSearchName = s.toString();
                    String filter_text = BaseScreen.searchEditText.getText().toString().trim().toLowerCase(Locale.getDefault());

                    eventListAdapter.searchFilter(filter_text);

                } catch (Exception e) {

                    e.printStackTrace();

                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            public void afterTextChanged(Editable s) {

            }
        });


        BaseScreen.searchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(searchopen){

                    BaseScreen.searchLayout.setVisibility(View.VISIBLE);
                    BaseScreen.searchImageView.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.close_icon));
                    searchopen = false;

                }else{

                    BaseScreen.searchLayout.setVisibility(View.GONE);
                    BaseScreen.searchEditText.setText("");
                    BaseScreen.searchImageView.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.search_icon));
                    searchopen = true;
                }
            }
        });

        return rootView;
    }

    private void getEventListResponse() {

        if(isConnectingToInternet()){

          //  if (getCacheData(Constents.EVENT_URL, EventModelList.class).toString().equals("empty")) {

                Map<String, String> params = new HashMap<>();
                showProgressDialog();
                serverRequestwithHeader.createRequest(EventScreen.this,params, RequestID.REQ_EVENT_LIST,"GET","");

           // }else{

                //onSuccess(getCacheData(Constents.EVENT_URL, EventModelList.class),RequestID.REQ_EVENT_LIST);
         //   }

        }else{

            noInternetAlertDialog();
        }
    }

    @Override
    public void onSuccess(Object result, RequestID requestID) {

        hideProgressDialog();

        try{
            EventModelList eventModelList = (EventModelList)result;
            if(!
                    eventModelList.message.equalsIgnoreCase("No event(s) found")){
                ArrayList<EventModelList.Data>datas = eventModelList.data;
                eventListAdapter = new EventListAdapter(getActivity(),datas);
                eventListView.setAdapter(eventListAdapter);

                eventListView.setVisibility(View.VISIBLE);
                errorImageView.setVisibility(View.GONE);
                BaseScreen.searchImageView.setVisibility(View.VISIBLE);
            }else{
                eventListView.setVisibility(View.GONE);
                errorImageView.setVisibility(View.VISIBLE);
                BaseScreen.searchImageView.setVisibility(View.GONE);
            }

        }catch (Exception e){
        }
    }

    @Override
    public void onFailure(String error, RequestID requestID) {

        hideProgressDialog();

        try{

            String[] errorTxt = error.split("<@>");

            if(errorTxt[1].equalsIgnoreCase("1")){

                toast(errorTxt[0]);
                LoginSession loginSession = LoginSession.getInstance(getActivity());
                loginSession.logout();
                NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancelAll();
                Intent intent = new Intent(getActivity(),GetStartedScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();

            }else{

                eventListView.setVisibility(View.GONE);
                errorImageView.setVisibility(View.VISIBLE);
                BaseScreen.searchImageView.setVisibility(View.GONE);
            }

        }catch (Exception  e){}



    }

    private class EventListAdapter extends BaseAdapter{

        Activity activity;
        ArrayList<EventModelList.Data> eventModelListsOriginal;
        ArrayList<EventModelList.Data> eventModelListsDummy;
        private LayoutInflater inflater;

        public EventListAdapter(Activity activity, ArrayList<EventModelList.Data> eventModelLists) {
            this.activity = activity;
            this.eventModelListsOriginal = eventModelLists;
            this.eventModelListsDummy = new ArrayList<>();
            this.eventModelListsDummy.addAll(eventModelListsOriginal);
                this.inflater = LayoutInflater.from(activity);
        }

        @Override
        public int getCount() {
            return eventModelListsOriginal.size();
        }

        @Override
        public Object getItem(int i) {
            return eventModelListsOriginal.get(i);
        }

        @Override
        public long getItemId(int i) {
            return eventModelListsOriginal.size();
        }

        @SuppressLint("NewApi")
        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            inflater = activity.getLayoutInflater();

            final ViewHolder viewHolder;

            try {
                if (view == null) {

                    viewHolder = new ViewHolder();
                    view = inflater.inflate(R.layout.custom_eventlist, null);

                    //Initialize xml object
                    viewHolder.monthTextView = (TextView) view.findViewById(R.id.monthTextView);
                    viewHolder.dateTextView = (TextView) view.findViewById(R.id.dateTextView);
                    viewHolder.dayTextView = (TextView) view.findViewById(R.id.dayTextView);
                    viewHolder.eventNameTextView = (TextView) view.findViewById(R.id.eventNameTextView);
                    viewHolder.placeNameTextView = (TextView) view.findViewById(R.id.placeNameTextView);
                    viewHolder.locationTextView = (TextView) view.findViewById(R.id.locationTextView);
                    viewHolder.eventImageView = (ImageView) view.findViewById(R.id.eventImageView);
                    viewHolder.eventErrorImage = (RelativeLayout) view.findViewById(R.id.eventErrorImage);
                    viewHolder.myLinearLayout = (LinearLayout) view.findViewById(R.id.myLinearLayout);
                    viewHolder.bookButton = (Button) view.findViewById(R.id.bookButton);
                    viewHolder.oneCat = (TextView) view.findViewById(R.id.oneCat);
                    viewHolder.twoCat = (TextView) view.findViewById(R.id.twoCat);
                    viewHolder.threeCat = (TextView) view.findViewById(R.id.threeCat);
                    viewHolder.fourCat = (TextView) view.findViewById(R.id.fourCat);
                    viewHolder.fiveCat = (TextView) view.findViewById(R.id.fiveCat);
                    view.setTag(viewHolder);

                } else {
                    viewHolder = (ViewHolder) view.getTag();
                }

                //Show Event time
                if (!eventModelListsOriginal.get(i).start_date.isEmpty()) {

                    String createDate = "";
                    try {
                        String date = eventModelListsOriginal.get(i).start_date;
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

                    //Check for event start date and end date
                    if (eventModelListsOriginal.get(i).start_date.equals(eventModelListsOriginal.get(i).end_date)) {
                        viewHolder.dayTextView.setText(splitDate[0]);
                    } else {
                        viewHolder.dayTextView.setText(getResources().getString(R.string.Onwards));
                    }

                }

                //Show event details
                viewHolder.eventNameTextView.setText(eventModelListsOriginal.get(i).event_name);
                viewHolder.placeNameTextView.setText(eventModelListsOriginal.get(i).event_place);
                viewHolder.locationTextView.setText(eventModelListsOriginal.get(i).event_address);


                //Event Image show
                if (eventModelListsOriginal.get(i).event_image != null) {
                    if (!eventModelListsOriginal.get(i).event_image.isEmpty() || !eventModelListsOriginal.get(i).event_image.equals("")) {
                        viewHolder.eventErrorImage.setVisibility(View.GONE);
                        viewHolder.eventImageView.setVisibility(View.VISIBLE);
                        Picasso
                                .with(activity)
                                .load(eventModelListsOriginal.get(i).event_image)
                                .resize(1024,500)
                                .into(viewHolder.eventImageView);
                    } else {
                        viewHolder.eventErrorImage.setVisibility(View.VISIBLE);
                        viewHolder.eventImageView.setVisibility(View.GONE);
                    }
                }

                //Set event category
                if (eventModelListsOriginal.get(i).event_categories.size() > 0) {

                    try {
                        viewHolder.oneCat.setText(eventModelListsOriginal.get(i).event_categories.get(0).business_category.category_name);
                        viewHolder.oneCat.setVisibility(View.VISIBLE);
                        viewHolder.twoCat.setText(eventModelListsOriginal.get(i).event_categories.get(1).business_category.category_name);
                        viewHolder.twoCat.setVisibility(View.VISIBLE);
                        viewHolder.threeCat.setText(eventModelListsOriginal.get(i).event_categories.get(2).business_category.category_name);

                        viewHolder.threeCat.setVisibility(View.VISIBLE);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            /*if(eventModelListsOriginal.get(i).event_categories.size() > 0){
                try{
                    final int N = eventModelListsOriginal.get(i).event_categories.size(); // total number of textviews to add

                    final TextView[] myTextViews = new TextView[N]; // create an empty array;

                    for (int k = 0; k < N; k++) {
                        // create a new textview
                        final TextView rowTextView = new TextView(activity);

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(0,0,10,0);
                        rowTextView.setLayoutParams(params);

                        // set some properties of rowTextView or something
                        rowTextView.setText(eventModelListsOriginal.get(i).event_categories.get(k).business_category.category_name);
                        rowTextView.setTextSize(11);
                        rowTextView.setPadding(5,5,5,5);
                        rowTextView.setBackground(activity.getResources().getDrawable(R.drawable.gray_white_border));

                        // add the textview to the linearlayout
                        viewHolder.myLinearLayout.addView(rowTextView);

                        // save a reference to the textview for later
                        myTextViews[i] = rowTextView;
                    }

                }catch (Exception e){e.printStackTrace();}

            }*/

                //Search text highlighted in feed name
                if (eventNameSearchText != null && !eventNameSearchText.isEmpty()) {

                    int startPos = eventModelListsOriginal.get(i).event_name.toLowerCase(Locale.US).indexOf(eventNameSearchText.toLowerCase(Locale.US));
                    int endPos = startPos + eventNameSearchText.length();

                    if (startPos != -1) {
                        Spannable spannable = new SpannableString(eventModelListsOriginal.get(i).event_name);
                        ColorStateList blueColor = new ColorStateList(new int[][]{new int[]{}}, new int[]{activity.getResources().getColor(R.color.colorAccent)});
                        TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.BOLD, -1, blueColor, null);
                        spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        viewHolder.eventNameTextView.setText(spannable);
                    } else {
                        viewHolder.eventNameTextView.setText(eventModelListsOriginal.get(i).event_name);
                    }

                } else {
                    viewHolder.eventNameTextView.setText(eventModelListsOriginal.get(i).event_name);

                }

                //Search text highlighted in feed description
                if (eventPlaceSearchText != null && !eventPlaceSearchText.isEmpty()) {

                    int startPos = eventModelListsOriginal.get(i).event_place.toLowerCase(Locale.US).indexOf(eventPlaceSearchText.toLowerCase(Locale.US));
                    int endPos = startPos + eventPlaceSearchText.length();

                    if (startPos != -1) {
                        Spannable spannable = new SpannableString(eventModelListsOriginal.get(i).event_place);
                        ColorStateList blueColor = new ColorStateList(new int[][]{new int[]{}}, new int[]{activity.getResources().getColor(R.color.colorAccent)});
                        TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.BOLD, -1, blueColor, null);
                        spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        viewHolder.placeNameTextView.setText(spannable);
                    } else {
                        viewHolder.placeNameTextView.setText(eventModelListsOriginal.get(i).event_place);
                    }

                } else {
                    viewHolder.placeNameTextView.setText(eventModelListsOriginal.get(i).event_place);

                }

                //Book button click event
                viewHolder.bookButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(getActivity(), EventDetailsScreen.class);
                        intent.putExtra("EventId", eventModelListsOriginal.get(i).id);
                        getActivity().startActivity(intent);

                    }
                });

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(getActivity(), EventDetailsScreen.class);
                        intent.putExtra("EventId", eventModelListsOriginal.get(i).id);
                        getActivity().startActivity(intent);

                    }
                });
            }catch (Exception e){}

            return view;
        }

        public void searchFilter(String filterText) {

            Log.e("filterText",filterText);
            Log.e("contactListsOriginal",""+eventModelListsOriginal.size());
            Log.e("contactListsDummy",""+eventModelListsDummy.size());

            eventNameSearchText = filterText;
            eventPlaceSearchText = filterText;
            filterText = filterText.toLowerCase(Locale.getDefault());
            eventModelListsOriginal.clear();
            Log.e("contactListsOriginal","After"+eventModelListsOriginal.size());
            if (filterText.length() == 0) {
                eventModelListsOriginal.addAll(eventModelListsDummy);
            } else {
                for (EventModelList.Data contactList : eventModelListsDummy) {
                    if (contactList.event_name.toLowerCase(Locale.getDefault()).contains(filterText) || contactList.event_place.toLowerCase(Locale.getDefault()).contains(filterText)) {
                        eventModelListsOriginal.add(contactList);
                    }
                }
                if (eventModelListsOriginal.size() == 0) {
                    toast(getResources().getString(R.string.NoEventfound));
                }
            }

            notifyDataSetChanged();
        }

        private class ViewHolder {

            private TextView monthTextView, dateTextView,dayTextView,eventNameTextView,placeNameTextView,locationTextView;
            private TextView oneCat,twoCat,threeCat,fourCat,fiveCat;
            private ImageView eventImageView;
            private RelativeLayout eventErrorImage;
            private LinearLayout myLinearLayout;
            private Button bookButton;

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        BaseScreen.searchLayout.setVisibility(View.GONE);
        BaseScreen.searchImageView.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.search_icon));
    }
}
