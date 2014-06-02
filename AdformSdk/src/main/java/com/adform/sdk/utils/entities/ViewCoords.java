package com.adform.sdk.utils.entities;

import android.view.View;

/**
 * Created by mariusm on 15/05/14.
 */
public class ViewCoords implements Cloneable {
    private int x, y;
    private int width, height;

    private ViewCoords(int x, int y) {
        this.x = x;
        this.y = y;
    }

    private ViewCoords(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // Factory methods
    public static ViewCoords createViewCoord(View view) {
        int[] location = new int[2];
        view.getLocationInWindow(location);
        return new ViewCoords(location[0], location[1], view.getWidth(), view.getHeight());
    }
    public static ViewCoords createViewCoord(int x, int y) {
        return new ViewCoords(x, y);
    }

    public static ViewCoords createViewCoord(int x, int y, int width, int height) {
        return new ViewCoords(x, y, width, height);
    }

    public static ViewCoords createViewCoord(ViewCoords viewCoords) {
        if (viewCoords == null)
            return null;
        return new ViewCoords(viewCoords.getX(), viewCoords.getY(),
                viewCoords.getWidth(), viewCoords.getHeight());
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return new ViewCoords(x, y, width, height);
    }

    public boolean equals(ViewCoords viewCoords) {
        if (viewCoords == null)
            return false;
        if (viewCoords.getX() == getX() &&
                viewCoords.getY() == getY() &&
                viewCoords.getWidth() == getWidth() &&
                viewCoords.getHeight() == getHeight())
            return true;
        return false;
    }

    public boolean isZero() {
        if (x == 0 && y == 0 && width  == 0 && height == 0)
            return true;
        return false;
    }

    public String toString() {
        return "ViewCoords { x:"+x+","+"y:"+y+","+"width:"+width+","+"height:"+height+" }";
    }
}
