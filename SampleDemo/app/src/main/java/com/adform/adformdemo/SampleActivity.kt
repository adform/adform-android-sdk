package com.adform.adformdemo

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.adform.sdk.utils.AdSize

class SampleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_sample)
    }

    fun openAdInline(v: View?) {
        startActivity(Intent(this, AdInlineActivity::class.java))
    }

    fun openAdInlineCustom1(v: View?) {
        val intent = Intent(this, AdInlineActivityBundle::class.java)
        intent.putExtra(AdInlineActivityBundle.BUNDLE_KEY_MASTER_TAG, 4672767)
        intent.putExtra(AdInlineActivityBundle.BUNDLE_KEY_AD_SIZE, AdSize(300, 250))
        startActivity(intent)
    }

    fun openAdInlineCustom2(v: View?) {
        val intent = Intent(this, AdInlineActivityBundle::class.java)
        intent.putExtra(AdInlineActivityBundle.BUNDLE_KEY_MASTER_TAG, 4022668)
        intent.putExtra(AdInlineActivityBundle.BUNDLE_KEY_AD_SIZE, AdSize(320, 50))
        startActivity(intent)
    }

    fun openAdInlineCustom3(v: View?) {
        val intent = Intent(this, AdInlineActivityBundle::class.java)
        intent.putExtra(AdInlineActivityBundle.BUNDLE_KEY_MASTER_TAG, 4022833)
        intent.putExtra(AdInlineActivityBundle.BUNDLE_KEY_AD_SIZE, AdSize(320, 50))
        startActivity(intent)
    }

    fun openAdInlineCustom4(v: View?) {
        val intent = Intent(this, AdInlineActivityBundle::class.java)
        intent.putExtra(AdInlineActivityBundle.BUNDLE_KEY_MASTER_TAG, 4015369)
        intent.putExtra(AdInlineActivityBundle.BUNDLE_KEY_AD_SIZE, AdSize(320, 50))
        startActivity(intent)
    }

    fun openAdOverlayCustom1(v: View?) {
        val intent = Intent(this, AdOverlayActivityBundle::class.java)
        intent.putExtra(AdOverlayActivityBundle.BUNDLE_KEY_MASTER_TAG, 4660166)
        startActivity(intent)
    }

    fun openAdOverlayCustom2(v: View?) {
        val intent = Intent(this, AdOverlayActivityBundle::class.java)
        intent.putExtra(AdOverlayActivityBundle.BUNDLE_KEY_MASTER_TAG, 4935199)
        startActivity(intent)
    }

    fun openInStreamActivity(v: View?) {
        val intent = Intent(this, InStreamActivity::class.java)
        intent.putExtra(InStreamActivity.BUNDLE_KEY_MASTER_TAG, 1505418)
        startActivity(intent)
    }

    fun openAdInlineLW(v: View?) {
        startActivity(Intent(this, AdInlineLWActivity::class.java))
    }

    fun openAdOverlay(v: View?) {
        startActivity(Intent(this, AdOverlayActivity::class.java))
    }

    fun openAdInterstitial(v: View?) {
        startActivity(Intent(this, AdInterstitialActivity::class.java))
    }

    fun openAdWebInterstitial(v: View?) {
        startActivity(Intent(this, AdInterstitialWebActivity::class.java))
    }

    fun openAdHesion(v: View?) {
        startActivity(Intent(this, AdHesionActivity::class.java))
    }

    fun openAdNative(v: View?) {
        startActivity(Intent(this, AdNativeActivity::class.java))
    }
}
