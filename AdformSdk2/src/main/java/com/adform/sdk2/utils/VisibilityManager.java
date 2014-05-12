package com.adform.sdk2.utils;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by mariusm on 12/05/14.
 * A manager that handles various events that shows if view is visible
 */
public class VisibilityManager {

    private final VisibilityManagerListener mVisibilityManagerListener;
    private final int mScreenWidth;
    private final int mScreenHeight;
    private boolean isVisible = false;

    public interface VisibilityManagerListener {
        /**
         * An executable that should be overriden in a view.
         * @see #postDelayed(Runnable, long)
         */
        public boolean postDelayed(Runnable runnable, long delay);
        public boolean removeCallbacks(Runnable action);
        public void getLocationInWindow(int[] location);
        public int getHeight();
        public int getWidth();
        public void onVisibilityUpdate(boolean visibility);
    }
    private Runnable visibilityRunnable;

    public VisibilityManager(Context context, VisibilityManagerListener l) {
        mVisibilityManagerListener = l;

        // Getting screen dimensions
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        if (android.os.Build.VERSION.SDK_INT >= 13) {
            Point size = new Point();
            display.getSize(size);
            mScreenWidth = size.x;
            mScreenHeight = size.y;
        } else {
            mScreenWidth = display.getWidth();
            mScreenHeight = display.getHeight();
        }
    }

    public void checkVisibility() {
        if (visibilityRunnable == null)
            visibilityRunnable = new Runnable() {
                @Override
                public void run() {
                    int[] location = new int[2];
                    mVisibilityManagerListener.getLocationInWindow(location);
                    isVisible = true;
                    if (location[0] < 0)
                        isVisible = false;
                    else if (location[1] < 0)
                        isVisible = false;
                    else if ((location[0] + mVisibilityManagerListener.getWidth()) > mScreenWidth)
                        isVisible = false;
                    else if ((location[1] + mVisibilityManagerListener.getHeight()) > mScreenHeight)
                        isVisible = false;
                    mVisibilityManagerListener.onVisibilityUpdate(isVisible);
                }
            };
        else
            mVisibilityManagerListener.removeCallbacks(visibilityRunnable);
        mVisibilityManagerListener.postDelayed(visibilityRunnable, 500);
    }

    private class ViewDimensions {
        public int x, y, width, height;

        private ViewDimensions(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }
}
