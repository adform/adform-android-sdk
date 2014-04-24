package com.adform.sdk2.view;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import com.adform.sdk2.interfaces.AdViewControllable;
import com.adform.sdk2.network.app.entities.entities.AdServingEntity;
import com.adform.sdk2.network.app.services.AdService;
import com.adform.sdk2.utils.Utils;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by mariusm on 24/04/14.
 */
public class CoreAdView extends FrameLayout implements Observer {
    private static final String ATTR_URL = "request_url";
    public static final int VIEW_TYPE_BANNER = 0;

    private Context mContext;
    private AdService mAdService;

    private String mRequestUrl = null;
    private HashMap<Integer, View> mViews;

    public CoreAdView(Context context) {
        this(context, null);
    }

    public CoreAdView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CoreAdView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        mAdService = new AdService();
        mViews = initViews(mViews);

        // Getting special attributes
        if (attrs != null) {
            int attrCount = attrs.getAttributeCount();
            for (int i = 0; i < attrCount; i++) {
                String name = attrs.getAttributeName(i);
                if (name.equals(ATTR_URL)) {
                    this.mRequestUrl = attrs.getAttributeValue(i);
                }
            }
        }
    }

    private HashMap<Integer, View> initViews(HashMap<Integer, View> views) {
        if (views == null)
            views = new HashMap<Integer, View>();
        views.put(VIEW_TYPE_BANNER, new BannerView(mContext));
        return views;
    }

    @Override
    public void update(Observable observable, Object data) {
        if (mViews == null || data == null)
            return;
        Utils.p("Loaded something");

        // Clear all views
        for (View view : mViews.values()) {
            if (view != null)
                removeView(view);
        }

        // Add the only needed view
        addView(mViews.get(VIEW_TYPE_BANNER));

        // Todo remove this mockup later
        if (mViews.get(VIEW_TYPE_BANNER) instanceof AdViewControllable) {
            String content = ((AdServingEntity) data).getAdEntity().getTagDataEntity().getSrc();
            ((AdViewControllable) mViews.get(VIEW_TYPE_BANNER))
                    .showContent(content);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Utils.p("onAttachedToWindow");
//        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
//        filter.addAction(Intent.ACTION_USER_PRESENT);
//        mContext.registerReceiver(mScreenStateReceiver, filter);

        mAdService.addObserver(this);
        mAdService.startService();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Utils.p("onDetachedFromWindow");
//        unregisterScreenStateBroadcastReceiver();

        mAdService.deleteObserver(this);
        mAdService.stopService();
    }

    // Utils functions
    private WebView createWebView(final Context context) {
        WebSettings mWebSettings = null;

        final WebView webView = new WebView(this.getContext()) {

            @Override
            public void draw(final Canvas canvas) {
                if (this.getWidth() > 0 && this.getHeight() > 0)
                    super.draw(canvas);
            }
        };

        mWebSettings = webView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        webView.setBackgroundColor(Color.TRANSPARENT);
//        setLayer(webView);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view,
                                                    final String url) {
                return true;
            }
        });

        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);

        return webView;
    }


}
