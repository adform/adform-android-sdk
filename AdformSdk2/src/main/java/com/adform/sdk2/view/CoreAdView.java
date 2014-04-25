package com.adform.sdk2.view;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import com.adform.sdk2.interfaces.AdViewControllable;
import com.adform.sdk2.network.app.entities.entities.AdServingEntity;
import com.adform.sdk2.network.app.services.AdService;
import com.adform.sdk2.network.base.ito.observable.ObservableService;
import com.adform.sdk2.utils.Utils;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by mariusm on 24/04/14.
 */
public class CoreAdView extends RelativeLayout implements Observer {
    private static final String ATTR_URL = "request_url";
    public static final int VIEW_TYPE_BANNER = 0;

    private Context mContext;
    private AdService mAdService;

    private String mRequestUrl = null;
    private HashMap<Integer, View> mViews;

    public CoreAdView(Context context) {
        this(context, null);
    }

    public CoreAdView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CoreAdView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        mViews = initViews(mViews);

        // Getting special attributes
        if (attrs != null) {
            int attrCount = attrs.getAttributeCount();
            for (int i = 0; i < attrCount; i++) {
                String name = attrs.getAttributeName(i);
                if (name.equals(ATTR_URL)) {
                    this.mRequestUrl = attrs.getAttributeValue(i);
                }
            }
        }
    }

    private HashMap<Integer, View> initViews(HashMap<Integer, View> views) {
        if (views == null)
            views = new HashMap<Integer, View>();
        views.put(VIEW_TYPE_BANNER, new BannerView(mContext));
        return views;
    }

    @Override
    public void update(Observable observable, Object data) {
        if (mViews == null || data == null)
            return;
        Utils.p("Loaded something");

        // Clear all views
        for (View view : mViews.values()) {
            if (view != null)
                removeView(view);
        }

        // Add the only needed view
        addView(mViews.get(VIEW_TYPE_BANNER));

        // Todo remove this mockup later
        if (mViews.get(VIEW_TYPE_BANNER) instanceof AdViewControllable) {
            String content = ((AdServingEntity) data).getAdEntity().getTagDataEntity().getSrc();
            ((AdViewControllable) mViews.get(VIEW_TYPE_BANNER))
                    .showContent(content);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Utils.p("onAttachedToWindow");
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
        Utils.p("onDetachedFromWindow");
//        unregisterScreenStateBroadcastReceiver();

        mAdService.deleteObserver(this);
        mAdService.stopService();
    }

    // TODO: AdService should not be packed outside, but inside, though at the moment this does work properly

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
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
//            if (source.readInt() == 1)
//                requestUrl = source.readString();
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
//            dest.writeInt((requestUrl != null)?1:0);
//            if (requestUrl != null)
//                dest.writeString(requestUrl);
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
