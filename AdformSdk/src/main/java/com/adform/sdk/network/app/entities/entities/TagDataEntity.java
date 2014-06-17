package com.adform.sdk.network.app.entities.entities;

import android.util.JsonReader;
import android.util.JsonToken;

import java.io.IOException;

/**
 * Created by mariusm on 23/04/14.
 */
public class TagDataEntity {
    private String impressionUrl;
    private String src;

    public TagDataEntity(String impressionUrl, String src) {
        this.impressionUrl = impressionUrl;
        this.src = src;
    }

    public TagDataEntity() {
    }

    public String getImpressionUrl() {
        return impressionUrl;
    }

    public void setImpressionUrl(String impressionUrl) {
        this.impressionUrl = impressionUrl;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public static TagDataEntity readEntity(JsonReader reader) throws IOException {
        String impressionUrl = null;
        String src = null;
        if (reader.peek() == JsonToken.BEGIN_OBJECT) {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (reader.peek() != JsonToken.NULL) {
                    if (name.equals("impressionUrl")) {
                        impressionUrl = reader.nextString();
                    } else if (name.equals("src")) {
                        src = reader.nextString();
                    } else {
                        reader.skipValue();
                    }
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        } else
            return null;
        return new TagDataEntity(impressionUrl, src);
    }
}
