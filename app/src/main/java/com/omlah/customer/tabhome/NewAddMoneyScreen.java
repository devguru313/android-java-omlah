package com.omlah.customer.tabhome;

import android.os.Bundle;

import com.omlah.customer.R;
import com.omlah.customer.base.BaseActivity;

/**
 * Created by admin on 01-12-2017.
 */

public class NewAddMoneyScreen extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.business_account_screen);
        showBackArrow();
        setActionBarTitle("Add Money");
    }
}
