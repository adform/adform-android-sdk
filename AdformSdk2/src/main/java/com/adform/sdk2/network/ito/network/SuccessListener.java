package com.adform.sdk2.network.ito.network;

/**
* Created with IntelliJ IDEA.
* User: andrius
* Date: 7/10/13
* Time: 12:37 AM
*/ /* request listeners */
public interface SuccessListener<ResponseType> {
    public void onSuccess(NetworkTask request, NetworkResponse<ResponseType> response);
}
