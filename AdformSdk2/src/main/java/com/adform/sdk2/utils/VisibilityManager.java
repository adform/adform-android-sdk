package com.adform.sdk2.utils;

import android.content.Context;
import android.graphics.Point;
import android.view.*;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ScrollView;

/**
 * Created by mariusm on 12/05/14.
 * A manager that handles various events that shows if view is visible
 */
public class VisibilityManager implements ViewTreeObserver.OnScrollChangedListener, AbsListView.OnScrollListener {

    public static final int VISIBILITY_CHECK_DELAY = 400;
    private final VisibilityManagerListener mVisibilityManagerListener;
    private final int mScreenWidth;
    private final int mScreenHeight;
    private boolean isVisible = false;

    public interface VisibilityManagerListener {
        // Function list that the standart view should override
        public boolean postDelayed(Runnable runnable, long delay);
        public boolean removeCallbacks(Runnable action);
        public void getLocationInWindow(int[] location);
        public int getHeight();
        public int getWidth();
        public ViewParent getParent();
        // Callback functions
        public View getView();
        public void onVisibilityUpdate(boolean visibility);
    }
    private Runnable mVisibilityRunnable;
    private Runnable parentGetterRunnable;
    private ViewParent mViewParent;

    public VisibilityManager(Context context, View view) {
        if (view == null)
            throw new IllegalArgumentException("VisibilityManager cannot be initialized without a view");
        if (view instanceof VisibilityManagerListener) {
            mVisibilityManagerListener = (VisibilityManagerListener) view;
        } else
            throw new IllegalArgumentException("Provided view must implement VisibilityManagerListener");
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

    /**
     * A call from outside to check if view is still in the window.
     * This is called when content in the view has changed, orientation changed
     * or something else has occured.
     */
    public void checkVisibilityService() {
        lookParentWhenInflated();
        if (mVisibilityRunnable != null)
            return;
        mVisibilityRunnable = new Runnable() {
            @Override
            public void run() {
                isVisible = isViewVisible();
                mVisibilityManagerListener.onVisibilityUpdate(isVisible);
                mVisibilityRunnable = null;
            }
        };
        mVisibilityManagerListener.postDelayed(mVisibilityRunnable, VISIBILITY_CHECK_DELAY);
    }

    /**
     * @return true if a view is in the window.
     */
    private boolean isViewVisible() {
        int[] location = new int[2];
        mVisibilityManagerListener.getLocationInWindow(location);
        boolean isVisible = true;
        if (location[0] < 0)
            isVisible = false;
        else if (location[1] < 0)
            isVisible = false;
        else if ((location[0] + mVisibilityManagerListener.getWidth()) > mScreenWidth)
            isVisible = false;
        else if ((location[1] + mVisibilityManagerListener.getHeight()) > mScreenHeight)
            isVisible = false;
        return isVisible;
    }

    /**
     * Checks when parent view is inflated.
     * Then we can state, that layouts have been deployed.
     * When we have a parent view, we are looking for ScrollView's
     * that can be hooked to our listeners.
     * @see #hookScrollViewListeners(android.view.View)
     */
    public void lookParentWhenInflated() {
        if (mViewParent != null) {
            return;
        }
        if (parentGetterRunnable == null)
            parentGetterRunnable = new Runnable() {
                @Override
                public void run() {
                    mViewParent = mVisibilityManagerListener.getParent();
                    if (mViewParent != null)
                        hookScrollViewListeners((View)mViewParent);
                }
            };
        else
            mVisibilityManagerListener.removeCallbacks(parentGetterRunnable);
        mVisibilityManagerListener.postDelayed(parentGetterRunnable, 500);
    }

    /**
     * A recursive function to hook any scrollable Views to listen
     * when content has scrolled.
     * @param view view that is checked if its a ScrollView
     */
    private void hookScrollViewListeners(View view) {
        if (view == null)
            return;
        // Check if this is not a DecorView
        // This should be nicer, as there is no way to check its instance
        if (view.getClass().getName()
                .equals("com.android.internal.policy.impl.PhoneWindow$DecorView"))
            return;
        if (view instanceof ScrollView && view.getViewTreeObserver() != null)
            view.getViewTreeObserver().addOnScrollChangedListener(this);
        if (view instanceof ListView)
            ((ListView) view).setOnScrollListener(this);
        if (view instanceof ViewGroup) {
            ViewParent viewParent = view.getParent();
            if (viewParent != null)
                hookScrollViewListeners((View)viewParent);
        }
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {}

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        checkVisibilityService();
    }


    @Override
    public void onScrollChanged() {
        checkVisibilityService();
    }
}
