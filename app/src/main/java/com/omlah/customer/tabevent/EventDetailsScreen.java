package com.omlah.customer.tabevent;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.omlah.customer.R;
import com.omlah.customer.base.BaseActivity;
import com.omlah.customer.model.EventModelDetails;
import com.omlah.customer.service.RequestID;
import com.omlah.customer.service.ServerListener;
import com.omlah.customer.service.ServerRequestwithHeader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 16-10-2017.
 */

public class EventDetailsScreen extends BaseActivity implements ServerListener{

    //Create class objects
    Fragment fragment;
    ServerRequestwithHeader serverRequestwithHeader;

    //Create xml objects
    @BindView(R.id.contentLayout)RelativeLayout contentLayout;
    @BindView(R.id.webVideoView)WebView webVideoView;
    @BindView(R.id.eventImageView)ImageView eventImageView;
    @BindView(R.id.eventErrorImage)RelativeLayout eventErrorImage;
    @BindView(R.id.backImageView)ImageView backImageView;
    @BindView(R.id.eventNameTextView)TextView eventNameTextView;
    @BindView(R.id.eventTypeTextView)TextView eventTypeTextView;
    @BindView(R.id.placeNameTextView)TextView placeNameTextView;
    @BindView(R.id.locationTextView)TextView locationTextView;
    @BindView(R.id.dateTextView)TextView dateTextView;
    @BindView(R.id.timingTextView)TextView timingTextView;
    @BindView(R.id.eventDetailFrameLayout)FrameLayout eventDetailFrameLayout;
    @BindView(R.id.tabRadioGroup)RadioGroup tabRadioGroup;
    @BindView(R.id.aboutEventButton)RadioButton aboutEventButton;
    @BindView(R.id.organizersButton)RadioButton organizersButton;
    @BindView(R.id.termsconditionButton)RadioButton termsconditionButton;
    @BindView(R.id.bookButton)Button bookButton;

    private static String eventId ="",
            currentTab="About",
            eventDescriptiontxt="",
            organizerName="",
            organizerBusinessName="",
            EventID="",
            youtubeURL="";

