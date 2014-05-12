package com.adform.sample.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import com.adform.sdk2.utils.Utils;
import com.adform.sdk2.view.CoreAdView;

public class MainActivity2 extends Activity implements CoreAdView.CoreAdViewListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        CoreAdView mAdView = (CoreAdView) findViewById(R.id.custom_ad_view);
    }

    @Override
    public void onAdVisibilityChange(CoreAdView.ViewState viewState) {
        Utils.p("Ad changed to "+CoreAdView.ViewState.printType(viewState));
    }
}
