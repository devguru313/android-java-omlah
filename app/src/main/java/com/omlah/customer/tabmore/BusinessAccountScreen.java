package com.omlah.customer.tabmore;

import android.os.Bundle;

import com.omlah.customer.R;
import com.omlah.customer.base.BaseActivity;

/**
 * Created by admin on 09-11-2017.
 */

public class BusinessAccountScreen extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.business_account_screen);
        showBackArrow();
        setActionBarTitle("Business Account");
    }
}
