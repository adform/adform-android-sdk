package com.adform.adformdemo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.adform.sdk.network.entities.AdformEnum
import com.adform.sdk.pub.AdOverlay
import com.adform.sdk.pub.AdOverlay.OverlayLoaderListener
import com.adform.sdk.pub.AdOverlay.OverlayStateListener

/**
 * An ad implementation, that loads an ad, and shows it whenever its needed. It
 * displays a fullscreen ad with a close button.
 */
class AdOverlayActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var adOverlay: AdOverlay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // [app related] Initializing additional buttons for user control
        setContentView(R.layout.activity_adoverlay)
        val loadButton = findViewById<View>(R.id.load_button)
        loadButton?.setOnClickListener(this)
        val showButton = findViewById<View>(R.id.show_button)
        showButton?.setOnClickListener(this)

        // [mandatory] Initializing AdOverlay with its base parameters
        adOverlay = AdOverlay.createInstance(this)

        // [mandatory] Setting master tag. [optional] if set in layout XML
        adOverlay.setMasterTagId(MASTER_TAG)

        // [optional] Setting base listeners for ad load status
        adOverlay.setListener(object : OverlayLoaderListener {
            override fun onLoadSuccess() {}
            override fun onLoadError(error: String) {}
            override fun onShowError(error: String) {}
        })

        // [optional] Setting additional listeners for ad events
        adOverlay.setStateListener(object : OverlayStateListener {
            override fun onAdShown() {}
            override fun onAdClose() {}
        })

        // [optional] You can set an animation type that could be used when ad is shown
        // adOverlay.setPresentationStyle(AdformEnum.AnimationType.FADE)
        // adOverlay.setPresentationStyle(AdformEnum.AnimationType.NO_ANIMATION)
        adOverlay.setPresentationStyle(AdformEnum.AnimationType.FADE) // default

        // [optional] This flag sets if dim should be used in the background when ad shows.
        adOverlay.setDimOverlayEnabled(true) // default
    }

    override fun onResume() {
        super.onResume()
        // [mandatory] Sending world event to the view
        adOverlay.onResume()
    }

    override fun onPause() {
        super.onPause()
        // [mandatory] Sending world event to the view
        adOverlay.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        // [mandatory] Sending world event to the view
        adOverlay.destroy()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.load_button -> {
                // [mandatory] Sending load event
                adOverlay.loadAd()
            }
            R.id.show_button -> {
                // [mandatory] Sending show event.
                // (If ad was not loaded, it loads first automatically)
                adOverlay.showAd()
            }
        }
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // [mandatory] Sending world event to the view
        adOverlay.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // [mandatory] Sending world event to the view
        adOverlay.onRestoreInstanceState(savedInstanceState)
    }

    companion object {
        const val MASTER_TAG = 142636
    }
}
