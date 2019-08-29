package com.omlah.customer.tabfeed;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.omlah.customer.R;
import com.omlah.customer.base.BaseActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by admin on 20-10-2017.
 */

public class FeedVideoPlayer extends BaseActivity {

    //Create class objects
    private WebView webVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_videoplayer);
        hideActionBar();

        //Initialize xml objects
        webVideoView       = (WebView)findViewById(R.id.webVideoView);

        //get intent values
        Intent intent = getIntent();
        if(intent!=null){

            String uriPath = getYoutubeVideoId(intent.getStringExtra("URI")); //update package name

            webVideoView.getSettings().setJavaScriptEnabled(true);
            webVideoView.setWebChromeClient(new WebChromeClient() {

            } );
            webVideoView.loadData("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/"+uriPath+"\" frameborder=\"0\" allowfullscreen></iframe>", "text/html" , "utf-8" );
        }

    }

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
