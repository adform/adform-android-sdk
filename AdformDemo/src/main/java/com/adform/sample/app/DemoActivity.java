package com.adform.sample.app;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import com.adform.sample.app.fragments.*;

/**
 * Created by mariusm on 13/05/14.
 */
public class DemoActivity extends FragmentActivity {
    private FragmentTabHost mTabHost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_demo);

        mTabHost = (FragmentTabHost) findViewById(R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.tabFrameLayout);

        mTabHost.addTab(
                mTabHost.newTabSpec("tab1").setIndicator("Glued"),
                DemoFragment1.class, null
        );
        mTabHost.addTab(
                mTabHost.newTabSpec("tab2").setIndicator("Inside"),
                DemoFragment2.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec("tab3").setIndicator("ListView each 5"),
                DemoFragment3.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec("tab4").setIndicator("ListView only 5"),
                DemoFragment4.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec("tab5").setIndicator("Interstitial"),
                DemoFragment5.class, null);
    }
}
