package com.adform.sample.app.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.adform.sample.app.R;
import com.adform.sdk.activities.AdformInterstitialActivity;
import com.adform.sdk.utils.AdformContentLoadManager;
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
    private DummyView mDummyView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDummyView = new DummyView(getActivity());
        mDummyView.setMasterId(222222);
        mDummyView.setPublisherId(666666);

        // Initializing loading manager
        mAdformContentLoadManager = new AdformContentLoadManager();
        if (savedInstanceState != null) {
            // If there is info that should be restored (like loaded content that can be reused) to load manager,
            // restoreInstanceWithBundle exactly does that
            mAdformContentLoadManager.restoreInstanceWithBundle(savedInstanceState.getBundle(CONTENT_LOADER_INFO));
        }
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
                    AdformInterstitialActivity.startActivity(getActivity(),
                            mAdformContentLoadManager.getLastRawResponse(),
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
                mAdformContentLoadManager.loadContent(
                        mAdformContentLoadManager.getContractTask(
                                mDummyView.getUrlProperties(),
                                mDummyView.getRequestProperties())
                );
                mAdformContentLoadManager.setListener(new AdformContentLoadManager.ContentLoaderListener() {
                    @Override
                    public void onContentMraidLoadSuccessful(String content) {
                        try {
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Saving instance of the content loader
        if (mAdformContentLoadManager != null)
            outState.putBundle(CONTENT_LOADER_INFO, mAdformContentLoadManager.getSaveInstanceBundle());
    }
}
