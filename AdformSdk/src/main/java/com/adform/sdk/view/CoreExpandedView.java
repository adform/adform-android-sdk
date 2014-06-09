package com.adform.sdk.view;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.adform.sdk.mraid.properties.MraidDeviceIdProperty;
import com.adform.sdk.resources.AdDimension;
import com.adform.sdk.resources.CloseImageView;
import com.adform.sdk.utils.AdformEnum;
import com.adform.sdk.view.base.BaseCoreContainer;
import com.adform.sdk.view.base.BaseInnerContainer;
import com.adform.sdk.view.inner.InnerInterstitialView;

/**
 * Created by mariusm on 27/05/14.
 */
public class CoreExpandedView extends CoreInterstitialView implements View.OnClickListener {

    public CoreExpandedView(Context context, BaseInnerContainer innerContainer) {
        this(context, null, 0, innerContainer);
    }

    public CoreExpandedView(Context context) {
        this(context, null, 0, null);
    }

    public CoreExpandedView(Context context, AttributeSet attrs) {
        this(context, attrs, 0, null);
    }

    public CoreExpandedView(Context context, AttributeSet attrs, int defStyle, BaseInnerContainer innerContainer) {
        super(context, attrs, defStyle, innerContainer);

    }

}
