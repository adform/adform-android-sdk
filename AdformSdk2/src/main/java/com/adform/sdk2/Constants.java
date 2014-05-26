package com.adform.sdk2;

import android.content.ContentValues;

/**
 * Created by mariusm on 28/04/14.
 */
public class Constants {
    // Refresh service constants
    //TODO mariusm 07/05/14 Fix this to the proper value
    public static final int REFRESH_SECONDS = 30;
    // Network
//    public static final String SERVER_URL = "http://37.157.0.44/";
    public static final String SERVER_URL = "http://192.168.2.119/";
//    public static final String SERVER_URL = "http://192.168.1.156/";
    public static final String SDK_INFO_PATH = "mobilesdk/ad/";
    public static final String SDK_INFO_PATH_ERROR = "mobilesdk/error.php";
    public static final String SDK_INFO_PATH_EMPTY = "mobilesdk/tricky.php";
    // View parameters
    public static final int PHONE_WIDTH = 320;
    public static final int PHONE_HEIGHT = 50;
    public static final int TABLET_WIDTH = 728;
    public static final int TABLET_HEIGHT = 90;
}
