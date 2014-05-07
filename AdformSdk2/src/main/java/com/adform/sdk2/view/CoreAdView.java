package com.adform.sdk2.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import com.adform.sdk2.network.app.entities.entities.AdServingEntity;
import com.adform.sdk2.network.app.services.AdService;
import com.adform.sdk2.network.base.ito.network.NetworkError;
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
        ContentLoadManager.ContentLoaderListener {

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
        if (mContext instanceof CoreAdViewListener)
            mListener = (CoreAdViewListener)mContext;
        mSlidingManager = new SlidingManager(this);
        mContentLoadManager = new ContentLoadManager(this);
        setBackgroundResource(android.R.color.transparent);

        final float scale = mContext.getResources().getDisplayMetrics().density;
        ViewGroup.LayoutParams params = new RelativeLayout.LayoutParams(
                (int)(Utils.getWidthDeviceType(mContext) * scale+0.5f),
                (int)(Utils.getHeightDeviceType(mContext) * scale+0.5f));
        setLayoutParams(params);

        mBannerView = new BannerView(mContext);
        mBannerView.setListener(this);
        // TODO: Change this to something nicer. This must be binded, as this lets instance to be saved
        mBannerView.setId(156554);
        addView(mBannerView);

        setVisibility(INVISIBLE);
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

    //TODO mariusm 07/05/14 Called from slider manager, though might not be needed
    @Override
    public void onContainerVisibilityChange(boolean visible) {}

    @Override
    public void startSliding(final Animation animation) {
        post(new Runnable() {
            @Override
            public void run() {
                startAnimation(animation);
            }
        });
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
            mAdService = new AdService();
        mAdService.addObserver(this);
        mAdService.restoreInstanceWithBundle(mServiceInstanceBundle);
        mServiceInstanceBundle = null;
    }

    /**
     * Starts to run service anew
     */
    private void startService() {
        if (mAdService == null)
            mAdService = new AdService();
        mAdService.addObserver(this);
        if (mBannerView != null && mBannerView.getTimesLoaded() > 0)
            resumeService();
        else
            mAdService.startService();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.saveBundle = mAdService.getSaveInstanceBundle();
        savedState.viewState = getViewState().getValue();
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
    }

    private static class SavedState extends BaseSavedState {
        public Bundle saveBundle;
        public int viewState;

        public SavedState(Parcel source) {
            super(source);
            saveBundle = source.readBundle();
            viewState = source.readInt();
        }
        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeBundle(saveBundle);
            dest.writeInt(viewState);
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
