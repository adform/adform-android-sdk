package com.adform.sdk2.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.adform.sdk2.utils.Utils;

/**
 * Created by mariusm on 21/05/14.
 */
public class InterstitialView extends RelativeLayout {
    private Context mContext;

    public InterstitialView(Context context) {
        this(context, null);
    }

    public InterstitialView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InterstitialView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;

        // Compability issues
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB)
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);

    }
}
