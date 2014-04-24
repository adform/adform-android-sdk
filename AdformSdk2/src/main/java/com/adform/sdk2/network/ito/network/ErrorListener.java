package com.adform.sdk2.network.ito.network;

/**
 * Created with IntelliJ IDEA.
 * User: andrius
 * Date: 7/10/13
 * Time: 12:34 AM
 */
public interface ErrorListener {
    void onError(NetworkTask request, NetworkError error);
}
