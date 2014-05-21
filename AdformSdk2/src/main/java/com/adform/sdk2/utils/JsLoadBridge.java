package com.adform.sdk2.utils;

import android.webkit.JavascriptInterface;
import com.adform.sdk2.view.AdWebView;

/**
 * Created by mariusm on 30/04/14.
 * A bridge that connects through javascript of shown content in WebView and
 * callbacks when view has been rendered.
 * This is a workaround for this bug:
 *
 * http://stackoverflow.com/questions/7822481/picturelistener-and-onnewpicture-are-deprecated-alternatives
 * http://stackoverflow.com/questions/7166534/picturelistener-is-deprecated-and-obsolete-is-there-a-replacement
 * http://stackoverflow.com/questions/17873341/android-webview-picturelistener-deprecated-still-no-alternative
 * https://code.google.com/p/android/issues/detail?id=38646
 */
public class JsLoadBridge {
    /** A registered callback interface. This should be used something like this:
     * webView.addJavascriptInterface(mLoadBridge, JsLoadBridge.NATIVE_JS_INTERFACE);
     */
    public static final String NATIVE_JS_INTERFACE = "AdformNativeJs";
    public static final String JS_CB_CONTENT_LOADED = NATIVE_JS_INTERFACE+".contentLoaded";
    public static final String JS_CB_PRINT = NATIVE_JS_INTERFACE+".nativePrint";
    public static final String JS_CB_CONFIGURATION_PRESET = NATIVE_JS_INTERFACE+".configurationPreset";
    /** A header that should be used for callback to occur */
    public static final String NATIVE_JS_CALLBACK_HEADER = "<script type=\"text/javascript\">\n" +
            "function finishedLoading() {\n" +
            "   "+JS_CB_CONTENT_LOADED+"();\n" +
            "   nativePrint('Content loaded');" +
            "};\n" +
            "function nativePrint(call) {\n" +
            "   "+JS_CB_PRINT+"(call);\n" +
            "};\n" +
            "window.onload = function(){finishedLoading();};"+
            "</script>\n";
    public static final String NATIVE_JS_CALLBACK_BODY_ONLOAD =  "";

    public AdWebView mWebView;
    public LoadBridgeHandler mHandler;

    public JsLoadBridge(LoadBridgeHandler handler) {
        if (handler == null)
            throw new IllegalArgumentException("LoadBridgeHandler is null");
        this.mHandler = handler;
    }

    /**
     * For bridge to work, we need to set webview instance
     * @param webView provided webview
     */
    public void setWebView(AdWebView webView) {
        this.mWebView = webView;
        webView.addJavascriptInterface(this, JsLoadBridge.NATIVE_JS_INTERFACE);
    }

    /**
     * Function is called from javascript when document is loaded.
     */
    @JavascriptInterface
    public void contentLoaded(){
        if (mHandler != null)
            mHandler.onContentLoadedFromJsBridge();
    }

    /**
     * Function is called when some configuration occurs in javascript.
     * This can be something like screenSize, maxSize etc.
     * @param configuredParam parameter which was changed
     */
    @JavascriptInterface
    public void configurationPreset(String configuredParam){
        if (mHandler != null)
            mHandler.onConfigurationPreset(configuredParam);
    }

    /**
     * A callback when something is printed out in javascript console.
     * @param nativePrint provided message that is printed
     */
    @JavascriptInterface
    public void nativePrint(String nativePrint){
        if (mHandler != null)
            mHandler.onNativePrint(nativePrint);
    }

    /**
     * An callback interface that indicates content load state
     */
    public interface LoadBridgeHandler {
        /**
         * Callback when document is loaded
         */
        public void onContentLoadedFromJsBridge();

        /**
         * Callback the message that is printed in console in javascript
         * @param nativePrint provided message
         */
        public void onNativePrint(String nativePrint);

        /**
         * Callback when configuration changes are made in javascript
         * @param configuredParam provided changed configuration
         */
        public void onConfigurationPreset(String configuredParam);
    }

}
