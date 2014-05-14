package com.adform.sdk2.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;
import com.adform.sdk2.mraid.MraidWebViewClient;
import com.adform.sdk2.mraid.properties.MraidViewableProperty;
import com.adform.sdk2.resources.MraidJavascript;
import com.adform.sdk2.mraid.MraidBridge;
import com.adform.sdk2.utils.JsLoadBridge;
import com.adform.sdk2.utils.Utils;

import java.util.ArrayList;

/**
 * Created by mariusm on 24/04/14.
 * View that loads various type of ads for a small banner. Ads that are loaded in a circle,
 * are displayed with flip animation. View provides callbacks through {@link com.adform.sdk2.view.BannerView.BannerViewListener}
 */
public class BannerView extends RelativeLayout implements MraidBridge.MraidBridgeHandler,
        JsLoadBridge.LoadBridgeHandler {
    public static final int FLIP_SPEED = 500;
    public static final int FLIP_OFFSET = 0;

    /**
     * A callback interface for main container
     */
    public interface BannerViewListener {
        /**
         * An indicator that is called when content is restored from instance
         * @param state provided restore state. If false, content is null
         */
        public void onContentRestore(boolean state);

        /**
         * An indicator that is called when content is rendered for the first time.
         * This is needed for, that first time whole container is drawn with animation,
         * later on a mViewAnimator is used for inner animations.
         */
        public void onContentRender();
    }

    private Context mContext = null;
    private String mLoadedContent;
    private boolean mIsLoadedContentMraid = false;
    /** Global variable, indicating that content is being restored. */
    private boolean mIsRestoring = false;
    private BannerViewListener mListener;

    private ViewAnimator mViewAnimator;
    private ArrayList<AdWebView> mWebViews;
    private int mTimesLoaded = 0;
    private WebViewClient mSimpleWebViewClient;
    private WebViewClient mMraidWebViewClient;
    private MraidBridge mMraidBridge;
    private JsLoadBridge mLoadBridge;
    private Object mraidJavascript = null;

    private ImageView mViewCache;
    private Canvas mCanvas;
    private Bitmap mBitmap;

    public BannerView(Context context) {
        this(context, null);
    }

    public BannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        // Compability issues
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB)
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        mViewCache = new ImageView(context);
        final float scale = mContext.getResources().getDisplayMetrics().density;
        mViewCache.setLayoutParams(new RelativeLayout.LayoutParams(
                (int) (Utils.getWidthDeviceType(mContext) * scale + 0.5f),
                (int) (Utils.getHeightDeviceType(mContext) * scale + 0.5f)));
        addView(mViewCache);
        mViewCache.setVisibility(GONE);

        setBackgroundColor(Color.TRANSPARENT);
        initView(2);

        mViewCache.bringToFront();
    }

    private Runnable mClearCacheRunnable = new Runnable() {
        @Override
        public void run() {
            mViewCache.setImageBitmap(null);
            mViewCache.setVisibility(GONE);
            //TODO mariusm 14/05/14 Bitmaps should be recycled, but it causes trouble at the moment when quickly switching view instances
//            if (mBitmap != null && !mBitmap.isRecycled())
//                mBitmap.recycle();
            mBitmap = null;
            mCanvas = null;
        }
    };

    private Runnable mFlipContentRunnable = new Runnable() {
        @Override
        public void run() {
            if (mViewAnimator != null)
                mViewAnimator.showNext();
        }
    };

    @Override
    public void draw(Canvas canvas) {
        if (mBitmap != null && mBitmap.isRecycled())
            mBitmap = null;
        if (mBitmap == null && getWidth() != 0 && getHeight() != 0) {
            mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
        }
        if (mCanvas != null && mBitmap != null && !mBitmap.isRecycled())
            super.draw(mCanvas);
        super.draw(canvas);
    }

    /**
     * Creates web view and returns its instance. Inside all needed clients and variables are binded.
     *
     * @param context provided context
     * @return initialized web view
     */
    private AdWebView createWebView(final Context context) {
        final AdWebView webView = new AdWebView(context);

        if (mSimpleWebViewClient == null)
            mSimpleWebViewClient = new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(final WebView view,
                                                        final String url) {
                    return true;
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                }
            };

        webView.setWebViewClient(mSimpleWebViewClient);
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);

        return webView;
    }

    private void initView(int viewCount) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        setLayoutParams(new RelativeLayout.LayoutParams(
                (int) (Utils.getWidthDeviceType(mContext) * scale + 0.5f), (int) (Utils.getHeightDeviceType(mContext) * scale + 0.5f)));

        mViewAnimator = new ViewAnimator(mContext);
        mWebViews = new ArrayList<AdWebView>();
        for (int i = 1; i <= viewCount; i++) {
            AdWebView webView = createWebView(mContext);
            final FrameLayout.LayoutParams webViewParams = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            mViewAnimator.addView(webView, webViewParams);
            mWebViews.add(webView);
        }
        final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        TranslateAnimation fadeInAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        fadeInAnimation.setDuration(FLIP_SPEED);
        fadeInAnimation.setStartOffset(FLIP_OFFSET);
        TranslateAnimation fadeOutAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.0f);
        fadeOutAnimation.setDuration(FLIP_SPEED);
        fadeOutAnimation.setStartOffset(FLIP_OFFSET);
        mViewAnimator.setInAnimation(fadeInAnimation);
        mViewAnimator.setOutAnimation(fadeOutAnimation);

        addView(mViewAnimator, params);
    }

    /**
     * Flips already loaded content. If no content exist, nothing is done.
     */
    public void flipLoadedContent() {
        if (mLoadedContent != null)
            showContent(mLoadedContent, mIsLoadedContentMraid);
    }

    /**
     * Renders content in next in list webview
     * If content is null it resets loading content.
     *
     * @param content provided content to load
     * @param isMraid true if content is mraid
     */
    public void showContent(String content, boolean isMraid) {
        showContent(content, isMraid, false);
    }

    /**
     * Description in
     * @see #showContent(String, boolean)
     * This sets an additional setting for if content is being restored.
     *
     * @param isRestoring This flag only can be set from inside.
     *                    By default, from outside this flag will always be false
     */
    private void showContent(String content, boolean isMraid, boolean isRestoring) {
        if (!isRestoring) {
            mIsRestoring = false;
            post(mClearCacheRunnable);
        }
        if (content == null) {
            mLoadedContent = null;
            mIsLoadedContentMraid = false;
            return;
        } else {
            mLoadedContent = content;
            mIsLoadedContentMraid = isMraid;
        }
        // Lazy instantiation for mraid type of client
        if (isMraid && mMraidWebViewClient == null) {
            mMraidWebViewClient = new MraidWebViewClient();
        }
        // Wrapping js in js tags
        content = "<script type=\"text/javascript\">" + content + "</script>";

        // Injecting mraid script if needed
        String jsInjectionWrapper = ((isMraid) ? "<script>"+MraidJavascript.JAVASCRIPT_SOURCE+"</script>" : "");
        content = "<html><head>"
                + JsLoadBridge.NATIVE_JS_CALLBACK_HEADER
                + jsInjectionWrapper
                + "</head>"
                + "<body style='margin:0;padding:0;' "+JsLoadBridge.NATIVE_JS_CALLBACK_BODY_ONLOAD+">"
                + content
                + "</body></html>";
        AdWebView webView;
        if (mTimesLoaded == 0 || mIsRestoring)
            webView = (AdWebView) mViewAnimator.getCurrentView();
        else
            webView = (AdWebView) getNextView(mWebViews, mViewAnimator.getCurrentView());
        if (webView != null) {
            webView.setWebViewClient((mIsLoadedContentMraid) ? mMraidWebViewClient : mSimpleWebViewClient);
            if (mMraidBridge == null)
                mMraidBridge = new MraidBridge(this);
            mMraidBridge.setWebView(webView);
            if (mLoadBridge == null)
                mLoadBridge = new JsLoadBridge(this);
            mLoadBridge.setWebView(webView);
            webView.loadDataWithBaseURL(null, content, "text/html", "UTF-8", null);
        }
    }

    public void changeVisibility(final boolean visible) {
//        Utils.p("Changing property to "+visible);
        post(new Runnable() {
            @Override
            public void run() {
                ((AdWebView) mViewAnimator.getCurrentView())
                        .fireChangeEventForProperty(MraidViewableProperty.createWithViewable(visible));
            }
        });
    }

    @Override
    public void onContentLoadedFromJsBridge() {
//        Utils.p("("+mTimesLoaded+") Content should be rendered, displaying... (Content restored? "+mIsRestoring+")");
        if (mIsLoadedContentMraid) {
            post(new Runnable() {
                @Override
                public void run() {
                    Utils.p("Setting content ready");
                    mMraidBridge.getWebView().fireReady();
                    mMraidBridge.getWebView().fireState(MraidBridge.State.DEFAULT);
                }
            });
        }

        if (!mIsRestoring) {
            if (mListener != null)
                mListener.onContentRender();
            if (mTimesLoaded > 0) {
                post(mFlipContentRunnable);
            }
        } else {
//            Utils.p("Clearing mock up display cache");
            // The delay is not really needed here, but it removed flicker on older devices
            postDelayed(mClearCacheRunnable, 100);
            mIsRestoring = false;
        }
        setTimesLoaded(mTimesLoaded+1);
    }

    @Override
    public void onNativeCall(String nativeCall) {
        Utils.p("JS: "+nativeCall);
    }

    /**
     * Get next view in the list (the one that will be shown on view flipper list).
     *
     * @param views       list of views to search in
     * @param currentView shown view
     * @return next view in the list
     */
    private View getNextView(ArrayList<? extends View> views, View currentView) {
        for (int i = 0; i < views.size(); i++) {
            if (views.get(i) == currentView)
                if ((i + 1) < views.size()) {
                    return views.get(i + 1);
                } else {
                    return views.get(0);
                }
        }
        return null;
    }


    public int getTimesLoaded() {
        return mTimesLoaded;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        removeCallbacks(mClearCacheRunnable);
        savedState.loadedContent = mLoadedContent;
        savedState.isLoadedContentMraid = mIsLoadedContentMraid;
        savedState.screenShot = mBitmap;
        savedState.timesLoaded = mTimesLoaded;
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        setTimesLoaded(savedState.timesLoaded);
        mBitmap = savedState.screenShot;
        mViewCache.setImageBitmap(mBitmap);
        mViewCache.setVisibility(VISIBLE);
        mIsRestoring = true;
        if (mViewAnimator != null) {
            mLoadedContent = savedState.loadedContent;
            mIsLoadedContentMraid = savedState.isLoadedContentMraid;

            if (mLoadedContent != null && mLoadedContent.length() > 0) {
                if (mListener != null)
                    mListener.onContentRestore(true);
                showContent(mLoadedContent, mIsLoadedContentMraid, true);
            } else {
                if (mListener != null)
                    mListener.onContentRestore(false);
            }
        }
    }

    private static class SavedState extends BaseSavedState {
        String loadedContent;
        boolean isLoadedContentMraid;
        Bitmap screenShot;
        int timesLoaded;

        public SavedState(Parcel source) {
            super(source);
            if (source.readInt() == 1)
                loadedContent = source.readString();
            if (source.readInt() == 1)
                screenShot = source.readParcelable(Bitmap.class.getClassLoader());
            isLoadedContentMraid = (source.readInt() == 1);
            timesLoaded = source.readInt();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt((loadedContent != null) ? 1 : 0);
            if (loadedContent != null)
                dest.writeString(loadedContent);
            dest.writeInt((screenShot != null) ? 1 : 0);
            if (screenShot != null)
                dest.writeParcelable(screenShot, 0);
            dest.writeInt((isLoadedContentMraid) ? 1 : 0);
            dest.writeInt(timesLoaded);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {

            @Override
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    public void setListener(BannerViewListener listener) {
        this.mListener = listener;
    }

    public void setTimesLoaded(int timesLoaded) {
        this.mTimesLoaded = timesLoaded;
    }
}
