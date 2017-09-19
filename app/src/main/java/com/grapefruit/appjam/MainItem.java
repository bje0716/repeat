package com.grapefruit.appjam;

import io.realm.RealmObject;

/**
 * Created by GrapeFruit on 2017-08-14.
 */

public class MainItem extends RealmObject {

    private boolean check;
    private String item;
    private String date;

    public void setCheck(boolean check) {
        this.check = check;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getDate() {
        return date;
    }

    public String getItem() {
        return item;
    }

    public boolean getCheck() {
        return check;
    }
}
