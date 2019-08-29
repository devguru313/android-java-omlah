package com.omlah.customer.common;

import android.app.Activity;


public class Utility {

    public static Utility utility = null;
    Activity activity;


    public static Object COUNTRY_LIST = "";

    public static Utility getInstance(Activity activity) {
        utility = new Utility(activity);


        return utility;
    }

    /**********************************Constructor*************************************/
    public Utility(Activity activity) {
        super();
        this.activity = activity;

    }
}
