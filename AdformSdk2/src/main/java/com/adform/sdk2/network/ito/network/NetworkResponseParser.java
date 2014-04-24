package com.adform.sdk2.network.ito.network;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: andrius
 * Date: 7/5/13
 * Time: 10:38 AM
 */
public interface NetworkResponseParser<T> {

    /**
     * Parses the response.
     *
     * @param data the input data
     * @param clazz the response class to parse to
     * @return the base response
     * @throws java.io.IOException Signals that an I/O exception has occurred.
     */
    public NetworkResponse parse(String data, Class<T> clazz) throws IOException;

}