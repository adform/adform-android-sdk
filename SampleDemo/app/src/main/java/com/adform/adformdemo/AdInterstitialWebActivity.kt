package com.adform.adformdemo

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.adform.adformdemo.support.EmptyWebviewFragment
import com.adform.sdk.interfaces.AdListener
import com.adform.sdk.pager.AdInterstitial
import com.adform.sdk.pager.AdInterstitial.AdInterstitialStateListener
import com.adform.sdk.pub.views.AdInline
import java.util.ArrayList

/**
 * An ad implementation that can be used with ViewPager class (swiping content,
 * and ad is displayed between the pages).
 */
class AdInterstitialWebActivity : AppCompatActivity() {
    private lateinit var texts: ArrayList<String>
    private lateinit var adInterstitial: AdInterstitial
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_adinterstitial_web)
        // [app related] Initializing pager
        val pager = findViewById<View>(R.id.pager) as ViewPager
        val pagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager)
        pager.adapter = pagerAdapter
        pager.offscreenPageLimit = 1
        texts = ArrayList()
        for (i in 0 until NUM_PAGES) texts.add("Text $i")

        // Base implementation of adInterstitial

        // [mandatory] Getting an instance of the view
        adInterstitial = findViewById<View>(R.id.pager_container) as AdInterstitial

        // [mandatory] Setting master tag. [optional] if set in layout XML
        adInterstitial.setMasterTagId(142636)

        // [optional] Debug mode for testing ad. Can be set in layout XML
        adInterstitial.setDebugMode(false)

        // [optional] Custom implementation of the pager with overriden AdInterstitial ViewPager OnPageChangeListener events
        adInterstitial.setOverridePagerPageChangeListener(true)
        pager.setOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                adInterstitial.increaseShownPageCount()
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        adInterstitial.setListener(object : AdListener {
            override fun onAdLoadSuccess(adInline: AdInline) {
                Log.d(TAG, "Success")
            }

            override fun onAdLoadFail(adInline: AdInline, failError: String) {
                Log.d(TAG, "Fail: $failError")
            }
        })
        adInterstitial.setStateListener(object : AdInterstitialStateListener {
            override fun onAdShown() {
                Log.d(TAG, "Show")
            }

            override fun onAdClose() {
                Log.d(TAG, "Close")
            }
        })
    }

    //region World events
    override fun onResume() {
        super.onResume()
        // [mandatory] Sending world event to the view
        adInterstitial.onResume()
    }

    override fun onPause() {
        super.onPause()
        // [mandatory] Sending world event to the view
        adInterstitial.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        // [mandatory] Sending world event to the view
        adInterstitial.destroy()
    }
    //endregion
    //region Classes
    /**
     * A simple pager adapter that represents ScreenSlidePageFragment objects, in
     * sequence.
     */
    private inner class ScreenSlidePagerAdapter(
        fm: FragmentManager
    ) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            val emptyFragment = EmptyWebviewFragment()
            emptyFragment.setText(texts[position])
            return emptyFragment
        }

        override fun getItemPosition(`object`: Any): Int {
            return super.getItemPosition(`object`)
        }

        override fun getCount(): Int {
            return NUM_PAGES
        }
    } //endregion

    companion object {
        private const val NUM_PAGES = 30
        private val TAG = AdInterstitialWebActivity::class.java.simpleName
    }
}
