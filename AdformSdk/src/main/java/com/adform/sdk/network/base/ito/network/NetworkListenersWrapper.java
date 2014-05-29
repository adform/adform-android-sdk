package com.adform.sdk.network.base.ito.network;

/**
 * Created with IntelliJ IDEA.
 * User: andrius
 * Date: 7/10/13
 * Time: 12:36 AM
 * Wraps NetworkTask listeners under the hood.
 */
public class NetworkListenersWrapper<T> {

    private ErrorListener mErrorListener;
    private SuccessListener<T> mSuccessListener;
    private RetryListener mRetryListener;
    private CancelListener mCancelListener;
    private LoadingStateListener mLoadingStateListener;

    public void notifySuccess(NetworkTask request, NetworkResponse<T> response){
        if(mSuccessListener != null){
            mSuccessListener.onSuccess(request,response);
        }
    }

    public void notifyError(NetworkTask request, NetworkError error){
        if(mErrorListener != null){
            mErrorListener.onError(request,error);
        }
    }

    public void notifyRetry(NetworkTask request, NetworkError error){
        if(mRetryListener != null){
            mRetryListener.onRetry(request,error);
        }
    }

    public void notifyCancel(NetworkTask request){
        if(mCancelListener != null){
            mCancelListener.onCancel(request);
        }
    }

    public void notifyStart(NetworkTask request){
        if(mLoadingStateListener != null){
            mLoadingStateListener.onStart(request);
        }
    }
    public void notifyFinnish(NetworkTask request){
        if(mLoadingStateListener != null){
            mLoadingStateListener.onFinnish(request);
        }
    }

    public void setErrorListener(ErrorListener mErrorListener) {
        this.mErrorListener = mErrorListener;
    }

    public void setSuccessListener(SuccessListener<T> mSuccessListener) {
        this.mSuccessListener = mSuccessListener;
    }

    public void setRetryListener(RetryListener mRetryListener) {
        this.mRetryListener = mRetryListener;
    }

    public void setCancelListener(CancelListener mCancelListener) {
        this.mCancelListener = mCancelListener;
    }

    public void setLoadingStateListener(LoadingStateListener mLoadingStateListener) {
        this.mLoadingStateListener = mLoadingStateListener;
    }

    public ErrorListener getErrorListener() {
        return mErrorListener;
    }

    public SuccessListener<T> getSuccessListener() {
        return mSuccessListener;
    }

    public RetryListener getRetryListener() {
        return mRetryListener;
    }

    public CancelListener getCancelListener() {
        return mCancelListener;
    }

    public LoadingStateListener getLoadingStateListener() {
        return mLoadingStateListener;
    }
}
