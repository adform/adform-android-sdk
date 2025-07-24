package com.adform.adformdemo

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
import com.adform.sdk.network.entities.OpenRTBRequest.RequestTitleAsset
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
                        "Admob",
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

            adView.bodyView = adView.findViewById(R.id.ad_body)
            if (ad.body != null) {
                (adView.bodyView as TextView).text = ad.body
                adView.bodyView!!.visibility = View.VISIBLE
            } else {
                adView.bodyView!!.visibility = View.INVISIBLE
            }

            adView.callToActionView =
                adView.findViewById(R.id.ad_call_to_action)
            if (ad.callToAction != null) {
                (adView.callToActionView as Button).text = ad.callToAction
                adView.callToActionView!!.visibility = View.VISIBLE
            } else {
                adView.callToActionView!!.visibility = View.INVISIBLE
            }

            val iconView =
                adView.findViewById<ImageView>(R.id.ad_app_icon)
            if (ad.icon != null) {
                iconView.setImageDrawable(ad.icon!!.drawable)
                iconView.setVisibility(View.VISIBLE)
                adView.iconView = iconView
            } else {
                iconView.setVisibility(View.GONE)
            }

            val ratingBar =
                adView.findViewById<RatingBar>(R.id.ad_stars)
            if (ad.starRating != null) {
                ratingBar.rating = ad.starRating!!.toFloat()
                ratingBar.visibility = View.VISIBLE
            } else {
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
                Log.d("AdmobActivity", "onAdFailedToLoad: $loadAdError")
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

        val assetMainImage = NativeRequestAsset()
        assetMainImage.id = 0
        assetMainImage.required = 0
        assetMainImage.img = RequestImgAsset()
        assetMainImage.img.hmin = 166
        assetMainImage.img.wmin = 200
        assetMainImage.img.type = 3

        val assetTitle = NativeRequestAsset()
        assetTitle.id = 1
        assetTitle.required = 0
        assetTitle.title = RequestTitleAsset()
        assetTitle.title.len = 150

        val assetDescription = NativeRequestAsset()
        assetDescription.id = 2
        assetDescription.required = 0
        assetDescription.data = RequestDataAsset()
        assetDescription.data.type = 2

        val assetSponsoredBy = NativeRequestAsset()
        assetSponsoredBy.id = 3
        assetSponsoredBy.required = 0
        assetSponsoredBy.data = RequestDataAsset()
        assetSponsoredBy.data.type = 1

        val assetAppIcon = NativeRequestAsset()
        assetAppIcon.id = 4
        assetAppIcon.required = 0
        assetAppIcon.img = RequestImgAsset()
        assetAppIcon.img.type = 1
        assetAppIcon.img.wmin = 10
        assetAppIcon.img.hmin = 10

        val assetCallToAction = NativeRequestAsset()
        assetCallToAction.id = 5
        assetCallToAction.required = 0
        assetCallToAction.data = RequestDataAsset()
        assetCallToAction.data.type = 12

        val assetRating = NativeRequestAsset()
        assetRating.id = 6
        assetRating.required = 0
        assetRating.data = RequestDataAsset()
        assetRating.data.type = 3

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
        if (ad != null) {
            ad!!.destroy()
        }
    }
}