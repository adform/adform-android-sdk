package com.adform.adformdemo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.adform.sdk.interfaces.AdListener
import com.adform.sdk.interfaces.AdStateListener
import com.adform.sdk.network.entities.AdformEnum
import com.adform.sdk.pub.views.AdInline
import com.adform.sdk.utils.AdSize

/**
 * The most basic ad implementation. You pass in mandatory events and parameters,
 * start loading ad, and it handles everything else automatically.
 */
class AdInlineActivity : AppCompatActivity() {

    private lateinit var adInline: AdInline

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adinline)

        // [mandatory] Getting an instance of the view
        adInline = findViewById(R.id.view_ad_inline)

        // [optional] Setting base listeners for ad load status
        adInline.setListener(object : AdListener {
            override fun onAdLoadSuccess(adInline: AdInline) {
                Log.d(TAG, "onAdLoadSuccess")
            }
            override fun onAdLoadFail(adInline: AdInline, s: String) {
                Log.d(TAG, "onAdLoadFail: $s")
                // Setting fallback image whenever ad fails to load
//                findViewById<View>(R.id.ad_container).setBackgroundResource(R.drawable.ic_launcher)
            }
        })

        // [optional] Setting additional listeners for ad events
        adInline.setStateListener(object : AdStateListener {
            override fun onAdVisibilityChange(adInline: AdInline, b: Boolean) {}
            override fun onAdOpen(adInline: AdInline) {}
            override fun onAdClose(adInline: AdInline) {}
        })

        // [mandatory] Setting ad size. [optional] if set in layout XML
//        adInline.adSize = AdSize(320, 50)

//        [optional] You could add to the request additional sizes.
//        adInline.setSupportedSizes(AdSize(300, 300), AdSize(320, 160))

//        [optional] If you want to support multiple ad sizes at the same placement without setting them, you could use additional dimensions feature.
        adInline.isEnabledAdditionalDimensions = true

//        [optional] If you want to setup that HTML ad would be loaded if video ads fails, you need to set fallback master tag.
        adInline.fallbackMasterTagId = FALLBACK_MASTER_TAG

        // [mandatory] Setting master tag. [optional] if set in layout XML
        adInline.masterTagId = MASTER_TAG

        // [optional] You can set an animation type that could be used when ad shows in the banner.
//        adInline.setBannerAnimationType(AdformEnum.AnimationType.FADE)
//        adInline.setBannerAnimationType(AdformEnum.AnimationType.NO_ANIMATION)
        adInline.setBannerAnimationType(AdformEnum.AnimationType.SLIDE) // default

        // [optional] You can set an animation type that could be used when ad is expanded ( mraid.expand() ).
//        adInline.setModalPresentationStyle(AdformEnum.AnimationType.FADE)
//        adInline.setModalPresentationStyle(AdformEnum.AnimationType.NO_ANIMATION)
        adInline.setModalPresentationStyle(AdformEnum.AnimationType.SLIDE) // default

        // [optional] This flag sets if dim should be used in the background whenever mraid.expand() is used.
        adInline.isDimOverlayEnabled = true // default

        // [mandatory] Send an event to start loading
        adInline.loadAd()
    }

    override fun onResume() {
        super.onResume()
        // [mandatory] Sending world event to the view
        adInline.onResume()
    }

    override fun onPause() {
        super.onPause()
        // [mandatory] Sending world event to the view
        adInline.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        // [mandatory] Sending world event to the view
        adInline.destroy()
    }

    companion object {
        private val TAG = AdInlineActivity::class.java.simpleName
        private const val MASTER_TAG = 987051 // use your master tag
        private const val FALLBACK_MASTER_TAG = 111111 // [optional]  If you want to setup that HTML ad would be loaded if video ads fails, you need to set fallback master tag.
    }
}
