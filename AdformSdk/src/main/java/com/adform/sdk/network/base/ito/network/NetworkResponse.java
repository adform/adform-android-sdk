package com.adform.sdk.network.base.ito.network;

/**
 * Created with IntelliJ IDEA.
 * User: andrius
 * Date: 7/4/13
 * Time: 11:28 PM
 */
public class NetworkResponse<ResponseType> {
    protected ResponseType entity;
    protected NetworkError error;

    public NetworkResponse(ResponseType entity) {
        this.entity = entity;
    }

    public NetworkResponse(NetworkError error) {
        this.error = error;
    }

    public ResponseType getEntity() {
        return entity;
    }

    public NetworkError getError() {
        return error;
    }

    public boolean hasNoErrors(){
        return error == null;
    }

}
