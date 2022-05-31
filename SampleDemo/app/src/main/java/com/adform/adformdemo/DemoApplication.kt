package com.adform.adformdemo

import androidx.multidex.MultiDexApplication
import com.adform.sdk.utils.AdApplicationService

/**
 * An extended application class, that extends Adform SDK functionality.
 * Note: This class should be defined in AndroidManifest.xml to work properly.
 */
class DemoApplication : MultiDexApplication(), AdApplicationService.ServiceListener {

    private lateinit var adService: AdApplicationService

    override fun onCreate() {
        super.onCreate()
        // [mandatory] Initializes application service
        adService = AdApplicationService.init()
    }

    // [mandatory] A mandatory method for the SDK to work properly
    override fun getAdService(): AdApplicationService {
        return adService
    }
}
