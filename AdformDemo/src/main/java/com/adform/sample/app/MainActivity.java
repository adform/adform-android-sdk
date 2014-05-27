package com.adform.sample.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import com.adform.sdk2.network.base.ito.network.NetworkError;
import com.adform.sdk2.network.base.ito.network.NetworkTask;
import com.adform.sdk2.utils.Utils;
import com.adform.sdk2.view.CoreAdView;
import com.joshdholtz.sentry.Sentry;
import org.apache.http.NameValuePair;

import java.util.HashMap;

public class MainActivity extends Activity implements CoreAdView.CoreAdViewListener {

    private View mPlaceHolder;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CoreAdView mAdView = (CoreAdView) findViewById(R.id.custom_ad_view);
        mPlaceHolder = findViewById(R.id.place_holder);
        mPlaceHolder.setVisibility(mAdView.isAdVisible()?View.VISIBLE:View.GONE);
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
