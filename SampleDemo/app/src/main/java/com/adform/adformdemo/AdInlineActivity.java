package com.adform.adformdemo;

import android.app.Activity;
import android.os.Bundle;

import com.adform.sdk.entities.AdformEnum;
import com.adform.sdk.interfaces.AdListener;
import com.adform.sdk.interfaces.AdStateListener;
import com.adform.sdk.pub.views.AdInline;
import com.adform.sdk.utils.AdSize;

/**
 * The most basic ad implementation. You pass in mandatory events and parameters,
 * start loading ad, and it handles everything else automatically.
 */
public class AdInlineActivity extends Activity {

    private AdInline adInline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adinline);

        // [mandatory] Getting an instance of the view
        adInline = (AdInline) findViewById(R.id.view_ad_inline);

        // [optional] Setting base listeners for ad load status
        adInline.setListener(new AdListener() {
            @Override
            public void onAdLoadSuccess(AdInline adInline) { }

            @Override
            public void onAdLoadFail(AdInline adInline, String s) {
                // Setting fallback image whenever ad fails to load
                // findViewById(R.id.ad_container).setBackgroundResource(R.drawable.ic_launcher);
            }
        });

        // [optional] Setting additional listeners for ad events
        adInline.setStateListener(new AdStateListener() {
            @Override
            public void onAdVisibilityChange(AdInline adInline, boolean b) {

            }

            @Override
            public void onAdOpen(AdInline adInline) {

            }

            @Override
            public void onAdClose(AdInline adInline) {

            }
        });

        // [mandatory] Setting ad size. [optional] if set in layout XML
        adInline.setAdSize(new AdSize(320, 50));

        // [mandatory] Setting master tag. [optional] if set in layout XML
        adInline.setMasterTagId(4016318);

        // [optional] Debug mode for testing ad. Can be set in layout XML
        adInline.setDebugMode(true);

        // [optional] You can set an animation type that could be used when ad shows in the banner.
        // adInline.setBannerAnimationType(AdformEnum.AnimationType.FADE);
        // adInline.setBannerAnimationType(AdformEnum.AnimationType.NO_ANIMATION);
        adInline.setBannerAnimationType(AdformEnum.AnimationType.SLIDE); // default

        // [optional] You can set an animation type that could be used when ad is expanded ( mraid.expand() ).
        // adInline.setModalPresentationStyle(AdformEnum.AnimationType.FADE);
        // adInline.setModalPresentationStyle(AdformEnum.AnimationType.NO_ANIMATION);
        adInline.setModalPresentationStyle(AdformEnum.AnimationType.SLIDE); // default

        // [optional] This flag sets if dim should be used in the background whenever mraid.expand() is used.
        adInline.setDimOverlayEnabled(true); // default

        // [mandatory] Send an event to start loading
        adInline.loadAd();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // [mandatory] Sending world event to the view
        if (adInline != null)
            adInline.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // [mandatory] Sending world event to the view
        if (adInline != null)
            adInline.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // [mandatory] Sending world event to the view
        if (adInline != null)
            adInline.destroy();
    }

}
