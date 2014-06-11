package com.adform.sample.app.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.adform.sample.app.R;
import com.adform.sdk.network.base.ito.network.NetworkError;
import com.adform.sdk.network.base.ito.network.NetworkTask;
import com.adform.sdk.utils.builders.CustomParamBuilder;
import com.adform.sdk.view.CoreAdView;

import java.util.HashMap;

/**
 * Created by mariusm on 13/05/14.
 */
public class DemoFragment1 extends Fragment implements CoreAdView.CoreAdViewListener {
    private View mPlaceHolder;
    private Handler mHandler = new Handler();
    private CoreAdView mAdView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, null);
        mAdView = (CoreAdView) view.findViewById(R.id.custom_ad_view);
        mAdView.setListener(this);

        // Use builder to set custom parameters...
        mAdView.setCustomParams(new CustomParamBuilder()
                        .addCustomParam("gender", "female")
                        .addCustomParam("age", "23")
                        .buildParams()
        );
        mAdView.clearCustomParams();

        // ...or use variable to store custom params.
        HashMap<String, String> customParams = new CustomParamBuilder()
                .addCustomParam("gender", "female")
                .addCustomParam("age", "23")
                .buildParams();
        mAdView.setCustomParams(customParams);

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

    @Override
    public void onDestroy() {
        if (mAdView != null)
            mAdView.destroy();
        super.onDestroy();
    }
}
