package com.adform.sdk2.utils;

import android.view.View;

/**
 * Created by mariusm on 15/05/14.
 */
public class ViewCoords {
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
}
