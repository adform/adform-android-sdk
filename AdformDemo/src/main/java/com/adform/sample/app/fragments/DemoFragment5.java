package com.adform.sample.app.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.adform.sample.app.R;
import com.adform.sdk.activities.AdformInterstitialActivity;
import com.adform.sdk.utils.managers.AdformContentLoadManager;
import com.adform.sdk.view.DummyView;

/**
 * Created by mariusm on 13/05/14.
 */
public class DemoFragment5 extends Fragment implements View.OnClickListener,
        AdformContentLoadManager.ContentLoaderListener {

    // A variable that contains a key for persistent loaded ad saving
    public static final String CONTENT_LOADER_INFO = "CONTENT_LOADER_INFO";
    // A flag that handles when the ad should be shown
    private boolean showAfterLoad = false;
    // Manager that handles network loading tasks
    private AdformContentLoadManager mAdformContentLoadManager;
    // Dummy view is used to collect all the paramters that are needed for the request
    private DummyView mDummyView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // We just initialize view for it to collect all the required info
        mDummyView = new DummyView(getActivity());
        // Adding custom data
        mDummyView.setMasterId(222222);
        mDummyView.setPublisherId(666666);

        // Initializing loading manager
        mAdformContentLoadManager = new AdformContentLoadManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main4, null);
        // Initializing buttons, that provide loading and showing functionality
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
                // Loading information from the network with the provided link
                if (!mAdformContentLoadManager.isLoading())
                    loadContract();
                break;
            }
            case R.id.show_button: {
                // Showing information that was loaded
                showAfterLoad = true;
                if (mAdformContentLoadManager.getLastRawResponse() == null)
                    loadContract();
                else {
                    showAfterLoad = false;
                    // Opening interstitial window with content response
                    // and its impression url
                    AdformInterstitialActivity.startActivity(getActivity(),
                            mAdformContentLoadManager.getLastRawResponse(),
                            mAdformContentLoadManager.getLastAdServingResponse().getAdEntity().getTrackingUrlBase()+
                            mAdformContentLoadManager.getLastAdServingResponse()
                                    .getAdEntity().getTagDataEntity().getImpressionUrl()
                    );
                }
                break;
            }
        }
    }

    private void loadContract() {
        if (!mAdformContentLoadManager.isLoading()) {
            try {
                // First we need to load a contract for the ad
                // We use additional parameters from the dummy view.
                mAdformContentLoadManager.loadContent(
                        mAdformContentLoadManager.getContractTask(
                                mDummyView.getUrlProperties(),
                                mDummyView.getRequestProperties())
                );
                mAdformContentLoadManager.setListener(new AdformContentLoadManager.ContentLoaderListener() {
                    @Override
                    public void onContentMraidLoadSuccessful(String content) {
                        try {
                            // After successfully loading contract, we load the real ad content
                            mAdformContentLoadManager.loadContent(mAdformContentLoadManager.getRawGetTask(
                                    content, true
                            ));
                            mAdformContentLoadManager.setListener(DemoFragment5.this);
                        } catch (AdformContentLoadManager.ContentLoadException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onContentLoadFailed() {}
                });
            } catch (AdformContentLoadManager.ContentLoadException e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    // Response callback when loaded mraid type of content
    @Override
    public void onContentMraidLoadSuccessful(String content) {
        showToast("Loaded MRaid ad");
        if (showAfterLoad) {
            showAfterLoad = false;
            AdformInterstitialActivity.startActivity(getActivity(),
                    mAdformContentLoadManager.getLastRawResponse(),
                    mAdformContentLoadManager.getLastAdServingResponse().getAdEntity().getTrackingUrlBase()+
                    mAdformContentLoadManager.getLastAdServingResponse()
                            .getAdEntity().getTagDataEntity().getImpressionUrl());
        }
    }

    // Callback when something went wrong, and couldn't load content
    @Override
    public void onContentLoadFailed() {
        showToast("Error loading interstitial ad");
    }

    private void showToast(String message) {
        if (getActivity() != null)
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

}
