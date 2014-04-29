package com.adform.sdk2.network.app.services;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.adform.sdk2.Constants;
import com.adform.sdk2.network.app.AdformNetworkTask;
import com.adform.sdk2.network.app.entities.entities.AdServingEntity;
import com.adform.sdk2.network.base.ito.network.*;
import com.adform.sdk2.network.base.ito.observable.ObservableService;

public class AdService extends ObservableService implements ErrorListener, Parcelable {
    private static final String TAG = AdService.class.getSimpleName();
    public static final String INSTANCE_KEY_TIMEPASSED = "instance_key_timepassed";
    public static final String INSTANCE_KEY_TIMERSTATE = "instance_key_timerstate";
    public static final String INSTANCE_KEY_TIMERTIMEOUT = "instance_key_timertimeout";

    private AdServingEntity mAdServingEntity;
    public AdService() {}

    public Bundle getSaveInstanceBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt(INSTANCE_KEY_TIMEPASSED, getTimePassed());
        bundle.putInt(INSTANCE_KEY_TIMERSTATE, getStatus().getValue());
        bundle.putInt(INSTANCE_KEY_TIMERTIMEOUT, getTimerTimeout());
        return bundle;
    }

    public void restoreInstanceWithBundle(Bundle restoreBundle, int lastTime) {
        if (restoreBundle == null)
            restoreBundle = new Bundle(); // Mock up bundle to take default values
        setTimerTimeout(restoreBundle.getInt(INSTANCE_KEY_TIMERTIMEOUT, Constants.REFRESH_SECONDS));
        setStatus(ObservableService.Status.parseType(restoreBundle.getInt(INSTANCE_KEY_TIMERSTATE,
                Status.RUNNING.getValue())));
        resumeService((lastTime == -1)?restoreBundle.getInt(INSTANCE_KEY_TIMEPASSED, 0):lastTime);
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
            scheduleNewRequest(scheduleGetInfo(), Constants.REFRESH_SECONDS);
        }
    };

    private AdformNetworkTask<AdServingEntity> scheduleGetInfo(){
        AdformNetworkTask<AdServingEntity> getTask =
                new AdformNetworkTask<AdServingEntity>(NetworkRequest.Method.GET,
                        Constants.SDK_INFO_PATH,
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
        scheduleNewRequest(scheduleGetInfo(), Constants.ERROR_REFRESH_SECONDS);
    }

    private void notifyError(NetworkError error){
        setChanged();
        notifyObservers(error);
    }

    public AdServingEntity getAdServingEntity() {
        return mAdServingEntity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(getTimePassed());
        dest.writeInt(getTimerTimeout());
        dest.writeInt(getStatus().getValue());
    }

    public static final Parcelable.Creator<AdService> CREATOR
            = new Parcelable.Creator<AdService>() {
        public AdService createFromParcel(Parcel in) {
            return new AdService(in);
        }

        public AdService[] newArray(int size) {
            return new AdService[size];
        }
    };

    private AdService(Parcel in) {
        setTimePassed(in.readInt());
        setTimerTimeout(in.readInt());
        setStatus(Status.parseType(in.readInt()));
    }
}
