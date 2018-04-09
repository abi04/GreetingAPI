package com.example.pojo;

import java.util.Date;

/**
 * Created by Abi on 4/7/18.
 */
public class Token {

    public String name;
    public Date expirationTime;

    public void setUserID(String name) {
        this.name = name;
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }
}
