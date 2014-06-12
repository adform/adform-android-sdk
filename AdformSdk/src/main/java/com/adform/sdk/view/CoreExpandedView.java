package com.adform.sdk.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import com.adform.sdk.utils.AdformEnum;
import com.adform.sdk.utils.Utils;
import com.adform.sdk.utils.managers.SlidingManager;
import com.adform.sdk.view.base.BaseInnerContainer;

/**
 * Created by mariusm on 27/05/14.
 */
public class CoreExpandedView extends CoreInterstitialView implements SlidingManager.SliderableWidgetProperties,
        SlidingManager.SliderableWidgetCallbacks {
    public static final float TO_ALPHA = 0.80f;
    public static final float FROM_ALPHA = 0.0f;
    public static final String INNER_EXTRA_WIDTH = "INNER_EXTRA_WIDTH";
    public static final String INNER_EXTRA_HEIGHT = "INNER_EXTRA_HEIGHT";
    public static final String INNER_EXTRA_USE_CUSTOM_CLOSE = "INNER_EXTRA_USE_CUSTOM_CLOSE";
    public static final String INNER_EXTRA_CONTENT = "INNER_EXTRA_CONTENT";
    public static final String INNER_EXTRA_TYPE = "INNER_EXTRA_TYPE";
    private Animation mAnimation;
    private SlidingManager mSlidingManager;
    private View mDimmingView;
    private Animation mFadeInAnimation;
    private Animation mFadeOutAnimation;
    private AdformEnum.ExpandType mExpandType = AdformEnum.ExpandType.ONE_PART;

    public CoreExpandedView(Context context, BaseInnerContainer innerContainer, Bundle extras) {
        this(context, null, 0, innerContainer, extras);
    }

    public CoreExpandedView(Context context, Bundle extras) {
        this(context, null, 0, null, extras);
    }

    public CoreExpandedView(Context context) {
        this(context, null, 0);
    }

    public CoreExpandedView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CoreExpandedView(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, 0, null, null);
    }

    public CoreExpandedView(Context context, AttributeSet attrs, int defStyle,
                            BaseInnerContainer innerContainer, Bundle extras) {
        super(context, attrs, defStyle, innerContainer, extras);
        mSlidingManager = new SlidingManager(this);
        mSlidingManager.setListenerCallbacks(this);
        getInnerView().setBaseListener(this);
        getInnerView().getMraidBridge().setMraidListener(this);
        getInnerView().getMraidBridge().setCoreBridgeListener(this);

        mDimmingView = new View(getContext());
        mDimmingView.setBackgroundColor(Color.BLACK);
        mDimmingView.setVisibility(View.INVISIBLE);
        mDimmingView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        addView(mDimmingView, 0);
        getInnerView().setVisibility(View.INVISIBLE);
        getInnerView().setCloseButtonEnabled(true);
        getInnerView().onUseCustomClose(extras.getBoolean(INNER_EXTRA_USE_CUSTOM_CLOSE, true));

        mFadeInAnimation = createAlphaAnimation(FROM_ALPHA, TO_ALPHA);
        mFadeOutAnimation = createAlphaAnimation(TO_ALPHA, FROM_ALPHA);

        String extraContent = extras.getString(INNER_EXTRA_CONTENT);
        if (extraContent != null) {
            mExpandType = AdformEnum.ExpandType.TWO_PART;
            showContent(extraContent);
        }
        mDimmingView.setVisibility(View.VISIBLE);
        mDimmingView.startAnimation(mFadeInAnimation);
    }

    @Override
    protected ViewGroup.LayoutParams getInnerViewLayoutParams() {
        if (mExtraParams != null) {
            RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(
                    mExtraParams.getInt(INNER_EXTRA_WIDTH),
                    mExtraParams.getInt(INNER_EXTRA_HEIGHT));
            relativeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            relativeLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            return relativeLayoutParams;
        }
        return super.getInnerViewLayoutParams();
    }

    @Override
    public AdformEnum.State getDefaultState() {
        return AdformEnum.State.EXPANDED;
    }

    @Override
    public AdformEnum.PlacementType getDefaultPlacementType() {
        return AdformEnum.PlacementType.INLINE;
    }

    private AlphaAnimation createAlphaAnimation(float from, float to) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(from, to);
        alphaAnimation.setFillBefore(true);
        alphaAnimation.setFillAfter(true);
        alphaAnimation.setDuration(500);
        return alphaAnimation;
    }

    @Override
    public void onContentRender() {
        super.onContentRender();
        if (mExpandType == AdformEnum.ExpandType.TWO_PART)
            mSlidingManager.turnOn();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mExpandType == AdformEnum.ExpandType.ONE_PART)
            mSlidingManager.turnOn();
    }

    @Override
    public void onSliderFinishedHiding() {
        super.onMraidClose();
    }

    @Override
    public void onSliderFinishedShowing() {
        getInnerView().setVisibility(View.VISIBLE);
    }

    @Override
    public void onSliderStartedHiding() {
        mDimmingView.setVisibility(View.VISIBLE);
        mDimmingView.startAnimation(mFadeOutAnimation);
    }

    @Override
    public void onSliderStartedShowing() {}

    @Override
    public void onMraidClose() {
        if (!mSlidingManager.isAnimating())
            mSlidingManager.turnOff();
    }

    @Override
    public void destroy() {
        if (mSlidingManager != null)
            mSlidingManager.destroy();
        mSlidingManager = null;
        super.destroy();
    }
}
