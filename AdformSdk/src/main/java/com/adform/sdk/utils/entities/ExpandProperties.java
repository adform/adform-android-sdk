package com.adform.sdk.utils.entities;

/**
 * Created by mariusm on 10/06/14.
 */
public class ExpandProperties {
    private int mWidth, mHeight;
    private boolean useCustomClose, isModal;

    public ExpandProperties(int width, int height, boolean useCustomClose, boolean isModal) {
        this.mWidth = width;
        this.mHeight = height;
        this.useCustomClose = useCustomClose;
        this.isModal = isModal;
    }

    public ExpandProperties(int width, int height, boolean useCustomClose) {
        this.mWidth = width;
        this.mHeight = height;
        this.useCustomClose = useCustomClose;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public boolean useCustomClose() {
        return useCustomClose;
    }

    public boolean isModal() {
        return isModal;
    }

    public void setWidth(int width) {
        this.mWidth = width;
    }

    public void setHeight(int height) {
        this.mHeight = height;
    }

    public void setUseCustomClose(boolean useCustomClose) {
        this.useCustomClose = useCustomClose;
    }

    public void setModal(boolean isModal) {
        this.isModal = isModal;
    }
}
