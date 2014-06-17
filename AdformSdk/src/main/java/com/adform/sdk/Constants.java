package com.adform.sdk;

/**
 * Created by mariusm on 28/04/14.
 */
public class Constants {
    // Refresh service constants
    //TODO mariusm 07/05/14 Fix this to the proper value
    public static final int REFRESH_SECONDS = 30;
    // Network
    // Public
    public static final String API_VERSION = "1.0";
    public static final String SERVER_URL = "http://adx.adform.net/";
    public static final String SDK_INFO_PATH = "adx/mobile/api/";
    // Test - ddwrt
//    public static final String SERVER_URL = "http://192.168.2.122/";
//    public static final String SDK_INFO_PATH = "mobilesdk/ad/";
    // Test - iTo
//    public static final String SERVER_URL = "http://192.168.4.72/";
//    public static final String SDK_INFO_PATH = "mobilesdk/ad/";
    public static final String SDK_INFO_PATH_ERROR = "mobilesdk/error.php";
    public static final String SDK_INFO_PATH_EMPTY = "mobilesdk/tricky.php";
//    public static final String TEMP_INTERSTITIAL_LINK = SERVER_URL+SDK_INFO_PATH+
//            "banner_interstitial.js";
//    public static final String TEMP_INTERSTITIAL_LINK = "http://track.adform.net/mobile/script/?bn=3919540";
    // View parameters
    public static final int PHONE_WIDTH = 320;
    public static final int PHONE_HEIGHT = 50;
    public static final int TABLET_WIDTH = 728;
    public static final int TABLET_HEIGHT = 90;
}
