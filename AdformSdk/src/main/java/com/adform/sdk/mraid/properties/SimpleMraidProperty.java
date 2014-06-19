package com.adform.sdk.mraid.properties;

/**
 * Created by mariusm on 08/05/14.
 * A property that holds simple version of the parameter
 */
public class SimpleMraidProperty extends MraidBaseProperty {
    private final String mKey;
    private final String mValue;
    private final boolean isInteger; // Define variable type

    SimpleMraidProperty(String key, String value, boolean isInteger) {
        mKey = key;
        mValue = value;
        this.isInteger = isInteger;
    }

    public static SimpleMraidProperty createWithKeyAndValue(String key, String value) {
        return new SimpleMraidProperty(key, value, false);
    }

    public static SimpleMraidProperty createWithKeyAndValue(String key, int value) {
        return new SimpleMraidProperty(key, String.valueOf(value), true);
    }

    @Override
    public String toGet() {
        return mKey+"="+ mValue;
    }
    @Override
    public String toJson() {
        if (isInteger && Integer.parseInt(mValue) == 0)
            return null;
        return mKey+":" +
                ((!isInteger)?"\"":"") +
                mValue +
                ((!isInteger)?"\"":"");
    }
}
