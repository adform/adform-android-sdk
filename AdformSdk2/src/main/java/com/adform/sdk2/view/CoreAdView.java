package com.adform.sdk2.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.adform.sdk2.utils.VisibilityManager;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by mariusm on 24/04/14.
 * Base view that should be implemented when adding a banner
 */
public class CoreAdView extends RelativeLayout implements Observer,
        SlidingManager.SliderableWidget, BannerView.BannerViewListener,
        ContentLoadManager.ContentLoaderListener, AdService.AdServiceBinder,
        VisibilityManager.VisibilityManagerListener {

    // Special variables that can be set by the view
    public static final String MASTER_ID = "master_id";
    public static final String API_VERSION = "api_version";
    public static final String HIDDEN_STATE = "hidden_state";

    public interface CoreAdViewListener {
        public void onAdVisibilityChange(boolean visible);
    }
    public enum ViewState {
        LOAD_SUCCESSFUL(0),
        LOAD_FAIL(1),
        ON_SCREEN(2),
        OFF_SCREEN(3);

        private int value;

        private ViewState(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static ViewState parseType(int status) {
            switch (status) {
                case 0: return LOAD_SUCCESSFUL;
                case 1: return LOAD_FAIL;
                case 2: return ON_SCREEN;
                case 3: return OFF_SCREEN;
                default: return LOAD_SUCCESSFUL;
            }
        }
        public static String printType(ViewState state) {
            switch (state) {
                case LOAD_SUCCESSFUL: return "LOAD_SUCCESSFUL";
                case LOAD_FAIL: return "LOAD_FAIL";
                case ON_SCREEN: return "ON_SCREEN";
                case OFF_SCREEN: return "OFF_SCREEN";
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
    private ViewState mInternalViewState = ViewState.LOAD_FAIL;
    private AdDimension mPlacementDimen;
    // Should be taken from some kind of configuration
    private String mMasterId = "1234";
    // Should be taken from some kind of configuration
    private String mApiVersion = "0.1";
    private MraidDeviceIdProperty mDeviceId;
    // Set hidden state from outside, as when the view is hidden should it be INVISIBLE or GONE
    private int mHiddenState = GONE;
    private VisibilityManager mVisibilityManager;

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
        initializeCustomParameters(attrs);
        mPlacementDimen = new AdDimension(mContext);
        //TODO mariusm 08/05/14 Make a view parameter picker from view xml attributes here
        if (mContext instanceof CoreAdViewListener)
            mListener = (CoreAdViewListener)mContext;
        mSlidingManager = new SlidingManager(this);
        mVisibilityManager = new VisibilityManager(mContext, this);
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
            setViewState(ViewState.LOAD_SUCCESSFUL);
            resetTimesLoaded();
            return;
        }
        if (data instanceof NetworkError
                && ((NetworkError) data).getType() == NetworkError.Type.SERVER) {
            mBannerView.showContent(null, false);
            mSlidingManager.turnOff();
            setViewState(ViewState.LOAD_FAIL);
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
                setViewState(ViewState.LOAD_FAIL);
                resetTimesLoaded();
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
        if (mListener != null)
            mListener.onAdVisibilityChange(state);
    }

    @Override
    public void onContentRender() {
        mSlidingManager.turnOn();
        setViewState(ViewState.LOAD_SUCCESSFUL);
        resetTimesLoaded();
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
//                mBannerView.setVisibility(View.VISIBLE);
                setVisibility(visibility);
            }
        });
    }

    @Override
    public void onSliderPreOn() {
        post(new Runnable() {
            @Override
            public void run() {
//                mBannerView.setVisibility(View.GONE);
                setVisibility(View.VISIBLE);
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
            postDelayed(mStartServiceRunnable, 500);
        } else {
            if (mStartServiceRunnable != null)
                removeCallbacks(mStartServiceRunnable);
            mStartServiceRunnable = null;
            stopService();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mVisibilityManager.checkVisibilityService();
    }

    @Override
    public void onVisibilityUpdate(boolean visibility) {
        setViewState((visibility) ? ViewState.ON_SCREEN : ViewState.OFF_SCREEN);
    }

    @Override
    public View getView() {
        return this;
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
        if (mAdService != null) {
            mAdService.deleteObserver(this);
            mAdService.stopService();
        }
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
        resetTimesLoaded();
        mDeviceId = savedState.deviceIdProperty;
        if (mStartServiceRunnable != null) {
            removeCallbacks(mStartServiceRunnable);
            post(mStartServiceRunnable);
        }
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

    private void resetTimesLoaded() {
        if (mInternalViewState == ViewState.LOAD_FAIL)
            mBannerView.setTimesLoaded(0);
    }

    public void setViewState(ViewState state) {
        if (mInternalViewState == ViewState.LOAD_FAIL &&
                (state == ViewState.OFF_SCREEN || state == ViewState.ON_SCREEN))
            return;
        if (state == mInternalViewState)
            return;
        this.mInternalViewState = state;
        if (mListener != null)
            mListener.onAdVisibilityChange((mInternalViewState == ViewState.LOAD_SUCCESSFUL ||
                    mInternalViewState == ViewState.ON_SCREEN));
    }

    private ViewState getViewState() {
        return mInternalViewState;
    }

    public boolean isAdVisible() {
        if (mInternalViewState == ViewState.LOAD_SUCCESSFUL ||
                mInternalViewState == ViewState.ON_SCREEN)
            return true;
        return false;
    }

    public void setListener(CoreAdViewListener l) {
        this.mListener = l;
    }
}
