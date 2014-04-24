package com.adform.sdk2.network.base.ito.network;

/**
* Created with IntelliJ IDEA.
* User: andrius
* Date: 7/10/13
* Time: 12:38 AM
*/
public interface LoadingStateListener {
    public void onStart(NetworkTask request);
    public void onFinnish(NetworkTask request);
}
