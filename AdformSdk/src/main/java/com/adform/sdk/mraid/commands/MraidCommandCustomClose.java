package com.adform.sdk.mraid.commands;

import com.adform.sdk.mraid.MraidCommandFactory;
import com.adform.sdk.utils.AdformEnum;
import com.adform.sdk.view.inner.AdWebView;

import java.util.Map;

/**
 * Created by mariusm on 08/05/14.
 */
public class MraidCommandCustomClose extends MraidBaseCommand {
    public MraidCommandCustomClose(Map<String, String> params, AdWebView view) {
        super(params, view);
    }

    @Override
    public void execute() {
        boolean shouldUseCustomClose = getBooleanFromParamsForKey("shouldUseCustomClose");
        mWebView.getListener().onMraidUseCustomClose(shouldUseCustomClose);
    }
}