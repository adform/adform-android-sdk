package com.adform.sdk2.view.inner;

import android.content.Context;
import android.util.AttributeSet;
import com.adform.sdk2.view.base.BaseInnerContainer;

/**
 * Created by mariusm on 21/05/14.
 */
public class InnerInterstitialView extends BaseInnerContainer {
    private AdWebView mWebView;

    public InnerInterstitialView(Context context) {
        this(context, null);
    }

    public InnerInterstitialView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InnerInterstitialView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void initView() {
        mWebView = createWebView(mContext);
        addView(mWebView);
    }

    @Override
    protected AdWebView getCurrentWebView() {
        return mWebView;
    }

    @Override
    protected AdWebView getWebViewToLoadContentTo() {
        return mWebView;
    }

    @Override
    protected void animateAdShowing() {
        // Interstitials does not do any animations inside this container. Yet.
    }
}
