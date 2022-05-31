package com.adform.adformdemo

import android.app.Activity
import android.os.Bundle
import com.adform.sdk.network.entities.AdformEnum
import com.adform.sdk.pub.views.AdHesion
import com.adform.sdk.utils.AdSize

/**
 * Just like [com.adform.sdk.pub.views.AdInline], this is one of the most basic implementations.
 */
class AdHesionActivity : Activity() {

    private lateinit var adHesion: AdHesion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adhesion)

        // [mandatory] Getting an instance of the view
        adHesion = findViewById(R.id.view_ad_hesion)

        // [optional] Setting view position
        adHesion.setAdPositionType(AdformEnum.AdPositionType.TOP)

        // [mandatory] Setting ad size. [optional] if set in layout XML
        adHesion.setAdSize(AdSize(320, 50))

        // [mandatory] Setting master tag. [optional] if set in layout XML
        adHesion.setMasterTagId(MASTER_TAG)

        // [optional] You can set an animation type that could be used when ad shows in the banner.
//        adHesion.setBannerAnimationType(AdformEnum.AnimationType.FADE)
//        adHesion.setBannerAnimationType(AdformEnum.AnimationType.NO_ANIMATION)
        adHesion.setBannerAnimationType(AdformEnum.AnimationType.SLIDE) // default

        // [optional] You can set an animation type that could be used when ad is expanded ( mraid.expand() ).
//        adHesion.setModalPresentationStyle(AdformEnum.AnimationType.FADE)
//        adHesion.setModalPresentationStyle(AdformEnum.AnimationType.NO_ANIMATION)
        adHesion.setModalPresentationStyle(AdformEnum.AnimationType.SLIDE) // default

        // [optional] This flag sets if dim should be used in the background whenever mraid.expand() is used.
        adHesion.isDimOverlayEnabled = true // default

        // [mandatory] Send an event to start loading
        adHesion.loadAd()
    }

    override fun onResume() {
        super.onResume()
        // [mandatory] Sending world event to the view
        adHesion.onResume()
    }

    override fun onPause() {
        super.onPause()
        // [mandatory] Sending world event to the view
        adHesion.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        // [mandatory] Sending world event to the view
        adHesion.destroy()
    }

    companion object {
        const val MASTER_TAG = 206416
    }
}
