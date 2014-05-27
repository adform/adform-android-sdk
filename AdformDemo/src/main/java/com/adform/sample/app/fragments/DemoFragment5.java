package com.adform.sample.app.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.adform.sample.app.R;
import com.adform.sdk2.Constants;
import com.adform.sdk2.activities.AdformInterstitialActivity;
import com.adform.sdk2.network.app.RawNetworkTask;
import com.adform.sdk2.network.app.entities.entities.RawResponse;
import com.adform.sdk2.network.base.ito.network.*;
import com.adform.sdk2.utils.AdformContentLoadManager;

/**
 * Created by mariusm on 13/05/14.
 */
public class DemoFragment5 extends Fragment implements View.OnClickListener,
        AdformContentLoadManager.ContentLoaderListener {

    public static final String CONTENT_LOADER_INFO = "CONTENT_LOADER_INFO";
    private boolean showAfterLoad = false;
    private AdformContentLoadManager mAdformContentLoadManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdformContentLoadManager = new AdformContentLoadManager(this);
        if (savedInstanceState != null)
            mAdformContentLoadManager.restoreInstanceWithBundle(savedInstanceState.getBundle(CONTENT_LOADER_INFO));
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
                try {
                    mAdformContentLoadManager.loadContent(Constants.TEMP_INTERSTITIAL_LINK);
                } catch (AdformContentLoadManager.ContentLoadException e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                break;
            }
            case R.id.show_button: {
                showAfterLoad = true;
                if (mAdformContentLoadManager.getResponse() == null)
                    try {
                        mAdformContentLoadManager.loadContent(Constants.SERVER_URL+Constants.SDK_INFO_PATH+"banner_5.js");
                    } catch (AdformContentLoadManager.ContentLoadException e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                else {
                    showAfterLoad = false;
                    AdformInterstitialActivity.startActivity(getActivity(), mAdformContentLoadManager.getResponse());
                }
                break;
            }
        }
    }

    @Override
    public void onContentLoadSuccessful(String content) {
        showToast("Loaded basic ad");
        if (showAfterLoad) {
            showAfterLoad = false;
            AdformInterstitialActivity.startActivity(getActivity(), mAdformContentLoadManager.getResponse());
        }
    }

    @Override
    public void onContentMraidLoadSuccessful(String content) {
        showToast("Loaded MRaid ad");
        if (showAfterLoad) {
            showAfterLoad = false;
            AdformInterstitialActivity.startActivity(getActivity(), mAdformContentLoadManager.getResponse());
        }
    }

    @Override
    public void onContentLoadFailed() {
        showToast("Error loading interstitial ad");
    }

    private void showToast(String message) {
        if (getActivity() != null)
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(CONTENT_LOADER_INFO, mAdformContentLoadManager.getSaveInstanceBundle());
    }
}
