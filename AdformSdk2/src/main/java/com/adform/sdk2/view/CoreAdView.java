package com.adform.sdk2.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import com.adform.sdk2.mraid.properties.MraidDeviceIdProperty;
import com.adform.sdk2.network.app.entities.entities.AdServingEntity;
import com.adform.sdk2.mraid.AdService;
import com.adform.sdk2.network.base.ito.network.*;
import com.adform.sdk2.resources.AdDimension;
import com.adform.sdk2.utils.*;
import com.adform.sdk2.view.base.BaseCoreContainer;
import com.adform.sdk2.view.base.BaseInnerContainer;
import com.adform.sdk2.view.inner.InnerBannerView;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by mariusm on 24/04/14.
 * Base view that should be implemented when adding a banner
 */
public class CoreAdView extends BaseCoreContainer implements Observer,
        SlidingManager.SliderableWidget, ContentLoadManager.ContentLoaderListener,
        AdService.AdServiceBinder {


    public interface CoreAdViewListener {
        public void onAdVisibilityChange(boolean visible);
        public void onNetworkError(NetworkTask request, NetworkError networkError);
    }

    private AdService mAdService;
    /** Bundle that packs AdService last state when saving view instance */
    private Bundle mServiceInstanceBundle;
    /** Manager that handles core container sliding animation */
    private SlidingManager mSlidingManager;
    private InnerBannerView mBannerView;
    /** Manager that handles contract (json) loading */
    private ContentLoadManager mContentLoadManager;
    /** An interface for calling back handler functions for outer control */
    private CoreAdViewListener mListener;

    /* Basic values that store persistent information */
    private MraidDeviceIdProperty mDeviceId;

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
        mContentLoadManager = new ContentLoadManager(this);
        ViewGroup.LayoutParams params = new RelativeLayout.LayoutParams(
                mPlacementDimen.getWidth(),
                mPlacementDimen.getHeight());
        setLayoutParams(params);
        setVisibility(INVISIBLE);
    }

    @Override
    protected AdDimension initAdDimen() {
        //TODO mariusm 27/05/14 It uses default constructor with default parameters. This should be smarter
        return new AdDimension(mContext);
    }

    @Override
    protected View initInnerView() {
        mBannerView = new InnerBannerView(mContext);
        mBannerView.setListener(this);
        // TODO: Change this to something nicer. This must be binded, as this lets instance to be saved
        mBannerView.setId(156554);
        return mBannerView;
    }

    /** An update from configuration json */
    @Override
    public void update(Observable observable, Object data) {
        if (data instanceof NetworkError
                && ((NetworkError) data).getType() == NetworkError.Type.NETWORK) {
//            mBannerView.flipLoadedContent();
            mBannerView.showContent(null, false);
            mSlidingManager.turnOff();
            setViewState(AdformEnum.VisibilityGeneralState.LOAD_FAIL);
            resetTimesLoaded();
            return;
        }
        if (data instanceof NetworkError
                && ((NetworkError) data).getType() == NetworkError.Type.SERVER) {
            mBannerView.showContent(null, false);
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
                mContentLoadManager.loadContent(content);
            } else {
                mBannerView.showContent(null, false);
                mSlidingManager.turnOff();
                setViewState(AdformEnum.VisibilityGeneralState.LOAD_FAIL);
                resetTimesLoaded();
            }
        }
    }

    @Override
    public void onContentMraidLoadSuccessful(String content) {
        setContentMraid(true);
        mBannerView.showContent(content, true);
    }

    @Override
    public void onContentLoadSuccessful(String content) {
        setContentMraid(false);
        mBannerView.showContent(content, false);
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
        if (mListener != null)
            mListener.onNetworkError(request, networkError);
    }

    @Override
    public void onContentLoadFailed() {
        mBannerView.showContent(null, false);
    }

    @Override
    public void onSliderAnimating(final Animation animation) {
        post(new Runnable() {
            @Override
            public void run() {
                mBannerView.startAnimation(animation);
            }
        });
    }

    @Override
    public void onSliderVisibilityChange(final int visibility) {
        post(new Runnable() {
            @Override
            public void run() {
                setVisibility(visibility);
            }
        });
    }

    @Override
    public void onSliderPreOn() {
        post(new Runnable() {
            @Override
            public void run() {
                setVisibility(View.VISIBLE);
            }
        });
    }

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
        if (mAdService != null) {
            mAdService.deleteObserver(this);
            mAdService.stopService();
        }
    }

    /**
     * Resume service to run from the last time
     */
    protected void resumeService() {
        if (mAdService == null)
            mAdService = new AdService(this, this);
        mAdService.addObserver(this);
        mAdService.restoreInstanceWithBundle(mServiceInstanceBundle);
        mServiceInstanceBundle = null;
    }

    /**
     * Starts to run service anew
     */
    protected void startService() {
        if (mAdService == null)
            mAdService = new AdService(this, this);
        mAdService.addObserver(this);
        if (mBannerView != null && mBannerView.getTimesLoaded() > 0)
            resumeService();
        else {
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
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        if (mAdService != null)
            savedState.saveBundle = mAdService.getSaveInstanceBundle();
        savedState.deviceIdProperty = mDeviceId;
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState savedState = (SavedState)state;
        super.onRestoreInstanceState(savedState.getSuperState());
        mServiceInstanceBundle = savedState.saveBundle;
        resetTimesLoaded();
        mDeviceId = savedState.deviceIdProperty;
        if (mStartServiceRunnable != null) {
            removeCallbacks(mStartServiceRunnable);
            post(mStartServiceRunnable);
        }
    }

    private static class SavedState extends BaseSavedState {
        public Bundle saveBundle;
        public MraidDeviceIdProperty deviceIdProperty;

        public SavedState(Parcel source) {
            super(source);
            saveBundle = source.readBundle();
            if (source.readInt() == 1)
            deviceIdProperty = source.readParcelable(MraidDeviceIdProperty.class.getClassLoader());
        }
        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeBundle(saveBundle);
            dest.writeInt((deviceIdProperty != null)?1:0);
            if (deviceIdProperty != null)
                dest.writeParcelable(deviceIdProperty, 0);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {

            @Override
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    @Override
    protected void onVisibilityCallback(boolean isVisible) {
        if (mListener != null)
            mListener.onAdVisibilityChange(isVisible);
        mBannerView.changeVisibility(isVisible);
    }

    private void resetTimesLoaded() {
        if (getGeneralState() == AdformEnum.VisibilityGeneralState.LOAD_FAIL)
            mBannerView.setTimesLoaded(0);
    }

    public void setListener(CoreAdViewListener l) {
        this.mListener = l;
    }

    @Override
    public int getHiddenState() {
        return mHiddenState;
    }

    @Override
    public MraidDeviceIdProperty getDeviceId() {
        return mDeviceId;
    }

    @Override
    public String getUserAgent() {
        return mBannerView.getUserAgent();
    }
}
