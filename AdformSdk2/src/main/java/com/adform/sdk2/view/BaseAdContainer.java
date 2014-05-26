package com.adform.sdk2.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import com.adform.sdk2.mraid.MraidWebViewClient;

/**
 * Created by mariusm on 21/05/14.
 */
public class BaseAdContainer extends RelativeLayout {
    private WebViewClient mSimpleWebViewClient;
    private WebViewClient mMraidWebViewClient;
    private String mUserAgent;

    public BaseAdContainer(Context context) {
        this(context, null);
    }

    public BaseAdContainer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseAdContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Creates web view and returns its instance. Inside all needed clients and variables are binded.
     *
     * @param context provided context
     * @return initialized web view
     */
    protected AdWebView createWebView(final Context context) {
        final AdWebView webView = new AdWebView(context);

        if (mSimpleWebViewClient == null)
            mSimpleWebViewClient = new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(final WebView view,
                                                        final String url) {
                    return true;
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                }
            };

        webView.setWebViewClient(mSimpleWebViewClient);
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        if (mUserAgent == null)
            mUserAgent = webView.getSettings().getUserAgentString();
        return webView;
    }

    public String getUserAgent() {
        return mUserAgent;
    }

    public WebViewClient getSimpleWebViewClient() {
        return mSimpleWebViewClient;
    }

    public WebViewClient getMraidWebViewClient() {
        // Lazy instantiation for mraid type of client
        if (mMraidWebViewClient == null)
            mMraidWebViewClient = new MraidWebViewClient();
        return mMraidWebViewClient;
    }
}
