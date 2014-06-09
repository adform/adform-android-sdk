package com.adform.sdk.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import com.adform.sdk.utils.managers.SlidingManager;
import com.adform.sdk.view.base.BaseInnerContainer;

/**
 * Created by mariusm on 27/05/14.
 */
public class CoreExpandedView extends CoreInterstitialView implements SlidingManager.SliderableWidget {
    private Animation mAnimation;
    private SlidingManager mSlidingManager;

    public CoreExpandedView(Context context, BaseInnerContainer innerContainer) {
        this(context, null, 0, innerContainer);
    }

    public CoreExpandedView(Context context) {
        this(context, null, 0, null);
    }

    public CoreExpandedView(Context context, AttributeSet attrs) {
        this(context, attrs, 0, null);
    }

    public CoreExpandedView(Context context, AttributeSet attrs, int defStyle, BaseInnerContainer innerContainer) {
        super(context, attrs, defStyle, innerContainer);
        mSlidingManager = new SlidingManager(this);
        getInnerView().setBaseListener(this);
        getInnerView().getMraidBridge().setMraidListener(this);
        getInnerView().getMraidBridge().setCoreBridgeListener(this);

        View dimmingView = new View(getContext());
        dimmingView.setBackgroundColor(Color.BLACK);
        dimmingView.setAlpha(0.8f);
        dimmingView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        addView(dimmingView, 0);
        getInnerView().setVisibility(View.INVISIBLE);
        getInnerView().setCloseButtonEnabled(true);
        getInnerView().onUseCustomClose(true);
    }

    @Override
    public void onContentRender() {
        super.onContentRender();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mSlidingManager.turnOn();
    }

    @Override
    public void onSliderAnimating(final Animation animation) {
        post(new Runnable() {
            @Override
            public void run() {
                getInnerView().clearAnimation();
                getInnerView().startAnimation(animation);
            }
        });
    }

    public void slideOut() {
        mSlidingManager.turnOn();
    }

    @Override
    public int getHiddenState() {
        return View.INVISIBLE;
    }
}
