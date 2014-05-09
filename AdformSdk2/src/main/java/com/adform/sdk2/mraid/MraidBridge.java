package com.adform.sdk2.mraid;

import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import com.adform.sdk2.view.AdWebView;

/**
 * Created by mariusm on 30/04/14.
 */
public class MraidBridge {
    public static final String MRAID_JS_INTERFACE = "mraid";

    public interface MraidBridgeHandler {}

    public enum State {
        LOADING(0),
        DEFAULT(1),
        EXPANDED(2),
        RESIZED(3),
        HIDDEN(4);
        private int value;

        private State(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static State parseType(int status) {
            switch (status) {
                case 0: return LOADING;
                case 1: return DEFAULT;
                case 2: return EXPANDED;
                case 3: return RESIZED;
                case 4: return HIDDEN;
                default: return LOADING;
            }
        }

        public static String getStateString(State state) {
            switch (state) {
                case LOADING: return "loading";
                case DEFAULT: return "default";
                case EXPANDED: return "expanded";
                case RESIZED: return "resized";
                case HIDDEN: return "hidden";
            }
            return null;
        }

    }

    public AdWebView mWebView;
    public MraidBridgeHandler handler;
    private State mState = State.LOADING;

    public MraidBridge(MraidBridgeHandler handler) {
        if (handler == null)
            throw new IllegalArgumentException("handler null");
        this.handler = handler;
    }

    public void setWebView(AdWebView webView) {
        mWebView = webView;
        webView.addJavascriptInterface(this, MRAID_JS_INTERFACE);
    }

    public AdWebView getWebView() {
        return mWebView;
    }
}
