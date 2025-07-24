package com.adform.adformdemo

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.adform.adformdemo.support.toLogString
import com.adform.sdk.network.entities.OpenRTBRequest
import com.adform.sdk.network.entities.OpenRTBRequest.Imp
import com.adform.sdk.network.entities.OpenRTBRequest.NativeRequest
import com.adform.sdk.network.entities.OpenRTBRequest.NativeRequestAsset
import com.adform.sdk.network.entities.OpenRTBRequest.RequestDataAsset
import com.adform.sdk.network.entities.OpenRTBRequest.RequestImgAsset
import com.adform.sdk.network.entities.OpenRTBResponse
import com.adform.sdk.network.nativead.NativeAdListener
import com.adform.sdk.network.nativead.OpenRTBBidLoader
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView

class AdNativeActivity : AppCompatActivity() {

    private var ad: NativeAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_admob)
        initAdmob()
        initButtons()
    }

    private fun initButtons() {
        findViewById<Button>(R.id.admob_mediation_button).setOnClickListener {
            loadAdmobAd()
        }
        findViewById<Button>(R.id.admob_ad_inspector_button).setOnClickListener {
            openAdInspector()
        }
        findViewById<Button>(R.id.adform_native_ad_button).setOnClickListener {
            loadAdformAd()
        }
    }

    private fun initAdmob() {
        Thread {
            MobileAds.initialize(
                this
            ) { initializationStatus: InitializationStatus ->
                val statusMap = initializationStatus.adapterStatusMap
                for (adapterClass in statusMap.keys) {
                    val status = statusMap.get(adapterClass)
                    Log.d(
                        "AdNativeActivity",
                        String.format(
                            "Adapter name: %s, Description: %s, Latency: %d",
                            adapterClass,
                            status!!.description,
                            status.latency
                        )
                    )
                }

            }
        }.start()
    }

    private fun openAdInspector() {
        MobileAds.openAdInspector(
            this
        ) {
            // Error will be non-null if ad inspector closed due to an error.
        }
    }

    @SuppressLint("InflateParams")
    private fun loadAdmobAd() {
        val adLoader = AdLoader.Builder(
            this,
            "ca-app-pub-3084463524165616/1325995230"
        ).forNativeAd { ad ->
            this@AdNativeActivity.ad = ad
            // Inflate a layout and add it to the parent ViewGroup.
            val inflater = this@AdNativeActivity
                .getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater

            val adView = inflater.inflate(
                R.layout.native_ad_layout,
                null
            ) as NativeAdView

            adView.headlineView = adView.findViewById(R.id.ad_headline)
            (adView.headlineView as TextView).text = ad.headline

            val bodyTextView = adView.findViewById<TextView>(R.id.ad_body)
            adView.bodyView = bodyTextView
            ad.body?.let {
                bodyTextView.text = it
                bodyTextView.visibility = View.VISIBLE
            } ?: run {
                bodyTextView.visibility = View.INVISIBLE
            }

            val ctaButton = adView.findViewById<Button>(R.id.ad_call_to_action)
            adView.callToActionView = ctaButton
            ad.callToAction?.let {
                ctaButton.text = it
                ctaButton.visibility = View.VISIBLE
            } ?: run {
                ctaButton.visibility = View.INVISIBLE
            }

            val iconView = adView.findViewById<ImageView>(R.id.ad_app_icon)
            ad.icon?.let { icon ->
                iconView.setImageDrawable(icon.drawable)
                iconView.visibility = View.VISIBLE
                adView.iconView = iconView
            } ?: run {
                iconView.visibility = View.GONE
            }

            val ratingBar = adView.findViewById<RatingBar>(R.id.ad_stars)
            ad.starRating?.let { rating ->
                ratingBar.rating = rating.toFloat()
                ratingBar.visibility = View.VISIBLE
            } ?: run {
                ratingBar.visibility = View.GONE
            }

            val advertiserView =
                adView.findViewById<TextView>(R.id.ad_advertiser)
            advertiserView.text = ad.advertiser

            val mediaView = adView.findViewById<MediaView>(R.id.ad_media)
            adView.mediaView = mediaView

            adView.setNativeAd(ad)

            val adFrame = findViewById<FrameLayout>(R.id.container)
            adFrame.removeAllViews()
            adFrame.addView(adView)
        }.withAdListener(object : AdListener() {

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
                Log.d("AdNativeActivity", "onAdFailedToLoad: $loadAdError")
            }

        }).withNativeAdOptions(
            NativeAdOptions.Builder().build()
        ).build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun loadAdformAd() {
        val loader = OpenRTBBidLoader(this, createRequest(this))
        loader.setListener(object : NativeAdListener {
            override fun onSuccess(openRTBResponse: OpenRTBResponse) {
                Toast.makeText(
                    this@AdNativeActivity,
                    "Success loading Adform native ad",
                    Toast.LENGTH_SHORT
                ).show()
                Log.i("AdNativeActivity", "onSuccess: ${openRTBResponse.toLogString()}")
            }

            override fun onFail(errorMessage: String) {
                Toast.makeText(
                    this@AdNativeActivity,
                    "Error loading Adform native add",
                    Toast.LENGTH_SHORT
                ).show()
                Log.w("AdNativeActivity", "onFail: $errorMessage")
            }
        })
        loader.requestAd()
    }

    private fun createRequest(
        context: Context
    ): OpenRTBRequest {
        val nativeAd = createNativeAdRequest()
        val imp = Imp()
        imp.id = "1"
        imp.tagid = "1151535"
        imp.nativeAd = nativeAd
        return OpenRTBRequest.Builder()
            .setId("322a0c8ff399a7")
            .setImpression(listOf(imp))
            .build(context)
    }

    private fun createNativeAdRequest(): OpenRTBRequest.Native {
        val nativeAd = OpenRTBRequest.Native()
        val nativeRequest = NativeRequest()

        val assetMainImage = NativeRequestAsset().apply {
            id = 0
            required = 0
            img = RequestImgAsset()
            img.hmin = 166
            img.wmin = 200
            img.type = 3
        }

        val assetTitle = NativeRequestAsset().apply {
            id = 1
            required = 0
            data = RequestDataAsset()
            data.type = 1
        }

        val assetDescription = NativeRequestAsset().apply {
            id = 2
            required = 0
            data = RequestDataAsset()
            data.type = 1
        }

        val assetSponsoredBy = NativeRequestAsset().apply {
            id = 3
            required = 0
            data = RequestDataAsset()
            data.type = 1
        }

        val assetAppIcon = NativeRequestAsset().apply {
            id = 4
            required = 0
            img = RequestImgAsset()
            img.type = 1
            img.wmin = 10
            img.hmin = 10
        }

        val assetCallToAction = NativeRequestAsset().apply {
            id = 5
            required = 0
            data = RequestDataAsset()
            data.type = 12
        }

        val assetRating = NativeRequestAsset()
            .apply {
                id = 6
                required = 0
                data = RequestDataAsset()
                data.type = 3
            }

        nativeAd.request = nativeRequest
        val assets: MutableList<NativeRequestAsset> = ArrayList()
        assets.add(assetMainImage)
        assets.add(assetTitle)
        assets.add(assetDescription)
        assets.add(assetSponsoredBy)
        assets.add(assetAppIcon)
        assets.add(assetCallToAction)
        assets.add(assetRating)
        nativeRequest.assets = assets
        return nativeAd
    }

    override fun onDestroy() {
        super.onDestroy()
        ad?.destroy()
    }
}