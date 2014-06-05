package com.adform.sdk.utils;

import com.adform.sdk.mraid.properties.MraidPositionProperty;
import com.adform.sdk.mraid.properties.MraidSizeProperty;
import com.adform.sdk.mraid.properties.MraidViewableProperty;
import com.adform.sdk.mraid.properties.SimpleMraidProperty;
import com.adform.sdk.utils.entities.ViewCoords;
import com.adform.sdk.utils.managers.VisibilityPositionManager;
import com.adform.sdk.view.inner.AdWebView;

/**
 * Created by mariusm on 30/05/14.
 */
public class MraidBridge implements VisibilityPositionManager.PositionManagerListener,
        AdWebView.NativeWebviewListener {

    public interface MraidBridgeListener {
        public void onIsContentMraidChange(boolean isContentMraid);
    }

    public static final String VAR_PLACEMENT_TYPE = "placementType";

    private boolean isContentMraid = false;
    private AdWebView mWebView;
    private ViewCoords mCurrentPosition, mDefaultPosition, mMaxSize, mScreenSize;
    private AdformEnum.PlacementType mPlacementType = AdformEnum.PlacementType.UNKNOWN;
    private AdformEnum.State mState = AdformEnum.State.LOADING;
    private AdWebView.NativeWebviewListener mMraidListener;
    private MraidBridgeListener mBridgeListener;
    private boolean mVisible;
    private boolean mUseCustomClose = false;
    private boolean mAllowOrientationChange = true;
    private AdformEnum.ForcedOrientation mForcedOrientation = AdformEnum.ForcedOrientation.UNKNOWN;

    public MraidBridge() {}

    private boolean isPropertyNotValid(Object property) {
        if (!isContentMraid)
            return true;
        if (property == null)
            return true;
        return false;
    }

    // -----------------------------
    // Native -> Mraid js var update
    // -----------------------------
    @Override
    public void onDefaultPositionUpdate(ViewCoords viewCoords, boolean forceUpdate) {
        if (isPropertyNotValid(viewCoords))
            return;
        if (viewCoords.equals(mDefaultPosition) && !forceUpdate)
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

    public void onCurrentPositionUpdate(ViewCoords viewCoords, boolean forceUpdate) {
        if (isPropertyNotValid(viewCoords))
            return;
        if (viewCoords.equals(mCurrentPosition) && !forceUpdate)
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
    public void onMaxSizeUpdate(ViewCoords viewCoords, boolean forceUpdate) {
        if (viewCoords == null)
            return;
        if (viewCoords.equals(mMaxSize) && !forceUpdate)
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
    public void onScreenSizeUpdate(ViewCoords viewCoords, boolean forceUpdate) {
        if (viewCoords == null)
            return;
        if (viewCoords.equals(mScreenSize) && !forceUpdate)
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

    public void onStateChange(AdformEnum.State state, boolean forceUpdate) {
        if (isPropertyNotValid(state))
            return;
        if (mState == state && !forceUpdate)
            return;
        mState = state;
        if (mWebView == null)
            return;
        mWebView.post(new Runnable() {
            @Override
            public void run() {
                mWebView.fireChangeEventForProperty(SimpleMraidProperty.createWithKeyAndValue("state",
                        AdformEnum.State.getStateString(mState)));
            }
        });
    }

    public void setState(AdformEnum.State state) {
        this.mState = state;
    }

    public void onPlacementTypeChange(final AdformEnum.PlacementType placementType, boolean forceUpdate) {
        if (isPropertyNotValid(placementType))
            return;
        if (mPlacementType == placementType && !forceUpdate)
            return;
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

    public void setPlacementType(AdformEnum.PlacementType placementType) {
        this.mPlacementType = placementType;
    }

    public void changeVisibility(boolean visible, boolean forceUpdate) {
        if (mState != null && mState == AdformEnum.State.LOADING ||
                mState != null && mState == AdformEnum.State.HIDDEN)
            visible = false;
        if (mVisible == visible && !forceUpdate)
            return;
        mVisible = visible;
        if (mWebView == null)
            return;
        mWebView.post(new Runnable() {
            @Override
            public void run() {
                mWebView
                        .fireChangeEventForProperty(MraidViewableProperty.createWithViewable(mVisible));
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

    public void setContentMraid(boolean isContentMraid) {
        this.isContentMraid = isContentMraid;
        if (mBridgeListener != null)
            mBridgeListener.onIsContentMraidChange(isContentMraid);
    }

    public boolean isContentMraid() {
        return isContentMraid;
    }

    public void setBridgeListener(MraidBridgeListener bridgeListener) {
        this.mBridgeListener = bridgeListener;
    }

    // ---------
    // Runnables
    // ---------
    private Runnable forcePositionSettingRunnable = new Runnable() {
        @Override
        public void run() {
            onPlacementTypeChange(mPlacementType, true);
            onStateChange(mState, true);
            onScreenSizeUpdate(mScreenSize, true);
            onMaxSizeUpdate(mMaxSize, true);
            onDefaultPositionUpdate(mDefaultPosition, true);
            onCurrentPositionUpdate(mCurrentPosition, true);
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
        mAllowOrientationChange = allowOrientationChange;
        mForcedOrientation = forcedOrientation;
        if (mMraidListener != null)
            mMraidListener.onMraidSetOrientation(allowOrientationChange, forcedOrientation);
    }

    @Override
    public void onMraidUseCustomClose(boolean shouldUseCustomClose) {
        mUseCustomClose = shouldUseCustomClose;
        if (mMraidListener != null)
            mMraidListener.onMraidUseCustomClose(shouldUseCustomClose);
    }

    @Override
    public void onMraidExpand() {
        if (mMraidListener != null)
            mMraidListener.onMraidExpand();
    }
}
