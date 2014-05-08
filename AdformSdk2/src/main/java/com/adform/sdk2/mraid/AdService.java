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

import java.util.ArrayList;

public class AdService extends ObservableService2 implements ErrorListener {
    private static final String TAG = AdService.class.getSimpleName();
    public static final long INSTANT_EXECUTION_DELAY = 500;
    public static final String INSTANCE_KEY_STOP = "instance_key_stop";

    public interface AdServiceBinder {
        public AdDimension getAdDimension();
        public String getMasterId();
        public Context getContext();
        public String getVersion();
        public MraidDeviceIdProperty getDeviceId();
    }

    private AdServingEntity mAdServingEntity;
    private long mTimerStop;
//    private final AdDimension mAdDimension;
//    private String mMasterId;
    private AdServiceBinder mListener;

    public AdService(AdServiceBinder l) {
        mListener = l;
    }

    public Bundle getSaveInstanceBundle() {
        Bundle bundle = new Bundle();
        bundle.putLong(INSTANCE_KEY_STOP, mTimerStop);
        return bundle;
    }

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

    private void scheduleNextGetInfo(long delay) {
        mTimerStop = System.currentTimeMillis() + delay * 1000;
        scheduleRequest(getRequest(), mTimerStop - System.currentTimeMillis());
    }

    private AdformNetworkTask<AdServingEntity> getRequest(){

        String additionalGetProperties = getGeneratedPropertiesToString();

        AdformNetworkTask<AdServingEntity> getTask =
                new AdformNetworkTask<AdServingEntity>(NetworkRequest.Method.GET,
                        Constants.SDK_INFO_PATH
                                + ((additionalGetProperties != null)?additionalGetProperties:""),
                        AdServingEntity.class, AdServingEntity.responseParser);
        getTask.setSuccessListener(mGetSuccessListener);
        getTask.setErrorListener(AdService.this);
        return getTask;
    }

    private String getGeneratedPropertiesToString() {
        if (mListener == null)
//            throw new IllegalStateException("AdService requires for an AdServiceBinder interface implementation");
            return null;
        ArrayList<MraidBaseProperty> properties = new ArrayList<MraidBaseProperty>();
        properties.add(MraidPlacementSizeProperty.createWithDimension(mListener.getAdDimension()));
        properties.add(MraidMasterTagProperty.createWithMasterTag(mListener.getMasterId()));
        properties.add(mListener.getDeviceId());
        properties.add(MraidVersionProperty.createWithVersion(mListener.getVersion()));
        properties.add(MraidRandomNumberProperty.createWithRandomNumber());
        return MraidBaseProperty.generatePropertiesToString(properties);
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
