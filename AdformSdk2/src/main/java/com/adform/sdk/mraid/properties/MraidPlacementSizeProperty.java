package com.adform.sdk.mraid.properties;

import com.adform.sdk.resources.AdDimension;
import com.adform.sdk.utils.Utils;

/**
 * Created by mariusm on 08/05/14.
 * A property that holds placement coordinates for the view.
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
    public String toGet() {
        return "width="+ mPlacementWidth +"&"+
                "height="+ mPlacementHeight;
    }
    @Override
    public String toJson() {
        return "\"width\":"+ Utils.pxToDp(mPlacementWidth)+", "+
                "\"height\":"+Utils.pxToDp(mPlacementHeight);
    }
}
