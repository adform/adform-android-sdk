package com.adform.sdk2.network.app;

import com.adform.sdk2.BuildConfig;
import com.adform.sdk2.Constants;
import com.adform.sdk2.network.base.ito.network.*;
import com.adform.sdk2.utils.Utils;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import java.io.IOException;

/**
 * Created by marius on 10/10/13.
 */
public class AdformNetworkTask<ResponseType> extends NetworkTask<ResponseType> {

    public AdformNetworkTask(NetworkRequest.Method method, String url,
                             Class<ResponseType> responseEntity,
                             NetworkResponseParser<ResponseType> responseParser) {
        super(method, url, responseEntity);
        setParser(responseParser);
    }

    @Override
    protected void signRequest(HttpRequest httpRequest) throws AuthorizationError {}

    // This needs to be overriden, for custom network handling
    @Override
    protected NetworkResponse<ResponseType> handleResponse(HttpResponse response) throws IOException, AuthorizationError {
        int statusCode = response.getStatusLine().getStatusCode();
        mRawStringResponse = responseToRawString(response).toString();
        Utils.p("Raw response (" + statusCode + "):" + mRawStringResponse);
        for (int i = 0; i < getParameters().size(); i++) {
            Utils.p(getParameters().get(i).getName() + ":" + getParameters().get(i).getValue());
        }
        NetworkResponse networkResponse = null;

        switch (statusCode){
            case 200:
                //check if not empty response
                if(mRawStringResponse != null && mRawStringResponse.length() > 0)
                    networkResponse = parseJsonResponseBody();
                break;
            case 400:
            case 401:
            case 404:
            default:
                break;
        }

        return networkResponse;
    }

    /**
     * override server url to use our custom
     * @return
     */
    @Override
    protected String getServerUrl() {
        return Constants.SERVER_URL;
    }

}
