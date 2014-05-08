package com.adform.sdk2.mraid.properties;

import com.adform.sdk2.resources.AdDimension;

/**
 * Created by mariusm on 08/05/14.
 */
public class MraidPlacementSizeProperty extends MraidBaseProperty {
    private final int mPlacementWidth;
    private final int mPlacementHeight;

    public MraidPlacementSizeProperty(int width, int height) {
        mPlacementWidth = width;
        mPlacementHeight = height;
    }

    public MraidPlacementSizeProperty(AdDimension adDimension) {
        mPlacementWidth = adDimension.getWidth();
        mPlacementHeight = adDimension.getHeight();
    }

    public static MraidPlacementSizeProperty createWithSize(int width, int height) {
        return new MraidPlacementSizeProperty(width, height);
    }

    public static MraidPlacementSizeProperty createWithDimension(AdDimension adDimension) {
        return new MraidPlacementSizeProperty(adDimension);
    }

    @Override
    public String toGetPair() {
        return "width="+ mPlacementWidth +"&"+
                "height="+ mPlacementHeight;
    }
}
