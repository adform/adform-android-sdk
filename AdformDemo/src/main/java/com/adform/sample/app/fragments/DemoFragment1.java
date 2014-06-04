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
import com.adform.sdk.view.CoreAdView;

/**
 * Created by mariusm on 13/05/14.
 */
public class DemoFragment1 extends Fragment implements CoreAdView.CoreAdViewListener {
    private View mPlaceHolder;
    private Handler mHandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, null);
        CoreAdView mAdView = (CoreAdView) view.findViewById(R.id.custom_ad_view);
        mAdView.setListener(this);
        mAdView.addCustomParam("gender", "female");
        mAdView.addCustomParam("age", "23");
//        mAdView.setMasterId(111111);
        mAdView.setMasterId(11234);
        mAdView.setPublisherId(654321);
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
    public void onNetworkError(NetworkTask request, NetworkError networkError) {
//        HashMap<String, String> networkMap = new HashMap<String, String>();
//        networkMap.put("url", request.getRequest().getUrl());
//        for (String s : request.getRequest().getHeaders().keySet()) {
//            networkMap.put("header_"+s, request.getRequest().getHeaders().get(s));
//        }
//        for (NameValuePair nameValuePair : request.getRequest().getParams()) {
//            networkMap.put("param_"+nameValuePair.getName(), nameValuePair.getValue());
//        }
//        Sentry.captureEvent(new Sentry.SentryEventBuilder()
//                        .setMessage("NetworkError(" + networkError.getType().name() + ":" + networkError.getErrorCode() + "):" + networkError.getMessage())
//                        .setExtra(networkMap)
//                        .setCulprit("AdformDemoApp")
//                        .setLevel(Sentry.SentryEventBuilder.SentryEventLevel.WARNING)
//                        .setTimestamp(System.currentTimeMillis())
//        );
//        Utils.p("Sent error event from url: "+request.getRequest().getUrl());
    }
}
