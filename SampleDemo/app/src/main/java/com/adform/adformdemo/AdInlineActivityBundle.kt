package com.adform.adformdemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.adform.sdk.pub.views.AdInline
import com.adform.sdk.utils.AdSize

/**
 * The most basic ad implementation. You pass in mandatory events and parameters,
 * start loading ad, and it handles everything else automatically.
 */
class AdInlineActivityBundle : AppCompatActivity() {

    private lateinit var adInline: AdInline

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_adinline)
        adInline = findViewById(R.id.view_ad_inline)
        adInline.setDebugMode(true)
        adInline.masterTagId = intent.extras!!.getInt(BUNDLE_KEY_MASTER_TAG)
        adInline.adSize = intent.extras!!.getSerializable(BUNDLE_KEY_AD_SIZE) as AdSize?
        adInline.loadAd()
    }

    override fun onResume() {
        super.onResume()
        adInline.onResume()
    }

    override fun onPause() {
        super.onPause()
        adInline.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        adInline.destroy()
    }

    companion object {
        const val BUNDLE_KEY_MASTER_TAG = "BUNDLE_KEY_MASTER_TAG"
        const val BUNDLE_KEY_AD_SIZE = "BUNDLE_KEY_AD_SIZE"
    }
}
