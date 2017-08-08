package com.brandon.manhunt;


public class User {
    private String mDisplayName;
    private double mLat;
    private double mLong;
    private static User mUser;
    private String mEmail;
    private boolean mIsHunted;

    public User(String mDisplayName, double lat, double Long) {
        this.mDisplayName = mDisplayName;
        mLat = lat;
        mLong = Long;
    }

    public static User getInstance(){
        if (mUser == null){
            mUser = new User();
        }
        return mUser;
    }

    public User()
    {
        mDisplayName = null;
        mLat = 0.0;
        mLong = 0.0;
    }

    public String getEmail(){
        return mEmail;
    }

    public void setIsHunted(boolean truth){
        mIsHunted = truth;
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

    public void setName(String name){
        mDisplayName = name;
    }

    public void setEmail(String email){
        mEmail = email.replace("@", "at").replace(".", "dot");
    }

    public boolean isHunted(){
        return mIsHunted;
    }
}
