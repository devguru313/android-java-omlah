package com.omlah.customer.base;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.omlah.customer.R;
import com.omlah.customer.account.GetStartedScreen;
import com.omlah.customer.account.SignUpScreen;
import com.omlah.customer.common.LoginSession;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LanguageSelectionScreen extends BaseActivity {

    LoginSession loginSession;

    @BindView(R.id.englishSetButton)RelativeLayout englishSetButton;
    @BindView(R.id.arabicSetButton)RelativeLayout arabicSetButton;
    @BindView(R.id.portugueseButton)RelativeLayout portugueseButton;
    @BindView(R.id.hindiButton)RelativeLayout hindiButton;
    @BindView(R.id.englishTickImageView)ImageView englishTickImageView;
    @BindView(R.id.arabicTickImageView)ImageView arabicTickImageView;
    @BindView(R.id.portugueseTickImageView)ImageView portugueseTickImageView;
    @BindView(R.id.hindiTickImageView)ImageView hindiTickImageView;
    @BindView(R.id.goButton)ImageView goButton;

    String type="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.language_screen);

        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();

        hideActionBar();

        ButterKnife.bind(this);
        loginSession = LoginSession.getInstance(this);

        Intent viewintent = getIntent();
        if(viewintent!=null){
             type = viewintent.getStringExtra("type");
        }

        if(Locale.getDefault().getDisplayLanguage().equalsIgnoreCase("English")){
            englishTickImageView.setVisibility(View.VISIBLE);
            arabicTickImageView.setVisibility(View.INVISIBLE);
            portugueseTickImageView.setVisibility(View.INVISIBLE);
            hindiTickImageView.setVisibility(View.INVISIBLE);

        }else{
           // englishTickImageView.setVisibility(View.INVISIBLE);
            arabicTickImageView.setVisibility(View.INVISIBLE);
            portugueseTickImageView.setVisibility(View.INVISIBLE);
            hindiTickImageView.setVisibility(View.INVISIBLE);
        }
        if(Locale.getDefault().getDisplayLanguage().equalsIgnoreCase("Arabic")){
           arabicTickImageView.setVisibility(View.VISIBLE);
            englishTickImageView.setVisibility(View.INVISIBLE);
            //arabicTickImageView.setVisibility(View.INVISIBLE);
            portugueseTickImageView.setVisibility(View.INVISIBLE);
            hindiTickImageView.setVisibility(View.INVISIBLE);

        }else{
             englishTickImageView.setVisibility(View.INVISIBLE);
            //arabicTickImageView.setVisibility(View.INVISIBLE);
            portugueseTickImageView.setVisibility(View.INVISIBLE);
            hindiTickImageView.setVisibility(View.INVISIBLE);
        }
        if(Locale.getDefault().getDisplayLanguage().equalsIgnoreCase("Portuguese")){
            portugueseTickImageView.setVisibility(View.VISIBLE);
            englishTickImageView.setVisibility(View.INVISIBLE);
            arabicTickImageView.setVisibility(View.INVISIBLE);
            // portugueseTickImageView.setVisibility(View.INVISIBLE);
            hindiTickImageView.setVisibility(View.INVISIBLE);

        }else{
            englishTickImageView.setVisibility(View.INVISIBLE);
            arabicTickImageView.setVisibility(View.INVISIBLE);
            // portugueseTickImageView.setVisibility(View.INVISIBLE);
            hindiTickImageView.setVisibility(View.INVISIBLE);
        }
        if(Locale.getDefault().getDisplayLanguage().equalsIgnoreCase("Hindi")){
           hindiTickImageView.setVisibility(View.VISIBLE);
            englishTickImageView.setVisibility(View.INVISIBLE);
            arabicTickImageView.setVisibility(View.INVISIBLE);
            portugueseTickImageView.setVisibility(View.INVISIBLE);

        }else{
            englishTickImageView.setVisibility(View.INVISIBLE);
            arabicTickImageView.setVisibility(View.INVISIBLE);
             portugueseTickImageView.setVisibility(View.INVISIBLE);
           // hindiTickImageView.setVisibility(View.INVISIBLE);
        }
       /* switch(3)
        {
            case 0:
                Locale.getDefault().getDisplayLanguage().equalsIgnoreCase("English");
                englishTickImageView.setVisibility(View.VISIBLE);
                arabicTickImageView.setVisibility(View.INVISIBLE);
                portugueseTickImageView.setVisibility(View.INVISIBLE);
                hindiTickImageView.setVisibility(View.INVISIBLE);
                break;
            case 1:
                Locale.getDefault().getDisplayLanguage().equalsIgnoreCase("Arabic");
                arabicTickImageView.setVisibility(View.VISIBLE);
                portugueseTickImageView.setVisibility(View.INVISIBLE);
                hindiTickImageView.setVisibility(View.INVISIBLE);
                englishTickImageView.setVisibility(View.INVISIBLE);
                break;
            case 2:
                Locale.getDefault().getDisplayLanguage().equalsIgnoreCase("Portuguese");
                portugueseTickImageView.setVisibility(View.VISIBLE);
                arabicTickImageView.setVisibility(View.INVISIBLE);
                hindiTickImageView.setVisibility(View.INVISIBLE);
                englishTickImageView.setVisibility(View.INVISIBLE);
                break;
            case 3:
                Locale.getDefault().getDisplayLanguage().equalsIgnoreCase("Hindi");
                hindiTickImageView.setVisibility(View.VISIBLE);
                portugueseTickImageView.setVisibility(View.INVISIBLE);
                arabicTickImageView.setVisibility(View.INVISIBLE);
                englishTickImageView.setVisibility(View.INVISIBLE);
                break;

        }*/
        englishSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLanguage("en");
                englishTickImageView.setVisibility(View.VISIBLE);
                arabicTickImageView.setVisibility(View.INVISIBLE);
                portugueseTickImageView.setVisibility(View.INVISIBLE);
                hindiTickImageView.setVisibility(View.INVISIBLE);
            }
        });

        arabicSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLanguage("ar");
                arabicTickImageView.setVisibility(View.VISIBLE);
                portugueseTickImageView.setVisibility(View.INVISIBLE);
                hindiTickImageView.setVisibility(View.INVISIBLE);
                englishTickImageView.setVisibility(View.INVISIBLE);
            }
        });
        portugueseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLanguage("por");
                portugueseTickImageView.setVisibility(View.VISIBLE);
                englishTickImageView.setVisibility(View.INVISIBLE);
                hindiTickImageView.setVisibility(View.INVISIBLE);
                arabicTickImageView.setVisibility(View.INVISIBLE);
            }
        });
        hindiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLanguage("hi");
                hindiTickImageView.setVisibility(View.VISIBLE);
                englishTickImageView.setVisibility(View.INVISIBLE);
                arabicTickImageView.setVisibility(View.INVISIBLE);
                portugueseTickImageView.setVisibility(View.INVISIBLE);
            }
        });


        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(type.equalsIgnoreCase("LOGIN")){

                    loginSession.saveIntro("yes");
                    Intent intent = new Intent(LanguageSelectionScreen.this, GetStartedScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }else{
                    loginSession.saveIntro("yes");
                    Intent intent = new Intent(LanguageSelectionScreen.this, SignUpScreen.class);
                    intent.putExtra("firstName", "");
                    intent.putExtra("lastName", "");
                    intent.putExtra("email", "");
                    intent.putExtra("phoneCode","+251");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }

            }
        });
    }
}
