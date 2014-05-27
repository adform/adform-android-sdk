package com.adform.sdk2.activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import com.adform.sdk2.utils.Utils;
import com.adform.sdk2.view.inner.InnerInterstitialView;

/**
 * Created by mariusm on 21/05/14.
 */
public class AdformInterstitialActivity extends Activity {
    public static final String HTML_DATA = "HTML_DATA";

    private InnerInterstitialView mInnerInterstitialView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        RelativeLayout mainContainer = new RelativeLayout(this);
        final RelativeLayout.LayoutParams adViewLayout = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        adViewLayout.addRule(RelativeLayout.CENTER_IN_PARENT);
        mainContainer.addView(getAdView(), adViewLayout);
        setContentView(mainContainer);

        // Showing content
        if (getIntent().getExtras() != null) {
            mInnerInterstitialView.showContent(getIntent().getExtras().getString(HTML_DATA));
        }
    }

    public static void startActivity(Context context, String data) {
        Intent intent = new Intent(context, AdformInterstitialActivity.class);
        intent.putExtra(HTML_DATA, data);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException anfe) {
            Utils.p("AdformInterstitial activity must be declared in AndroidManifest.xml");
        }
    }

    //TODO mariusm 23/05/14 This probably should be put in core class as an abstract method
    protected View getAdView() {
        mInnerInterstitialView = new InnerInterstitialView(getBaseContext());
        return mInnerInterstitialView;
    }
}
