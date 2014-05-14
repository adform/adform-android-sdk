package com.adform.sample.app;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import com.adform.sample.app.fragments.DemoFragment1;
import com.adform.sample.app.fragments.DemoFragment2;
import com.adform.sample.app.fragments.DemoFragment3;

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
                mTabHost.newTabSpec("tab1").setIndicator("Tab 1"),
                DemoFragment1.class, null
        );
        mTabHost.addTab(
                mTabHost.newTabSpec("tab2").setIndicator("Tab 2"),
                DemoFragment2.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec("tab3").setIndicator("Tab 3"),
                DemoFragment3.class, null);
    }


}
