package com.adform.sdk2.utils;

import android.content.Context;
import android.graphics.Point;
import android.view.*;
import android.widget.ListView;
import android.widget.ScrollView;
import com.adform.sdk2.utils.entities.ViewCoords;

import java.util.ArrayList;

/**
 * Created by mariusm on 12/05/14.
 * A manager that handles various events that shows if view is visible
 */
public class VisibilityPositionManager implements ViewTreeObserver.OnScrollChangedListener {

    public static final int VISIBILITY_CHECK_DELAY = 100;

    public interface VisibilityManagerListener {
        // Function list that the standart view should override
        public boolean postDelayed(Runnable runnable, long delay);
        public boolean removeCallbacks(Runnable action);
        public void getLocationInWindow(int[] location);
        public int getHeight();
        public int getWidth();
        public ViewParent getParent();
        public boolean isContentMraid();
        public View getView();
        public Context getContext();
        // Callback functions
        public void onVisibilityUpdate(boolean visibility);
    }

    public interface PositionManagerListener {
        public void onDefaultPositionUpdate(ViewCoords viewCoords);
        public void onCurrentPositionUpdate(ViewCoords viewCoords);
        public void onMaxSizeUpdate(ViewCoords viewCoords);
        public void onScreenSizeUpdate(ViewCoords viewCoords);
    }

    private Runnable mVisibilityRunnable;
    private Runnable parentGetterRunnable;
    private ViewParent mViewParent;
    private ArrayList<ViewCoords> mParentCoords;
    private ViewCoords mDefaultPosition, mCurrentPosition, mMaxSize, mScreenSize;
    private View mLastContainer;
    private final VisibilityManagerListener mVisibilityManagerListener;
    private PositionManagerListener mPositionManagerListener;
    private boolean isVisible = false;

    public VisibilityPositionManager(Context context, VisibilityManagerListener visibilityManagerListener) {
        this(context, visibilityManagerListener, null);
    }

    public VisibilityPositionManager(Context context,
                                     VisibilityManagerListener visibilityManager,
                                     PositionManagerListener positionManager) {
        if (visibilityManager == null)
            throw new IllegalArgumentException("When creating VisibilityManagerListener must be implemented");

        mVisibilityManagerListener = visibilityManager;
        mPositionManagerListener = positionManager;
        // Getting screen dimensions
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        ViewCoords screenCoords = ViewCoords.createViewCoord(0, 0);
        if (android.os.Build.VERSION.SDK_INT >= 13) {
            Point size = new Point();
            display.getSize(size);
            screenCoords.setWidth(size.x);
            screenCoords.setHeight(size.y);
        } else {
            screenCoords.setWidth(display.getWidth());
            screenCoords.setHeight(display.getHeight());
        }
        // Need to modify screen height, as status bar is always included in the counting
        screenCoords.setHeight(screenCoords.getHeight() + getNavigationBarHeight());
        setScreenSize(screenCoords);
        mParentCoords = new ArrayList<ViewCoords>();
    }

    /**
     * A call from outside to check if view is still in the window.
     * This is called when content in the view has changed, orientation changed
     * or something else has occured.
     */
    public void checkVisibilityService() {
        if (!mVisibilityManagerListener.isContentMraid())
            return;
        lookParentWhenInflated();
        if (mVisibilityRunnable != null)
            return;
        mVisibilityRunnable = new Runnable() {
            @Override
            public void run() {
                if (mPositionManagerListener != null) {
                    setCurrentPosition(ViewCoords.createViewCoord(mVisibilityManagerListener.getView()));
                    if ((mDefaultPosition != null && mDefaultPosition.isZero()) || mDefaultPosition == null) {
                        setDefaultPosition(ViewCoords.createViewCoord(mCurrentPosition));
                    }
                }
                isVisible = isViewVisibleInPresetContainers();
                mVisibilityManagerListener.onVisibilityUpdate(isVisible);
                mVisibilityRunnable = null;
            }
        };
        mVisibilityManagerListener.postDelayed(mVisibilityRunnable, VISIBILITY_CHECK_DELAY);
    }

