package com.adform.sdk.mraid.properties;

/**
 * Created by mariusm on 14/05/14.
 */
public class MraidViewableProperty extends MraidBaseProperty {
    private final boolean mViewable;

    MraidViewableProperty(boolean viewable) {
        mViewable = viewable;
    }

    public static MraidViewableProperty createWithViewable(boolean viewable) {
        return new MraidViewableProperty(viewable);
    }

    @Override
    public String toGet() {
        return null;
    }
    @Override
    public String toJson() {
        return "viewable: " + (mViewable ? "true" : "false");
    }
}