    private static ArrayList<String>termsandcondition = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details_screen);
        hideActionBar();

        //Initialize xml objects
        ButterKnife.bind(this);

        //Initialize class objects
        serverRequestwithHeader = ServerRequestwithHeader.getInstance(this);

        //Fonts
        Typeface font= Typeface.createFromAsset(getAssets(), "font/GothamRounded-Medium.ttf");
        aboutEventButton.setTypeface(font);
        organizersButton.setTypeface(font);
        termsconditionButton.setTypeface(font);

        //Get Intent value
        final Intent intent = getIntent();
        if(intent!=null){

            eventId = intent.getStringExtra("EventId");
            if(isConnectingToInternet()){
                Map<String, String> params = new HashMap<>();
                showProgressDialog();
                serverRequestwithHeader.createRequest(EventDetailsScreen.this,params, RequestID.REQ_EVENT_DETAILS,"GET",eventId);
            }else{
                noInternetAlertDialog();
            }
        }

        //Switching event
        tabRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {

                switch (i){

                    case R.id.aboutEventButton:

                        if(!currentTab.equalsIgnoreCase("About")){

                            currentTab = "About";
                            fragment = new AboutScreen();
                            fragmentChange(fragment);

                        }

                        break;

                    case R.id.organizersButton:

                        if(!currentTab.equalsIgnoreCase("Organizers")){

                            currentTab = "Organizers";
                            fragment = new OrganizersScreen();
                            fragmentChange(fragment);
                        }

                        break;

                    case R.id.termsconditionButton:

                        if(!currentTab.equalsIgnoreCase("termscondition")){

                            currentTab = "termscondition";
                            fragment = new TermsandCondition();
                            fragmentChange(fragment);
                        }


                        break;

                }
            }
        });


        //BackIcon click event
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent1 = new Intent(EventDetailsScreen.this, EventTicketScreen.class);
                intent1.putExtra("EventID",EventID);
                Log.e("EventID",EventID);
                startActivity(intent1);
            }
        });

    }

    //Fragment changes method
    public void fragmentChange(Fragment fragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.eventDetailFrameLayout,fragment).commit();
    }

    @Override
    public void onSuccess(Object result, RequestID requestID) {

        hideProgressDialog();

        EventModelDetails eventModelDetails = (EventModelDetails)result;

        //Save eventID
        EventID = eventModelDetails.data.events.id;

        //Event Image show
        if(eventModelDetails.data.events.event_image!=null){
            if(!eventModelDetails.data.events.event_image.isEmpty() || !eventModelDetails.data.events.event_image.equals("")){
                eventErrorImage.setVisibility(View.GONE);
                eventImageView.setVisibility(View.VISIBLE);
                Picasso.with(EventDetailsScreen.this).load(eventModelDetails.data.events.event_image).into(eventImageView);
            }else{
                eventErrorImage.setVisibility(View.VISIBLE);
                eventImageView.setVisibility(View.GONE);
            }
        }else{

            eventErrorImage.setVisibility(View.VISIBLE);
            eventImageView.setVisibility(View.GONE);
        }

        //Show event details
        youtubeURL = eventModelDetails.data.events.youtube_url;
        if(youtubeURL.isEmpty()){
            eventImageView.setVisibility(View.VISIBLE);
        }else{
            //set youtube video
            String uriPath = getYoutubeVideoId(youtubeURL); //update package name
            webVideoView.getSettings().setJavaScriptEnabled(true);
            webVideoView.setWebChromeClient(new WebChromeClient(){});
            webVideoView.loadData("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/"+uriPath+"\" frameborder=\"0\" allowfullscreen></iframe>", "text/html" , "utf-8" );


            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    eventImageView.setVisibility(View.GONE);
                }
            },1000);
        }

        eventNameTextView.setText(eventModelDetails.data.events.event_name);
        placeNameTextView.setText(eventModelDetails.data.events.event_place);
        locationTextView.setText(eventModelDetails.data.events.event_address);


        //Show date and time
        if(!eventModelDetails.data.events.start_date.isEmpty()){
            if(!eventModelDetails.data.events.end_date.isEmpty()){
                dateTextView.setText(dayDateMonthConversion(eventModelDetails.data.events.start_date)+" - "+dayDateMonthConversion(eventModelDetails.data.events.end_date));
            }else{
                dateTextView.setText(dayDateMonthConversion(eventModelDetails.data.events.start_date));
            }
        }

        //Show time
        try{
            if(eventModelDetails.data.events.event_ticket_dates.get(0).event_times.size() > 0){
                timingTextView.setText(timeConversion(eventModelDetails.data.events.event_ticket_dates.get(0).event_times.get(0).event_time)+" Onwards");
                timingTextView.setVisibility(View.VISIBLE);
            }else{
                timingTextView.setVisibility(View.GONE);
            }

        }catch (Exception e){}


        eventDescriptiontxt = eventModelDetails.data.events.description;
        organizerName = eventModelDetails.data.events.user.name;
        organizerBusinessName = eventModelDetails.data.events.user.business_name;


        termsandcondition.clear();
        ArrayList<EventModelDetails.Event_terms>event_categories = eventModelDetails.data.events.event_terms;
        for(EventModelDetails.Event_terms event_terms : event_categories){

            termsandcondition.add(event_terms.terms);
        }

        //default load fragment
        currentTab = "About";
        fragment = new AboutScreen();
        fragmentChange(fragment);

        //Set visible to the xml file
        contentLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFailure(String error, RequestID requestID) {

        hideProgressDialog();
        toast(error);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //About fragment screen
    public static class AboutScreen extends Fragment {

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.event_about_screen, container,false);

            TextView eventDescription = (TextView)view.findViewById(R.id.eventDescription);

            eventDescription.setText(eventDescriptiontxt);


            return view;
        }
    }

    public static class OrganizersScreen extends Fragment {

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.event_organizer_screen, container,false);

            TextView userNameTextView     = (TextView)view.findViewById(R.id.userNameTextView);
            TextView businessNameTextView = (TextView)view.findViewById(R.id.businessNameTextView);

            userNameTextView.setText(organizerName);
            businessNameTextView.setText(organizerBusinessName);

            return view;
        }
    }

    public static class TermsandCondition extends Fragment {

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.event_tc_screen, container,false);

            LinearLayout tclayout = (LinearLayout)view.findViewById(R.id.tclayout);

            //Set event category
            if(termsandcondition.size() > 0){

                final int N = termsandcondition.size(); // total number of textviews to add

                final TextView[] myTextViews = new TextView[N]; // create an empty array;

                for (int k = 0; k < N; k++) {
                    // create a new textview
                    final TextView rowTextView = new TextView(getActivity());
                    //Fonts
                    Typeface font= Typeface.createFromAsset(getActivity().getAssets(), "font/GothamRounded-Book.otf");
                    rowTextView.setTypeface(font);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(25,7,25,7);
                    rowTextView.setLayoutParams(params);

                    // set some properties of rowTextView or something
                    rowTextView.setText(termsandcondition.get(k));
                    rowTextView.setTextSize(13);
                    rowTextView.setPadding(5,5,5,5);
                    //rowTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dot, 0, 0, 0);

                    // add the textview to the linearlayout
                    tclayout.addView(rowTextView);

                    // save a reference to the textview for later
                    myTextViews[k] = rowTextView;
                }
            }

            return view;
        }
    }

    //get youtube video code
    public static String getYoutubeVideoId(String youtubeUrl)
    {
        String video_id="";
        if (youtubeUrl != null && youtubeUrl.trim().length() > 0 && youtubeUrl.startsWith("http"))
        {

            String expression = "^.*((youtu.be"+ "\\/)" + "|(v\\/)|(\\/u\\/w\\/)|(embed\\/)|(watch\\?))\\??v?=?([^#\\&\\?]*).*"; // var regExp = /^.*((youtu.be\/)|(v\/)|(\/u\/\w\/)|(embed\/)|(watch\?))\??v?=?([^#\&\?]*).*/;
            CharSequence input = youtubeUrl;
            Pattern pattern = Pattern.compile(expression,Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(input);
            if (matcher.matches())
            {
                String groupIndex1 = matcher.group(7);
                if(groupIndex1!=null && groupIndex1.length()==11)
                    video_id = groupIndex1;
            }
        }
        return video_id;
    }
}
