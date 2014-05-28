package com.adform.sdk2.view.inner;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.adform.sdk2.mraid.properties.SimpleMraidProperty;
import com.adform.sdk2.utils.AdformEnum;
import com.adform.sdk2.view.base.BaseInnerContainer;

/**
 * Created by mariusm on 21/05/14.
 */
public class InnerInterstitialView extends BaseInnerContainer {

    public interface InnerInterstitialListener {
        public void onAdClose();
    }

    private AdWebView mWebView;
    private InnerInterstitialListener mListener;

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
        final FrameLayout.LayoutParams webViewParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mWebView, webViewParams);
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

    @Override
    public void onPlacementTypeChange() {
        post(new Runnable() {
            @Override
            public void run() {
                getCurrentWebView().fireChangeEventForProperty(
                        SimpleMraidProperty.createWithKeyAndValue("placementType",
                                AdformEnum.PlacementType.getPlacementString(AdformEnum.PlacementType.INTERSTITIAL)));
            }
        });

    }


    @Override
    public void onMraidClose() {
        if (mListener != null)
            mListener.onAdClose();
    }

    public void setListener(InnerInterstitialListener listener) {
        this.mListener = listener;
    }
}
