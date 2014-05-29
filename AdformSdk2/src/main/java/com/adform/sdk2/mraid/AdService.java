package com.adform.sdk2.mraid;

import android.content.Context;
import android.os.Bundle;
import com.adform.sdk2.Constants;
import com.adform.sdk2.interfaces.AdformRequestParamsListener;
import com.adform.sdk2.mraid.properties.*;
import com.adform.sdk2.network.app.AdformNetworkTask;
import com.adform.sdk2.network.app.entities.entities.AdServingEntity;
import com.adform.sdk2.network.base.ito.network.*;
import com.adform.sdk2.network.base.ito.observable.ObservableService2;
import com.adform.sdk2.utils.Utils;
import com.adform.sdk2.view.base.BaseCoreContainer;

import java.util.ArrayList;

/**
 * Created by mariusm on 23/04/14.
 * A service that controls when the ad should be loaded from the network.
 */
public class AdService extends ObservableService2 {
    private static boolean IS_CUSTOMDATA_LOADED = false;
    private static boolean IS_REQUEST_WITH_CUSTOMDATA = false;
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
        public void onNetworkError(NetworkTask request, NetworkError networkError);
    }



    private AdServingEntity mAdServingEntity;
    private long mTimerStop;
    private AdServiceBinder mListener;
    private AdformRequestParamsListener mParamsListener;

    public AdService(AdServiceBinder mListener, AdformRequestParamsListener mParamsListener) {
        this.mListener = mListener;
        this.mParamsListener = mParamsListener;
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
            if (IS_REQUEST_WITH_CUSTOMDATA && !IS_CUSTOMDATA_LOADED) {
                IS_CUSTOMDATA_LOADED = true;
            }
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

        String additionalPOSTProperties = getGeneratedPOSTPropertiesToString();
        String additionalURLProperties = getGeneratedUrlPropertiesToString();
//        Utils.p("Generated post properties: "+additionalPOSTProperties);
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

    /**
     * Generates required parameters that are needed with the request for a contract.
     * This also forms a json object.
     * @return formed parameters as json
     */
    private String getGeneratedPOSTPropertiesToString() {
        if (mListener == null)
//            throw new IllegalStateException("AdService requires for an AdServiceBinder interface implementation");
            return null;
        ArrayList<MraidBaseProperty> properties = new ArrayList<MraidBaseProperty>();
        properties.add(MraidPlacementSizeProperty.createWithDimension(mParamsListener.getAdDimension()));
        properties.add(MraidMasterTagProperty.createWithMasterTag(mParamsListener.getMasterId()));
        properties.add(SimpleMraidProperty.createWithKeyAndValue("version", mParamsListener.getVersion()));
        properties.add(SimpleMraidProperty.createWithKeyAndValue("user_agent", mParamsListener.getUserAgent()));
        properties.add(SimpleMraidProperty.createWithKeyAndValue(
                "accepted_languages", mParamsListener.getLocale().replaceAll("_", "-")));
        properties.add(SimpleMraidProperty.createWithKeyAndValue("publisher_id", mParamsListener.getPublisherId()));
        if (!IS_CUSTOMDATA_LOADED) {
            if (!mParamsListener.isCustomParamsEmpty())
                IS_REQUEST_WITH_CUSTOMDATA = true;
            properties.add(MraidCustomProperty.createWithCustomParams(mParamsListener.getCustomParameters()));
        }
        properties.add(mParamsListener.getDeviceId());
        return MraidBaseProperty.generateJSONPropertiesToString(properties);
    }

    /**
     * Generates required parameters that have to be passed with the request in GET form.
     * @return required parameters appended to url
     */
    private String getGeneratedUrlPropertiesToString() {
        if (mListener == null)
//            throw new IllegalStateException("AdService requires for an AdServiceBinder interface implementation");
            return null;
        ArrayList<MraidBaseProperty> properties = new ArrayList<MraidBaseProperty>();
        properties.add(MraidRandomNumberProperty.createWithRandomNumber());
        return MraidBaseProperty.generateGETPropertiesToString(properties);
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
