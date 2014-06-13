package com.adform.sdk.view;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import com.adform.sdk.interfaces.CoreInterstitialListener;
import com.adform.sdk.mraid.properties.MraidDeviceIdProperty;
import com.adform.sdk.resources.AdDimension;
import com.adform.sdk.resources.CloseImageView;
import com.adform.sdk.utils.AdformEnum;
import com.adform.sdk.utils.entities.ExpandProperties;
import com.adform.sdk.utils.managers.AdformAnimationManager;
import com.adform.sdk.view.base.BaseCoreContainer;
import com.adform.sdk.view.base.BaseInnerContainer;
import com.adform.sdk.view.inner.InnerInterstitialView;

/**
 * Created by mariusm on 27/05/14.
 */
public class CoreInterstitialView extends BaseCoreContainer implements AdformAnimationManager.SliderableWidgetProperties,
        AdformAnimationManager.SliderableWidgetCallbacks {

    private AdformAnimationManager mAdformAnimationManager;

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

        mAdformAnimationManager = new AdformAnimationManager(this, new AdformAnimationManager.SlidingAnimationProperties() {
            @Override
            public Animation getCollapseAnimation() {
                return new AlphaAnimation(1.0f, 0.0f);
            }

            @Override
            public Animation getExpandAnimation() {
                return new AlphaAnimation(0.0f, 1.0f);
            }

            @Override
            public int getAnimationDuration() {
                return AdformAnimationManager.DEFAULT_DURATION;
            }
            @Override
            public int getAnimationDelay() {
                return AdformAnimationManager.DEFAULT_DELAY;
            }
        });
        mAdformAnimationManager.setListenerCallbacks(this);

        setAnimating(false);
        getInnerView().setCloseButtonEnabled(true);
        getInnerView().onUseCustomClose(mExtraParams.getBoolean(INNER_EXTRA_USE_CUSTOM_CLOSE, false));
    }

    public void showContent(String content) {
        // Loaded content will always be loaded and mraid type
        setViewState(AdformEnum.VisibilityGeneralState.LOAD_SUCCESSFUL);
        super.showContent(content);
    }

    @Override
    public BaseInnerContainer getInnerView() {
        if (mInnerContainer == null) {
            Bundle extras = new Bundle();
            extras.putBoolean(BaseInnerContainer.INNER_EXTRA_SKIP_INIT, false);
            mInnerContainer = new InnerInterstitialView(mContext, extras);
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
    public AdformEnum.State getDefaultState() {
        return AdformEnum.State.DEFAULT;
    }

    @Override
    public AdformEnum.PlacementType getDefaultPlacementType() {
        return AdformEnum.PlacementType.INTERSTITIAL;
    }

    @Override
    public MraidDeviceIdProperty getDeviceId() {
        return null;
    }

    @Override
    public void onContentRestore(boolean state) {}

    @Override
    public void onContentRender() {
        mAdformAnimationManager.turnOn();
        if (mListener != null)
            mListener.onAdShown();
    }


    @Override
    public void onMraidClose() {
        // Closing functionality is passed to its listener
        mAdformAnimationManager.turnOff();
    }

    @Override
    public void onSliderFinishedHiding() {
        getInnerView().setVisibility(View.GONE);
        if (mListener != null)
            mListener.onAdClose();
    }

    @Override
    public void onSliderFinishedShowing() {
        getInnerView().setVisibility(View.VISIBLE);
    }

    @Override
    public void onSliderStartedHiding() {}

    @Override
    public void onSliderStartedShowing() {}

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
