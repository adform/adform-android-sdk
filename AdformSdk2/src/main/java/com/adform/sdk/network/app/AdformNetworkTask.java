package com.adform.sdk.network.app;

import com.adform.sdk.Constants;
import com.adform.sdk.network.base.ito.network.*;
import com.adform.sdk.utils.Utils;
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
        Utils.p("Raw response: "+mRawStringResponse);
        NetworkResponse networkResponse = null;

        switch (statusCode){
            case 200:
                //check if not empty response
                if(mRawStringResponse != null && mRawStringResponse.length() > 0) {
                    networkResponse = parseJsonResponseBody();
                    if (networkResponse == null) {
                        networkResponse = createResponseWithError(NetworkError.Type.SERVER, statusCode, "Failed to parse (Raw: "+mRawStringResponse+")");
                    }
                } else {
                    networkResponse = createResponseWithError(NetworkError.Type.SERVER, statusCode, "Empty response");
                }
                break;
            default:
                break;
        }
        if (networkResponse == null)
            networkResponse = createResponseWithError(
                    NetworkError.Type.SERVER, 0, "Something is way off (Raw: "+mRawStringResponse+")"
            );
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
