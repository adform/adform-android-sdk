package com.adform.sdk2.utils;

import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import com.adform.sdk2.view.AdWebView;

/**
 * Created by mariusm on 30/04/14.
 */
public class MraidBridge {
    public static final String MRAID_JS_INTERFACE = "AndroidMRaid";

    public static final String StateLoading = "loading";
    public static final String StateDefault = "default";
    public static final String StateExpanded = "expanded";
    public static final String StateResized = "resized";
    public static final String StateHidden = "hidden";

    public static final String EventReady = "ready";

    public enum State {
        LOADING,
        DEFAULT,
        EXPANDED,
        RESIZED,
        HIDDEN,
    }

    public AdWebView mWebView;
    public MraidHandler handler;

    public MraidBridge(MraidHandler handler) {
        if (handler == null)
            throw new IllegalArgumentException("handler null");
        this.handler = handler;
    }

    public void setWebView(AdWebView webView) {
        mWebView = webView;
        webView.addJavascriptInterface(this, MRAID_JS_INTERFACE);
    }

    public void sendErrorMessage(String message, String action) {
        String script = "mraid.fireErrorEvent('" + message + "','" + action + "');";
        mWebView.injectJavascript(script);
    }
    private State state = State.LOADING;
    /**
     * @return Configured bridge state.
     */
    public State getState()
    {
        return state;
    }

    public void setState(State state) {
        this.state = state;

        String stateString = StateLoading;
        switch (state) {
            case LOADING:
                stateString = StateLoading;
                break;
            case DEFAULT:
                stateString = StateDefault;
                break;
            case HIDDEN:
                stateString = StateHidden;
                break;
            case RESIZED:
                stateString = StateResized;
                break;
            case EXPANDED:
                stateString = StateExpanded;
                break;
        }

        String script = "mraid.setState('" + stateString + "');";
        mWebView.injectJavascript(script);
    }

    public void sendReady() {
        String script = "mraid.fireEvent('" + EventReady + "');";
        mWebView.injectJavascript(script);
    }

    public interface MraidHandler
    {
//        public void mraidInit(MraidBridge bridge);
//        public void mraidClose(MraidBridge bridge);
//        public void mraidOpen(MraidBridge bridge, String url);
    }

}
