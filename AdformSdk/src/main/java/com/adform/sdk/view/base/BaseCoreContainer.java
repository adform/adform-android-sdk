package com.adform.sdk.view.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import com.adform.sdk.interfaces.AdformRequestParamsListener;
import com.adform.sdk.network.app.RawNetworkTask;
import com.adform.sdk.network.app.entities.entities.RawResponse;
import com.adform.sdk.network.base.ito.network.NetworkRequest;
import com.adform.sdk.network.base.ito.network.NetworkResponse;
import com.adform.sdk.network.base.ito.network.NetworkTask;
import com.adform.sdk.network.base.ito.network.SuccessListener;
import com.adform.sdk.resources.AdDimension;
import com.adform.sdk.resources.MraidJavascript;
import com.adform.sdk.utils.AdformEnum;
import com.adform.sdk.utils.Utils;
import com.adform.sdk.utils.VisibilityPositionManager;
import com.adform.sdk.view.inner.AdWebView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Created by mariusm on 27/05/14.
 */
public abstract class BaseCoreContainer extends RelativeLayout implements
        VisibilityPositionManager.VisibilityManagerListener, BaseInnerContainer.BaseAdViewListener,
        AdformRequestParamsListener, AdWebView.NativeWebviewListener {
    // Special variables that can be set by the view
    public static final String MASTER_ID = "master_id";
    public static final String API_VERSION = "api_version";
    public static final String HIDDEN_STATE = "hidden_state";

    protected Context mContext;
    /** States that helps handle various states for the view visibility */
    private AdformEnum.VisibilityGeneralState mVisibilityGeneralState = AdformEnum.VisibilityGeneralState.LOAD_FAIL;
    private AdformEnum.VisibilityOnScreenState mVisibilityOnScreenState = AdformEnum.VisibilityOnScreenState.OFF_SCREEN;
    /** Manager that helps to handle visibility and position changes for the view */
    private VisibilityPositionManager mVisibilityPositionManager;
    public static float sDeviceDensity;
    private HashMap<String, String> mCustomParams;
    // Should be taken from some kind of configuration
    private int mMasterId = 0;
    private int mPublisherId = 0; // Some hardcoded number, probably will be used later on
    // Should be taken from some kind of configuration
    private String mApiVersion = "1.0";
    // Set hidden state from outside, as when the view is hidden should it be INVISIBLE or GONE
    protected int mHiddenState = INVISIBLE;
    private boolean isContentMraid = false;
    private boolean isAnimating;
    protected AdDimension mPlacementDimen;

    public BaseCoreContainer(Context context) {
        this(context, null);
    }

    public BaseCoreContainer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseCoreContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;

        initializeCustomParameters(attrs);
        sDeviceDensity = mContext.getResources().getDisplayMetrics().density;
        mPlacementDimen = initAdDimen();
        mCustomParams = new HashMap<String, String>();
        setBackgroundResource(android.R.color.transparent);
        BaseInnerContainer innerView = initInnerView();
        mVisibilityPositionManager = new VisibilityPositionManager(mContext, this, innerView.getMraidBridge());
        addView(innerView);
    }

    /**
     * Initializes inner used view that display an ad
     * @return initialized inner view
     */
    protected abstract BaseInnerContainer initInnerView();

    /**
     * Initializes created ad used dimensions.
     * @return return ad dimensions
     */
    protected abstract AdDimension initAdDimen();
    protected abstract void startService();
    protected abstract void stopService();
    protected abstract void resumeService();

    protected void initializeCustomParameters(AttributeSet attributes) {
        if (attributes != null) {
            int count = attributes.getAttributeCount();
            for (int i = 0; i < count; i++) {
                String name = attributes.getAttributeName(i);
                if (name.equals(MASTER_ID)) {
                    String masterAttr = attributes.getAttributeValue(i);
                    try {
                        mMasterId = Integer.parseInt(masterAttr);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
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

    protected void loadImpression(String impressionUrl) {
        // Loading impression
        Utils.p("Sending impression...");
        RawNetworkTask impressionTask =
                new RawNetworkTask(NetworkRequest.Method.GET,
                        impressionUrl);
        impressionTask.setSuccessListener(new SuccessListener<RawResponse>() {
            @Override
            public void onSuccess(NetworkTask request, NetworkResponse<RawResponse> response) {
                Utils.p("Impression sent");
            }
        });
        impressionTask.execute();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mVisibilityPositionManager.checkVisibilityService();
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

    // -------------------
    // Visibility handling
    // -------------------

    /**
     * Final call when visibility has changed. This should be called when something should be indicated
     * as visibility state changed
     * @param isVisible
     */
    protected abstract void onVisibilityCallback(boolean isVisible);

    // Method called from visibility service
    @Override
    public void onVisibilityManagerUpdate(boolean visibility) {
        setViewState((visibility) ? AdformEnum.VisibilityOnScreenState.ON_SCREEN :
                AdformEnum.VisibilityOnScreenState.OFF_SCREEN);
    }
    public void setViewState(AdformEnum.VisibilityGeneralState state) {
        setViewState(state, mVisibilityOnScreenState);
    }

    public void setViewState(AdformEnum.VisibilityOnScreenState state) {
        setViewState(mVisibilityGeneralState, state);
    }

    public void setViewState(AdformEnum.VisibilityGeneralState generalState,
                             AdformEnum.VisibilityOnScreenState onScreenState) {
        this.mVisibilityGeneralState = generalState;
        this.mVisibilityOnScreenState = onScreenState;
        boolean newVisibility = (mVisibilityGeneralState == AdformEnum.VisibilityGeneralState.LOAD_SUCCESSFUL &&
                mVisibilityOnScreenState == AdformEnum.VisibilityOnScreenState.ON_SCREEN);
        if (!isAnimating) {
            onVisibilityCallback(newVisibility);
        }
    }

    protected AdformEnum.VisibilityGeneralState getGeneralState() {
        return mVisibilityGeneralState;
    }
    protected AdformEnum.VisibilityOnScreenState getOnScreenState() {
        return mVisibilityOnScreenState;
    }

    public boolean isAdVisible() {
        if (mVisibilityGeneralState == AdformEnum.VisibilityGeneralState.LOAD_SUCCESSFUL ||
                mVisibilityOnScreenState == AdformEnum.VisibilityOnScreenState.ON_SCREEN)
            return true;
        return false;
    }

    public void setAnimating(boolean isAnimating) {
        this.isAnimating = isAnimating;
        if (isAnimating) {
            onVisibilityCallback(false);
        } else {
            setViewState(mVisibilityGeneralState, mVisibilityOnScreenState);
        }
    }

    // -------------------------
    // Custom parameter handling
    // -------------------------
    public void addCustomParam(String name, String value) {
        if (mCustomParams != null && name != null && value != null)
            mCustomParams.put(name, value);
    }

    public void clearCustomParams(){
        if (mCustomParams != null)
            mCustomParams.clear();
    }

    // -----------------
    // Getters / Setters
    // -----------------

    @Override
    public AdDimension getAdDimension() {
        return mPlacementDimen;
    }

    @Override
    public int getMasterId() {
        return mMasterId;
    }

    public void setMasterId(int masterId) {
        this.mMasterId = masterId;
    }

    @Override
    public String getVersion() {
        return mApiVersion;
    }

    @Override
    public HashMap<String, String> getCustomParameters() {
        return mCustomParams;
    }

    @Override
    public boolean isCustomParamsEmpty() {
        if ((mCustomParams != null && mCustomParams.size() == 0) || mCustomParams == null)
            return true;
        return false;
    }

    @Override
    public String getLocale() {
        return mContext.getResources().getConfiguration().locale.toString();
    }

    @Override
    public int getPublisherId() {
        return mPublisherId;
    }

    public void setPublisherId(int publisherId) {
        this.mPublisherId = publisherId;
    }

    public void setContentMraid(boolean isContentMraid) {
        this.isContentMraid = isContentMraid;
        mVisibilityPositionManager.checkVisibilityService();
    }

    @Override
    public boolean isContentMraid() {
        return isContentMraid;
    }

    @Override
    public View getView() {
        return this;
    }

    // -------------------
    // Broadcast receivers
    // -------------------
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

    // ---------------
    // Instance saving
    // ---------------
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.visibilityGeneralState = getGeneralState().getValue();
        savedState.visibilityOnScreenState = getOnScreenState().getValue();
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
        setContentMraid(savedState.isContentMraid);
        mCustomParams = savedState.customParams;
        setViewState(AdformEnum.VisibilityGeneralState.parseType(savedState.visibilityGeneralState),
                AdformEnum.VisibilityOnScreenState.parseType(savedState.visibilityOnScreenState));
        setAnimating(false);
    }

    private static class SavedState extends BaseSavedState {
        public int visibilityGeneralState;
        public int visibilityOnScreenState;
        public HashMap customParams;
        public boolean isContentMraid;

        public SavedState(Parcel source) {
            super(source);
            visibilityGeneralState = source.readInt();
            visibilityOnScreenState = source.readInt();
            isContentMraid = (source.readInt() == 1);
            customParams = source.readHashMap(String.class.getClassLoader());
        }
        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(visibilityGeneralState);
            dest.writeInt(visibilityOnScreenState);
            dest.writeInt((isContentMraid)?1:0);
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

    // -----------------------------
    // Callbacks for mraid functions
    // -----------------------------

    @Override
    public void onMraidOpen(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://" + url;
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(browserIntent);
    }

    // This should always be implemented
//    @Override
//    public void onMraidClose() {}
}
