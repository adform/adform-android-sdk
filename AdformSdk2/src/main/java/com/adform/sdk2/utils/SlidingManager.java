package com.adform.sdk2.utils;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * Created by mariusm on 28/04/14.
 * Manager that helps to handle animation showing.
 * Callback for view control is provided with {@link com.adform.sdk2.utils.SlidingManager.SliderableWidget}
 */
public class SlidingManager {

    public interface SliderableWidget {
        public void setVisibility(int visibility);
        public void startAnimation(Animation animation);
        public float getWidgetHeight();
    }

    private static final int SHOW_SPEED = 500;
    private static final int HIDE_SPEED = 500;
    private static final int SHOW_DELAY = 1500;
    private boolean isOpen = false;
    private SliderableWidget mListener;
    private boolean isAnimating = false;
    private Animation mAnimation;

    public SlidingManager(SliderableWidget listener) {
        this.mListener = listener;
    }

    @Deprecated
    /**
     * Toggles slider. The code is deprecated, needs inspection.
     */
    public void toggle() {
        TranslateAnimation anim = null;

        isOpen = !isOpen;

        if (isOpen) {
            mListener.setVisibility(View.VISIBLE);
            anim = new TranslateAnimation(0.0f, 0.0f, mListener.getWidgetHeight(), 0.0f);
        } else {
            anim = new TranslateAnimation(0.0f, 0.0f, 0.0f, mListener.getWidgetHeight());
            anim.setAnimationListener(collapseListener);
        }

        anim.setDuration(SHOW_SPEED);
        mListener.startAnimation(anim);
    }

    /**
     * Collapses slider down
     */
    public void turnOff() {
        if (!isOpen)
            return;
        if (mAnimation != null)
            mAnimation.cancel();
        mAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f, mListener.getWidgetHeight());
        mAnimation.setDuration(HIDE_SPEED);
        mAnimation.setAnimationListener(collapseListener);
        mListener.startAnimation(mAnimation);
    }

    /**
     * Expands slider back up
     */
    public void turnOn(int showSpeed) {
        if (isOpen)
            return;
        if (mAnimation != null)
            mAnimation.cancel();
        mAnimation = new TranslateAnimation(0.0f, 0.0f, mListener.getWidgetHeight(), 0.0f);
        mAnimation.setDuration(showSpeed);
        mAnimation.setStartOffset(SHOW_DELAY);
        mAnimation.setAnimationListener(expandListener);
        mListener.startAnimation(mAnimation);
    }

    public void turnOnImmediate() {
        turnOn(0);
    }

    public void turnOn() {
        turnOn(SHOW_SPEED);
    }

    Animation.AnimationListener collapseListener = new Animation.AnimationListener() {
        public void onAnimationEnd(Animation animation) {
            mListener.setVisibility(View.INVISIBLE);
            isOpen = false;
            isAnimating = false;
        }

        public void onAnimationRepeat(Animation animation) {}

        public void onAnimationStart(Animation animation) {
            mListener.setVisibility(View.VISIBLE);
            isAnimating = true;
        }
    };

    Animation.AnimationListener expandListener = new Animation.AnimationListener() {
        public void onAnimationEnd(Animation animation) {
            mListener.setVisibility(View.VISIBLE);
            isOpen = true;
            isAnimating = false;
        }

        public void onAnimationRepeat(Animation animation) {}

        public void onAnimationStart(Animation animation) {
            mListener.setVisibility(View.VISIBLE);
            isAnimating = true;
        }
    };
}
