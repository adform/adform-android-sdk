package com.adform.sdk2.mraid.properties;

import java.util.ArrayList;

/**
 * Created by mariusm on 07/05/14.
 */
public abstract class MraidBaseProperty {
    @Override
    public String toString() {
        if (toGet() != null)
            return toGet();
        if (toJson() != null)
            return toJson();
        return null;
    }

    public abstract String toJson();

    public abstract String toGet();

    public static String generatePropertiesToString(ArrayList<MraidBaseProperty> properties) {
        if (properties == null)
            return null;
        StringBuilder path = new StringBuilder("?");
        for (int i = 0; i < properties.size(); i++) {
            MraidBaseProperty property = properties.get(i);
            if (i != 0)
                path.append("&");
            path.append(property.toGet());
        }
        return path.toString();
    }

}





