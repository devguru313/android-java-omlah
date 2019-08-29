package com.omlah.customer.tabmore.smartcard;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.omlah.customer.R;
import com.omlah.customer.base.BaseActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 01-11-2017.
 */

public class SmartCardScreen extends BaseActivity{

    //Create xml objects
    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.backIconImageView)ImageView backIconImageView;
    @BindView(R.id.activateButton)Button activateButton;
    @BindView(R.id.activateNowButton)Button activateNowButton;
    @BindView(R.id.smartcardText)TextView smartcardText;
    @BindView(R.id.smartCardNeedListView)ListView smartCardNeedListView;
    @BindView(R.id.needTextLayout)LinearLayout needTextLayout;
    @BindView(R.id.cardNumberLayout)LinearLayout cardNumberLayout;
    @BindView(R.id.detailsLayout)LinearLayout detailsLayout;
    @BindView(R.id.cardNumberEditText)EditText cardNumberEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smart_card_screen);
        hideActionBar();

        //Initialize xml file
        ButterKnife.bind(this);

        //cardNumberEditText.addTextChangedListener(new CreditCardNumberFormattingTextWatcher());

        //Create list
        ArrayList<String> strings =  new ArrayList<>();
        strings.add("Your Smart Card works like your regular bank card and more.");
        strings.add("To Use your rewards points to enter the popnation events.");
        strings.add("Make your Smart Card much more secure than your existing bank card.");
        strings.add("Paying with a Smart card makes it easier to avoid losses from fraud");


        SmartCardAdapter smartCardAdapter = new SmartCardAdapter(this,strings);
        smartCardNeedListView.setAdapter(smartCardAdapter);
        smartCardAdapter.notifyDataSetChanged();


        backIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });


        activateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                needTextLayout.setVisibility(View.GONE);
                cardNumberLayout.setVisibility(View.VISIBLE);

            }
        });

        activateNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String cardNumber = cardNumberEditText.getText().toString();

                if(cardNumber.isEmpty()){

                    toast("please enter smart card number");

                }else{

                    cardNumberLayout.setVisibility(View.GONE);
                    detailsLayout.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    public static class CreditCardNumberFormattingTextWatcher implements TextWatcher {

        private boolean lock;

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (lock || s.length() > 10) {
                return;
            }
            lock = true;
            for (int i = 4; i < s.length(); i += 5) {
                if (s.toString().charAt(i) != ' ') {
                    s.insert(i, " ");
                }
            }
            lock = false;
        }
    }

    private class SmartCardAdapter extends BaseAdapter{

        Activity activity;
        ArrayList<String>stringArrayList;
        private LayoutInflater inflater;

        public SmartCardAdapter(Activity activity, ArrayList<String> stringArrayList) {
            this.activity = activity;
            this.stringArrayList = stringArrayList;
            this.inflater = LayoutInflater.from(activity);
        }

        @Override
        public int getCount() {
            return stringArrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return stringArrayList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            if (view == null)
                view = inflater.inflate(R.layout.custom_smartcard_list,null);

            TextView needTextView = (TextView)view.findViewById(R.id.needTextView);

            needTextView.setText(stringArrayList.get(i));

            return view;

        }
    }
}
