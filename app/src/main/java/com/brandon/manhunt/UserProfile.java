package com.brandon.manhunt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by brandoncole on 7/20/17.
 */

public class UserProfile {

    private String mEmail;
    private Map<String, String> mFriendList;

    public UserProfile(){
        mEmail = "none";
        mFriendList = new HashMap<String, String>();
    }

    public UserProfile(String email){
        mEmail = email;
    }

    public void setEmail(String email){
        mEmail = email;
    }

    public String getEmail(){
        return mEmail;
    }

    public Map<String, String> getFriends() {
        return mFriendList;
    }

    public void addFriend(String UniqueId, String DisplayName){
        mFriendList.put(UniqueId, DisplayName);
    }

    public void removeFriends(String UniqueId){
        mFriendList.remove(UniqueId);
    }

    public boolean isFriend(String UniqueId){
        return mFriendList.containsKey(UniqueId);
    }
}
