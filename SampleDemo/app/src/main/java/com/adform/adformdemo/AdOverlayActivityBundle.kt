package com.adform.adformdemo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.adform.sdk.pub.AdOverlay

/**
 * An ad implementation, that loads an ad, and shows it whenever its needed. It
 * displays a fullscreen ad with a close button.
 */
class AdOverlayActivityBundle : AppCompatActivity(), View.OnClickListener {
    private lateinit var adOverlay: AdOverlay
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        // [app related] Initializing additional buttons for user control
        setContentView(R.layout.activity_adoverlay)
        val loadButton = findViewById<View>(R.id.load_button)
        loadButton?.setOnClickListener(this)
        val showButton = findViewById<View>(R.id.show_button)
        showButton?.setOnClickListener(this)
        adOverlay = AdOverlay.createInstance(this)
        adOverlay.setMasterTagId(intent.extras!!.getInt(BUNDLE_KEY_MASTER_TAG))
        adOverlay.setDebugMode(true)
    }

    override fun onResume() {
        super.onResume()
        adOverlay.onResume()
    }

    override fun onPause() {
        super.onPause()
        adOverlay.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        adOverlay.destroy()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.load_button -> {
                adOverlay.loadAd()
            }
            R.id.show_button -> {
                adOverlay.showAd()
            }
        }
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        adOverlay.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        adOverlay.onRestoreInstanceState(savedInstanceState)
    }

    companion object {
        const val BUNDLE_KEY_MASTER_TAG = "BUNDLE_KEY_MASTER_TAG"
    }
}
