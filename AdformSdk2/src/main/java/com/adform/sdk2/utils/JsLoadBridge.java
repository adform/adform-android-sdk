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
    /** A header that should be used for callback to occur */
    public static final String NATIVE_JS_CALLBACK_HEADER = "<script type=\"text/javascript\">\n" +
            "function finishedLoading() {\n" +
            "   "+NATIVE_JS_INTERFACE+".contentLoaded();\n" +
            "   nativePrint('Content loaded');" +
            "};\n" +
            "function nativePrint(call) {\n" +
            "   "+NATIVE_JS_INTERFACE+".nativePrint(call);\n" +
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

    @JavascriptInterface
    public void contentLoaded(){
        if (mHandler != null)
            mHandler.onContentLoadedFromJsBridge();
    }

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
         * Content has been rendered
         */
        public void onContentLoadedFromJsBridge();
        public void onNativePrint(String nativeCall);
    }

}
