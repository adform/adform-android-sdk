package com.adform.sdk2.mraid;

import android.graphics.Bitmap;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.adform.sdk2.mraid.commands.MraidBaseCommand;
import com.adform.sdk2.utils.JsLoadBridge;
import com.adform.sdk2.view.base.BaseInnerContainer;
import com.adform.sdk2.view.inner.AdWebView;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mariusm on 06/05/14.
 */
public class MraidWebViewClient extends WebViewClient {
    private AdWebView mWebView;

    public MraidWebViewClient() {}

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Uri uri = Uri.parse(url);

        // Note that scheme will be null when we are passed a relative Uri
        String scheme = uri.getScheme();

        if (JsLoadBridge.NATIVE_JS_INTERFACE.equals(scheme)) {
            return true;
        }

        if (BaseInnerContainer.MRAID_JS_INTERFACE.equals(scheme)) {
            tryCommand(URI.create(url));
            return true;
        }

        return false;
    }

    private boolean tryCommand(URI uri) {
        String commandType = uri.getHost();
        List<NameValuePair> list = URLEncodedUtils.parse(uri, "UTF-8");
        Map<String, String> params = new HashMap<String, String>();
        for (NameValuePair pair : list) {
            params.put(pair.getName(), pair.getValue());
        }

        MraidBaseCommand command = MraidCommandFactory.create(commandType, params, mWebView);
        if (command != null) {
            command.execute();
            mWebView.fireNativeCommandCompleteEvent(commandType);
            return true;
        }
        return false;
    }

    public void setWebView(AdWebView webView) {
        this.mWebView = webView;
    }
}
