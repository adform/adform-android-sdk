package com.adform.sdk2.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import com.adform.sdk2.interfaces.AdViewControllable;
import com.adform.sdk2.network.app.RawNetworkTask;
import com.adform.sdk2.network.app.entities.entities.AdServingEntity;
import com.adform.sdk2.network.app.entities.entities.RawResponse;
import com.adform.sdk2.network.app.services.AdService;
import com.adform.sdk2.network.base.ito.network.NetworkRequest;
import com.adform.sdk2.network.base.ito.network.NetworkResponse;
import com.adform.sdk2.network.base.ito.network.NetworkTask;
import com.adform.sdk2.network.base.ito.network.SuccessListener;
import com.adform.sdk2.network.base.ito.observable.ObservableService;
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
    private String mLoadedContent;
    private DocumentBuilderFactory mDocBuilderFactory;

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
    public void loadContent(String url) {
        Utils.p("Loading content...");
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
                    }
                }
            });
            getTask.execute();
        }
    }

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
        mBannerWebView.loadDataWithBaseURL(null, content, "text/html", "UTF-8", null);
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

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.loadedContent = mLoadedContent;
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
        if(mBannerWebView != null && savedState.loadedContent != null) {
            mLoadedContent = savedState.loadedContent;
            showContent(savedState.loadedContent);
        }
    }


    private static class SavedState extends BaseSavedState {
        String loadedContent;

        public SavedState(Parcel source) {
            super(source);
            if (source.readInt() == 1)
                loadedContent = source.readString();
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
}
