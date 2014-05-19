package com.adform.sdk2.mraid.properties;

import java.util.HashMap;

/**
 * Created by mariusm on 08/05/14.
 * A property that holds API version
 */
public class MraidCustomProperty extends MraidBaseProperty {
    private final HashMap<String, String> mCustomParams;

    MraidCustomProperty(HashMap<String, String> customParams) {
        mCustomParams = customParams;
    }

    public static MraidCustomProperty createWithCustomParams(HashMap<String, String> customParams) {
        return new MraidCustomProperty(customParams);
    }

    @Override
    public String toGet() {
        StringBuilder paramsFormedToGet = new StringBuilder();
        for (String key : mCustomParams.keySet()) {
            paramsFormedToGet.append(key+"="+mCustomParams.get(key)+"&");
        }

        if (mCustomParams.size() > 0 && paramsFormedToGet.charAt(paramsFormedToGet.length()-1) == "&".charAt(0))
            paramsFormedToGet.deleteCharAt(paramsFormedToGet.length()-1);
        return paramsFormedToGet.toString();
    }
    @Override
    public String toJson() {
        return null;
    }
}
