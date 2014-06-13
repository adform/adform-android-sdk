package com.adform.sdk.utils.managers;

import android.view.View;
import android.view.animation.Animation;
import com.adform.sdk.utils.Utils;
import com.adform.sdk.view.base.BaseCoreContainer;

/**
 * Created by mariusm on 28/04/14.
 * Manager that helps to handle animation showing.
 * Callback for view control is provided with {@link AdformAnimationManager.SliderableWidgetCallbacks}
 */
public class AdformAnimationManager {

    /**
     * An interface that bridges some needed states,
     * and callbacks functions that should be executed
     */
    public interface SliderableWidgetProperties {
        /**
         * Function is called, before animation starts, when turning on banner.
         * This is a small fix, when there is a flicker.
         */
//        public void onSliderPreOn();

        /** Gets height that animation should slide to */
        public void setAnimating(boolean isAnimating);
        public BaseCoreContainer getView();
    }

    public interface SliderableWidgetCallbacks {
        public void onSliderStartedShowing();
        public void onSliderFinishedShowing();
        public void onSliderStartedHiding();
        public void onSliderFinishedHiding();
    }

    public interface SlidingAnimationProperties {
        public Animation getCollapseAnimation();
        public Animation getExpandAnimation();
        public int getAnimationDuration();
        public int getAnimationDelay();
    }

    public static final int DEFAULT_DURATION = 500;
    public static final int DEFAULT_DELAY = 0;
    private boolean isOpen = false;
    private SliderableWidgetProperties mListenerProperties;
    private SliderableWidgetCallbacks mListenerCallbacks;
    private SlidingAnimationProperties mListenerAnimationProperties;
    private boolean isAnimating = false;
    private Animation mAnimation;

    public AdformAnimationManager(SliderableWidgetProperties listener, SlidingAnimationProperties listenerAnimationProperties) {
        this.mListenerProperties = listener;
        this.mListenerAnimationProperties = listenerAnimationProperties;
    }

    public void turnOffImmediate() {
        turnOff(0);
    }

    public void turnOff() {
        turnOff(DEFAULT_DURATION);
    }

    /**
     * Collapses slider down
     */
    public void turnOff(int hideSpeed) {
        if (!isOpen)
            return;
        if (mAnimation != null)
            mAnimation.cancel();
//        mAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f, mListenerProperties.getHeight());
//        mAnimation.setDuration(hideSpeed);
        mAnimation = mListenerAnimationProperties.getCollapseAnimation();
        mAnimation.setDuration(mListenerAnimationProperties.getAnimationDuration());
        mAnimation.setStartOffset(mListenerAnimationProperties.getAnimationDelay());
        mAnimation.setAnimationListener(collapseListener);
        mListenerProperties.getView().post(preHideRunnable);
        mListenerProperties.getView().post(animationRunnable);
    }

    public void turnOnImmediate() {
        turnOn(0);
    }

    public void turnOn() {
        Utils.p("turning on");
        turnOn(DEFAULT_DURATION);
    }

    /**
     * Expands slider back up
     */
    public void turnOn(int showSpeed) {
        if (isOpen)
            return;
        if (mAnimation != null)
            mAnimation.cancel();
//        mAnimation = new TranslateAnimation(0.0f, 0.0f, mListenerProperties.getHeight(), 0.0f);
//        mAnimation.setDuration(showSpeed);
        mAnimation = mListenerAnimationProperties.getExpandAnimation();
        mAnimation.setDuration(mListenerAnimationProperties.getAnimationDuration());
        mAnimation.setStartOffset(mListenerAnimationProperties.getAnimationDelay());
        mAnimation.setAnimationListener(expandListener);
        mListenerProperties.getView().post(preShowRunnable);
        mListenerProperties.getView().post(animationRunnable);
    }

    // ---------
    // Runnables
    // ---------
    private Runnable preShowRunnable = new Runnable() {
        @Override
        public void run() {
            if (mListenerProperties == null)
                return;
            mListenerProperties.getView().setVisibility(View.VISIBLE);
            mListenerProperties.getView().getInnerView().setVisibility(View.INVISIBLE);
        }
    };
    private Runnable preHideRunnable = new Runnable() {
        @Override
        public void run() {
            if (mListenerProperties == null)
                return;
            mListenerProperties.getView().setVisibility(View.VISIBLE);
            mListenerProperties.getView().getInnerView().setVisibility(View.VISIBLE);
        }
    };

    private Runnable animationRunnable = new Runnable() {
        @Override
        public void run() {
            if (mListenerProperties == null)
                return;
            mListenerProperties.getView().getInnerView().clearAnimation();
            mListenerProperties.getView().getInnerView().startAnimation(mAnimation);
        }
    };

    // -------------------
    // Animation callbacks
    // -------------------
    private Animation.AnimationListener collapseListener = new Animation.AnimationListener() {
        public void onAnimationRepeat(Animation animation) {}

        public void onAnimationStart(Animation animation) {
            isAnimating = true;
            mListenerProperties.setAnimating((animation.getDuration() != 0));
            if (mListenerCallbacks != null)
                mListenerCallbacks.onSliderStartedHiding();
        }

        public void onAnimationEnd(Animation animation) {
            isOpen = false;
            isAnimating = false;
            mListenerProperties.setAnimating(false);
            if (mListenerCallbacks != null)
                mListenerCallbacks.onSliderFinishedHiding();
        }
    };

    private Animation.AnimationListener expandListener = new Animation.AnimationListener() {
        public void onAnimationRepeat(Animation animation) {}

        public void onAnimationStart(Animation animation) {
            isAnimating = true;
            mListenerProperties.setAnimating((animation.getDuration() != 0));
            if (mListenerCallbacks != null)
                mListenerCallbacks.onSliderStartedShowing();
        }

        public void onAnimationEnd(Animation animation) {
            isOpen = true;
            isAnimating = false;
            mListenerProperties.setAnimating(false);
            if (mListenerCallbacks != null)
                mListenerCallbacks.onSliderFinishedShowing();
        }
    };

    public boolean isAnimating() {
        return isAnimating;
    }

    public void setListenerCallbacks(SliderableWidgetCallbacks listenerCallbacks) {
        this.mListenerCallbacks = listenerCallbacks;
    }

    public void setOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

    public void destroy() {
        mListenerProperties.getView().removeCallbacks(preShowRunnable);
        mListenerProperties.getView().removeCallbacks(preHideRunnable);
        mListenerProperties.getView().removeCallbacks(animationRunnable);
        mListenerAnimationProperties = null;
        mListenerCallbacks = null;
        mListenerProperties = null;
    }
}
