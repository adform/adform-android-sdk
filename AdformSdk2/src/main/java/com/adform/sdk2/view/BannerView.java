package com.adform.sdk2.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.*;
import com.adform.sdk2.utils.Utils;

import java.util.ArrayList;

/**
 * Created by mariusm on 24/04/14.
 * View that loads various type of ads for a small banner. Ads that are loaded in a circle,
 * are displayed with flip animation. View provides callbacks through {@link com.adform.sdk2.view.BaseAdContainer.BaseAdViewListener}
 */
public class BannerView extends BaseAdContainer {
    public static final int FLIP_SPEED = 500;
    public static final int FLIP_OFFSET = 0;
    public static final int VIEW_COUNT = 2;

    private ViewAnimator mViewAnimator;
    private ArrayList<AdWebView> mWebViews;

    public BannerView(Context context) {
        this(context, null);
    }

    public BannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private Runnable mFlipContentRunnable = new Runnable() {
        @Override
        public void run() {
            if (mViewAnimator != null)
                mViewAnimator.showNext();
        }
    };

    @Override
    protected void animateAdShowing() {
        if (getTimesLoaded() > 0) {
            post(mFlipContentRunnable);
        }
    }

    @Override
    protected void initView() {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        setLayoutParams(new RelativeLayout.LayoutParams(
                (int) (Utils.getWidthDeviceType(mContext) * scale + 0.5f), (int) (Utils.getHeightDeviceType(mContext) * scale + 0.5f)));

        mViewAnimator = new ViewAnimator(mContext);
        mWebViews = new ArrayList<AdWebView>();
        for (int i = 1; i <= VIEW_COUNT; i++) {
            AdWebView webView = createWebView(mContext);
            final FrameLayout.LayoutParams webViewParams = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            mViewAnimator.addView(webView, webViewParams);
            mWebViews.add(webView);
        }
        final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        TranslateAnimation fadeInAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        fadeInAnimation.setDuration(FLIP_SPEED);
        fadeInAnimation.setStartOffset(FLIP_OFFSET);
        fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                getListener().setAnimating(true);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                getListener().setAnimating(false);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        TranslateAnimation fadeOutAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.0f);
        fadeOutAnimation.setDuration(FLIP_SPEED);
        fadeOutAnimation.setStartOffset(FLIP_OFFSET);
        // We need listener only on one animation, as both are animating when executing
        mViewAnimator.setInAnimation(fadeInAnimation);
        mViewAnimator.setOutAnimation(fadeOutAnimation);

        addView(mViewAnimator, params);
    }

    /**
     * Flips already loaded content. If no content exist, nothing is done.
     */
    public void flipLoadedContent() {
        if (getLoadedContent() != null)
            showContent(getLoadedContent());
    }

    @Override
    protected AdWebView getCurrentWebView() {
        return (AdWebView)mViewAnimator.getCurrentView();
    }

    @Override
    protected AdWebView getWebViewToLoadContentTo() {
        AdWebView webView;
        if (getTimesLoaded() == 0 || isRestoringContent())
            webView = (AdWebView) mViewAnimator.getCurrentView();
        else
            webView = (AdWebView) getNextView(mWebViews, mViewAnimator.getCurrentView());
        return webView;
    }

    /**
     * Get next view in the list (the one that will be shown on view flipper list).
     *
     * @param views       list of views to search in
     * @param currentView shown view
     * @return next view in the list
     */
    private View getNextView(ArrayList<? extends View> views, View currentView) {
        for (int i = 0; i < views.size(); i++) {
            if (views.get(i) == currentView)
                if ((i + 1) < views.size()) {
                    return views.get(i + 1);
                } else {
                    return views.get(0);
                }
        }
        return null;
    }
}
