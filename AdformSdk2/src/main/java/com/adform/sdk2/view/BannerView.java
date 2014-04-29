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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;
import com.adform.sdk2.interfaces.AdViewControllable;
import com.adform.sdk2.network.app.RawNetworkTask;
import com.adform.sdk2.network.app.entities.entities.RawResponse;
import com.adform.sdk2.network.base.ito.network.*;
import com.adform.sdk2.utils.Utils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by mariusm on 24/04/14.
 * View that loads various type of ads for a small banner. Ads that are loaded in a circle,
 * are displayed with flip animation. View provides callbacks through {@link com.adform.sdk2.view.BannerView.BannerViewListener}
 */
public class BannerView extends RelativeLayout implements AdViewControllable {
    public static final int FLIP_SPEED = 500;
    public static final int FLIP_OFFSET = 1000; // Needed for webview render time.
    private Context mContext = null;
    private WebSettings mWebSettings;
    private String mLoadedContent;
    private DocumentBuilderFactory mDocBuilderFactory;
    private BannerViewListener mListener;

    private ViewFlipper mViewFlipper;
    private ArrayList<WebView> mWebViews;
    private int mTimesLoaded = 0;

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
            if (mBitmap != null)
                mBitmap.recycle();
            mBitmap = null;
            mCanvas = null;
        }
    };

    @Override
    public void draw(Canvas canvas) {
        if (mBitmap == null && getWidth() != 0 && getHeight() != 0){
            mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
        }
        if (mCanvas != null && mBitmap != null && !mBitmap.isRecycled())
            super.draw(mCanvas);
        super.draw(canvas);
    }

    /**
     * Creates web view and returns its instance. Inside all needed clients and variables are binded.
     * @param context provided context
     * @return initialized web view
     */
    private WebView createWebView(final Context context) {
        final WebView webView = new WebView(context) {
            @Override
            public void draw(final Canvas canvas) {
                super.draw(canvas);
            }
        };

        mWebSettings = webView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        webView.setBackgroundColor(Color.TRANSPARENT);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view,
                                                    final String url) {
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
//                Picture picture = view.capturePicture();
//                Bitmap  b = Bitmap.createBitmap( picture.getWidth(),
//                        picture.getHeight(), Bitmap.Config.ARGB_8888);
//                Canvas c = new Canvas( b );
//
//                picture.draw( c );
//                FileOutputStream fos = null;
//                try {
//                    fos = new FileOutputStream( "mnt/sdcard/yahoo.jpg" );
//                    if ( fos != null ){
//                        b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//                        fos.close();
//                    }
//                }
//                catch( Exception e ) {
//                    e.printStackTrace();
//                }
            }
        });

        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);

        // todo: fix this method deprecation, when its provided
        // This methods seems to be deprecated, but still there is no alternative to this
        // As closest alternative is onPageFinished, but it indicated content loading, not rendering it
        // Some sources:
        // http://stackoverflow.com/questions/7822481/picturelistener-and-onnewpicture-are-deprecated-alternatives
        // http://stackoverflow.com/questions/7166534/picturelistener-is-deprecated-and-obsolete-is-there-a-replacement
        // http://stackoverflow.com/questions/17873341/android-webview-picturelistener-deprecated-still-no-alternative
        // https://code.google.com/p/android/issues/detail?id=38646
