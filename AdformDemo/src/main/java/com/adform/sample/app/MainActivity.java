package com.adform.sample.app;

import android.app.Activity;
import android.os.Bundle;
import com.adform.sdk2.view.CoreAdView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CoreAdView mAdView = (CoreAdView) findViewById(R.id.custom_ad_view);
    }
}
