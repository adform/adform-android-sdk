package com.adform.sdk2.mraid.properties;

/**
 * Created by mariusm on 08/05/14.
 * A property that holds API version
 */
public class MraidStringProperty extends MraidBaseProperty {
    private final String mKey;
    private final String mValue;
    private final boolean isInteger; // Define variable type

    MraidStringProperty(String key, String value, boolean isInteger) {
        mKey = key;
        mValue = value;
        this.isInteger = isInteger;
    }

    public static MraidStringProperty createWithKeyAndValue(String key, String value) {
        return new MraidStringProperty(key, value, false);
    }

    public static MraidStringProperty createWithKeyAndValue(String key, int value) {
        return new MraidStringProperty(key, String.valueOf(value), true);
    }

    @Override
    public String toGet() {
        return mKey+"="+ mValue;
    }
    @Override
    public String toJson() {
        return "\""+mKey+"\":" +
                ((!isInteger)?"\"":"") +
                mValue +
                ((!isInteger)?"\"":"");
    }
}
