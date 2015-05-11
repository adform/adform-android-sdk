package com.adform.adformdemo;

import android.app.Application;

import com.adform.sdk.utils.AdApplicationService;

/**
 * An extended application class, that extends Adform SDK functionality.
 * Note: This class should be defined in AndroidManifest.xml to work properly.
 */
public class DemoApplication extends Application implements AdApplicationService.ServiceListener {

    private AdApplicationService adService;

    @Override
    public void onCreate() {
        super.onCreate();
        // [mandatory] Initializes application service
        adService = AdApplicationService.init();
    }

    // [mandatory] A mandatory method for the SDK to work properly
    @Override
    public AdApplicationService getAdService() {
        return adService;
    }
}
