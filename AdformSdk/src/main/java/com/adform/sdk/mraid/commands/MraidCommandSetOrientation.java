package com.adform.sdk.mraid.commands;

import com.adform.sdk.mraid.MraidCommandFactory;
import com.adform.sdk.utils.AdformEnum;
import com.adform.sdk.view.inner.AdWebView;

import java.util.Map;

/**
 * Created by mariusm on 08/05/14.
 */
public class MraidCommandSetOrientation extends MraidBaseCommand {
    public MraidCommandSetOrientation(Map<String, String> params, AdWebView view) {
        super(params, view);
    }

    @Override
    public void execute() {
        boolean allowOrientationChange = getBooleanFromParamsForKey("allowOrientationChange");
        AdformEnum.ForcedOrientation forcedOrientation =
                AdformEnum.ForcedOrientation.parseType(getStringFromParamsForKey("forceOrientation"));
        if (forcedOrientation == AdformEnum.ForcedOrientation.UNKNOWN) {
            mWebView.fireErrorEvent(MraidCommandFactory.MraidJavascriptCommand.SET_ORIENTATION, "Undefined orientation");
            return;
        }
        mWebView.getListener().onMraidSetOrientation(allowOrientationChange, forcedOrientation);
    }
}