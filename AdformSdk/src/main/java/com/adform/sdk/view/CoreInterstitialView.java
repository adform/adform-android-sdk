package com.adform.sdk.view;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.adform.sdk.mraid.properties.MraidDeviceIdProperty;
import com.adform.sdk.resources.AdDimension;
import com.adform.sdk.resources.CloseImageView;
import com.adform.sdk.utils.AdformEnum;
import com.adform.sdk.utils.entities.ExpandProperties;
import com.adform.sdk.view.base.BaseCoreContainer;
import com.adform.sdk.view.base.BaseInnerContainer;
import com.adform.sdk.view.inner.InnerInterstitialView;

/**
 * Created by mariusm on 27/05/14.
 */
public class CoreInterstitialView extends BaseCoreContainer {

    public interface CoreInterstitialListener {
        public void onAdClose();
        public void onAdOrientationChange(int orientation);
        public void onAdShown();
    }

    private CoreInterstitialListener mListener;

    public CoreInterstitialView(Context context, BaseInnerContainer innerContainer, Bundle extras) {
        this(context, null, 0, innerContainer, extras);
    }

    public CoreInterstitialView(Context context) {
        this(context, null, 0);
    }

    public CoreInterstitialView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CoreInterstitialView(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, 0, null, null);
    }
    public CoreInterstitialView(Context context, AttributeSet attrs, int defStyle,
                                BaseInnerContainer innerContainer, Bundle extras) {
        super(context, attrs, defStyle, innerContainer, extras);
        if (mContext instanceof CoreInterstitialListener)
            mListener = (CoreInterstitialListener)mContext;
        setAnimating(false);
        getInnerView().setCloseButtonEnabled(true);
        getInnerView().onUseCustomClose(true);
    }

    public void showContent(String content) {
        // Loaded content will always be loaded and mraid type
        setViewState(AdformEnum.VisibilityGeneralState.LOAD_SUCCESSFUL);
        mInnerContainer.showContent(content);
    }

    @Override
    public BaseInnerContainer getInnerView() {
        if (mInnerContainer == null) {
            mInnerContainer = new InnerInterstitialView(mContext);
        }
        return mInnerContainer;
    }

    @Override
    protected ViewGroup.LayoutParams getInnerViewLayoutParams() {
        return new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    protected void onVisibilityCallback(boolean isVisible) {
        mInnerContainer.getMraidBridge().changeVisibility(isVisible, false);
    }

    @Override
    public MraidDeviceIdProperty getDeviceId() {
        return null;
    }

    @Override
    public String getUserAgent() {
        return mInnerContainer.getUserAgent();
    }

    @Override
    public void onContentRestore(boolean state) {}

    @Override
    public void onContentRender() {
        if (mListener != null)
            mListener.onAdShown();
    }

    public void setListener(CoreInterstitialListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onMraidClose() {
        // Closing functionality is passed to its listener
        if (mListener != null)
            mListener.onAdClose();
    }

    @Override
    public void onMraidSetOrientation(boolean allowOrientationChange, AdformEnum.ForcedOrientation forcedOrientation) {
        switch (forcedOrientation) {
            case NONE:{
                if (allowOrientationChange)
                    mListener.onAdOrientationChange(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                else
                    mListener.onAdOrientationChange(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
                break;
            }
            case LANDSCAPE:{
                    mListener.onAdOrientationChange(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            }
            case PORTRAIT:{
                    mListener.onAdOrientationChange(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            }
            default:
                mListener.onAdOrientationChange(Configuration.ORIENTATION_UNDEFINED);
        }
    }

    @Override
    public void onMraidUseCustomClose(boolean shouldUseCustomClose) {
        // Nothing to respond to here anymore
    }

    @Override
    public void onMraidExpand(String url, ExpandProperties expandProperties) {
        // Nothing to do here
    }

    @Override
    public void destroy() {
        mListener = null;
        super.destroy();
    }
}
