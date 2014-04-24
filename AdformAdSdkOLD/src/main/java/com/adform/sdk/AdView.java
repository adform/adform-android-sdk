package com.adform.sdk;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import com.adform.sdk.interfaces.AdListener;
import com.adform.sdk.interfaces.Const;
import com.adform.sdk.support.AdRequest;
import com.adform.sdk.entities.AdServingEntity;
import com.adform.sdk.support.RequestBannerAd;
import com.adform.sdk.utils.ReloadTask;
import com.adform.sdk.views.BannerAdView;

import java.io.InputStream;
import java.util.Timer;

/**
 * Created by mariusm on 23/04/14.
 */
public class AdView extends FrameLayout {


    private Context mContext;
    private Thread loadContentThread;
    private Timer reloadTimer;
    private AdServingEntity response;
    private InputStream xml;
    private AdRequest request;
    private final Handler updateHandler = new Handler();
    private final Runnable showContent = new Runnable() {
        @Override
        public void run() {
            AdView.this.showContent();
        }
    };
    private AdListener adListener;
    private String requestURL = null;
    private BannerAdView mBannerView;

    public AdView(final Context context, final AttributeSet attributes){
        super(context, attributes);
        mContext = context;
        if (attributes != null) {
            int count = attributes.getAttributeCount();
            for (int i = 0; i < count; i++) {
                String name = attributes.getAttributeName(i);
                if (name.equals("request_url")) {
                    this.requestURL = attributes.getAttributeValue(i);
                }
//                else if (name.equals("animation")) {
//                    this.animation = attributes.getAttributeBooleanValue(i, false);
//                } else if (name.equals("includeLocation")) {
//                    this.includeLocation = attributes.getAttributeBooleanValue(i, false);
//                }
            }
        }
        initialize(context);
    }

    public AdView(final Context context, final String requestURL, final InputStream xml, final String publisherId,
                  final boolean includeLocation, final boolean animation) {
        this(context, xml, requestURL, publisherId, includeLocation, animation);
    }

    public AdView(final Context context, final InputStream xml, final String requestURL, final String publisherId, final boolean includeLocation,
                  final boolean animation){
        super(context);
        mContext = context;
        this.xml = xml;
        this.requestURL = requestURL;
//        this.publisherId = publisherId;
//        this.includeLocation = includeLocation;
//        this.animation = animation;
        this.initialize(context);
    }
    private void initialize(final Context context) {
//        mUserAgent = Util.getDefaultUserAgentString(getContext());
//        registerScreenStateBroadcastReceiver();
//        this.locationManager = null;
//        this.telephonyPermission = context
//                .checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE);
//        this.isAccessFineLocation = context
//                .checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
//        this.isAccessCoarseLocation = context
//                .checkCallingOrSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
//        if (this.isAccessFineLocation == PackageManager.PERMISSION_GRANTED
//                || this.isAccessCoarseLocation == PackageManager.PERMISSION_GRANTED)
//            this.locationManager = (LocationManager) this.getContext()
//                    .getSystemService(Context.LOCATION_SERVICE);
    }
    private void loadContent() {
        if (this.loadContentThread == null) {
            this.loadContentThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    final RequestBannerAd requestAd;
                    if(xml == null)
                        requestAd = new RequestBannerAd();
                    else
                        requestAd = new RequestBannerAd(xml);

                    try {

                        AdView.this.response = requestAd
                                .sendRequest(AdView.this.getRequest());
                        if (AdView.this.response != null) {
                            AdView.this.updateHandler
                                    .post(AdView.this.showContent);
                        }
                    } catch (final Throwable e) {
                        AdView.this.notifyLoadAdFailed(e);
                    }
                    AdView.this.loadContentThread = null;
                }

            });
            this.loadContentThread
                    .setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

                        @Override
                        public void uncaughtException(final Thread thread,
                                                      final Throwable ex) {
                            AdView.this.loadContentThread = null;
                        }
                    });
            this.loadContentThread.start();
        } else {

        }
    }
    private AdRequest getRequest() {
        if (this.request == null) {
            this.request = new AdRequest();
//            if (this.telephonyPermission == PackageManager.PERMISSION_GRANTED) {
//                final TelephonyManager tm = (TelephonyManager) this
//                        .getContext().getSystemService(
//                                Context.TELEPHONY_SERVICE);
//                this.request.setDeviceId(tm.getDeviceId());
//            } else {
//                this.request.setDeviceId(Util.getDeviceId(this.mContext));
//            }
//            this.request.setPublisherId(this.publisherId);
//            this.request.setUserAgent(mUserAgent);
//            this.request.setUserAgent2(Util.buildUserAgent());
        }
//        Location location = null;
//        if (this.includeLocation)
//            location = this.getLocation();
//        if (location != null) {
//            this.request.setLatitude(location.getLatitude());
//            this.request.setLongitude(location.getLongitude());
//        } else {
//            this.request.setLatitude(0.0);
//            this.request.setLongitude(0.0);
//        }

        this.request.setType(AdRequest.BANNER);
        this.request.setRequestURL(requestURL);
        return this.request;
    }

    public void loadNextAd() {
        this.loadContent();
    }

    public void pause() {
        if (this.reloadTimer != null)
            try {
                this.reloadTimer.cancel();
                this.reloadTimer = null;
            } catch (final Exception e) {
            }
    }
    private void notifyLoadAdFailed(final Throwable e) {
        this.updateHandler.post(new Runnable() {

            @Override
            public void run() {
                if (AdView.this.adListener != null) {
                    adListener.noAdFound();
                }
            }
        });
    }

    private void showContent(){
//        if(mMoPubview!=null){
//            mMoPubview.destroy();
//            this.removeView(mMoPubview);
//        }
        if(mBannerView!=null){
            this.removeView(mBannerView);
        }
        if(response.getType() == Const.TEXT || response.getType()==Const.IMAGE){
            mBannerView = new BannerAdView(mContext, response, false, adListener);
            this.addView(mBannerView);
        }
//        if(response.getType()==Const.MRAID){
//            mMoPubview = new com.adsdk.sdk.mraid.MoPubView(mContext);
//            final float scale = mContext.getResources().getDisplayMetrics().density;
//            this.addView(mMoPubview,new FrameLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, (int)(50*scale+0.5f)));
//            com.adsdk.sdk.mraid.AdView m = new com.adsdk.sdk.mraid.AdView(mContext, mMoPubview, response);
//            mMoPubview.setAdListener(adListener);
//            m.setAdUnitId("");
//            m.loadAd();
//
//        }
//        if(response.getType()==Const.NO_AD){
//            notifyNoAd();
//        }
        this.startReloadTimer();
    }
    private void startReloadTimer() {
        if (this.reloadTimer == null)
            return;

        final int refreshTime = Const.REFRESH_TIMER * 1000;

        final ReloadTask reloadTask = new ReloadTask(AdView.this);
        this.reloadTimer.schedule(reloadTask, refreshTime);
    }

}
