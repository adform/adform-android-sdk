package com.adform.sdk2.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import com.adform.sdk2.BuildConfig;
import com.adform.sdk2.Constants;
import com.adform.sdk2.view.CoreAdView;

/**
 * Created by marius on 8/16/13.
 */
public class Utils {
    public static void p(String msg){
//        if(BuildConfig.DEBUG)
        Log.e("AdformSdk", msg);
    }
//    public static void p(String msg, String... args){
//        if(BuildConfig.DEBUG)
//            Log.e("CoreAndroidLib", String.format(msg, args));
//    }

    /**
     * Defines device type.
     * Source: http://stackoverflow.com/questions/5832368/tablet-or-phone-android
     * @param context provided context
     * @return true if table, false otherwise
     */
    public static boolean isTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }

    /**
     * Return height by device type. Scale is defined in {@link com.adform.sdk2.Constants}
     * @param context provided context
     * @return height of device ad banner
     */
    public static float getHeightDeviceType(Context context){
        return (isTablet(context))?Constants.TABLET_HEIGHT:Constants.PHONE_HEIGHT;
    }

    /**
     * Return width by device type. Scale is defined in {@link com.adform.sdk2.Constants}
     * @param context provided context
     * @return width of device ad banner
     */
    public static float getWidthDeviceType(Context context){
        return (isTablet(context))?Constants.TABLET_WIDTH:Constants.PHONE_WIDTH;
    }

    /**
     * *** Note this is usable only when CoreAdView is initialized ***
     * Coverts input pixels to density independent pixels.
     * @param pixels provided pixels
     * @return density independent pixels
     */
    public static int pxToDp(int pixels) {
        return (int) Math.ceil(pixels / CoreAdView.sDeviceDensity);
    }
}
