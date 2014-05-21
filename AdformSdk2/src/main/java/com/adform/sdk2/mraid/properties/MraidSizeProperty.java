package com.adform.sdk2.mraid.properties;

import com.adform.sdk2.utils.ViewCoords;

/**
 * Created by mariusm on 14/05/14.
 */
public class MraidSizeProperty extends MraidBaseProperty {
    public enum SizeType {
        MAX_SIZE,
        SCREEN_SIZE;

        public static String printType(SizeType position) {
            switch (position) {
                case MAX_SIZE:
                    return "maxSize";
                case SCREEN_SIZE:
                    return "screenSize";
            }
            return null;
        }
    }

    private final int mWidth, mHeight;
    private final SizeType mSizeType;

    MraidSizeProperty(SizeType sizeType, int width, int height) {
        this.mSizeType = sizeType;
        this.mWidth = width;
        this.mHeight = height;
    }

    public static MraidSizeProperty createWithSize(SizeType sizeType, int width, int height) {
        return new MraidSizeProperty(sizeType, width, height);
    }

    public static MraidSizeProperty createWithSize(SizeType sizeType,
                                                           ViewCoords viewCoords) {
        return new MraidSizeProperty(sizeType, viewCoords.getWidth(), viewCoords.getHeight());
    }

    @Override
    public String toGet() {
        return null;
    }
    @Override
    public String toJson() {
        return SizeType.printType(mSizeType)+": {width:"+mWidth+",height:"+mHeight+"}";
    }
}