package com.adform.sdk2.network.app.entities.entities;

import com.adform.sdk2.network.base.ito.network.NetworkResponse;
import com.adform.sdk2.network.base.ito.network.NetworkResponseParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;

/**
 * Created by mariusm on 25/04/14.
 */
public class RawResponse {
    private String content;

    public RawResponse(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static NetworkResponseParser<RawResponse> responseParser = new NetworkResponseParser<RawResponse>() {
        @Override
        public NetworkResponse parse(String data, Class<RawResponse> clazz) throws IOException {
            final RawResponse response = new RawResponse(data);
            NetworkResponse<RawResponse> networkResponse =
                    new NetworkResponse<RawResponse>(response);
            return networkResponse;
        }
    };

}
