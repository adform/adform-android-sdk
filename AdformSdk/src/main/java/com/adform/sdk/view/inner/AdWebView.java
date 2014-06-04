package com.adform.sdk.view.inner;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.adform.sdk.mraid.MraidCommandFactory;
import com.adform.sdk.mraid.MraidWebViewClient;
import com.adform.sdk.mraid.properties.MraidBaseProperty;
import com.adform.sdk.utils.AdformEnum;
import com.adform.sdk.utils.Utils;

import java.util.ArrayList;

/**
 * Created by mariusm on 29/04/14.
 */
public class AdWebView extends WebView {

    public interface NativeWebviewListener {
        public void onMraidOpen(String url);
        public void onMraidClose();
        public void onMraidSetOrientation(boolean allowOrientationChange,
                                          AdformEnum.ForcedOrientation forcedOrientation);
        public void onMraidUseCustomClose(boolean shouldUseCustomClose);
    }

    private Context mContext;
    private NativeWebviewListener mListener;

    public AdWebView(Context context) {
        this(context, null);
    }

    public AdWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AdWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    public void injectJavascript(String script) {
        final String url = "javascript:" + script;
        super.loadUrl(url);
    }

    @Override
    public void setWebViewClient(WebViewClient client) {
        super.setWebViewClient(client);
        if (client instanceof MraidWebViewClient)
            ((MraidWebViewClient) client).setWebView(this);
    }

    // ----------------------------------
    // Event firing to WebView javascript
    // ----------------------------------
    public void fireErrorEvent(MraidCommandFactory.MraidJavascriptCommand mraidJavascriptCommand,
                                  String message) {
        String action = mraidJavascriptCommand.getCommand();
        injectJavascript("window.mraidbridge.fireErrorEvent('" + action + "', '" + message + "');");
    }

    public void fireReady() {
        Utils.p("Ready event...");
        injectJavascript("window.mraidbridge.fireReadyEvent();");
    }

//    public void fireViewportUpdate() {
//        Utils.p("Updating viewport with "+getWidth());
//        injectJavascript("document.querySelector('meta[name=viewport]')" + String.format(".setAttribute('content', 'width=%d;', false);", getWidth()));
//    }

    public void fireChangeEventForProperty(MraidBaseProperty property) {
        String json = "{" + property.toString() + "}";
        Utils.p("Sending: "+json);
        injectJavascript("window.mraidbridge.fireChangeEvent(" + json + ");");
    }

    public void fireChangeEventForProperties(ArrayList<MraidBaseProperty> properties) {
        String props = properties.toString();
        if (props.length() < 2) return;

        String json = "{" + props.substring(1, props.length() - 1) + "}";
        injectJavascript("window.mraidbridge.fireChangeEvent(" + json + ");");
    }
    public void fireNativeCommandCompleteEvent(String command) {
        injectJavascript("window.mraidbridge.nativeCallComplete('" + command + "');");
    }

    public void setMraidCallbackListener(NativeWebviewListener listener) {
        this.mListener = listener;
    }

    public NativeWebviewListener getListener() {
        return mListener;
    }
}
