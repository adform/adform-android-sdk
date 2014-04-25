package com.adform.sdk2.utils;

import android.util.Log;
import com.adform.sdk2.BuildConfig;

/**
 * Created by marius on 8/16/13.
 */
public class Utils {
    public static void p(String msg){
//        if(BuildConfig.DEBUG)
        Log.e("CoreAndroidLib", msg);
    }
//    public static void p(String msg, String... args){
//        if(BuildConfig.DEBUG)
//            Log.e("CoreAndroidLib", String.format(msg, args));
//    }
}
