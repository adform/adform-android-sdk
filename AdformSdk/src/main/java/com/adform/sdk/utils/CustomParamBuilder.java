package com.adform.sdk.utils;

import java.util.HashMap;

/**
 * Created by mariusm on 05/06/14.
 */
public class CustomParamBuilder {
    private HashMap<String, String> mCustomParams;
    public CustomParamBuilder() {
        mCustomParams = new HashMap<String, String>();
    }

    public CustomParamBuilder addCustomParam(String key, String value) {
        if (key != null)
            mCustomParams.put(key, value);
        return this;
    }

    public HashMap<String, String> buildParams() {
        return mCustomParams;
    }

}
