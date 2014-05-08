package com.adform.sdk2.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * Created by mariusm on 28/04/14.
 * Manager that helps to handle animation showing.
 * Callback for view control is provided with {@link com.adform.sdk2.utils.SlidingManager.SliderableWidget}
 */
public class SlidingManager {

    /**
     * An interface that bridges some needed states,
     * and callbacks functions that should be executed
     */
    public interface SliderableWidget {
        /**
         * A callback when visibility should change with provided visibility
         * @param visibility provided visibility
         */
        public void onSliderVisibilityChange(int visibility);

        /**
         * A callback when animation should occur.
         * @param animation prided animation
         */
        public void onSliderAnimating(Animation animation);
        /** Gets height that animation should slide to */
        public int getHeight();
        /** Gets state that should be set when hiding slider */
        public int getHiddenState();
    }

    private static final int SHOW_SPEED = 500;
    private static final int HIDE_SPEED = 500;
    private static final int SHOW_DELAY = 0;
    private boolean isOpen = false;
    private SliderableWidget mListener;
    private boolean isAnimating = false;
    private Animation mAnimation;

    public SlidingManager(SliderableWidget listener) {
        this.mListener = listener;
    }

    public void turnOffImmediate() {
        turnOff(0);
    }
    public void turnOff() {
        turnOff(HIDE_SPEED);
    }

    /**
     * Collapses slider down
     */
    public void turnOff(int hideSpeed) {
        if (!isOpen)
            return;
        if (mAnimation != null)
            mAnimation.cancel();
        mAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f, mListener.getHeight());
        mAnimation.setDuration(hideSpeed);
        mAnimation.setAnimationListener(collapseListener);
        mListener.onSliderAnimating(mAnimation);
    }

    public void turnOnImmediate() {
        turnOn(0);
    }

    public void turnOn() {
        turnOn(SHOW_SPEED);
    }

    /**
     * Expands slider back up
     */
    public void turnOn(int showSpeed) {
        if (isOpen)
            return;
        if (mAnimation != null)
            mAnimation.cancel();
        mAnimation = new TranslateAnimation(0.0f, 0.0f, mListener.getHeight(), 0.0f);
        mAnimation.setDuration(showSpeed);
        mAnimation.setAnimationListener(expandListener);
        mListener.onSliderVisibilityChange(View.VISIBLE);
        mListener.onSliderAnimating(mAnimation);
    }

    private Animation.AnimationListener collapseListener = new Animation.AnimationListener() {
        public void onAnimationRepeat(Animation animation) {}

        public void onAnimationStart(Animation animation) {
            mListener.onSliderVisibilityChange(View.VISIBLE);
            isAnimating = true;
        }

        public void onAnimationEnd(Animation animation) {
            mListener.onSliderVisibilityChange(mListener.getHiddenState());
            isOpen = false;
            isAnimating = false;
        }
    };

    private Animation.AnimationListener expandListener = new Animation.AnimationListener() {
        public void onAnimationRepeat(Animation animation) {}

        public void onAnimationStart(Animation animation) {
            mListener.onSliderVisibilityChange(View.VISIBLE);
            isAnimating = true;
        }

        public void onAnimationEnd(Animation animation) {
            mListener.onSliderVisibilityChange(View.VISIBLE);
            isOpen = true;
            isAnimating = false;
        }
    };
}
