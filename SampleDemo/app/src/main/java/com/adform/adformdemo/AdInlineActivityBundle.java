package com.adform.adformdemo;

import android.app.Activity;
import android.os.Bundle;

import com.adform.sdk.pub.views.AdInline;
import com.adform.sdk.utils.AdSize;

/**
 * The most basic ad implementation. You pass in mandatory events and parameters,
 * start loading ad, and it handles everything else automatically.
 */
public class AdInlineActivityBundle extends Activity {

    public static final String BUNDLE_KEY_MASTER_TAG = "BUNDLE_KEY_MASTER_TAG";
    public static final String BUNDLE_KEY_AD_SIZE = "BUNDLE_KEY_AD_SIZE";
    private AdInline adInline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adinline);
        adInline = (AdInline) findViewById(R.id.view_ad_inline);
        adInline.setDebugMode(true);
        adInline.setMasterTagId(getIntent().getExtras().getInt(BUNDLE_KEY_MASTER_TAG));
        adInline.setAdSize(((AdSize) getIntent().getExtras().getSerializable(BUNDLE_KEY_AD_SIZE)));
        adInline.loadAd();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adInline != null)
            adInline.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (adInline != null)
            adInline.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adInline != null)
            adInline.destroy();
    }

}
