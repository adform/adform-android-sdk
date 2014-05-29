package com.adform.sdk2.interfaces;

import com.adform.sdk2.mraid.properties.MraidDeviceIdProperty;
import com.adform.sdk2.resources.AdDimension;

import java.util.HashMap;

/**
 * Created by mariusm on 27/05/14.
 * An interface set for collecting various information about device
 * and user that is required for the contract request
 */
public interface AdformRequestParamsListener {
    /** @return ad dimensions */
    public AdDimension getAdDimension();
    /** @return ad unique */
    public int getMasterId();
    /** @return unique device id */
    public MraidDeviceIdProperty getDeviceId();
    /** @return Custom set user parameters */
    public HashMap<String, String> getCustomParameters();
    /** @return true of custom parameter list is empty */
    public boolean isCustomParamsEmpty();
    /** @return device locale */
    public int getPublisherId();
    /** @return defined api version */
    public String getVersion();
    /** @return user agent */
    public String getUserAgent();
    /** @return device locale */
    public String getLocale();
}
