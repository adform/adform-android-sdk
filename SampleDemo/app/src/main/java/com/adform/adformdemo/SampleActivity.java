package com.adform.adformdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.adform.sdk.utils.AdSize;

/**
 * Created by mariusmerkevicius on 5/4/15.
 */
public class SampleActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
    }

    public void openAdInline(View v) {
        startActivity(new Intent(this, AdInlineActivity.class));
    }

    public void openAdInlineCustom1(View v) {
        Intent intent = new Intent(this, AdInlineActivityBundle.class);
        intent.putExtra(AdInlineActivityBundle.BUNDLE_KEY_MASTER_TAG, 4672767);
        intent.putExtra(AdInlineActivityBundle.BUNDLE_KEY_AD_SIZE, new AdSize(300, 250));
        startActivity(intent);
    }

    public void openAdInlineCustom2(View v) {
        Intent intent = new Intent(this, AdInlineActivityBundle.class);
        intent.putExtra(AdInlineActivityBundle.BUNDLE_KEY_MASTER_TAG, 4022668);
        intent.putExtra(AdInlineActivityBundle.BUNDLE_KEY_AD_SIZE, new AdSize(320, 50));
        startActivity(intent);
    }

    public void openAdInlineCustom3(View v) {
        Intent intent = new Intent(this, AdInlineActivityBundle.class);
        intent.putExtra(AdInlineActivityBundle.BUNDLE_KEY_MASTER_TAG, 4022833);
        intent.putExtra(AdInlineActivityBundle.BUNDLE_KEY_AD_SIZE, new AdSize(320, 50));
        startActivity(intent);
    }

    public void openAdInlineCustom4(View v) {
        Intent intent = new Intent(this, AdInlineActivityBundle.class);
        intent.putExtra(AdInlineActivityBundle.BUNDLE_KEY_MASTER_TAG, 4015369);
        intent.putExtra(AdInlineActivityBundle.BUNDLE_KEY_AD_SIZE, new AdSize(320, 50));
        startActivity(intent);
    }

    public void openAdOverlayCustom1(View v) {
        Intent intent = new Intent(this, AdOverlayActivityBundle.class);
        intent.putExtra(AdOverlayActivityBundle.BUNDLE_KEY_MASTER_TAG, 4660166);
        startActivity(intent);
    }

    public void openAdOverlayCustom2(View v) {
        Intent intent = new Intent(this, AdOverlayActivityBundle.class);
        intent.putExtra(AdOverlayActivityBundle.BUNDLE_KEY_MASTER_TAG, 4935199);
        startActivity(intent);
    }

    public void openAdInlineLW(View v) {
        startActivity(new Intent(this, AdInlineLWActivity.class));
    }

    public void openAdOverlay(View v) {
        startActivity(new Intent(this, AdOverlayActivity.class));
    }

    public void openAdInterstitial(View v) {
        startActivity(new Intent(this, AdInterstitialActivity.class));
    }

    public void openAdHesion(View v) {
        startActivity(new Intent(this, AdHesionActivity.class));
    }

}
