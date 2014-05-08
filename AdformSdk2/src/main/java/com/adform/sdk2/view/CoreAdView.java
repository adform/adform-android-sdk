package com.adform.sdk2.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
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
import com.adform.sdk2.network.base.ito.network.NetworkError;
import com.adform.sdk2.resources.AdDimension;
import com.adform.sdk2.utils.ContentLoadManager;
import com.adform.sdk2.utils.SlidingManager;
import com.adform.sdk2.utils.Utils;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by mariusm on 24/04/14.
 * Base view that should be implemented when adding a banner
 */
public class CoreAdView extends RelativeLayout implements Observer,
        SlidingManager.SliderableWidget, BannerView.BannerViewListener,
        ContentLoadManager.ContentLoaderListener, AdService.AdServiceBinder {

    // Special variables that can be set by the view
    public static final String MASTER_ID = "master_id";
    public static final String API_VERSION = "api_version";
    public static final String HIDDEN_STATE = "hidden_state";

    public interface CoreAdViewListener {
        public void onAdVisibilityChange(ViewState viewState);
    }
    public enum ViewState {
        SHOWN(0),
        HIDDEN(1);

        private int value;

        private ViewState(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static ViewState parseType(int status) {
            switch (status) {
                case 0: return SHOWN;
                case 1: return HIDDEN;
                default: return SHOWN;
            }
        }
        public static String printType(ViewState state) {
            switch (state) {
                case SHOWN: return "SHOWN";
                case HIDDEN: return "HIDDEN";
            }
            return null;
        }

    }

    private Context mContext;
    private AdService mAdService;
    /** Bundle that packs AdService last state when saving view instance */
    private Bundle mServiceInstanceBundle;
    private SlidingManager mSlidingManager;
    private BannerView mBannerView;
    private ContentLoadManager mContentLoadManager;
    private CoreAdViewListener mListener;
    private ViewState mViewState = ViewState.HIDDEN;
    private AdDimension mPlacementDimen;
    // Should be taken from some kind of configuration
    private String mMasterId = "1234";
    // Should be taken from some kind of configuration
    private String mApiVersion = "0.1";
    private MraidDeviceIdProperty mDeviceId;
    // Set hidden state from outside, as when the view is hidden should it be INVISIBLE or GONE
    private int mHiddenState = GONE;

    private BroadcastReceiver mScreenStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                stopService();
            } else if (intent != null && intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                startService();
            }
        }
    };

    public CoreAdView(Context context) {
        this(context, null);
    }

    public CoreAdView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CoreAdView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        mPlacementDimen = new AdDimension(mContext);
        //TODO mariusm 08/05/14 Make a view parameter picker from view xml attributes here
        if (mContext instanceof CoreAdViewListener)
            mListener = (CoreAdViewListener)mContext;
        mSlidingManager = new SlidingManager(this);
        mContentLoadManager = new ContentLoadManager(this);
        setBackgroundResource(android.R.color.transparent);


        ViewGroup.LayoutParams params = new RelativeLayout.LayoutParams(
                mPlacementDimen.getWidth(),
                mPlacementDimen.getHeight());
        setLayoutParams(params);

        mBannerView = new BannerView(mContext);
        mBannerView.setListener(this);
        // TODO: Change this to something nicer. This must be binded, as this lets instance to be saved
        mBannerView.setId(156554);
        addView(mBannerView);

        setVisibility(INVISIBLE);
    }

    private void initializeCustomParameters(AttributeSet attributes) {
        if (attributes != null) {
            int count = attributes.getAttributeCount();
            for (int i = 0; i < count; i++) {
                String name = attributes.getAttributeName(i);
                if (name.equals(MASTER_ID)) {
                    mMasterId = attributes.getAttributeValue(i);
                } else if (name.equals(API_VERSION)) {
                    mApiVersion = attributes.getAttributeValue(i);
                } else if (name.equals(HIDDEN_STATE)) {
                    String hiddenState = attributes.getAttributeValue(i);
                    if (hiddenState.equals("invisible"))
                        mHiddenState = View.INVISIBLE;
                    else if (hiddenState.equals("gone"))
                        mHiddenState = View.GONE;
                    else
                        mHiddenState = View.INVISIBLE;
                }
            }
        }
    }

    /** An update from configuration json */
    @Override
    public void update(Observable observable, Object data) {
        if (data instanceof NetworkError
                && ((NetworkError) data).getType() == NetworkError.Type.NETWORK) {
            mBannerView.flipLoadedContent();
            setViewState(ViewState.SHOWN);
            return;
        }
        if (data instanceof NetworkError
                && ((NetworkError) data).getType() == NetworkError.Type.SERVER) {
            mBannerView.showContent(null, false);
            mSlidingManager.turnOff();
            setViewState(ViewState.HIDDEN);
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
                setViewState(ViewState.HIDDEN);
            }
        }
    }

    @Override
    public void onContentMraidLoadSuccessful(String content) {
        mBannerView.showContent(content, true);
    }

    @Override
    public void onContentLoadSuccessful(String content) {
        mBannerView.showContent(content, false);
    }

    @Override
    public void onContentRestore(boolean state) {
        if (state)
            mSlidingManager.turnOnImmediate();
        else
            mSlidingManager.turnOffImmediate();
    }

    @Override
    public void onContentRender() {
        mSlidingManager.turnOn();
        setViewState(ViewState.SHOWN);
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
    public int getHiddenState() {
        return mHiddenState;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        mContext.registerReceiver(mScreenStateReceiver, filter);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mContext.unregisterReceiver(mScreenStateReceiver);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
            if (mServiceInstanceBundle == null) {
                startService();
            } else {
                resumeService();
            }
        } else {
            stopService();
        }
    }

    @Override
    public AdDimension getAdDimension() {
        return mPlacementDimen;
    }

    @Override
    public String getMasterId() {
        return mMasterId;
    }

    @Override
    public String getVersion() {
        return mApiVersion;
    }

    @Override
    public MraidDeviceIdProperty getDeviceId() {
        return mDeviceId;
    }

    /**
     * Stops service from being runned
     */
    private void stopService() {
        mAdService.deleteObserver(this);
        mAdService.stopService();
    }

    /**
     * Resume service to run from the last time
     */
    private void resumeService() {
        if (mAdService == null)
            mAdService = new AdService(this);
        mAdService.addObserver(this);
        mAdService.restoreInstanceWithBundle(mServiceInstanceBundle);
        mServiceInstanceBundle = null;
    }

    /**
     * Starts to run service anew
     */
    private void startService() {
        if (mAdService == null)
            mAdService = new AdService(this);
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
        savedState.viewState = getViewState().getValue();
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
        setViewState(ViewState.parseType(savedState.viewState));
        mDeviceId = savedState.deviceIdProperty;
    }

    private static class SavedState extends BaseSavedState {
        public Bundle saveBundle;
        public int viewState;
        public MraidDeviceIdProperty deviceIdProperty;

        public SavedState(Parcel source) {
            super(source);
            saveBundle = source.readBundle();
            viewState = source.readInt();
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
            dest.writeInt(viewState);
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

    public void setViewState(ViewState state) {
        this.mViewState = state;
        if (mViewState == ViewState.HIDDEN)
            mBannerView.setTimesLoaded(0);
        if (mListener != null)
            mListener.onAdVisibilityChange(mViewState);
    }

    public ViewState getViewState() {
        return mViewState;
    }

    public void setListener(CoreAdViewListener l) {
        this.mListener = l;
    }
}
