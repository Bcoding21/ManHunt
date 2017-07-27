package com.brandon.manhunt;

import java.util.List;
import java.util.Map;

/**
 * Created by brandoncole on 7/26/17.
 */

public class game_session {

    private String mSession_id;
    private boolean mGame_in_progress;

    List<String> mPeople;
    Map<String, String> mLocations;

    public String getmSession_id() {
        return mSession_id;
    }

    public void setmSession_id(String mSession_id) {
        this.mSession_id = mSession_id;
    }

    public boolean ismGame_in_progress() {
        return mGame_in_progress;
    }

    public void setmGame_in_progress(boolean mGame_in_progress) {
        this.mGame_in_progress = mGame_in_progress;
    }


    public void addPlayer(String player_name){
        mPeople.add(player_name);
    }





}
