package com.adform.sample.app.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.adform.sample.app.R;
import com.adform.sample.app.adapters.TestAdapter3;
import com.adform.sdk2.Constants;
import com.adform.sdk2.activities.AdformInterstitialActivity;
import com.adform.sdk2.network.app.AdformNetworkTask;
import com.adform.sdk2.network.app.RawNetworkTask;
import com.adform.sdk2.network.app.entities.entities.AdServingEntity;
import com.adform.sdk2.network.app.entities.entities.RawResponse;
import com.adform.sdk2.network.base.ito.network.*;
import com.adform.sdk2.utils.Utils;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;

/**
 * Created by mariusm on 13/05/14.
 */
public class DemoFragment5 extends Fragment implements View.OnClickListener, SuccessListener<RawResponse>,
        ErrorListener, LoadingStateListener {
    private RawResponse mRawResponse;
    private boolean isLoading = false;
    private boolean showAfterLoad = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main4, null);
        View loadButton = view.findViewById(R.id.load_button);
        if (loadButton != null)
            loadButton.setOnClickListener(this);
        View showButton = view.findViewById(R.id.show_button);
        if (showButton != null)
            showButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.load_button: {
                runInterstitialRequest();
                break;
            }
            case R.id.show_button: {
                showAfterLoad = true;
                if (!isLoading) {
                    if (mRawResponse == null) {
                        runInterstitialRequest();
                    } else {
                        showAfterLoad = false;
                        AdformInterstitialActivity.startActivity(getActivity(), mRawResponse.getContent());
                    }
                }
                break;
            }
        }
    }

    private void runInterstitialRequest(){
        RawNetworkTask impressionTask =
                new RawNetworkTask(NetworkRequest.Method.GET,
                        Constants.SERVER_URL+Constants.SDK_INFO_PATH+"banner_5.js");
        impressionTask.setSuccessListener(this);
        impressionTask.setErrorListener(this);
        impressionTask.setLoadingStateListener(this);
        impressionTask.execute();
    }

    @Override
    public void onError(NetworkTask request, NetworkError error) {
        Toast.makeText(getActivity(), "Error loading interstitial ad", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccess(NetworkTask request, NetworkResponse<RawResponse> response) {
        mRawResponse = response.getEntity();
        Toast.makeText(getActivity(), "Ad loaded", Toast.LENGTH_SHORT).show();
        if (showAfterLoad) {
            showAfterLoad = false;
            AdformInterstitialActivity.startActivity(getActivity(), mRawResponse.getContent());
        }
    }

    @Override
    public void onStart(NetworkTask request) {
        isLoading = true;
    }

    @Override
    public void onFinnish(NetworkTask request) {
        isLoading = false;
    }
}
