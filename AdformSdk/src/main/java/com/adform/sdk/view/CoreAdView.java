package com.adform.sdk.view;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.adform.sdk.mraid.properties.MraidDeviceIdProperty;
import com.adform.sdk.network.app.entities.entities.AdServingEntity;
import com.adform.sdk.mraid.AdService;
import com.adform.sdk.network.base.ito.network.*;
import com.adform.sdk.resources.AdDimension;
import com.adform.sdk.utils.*;
import com.adform.sdk.utils.entities.ExpandProperties;
import com.adform.sdk.utils.managers.AdformContentLoadManager;
import com.adform.sdk.utils.managers.SlidingManager;
import com.adform.sdk.view.base.BaseCoreContainer;
import com.adform.sdk.view.base.BaseInnerContainer;
import com.adform.sdk.view.inner.InnerBannerView;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by mariusm on 24/04/14.
 * Base view that should be implemented when adding a banner
 */
public class CoreAdView extends BaseCoreContainer implements Observer,
        SlidingManager.SliderableWidgetProperties, SlidingManager.SliderableWidgetCallbacks,
        AdformContentLoadManager.ContentLoaderListener,
        AdService.AdServiceBinder {


    public interface CoreAdViewListener {
        public void onAdVisibilityChange(boolean visible);
//        public void onNetworkError(NetworkTask request, NetworkError networkError);
    }

    private AdService mAdService;
    /** Bundle that packs AdService last state when saving view instance */
    private Bundle mServiceInstanceBundle;
    /** Manager that handles core container sliding animation */
    private SlidingManager mSlidingManager;
    /** Manager that handles contract (json) loading */
    private AdformContentLoadManager mAdformContentLoadManager;
    /** An interface for calling back handler functions for outer control */
    private CoreAdViewListener mListener;
    /* Basic values that store persistent information */
    private MraidDeviceIdProperty mDeviceId;
    private boolean isViewExpanding = false;

    public CoreAdView(Context context) {
        this(context, null);
    }

    public CoreAdView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CoreAdView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        if (mContext instanceof CoreAdViewListener)
            mListener = (CoreAdViewListener)mContext;
        mSlidingManager = new SlidingManager(this);
        mSlidingManager.setListenerCallbacks(this);
        mAdformContentLoadManager = new AdformContentLoadManager();
        mAdformContentLoadManager.setListener(this);
        setVisibility(INVISIBLE);
        getInnerView().setCloseButtonEnabled(false);
    }

    @Override
    public BaseInnerContainer getInnerView() {
        if (mInnerContainer == null) {
            mInnerContainer = new InnerBannerView(mContext);
        }
        return mInnerContainer;
    }

    @Override
    protected ViewGroup.LayoutParams getInnerViewLayoutParams() {
        AdDimension mockDimension = AdDimension.createDefaultDimension(mContext);
        return new RelativeLayout.LayoutParams(
                mockDimension.getWidth(),
                mockDimension.getHeight());
    }

    /** An update from configuration json */
    @Override
    public void update(Observable observable, Object data) {
        if (data instanceof NetworkError
                && ((NetworkError) data).getType() == NetworkError.Type.NETWORK) {
            getInnerView().showContent(null);
            mSlidingManager.turnOff();
            setViewState(AdformEnum.VisibilityGeneralState.LOAD_FAIL);
            resetTimesLoaded();
            return;
        }
        if (data instanceof NetworkError
                && ((NetworkError) data).getType() == NetworkError.Type.SERVER) {
            getInnerView().showContent(null);
            mSlidingManager.turnOff();
            setViewState(AdformEnum.VisibilityGeneralState.LOAD_FAIL);
            resetTimesLoaded();
            return;
        }
        if (data != null) {
            AdServingEntity adServingEntity = (AdServingEntity) data;

            // Loading banner
            if (adServingEntity.getAdEntity() != null
                    && adServingEntity.getAdEntity().getTagDataEntity() != null
                    && adServingEntity.getAdEntity().getTagDataEntity().getSrc() != null
                    ) {
                String content = adServingEntity.getAdEntity().getTagDataEntity().getSrc();
                try {
                    mAdformContentLoadManager.loadContent(
                            mAdformContentLoadManager.getRawGetTask(content,true));
                } catch (AdformContentLoadManager.ContentLoadException e) {
                    e.printStackTrace();
                }
            } else {
                getInnerView().showContent(null);
                mSlidingManager.turnOff();
                setViewState(AdformEnum.VisibilityGeneralState.LOAD_FAIL);
                resetTimesLoaded();
            }
        }
    }

    @Override
    public void onContentMraidLoadSuccessful(String content) {
        getInnerView().showContent(content);
    }

    @Override
    public void onContentRestore(boolean state) {
        if (state)
            mSlidingManager.turnOnImmediate();
        else
            mSlidingManager.turnOffImmediate();
        if (mListener != null)
            mListener.onAdVisibilityChange(state);
    }

    @Override
    public void onContentRender() {
        mSlidingManager.turnOn();
        setViewState(AdformEnum.VisibilityGeneralState.LOAD_SUCCESSFUL);
        resetTimesLoaded();
        //TODO mariusm 22/05/14 There really should be a better way to check if impression exist
        if (mAdService != null &&
                mAdService.getAdServingEntity() != null &&
                mAdService.getAdServingEntity().getAdEntity() != null &&
                mAdService.getAdServingEntity().getAdEntity().getTagDataEntity() != null &&
                mAdService.getAdServingEntity().getAdEntity().getTagDataEntity().getImpressionUrl() != null)
            loadImpression(mAdService.getAdServingEntity().getAdEntity().getTagDataEntity().getImpressionUrl());
    }

    @Override
    public void onNetworkError(NetworkTask request, NetworkError networkError) {
//        if (mListener != null)
//            mListener.onNetworkError(request, networkError);
    }

    @Override
    public void onContentLoadFailed() {
        getInnerView().showContent(null);
    }

    @Override
    public void onSliderFinishedHiding() {
        getInnerView().setVisibility(View.INVISIBLE);
    }

    @Override
    public void onSliderFinishedShowing() {
        getInnerView().setVisibility(View.VISIBLE);
    }

    @Override
    public void onSliderStartedHiding() {}

    @Override
    public void onSliderStartedShowing() {}

    /* Start service runnable is needed as of this moment there is no better way to check
             * if view is being restored from an instance, or is created anew.
             */
    private Runnable mStartServiceRunnable = null;

    @Override
    protected void onWindowVisibilityChanged(final int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
            mStartServiceRunnable = new Runnable() {
                @Override
                public void run() {
                    if (mServiceInstanceBundle == null) {
                        startService();
                    } else {
                        resumeService();
                    }
                }
            };
            /* Service starting is delayed here as for the same instance can be started
            from the on #onRestoreInstanceState(Parcelable). If it is started from there,
            this Runnable is not used.
             */
            postDelayed(mStartServiceRunnable, 500);
        } else {
            if (mStartServiceRunnable != null)
                removeCallbacks(mStartServiceRunnable);
            mStartServiceRunnable = null;
            stopService();
        }
    }

    /**
     * Stops service from being runned
     */
    protected void stopService() {
        super.stopService();
        getInnerView().getMraidBridge().changeVisibility(false, true);
        if (mAdService != null) {
            mAdService.deleteObserver(this);
            mAdService.stopService();
        }
    }

    /**
     * Resume service to run from the last time
     */
    protected void resumeService() {
        super.resumeService();
        if (mAdService == null)
            mAdService = new AdService(this);
        mAdService.addObserver(this);
        mAdService.restoreInstanceWithBundle(mServiceInstanceBundle);
        mServiceInstanceBundle = null;
    }

    /**
     * Starts to run service anew
     */
    protected void startService() {
        super.startService();
        if (mAdService == null)
            mAdService = new AdService(this);
        mAdService.addObserver(this);
        if (getInnerView() != null && getInnerView().getTimesLoaded() > 0)
            resumeService();
        else {
            if (mDeviceId == null) {
                Thread thr = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mDeviceId = MraidDeviceIdProperty.createWithDeviceId(mContext);
                            mAdService.startService();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                thr.start();
            } else {
                mAdService.startService();
            }
        }
    }

    @Override
    protected void pauseService() {
        super.pauseService();
        if (mAdService != null) {
            mAdService.pauseService();
        }
    }

    //    @Override
