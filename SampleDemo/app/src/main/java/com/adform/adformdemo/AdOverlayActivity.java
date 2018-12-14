package com.adform.adformdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.adform.sdk.network.entities.AdformEnum;
import com.adform.sdk.pub.AdOverlay;

/**
 * An ad implementation, that loads an ad, and shows it whenever its needed. It
 * displays a fullscreen ad with a close button.
 */
public class AdOverlayActivity extends Activity implements View.OnClickListener {

    private AdOverlay adOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // [app related] Initializing additional buttons for user control
        setContentView(R.layout.activity_adoverlay);
        View loadButton = findViewById(R.id.load_button);
        if (loadButton != null)
            loadButton.setOnClickListener(this);
        View showButton = findViewById(R.id.show_button);
        if (showButton != null)
            showButton.setOnClickListener(this);

        // [mandatory] Initializing AdOverlay with its base parameters
        adOverlay = AdOverlay.createInstance(this);

        // [mandatory] Setting master tag. [optional] if set in layout XML
        adOverlay.setMasterTagId(142636);

        // [optional] Debug mode for testing ad. Can be set in layout XML
        adOverlay.setDebugMode(false);

        // [optional] Setting base listeners for ad load status
        adOverlay.setListener(new AdOverlay.OverlayLoaderListener() {
            @Override
            public void onLoadSuccess() {
            }

            @Override
            public void onLoadError(String error) {
            }

            @Override
            public void onShowError(String error) {
            }
        });

        // [optional] Setting additional listeners for ad events
        adOverlay.setStateListener(new AdOverlay.OverlayStateListener() {
            @Override
            public void onAdShown() {
            }

            @Override
            public void onAdClose() {
            }
        });

        // [optional] You can set an animation type that could be used when ad is shown
        // adOverlay.setPresentationStyle(AdformEnum.AnimationType.FADE);
        // adOverlay.setPresentationStyle(AdformEnum.AnimationType.NO_ANIMATION);
        adOverlay.setPresentationStyle(AdformEnum.AnimationType.FADE); // default

        // [optional] This flag sets if dim should be used in the background when ad shows.
        adOverlay.setDimOverlayEnabled(true); // default
    }

    @Override
    protected void onResume() {
        super.onResume();
        // [mandatory] Sending world event to the view
        if (adOverlay != null)
            adOverlay.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // [mandatory] Sending world event to the view
        if (adOverlay != null)
            adOverlay.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // [mandatory] Sending world event to the view
        if (adOverlay != null)
            adOverlay.destroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.load_button: {
                // [mandatory] Sending load event
                adOverlay.loadAd();
                break;
            }
            case R.id.show_button: {
                // [mandatory] Sending show event.
                // (If ad was not loaded, it loads first automatically)
                adOverlay.showAd();
                break;
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // [mandatory] Sending world event to the view
        if (adOverlay != null)
            adOverlay.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // [mandatory] Sending world event to the view
        if (adOverlay != null)
            adOverlay.onRestoreInstanceState(savedInstanceState);
    }

}
