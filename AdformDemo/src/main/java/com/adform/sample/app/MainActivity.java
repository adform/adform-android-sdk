package com.adform.sample.app;

import android.app.Activity;
import android.os.Bundle;
import com.adform.sdk2.network.app.AdformNetworkTask;
import com.adform.sdk2.network.app.entities.entities.AdServingEntity;
import com.adform.sdk2.network.ito.network.NetworkRequest;
import com.adform.sdk2.network.ito.network.NetworkResponse;
import com.adform.sdk2.network.ito.network.NetworkTask;
import com.adform.sdk2.network.ito.network.SuccessListener;
import com.adform.sdk2.utils.Utils;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        AdView mAdView = (AdView) findViewById(R.id.ad_view);
//        mAdView.loadNextAd();

        AdformNetworkTask<AdServingEntity> adServingTask = new AdformNetworkTask<AdServingEntity>(
                NetworkRequest.Method.GET, "/mobilesdk/", AdServingEntity.class, AdServingEntity.responseParser);
        adServingTask.setSuccessListener(new SuccessListener() {
            @Override
            public void onSuccess(NetworkTask request, NetworkResponse response) {
//                Utils.p("Loadded something");
            }
        });
        adServingTask.execute();

    }
}
