package com.adform.sdk2.view;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import com.adform.sdk2.network.app.services.AdService;
import com.adform.sdk2.utils.Utils;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by mariusm on 24/04/14.
 */
public class CoreAdView extends FrameLayout implements Observer {
    private static final String ATTR_URL = "request_url";

    private Context mContext;
    private AdService mAdService;

    private String mRequestUrl = null;

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

    @Override
    public void update(Observable observable, Object data) {
        Utils.p("Loaded something");
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
}
