package com.adform.sdk.utils.managers;

import android.content.Context;
import android.graphics.Point;
import android.view.*;
import android.widget.ListView;
import android.widget.ScrollView;
import com.adform.sdk.utils.entities.ViewCoords;
import com.adform.sdk.view.base.BaseCoreContainer;

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
        public BaseCoreContainer getView();
        public Context getContext();
        // Callback functions
        public void onVisibilityManagerUpdate(boolean visibility);
    }

    public interface PositionManagerListener {
        public void onDefaultPositionUpdate(ViewCoords viewCoords, boolean forceUpdate);
        public void onCurrentPositionUpdate(ViewCoords viewCoords, boolean forceUpdate);
        public void onMaxSizeUpdate(ViewCoords viewCoords, boolean forceUpdate);
        public void onScreenSizeUpdate(ViewCoords viewCoords, boolean forceUpdate);
    }

    private Context mContext;
    private Runnable mVisibilityRunnable;
    private Runnable parentGetterRunnable;
    private ViewParent mViewParent;
    private ArrayList<ViewCoords> mParentCoords;
    private ViewCoords mDefaultPosition, mCurrentPosition, mMaxSize, mScreenSize;
    private ViewCoords mGapBetweenScreenSizeMaxSize;
    private View mLastContainer;
    private VisibilityManagerListener mVisibilityManagerListener;
    private PositionManagerListener mPositionManagerListener;
    private boolean isVisible = false;
    /** This can go from 0 to 1.
     * It reacts when visibility changes depending on the percent of the view displayed
     * */
    private double mVisibilityKoef = 0.5;

    public VisibilityPositionManager(Context context,
                                     VisibilityManagerListener visibilityManager,
                                     PositionManagerListener positionManager) {
        if (visibilityManager == null)
            throw new IllegalArgumentException("When creating VisibilityManagerListener must be implemented");
        mContext = context;
        mVisibilityManagerListener = visibilityManager;
        mPositionManagerListener = positionManager;
        initScreenSize();
        mParentCoords = new ArrayList<ViewCoords>();
    }

    private void initScreenSize() {
        // Getting screen dimensions
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
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
        screenCoords.setHeight(screenCoords.getHeight());
        setScreenSize(screenCoords);
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
                if (mVisibilityManagerListener == null)
                    return;
                if (mPositionManagerListener != null && mGapBetweenScreenSizeMaxSize != null) {
                    setCurrentPosition(ViewCoords.createViewCoordSubrtactModifier(
                            mVisibilityManagerListener.getView(), mGapBetweenScreenSizeMaxSize));
                    if ((mDefaultPosition != null && mDefaultPosition.isZero()) || mDefaultPosition == null) {
                        setDefaultPosition(ViewCoords.createViewCoord(mCurrentPosition));
                    }
                }
                isVisible = isViewVisibleInPresetContainers();
                mVisibilityManagerListener.onVisibilityManagerUpdate(isVisible);
                mVisibilityRunnable = null;
            }
        };
        mVisibilityManagerListener.postDelayed(mVisibilityRunnable, VISIBILITY_CHECK_DELAY);
    }

    /**
     * Runs the checks through all the containers if something has changed.
     * @see #isViewVisible(ViewCoords, ViewCoords)
     * @return false if main view is not visible.
     */
    private boolean isViewVisibleInPresetContainers() {
        if(mVisibilityManagerListener == null)
            return false;
        ViewCoords mainViewCoords = ViewCoords.createViewCoord(mVisibilityManagerListener.getView());
        for (ViewCoords mParentCoord : mParentCoords) {
            if (!isViewVisible(mainViewCoords, mParentCoord))
                return false;
        }
        return true;
    }
    /**
     * @return true if a view is in the window.
     */
    private boolean isViewVisible(ViewCoords mainView, ViewCoords viewCoords) {
        boolean isVisible = true;
        if (mainView.getX() < viewCoords.getX() - (mainView.getWidth() * mVisibilityKoef))
            isVisible = false;
        else if (mainView.getY() < viewCoords.getY() - (mainView.getHeight() * mVisibilityKoef))
            isVisible = false;
        else if (mainView.getX() + mainView.getWidth() >
                (viewCoords.getX() + viewCoords.getWidth() + (mainView.getWidth() * mVisibilityKoef)))
            isVisible = false;
        else if (mainView.getY() + mainView.getHeight() >
                (viewCoords.getY() + viewCoords.getHeight() + (mainView.getHeight() * mVisibilityKoef)))
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
                    if(mVisibilityManagerListener == null)
                        return;
                    mViewParent = mVisibilityManagerListener.getParent();
                    if (mViewParent != null) {
                        hookScrollViewListeners((View) mViewParent);
                    }
                }
            };
        else
            mVisibilityManagerListener.removeCallbacks(parentGetterRunnable);
        mVisibilityManagerListener.postDelayed(parentGetterRunnable, 100);
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
     * @see VisibilityPositionManager.PositionManagerListener
     * @param currentPosition provided new position
     */
    private void setCurrentPosition(ViewCoords currentPosition) {
        if (currentPosition == null || mPositionManagerListener == null)
            return;
        this.mCurrentPosition = currentPosition;
        mPositionManagerListener.onCurrentPositionUpdate(mCurrentPosition, false);
    }

    private void setDefaultPosition(ViewCoords defaultPosition) {
        if (mPositionManagerListener == null)
            return;
        this.mDefaultPosition = defaultPosition;
        mPositionManagerListener.onDefaultPositionUpdate(mDefaultPosition, false);
    }

    private void setMaxSize(ViewCoords maxSize) {
        if (mPositionManagerListener == null)
            return;
        this.mMaxSize = maxSize;
        mGapBetweenScreenSizeMaxSize = ViewCoords.createViewCoordFromGapBetweenCoords(
                mScreenSize, mMaxSize
        );
        mPositionManagerListener.onMaxSizeUpdate(mMaxSize, false);
    }

    private void setScreenSize(ViewCoords screenSize) {
        if (mPositionManagerListener == null)
            return;
        this.mScreenSize = screenSize;
        mPositionManagerListener.onScreenSizeUpdate(mScreenSize, false);
    }

    public void setPositionManagerListener(PositionManagerListener positionManagerListener) {
        this.mPositionManagerListener = positionManagerListener;
    }

    public void destroy() {
        mVisibilityManagerListener.removeCallbacks(parentGetterRunnable);
        mVisibilityManagerListener = null;
        mPositionManagerListener = null;
    }

}
