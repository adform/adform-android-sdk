package com.adform.sdk2.network.base.ito.network;

/**
 * Created with IntelliJ IDEA.
 * User: andrius
 * Date: 7/4/13
 * Time: 11:27 PM
 */
public class NetworkError {

    public static final int NOT_EXISTS = 404;
    public static final int CODE_ALREADY_EXISTS = 409;

    public enum Type {
        NETWORK,
        AUTH,
        SERVER,
        USER,
    }

    private String message;
    private int errorCode;
    private Type type;
    private String errorInnerCode;

    public NetworkError(Type type, int errorCode,String message, String errorInnerCode) {
        this.type = type;
        this.message = message;
        this.errorCode = errorCode;
        this.errorInnerCode = errorInnerCode;
    }
    public NetworkError(Type type, int errorCode,String message) {
        this.type = type;
        this.message = message;
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    protected void setMessage(String message) {
        this.message = message;
    }

    protected void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public Type getType() {
        return type;
    }

    public String getErrorInnerCode() {
        return errorInnerCode;
    }
}
