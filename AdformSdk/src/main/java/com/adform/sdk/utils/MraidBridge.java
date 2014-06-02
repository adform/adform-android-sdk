package com.adform.sdk.utils;

import com.adform.sdk.mraid.properties.MraidPositionProperty;
import com.adform.sdk.mraid.properties.MraidSizeProperty;
import com.adform.sdk.mraid.properties.MraidViewableProperty;
import com.adform.sdk.mraid.properties.SimpleMraidProperty;
import com.adform.sdk.utils.entities.ViewCoords;
import com.adform.sdk.view.inner.AdWebView;

/**
 * Created by mariusm on 30/05/14.
 */
public class MraidBridge implements VisibilityPositionManager.PositionManagerListener,
        AdWebView.NativeWebviewListener {
    public static final String VAR_PLACEMENT_TYPE = "placementType";
    private AdWebView mWebView;
    private ViewCoords mCurrentPosition, mDefaultPosition, mMaxSize, mScreenSize;
    private AdformEnum.PlacementType mPlacementType = AdformEnum.PlacementType.UNKNOWN;
    private AdformEnum.State mState = AdformEnum.State.LOADING;
    private AdWebView.NativeWebviewListener mMraidListener;
    private boolean mVisible;

    public MraidBridge() {}

    // -----------------------------
    // Native -> Mraid js var update
    // -----------------------------
    @Override
    public void onDefaultPositionUpdate(ViewCoords viewCoords) {
        if (viewCoords == null)
            return;
        mDefaultPosition = viewCoords;
        if (mWebView == null)
            return;
        mWebView.post(new Runnable() {
            @Override
            public void run() {
                mWebView
                        .fireChangeEventForProperty(
                                MraidPositionProperty.createWithPosition(
                                        MraidPositionProperty.PositionType.DEFAULT_POSITION, mDefaultPosition)
                        );
            }
        });
    }

    public void onCurrentPositionUpdate(ViewCoords viewCoords) {
        if (viewCoords == null)
            return;
        mCurrentPosition = viewCoords;
        if (mWebView == null)
            return;
        mWebView.post(new Runnable() {
            @Override
            public void run() {
                mWebView
                        .fireChangeEventForProperty(
                                MraidPositionProperty.createWithPosition(
                                        MraidPositionProperty.PositionType.CURRENT_POSITION, mCurrentPosition)
                        );
            }
        });
    }

    @Override
    public void onMaxSizeUpdate(ViewCoords viewCoords) {
        if (viewCoords == null)
            return;
        mMaxSize = viewCoords;
        if (mWebView == null)
            return;
        mWebView.post(new Runnable() {
            @Override
            public void run() {
                mWebView
                        .fireChangeEventForProperty(
                                MraidSizeProperty.createWithSize(
                                        MraidSizeProperty.SizeType.MAX_SIZE, mMaxSize)
                        );
            }
        });
    }

    @Override
    public void onScreenSizeUpdate(ViewCoords viewCoords) {
        if (viewCoords == null)
            return;
        mScreenSize = viewCoords;
        if (mWebView == null)
            return;
        mWebView.post(new Runnable() {
            @Override
            public void run() {
                mWebView
                        .fireChangeEventForProperty(
                                MraidSizeProperty.createWithSize(
                                        MraidSizeProperty.SizeType.SCREEN_SIZE, mScreenSize)
                        );
            }
        });
    }

    public void onStateChange(AdformEnum.State state) {
        mState = state;
        if (mWebView == null)
            return;
        mWebView.post(new Runnable() {
            @Override
            public void run() {
                mWebView.fireChangeEventForProperty(SimpleMraidProperty.createWithKeyAndValue("state",
                        AdformEnum.State.getStateString(AdformEnum.State.DEFAULT)));
            }
        });
    }

    public void onPlacementTypeChange(final AdformEnum.PlacementType placementType) {
        mPlacementType = placementType;
        if (mWebView == null)
            return;
        mWebView.post(new Runnable() {
            @Override
            public void run() {
                mWebView.fireChangeEventForProperty(
                        SimpleMraidProperty.createWithKeyAndValue(VAR_PLACEMENT_TYPE,
                                AdformEnum.PlacementType.getPlacementString(placementType)));
            }
        });

    }

    public void changeVisibility(final boolean visible) {
        mVisible = visible;
        if (mWebView == null)
            return;
        mWebView.post(new Runnable() {
            @Override
            public void run() {
                mWebView
                        .fireChangeEventForProperty(MraidViewableProperty.createWithViewable(visible));
            }
        });
    }

    public void forceSettingUpdate() {
        mWebView.postDelayed(forcePositionSettingRunnable, 200);
    }

    // -----------------
    // Getters / Setters
    // -----------------
    public void setWebView(AdWebView mWebView) {
        this.mWebView = mWebView;
    }

    public void setMraidListener(AdWebView.NativeWebviewListener mraidListener) {
        this.mMraidListener = mraidListener;
    }

    // ---------
    // Runnables
    // ---------
    private Runnable forcePositionSettingRunnable = new Runnable() {
        @Override
        public void run() {
            onPlacementTypeChange(mPlacementType);
            onStateChange(mState);
            onScreenSizeUpdate(mScreenSize);
            onMaxSizeUpdate(mMaxSize);
            if (mDefaultPosition != null)
                onDefaultPositionUpdate(mDefaultPosition);
            if (mCurrentPosition != null)
                onCurrentPositionUpdate(mCurrentPosition);
        }
    };

    // ------------------------
    // Mraid callback functions
    // ------------------------
    @Override
    public void onMraidOpen(String url) {
        if (mMraidListener != null)
            mMraidListener.onMraidOpen(url);
    }

    @Override
    public void onMraidClose() {
        if (mMraidListener != null)
            mMraidListener.onMraidClose();
    }

    @Override
    public void onMraidSetOrientation(boolean allowOrientationChange, AdformEnum.ForcedOrientation forcedOrientation) {
        if (mMraidListener != null)
            mMraidListener.onMraidSetOrientation(allowOrientationChange, forcedOrientation);
    }
}
