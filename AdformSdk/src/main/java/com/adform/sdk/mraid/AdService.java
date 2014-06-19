package com.adform.sdk.mraid;

import android.content.Context;
import android.os.Bundle;
import com.adform.sdk.Constants;
import com.adform.sdk.interfaces.AdformRequestParamsListener;
import com.adform.sdk.mraid.properties.*;
import com.adform.sdk.network.app.AdformNetworkTask;
import com.adform.sdk.network.app.entities.entities.AdServingEntity;
import com.adform.sdk.network.base.ito.network.*;
import com.adform.sdk.network.base.ito.observable.ObservableService2;
import com.adform.sdk.utils.Utils;
import com.adform.sdk.view.base.BaseCoreContainer;

import java.util.ArrayList;

/**
 * Created by mariusm on 23/04/14.
 * A service that controls when the ad should be loaded from the network.
 */
public class AdService extends ObservableService2 {
    private static final String TAG = AdService.class.getSimpleName();
    public static final long INSTANT_EXECUTION_DELAY = 500;
    public static final String INSTANCE_KEY_STOP = "instance_key_stop";

    /**
     * A helper interface that helps to bind re-occuring service for additional
     * information, that is needed when forming the request.
     */
    public interface AdServiceBinder {
        /** @return view context */
        public Context getContext();
        public BaseCoreContainer getView();
        public void onNetworkError(NetworkTask request, NetworkError networkError);
    }

    private AdServingEntity mAdServingEntity;
    private long mTimerStop;
    private long mTimerPauseOffset = 0;
    private AdServiceBinder mListener;

    public AdService(AdServiceBinder mListener) {
        this.mListener = mListener;
    }

    /**
     * @return a bundle of variables that should be saved into instance
     */
    public Bundle getSaveInstanceBundle() {
        Bundle bundle = new Bundle();
        bundle.putLong(INSTANCE_KEY_STOP, mTimerStop);
        return bundle;
    }

    /**
     * Restores service state from the instance provided bundle
     * @param restoreBundle variable bundle with stored information
     */
    public void restoreInstanceWithBundle(Bundle restoreBundle) {
        if (mTimerStop == 0 && restoreBundle != null)
            mTimerStop = restoreBundle.getLong(INSTANCE_KEY_STOP);
        // todo when instance saving is enabled once again, mTimerPauseOffset should be restored here
        if (mTimerPauseOffset != 0) {
            mTimerStop = System.currentTimeMillis() + mTimerPauseOffset;
            mTimerPauseOffset = 0;
        }
        setStatus(Status.RUNNING);
        long executionTime = mTimerStop - System.currentTimeMillis();
        scheduleRequest(getRequest(), (executionTime > 0)?executionTime:INSTANT_EXECUTION_DELAY);
    }

    @Override
    public String getTag() {
        return TAG;
    }

    private SuccessListener<AdServingEntity> mGetSuccessListener = new SuccessListener<AdServingEntity>() {
        @Override
        public void onSuccess(NetworkTask request, NetworkResponse<AdServingEntity> response) {
            BaseCoreContainer.setCustomDataLoaded();
            mAdServingEntity = response.getEntity();
            triggerObservers(mAdServingEntity);
            if (mAdServingEntity != null
                    && mAdServingEntity.getAdEntity() != null
                    && mAdServingEntity.getAdEntity().getRefreshInterval() > 0)
                scheduleNextGetInfo(mAdServingEntity.getAdEntity().getRefreshInterval());
            else
                scheduleNextGetInfo(Constants.REFRESH_SECONDS);
        }
    };

    private ErrorListener mGetErrorListener = new ErrorListener() {
        @Override
        public void onError(NetworkTask request, NetworkError error) {
            notifyError(error);
            scheduleNextGetInfo(Constants.REFRESH_SECONDS);
            mListener.onNetworkError(request, error);
        }
    };

    /**
     * Schedules when the next request should occur.
     * @param delay provided delay, when will the next request will occur. Time is in seconds.
     */
    private void scheduleNextGetInfo(long delay) {
        mTimerStop = System.currentTimeMillis() + delay * 1000;
        scheduleRequest(getRequest(), mTimerStop - System.currentTimeMillis());
    }

    /**
     * Creates and returns the request for the ad contract
     * @return formed network request
     */
    private AdformNetworkTask<AdServingEntity> getRequest(){
        String additionalPOSTProperties = mListener.getView().getRequestProperties();
        String additionalURLProperties = mListener.getView().getUrlProperties();
        if (additionalPOSTProperties == null) {
            Utils.e("Error loading contract. Additional parameters are null.");
            return null;
        }
        Utils.d("Generated params: "+additionalPOSTProperties);
        AdformNetworkTask<AdServingEntity> getTask =
                new AdformNetworkTask<AdServingEntity>(NetworkRequest.Method.POST,
                        Constants.SDK_INFO_PATH+
                                (additionalURLProperties != null?additionalURLProperties:""),
                        AdServingEntity.class, AdServingEntity.responseParser);
        getTask.setJsonEntity(additionalPOSTProperties);
        getTask.setSuccessListener(mGetSuccessListener);
        getTask.setErrorListener(mGetErrorListener);
        return getTask;
    }

    @Override
    protected void onStartService() {
        scheduleNextGetInfo(0);
    }

    @Override
    protected void onStopService() {
        mAdServingEntity = null;
    }

    @Override
    protected void onPauseService() {
        mTimerPauseOffset = mTimerStop - System.currentTimeMillis();
    }

    @Override
    protected void onResumeService() {
        long executionTime = mTimerStop - System.currentTimeMillis();
        scheduleRequest(getRequest(), (executionTime > 0) ? executionTime : INSTANT_EXECUTION_DELAY);
    }

    private void notifyError(NetworkError error){
        setChanged();
        notifyObservers(error);
    }

    public AdServingEntity getAdServingEntity() {
        return mAdServingEntity;
    }

}
