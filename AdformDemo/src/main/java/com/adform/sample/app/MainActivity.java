package com.adform.sample.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import com.adform.sdk2.view.CoreAdView;

public class MainActivity extends Activity implements CoreAdView.CoreAdViewListener {

    private View mPlaceHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CoreAdView mAdView = (CoreAdView) findViewById(R.id.custom_ad_view);
        mPlaceHolder = findViewById(R.id.place_holder);
    }

    @Override
    public void onAdVisibilityChange(boolean visibility) {
        mPlaceHolder.setVisibility((visibility)?View.VISIBLE:View.GONE);
    }
}
