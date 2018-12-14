package com.adform.adformdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.adform.sdk.pub.AdOverlay;

/**
 * An ad implementation, that loads an ad, and shows it whenever its needed. It
 * displays a fullscreen ad with a close button.
 */
public class AdOverlayActivityBundle extends Activity implements View.OnClickListener {

    public static final String BUNDLE_KEY_MASTER_TAG = "BUNDLE_KEY_MASTER_TAG";
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
        adOverlay = AdOverlay.createInstance(this);
        adOverlay.setMasterTagId(getIntent().getExtras().getInt(BUNDLE_KEY_MASTER_TAG));
        adOverlay.setDebugMode(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adOverlay.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        adOverlay.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adOverlay.destroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.load_button: {
                adOverlay.loadAd();
                break;
            }
            case R.id.show_button: {
                adOverlay.showAd();
                break;
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        adOverlay.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        adOverlay.onRestoreInstanceState(savedInstanceState);
    }

}
