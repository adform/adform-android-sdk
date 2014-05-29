package com.adform.sdk.network.app.entities.entities;

import android.util.JsonReader;
import com.adform.sdk.network.base.ito.network.NetworkResponse;
import com.adform.sdk.network.base.ito.network.NetworkResponseParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AdServingEntity {

    private static final long serialVersionUID = 3271938798582141269L;

    private String version;
    private AdEntity adEntity;
    private MetaEntity metaEntity;

    public AdServingEntity() {
    }

    public AdServingEntity(String version, AdEntity adEntity) {
        this.version = version;
        this.adEntity = adEntity;
    }

    public MetaEntity getMetaEntity() {
        return metaEntity;
    }

    public void setMetaEntity(MetaEntity metaEntity) {
        this.metaEntity = metaEntity;
    }

    public AdEntity getAdEntity() {
        return adEntity;
    }

    public void setAdEntity(AdEntity adEntity) {
        this.adEntity = adEntity;
    }

    public static NetworkResponseParser<AdServingEntity> responseParser = new NetworkResponseParser<AdServingEntity>() {
        @Override
        public NetworkResponse parse(String data, Class<AdServingEntity> clazz) throws IOException {
            InputStream in = new ByteArrayInputStream(data.getBytes("UTF-8"));
            JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
            NetworkResponse<AdServingEntity> networkResponse = null;
            try {
                reader.beginObject();
                if (reader.hasNext() && reader.nextName().equals("adServing"))
                    networkResponse = new NetworkResponse<AdServingEntity>(readEntity(reader));
                reader.endObject();
            } catch(Exception e) {
                e.printStackTrace();
            } finally {
                reader.close();
            }
            return networkResponse;
        }
    };

    public static AdServingEntity readEntity(JsonReader reader) throws IOException  {
        String version = null;
        AdEntity adEntity = null;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("version")) {
                version = reader.nextString();
            } else if (name.equals("ad")) {
                adEntity = AdEntity.readEntity(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new AdServingEntity(version, adEntity);
    }
}
