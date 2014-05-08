package com.adform.sdk2.mraid.properties;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;

/**
 * Created by mariusm on 08/05/14.
 */
public class MraidDeviceIdProperty extends MraidBaseProperty implements Parcelable {
    // More info on advertising id: https://developer.android.com/google/play-services/id.html
    private String mAdvertisingId; // Might be null
    // Unique number generator http://blog.vogella.com/2011/04/11/android-unique-identifier/
    private String mHardwareId;

    public MraidDeviceIdProperty(String advertisingId, String hardwareId) {
        mAdvertisingId = advertisingId;
        mHardwareId = hardwareId;
    }

    public static MraidDeviceIdProperty createWithDeviceId(Context context) {
        String uniqueId = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        AdvertisingIdClient.Info adInfo = null;
        try {
            adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String advertisingId = null;
        if (adInfo != null)
            advertisingId = adInfo.getId();
        return new MraidDeviceIdProperty(advertisingId, uniqueId);
    }

    @Override
    public String toGetPair() {
        return "aid="+ mAdvertisingId +"&"+
                "hwid="+ mHardwareId;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt((mAdvertisingId != null)?1:0);
        if (mAdvertisingId != null) out.writeString(mAdvertisingId);
        out.writeInt((mHardwareId != null)?1:0);
        if (mHardwareId != null) out.writeString(mHardwareId);
    }

    public static final Parcelable.Creator<MraidDeviceIdProperty> CREATOR
            = new Parcelable.Creator<MraidDeviceIdProperty>() {
        public MraidDeviceIdProperty createFromParcel(Parcel in) {
            return new MraidDeviceIdProperty(in);
        }

        public MraidDeviceIdProperty[] newArray(int size) {
            return new MraidDeviceIdProperty[size];
        }
    };

    private MraidDeviceIdProperty(Parcel in) {
        if (in.readInt() == 1)
            mAdvertisingId = in.readString();
        if (in.readInt() == 1)
            mHardwareId = in.readString();
    }
}
