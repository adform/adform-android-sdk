package com.adform.adformdemo;

import android.app.Activity;
import android.os.Bundle;

import com.adform.sdk.network.entities.AdformEnum;
import com.adform.sdk.pub.views.AdHesion;
import com.adform.sdk.utils.AdSize;

/**
 * Just like {@link com.adform.sdk.pub.views.AdInline}, this is one of the most basic implementations.
 */
public class AdHesionActivity extends Activity {

    private AdHesion adHesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adhesion);

        // [mandatory] Getting an instance of the view
        adHesion = (AdHesion) findViewById(R.id.view_ad_hesion);

        // [optional] Setting view position
        adHesion.setAdPositionType(AdformEnum.AdPositionType.TOP);

        // [mandatory] Setting ad size. [optional] if set in layout XML
        adHesion.setAdSize(new AdSize(320, 50));

        // [mandatory] Setting master tag. [optional] if set in layout XML
        adHesion.setMasterTagId(206416);

        // [optional] Debug mode for testing ad. Can be set in layout XML
        adHesion.setDebugMode(false);

        // [optional] You can set an animation type that could be used when ad shows in the banner.
        // adInline.setBannerAnimationType(AdformEnum.AnimationType.FADE);
        // adInline.setBannerAnimationType(AdformEnum.AnimationType.NO_ANIMATION);
        adHesion.setBannerAnimationType(AdformEnum.AnimationType.SLIDE); // default

        // [optional] You can set an animation type that could be used when ad is expanded ( mraid.expand() ).
        // adInline.setModalPresentationStyle(AdformEnum.AnimationType.FADE);
        // adInline.setModalPresentationStyle(AdformEnum.AnimationType.NO_ANIMATION);
        adHesion.setModalPresentationStyle(AdformEnum.AnimationType.SLIDE); // default

        // [optional] This flag sets if dim should be used in the background whenever mraid.expand() is used.
        adHesion.setDimOverlayEnabled(true); // default

        // [mandatory] Send an event to start loading
        adHesion.loadAd();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // [mandatory] Sending world event to the view
        if (adHesion != null)
            adHesion.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // [mandatory] Sending world event to the view
        if (adHesion != null)
            adHesion.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // [mandatory] Sending world event to the view
        if (adHesion != null)
            adHesion.destroy();
    }

}
