package com.adform.sdk2.mraid.commands;

import com.adform.sdk2.mraid.MraidCommandFactory;
import com.adform.sdk2.view.inner.AdWebView;

import java.util.Map;

/**
 * Created by mariusm on 08/05/14.
 */
public class MraidCommandOpen extends MraidBaseCommand {
    public MraidCommandOpen(Map<String, String> params, AdWebView view) {
        super(params, view);
    }

    @Override
    public void execute() {
        String url = getStringFromParamsForKey("url");
        if (url == null) {
            mWebView.fireErrorEvent(MraidCommandFactory.MraidJavascriptCommand.OPEN, "Url can not be null.");
            return;
        }
        mWebView.open(url);
    }
}