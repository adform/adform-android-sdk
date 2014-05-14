package com.adform.sample.app.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.adform.sample.app.R;
import com.adform.sdk2.utils.Utils;
import com.adform.sdk2.view.CoreAdView;

/**
 * Created by mariusm on 13/05/14.
 */
public class DemoFragment1 extends Fragment implements CoreAdView.CoreAdViewListener {
    private View mPlaceHolder;
    private Handler mHandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, null);
        CoreAdView mAdView = (CoreAdView) view.findViewById(R.id.custom_ad_view);
        mAdView.setListener(this);
        mPlaceHolder = view.findViewById(R.id.place_holder);
        mPlaceHolder.setVisibility(mAdView.isAdVisible()?View.VISIBLE:View.GONE);
        return view;
    }

    @Override
    public void onAdVisibilityChange(final boolean visible) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mPlaceHolder.setVisibility((visible)?View.VISIBLE:View.GONE);
            }
        });
    }

}
