package com.adform.sdk.resources;

import android.content.Context;
import com.adform.sdk.utils.Utils;

/**
 * Created by mariusm on 07/05/14.
 */
public class AdDimension {
    private int mWidth;
    private int mHeight;
    private float mScale = 0;

    private AdDimension() {}

    public static AdDimension createEmptyDimension() {
        return new AdDimension();
    }

    private AdDimension(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
    }

    public static AdDimension createDimensionWithSize(int width, int height) {
        return new AdDimension(width, height);
    }

    private AdDimension(Context context) {
        mScale = context.getResources().getDisplayMetrics().density;
        mWidth = (int)(Utils.getWidthDeviceType(context) * mScale+0.5f);
        mHeight = (int)(Utils.getHeightDeviceType(context) * mScale+0.5f);
    }

    public static AdDimension createDefaultDimension(Context context) {
        return new AdDimension(context);
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

    public boolean equals(int width, int height) {
        if (mWidth != width || mHeight != height)
            return false;
        return true;
    }

    public boolean equals(AdDimension adDimension) {
        if (adDimension == null)
            return false;
        if (adDimension.getWidth() == getWidth() &&
                adDimension.getHeight() == getHeight())
            return true;
        return false;
    }

    @Override
    public String toString() {
        return super.toString()+" - dimen: w:"+mWidth+" / h:"+mHeight;
    }
}
