package com.adform.sdk.mraid.commands;

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
        mWebView.getListener().onMraidExpand();
    }
}