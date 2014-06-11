package com.adform.sample.app.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.adform.sample.app.R;
import com.adform.sdk.utils.builders.CustomParamBuilder;
import com.adform.sdk.view.CoreAdView;

/**
 * Created by mariusm on 13/05/14.
 */
public class DemoFragment2 extends Fragment {

    private CoreAdView mAdView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main2, null);
        mAdView = (CoreAdView) view.findViewById(R.id.custom_ad_view);

        // Use builder to set custom parameters...
        mAdView.setCustomParams(new CustomParamBuilder()
                        .addCustomParam("gender", "female")
                        .addCustomParam("age", "23")
                        .buildParams()
        );
        return view;
    }

    @Override
    public void onDestroy() {
        if (mAdView != null)
            mAdView.destroy();
        super.onDestroy();
    }

}
