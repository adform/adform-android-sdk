package com.adform.sdk2.mraid;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import com.adform.sdk2.Constants;
import com.adform.sdk2.mraid.properties.*;
import com.adform.sdk2.network.app.AdformNetworkTask;
import com.adform.sdk2.network.app.entities.entities.AdServingEntity;
import com.adform.sdk2.network.base.ito.network.*;
import com.adform.sdk2.network.base.ito.observable.ObservableService2;
import com.adform.sdk2.resources.AdDimension;
import com.adform.sdk2.utils.Utils;
import org.apache.http.entity.StringEntity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mariusm on 23/04/14.
 * A service that controls when the ad should be loaded from the network.
 */
public class AdService extends ObservableService2 implements ErrorListener {
    private static final String TAG = AdService.class.getSimpleName();
    public static final long INSTANT_EXECUTION_DELAY = 500;
    public static final String INSTANCE_KEY_STOP = "instance_key_stop";

    /**
     * A helper interface that helps to bind re-occuring service for additional
     * information, that is needed when forming the request.
     */
    public interface AdServiceBinder {
        /** @return ad dimensions */
        public AdDimension getAdDimension();
        /** @return ad unique */
        public String getMasterId();
        /** @return view context */
        public Context getContext();
        /** @return defined api version */
        public String getVersion();
        /** @return unique device id */
        public MraidDeviceIdProperty getDeviceId();
        /** @return Custom set user parameters */
        public HashMap<String, String> getCustomParameters();
    }

    private AdServingEntity mAdServingEntity;
    private long mTimerStop;
    private AdServiceBinder mListener;

    public AdService(AdServiceBinder l) {
        mListener = l;
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

        String additionalPOSTProperties = getGeneratedPOSTPropertiesToString();
//        Utils.p("Generated post properties: "+additionalPOSTProperties);
        AdformNetworkTask<AdServingEntity> getTask =
                new AdformNetworkTask<AdServingEntity>(NetworkRequest.Method.POST,
                        Constants.SDK_INFO_PATH,
                        AdServingEntity.class, AdServingEntity.responseParser);
        getTask.setJsonEntity(additionalPOSTProperties);
        getTask.setSuccessListener(mGetSuccessListener);
        getTask.setErrorListener(AdService.this);
        return getTask;
    }

    private String getGeneratedPOSTPropertiesToString() {
        if (mListener == null)
//            throw new IllegalStateException("AdService requires for an AdServiceBinder interface implementation");
            return null;
        ArrayList<MraidBaseProperty> properties = new ArrayList<MraidBaseProperty>();
        properties.add(MraidPlacementSizeProperty.createWithDimension(mListener.getAdDimension()));
        properties.add(MraidMasterTagProperty.createWithMasterTag(mListener.getMasterId()));
        properties.add(MraidVersionProperty.createWithVersion(mListener.getVersion()));
        properties.add(mListener.getDeviceId());
        return MraidBaseProperty.generateJSONPropertiesToString(properties);
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
    protected void onPauseService() {}

    @Override
    protected void onResumeService() {
        long executionTime = mTimerStop - System.currentTimeMillis();
        scheduleRequest(getRequest(), (executionTime > 0)?executionTime:INSTANT_EXECUTION_DELAY);
    }

    @Override
    public void onError(NetworkTask request, NetworkError error) {
        Log.d(TAG, "error:" + error.getType());
        //notify UI on error
        notifyError(error);
        scheduleNextGetInfo(Constants.REFRESH_SECONDS);
    }

    private void notifyError(NetworkError error){
        setChanged();
        notifyObservers(error);
    }

    public AdServingEntity getAdServingEntity() {
        return mAdServingEntity;
    }

}
