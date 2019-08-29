package com.omlah.customer.account;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.omlah.customer.R;
import com.omlah.customer.base.BaseActivity;
import com.omlah.customer.common.LoginSession;
import com.omlah.customer.model.CountryList;
import com.omlah.customer.service.RequestID;
import com.omlah.customer.service.ServerListener;
import com.omlah.customer.service.ServerRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by admin on 26-09-2017.
 */

public class CountrySelectScreen extends BaseActivity implements ServerListener{

    //Create model classes
    private LoginSession loginSession;
    private ServerRequest serverRequest;
    private ListAdapter adapter;

    //Create xml file
    ListView countryListView;
    Toolbar toolbar;
    EditText countryEditText;
    ImageView backIconImageView;
    Button acceptButton;

    ArrayList<String> countryNameList = new ArrayList<>();
    HashMap<String,String> countryIDList = new HashMap<>();
    HashMap<String,String> countryPhoneCodeList = new HashMap<>();

    //String create
    String mSearchText,countryName ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.country_screen);
        hideActionBar();

        //Initialize xml file
        toolbar            = (Toolbar)findViewById(R.id.toolbar);
        countryListView    = (ListView) findViewById(R.id.countryListView);
        countryEditText    = (EditText) findViewById(R.id.countryEditText);
        backIconImageView  = (ImageView) findViewById(R.id.backIconImageView);
        acceptButton       = (Button) findViewById(R.id.acceptButton);

        //Initialize class objects
        serverRequest      = ServerRequest.getInstance(this);
        loginSession       = LoginSession.getInstance(this);

        //Get Country list
        if(!isConnectingToInternet()){
            noInternetAlertDialog();
        }else{

            countryNameList.clear();
            final Map<String, String> param = new HashMap<String, String>();
            showProgressDialog();
            serverRequest.createRequest(CountrySelectScreen.this,param,RequestID.REQ_COUNTRYLIST,"GET","");
        }


        backIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        //filter method
        countryEditText.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try {

                    String filter_text = countryEditText.getText().toString().trim().toLowerCase(Locale.getDefault());
                    adapter.searchFilter(filter_text);

                } catch (Exception e) {

                    e.printStackTrace();

                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void afterTextChanged(Editable s) {

            }
        });

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                countryName = adapter.getSelectedItem();
                if(!countryName.isEmpty()){

                    Intent intent = new Intent();
                    intent.putExtra("editTextValue", countryName);
                    intent.putExtra("countryID", countryIDList.get(countryName));
                    intent.putExtra("countryPhoneCode", countryPhoneCodeList.get(countryName));
                    setResult(RESULT_OK, intent);
                    finish();

                }else{

                    toast(getResources().getString(R.string.SearchCountryName));
                }
            }
        });
    }

    private class ListAdapter extends BaseAdapter {

        private Context context;
        ArrayList<String> dataList;
        ArrayList<String> arrayList;
        private LayoutInflater inflater;
        private int selectedPosition = -1;

        public ListAdapter(Context context, ArrayList<String> arrayList) {
            this.context = context;
            this.dataList = new ArrayList<String>();
            this.dataList.addAll(arrayList);
            this.arrayList = arrayList;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return arrayList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            ViewHolder viewHolder;
            if (view == null) {
                viewHolder = new ViewHolder();

                //inflate the layout on basis of boolean
                view = inflater.inflate(R.layout.list_custom_row_layout, viewGroup, false);

                viewHolder.label = (TextView) view.findViewById(R.id.label);
                viewHolder.radioButton = (RadioButton) view.findViewById(R.id.radio_button);

                view.setTag(viewHolder);
            } else
                viewHolder = (ViewHolder) view.getTag();

            viewHolder.label.setText(arrayList.get(i));

            //check the radio button if both position and selectedPosition matches
            viewHolder.radioButton.setChecked(i == selectedPosition);

            //Set the position tag to both radio button and label
            viewHolder.radioButton.setTag(i);
            viewHolder.label.setTag(i);

            viewHolder.radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemCheckChanged(v);
                }
            });

            viewHolder.label.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemCheckChanged(v);
                }


            });


            if (mSearchText != null && !mSearchText.isEmpty()) {
                int startPos = arrayList.get(i).toLowerCase(Locale.US).indexOf(mSearchText.toLowerCase(Locale.US));
                int endPos = startPos + mSearchText.length();

                if (startPos != -1) {
                    Spannable spannable = new SpannableString(arrayList.get(i));
                    ColorStateList blueColor = new ColorStateList(new int[][]{new int[]{}}, new int[]{context.getResources().getColor(R.color.colorPrimary)});
                    TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.BOLD, -1, blueColor, null);
                    spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    viewHolder.label.setText(spannable);
                } else {
                    viewHolder.label.setText(arrayList.get(i));
                }
            } else {
                viewHolder.label.setText(arrayList.get(i));
            }


            return view;
        }

        private void itemCheckChanged(View v) {
            selectedPosition = (Integer) v.getTag();
            notifyDataSetChanged();
        }

        //Return the selectedPosition item
        public String getSelectedItem() {
            if (selectedPosition != -1) {
                return arrayList.get(selectedPosition);
            }
            return "";
        }

        private class ViewHolder {
            private TextView label;
            private RadioButton radioButton;
        }

        public void searchFilter(String filter_text) {

            mSearchText = filter_text;
            filter_text = filter_text.toLowerCase(Locale.getDefault());
            arrayList.clear();

            if (filter_text.length() == 0) {
                arrayList.addAll(dataList);
            } else {
                for (String restaurantss : dataList) {
                    if (restaurantss.toLowerCase(Locale.getDefault()).contains(filter_text)) {
                        arrayList.add(restaurantss);
                    }
                }
                if (arrayList.size() == 0) {
                    toast(getResources().getString(R.string.NoCountryFound));
                }
            }

            notifyDataSetChanged();
        }
    }

    ///////////////////////////////////// SERVER RESPONSE HANDLE ///////////////////////////////////
    @Override
    public void onSuccess(Object result, RequestID requestID) {

        hideProgressDialog();

        CountryList countryList = (CountryList)result;

        ArrayList<CountryList.CountriesList>countries = countryList.data.countryList;

        for(CountryList.CountriesList country : countries){

            countryNameList.add(country.country_name);
            countryIDList.put(country.country_name,country.id);
            countryPhoneCodeList.put(country.country_name,country.phone_code);
        }

        adapter = new ListAdapter(this, countryNameList);
        countryListView.setAdapter(adapter);

    }

    @Override
    public void onFailure(String error, RequestID requestID) {

        hideProgressDialog();
        toast(error);
    }

}
