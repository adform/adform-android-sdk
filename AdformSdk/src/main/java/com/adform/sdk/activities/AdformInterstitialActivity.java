package com.adform.sdk.activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.adform.sdk.utils.Utils;
import com.adform.sdk.view.CoreInterstitialView;

/**
 * Created by mariusm on 21/05/14.
 */
public class AdformInterstitialActivity extends Activity implements CoreInterstitialView.CoreInterstitialListener {
    public static final String HTML_DATA = "HTML_DATA";
    public static final String IS_CONTENT_MRAID = "IS_CONTENT_MRAID";

    private CoreInterstitialView mInterstitialView;
    private Handler mHandler;
    private Configuration mLastConfiguration = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mHandler = new Handler();

        final RelativeLayout mainContainer = new RelativeLayout(this);
        final RelativeLayout.LayoutParams adViewLayout = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        adViewLayout.addRule(RelativeLayout.CENTER_IN_PARENT);
        mainContainer.addView(getAdView(), adViewLayout);

        // Showing content
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mInterstitialView.showContent(extras.getString(HTML_DATA), extras.getBoolean(IS_CONTENT_MRAID));
        }
        setContentView(mainContainer);
    }

    public static void startActivity(Context context, String data, boolean isMraid) {
        Intent intent = new Intent(context, AdformInterstitialActivity.class);
        intent.putExtra(HTML_DATA, data);
        intent.putExtra(IS_CONTENT_MRAID, isMraid);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException anfe) {
            Utils.p("AdformInterstitial activity must be declared in AndroidManifest.xml");
        }
    }

    //TODO mariusm 23/05/14 This probably should be put in core class as an abstract method
    protected View getAdView() {
        mInterstitialView = new CoreInterstitialView(getBaseContext());
        mInterstitialView.setListener(this);
        return mInterstitialView;
    }

    @Override
    public void onAdClose() {
        finish();
//        if (mLastConfiguration == null)
//            mLastConfiguration = getResources().getConfiguration();
//        if (mLastConfiguration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        } else {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        }
    }

    @Override
    public void onAdOrientationChange(int orientation) {
        setRequestedOrientation(orientation);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
