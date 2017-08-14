package com.brandon.manhunt;


public class User {
    private static User mUser;

    private double mLat;
    private double mLong;
    private double mHintLatitude;
    private double mHintLongitude;

    private String mEmail;
    private boolean mIsPlaying = false;

    public static User getInstance(){
        if (mUser == null){
            mUser = new User();
        }
        return mUser;
    }

    public User(String email, double lat, double Long, double hintLat, double hintLong) {

        mEmail = email;
        mLat = lat;
        mLong = Long;
        mHintLatitude = hintLat;
        mHintLongitude = hintLong;
    }

    public User()
    {
        mEmail = null;
        mLat = 0.0;
        mLong = 0.0;
        mHintLatitude = 0.0;
        mHintLongitude = 0.0;
    }

    public String getEmail(){
        return mEmail;
    }

    public double getLat() {
        return mLat;
    }

    public double getLong() {
        return mLong;
    }

    public void setEmail(String email){
        mEmail = email.replace("@", "at").replace(".", "dot");
    }

    public double getHintLat(){
        return mHintLatitude;
    }

    public double getHintLong(){
        return mHintLongitude;
    }

    public void setIsPlaying(boolean isPlaying){
        mIsPlaying = isPlaying;
    }

    public boolean isPlaying(){
        return mIsPlaying;
    }

}

