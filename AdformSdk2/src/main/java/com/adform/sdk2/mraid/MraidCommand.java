package com.adform.sdk2.mraid;

import com.adform.sdk2.view.AdWebView;

import java.util.Map;

/**
 * Created by mariusm on 06/05/14.
 */
abstract class MraidCommand {
    protected static final String URI_KEY = "uri";
    protected Map<String, String> mParams;
    protected AdWebView mWebView;

    MraidCommand(Map<String, String> params, AdWebView view) {
        mParams = params;
        mWebView = view;
    }

    abstract void execute();

    protected int getIntFromParamsForKey(String key) {
        String s = mParams.get(key);
        if (s == null) return -1;
        else {
            try {
                return Integer.parseInt(s, 10);
            } catch (NumberFormatException e) {
                return -1;
            }
        }
    }

    protected String getStringFromParamsForKey(String key) {
        return mParams.get(key);
    }

    protected float getFloatFromParamsForKey(String key) {
        String s = mParams.get(key);
        if (s == null) return 0.0f;
        else {
            try {
                return Float.parseFloat(key);
            } catch (NumberFormatException e) {
                return 0.0f;
            }
        }
    }

    protected boolean getBooleanFromParamsForKey(String key) {
        return "true".equals(mParams.get(key));
    }

}

class MraidCommandOpen extends MraidCommand {
    MraidCommandOpen(Map<String, String> params, AdWebView view) {
        super(params, view);
    }

    @Override
    void execute() {
        String url = getStringFromParamsForKey("url");
        if (url == null) {
            mWebView.fireErrorEvent(MraidCommandFactory.MraidJavascriptCommand.OPEN, "Url can not be null.");
            return;
        }
        mWebView.open(url);
    }
}


