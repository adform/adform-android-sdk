package com.adform.sdk2.mraid.properties;

/**
 * Created by mariusm on 08/05/14.
 * A property that holds API version
 */
public class MraidStringProperty extends MraidBaseProperty {
    private final String mKey;
    private final String mValue;

    MraidStringProperty(String key, String value) {
        mKey = key;
        mValue = value;
    }

    public static MraidStringProperty createWithKeyAndValue(String key, String value) {
        return new MraidStringProperty(key, value);
    }

    @Override
    public String toGet() {
        return mKey+"="+ mValue;
    }
    @Override
    public String toJson() {
        return "\""+mKey+"\":\""+ mValue +"\"";
    }
}
