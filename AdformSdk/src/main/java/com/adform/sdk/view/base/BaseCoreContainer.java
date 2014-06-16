package com.adform.sdk.view.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.adform.sdk.interfaces.AdformRequestParamsListener;
import com.adform.sdk.interfaces.CoreInterstitialListener;
import com.adform.sdk.mraid.properties.*;
import com.adform.sdk.network.app.RawNetworkTask;
import com.adform.sdk.network.base.ito.network.NetworkRequest;
import com.adform.sdk.resources.AdDimension;
import com.adform.sdk.utils.AdformEnum;
import com.adform.sdk.utils.MraidBridge;
import com.adform.sdk.utils.Utils;
import com.adform.sdk.utils.entities.ExpandProperties;
import com.adform.sdk.utils.managers.AdformContentLoadManager;
import com.adform.sdk.utils.managers.VisibilityPositionManager;
import com.adform.sdk.view.CoreExpandedView;
import com.adform.sdk.view.CoreInterstitialView;
import com.adform.sdk.view.inner.AdWebView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mariusm on 27/05/14.
 */
public abstract class BaseCoreContainer extends RelativeLayout implements
        VisibilityPositionManager.VisibilityManagerListener, BaseInnerContainer.BaseAdViewListener,
        AdformRequestParamsListener, AdWebView.NativeWebviewListener, MraidBridge.CoreMraidBridgeListener {
    public static final String INNER_EXTRA_WIDTH = "INNER_EXTRA_WIDTH";
    public static final String INNER_EXTRA_HEIGHT = "INNER_EXTRA_HEIGHT";
    public static final String INNER_EXTRA_USE_CUSTOM_CLOSE = "INNER_EXTRA_USE_CUSTOM_CLOSE";
    public static final String INNER_EXTRA_CONTENT = "INNER_EXTRA_CONTENT";
    public static final String INNER_EXTRA_TYPE = "INNER_EXTRA_TYPE";

    // Special variables that can be set by the view
    public static final String MASTER_ID = "master_id";
    public static final String API_VERSION = "api_version";
    public static final String HIDDEN_STATE = "hidden_state";
    public static final String PUBLISHER_ID = "publisher_id";
    private static boolean IS_CUSTOMDATA_LOADED = false;
    private static boolean IS_REQUEST_WITH_CUSTOMDATA = false;

    protected Context mContext;
    /**
     * States that helps handle various states for the view visibility
     */
    private AdformEnum.VisibilityGeneralState mVisibilityGeneralState = AdformEnum.VisibilityGeneralState.LOAD_FAIL;
    private AdformEnum.VisibilityOnScreenState mVisibilityOnScreenState = AdformEnum.VisibilityOnScreenState.OFF_SCREEN;
    /**
     * Manager that helps to handle visibility and position changes for the view
     */
    private VisibilityPositionManager mVisibilityPositionManager;
    public static float sDeviceDensity;
    private HashMap<String, String> mCustomParams;
    // Should be taken from some kind of configuration
    private int mMasterId = 0;
    private int mPublisherId = 0; // Some hardcoded number, probably will be used later on
    // Should be taken from some kind of configuration
    private String mApiVersion = "1.0";
    private boolean isAnimating;
    protected MraidDeviceIdProperty mDeviceId;
    protected BaseInnerContainer mInnerContainer;
    protected Bundle mExtraParams;
    protected CoreInterstitialListener mListener;
    private boolean isDestroyed = false;

    public BaseCoreContainer(Context context) {
        this(context, null);
    }

    public BaseCoreContainer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseCoreContainer(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, null, null);
    }

    public BaseCoreContainer(Context context, AttributeSet attrs, int defStyle,
                             BaseInnerContainer innerContainer, Bundle extras) {
        super(context, attrs, defStyle);
        if (extras == null)
            extras = new Bundle();
        mExtraParams = extras;
        mContext = context;
        mInnerContainer = innerContainer;
        initializeDeviceId();
        initializeCustomParameters(attrs);
        sDeviceDensity = mContext.getResources().getDisplayMetrics().density;
        mCustomParams = new HashMap<String, String>();
        setBackgroundResource(android.R.color.transparent);
        getInnerView().setBaseListener(this);
        getInnerView().getMraidBridge().setMraidListener(this);
        getInnerView().getMraidBridge().setCoreBridgeListener(this);
        mVisibilityPositionManager = new VisibilityPositionManager(mContext, this, getInnerView().getMraidBridge());
        ViewGroup.LayoutParams params = new RelativeLayout.LayoutParams(
                getInnerView().getAdDimension().getWidth(),
                getInnerView().getAdDimension().getHeight());
        setLayoutParams(params);
        addView(getInnerView(), getInnerViewLayoutParams());
    }

    private void initializeDeviceId() {
        Thread thr = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mDeviceId = MraidDeviceIdProperty.createWithDeviceId(mContext);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thr.start();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mVisibilityPositionManager.checkVisibilityService();
    }

    protected void showContent(String content) {
        // Loaded content will always be loaded and mraid type
        mInnerContainer.showContent(content);
    }

    /**
     * Final call when visibility has changed. This should be called when something should be indicated
     * as visibility state changed
     *
     * @param isVisible
     */
    protected void onVisibilityCallback(boolean isVisible) {
        mInnerContainer.getMraidBridge().changeVisibility(isVisible, false);
    }

    /**
     * Initializes inner used view that display an ad
     *
     * @return initialized inner view
     */
    public abstract BaseInnerContainer getInnerView();

    protected abstract ViewGroup.LayoutParams getInnerViewLayoutParams();

    public abstract AdformEnum.State getDefaultState();

    public abstract AdformEnum.PlacementType getDefaultPlacementType();

    @Override
    public AdDimension getAdDimension() {
        return getInnerView().getAdDimension();
    }

    protected void startService() {
        mVisibilityPositionManager.checkVisibilityService();
    }

    protected void stopService() {
        if (getInnerView() != null && getInnerView().getMraidBridge() != null)
            getInnerView().getMraidBridge().changeVisibility(false, true);
    }

    protected void resumeService() {
        mVisibilityPositionManager.checkVisibilityService();
    }

    protected void pauseService() {

    }

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
                } else if (name.equals(PUBLISHER_ID)) {
                    String publisherAttr = attributes.getAttributeValue(i);
                    try {
                        mPublisherId = Integer.parseInt(publisherAttr);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
//                else if (name.equals(HIDDEN_STATE)) {
//                    String hiddenState = attributes.getAttributeValue(i);
//                    if (hiddenState.equals("invisible"))
//                        mHiddenState = View.INVISIBLE;
//                    else if (hiddenState.equals("gone"))
//                        mHiddenState = View.GONE;
//                    else
//                        mHiddenState = View.INVISIBLE;
//                }
            }
        }
    }

    public void loadImpression(String impressionUrl) {
        // Loading impression
        Utils.p("Loading impression to: "+impressionUrl);
        RawNetworkTask impressionTask =
                new RawNetworkTask(NetworkRequest.Method.GET,
                        impressionUrl);
        impressionTask.execute();
    }

