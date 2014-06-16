package com.adform.sdk.utils;

import com.adform.sdk.mraid.properties.MraidPositionProperty;
import com.adform.sdk.mraid.properties.MraidSizeProperty;
import com.adform.sdk.mraid.properties.MraidViewableProperty;
import com.adform.sdk.mraid.properties.SimpleMraidProperty;
import com.adform.sdk.resources.AdDimension;
import com.adform.sdk.utils.entities.ExpandProperties;
import com.adform.sdk.utils.entities.ViewCoords;
import com.adform.sdk.utils.managers.VisibilityPositionManager;
import com.adform.sdk.view.inner.AdWebView;

/**
 * Created by mariusm on 30/05/14.
 */
public class MraidBridge implements VisibilityPositionManager.PositionManagerListener,
        AdWebView.NativeWebviewListener {

    public static final int RUN_INDEX_DEFAULT_POS = 0;
    public static final int RUN_INDEX_CURR_POS = 1;
    public static final int RUN_INDEX_MAX_SIZE = 2;
    public static final int RUN_INDEX_SCREEN_SIZE = 3;
    public static final int RUN_INDEX_SIZE = 4;
    public static final int RUN_INDEX_STATE = 5;
    public static final int RUN_INDEX_PLACEMENT = 6;
    public static final int RUN_INDEX_VISIBILITY = 7;

    public interface CoreMraidBridgeListener {
        public void onIsContentMraidChange(boolean isContentMraid);
    }

    public interface InnerMraidBridgeListener {
        public void onUseCustomClose(boolean useCustomClose);
    }

    public static final String VAR_PLACEMENT_TYPE = "placementType";

    private boolean isContentMraid = false;
    private AdWebView mWebView;
    private ViewCoords mCurrentPosition, mDefaultPosition, mMaxSize, mScreenSize;
    private AdformEnum.PlacementType mPlacementType = AdformEnum.PlacementType.UNKNOWN;
    private AdformEnum.State mState = AdformEnum.State.LOADING;
    private AdWebView.NativeWebviewListener mMraidListener;
    private CoreMraidBridgeListener mCoreBridgeListener;
    private InnerMraidBridgeListener mInnerBridgeListener;
    private boolean mVisible;
    private boolean mUseCustomClose = false;
    private boolean mAllowOrientationChange = true;
    private AdformEnum.ForcedOrientation mForcedOrientation = AdformEnum.ForcedOrientation.UNKNOWN;
    private ExpandProperties mExpandProperties;
    private AdDimension mAdDimension;
    private Runnable[] mRunnableArray = new Runnable[10];

    public MraidBridge() {}

    public MraidBridge(InnerMraidBridgeListener innerBridge) {
        mInnerBridgeListener = innerBridge;
    }

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
        mRunnableArray[RUN_INDEX_DEFAULT_POS] = new Runnable() {
            @Override
            public void run() {
                if (mWebView != null)
                    mWebView
                            .fireChangeEventForProperty(
                                    MraidPositionProperty.createWithPosition(
                                            MraidPositionProperty.PositionType.DEFAULT_POSITION, mDefaultPosition)
                            );
            }
        };
        mWebView.post(mRunnableArray[RUN_INDEX_DEFAULT_POS]);
    }

    public void onCurrentPositionUpdate(ViewCoords viewCoords, boolean forceUpdate) {
        if (isPropertyNotValid(viewCoords))
            return;
        if (viewCoords.equals(mCurrentPosition) && !forceUpdate)
            return;
        mCurrentPosition = viewCoords;
        if (mWebView == null)
            return;
        mRunnableArray[RUN_INDEX_CURR_POS] = new Runnable() {
            @Override
            public void run() {
                if (mWebView != null)
                    mWebView
                            .fireChangeEventForProperty(
                                    MraidPositionProperty.createWithPosition(
                                            MraidPositionProperty.PositionType.CURRENT_POSITION, mCurrentPosition)
                            );
            }
        };
        mWebView.post(mRunnableArray[RUN_INDEX_CURR_POS]);
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
        mRunnableArray[RUN_INDEX_MAX_SIZE] = new Runnable() {
            @Override
            public void run() {
                if (mWebView != null)
                    mWebView
                            .fireChangeEventForProperty(
                                    MraidSizeProperty.createWithViewCoords(
                                            MraidSizeProperty.SizeType.MAX_SIZE, mMaxSize)
                            );
            }
        };
        mWebView.post(mRunnableArray[RUN_INDEX_MAX_SIZE]);
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
        mRunnableArray[RUN_INDEX_SCREEN_SIZE] = new Runnable() {
            @Override
            public void run() {
                if (mWebView != null)
                    mWebView
                            .fireChangeEventForProperty(
                                    MraidSizeProperty.createWithViewCoords(
                                            MraidSizeProperty.SizeType.SCREEN_SIZE, mScreenSize)
                            );
            }
        };
        mWebView.post(mRunnableArray[RUN_INDEX_SCREEN_SIZE]);
    }

    public void onSizeUpdate(AdDimension adDimension, boolean forceUpdate) {
        if (adDimension == null)
            return;
        if (adDimension.equals(mAdDimension) && !forceUpdate)
            return;
        mAdDimension = adDimension;
        if (mWebView == null)
            return;
        mRunnableArray[RUN_INDEX_SIZE] = new Runnable() {
            @Override
            public void run() {
                if (mWebView != null)
                    mWebView
                            .fireChangeEventForProperty(
                                    MraidSizeProperty.createWithAdDimension(
                                            MraidSizeProperty.SizeType.SIZE, mAdDimension)
                            );
            }
        };
        mWebView.post(mRunnableArray[RUN_INDEX_SIZE]);
    }

    public void onStateChange(AdformEnum.State state, boolean forceUpdate) {
        if (isPropertyNotValid(state))
            return;
        if (mState == state && !forceUpdate)
            return;
        mState = state;
        if (mWebView == null)
            return;
        mRunnableArray[RUN_INDEX_STATE] = new Runnable() {
            @Override
            public void run() {
                if (mWebView != null)
                    mWebView.fireChangeEventForProperty(SimpleMraidProperty.createWithKeyAndValue("state",
                            AdformEnum.State.getStateString(mState)));
            }
        };
        mWebView.post(mRunnableArray[RUN_INDEX_STATE]);
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
        mRunnableArray[RUN_INDEX_PLACEMENT] = new Runnable() {
            @Override
            public void run() {
                if (mWebView != null)
                    mWebView.fireChangeEventForProperty(
                            SimpleMraidProperty.createWithKeyAndValue(VAR_PLACEMENT_TYPE,
                                    AdformEnum.PlacementType.getPlacementString(placementType)));
            }
        };
        mWebView.post(mRunnableArray[RUN_INDEX_PLACEMENT]);
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
        mRunnableArray[RUN_INDEX_VISIBILITY] = new Runnable() {
            @Override
            public void run() {
                if (mWebView != null)
                    mWebView
                            .fireChangeEventForProperty(MraidViewableProperty.createWithViewable(mVisible));
            }
        };
        mWebView.post(mRunnableArray[RUN_INDEX_VISIBILITY]);
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
        if (mCoreBridgeListener != null)
            mCoreBridgeListener.onIsContentMraidChange(isContentMraid);
    }

    public boolean isContentMraid() {
        return isContentMraid;
    }

    public void setCoreBridgeListener(CoreMraidBridgeListener coreBridgeListener) {
        this.mCoreBridgeListener = coreBridgeListener;
    }

    public void setInnerBridgeListener(InnerMraidBridgeListener innerBridgeListener) {
        this.mInnerBridgeListener = innerBridgeListener;
    }

    public AdDimension getAdDimension() {
        return mAdDimension;
    }

    public void setAdDimension(AdDimension adDimension) {
        mAdDimension = adDimension;
    }

    public AdformEnum.State getState() {
        return mState;
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
        if (mInnerBridgeListener != null)
            mInnerBridgeListener.onUseCustomClose(shouldUseCustomClose);
    }

    @Override
    public void onMraidExpand(String url, ExpandProperties expandProperties) {
        mExpandProperties = expandProperties;
        mUseCustomClose = expandProperties.useCustomClose();
        if (mMraidListener != null)
            mMraidListener.onMraidExpand(url, expandProperties);
    }

    public void destroy() {
        if (mWebView != null) {
            for (int i = 0; i < mRunnableArray.length; i++) {
                if (mRunnableArray[i] != null)
                    mWebView.removeCallbacks(mRunnableArray[i]);
            }
            mWebView.removeCallbacks(forcePositionSettingRunnable);
            mWebView = null;
        }
        mMraidListener = null;
        mCoreBridgeListener = null;
        mInnerBridgeListener = null;
    }

}
