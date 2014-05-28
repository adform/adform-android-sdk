package com.adform.sdk2.mraid.commands;

import com.adform.sdk2.mraid.MraidCommandFactory;
import com.adform.sdk2.view.inner.AdWebView;

import java.util.Map;

/**
 * Created by mariusm on 08/05/14.
 */
public class MraidCommandClose extends MraidBaseCommand {
    public MraidCommandClose(Map<String, String> params, AdWebView view) {
        super(params, view);
    }

    @Override
    public void execute() {
        mWebView.close();
    }
}