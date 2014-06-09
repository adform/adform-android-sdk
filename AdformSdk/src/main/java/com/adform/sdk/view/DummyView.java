package com.adform.sdk.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.adform.sdk.interfaces.AdformRequestParamsListener;
import com.adform.sdk.mraid.properties.*;
import com.adform.sdk.resources.AdDimension;
import com.adform.sdk.resources.CloseImageView;
import com.adform.sdk.utils.AdformEnum;
import com.adform.sdk.view.base.BaseCoreContainer;
import com.adform.sdk.view.base.BaseInnerContainer;
import com.adform.sdk.view.inner.AdWebView;

import java.util.ArrayList;

/**
 * Created by mariusm on 03/06/14.
 */
public class DummyView extends BaseCoreContainer {
    private BaseInnerContainer mInnerContainer;
    private AdWebView mWebView;

    public DummyView(Context context) {
        this(context, null);
    }

    public DummyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DummyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public BaseInnerContainer getInnerView() {
        if (mInnerContainer == null)
            mInnerContainer = new BaseInnerContainer(mContext) {
                @Override
                protected void initView() {
                    mWebView = createWebView(mContext);
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
                protected void animateAdShowing() {}

                @Override
                public AdformEnum.PlacementType getPlacementType() {
                    return AdformEnum.PlacementType.INTERSTITIAL;
                }
            };
        return mInnerContainer;
    }

    @Override
    protected ViewGroup.LayoutParams getInnerViewLayoutParams() {
        return new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    protected AdDimension initAdDimen() {
        return new AdDimension(mContext);
    }

    @Override
    protected void onVisibilityCallback(boolean isVisible) {}

    @Override
    public String getUserAgent() {
        return getInnerView().getUserAgent();
    }

    @Override
    public void onContentRestore(boolean state) {}

    @Override
    public void onContentRender() {}

    @Override
    public void onMraidClose() {}

    @Override
    public void onMraidSetOrientation(boolean allowOrientationChange, AdformEnum.ForcedOrientation forcedOrientation) {}

    @Override
    public void onMraidUseCustomClose(boolean shouldUseCustomClose) {}

    @Override
    public void onMraidExpand() {}
}
