package com.omlah.customer.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.omlah.customer.R;
import com.omlah.customer.model.CountryList;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CountryAdapter extends BaseAdapter {
    Context context;
    ArrayList<CountryList.CountriesList>countriesLists;
    LayoutInflater inflter;

    public CountryAdapter(Context applicationContext, ArrayList<CountryList.CountriesList>countriesLists) {
        this.context = applicationContext;
        this.countriesLists = countriesLists;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return countriesLists.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.simple_list_item, null);
        ImageView countryImageView = (ImageView) view.findViewById(R.id.countryImageView);
        TextView countryCodeTextView = (TextView) view.findViewById(R.id.countryCodeTextView);
        if(!countriesLists.get(i).country_flag.isEmpty()){
            Picasso.with(context).load(countriesLists.get(i).country_flag).into(countryImageView);
        }
        countryCodeTextView.setText(countriesLists.get(i).phone_code);
        return view;
    }
}
