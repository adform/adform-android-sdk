package com.adform.sdk.mraid.commands;

import com.adform.sdk.utils.entities.ExpandProperties;
import com.adform.sdk.view.inner.AdWebView;

import java.util.Map;

/**
 * Created by mariusm on 08/05/14.
 */
public class MraidCommandExpand extends MraidBaseCommand {
    public MraidCommandExpand(Map<String, String> params, AdWebView view) {
        super(params, view);
    }

    @Override
    public void execute() {
        String url = getStringFromParamsForKey("url");
        int width = getIntFromParamsForKey("w");
        int height = getIntFromParamsForKey("h");
        boolean shouldUseCustomClose = getBooleanFromParamsForKey("shouldUseCustomClose");
        mWebView.getListener().onMraidExpand(url, new ExpandProperties(width, height, shouldUseCustomClose));
    }
}