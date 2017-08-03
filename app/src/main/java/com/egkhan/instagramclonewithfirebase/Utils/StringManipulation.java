package com.egkhan.instagramclonewithfirebase.Utils;

/**
 * Created by EgK on 8/2/2017.
 */

public class StringManipulation {
    public static String expandUsername(String username)
    {
        return username.replace("."," ");
    }
    public static String condenseUsername(String username)
    {
        return username.replace(" ",".");
    }
}
