package com.adform.sdk2.view;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import com.adform.sdk2.network.app.entities.entities.AdServingEntity;
import com.adform.sdk2.network.app.services.AdService;
import com.adform.sdk2.network.base.ito.network.NetworkError;
import com.adform.sdk2.network.base.ito.observable.ObservableService;
import com.adform.sdk2.utils.SlidingManager;
import com.adform.sdk2.utils.Utils;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by mariusm on 24/04/14.
 */
public class CoreAdView extends RelativeLayout implements Observer,
        SlidingManager.SliderableWidget, BannerView.BannerViewListener {
    private static final String ATTR_URL = "request_url";
    public static final int VIEW_TYPE_BANNER = 0;

    private Context mContext;
    private AdService mAdService;

    private BannerView mBannerView;
    private SlidingManager mSlidingManager;

    public enum AdLoadState {
        LOADING,
        LOAD_SUCCESSFUL,
        LOAD_FAILED
    }


    public CoreAdView(Context context) {
        this(context, null);
    }

    public CoreAdView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CoreAdView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        mSlidingManager = new SlidingManager(this);
        setBackgroundResource(android.R.color.transparent);
        // Getting special attributes
//        if (attrs != null) {
//            int attrCount = attrs.getAttributeCount();
//            for (int i = 0; i < attrCount; i++) {
//                String name = attrs.getAttributeName(i);
//                if (name.equals(ATTR_URL)) {
//                    this.mRequestUrl = attrs.getAttributeValue(i);
//                }
//            }
//        }
        mBannerView = new BannerView(mContext);
        mBannerView.setListener(this);
        // TODO: Change this to something nicer. This must be binded, as this lets instance to be saved
        mBannerView.setId(156554);
        addView(mBannerView);
        setVisibility(INVISIBLE);
    }

//    private HashMap<Integer, View> initViews(HashMap<Integer, View> views) {
//        if (views == null)
//            views = new HashMap<Integer, View>();
//        views.put(VIEW_TYPE_BANNER, new BannerView(mContext));
//        return views;
//    }

    /** An update from configuration json */
    @Override
    public void update(Observable observable, Object data) {
        if (data instanceof NetworkError)
            return;
        if (mBannerView != null && data != null) {
            String content = ((AdServingEntity) data).getAdEntity().getTagDataEntity().getSrc();
            mBannerView.loadContent(content);
        } else {
            mSlidingManager.turnOff();
        }
    }

    @Override
    public void onContentLoadSuccessful() {
        mSlidingManager.turnOn();
    }

    @Override
    public void onNewContentLoad() {
        mSlidingManager.turnOff();
    }

    @Override
    public void onContentLoadFailed() {}

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
//        filter.addAction(Intent.ACTION_USER_PRESENT);
//        mContext.registerReceiver(mScreenStateReceiver, filter);

        if (mAdService == null) {
            mAdService = new AdService();
            mAdService.addObserver(this);
            mAdService.startService();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
//        unregisterScreenStateBroadcastReceiver();

        mAdService.deleteObserver(this);
        mAdService.stopService();
    }

    /** Widget height return for SliderManager */
    @Override
    public float getWidgetHeight() {
        return getHeight();
    }

    // TODO: AdService should not be packed outside, but inside, though at the moment this does work properly

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        // todo: change instance saving here, from seperate variables to whole bundle
        savedState.timePassed = mAdService.getTimePassed();
        savedState.timerState = mAdService.getStatus().getValue();
        savedState.timerTimeout = mAdService.getTimerTimeout();
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
        if (mAdService == null) {
            mAdService = new AdService();
            mAdService.addObserver(this);
            // todo: same as saving, just instance restore manipulation
            mAdService.setTimerTimeout(savedState.timerTimeout);
            mAdService.setStatus(ObservableService.Status.parseType(savedState.timerState));
            mAdService.resumeService(savedState.timePassed);
        }
    }


    private static class SavedState extends BaseSavedState {
        public String requestUrl;
        public int timePassed;
        public int timerTimeout;
        public int timerState;

        public SavedState(Parcel source) {
            super(source);
            timePassed = source.readInt();
            timerTimeout = source.readInt();
            timerState = source.readInt();
        }
        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(timePassed);
            dest.writeInt(timerTimeout);
            dest.writeInt(timerState);
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


}
