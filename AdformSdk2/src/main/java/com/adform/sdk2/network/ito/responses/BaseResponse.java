package com.adform.sdk2.network.ito.responses;

import com.adform.sdk2.network.ito.entities.ErrorEntity;

/**
 * Created with IntelliJ IDEA.
 * User: andrius
 * Date: 7/8/13
 * Time: 12:44 PM
 */
public class BaseResponse {

    ErrorEntity error;

    public ErrorEntity getError() {
        return error;
    }
}


