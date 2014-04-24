package com.adform.sdk.utils;

import com.adform.sdk.AdView;

import java.util.TimerTask;

public class ReloadTask extends TimerTask {

	private final AdView mWebView;

	public ReloadTask(final AdView WebView) {
		this.mWebView = WebView;
	}

	@Override
	public void run() {
		this.mWebView.loadNextAd();
	}
}