//    @Override
//    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        super.onLayout(changed, l, t, r, b);
//    }

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

    public void setCustomParams(HashMap<String, String> customParams) {
        mCustomParams = customParams;
    }

    public void addCustomParam(String name, String value) {
        if (mCustomParams != null && name != null && value != null)
            mCustomParams.put(name, value);
    }

    public void clearCustomParams() {
        if (mCustomParams != null)
            mCustomParams.clear();
    }

    // -----------------
    // Getters / Setters
    // -----------------

    @Override
    public int getMasterId() {
        return mMasterId;
    }

    public void setMasterId(int masterId) {
        this.mMasterId = masterId;
    }

    @Override
    public MraidDeviceIdProperty getDeviceId() {
        return mDeviceId;
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

    @Override
    public boolean isContentMraid() {
        return getInnerView().getMraidBridge().isContentMraid();
    }

    @Override
    public void onIsContentMraidChange(boolean isContentMraid) {
        if (isContentMraid)
            mVisibilityPositionManager.checkVisibilityService();
    }

    @Override
    public BaseCoreContainer getView() {
        return this;
    }

    public void setListener(CoreInterstitialListener listener) {
        this.mListener = listener;
    }

    @Override
    public String getUserAgent() {
        return mInnerContainer.getUserAgent();
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }

    /**
     * Generates required parameters that are needed with the request for a contract.
     * This also forms a json object.
     *
     * @return formed parameters as json
     */
    public String getRequestProperties() {
        ArrayList<MraidBaseProperty> properties = new ArrayList<MraidBaseProperty>();
        if (getDefaultPlacementType() == AdformEnum.PlacementType.INTERSTITIAL)
            properties.add(MraidPlacementSizeProperty.createWithSize(1, 1));
        else
            properties.add(MraidPlacementSizeProperty.createWithDimension(getAdDimension()));
        properties.add(MraidMasterTagProperty.createWithMasterTag(getMasterId()));
        properties.add(SimpleMraidProperty.createWithKeyAndValue("\"version\"", getVersion()));
        properties.add(SimpleMraidProperty.createWithKeyAndValue("\"user_agent\"", getUserAgent()));
        properties.add(SimpleMraidProperty.createWithKeyAndValue(
                "\"accepted_languages\"", getLocale().replaceAll("_", "-")));
        properties.add(SimpleMraidProperty.createWithKeyAndValue(
                "\"type\"", AdformEnum.PlacementType.getPlacementString(getDefaultPlacementType())));
        properties.add(SimpleMraidProperty.createWithKeyAndValue("\"publisher_id\"", getPublisherId()));
        if (!IS_CUSTOMDATA_LOADED) {
            if (!isCustomParamsEmpty())
                IS_REQUEST_WITH_CUSTOMDATA = true;
            properties.add(MraidCustomProperty.createWithCustomParams(getCustomParameters()));
        }
        properties.add(getDeviceId());
        return MraidBaseProperty.getRequestPropertiesToString(properties);
    }

    /**
     * Generates required parameters that have to be passed with the request in GET form.
     *
     * @return required parameters appended to url
     */
    public String getUrlProperties() {
        ArrayList<MraidBaseProperty> properties = new ArrayList<MraidBaseProperty>();
        properties.add(MraidRandomNumberProperty.createWithRandomNumber());
        return MraidBaseProperty.getUrlPropertiesToString(properties);
    }

    public static void setCustomDataLoaded() {
        if (IS_REQUEST_WITH_CUSTOMDATA && !IS_CUSTOMDATA_LOADED) {
            IS_CUSTOMDATA_LOADED = true;
        }
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

    // ----------------
    // Expand functions
    // ----------------
    // View switching implementation
    private FrameLayout mRootView;
    private ImageView mPlaceholderView;
    private RelativeLayout mExpandContainer;
    private CoreExpandedView mCoreExpandView;

    @Override
    public void onMraidExpand(String url, final ExpandProperties properties) {
        pauseService();
        if (url == null) {
            expand(null, properties);
        } else {
            AdformContentLoadManager expandLoader = new AdformContentLoadManager();
            expandLoader.setListener(new AdformContentLoadManager.ContentLoaderListener() {
                @Override
                public void onContentMraidLoadSuccessful(String content) {
                    expand(content, properties);
                }

                @Override
                public void onContentLoadFailed() { }
            });
            try {
                expandLoader.loadContent(expandLoader.getRawGetTask(url, false));
            } catch (AdformContentLoadManager.ContentLoadException e) {
                e.printStackTrace();
            }
        }
    }

    protected void expand(String content, ExpandProperties properties) {
        mRootView = (FrameLayout) getRootView().findViewById(android.R.id.content);
        // Changing inner container with an empty view
        mPlaceholderView = getInnerView().getMockupPlaceholder();

        addView(mPlaceholderView,
                new ViewGroup.LayoutParams(getInnerView().getWidth(), getInnerView().getHeight()));
        removeView(getInnerView());

        if (mExpandContainer == null)
            mExpandContainer = new RelativeLayout(mContext);
        Bundle extraParams = new Bundle();
        int buttonDimen = getInnerView().getCloseButtonDimen();
        extraParams.putInt(CoreExpandedView.INNER_EXTRA_WIDTH,
                (properties.getWidth() == -1 || properties.getWidth() > buttonDimen)?properties.getWidth():buttonDimen);
        extraParams.putInt(CoreExpandedView.INNER_EXTRA_HEIGHT,
                (properties.getHeight() == -1 || properties.getHeight() > buttonDimen)?properties.getHeight():buttonDimen);
        extraParams.putBoolean(CoreExpandedView.INNER_EXTRA_USE_CUSTOM_CLOSE, properties.useCustomClose());
        if (content == null) {
            mCoreExpandView = new CoreExpandedView(mContext, getInnerView(), extraParams);
        } else {
            extraParams.putString(CoreExpandedView.INNER_EXTRA_CONTENT, content);
            mCoreExpandView = new CoreExpandedView(mContext, extraParams);
        }
        mCoreExpandView.setListener(new CoreInterstitialListener() {
            @Override
            public void onAdClose() {
                resetViewToDefaultState();
                getInnerView().getMraidBridge().onStateChange(AdformEnum.State.DEFAULT, false);
                getInnerView().getMraidBridge().forceSettingUpdate();
            }

            @Override
            public void onAdShown() {}
        });
        RelativeLayout.LayoutParams lp;
        lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        mExpandContainer.addView(mCoreExpandView, lp);


        mRootView.addView(mExpandContainer, new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        getInnerView().getMraidBridge().onStateChange(AdformEnum.State.EXPANDED, false);
    }

    /**
     * Resets expanded view to its default state
     */
    private void resetViewToDefaultState() {
        mCoreExpandView.setListener(null);
        mCoreExpandView.removeAllViewsInLayout();
        mExpandContainer.removeAllViewsInLayout();
        mRootView.removeView(mExpandContainer);

        getInnerView().requestLayout();
        addView(getInnerView(), 0, getInnerViewLayoutParams());
        invalidate();
        getInnerView().setBaseListener(this);
        getInnerView().getMraidBridge().setMraidListener(this);
        getInnerView().getMraidBridge().setCoreBridgeListener(this);
        getInnerView().setCloseButtonEnabled(false);
        resumeService();

        // Giving a bit of time for javascript to react
        postDelayed(removePlaceholderRunnable, 100);
    }

    @Override
    public void onMraidSetOrientation(boolean allowOrientationChange, AdformEnum.ForcedOrientation forcedOrientation) {
        if (allowOrientationChange && getInnerView().getMraidBridge().getState() != AdformEnum.State.DEFAULT) {
            switch (forcedOrientation) {
                case NONE: {
                    if (allowOrientationChange)
                        setOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    else
                        setOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
                    break;
                }
                case LANDSCAPE: {
                    setOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    break;
                }
                case PORTRAIT: {
                    setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    break;
                }
                default:
                    setOrientation(Configuration.ORIENTATION_UNDEFINED);
            }
        }
    }

    private void setOrientation(int orientation) {
        Context context = getContext();
        Activity activity = null;
        try {
            activity = (Activity) context;
            activity.setRequestedOrientation(orientation);
        } catch (ClassCastException e) {
            Utils.p("Cant modify orientation");
        }
    }


    // ---------------
    // Instance saving
    // ---------------
//    @Override
//    protected Parcelable onSaveInstanceState() {
//        Parcelable superState = super.onSaveInstanceState();
//        SavedState savedState = new SavedState(superState);
//        savedState.visibilityGeneralState = getGeneralState().getValue();
//        savedState.visibilityOnScreenState = getOnScreenState().getValue();
//        savedState.isContentMraid = isContentMraid;
//        savedState.customParams = mCustomParams;
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
//        setContentMraid(savedState.isContentMraid);
//        mCustomParams = savedState.customParams;
//        setViewState(AdformEnum.VisibilityGeneralState.parseType(savedState.visibilityGeneralState),
//                AdformEnum.VisibilityOnScreenState.parseType(savedState.visibilityOnScreenState));
//        setAnimating(false);
//    }

//    private static class SavedState extends BaseSavedState {
//        public int visibilityGeneralState;
//        public int visibilityOnScreenState;
//        public HashMap customParams;
//        public boolean isContentMraid;
//
//        public SavedState(Parcel source) {
//            super(source);
//            visibilityGeneralState = source.readInt();
//            visibilityOnScreenState = source.readInt();
//            isContentMraid = (source.readInt() == 1);
//            customParams = source.readHashMap(String.class.getClassLoader());
//        }
//        public SavedState(Parcelable superState) {
//            super(superState);
//        }

//        @Override
//        public void writeToParcel(Parcel dest, int flags) {
//            super.writeToParcel(dest, flags);
//            dest.writeInt(visibilityGeneralState);
//            dest.writeInt(visibilityOnScreenState);
//            dest.writeInt((isContentMraid)?1:0);
//            dest.writeMap(customParams);
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

    // ---------
    // Runnables
    // ---------

    private Runnable removePlaceholderRunnable = new Runnable() {
        @Override
        public void run() {
            removeView(mPlaceholderView);
            mPlaceholderView.setImageBitmap(null);
            mPlaceholderView = null;
        }
    };

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

    public void destroy() {
        isDestroyed = true;
        removeCallbacks(removePlaceholderRunnable);
        if (mVisibilityPositionManager != null) {
            mVisibilityPositionManager.destroy();
            mVisibilityPositionManager = null;
        }
        mInnerContainer.destroy();
        removeAllViews();
    }
}