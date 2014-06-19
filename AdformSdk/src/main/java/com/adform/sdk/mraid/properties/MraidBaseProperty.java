package com.adform.sdk.mraid.properties;

import java.util.ArrayList;

/**
 * Created by mariusm on 07/05/14.
 */
public abstract class MraidBaseProperty {
    @Override
    public String toString() {
        if (toJson() != null)
            return toJson();
        if (toGet() != null)
            return toGet();
        return null;
    }

    public abstract String toJson();

    public abstract String toGet();

    public static String getUrlPropertiesToString(ArrayList<MraidBaseProperty> properties) {
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

    public static String getRequestPropertiesToString(ArrayList<MraidBaseProperty> properties) {
        if (properties == null)
            return null;
        StringBuilder path = new StringBuilder("{");
        for (int i = 0; i < properties.size(); i++) {
            MraidBaseProperty property = properties.get(i);
            String json = property.toJson();
            if (json != null) {
                path.append(json);
                if (i < properties.size() - 1)
                    path.append(", ");
            }
        }
        path.append("}");
        return path.toString();
    }

}





