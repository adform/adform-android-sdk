package com.adform.sdk2.utils;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * Created by mariusm on 28/04/14.
 */
public class SlidingManager {
    private static final int SHOW_SPEED = 500;
    private static final int HIDE_SPEED = 200;
    private static final int SHOW_DELAY = 500;
    private boolean isOpen = false;
    private SliderableWidget mListener;
    private boolean isAnimating = false;
    private Animation mAnimation;
    private AccelerateInterpolator mAnimationInterpolator;

    public SlidingManager(SliderableWidget listener) {
        this.mListener = listener;
        mAnimationInterpolator = new AccelerateInterpolator(1.0f);
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
        anim.setInterpolator(new AccelerateInterpolator(1.0f));
        mListener.startAnimation(anim);
    }

    /**
     * Collapses slider down
     */
    public void turnOff() {
        if (mAnimation != null)
            mAnimation.cancel();
        mAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f, mListener.getWidgetHeight());
        mAnimation.setDuration(HIDE_SPEED);
        mAnimation.setInterpolator(mAnimationInterpolator);
        mAnimation.setAnimationListener(collapseListener);
        mListener.startAnimation(mAnimation);
    }

    /**
     * Expands slider back up
     */
    public void turnOn() {
        if (mAnimation != null)
            mAnimation.cancel();
        mAnimation = new TranslateAnimation(0.0f, 0.0f, mListener.getWidgetHeight(), 0.0f);
        mAnimation.setDuration(SHOW_SPEED);
        mAnimation.setStartOffset(SHOW_DELAY);
        mAnimation.setInterpolator(mAnimationInterpolator);
        mAnimation.setAnimationListener(expandListener);
        mListener.startAnimation(mAnimation);
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

    public interface SliderableWidget {
        public void setVisibility(int visibility);
        public void startAnimation(Animation animation);
        public float getWidgetHeight();
    }
}