//    protected Parcelable onSaveInstanceState() {
//        Parcelable superState = super.onSaveInstanceState();
//        SavedState savedState = new SavedState(superState);
//        if (mAdService != null)
//            savedState.saveBundle = mAdService.getSaveInstanceBundle();
//        savedState.deviceIdProperty = mDeviceId;
//        return savedState;
//    }

//    @Override
//    protected void onRestoreInstanceState(Parcelable state) {
//        if(!(state instanceof SavedState)) {
//            super.onRestoreInstanceState(state);
//            return;
//        }
//        SavedState savedState = (SavedState)state;
//        super.onRestoreInstanceState(savedState.getSuperState());
//        mServiceInstanceBundle = savedState.saveBundle;
//        resetTimesLoaded();
//        mDeviceId = savedState.deviceIdProperty;
//        if (mStartServiceRunnable != null) {
//            removeCallbacks(mStartServiceRunnable);
//            post(mStartServiceRunnable);
//        }
//    }

//    private static class SavedState extends BaseSavedState {
//        public Bundle saveBundle;
//        public MraidDeviceIdProperty deviceIdProperty;
//
//        public SavedState(Parcel source) {
//            super(source);
//            saveBundle = source.readBundle();
//            if (source.readInt() == 1)
//            deviceIdProperty = source.readParcelable(MraidDeviceIdProperty.class.getClassLoader());
//        }
//        public SavedState(Parcelable superState) {
//            super(superState);
//        }
//
//        @Override
//        public void writeToParcel(Parcel dest, int flags) {
//            super.writeToParcel(dest, flags);
//            dest.writeBundle(saveBundle);
//            dest.writeInt((deviceIdProperty != null)?1:0);
//            if (deviceIdProperty != null)
//                dest.writeParcelable(deviceIdProperty, 0);
//        }
//
//        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
//
//            @Override
//            public SavedState createFromParcel(Parcel source) {
//                return new SavedState(source);
//            }
//
//            @Override
//            public SavedState[] newArray(int size) {
//                return new SavedState[size];
//            }
//        };
//    }

    @Override
    protected void onVisibilityCallback(boolean isVisible) {
        if (mListener != null)
            mListener.onAdVisibilityChange(isVisible);
        getInnerView().getMraidBridge().changeVisibility(isVisible, false);
    }

    private void resetTimesLoaded() {
        if (getGeneralState() == AdformEnum.VisibilityGeneralState.LOAD_FAIL)
            getInnerView().setTimesLoaded(0);
    }

    public void setListener(CoreAdViewListener l) {
        this.mListener = l;
    }

    @Override
    public MraidDeviceIdProperty getDeviceId() {
        return mDeviceId;
    }

    @Override
    public String getUserAgent() {
        return getInnerView().getUserAgent();
    }

    @Override
    public void onMraidClose() {
        // Nothing should be done in this case
    }

    @Override
    public void onMraidSetOrientation(boolean allowOrientationChange, AdformEnum.ForcedOrientation forcedOrientation) {
        // Nothing should be done
    }

    @Override
    public void onMraidUseCustomClose(boolean shouldUseCustomClose) {
        // Nothing should be done
    }

    @Override
    public void onMraidOpen(String url) {
        super.onMraidOpen(url);
    }

    @Override
    public void destroy() {
        if (mAdService != null)
            mAdService.stopService();
        mAdService = null;
        if (mSlidingManager != null)
            mSlidingManager.destroy();
        mSlidingManager = null;
        mListener = null;
        mAdformContentLoadManager.setListener(null);
        mAdformContentLoadManager = null;
        super.destroy();
    }
}
