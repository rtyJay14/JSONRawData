package com.directions.myapplication;

/**
 * Created by rtyJa on 01/08/2017.
 */

public class Account {

    private String userid;
    private String password;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "[userid=" + userid + ", password=" + password + "]";
    }

}