//        webView.setPictureListener(new WebView.PictureListener() {
//            @Override
//            public void onNewPicture(WebView view, Picture picture) {
//                if (mListener != null)
//                    mListener.onContentLoadSuccessful();
//            }
//        });
        return webView;
    }

    private void initView(int viewCount) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        setLayoutParams(new RelativeLayout.LayoutParams(
                (int)(Utils.getWidthDeviceType(mContext) * scale+0.5f), (int)(Utils.getHeightDeviceType(mContext) * scale+0.5f)));

        mViewFlipper = new ViewFlipper(mContext);
        mWebViews = new ArrayList<WebView>();
        for (int i = 1; i <= viewCount; i++) {
            WebView webView = createWebView(mContext);
            final FrameLayout.LayoutParams webViewParams = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            mViewFlipper.addView(webView, webViewParams);
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
        mViewFlipper.setInAnimation(fadeInAnimation);
        mViewFlipper.setOutAnimation(fadeOutAnimation);

        addView(mViewFlipper, params);
    }

    /**
     * Loads url that should be displayed in webview. When url is loaded,
     * showContent(String) is initiated.
     * If url is null it resets loading content.
     * @param url provided url to load.
     */
    @Override
    public void loadContent(String url) {
        if (url == null) {
            mLoadedContent = null;
            return;
        }
        Utils.p("Loading content...");
        if (mListener != null)
            mListener.onNewContentLoad();
        String pulledUrl = pullUrlFromXmlScript(url);
        if (pulledUrl != null) {
            RawNetworkTask getTask =
                    new RawNetworkTask(NetworkRequest.Method.GET, pulledUrl);
            getTask.setSuccessListener(new SuccessListener<RawResponse>() {
                @Override
                public void onSuccess(NetworkTask request, NetworkResponse<RawResponse> response) {
                    if (response != null && response.getEntity() != null) {
                        mLoadedContent = response.getEntity().getContent();
                        showContent(mLoadedContent);
                        if (mListener != null)
                            mListener.onContentLoadSuccessful();
                    }
                }
            });
            getTask.setErrorListener(new ErrorListener() {
                @Override
                public void onError(NetworkTask request, NetworkError error) {
                    if (mListener != null)
                        mListener.onContentLoadFailed();
                }
            });
            getTask.execute();
        }
    }

    /**
     * Flips already loaded content. If no content exist, nothing is done.
     */
    @Override
    public void flipLoadedContent() {
        if (mLoadedContent != null)
            showContent(mLoadedContent);
    }

    /**
     * Renders content in next in list webview
     * @param content provided conent to load
     */
    @Override
    public void showContent(String content) {
        Utils.p("Showing content...");

        // Wrapping js in js tags
        content = "<script type=\"text/javascript\">" + content + "</script>";

        // If the string data lacks the HTML boilerplate, add it.
        if (!content.contains("<html>")) {
            content = "<html><head></head><body style='margin:0;padding:0;'>" + content +
                    "</body></html>";
        }
        WebView webView = null;
        if (mTimesLoaded == 0)
            webView = (WebView) mViewFlipper.getCurrentView();
        else
            webView = (WebView) getNextView(mWebViews, mViewFlipper.getCurrentView());
        if (webView != null) {
            Utils.p("Rendering content...");
            webView.loadDataWithBaseURL(null, content, "text/html", "UTF-8", null);
            if (mTimesLoaded > 0) {
                mViewFlipper.showNext();
                Utils.p("Showing next item...");
            }
            mTimesLoaded++;
        }
    }

    /**
     * Get next view in the list (the one that will be shown on view flipper list).
     * @param views list of views to search in
     * @param currentView shown view
     * @return next view in the list
     */
    private View getNextView(ArrayList<? extends View> views, View currentView) {
        for (int i = 0; i < views.size(); i++) {
            if (views.get(i) == currentView)
                if ((i+1) < views.size()) {
                    return views.get(i + 1);
                } else {
                    return views.get(0);
                }
        }
        return null;
    }

    private String pullUrlFromXmlScript(String xml) {
        // Inserting header
        if (mDocBuilderFactory == null)
            mDocBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder dBuilder = mDocBuilderFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));
            NodeList nList = doc.getElementsByTagName("script");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    return eElement.getAttribute("src");
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
        savedState.screenShot = mBitmap;
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState savedState = (SavedState)state;
        super.onRestoreInstanceState(savedState.getSuperState());
        mBitmap = savedState.screenShot;
        mViewCache.setImageBitmap(mBitmap);
        mViewCache.setVisibility(VISIBLE);
        postDelayed(mClearCacheRunnable, 800);
        if(mViewFlipper != null && savedState.loadedContent != null) {
            mLoadedContent = savedState.loadedContent;
            if (mLoadedContent != null && mLoadedContent.length() > 0) {
                showContent(savedState.loadedContent);
                if (mListener != null)
                    mListener.onContentRestore();
            }
        }
    }


    private static class SavedState extends BaseSavedState {
        String loadedContent;
        Bitmap screenShot;

        public SavedState(Parcel source) {
            super(source);
            if (source.readInt() == 1)
                loadedContent = source.readString();
            if (source.readInt() == 1)
                screenShot = source.readParcelable(Bitmap.class.getClassLoader());
        }
        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt((loadedContent != null)?1:0);
            if (loadedContent != null)
                dest.writeString(loadedContent);
            dest.writeInt((screenShot != null) ? 1 : 0);
            if (screenShot != null)
                dest.writeParcelable(screenShot, 0);
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

    public interface BannerViewListener {
        public void onNewContentLoad();
        public void onContentLoadSuccessful();
        public void onContentRestore();
        public void onContentLoadFailed();
    }

    public void setListener(BannerViewListener listener) {
        this.mListener = listener;
    }

}
