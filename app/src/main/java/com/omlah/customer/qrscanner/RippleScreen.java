package com.omlah.customer.qrscanner;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.omlah.customer.R;
import com.omlah.customer.base.BaseActivity;
import com.omlah.customer.common.RippleBackground;

/**
 * Created by Admin on 23-03-2018.
 */

public class RippleScreen extends BaseActivity{

    TextView button;
    ImageView closeImageView;
    RelativeLayout contentLayout;

    SharedPreferences prfs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ripple_screen);
        hideActionBar();

        button = (TextView)findViewById(R.id.button);
        closeImageView = (ImageView)findViewById(R.id.closeImageView);
        contentLayout = (RelativeLayout)findViewById(R.id.contentLayout);

        prfs = getSharedPreferences("APP_THEME", Context.MODE_PRIVATE);

        RippleBackground rippleBackground=(RippleBackground)findViewById(R.id.content);
        rippleBackground.startRippleAnimation();

        Intent intent = getIntent();
        if(intent!=null){

            if(intent.getStringExtra("type").equalsIgnoreCase("PAY")){
                button.setText(getResources().getString(R.string.PAYNOW));
            }else{
                button.setText(getResources().getString(R.string.RECEIVE));
            }

            if(intent.getStringExtra("screen").equalsIgnoreCase("kid")){

                setStatusBarGradiant(this);

                String Astatus = prfs.getString("theme", "");
                if(Astatus.equalsIgnoreCase("one")){
                    contentLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient_file_one));
                }else if(Astatus.equalsIgnoreCase("two")){
                    contentLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient_file_two));
                }else if(Astatus.equalsIgnoreCase("three")){
                    contentLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient_file_three));
                }else if(Astatus.equalsIgnoreCase("four")){
                    contentLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient_file_four));
                }else{
                    contentLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient_file_one));
                }

            }

        }

        closeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setStatusBarGradiant(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();

            Drawable background;

            String Astatus = prfs.getString("theme", "");
            if(Astatus.equalsIgnoreCase("one")){
                background = activity.getResources().getDrawable(R.drawable.gradient_file_one);
            }else if(Astatus.equalsIgnoreCase("two")){
                background = activity.getResources().getDrawable(R.drawable.gradient_file_two);
            }else if(Astatus.equalsIgnoreCase("three")){
                background = activity.getResources().getDrawable(R.drawable.gradient_file_three);
            }else if(Astatus.equalsIgnoreCase("four")){
                background = activity.getResources().getDrawable(R.drawable.gradient_file_four);
            }else{
                background = activity.getResources().getDrawable(R.drawable.gradient_file_one);
            }


            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
        }
    }
}
