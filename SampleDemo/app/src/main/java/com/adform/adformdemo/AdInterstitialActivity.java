package com.adform.adformdemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.adform.adformdemo.support.EmptyFragment;
import com.adform.sdk.pager.AdInterstitial;

import java.util.ArrayList;

/**
 * An ad implementation that can be used with ViewPager class (swiping content,
 * and ad is displayed between the pages).
 */
public class AdInterstitialActivity extends FragmentActivity {
    private static final int NUM_PAGES = 30;
    private ArrayList<String> texts;

    private AdInterstitial adInterstitial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adinterstitial);
        // [app related] Initializing pager
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        ScreenSlidePagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        pager.setOffscreenPageLimit(1);
        texts = new ArrayList<String>();
        for (int i = 0; i < NUM_PAGES; i++)
            texts.add("Text "+i);

        // Base implementation of adInterstitial

        // [mandatory] Getting an instance of the view
        adInterstitial = (AdInterstitial) findViewById(R.id.pager_container);

        // [mandatory] Setting master tag. [optional] if set in layout XML
        adInterstitial.setMasterTagId(4660165);

        // [optional] Debug mode for testing ad. Can be set in layout XML
        adInterstitial.setDebugMode(true);

        // [optional] Custom implementation of the pager with overriden AdInterstitial ViewPager OnPageChangeListener events
        adInterstitial.setOverridePagerPageChangeListener(true);
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                adInterstitial.increaseShownPageCount();
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // [mandatory] Sending world event to the view
        if (adInterstitial != null)
            adInterstitial.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // [mandatory] Sending world event to the view
        if (adInterstitial != null)
            adInterstitial.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // [mandatory] Sending world event to the view
        if (adInterstitial != null)
            adInterstitial.destroy();
    }

    /**
     * A simple pager adapter that represents ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object2) {
            super.destroyItem(container, position, object2);
        }

        @Override
        public Fragment getItem(int position) {
            EmptyFragment emptyFragment = new EmptyFragment();
            emptyFragment.setText(texts.get(position));
            return emptyFragment;
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

}
