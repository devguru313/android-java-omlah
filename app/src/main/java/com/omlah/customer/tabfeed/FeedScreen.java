package com.omlah.customer.tabfeed;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.omlah.customer.R;
import com.omlah.customer.account.GetStartedScreen;
import com.omlah.customer.base.BaseActivity;
import com.omlah.customer.base.Utility;
import com.omlah.customer.common.LoginSession;
import com.omlah.customer.fcm.Config;
import com.omlah.customer.model.FeedModelList;
import com.omlah.customer.service.RequestID;
import com.omlah.customer.service.ServerListener;
import com.omlah.customer.service.ServerRequestwithHeader;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 15-09-2017.
 */

public class FeedScreen extends BaseActivity implements Serializable,ServerListener {

    //Create class objects
    Dialog settingsDialog;
    LoginSession loginSession;
    FeedListAdapter feedListAdapter;
    ServerRequestwithHeader serverRequestwithHeader;

    //Create xml objects
    @BindView(R.id.searchLayout)RelativeLayout searchLayout;
    @BindView(R.id.feedListView)ListView feedListView;
    @BindView(R.id.searchEditText)EditText searchEditText;
    @BindView(R.id.errorImageView)TextView errorImageView;

    String globalSearchName = "",mSearchText="",feedDesSearchText="";

