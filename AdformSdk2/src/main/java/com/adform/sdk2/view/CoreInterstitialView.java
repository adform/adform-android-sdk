package com.adform.sdk2.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.adform.sdk2.mraid.properties.MraidDeviceIdProperty;
import com.adform.sdk2.resources.AdDimension;
import com.adform.sdk2.view.base.BaseCoreContainer;
import com.adform.sdk2.view.inner.InnerInterstitialView;

/**
 * Created by mariusm on 27/05/14.
 */
public class CoreInterstitialView extends BaseCoreContainer {

    private InnerInterstitialView mInterstitialView;

    public CoreInterstitialView(Context context) {
        super(context);
    }

    public CoreInterstitialView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CoreInterstitialView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setVisibility(View.VISIBLE);
    }

    public void showContent(String content) {
        mInterstitialView.showContent(content);
    }

    @Override
    protected View initInnerView() {
        mInterstitialView = new InnerInterstitialView(mContext);
        return mInterstitialView;
    }

    @Override
    protected AdDimension initAdDimen() {
        return new AdDimension(mContext);
    }

    @Override
    protected void startService() {}

    @Override
    protected void stopService() {}

    @Override
    protected void resumeService() {}

    @Override
    protected void onVisibilityCallback(boolean isVisible) {
        mInterstitialView.changeVisibility(isVisible);
    }

    @Override
    public MraidDeviceIdProperty getDeviceId() {
        return null;
    }

    @Override
    public String getUserAgent() {
        return mInterstitialView.getUserAgent();
    }

    @Override
    public void onContentRestore(boolean state) {

    }

    @Override
    public void onContentRender() {

    }
}
