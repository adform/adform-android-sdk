package com.adform.sdk2.network.base.ito.network;

/**
 * Created with IntelliJ IDEA.
 * User: andrius
 * Date: 7/10/13
 * Time: 12:37 AM
 */
public interface RetryListener {
    void onRetry(NetworkTask request, NetworkError error);
}
