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
import com.adform.sdk2.utils.*;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by mariusm on 24/04/14.
 * Base view that should be implemented when adding a banner
 */
public class CoreAdView extends RelativeLayout implements Observer,
        SlidingManager.SliderableWidget, BannerView.BannerViewListener,
        ContentLoadManager.ContentLoaderListener, AdService.AdServiceBinder,
        VisibilityPositionManager.VisibilityManagerListener {

    // Special variables that can be set by the view
    public static final String MASTER_ID = "master_id";
    public static final String API_VERSION = "api_version";
    public static final String HIDDEN_STATE = "hidden_state";

    public interface CoreAdViewListener {
        public void onAdVisibilityChange(boolean visible);
    }

    public enum VisibilityOnScreenState {
        ON_SCREEN(0),
        OFF_SCREEN(1);
        private int value;

        private VisibilityOnScreenState(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static VisibilityOnScreenState parseType(int status) {
            switch (status) {
                case 1:
                    return ON_SCREEN;
                case 2:
                    return OFF_SCREEN;
                default:
                    return OFF_SCREEN;
            }
        }

        public static String printType(VisibilityOnScreenState state) {
            switch (state) {
                case ON_SCREEN:
                    return "ON_SCREEN";
                case OFF_SCREEN:
                    return "OFF_SCREEN";
            }
            return null;
        }
    }
    public enum VisibilityGeneralState {
        LOAD_SUCCESSFUL(0),
        LOAD_FAIL(1);

        private int value;

        private VisibilityGeneralState(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static VisibilityGeneralState parseType(int status) {
            switch (status) {
                case 0: return LOAD_SUCCESSFUL;
                case 1: return LOAD_FAIL;
                default: return LOAD_SUCCESSFUL;
            }
        }
        public static String printType(VisibilityGeneralState state) {
            switch (state) {
                case LOAD_SUCCESSFUL: return "LOAD_SUCCESSFUL";
                case LOAD_FAIL: return "LOAD_FAIL";
            }
            return null;
        }

    }

    private Context mContext;
    private AdService mAdService;
    /** Bundle that packs AdService last state when saving view instance */
    private Bundle mServiceInstanceBundle;
    /** Manager that handles core container sliding animation */
    private SlidingManager mSlidingManager;
    private BannerView mBannerView;
    /** Manager that handles contract (json) loading */
    private ContentLoadManager mContentLoadManager;
    /** An interface for calling back handler functions for outer control */
    private CoreAdViewListener mListener;
    /** States that helps handle various states for the view visibility */
    private VisibilityGeneralState mVisibilityGeneralState = VisibilityGeneralState.LOAD_FAIL;
    private VisibilityOnScreenState mVisibilityOnScreenState = VisibilityOnScreenState.OFF_SCREEN;
    private boolean isAnimating;
    /** Manager that helps to handle visibility and position changes for the view */
    private VisibilityPositionManager mVisibilityPositionManager;

    /* Basic values that store persistent information */
    private AdDimension mPlacementDimen;
    // Should be taken from some kind of configuration
    private String mMasterId = "1234";
    private String mPublisherId = "654321"; // Some hardcoded number, probably will be used later on
    // Should be taken from some kind of configuration
    private String mApiVersion = "1.0";
    private MraidDeviceIdProperty mDeviceId;
    // Set hidden state from outside, as when the view is hidden should it be INVISIBLE or GONE
    private int mHiddenState = INVISIBLE;
    private HashMap<String, String> mCustomParams;
    private boolean isContentMraid = false;

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
        mVisibilityPositionManager = new VisibilityPositionManager(mContext, this);
        mContentLoadManager = new ContentLoadManager(this);
        mCustomParams = new HashMap<String, String>();
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
            setViewState(VisibilityGeneralState.LOAD_SUCCESSFUL);
            resetTimesLoaded();
            return;
        }
        if (data instanceof NetworkError
                && ((NetworkError) data).getType() == NetworkError.Type.SERVER) {
            mBannerView.showContent(null, false);
            mSlidingManager.turnOff();
            setViewState(VisibilityGeneralState.LOAD_FAIL);
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
                setViewState(VisibilityGeneralState.LOAD_FAIL);
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
        setViewState(VisibilityGeneralState.LOAD_SUCCESSFUL);
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

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mVisibilityPositionManager.checkVisibilityService();
    }

    @Override
    public void onVisibilityUpdate(boolean visibility) {
        setViewState((visibility) ? VisibilityOnScreenState.ON_SCREEN : VisibilityOnScreenState.OFF_SCREEN);
    }

    @Override
    public void onCurrentPositionUpdate(ViewCoords viewCoords) {
        if (mBannerView != null)
            mBannerView.changeCurrentPosition(viewCoords);
    }

    @Override
    public void onDefaultPositionUpdate(ViewCoords viewCoords) {
        if (mBannerView != null)
            mBannerView.changeDefaultPosition(viewCoords);
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

    @Override
    public HashMap<String, String> getCustomParameters() {
        return mCustomParams;
    }

    @Override
    public String getUserAgent() {
        return mBannerView.getUserAgent();
    }

    @Override
    public String getLocale() {
        return mContext.getResources().getConfiguration().locale.toString();
    }

    @Override
    public String getPublisherId() {
        return mPublisherId;
    }

    @Override
    public ViewCoords getDefaultPosition() {
        return mVisibilityPositionManager.getDefaultPosition();
    }

    @Override
    public ViewCoords getCurrentPosition() {
        return mVisibilityPositionManager.getCurrentPosition();
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
        savedState.visibilityGeneralState = getGeneralState().getValue();
        savedState.visibilityOnScreenState = getOnScreenState().getValue();
        savedState.deviceIdProperty = mDeviceId;
        savedState.isContentMraid = isContentMraid;
        savedState.customParams = mCustomParams;
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
        setContentMraid(savedState.isContentMraid);
        mCustomParams = savedState.customParams;
        setViewState(VisibilityGeneralState.parseType(savedState.visibilityGeneralState),
                VisibilityOnScreenState.parseType(savedState.visibilityOnScreenState));
        resetTimesLoaded();
        mDeviceId = savedState.deviceIdProperty;
        if (mStartServiceRunnable != null) {
            removeCallbacks(mStartServiceRunnable);
            post(mStartServiceRunnable);
        }
        setAnimating(false);
    }

    private static class SavedState extends BaseSavedState {
        public Bundle saveBundle;
        public int visibilityGeneralState;
        public int visibilityOnScreenState;
        public MraidDeviceIdProperty deviceIdProperty;
        public HashMap customParams;
        public boolean isContentMraid;

        public SavedState(Parcel source) {
            super(source);
            saveBundle = source.readBundle();
            visibilityGeneralState = source.readInt();
            visibilityOnScreenState = source.readInt();
            isContentMraid = (source.readInt() == 1);
            if (source.readInt() == 1)
            deviceIdProperty = source.readParcelable(MraidDeviceIdProperty.class.getClassLoader());
            customParams = source.readHashMap(String.class.getClassLoader());
        }
        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeBundle(saveBundle);
            dest.writeInt(visibilityGeneralState);
            dest.writeInt(visibilityOnScreenState);
            dest.writeInt((isContentMraid)?1:0);
            dest.writeInt((deviceIdProperty != null)?1:0);
            if (deviceIdProperty != null)
                dest.writeParcelable(deviceIdProperty, 0);
            dest.writeMap(customParams);
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
        if (mVisibilityGeneralState == VisibilityGeneralState.LOAD_FAIL)
            mBannerView.setTimesLoaded(0);
    }

    public void setViewState(VisibilityGeneralState state) {
        setViewState(state, mVisibilityOnScreenState);
    }

    public void setViewState(VisibilityOnScreenState state) {
        setViewState(mVisibilityGeneralState, state);
    }

    public void setViewState(VisibilityGeneralState generalState, VisibilityOnScreenState onScreenState) {
        this.mVisibilityGeneralState = generalState;
        this.mVisibilityOnScreenState = onScreenState;
        boolean newVisibility = (mVisibilityGeneralState == VisibilityGeneralState.LOAD_SUCCESSFUL &&
                mVisibilityOnScreenState == VisibilityOnScreenState.ON_SCREEN);
        if (!isAnimating) {
            if (mListener != null)
                mListener.onAdVisibilityChange(newVisibility);
            mBannerView.changeVisibility(newVisibility);
        }
    }

    private VisibilityGeneralState getGeneralState() {
        return mVisibilityGeneralState;
    }
    private VisibilityOnScreenState getOnScreenState() {
        return mVisibilityOnScreenState;
    }

    public boolean isAdVisible() {
        if (mVisibilityGeneralState == VisibilityGeneralState.LOAD_SUCCESSFUL ||
                mVisibilityOnScreenState == VisibilityOnScreenState.ON_SCREEN)
            return true;
        return false;
    }

    public void setAnimating(boolean isAnimating) {
        this.isAnimating = isAnimating;
        if (isAnimating) {
            if (mListener != null)
                mListener.onAdVisibilityChange(false);
            mBannerView.changeVisibility(false);
        } else {
            setViewState(mVisibilityGeneralState, mVisibilityOnScreenState);
        }
    }

    public void setListener(CoreAdViewListener l) {
        this.mListener = l;
    }

    public void addCustomParam(String name, String value) {
        if (mCustomParams != null && name != null && value != null)
            mCustomParams.put(name, value);
    }

    public void clearCustomParams(){
        if (mCustomParams != null)
            mCustomParams.clear();
    }

    public void setContentMraid(boolean isContentMraid) {
        this.isContentMraid = isContentMraid;
        mVisibilityPositionManager.checkVisibilityService();
    }

    @Override
    public boolean isContentMraid() {
        return isContentMraid;
    }
}
