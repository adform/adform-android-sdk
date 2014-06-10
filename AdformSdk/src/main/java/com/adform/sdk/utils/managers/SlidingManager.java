package com.adform.sdk.utils.managers;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import com.adform.sdk.view.base.BaseCoreContainer;

/**
 * Created by mariusm on 28/04/14.
 * Manager that helps to handle animation showing.
 * Callback for view control is provided with {@link SlidingManager.SliderableWidget}
 */
public class SlidingManager {

    /**
     * An interface that bridges some needed states,
     * and callbacks functions that should be executed
     */
    public interface SliderableWidget {
        /**
         * Function is called, before animation starts, when turning on banner.
         * This is a small fix, when there is a flicker.
         */
//        public void onSliderPreOn();

        /** Gets height that animation should slide to */
        public int getHeight();
        public void setAnimating(boolean isAnimating);
        public BaseCoreContainer getView();
        public void onSliderStartedShowing();
        public void onSliderFinishedShowing();
        public void onSliderStartedHiding();
        public void onSliderFinishedHiding();

    }

    private static final int SHOW_SPEED = 500;
    private static final int HIDE_SPEED = 500;
//    private static final int SHOW_DELAY = 50;
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
        mListener.getView().post(preHideRunnable);
        mListener.getView().post(animationRunnable);
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
        mListener.getView().post(preShowRunnable);
        mListener.getView().post(animationRunnable);
    }

    // ---------
    // Runnables
    // ---------
    private Runnable preShowRunnable = new Runnable() {
        @Override
        public void run() {
            mListener.getView().setVisibility(View.VISIBLE);
            mListener.getView().getInnerView().setVisibility(View.INVISIBLE);
        }
    };
    private Runnable preHideRunnable = new Runnable() {
        @Override
        public void run() {
            mListener.getView().setVisibility(View.VISIBLE);
            mListener.getView().getInnerView().setVisibility(View.VISIBLE);
        }
    };
    private Runnable animationRunnable = new Runnable() {
        @Override
        public void run() {
            mListener.getView().getInnerView().clearAnimation();
            mListener.getView().getInnerView().startAnimation(mAnimation);
        }
    };

    // -------------------
    // Animation callbacks
    // -------------------
    private Animation.AnimationListener collapseListener = new Animation.AnimationListener() {
        public void onAnimationRepeat(Animation animation) {}

        public void onAnimationStart(Animation animation) {
            isAnimating = true;
            mListener.setAnimating((animation.getDuration() != 0));
            mListener.onSliderStartedHiding();
        }

        public void onAnimationEnd(Animation animation) {
            isOpen = false;
            isAnimating = false;
            mListener.setAnimating(false);
            mListener.onSliderFinishedHiding();
        }
    };

    private Animation.AnimationListener expandListener = new Animation.AnimationListener() {
        public void onAnimationRepeat(Animation animation) {}

        public void onAnimationStart(Animation animation) {
            mListener.getView().getInnerView().setVisibility(View.VISIBLE);
            isAnimating = true;
            mListener.setAnimating((animation.getDuration() != 0));
            mListener.onSliderStartedShowing();
        }

        public void onAnimationEnd(Animation animation) {
            isOpen = true;
            isAnimating = false;
            mListener.setAnimating(false);
            mListener.onSliderFinishedShowing();
        }
    };

    public boolean isAnimating() {
        return isAnimating;
    }
}
