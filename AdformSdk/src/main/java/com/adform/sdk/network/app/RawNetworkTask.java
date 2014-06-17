package com.adform.sdk.network.app;

import com.adform.sdk.network.app.entities.entities.RawResponse;
import com.adform.sdk.network.base.ito.network.*;
import com.adform.sdk.utils.Utils;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import java.io.IOException;

/**
 * Created by marius on 10/10/13.
 */
public class RawNetworkTask extends NetworkTask<RawResponse> {

    public RawNetworkTask(NetworkRequest.Method method, String url) {
        super(method, url, RawResponse.class);
        setParser(RawResponse.responseParser);
    }

    @Override
    protected void signRequest(HttpRequest httpRequest) throws AuthorizationError {}

    // This needs to be overriden, for custom network handling
    @Override
    protected NetworkResponse<RawResponse> handleResponse(HttpResponse response) throws IOException, AuthorizationError {
        int statusCode = response.getStatusLine().getStatusCode();
        NetworkResponse networkResponse = null;
        if (response != null &&
                response.getEntity() != null) {
            mRawStringResponse = responseToRawString(response).toString();
            networkResponse = null;

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
        }
        if (networkResponse == null)
            networkResponse = createResponseWithError(
                    NetworkError.Type.SERVER, 0, "Error: " + mRawStringResponse
            );
        if (networkResponse.getError() != null)
            Utils.e(networkResponse.getError().getMessage());
        return networkResponse;
    }

    /**
     * override server url to use our custom
     * @return
     */
    @Override
    protected String getServerUrl() {
        return "";
    }

}
