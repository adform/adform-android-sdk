package com.adform.sdk.mraid.properties;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;

/**
 * Created by mariusm on 08/05/14.
 * A property that holds unique information about the device.
 * Hardware id *should* not be null, advertising id might be null
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
        String advertisingId = null;
        try {
            AdvertisingIdClient.Info adInfo = null;
            adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
            if (adInfo != null && !adInfo.isLimitAdTrackingEnabled())
                advertisingId = adInfo.getId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new MraidDeviceIdProperty(advertisingId, uniqueId);
    }

    @Override
    public String toGet() {
        return "aid="+ mAdvertisingId +"&"+
                "hwid="+ mHardwareId;
    }

    @Override
    public String toJson() {
        return "\"aid\":\""+mAdvertisingId+"\"";
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
