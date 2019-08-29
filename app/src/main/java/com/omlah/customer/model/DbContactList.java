package com.omlah.customer.model;

import java.io.Serializable;

/**
 * Created by admin on 18-09-2017.
 */

public class DbContactList implements Serializable{

    String name;
    String number;
    String image;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
