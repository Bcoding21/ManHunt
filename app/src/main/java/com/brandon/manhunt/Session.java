package com.brandon.manhunt;

import java.util.List;
import java.util.Map;

/**
 * Created by brandoncole on 7/24/17.
 */

public class Session {

    private enum Role {Hunter, Hunted};


    private class UserInfo{

    }

    String mSessionId;
    Map<String, String> mLocations;

    String mHuntedLocation;
}
