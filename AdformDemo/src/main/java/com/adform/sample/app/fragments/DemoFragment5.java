package com.adform.sample.app.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.adform.sample.app.R;
import com.adform.sdk.Constants;
import com.adform.sdk.activities.AdformInterstitialActivity;
import com.adform.sdk.utils.AdformContentLoadManager;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Initializing loading manager
        mAdformContentLoadManager = new AdformContentLoadManager(this);
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
                try {
                    mAdformContentLoadManager.loadContent(Constants.TEMP_INTERSTITIAL_LINK);
                } catch (AdformContentLoadManager.ContentLoadException e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                break;
            }
            case R.id.show_button: {
                // Showing information that was loaded
                showAfterLoad = true;
                if (mAdformContentLoadManager.getResponse() == null)
                    try {
                        mAdformContentLoadManager.loadContent(Constants.TEMP_INTERSTITIAL_LINK);
                    } catch (AdformContentLoadManager.ContentLoadException e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                else {
                    showAfterLoad = false;
                    AdformInterstitialActivity.startActivity(getActivity(),
                            mAdformContentLoadManager.getResponse(), mAdformContentLoadManager.isMraid());
                }
                break;
            }
        }
    }

    // Response callback when loaded basic type of content
    @Override
    public void onContentLoadSuccessful(String content) {
        showToast("Loaded basic ad");
        if (showAfterLoad) {
            showAfterLoad = false;
            AdformInterstitialActivity.startActivity(getActivity(),
                    mAdformContentLoadManager.getResponse(), mAdformContentLoadManager.isMraid());
        }
    }

    // Response callback when loaded mraid type of content
    @Override
    public void onContentMraidLoadSuccessful(String content) {
        showToast("Loaded MRaid ad");
        if (showAfterLoad) {
            showAfterLoad = false;
            AdformInterstitialActivity.startActivity(getActivity(),
                    mAdformContentLoadManager.getResponse(), mAdformContentLoadManager.isMraid());
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
