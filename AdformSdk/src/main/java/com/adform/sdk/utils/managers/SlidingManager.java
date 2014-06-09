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

        /**
         * A callback when animation should occur.
         * @param animation prided animation
         */
        public void onSliderAnimating(Animation animation);
        /** Gets height that animation should slide to */
        public int getHeight();
        /** Gets state that should be set when hiding slider */
        public int getHiddenState();
        public void setAnimating(boolean isAnimating);
        public BaseCoreContainer getView();
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
        mListener.getView().post(new Runnable() {
            @Override
            public void run() {
                mListener.getView().setVisibility(View.VISIBLE);
                mListener.getView().getInnerView().setVisibility(View.VISIBLE);
            }
        });
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
        mListener.getView().post(new Runnable() {
            @Override
            public void run() {
                mListener.getView().setVisibility(View.VISIBLE);
                mListener.getView().getInnerView().setVisibility(View.INVISIBLE);
            }
        });
        mListener.onSliderAnimating(mAnimation);
    }

    private Animation.AnimationListener collapseListener = new Animation.AnimationListener() {
        public void onAnimationRepeat(Animation animation) {}

        public void onAnimationStart(Animation animation) {
            isAnimating = true;
            mListener.setAnimating((animation.getDuration() != 0));
        }

        public void onAnimationEnd(Animation animation) {
            isOpen = false;
            isAnimating = false;
            mListener.setAnimating(false);
        }
    };

    private Animation.AnimationListener expandListener = new Animation.AnimationListener() {
        public void onAnimationRepeat(Animation animation) {}

        public void onAnimationStart(Animation animation) {
            mListener.getView().getInnerView().setVisibility(View.VISIBLE);
            isAnimating = true;
            mListener.setAnimating((animation.getDuration() != 0));
        }

        public void onAnimationEnd(Animation animation) {
            isOpen = true;
            isAnimating = false;
            mListener.setAnimating(false);
        }
    };
}
