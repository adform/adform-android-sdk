package com.adform.sdk.network.base.ito.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created with IntelliJ IDEA.
 * User: andrius
 * Date: 8/2/13
 * Time: 8:02
 */
public class ErrorEntity implements Parcelable {

    int code;
    String desc;
    String context;

    public ErrorEntity(){}

    // ---- Getters/Setters ---- //

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    // ---- Parcelable implementation ---- //

    @Override
    public int describeContents() {
        return 0;
    }

    public ErrorEntity(Parcel in) {
        this.code = in.readInt();
        if(in.readInt() == 1) this.desc = in.readString();
        if(in.readInt() == 1) this.context = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(code);
        dest.writeInt((desc == null)?0:1);
        dest.writeString(desc);
        dest.writeInt((context == null)?0:1);
        dest.writeString(context);
    }

    public static final Parcelable.Creator<ErrorEntity> CREATOR
            = new Parcelable.Creator<ErrorEntity>() {
        public ErrorEntity createFromParcel(Parcel in) {
            return new ErrorEntity(in);
        }

        public ErrorEntity[] newArray(int size) {
            return new ErrorEntity[size];
        }
    };
}
