package com.adform.sdk2.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import com.adform.sdk2.interfaces.AdViewControllable;
import com.adform.sdk2.network.app.entities.entities.AdServingEntity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by mariusm on 24/04/14.
 */
public class BannerView extends RelativeLayout implements AdViewControllable {
    private Context mContext = null;
    private WebSettings mWebSettings;
    private WebView mBannerWebView;

    public BannerView(Context context) {
        this(context, null);
    }

    public BannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initView();
    }

    private WebView createWebView(final Context context) {
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
                return false;
            }
        });

        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);

        return webView;
    }

    private void initView() {
        mBannerWebView = createWebView(mContext);
        final float scale = mContext.getResources().getDisplayMetrics().density;
        this.setLayoutParams(new RelativeLayout.LayoutParams((int)(300 * scale+0.5f), (int)(50 * scale+0.5f)));
        final FrameLayout.LayoutParams webViewParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mBannerWebView, webViewParams);
    }

    @Override
    public void showContent(String content) {
        mBannerWebView.loadData(content, "text/html", "UTF-8");
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
    }
}
