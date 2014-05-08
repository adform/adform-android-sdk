package com.adform.sdk2.mraid.properties;

import android.content.Context;
import android.provider.Settings;
import com.adform.sdk2.resources.AdDimension;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by mariusm on 07/05/14.
 */
public abstract class MraidBaseProperty {
    @Override
    public String toString() {
        return toGetPair();
    }

    public abstract String toGetPair();

    public static String generatePropertiesToString(ArrayList<MraidBaseProperty> properties) {
        if (properties == null)
            return null;
        String path = "?";
        for (int i = 0; i < properties.size(); i++) {
            MraidBaseProperty property = properties.get(i);
            if (i != 0)
                path += "&";
            path += property.toGetPair();
        }
        return path;
    }

}





