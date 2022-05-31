package com.adform.adformdemo.support

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.adform.adformdemo.R

class EmptyWebviewFragment : Fragment() {
    private var text: String? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.fragment_empty_webview, container, false)
        if (savedInstanceState != null) text = savedInstanceState.getString(INSTANCE_TEXT)
        if (text != null) (rootView.findViewById<View>(R.id.text_view) as TextView).text = text
        val webView = rootView.findViewById<View>(R.id.web_view) as WebView
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebClient()
        webView.loadUrl("https://google.com")
        return rootView
    }

    fun setText(text: String?) {
        this.text = text
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INSTANCE_TEXT, text)
    }

    //region Classes
    private inner class WebClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return false
        }
    } //endregion

    companion object {
        const val INSTANCE_TEXT = "TEXT"
    }
}
