package com.adform.sdk.mraid.properties;

import com.adform.sdk.utils.Utils;
import com.adform.sdk.utils.entities.ViewCoords;

/**
 * Created by mariusm on 14/05/14.
 */
public class MraidPositionProperty extends MraidBaseProperty {
    public enum PositionType {
        DEFAULT_POSITION,
        CURRENT_POSITION;

        public static String printType(PositionType position) {
            switch (position) {
                case DEFAULT_POSITION:
                    return "defaultPosition";
                case CURRENT_POSITION:
                    return "currentPosition";
            }
            return null;
        }
    }

    private final int mX, mY, mWidth, mHeight;
    private final PositionType mPositionType;

    MraidPositionProperty(PositionType positionType, int x, int y, int width, int height) {
        this.mPositionType = positionType;
        this.mX = x;
        this.mY = y;
        this.mWidth = width;
        this.mHeight = height;
    }

    public static MraidPositionProperty createWithPosition(PositionType positionType,
                                                           int x, int y, int width, int height) {
        return new MraidPositionProperty(positionType, x, y, width, height);
    }

    public static MraidPositionProperty createWithPosition(PositionType positionType,
                                                           ViewCoords viewCoords) {
        return new MraidPositionProperty(positionType, viewCoords.getX(), viewCoords.getY(),
                viewCoords.getWidth(), viewCoords.getHeight());
    }

    @Override
    public String toGet() {
        return null;
    }
    @Override
    public String toJson() {
        return PositionType.printType(mPositionType)+": {x:"+ Utils.pxToDp(mX)+
                ",y:"+Utils.pxToDp(mY)+
                ",width:"+Utils.pxToDp(mWidth)+
                ",height:"+Utils.pxToDp(mHeight)+"}";
    }
}