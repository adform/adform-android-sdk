package com.adform.sdk2.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.net.Uri;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.adform.sdk2.mraid.MraidBridge;
import com.adform.sdk2.mraid.MraidCommandFactory;
import com.adform.sdk2.mraid.MraidWebViewClient;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by mariusm on 29/04/14.
 */
public class AdWebView extends WebView {
    private Context mContext;

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

    public void open(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://" + url;
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        mContext.startActivity(browserIntent);
    }

    @Override
    public void setWebViewClient(WebViewClient client) {
        super.setWebViewClient(client);
        if (client instanceof MraidWebViewClient)
            ((MraidWebViewClient) client).setWebView(this);
    }

    public void fireErrorEvent(MraidCommandFactory.MraidJavascriptCommand mraidJavascriptCommand,
                                  String message) {
        String action = mraidJavascriptCommand.getCommand();
        injectJavascript("window.mraidbridge.fireErrorEvent('" + action + "', '" + message + "');");
    }

    public void fireReady() {
        injectJavascript("mraidbridge.fireReadyEvent();");
    }

    public void fireState(MraidBridge.State state) {
        injectJavascript("mraidbridge.fireChangeEvent('" + MraidBridge.State.getStateString(state) + "');");
    }

}
