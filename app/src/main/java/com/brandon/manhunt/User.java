package com.brandon.manhunt;

/**
 * Created by gdriver on 8/3/17.
 */

public class User {
    private String mDisplayName;
    private double mLat;
    private double mLong;

    public User(String mDisplayName, double lat, double Long) {
        this.mDisplayName = mDisplayName;
        mLat = lat;
        mLong = Long;
    }

    public User()
    {
        mDisplayName = null;
        mLat = 0.0;
        mLong = 0.0;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public double getLat() {
        return mLat;
    }

    public double getLong() {
        return mLong;
    }
}
