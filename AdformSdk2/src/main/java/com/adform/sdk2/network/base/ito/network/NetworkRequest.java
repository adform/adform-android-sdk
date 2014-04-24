package com.adform.sdk2.network.base.ito.network;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: andrius
 * Date: 7/4/13
 * Time: 11:19 PM
 *
 * Class representing a network request performed by {@link NetworkTask}
 *
 */
public class NetworkRequest {

    public enum Method {
        GET, POST, PUT, DELETE
    }

    private String mUrl;
    private Method mMethod;
    private Map<String,String> mHeaders;
    private List<NameValuePair> mParams;

    public NetworkRequest(Method mMethod, String mUrl) {
        this.mMethod = mMethod;
        this.mUrl = mUrl;
        this.mParams = new ArrayList<NameValuePair>();
        this.mHeaders = new HashMap<String, String>();

        switch (mMethod) {
            case GET:
                break;
            case POST:
                setContentType("application/x-www-form-urlencoded");
                break;
            case PUT:
                setContentType("application/x-www-form-urlencoded");
                break;
            case DELETE:
                setContentType("application/x-www-form-urlencoded");
                break;
        }
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public Method getMethod() {
        return mMethod;
    }

    public void setMethod(Method mMethod) {
        this.mMethod = mMethod;
    }

    public void setContentType(String contentType) {
        mHeaders.put("Content-type",contentType);
    }

    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    public List<NameValuePair> getParams() {
        return mParams;
    }

    public void addParameter(String name, String value) {
        mParams.add(new BasicNameValuePair(name, value));
    }

    public List<NameValuePair> getRequestParameters() {
        return mParams;
    }

    public void setRequestParameters(ArrayList<NameValuePair> requestParameters) {
        this.mParams = requestParameters;
    }

    public void addRequestParameters(ArrayList<NameValuePair> requestParameters) {
        this.mParams.addAll(requestParameters);
    }
}