    //BroadcastReceiver method
    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_screen);
        showBackArrow();
        setActionBarTitle(getResources().getString(R.string.Feeds));

        //Initialize xml objects
        ButterKnife.bind(this);

        //Initilaize class objects
        serverRequestwithHeader = ServerRequestwithHeader.getInstance(this);
        loginSession = LoginSession.getInstance(this);

        //getFeed response
        getFeedResponse();

        //filter method
        searchEditText.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try {

                    globalSearchName = s.toString();
                    String filter_text = searchEditText.getText().toString().trim().toLowerCase(Locale.getDefault());

                    feedListAdapter.searchFilter(filter_text);

                } catch (Exception e) {

                    e.printStackTrace();

                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            public void afterTextChanged(Editable s) {

            }
        });


        //Notification message received method
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                boolean check_update = intent.getBooleanExtra("update", false);
                if(check_update){

                    //getFeed response
                    getFeedResponse();

                }
            }
        };

    }

    private void getFeedResponse() {

        if(isConnectingToInternet()){

            Map<String, String> params = new HashMap<>();
            showProgressDialog();
            serverRequestwithHeader.createRequest(FeedScreen.this,params, RequestID.REQ_FEED,"GET","");

        }else{

            noInternetAlertDialog();
        }
    }

    @Override
    public void onSuccess(Object result, RequestID requestID) {

        hideProgressDialog();

        switch (requestID){

            case REQ_FEED:

                FeedModelList feedModelList = (FeedModelList) result;

                if(feedModelList.data == null){
                    errorImageView.setVisibility(View.VISIBLE);
                    feedListView.setVisibility(View.GONE);
                    searchLayout.setVisibility(View.GONE);
                }else{
                    ArrayList<FeedModelList.Data> feedModelLists = feedModelList.data;
                    feedListAdapter = new FeedListAdapter(this, feedModelLists);
                    feedListView.setAdapter(feedListAdapter);
                    errorImageView.setVisibility(View.GONE);
                    feedListView.setVisibility(View.VISIBLE);
                    searchLayout.setVisibility(View.VISIBLE);
                }

                break;

            case REQ_FEED_HIDE:

                toast(result.toString());
                getFeedResponse();

                break;
        }

    }

    @Override
    public void onFailure(String error, RequestID requestID) {

        hideProgressDialog();
        switch (requestID) {

            case REQ_FEED:
                hideProgressDialog();
                String[] errorTxt = error.split("<@>");

                if(errorTxt[1].equalsIgnoreCase("1")){

                    toast(errorTxt[0]);
                    LoginSession loginSession = LoginSession.getInstance(this);
                    loginSession.logout();
                    NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancelAll();
                    Intent intent = new Intent(this,GetStartedScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                }else {
                    errorImageView.setVisibility(View.VISIBLE);
                    feedListView.setVisibility(View.GONE);
                    searchLayout.setVisibility(View.GONE);
                }
                break;

            case REQ_FEED_HIDE:
                toast(error);
                break;
        }

    }

    private class FeedListAdapter extends BaseAdapter{

        Activity activity;
        ArrayList<FeedModelList.Data> feedModelListsOriginal;
        ArrayList<FeedModelList.Data> feedModelListsDummy;
        private LayoutInflater inflater;

        public FeedListAdapter(Activity activity, ArrayList<FeedModelList.Data> feedModelListsOriginal) {
            this.activity = activity;
            this.feedModelListsOriginal = feedModelListsOriginal;
            this.feedModelListsDummy = new ArrayList<>();
            this.feedModelListsDummy.addAll(feedModelListsOriginal);
            this.inflater = LayoutInflater.from(activity);
        }

        @Override
        public int getCount() {
            return feedModelListsOriginal.size();
        }

        @Override
        public Object getItem(int i) {
            return feedModelListsOriginal.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {

            LayoutInflater inflater = activity.getLayoutInflater();
            final ViewHolder viewHolder;

            if (view == null) {

                viewHolder = new ViewHolder();
                view = inflater.inflate(R.layout.custom_feedlist, null);

                viewHolder.feedImageView       = (ImageView)view.findViewById(R.id.feedImageView);
                viewHolder.userNameTextView    = (TextView)view.findViewById(R.id.userNameTextView);
                viewHolder.feedTimeTextView    = (TextView)view.findViewById(R.id.feedTimeTextView);
                viewHolder.feedContentTextView = (TextView)view.findViewById(R.id.feedContentTextView);
                viewHolder.moreButton          = (ImageView)view.findViewById(R.id.moreButton);
                viewHolder.feedVideoLayout     = (RelativeLayout) view.findViewById(R.id.feedVideoLayout);
                viewHolder.webVideoView        =  (WebView) view.findViewById(R.id.webVideoView);

                view.setTag(viewHolder);

            } else {

                viewHolder = (ViewHolder) view.getTag();
            }

            //Feed Name
            viewHolder.userNameTextView.setText(feedModelListsOriginal.get(i).user.business_name);

            viewHolder.feedTimeTextView.setText(timeZoneConverter(feedModelListsOriginal.get(i).created,loginSession.gettimeZone()));

            //Feed Description
            if(!feedModelListsOriginal.get(i).description.isEmpty() || !feedModelListsOriginal.get(i).description.equals("")){
                viewHolder.feedContentTextView.setText(feedModelListsOriginal.get(i).description);
            }else{
                viewHolder.feedContentTextView.setVisibility(View.GONE);
            }

            //Feed Image check
            if(feedModelListsOriginal.get(i).media_type.equalsIgnoreCase("image")){

                viewHolder.feedImageView.setVisibility(View.VISIBLE);
                viewHolder.feedVideoLayout.setVisibility(View.GONE);
                Picasso.with(FeedScreen.this).load(feedModelListsOriginal.get(i).media).into(viewHolder.feedImageView);

            }else if(feedModelListsOriginal.get(i).media_type.equalsIgnoreCase("video")){

                viewHolder.feedImageView.setVisibility(View.GONE);
                viewHolder.feedVideoLayout.setVisibility(View.VISIBLE);

                String uriPath = getYoutubeVideoId(feedModelListsOriginal.get(i).media);
                viewHolder.webVideoView.getSettings().setJavaScriptEnabled(true);
                viewHolder.webVideoView.setWebChromeClient(new WebChromeClient(){});
                viewHolder.webVideoView.loadData("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/"+uriPath+"\" frameborder=\"0\" allowfullscreen></iframe>", "text/html" , "utf-8" );

            }else{

                viewHolder.feedImageView.setVisibility(View.GONE);
                viewHolder.feedVideoLayout.setVisibility(View.GONE);
            }

            //Search text highlighted in feed name
            if (mSearchText != null && !mSearchText.isEmpty()) {

                int startPos = feedModelListsOriginal.get(i).user.business_name.toLowerCase(Locale.US).indexOf(mSearchText.toLowerCase(Locale.US));
                int endPos = startPos + mSearchText.length();

                if (startPos != -1) {
                    Spannable spannable = new SpannableString(feedModelListsOriginal.get(i).user.business_name);
                    ColorStateList blueColor = new ColorStateList(new int[][]{new int[]{}}, new int[]{activity.getResources().getColor(R.color.colorAccent)});
                    TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.BOLD, -1, blueColor, null);
                    spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    viewHolder.userNameTextView.setText(spannable);
                } else {
                    viewHolder.userNameTextView.setText(feedModelListsOriginal.get(i).user.business_name);
                }

            }else{
                viewHolder.userNameTextView.setText(feedModelListsOriginal.get(i).user.business_name);

            }

            //Search text highlighted in feed description
            if (feedDesSearchText != null && !feedDesSearchText.isEmpty()) {

                int startPos = feedModelListsOriginal.get(i).description.toLowerCase(Locale.US).indexOf(feedDesSearchText.toLowerCase(Locale.US));
                int endPos = startPos + feedDesSearchText.length();

                if (startPos != -1) {
                    Spannable spannable = new SpannableString(feedModelListsOriginal.get(i).description);
                    ColorStateList blueColor = new ColorStateList(new int[][]{new int[]{}}, new int[]{activity.getResources().getColor(R.color.colorAccent)});
                    TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.BOLD, -1, blueColor, null);
                    spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    viewHolder.feedContentTextView.setText(spannable);
                } else {
                    viewHolder.feedContentTextView.setText(feedModelListsOriginal.get(i).description);
                }

            }else{
                viewHolder.feedContentTextView.setText(feedModelListsOriginal.get(i).description);

            }

            //More button click event
            viewHolder.moreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (settingsDialog == null) {

                        settingsDialog = new Dialog(activity);
                        settingsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        settingsDialog.setContentView(R.layout.dialog_for_settings);
                        settingsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        settingsDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    }

                    LinearLayout shareFeedButton    = (LinearLayout)settingsDialog.findViewById(R.id.shareFeedButton);
                    LinearLayout hideFeedButton     = (LinearLayout)settingsDialog.findViewById(R.id.hideFeedButton);
                    LinearLayout unfollowFeedButton = (LinearLayout)settingsDialog.findViewById(R.id.unfollowFeedButton);

                    //Share image and text
                    shareFeedButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            settingsDialog.dismiss();

                            String imageToShare = feedModelListsOriginal.get(i).media; //Image You wants to share
                            String title = feedModelListsOriginal.get(i).description; //Title you wants to share
                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
                            shareIntent.setType("*/*");
                            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            shareIntent.putExtra(Intent.EXTRA_TEXT, imageToShare);
                            startActivity(Intent.createChooser(shareIntent, "Select App to Share Text and Image"));
                        }
                    });

                    //Hide feed
                    hideFeedButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if(isConnectingToInternet()){

                                settingsDialog.dismiss();

                                Map<String, String> params = new HashMap<>();
                                params.put("feed_id",feedModelListsOriginal.get(i).id);
                                params.put("block_type","feed");
                                showProgressDialog();
                                serverRequestwithHeader.createRequest(FeedScreen.this,params, RequestID.REQ_FEED_HIDE,"POST","");

                            }else{

                                noInternetAlertDialog();
                            }

                        }
                    });

                    //UnFollow shop
                    unfollowFeedButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if(feedModelListsOriginal.get(i).user_id.equalsIgnoreCase("1")){

                                toast("Sorry, you can't un-follow the RPay admin feeds");

                            }else{

                                if(isConnectingToInternet()){

                                    settingsDialog.dismiss();

                                    Map<String, String> params = new HashMap<>();
                                    params.put("feed_id",feedModelListsOriginal.get(i).id);
                                    params.put("block_type","");
                                    showProgressDialog();
                                    serverRequestwithHeader.createRequest(FeedScreen.this,params, RequestID.REQ_FEED_HIDE,"POST","");

                                }else{

                                    noInternetAlertDialog();
                                }
                            }


                        }
                    });

                    settingsDialog.setCancelable(true);
                    settingsDialog.show();

                }
            });


            viewHolder.feedContentTextView.setTag(i);
            viewHolder.feedImageView.setTag(i);
            viewHolder.feedTimeTextView.setTag(i);
            viewHolder.feedVideoLayout.setTag(i);
            viewHolder.moreButton.setTag(i);
            viewHolder.userNameTextView.setTag(i);
            viewHolder.webVideoView.setTag(i);


            return view;
        }

        //get youtube video code
        public String getYoutubeVideoId(String youtubeUrl)
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

        private class ViewHolder {

            private ImageView feedImageView;
            private TextView userNameTextView,feedTimeTextView,feedContentTextView;
            private ImageView moreButton;
            private RelativeLayout feedVideoLayout;
            private WebView webVideoView;
        }

        public void searchFilter(String filterText) {

            Log.e("filterText",filterText);
            Log.e("contactListsOriginal",""+feedModelListsOriginal.size());
            Log.e("contactListsDummy",""+feedModelListsDummy.size());

            mSearchText = filterText;
            feedDesSearchText = filterText;
            filterText = filterText.toLowerCase(Locale.getDefault());
            feedModelListsOriginal.clear();

            if (filterText.length() == 0) {
                feedModelListsOriginal.addAll(feedModelListsDummy);
            } else {
                for (FeedModelList.Data contactList : feedModelListsDummy) {
                    if (contactList.user.business_name.toLowerCase(Locale.getDefault()).contains(filterText) || contactList.description.toLowerCase(Locale.getDefault()).contains(filterText)) {
                        feedModelListsOriginal.add(contactList);
                    }
                }
                if (feedModelListsOriginal.size() == 0) {
                    toast("No feed found");
                }
            }

            notifyDataSetChanged();
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
        //Get Logged user details
        Utility.update_check = true;
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(Config.PUSH_NOTIFICATION));

    }
}
