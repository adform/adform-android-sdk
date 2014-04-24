package com.adform.sdk2.network.ito.network;

/**
 * Created with IntelliJ IDEA.
 * User: andrius
 * Date: 7/8/13
 * Time: 6:35 PM
 */
public class AuthorizationError extends Exception {
    private String mErrorInnerCode;
    public AuthorizationError(String errorCode) {
        this.mErrorInnerCode = errorCode;
    }

    public String getErrorCode() {
        return mErrorInnerCode;
    }
}
