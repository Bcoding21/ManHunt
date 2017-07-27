package com.brandon.manhunt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by brandoncole on 7/26/17.
 */

public class gameSession {
    private String mSession_id;
    private boolean mGame_in_progress;
    private List<String> mPlayers;
    private Map<String, String> mPlayerLocations;

    public gameSession(String sessionId){
        mSession_id = sessionId;
        mGame_in_progress = false;
        mPlayers = new ArrayList<String>();
        mPlayerLocations = new HashMap<String, String>();
    }

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
        mPlayers.add(player_name);
        mPlayerLocations.put(player_name, "");
    }

    public void removePlayer(String player_name){
        mPlayers.remove(player_name);
        mPlayerLocations.remove(player_name);
    }

}
