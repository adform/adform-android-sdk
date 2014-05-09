package com.adform.sdk2.resources;

import android.content.Context;
import com.adform.sdk2.utils.Utils;

/**
 * Created by mariusm on 07/05/14.
 */
public class AdDimension {
    private int mWidth;
    private int mHeight;

    public AdDimension(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
    }

    public AdDimension(Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        mWidth = (int)(Utils.getWidthDeviceType(context) * scale+0.5f);
        mHeight = (int)(Utils.getHeightDeviceType(context) * scale+0.5f);
    }

    public int getWidth() {
        return mWidth;
    }

    public void setWidth(int width) {
        this.mWidth = width;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int height) {
        this.mHeight = height;
    }
}