    /**
     * @return size in pixels of android status (notifications, clock) bar
     */
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = mVisibilityManagerListener.getContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = mVisibilityManagerListener.getContext().getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * @return size in pixels of android navigation (home, back buttons) bar
     */
    private int getNavigationBarHeight() {
        int result = 0;
        int resourceId = mVisibilityManagerListener.getContext().getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return mVisibilityManagerListener.getContext().getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }
    /**
     * Runs the checks through all the containers if something has changed.
     * @see #isViewVisible(int[], ViewCoords)
     * @return false if main view is not visible.
     */
    private boolean isViewVisibleInPresetContainers() {
        int[] location = new int[2];
        mVisibilityManagerListener.getLocationInWindow(location);
        for (ViewCoords mParentCoord : mParentCoords) {
            if (!isViewVisible(location, mParentCoord))
                return false;
        }
        return true;
    }
    /**
     * @return true if a view is in the window.
     */
    private boolean isViewVisible(int[] mainViewLocationXY, ViewCoords viewCoords) {
        boolean isVisible = true;
        if (mainViewLocationXY[0] < viewCoords.getX())
            isVisible = false;
        else if (mainViewLocationXY[1] < viewCoords.getY())
            isVisible = false;
        else if ((mainViewLocationXY[0] + mVisibilityManagerListener.getWidth()) > (viewCoords.getX() + viewCoords.getWidth()))
            isVisible = false;
        else if ((mainViewLocationXY[1] + mVisibilityManagerListener.getHeight()) > (viewCoords.getY() + viewCoords.getHeight()))
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
                    if (mViewParent != null) {
                        hookScrollViewListeners((View) mViewParent);
                    }
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
        String idAsString = null;
        if (view.getId() != View.NO_ID)
            idAsString = mVisibilityManagerListener.getContext().getResources().getResourceName(view.getId());
//        Utils.p("View "+view+" / "+idAsString+" / "+view.getWidth()+" / "+view.getHeight());
        if (idAsString != null && idAsString.equals("android:id/content")) {
            setMaxSize(ViewCoords.createViewCoord(view));
        }
        if (view.getClass().getName()
                .equals("com.android.internal.policy.impl.PhoneWindow$DecorView")) {
            return;
        }

        if ((view instanceof ScrollView || view instanceof ListView)
                && view.getViewTreeObserver() != null) {
            view.getViewTreeObserver().addOnScrollChangedListener(this);
            mParentCoords.add(ViewCoords.createViewCoord(view));
        }
        if (view instanceof ViewGroup) {
            ViewParent viewParent = view.getParent();
            if (viewParent != null) {
                hookScrollViewListeners((View) viewParent);
            }
        }
    }

    @Override
    public void onScrollChanged() {
        checkVisibilityService();
    }

    /**
     * Sets current position. This also checks if there are any changes to the old position.
     * Notifies listener to invoke #onCurrentPositionUpdate(ViewCoords)
     * @see com.adform.sdk2.utils.VisibilityPositionManager.PositionManagerListener
     * @param currentPosition provided new position
     */
    private void setCurrentPosition(ViewCoords currentPosition) {
        if (currentPosition == null)
            return;
        if (mCurrentPosition == null || (mCurrentPosition != null && !currentPosition.equals(mCurrentPosition))) {
            this.mCurrentPosition = currentPosition;
            mPositionManagerListener.onCurrentPositionUpdate(mCurrentPosition);
        }
    }

    private void setDefaultPosition(ViewCoords defaultPosition) {
        this.mDefaultPosition = defaultPosition;
        mPositionManagerListener.onDefaultPositionUpdate(mDefaultPosition);
    }

    private void setMaxSize(ViewCoords maxSize) {
        this.mMaxSize = maxSize;
        mPositionManagerListener.onMaxSizeUpdate(mMaxSize);
    }

    private void setScreenSize(ViewCoords screenSize) {
        this.mScreenSize = screenSize;
        if (mPositionManagerListener != null)
            mPositionManagerListener.onScreenSizeUpdate(mScreenSize);
    }

    public void setPositionManagerListener(PositionManagerListener positionManagerListener) {
        this.mPositionManagerListener = positionManagerListener;
    }
}
