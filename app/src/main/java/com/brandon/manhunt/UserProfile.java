package com.brandon.manhunt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by brandoncole on 7/20/17.
 */

public class UserProfile {

    private String mEmail, mPassword, mDisplayName, mUniqueID;
    private Map<String, String> mFriendList;


    public UserProfile(String mEmail, String mPassword, String mDisplayName,
                       String mUniqueID) {
        this.mEmail = mEmail;
        this.mPassword = mPassword;
        this.mDisplayName = mDisplayName;
        this.mUniqueID = mUniqueID;
        mFriendList = new HashMap<String, String>();
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getmPassword() {
        return mPassword;
    }

    public void setmPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public String getmDisplayName() {
        return mDisplayName;
    }

    public void setmDisplayName(String mDisplayName) {
        this.mDisplayName = mDisplayName;
    }

    public String getmUniqueID() {
        return mUniqueID;
    }

    public void setmUniqueID(String mUniqueID) {
        this.mUniqueID = mUniqueID;
    }

    public Map<String, String> getmFriendList() {
        return mFriendList;
    }

    public void addFriend(String UniqueId, String DisplayName){
        mFriendList.put(UniqueId, DisplayName);
    }

    public void removeFriend(String UniqueId){
        mFriendList.remove(UniqueId);
    }

    public boolean isFriend(String UniqueId){
        return mFriendList.containsKey(UniqueId);
    }
}
