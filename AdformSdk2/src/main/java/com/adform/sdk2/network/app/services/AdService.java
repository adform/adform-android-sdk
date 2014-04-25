package com.adform.sdk2.network.app.services;

import android.util.Log;
import com.adform.sdk2.network.app.AdformNetworkTask;
import com.adform.sdk2.network.app.entities.entities.AdServingEntity;
import com.adform.sdk2.network.base.ito.network.*;
import com.adform.sdk2.network.base.ito.observable.ObservableService;

public class AdService extends ObservableService implements ErrorListener {
    private static final String TAG = AdService.class.getSimpleName();
    public static final String PATH = "/mobilesdk/";
    public static final int REFRESH_SECONDS = 15;
    public static final int ERROR_REFRESH_SECONDS = 3;

    private AdServingEntity mAdServingEntity;

    public AdService() {}

    @Override
    public String getTag() {
        return TAG;
    }

    private SuccessListener<AdServingEntity> mGetSuccessListener = new SuccessListener<AdServingEntity>() {
        @Override
        public void onSuccess(NetworkTask request, NetworkResponse<AdServingEntity> response) {
            mAdServingEntity = response.getEntity();
            triggerObservers(mAdServingEntity);
            scheduleNewRequest(scheduleGetInfo(), REFRESH_SECONDS);
        }
    };

    private AdformNetworkTask<AdServingEntity> scheduleGetInfo(){
        AdformNetworkTask<AdServingEntity> getTask =
                new AdformNetworkTask<AdServingEntity>(NetworkRequest.Method.GET, PATH,
                        AdServingEntity.class, AdServingEntity.responseParser);
        getTask.setSuccessListener(mGetSuccessListener);
        getTask.setErrorListener(AdService.this);
        return getTask;
    }

    @Override
    protected void onStartService() {
        scheduleGetInfo().execute();
    }

    @Override
    protected void onStopService() {
        mAdServingEntity = null;
    }

    @Override
    protected void onPauseService() {
    }

    @Override
    protected void onResumeService() {
        resumeScheduledRequest(scheduleGetInfo());
    }

    @Override
    public void onError(NetworkTask request, NetworkError error) {
        Log.d(TAG, "error:" + error.getType());
        //notify UI on error
        notifyError(error);
        scheduleNewRequest(scheduleGetInfo(), ERROR_REFRESH_SECONDS);
    }

    private void notifyError(NetworkError error){
        setChanged();
        notifyObservers(error);
    }

    public AdServingEntity getAdServingEntity() {
        return mAdServingEntity;
    }

}
