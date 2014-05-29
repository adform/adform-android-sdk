package com.adform.sdk2.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.adform.sdk2.mraid.properties.MraidDeviceIdProperty;
import com.adform.sdk2.resources.AdDimension;
import com.adform.sdk2.resources.CloseImageView;
import com.adform.sdk2.utils.AdformEnum;
import com.adform.sdk2.utils.Utils;
import com.adform.sdk2.view.base.BaseCoreContainer;
import com.adform.sdk2.view.inner.InnerInterstitialView;

/**
 * Created by mariusm on 27/05/14.
 */
public class CoreInterstitialView extends BaseCoreContainer implements View.OnClickListener,
        InnerInterstitialView.InnerInterstitialListener {

    public interface CoreInterstitialListener {
        public void onAdClose();
    }

    private InnerInterstitialView mInterstitialView;
    private CoreInterstitialListener mListener;

    public CoreInterstitialView(Context context) {
        this(context, null);
    }

    public CoreInterstitialView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CoreInterstitialView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (mContext instanceof CoreInterstitialListener)
            mListener = (CoreInterstitialListener)mContext;
        setAnimating(false);
        setVisibility(View.VISIBLE);
        CloseImageView imageView = new CloseImageView(mContext);
        final RelativeLayout.LayoutParams closeImageViewParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        closeImageViewParams.addRule(ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        closeImageViewParams.addRule(ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        imageView.setOnClickListener(this);
        addView(imageView, closeImageViewParams);
        imageView.bringToFront();
    }

    public void showContent(String content, boolean isMraid) {
        // Loaded content will always be loaded and mraid type
        setViewState(AdformEnum.VisibilityGeneralState.LOAD_SUCCESSFUL);
        setContentMraid(isMraid);
        mInterstitialView.showContent(content, isMraid);
    }

    @Override
    protected View initInnerView() {
        mInterstitialView = new InnerInterstitialView(mContext);
        mInterstitialView.setListener(this);
        return mInterstitialView;
    }

    @Override
    protected AdDimension initAdDimen() {
        return new AdDimension(mContext);
    }

    @Override
    protected void startService() {}

    @Override
    protected void stopService() {}

    @Override
    protected void resumeService() {}

    @Override
    protected void onVisibilityCallback(boolean isVisible) {
        mInterstitialView.changeVisibility(isVisible);
    }

    @Override
    public MraidDeviceIdProperty getDeviceId() {
        return null;
    }

    @Override
    public String getUserAgent() {
        return mInterstitialView.getUserAgent();
    }

    @Override
    public void onContentRestore(boolean state) {

    }

    @Override
    public void onContentRender() {
        Utils.p("On content render");
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            mListener.onAdClose();
        }
    }

    public void setListener(CoreInterstitialListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onAdClose() {
        if (mListener != null)
            mListener.onAdClose();
    }
}
