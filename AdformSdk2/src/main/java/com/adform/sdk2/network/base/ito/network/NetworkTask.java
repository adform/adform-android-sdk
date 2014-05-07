package com.adform.sdk2.network.base.ito.network;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created with IntelliJ IDEA.
 * User: andrius
 * Date: 7/4/13
 * Time: 11:13 PM
 *
 * Updated by marius
 *
 * Main network class for performing requests, supports retries, oauth signing
 * Removed parser, as entity is parsed seperately
 */
public abstract class NetworkTask<ResponseType> extends AsyncTask<Void, NetworkError, NetworkResponse<ResponseType>> {

    private static final String TAG = NetworkTask.class.getSimpleName();
    public static final int REQUEST_TIMEOUT = 15; // In seconds

    // server url
    public static final String HTTP_OK = "OK";

    // tag to cheaply distinguish requests in listeners
    private int mTag;

    // stores request data - headers, content, method, relative url
    private NetworkRequest mRequest;

    // server response body in string, intermediate result.
    protected String mRawStringResponse;

    //default retry times is zero
    private long mNumberOfRetries = 0;

    // response entity, can be any object
    protected Class<ResponseType> mResponseEntity;

    // no response body expected
    protected boolean mIgnoreBodyParsing;

    // OAuth consumer that signs requests
//    private CommonsHttpOAuthConsumer mConsumer;

    // http client that performs http requests. can be set externally
    private DefaultHttpClient mHttpClient;
    private static DefaultHttpClient sHttpClient;

    // all listeners are under this wrapper, for cleaner code.
    protected NetworkListenersWrapper<ResponseType> mListeners;
    private HttpUriRequest mRunningHttpUriRequest;

    // raw response parsing to entity, can be externally set before executing
    private NetworkResponseParser<ResponseType> mParser;

    /**
     * Note, using this constructor, base parameters such as 'version' are not added to request, it have to done manually.
     * @param mRequest request
     * @param responseEntity response entity for parsing response body
     */
    @SuppressWarnings("unused")
    public NetworkTask(NetworkRequest mRequest,Class<ResponseType> responseEntity) {
        this.mRequest = mRequest;
        this.mResponseEntity = responseEntity;
        this.mListeners = new NetworkListenersWrapper<ResponseType>();
        //do not add base parameters
    }

    /**
     * Default contstructor for initialization of NetworkTask
     * @param method Http method
     * @param url relative URL
     * @param responseEntity response entity for parsing response body
     */
    @SuppressWarnings("unused")
    public NetworkTask(NetworkRequest.Method method, String url,Class<ResponseType> responseEntity) {
        this.mRequest = new NetworkRequest(method, getServerUrl()+url);
        this.mResponseEntity = responseEntity;
        addBaseParameters();
        this.mListeners = new NetworkListenersWrapper<ResponseType>();
    }

    /**
     * adds base parameters that common to all requests
     */
    protected void addBaseParameters() {
        // Should probably be overriden
    }

    /* (non-Javadoc)
     * @see android.os.AsyncTask#onPreExecute()
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

//        mConsumer = WoraPayApplication.getContext().getAuthManager().getCurrentConsumer();

        //ensure that request is created and url path is set
        if(mRequest==null){
            throw new IllegalStateException("request cannot be null");
        } else {
            if(mRequest.getUrl()==null){
                throw new IllegalStateException("request is empty (missing url path)");
            }
        }

        // init if not set externally
        if (mHttpClient == null) {
            mHttpClient = getClient();
        }

        //notify listener that load has been started
        mListeners.notifyStart(this);
    }

    @Override
    protected NetworkResponse<ResponseType> doInBackground(Void... params) {
        long time = System.currentTimeMillis();
        Log.d(TAG, mRequest.getMethod().name() + " " + mRequest.getUrl());

        //capture last error to return if request after retries was not successfull
        NetworkError lastError = null;
        for (int i = 0; i <mNumberOfRetries+1; i++)
        {
            if(!isCancelled()){
                try {
                    HttpResponse response = performRequest(mRequest);
                    NetworkResponse<ResponseType> networkResponse = handleResponse(response);
                    Log.v(TAG, "networkResponse: " + (networkResponse.getEntity() != null ? networkResponse.getEntity().getClass().getSimpleName() : "null") + ", it took " + (System.currentTimeMillis() - time) + "ms to complete");
                    return networkResponse;
                } catch (UnknownHostException host) {
                    lastError = new NetworkError(NetworkError.Type.NETWORK,0,null);
                    publishProgress(lastError);
                    host.printStackTrace();
                } catch (HttpHostConnectException e) {
                    lastError = new NetworkError(NetworkError.Type.NETWORK,0,null);
                    publishProgress(lastError);
                    e.printStackTrace();
                } catch (ConnectTimeoutException host) {
                    lastError = new NetworkError(NetworkError.Type.NETWORK,0,null);
                    publishProgress(lastError);
                    host.printStackTrace();
                } catch (SocketTimeoutException host) {
                    lastError = new NetworkError(NetworkError.Type.NETWORK,0,null);
                    publishProgress(lastError);
                    host.printStackTrace();
                } catch (IllegalStateException e) {
                    lastError = new NetworkError(NetworkError.Type.NETWORK,0,null);
                    publishProgress(lastError);
                    e.printStackTrace();
                } catch (IOException e) {
                    lastError = new NetworkError(NetworkError.Type.SERVER,0,null);
                    publishProgress(lastError);
                    e.printStackTrace();
                } catch (AuthorizationError authorizationError) {
                    lastError = new NetworkError(NetworkError.Type.AUTH,0,null,authorizationError.getErrorCode());
                    publishProgress(lastError);
                    authorizationError.printStackTrace();
                } finally {
                    //sleep before retry (don't sleep after last retry)
                    if(i < mNumberOfRetries-1)
                        sleep();
                }
            } else {
                return null;
            }
        }
        Log.d(TAG, "request unsuccessful exiting with error: " + lastError.getMessage());
        return new NetworkResponse(lastError);
    }

    @Override
    protected void onProgressUpdate(NetworkError... values) {
        super.onProgressUpdate(values);

        //notify listener that network was retry with previous error
        mListeners.notifyRetry(this, values.length > 0 ? values[0] : null);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Log.d(TAG, "onCancelled");
        // call abort to network client
        if(mRunningHttpUriRequest !=null && !mRunningHttpUriRequest.isAborted()){
            mHttpClient.getConnectionManager().shutdown();
            Log.d(TAG, "running http request abort received");
        }
        mListeners.notifyCancel(this);
        mListeners.notifyFinnish(this);
    }

    private void sleep() {
        try {
            //retry operation after 2 seconds
            Log.d(TAG, "sleeping for 3 seconds before retry...");
            Thread.sleep(3000);
        } catch (InterruptedException e1) {
            //we expect it to be interrupted to, don't log stack trace
            //e1.printStackTrace();
            Log.d(TAG, "sleeping interrupted");
        }
    }

    /* (non-Javadoc)
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @Override
    protected void onPostExecute(NetworkResponse<ResponseType> networkResponse) {
        super.onPostExecute(networkResponse);
        if(!isCancelled()){
            if(networkResponse.getError() != null){
                mListeners.notifyError(this,networkResponse.getError());
            } else {
                mListeners.notifySuccess(this,networkResponse);
            }
        } else {
            Log.d(TAG, "canceled (onPostExecute): " + mRequest.getUrl());
            mListeners.notifyCancel(this);
        }
        mListeners.notifyFinnish(this);
    }


    /**
     * Handle response.
     *
     * @param response the client response
     * @return the network response
     */
    protected NetworkResponse<ResponseType> handleResponse(HttpResponse response) throws IOException, AuthorizationError {
        return null;
    }

//    /**
//     * Parses the {@code mRawStringResponse}. Parser can be set externally
//     * @return parsed response to NetworkResponse
//     */
//    protected NetworkResponse parseJsonResponseBody() throws IOException {
//        // parser can be set externally
//        if(mParser == null){
//            mParser = new NetworkStringResponseParser<ResponseType>();
//        }
//
//        NetworkResponse networkResponse = mParser.parse(mRawStringResponse,mResponseEntity);
//        Log.v(TAG, "mRawStringResponse parsed");
//        // NetworkResponse networkResponse = new NetworkResponse(entity);
//        return networkResponse;
//    }

    private HttpResponse performRequest(NetworkRequest mRequest) throws IOException, AuthorizationError {
        // create request
        HttpUriRequest request = createRequest(mRequest);

        //sign request
        signRequest(request);

        // execute request
        mRunningHttpUriRequest = request;
        HttpResponse response = mHttpClient.execute(request);
        mRunningHttpUriRequest = null;

        return response;
    }
    private DefaultHttpClient getClient(){
        if(sHttpClient == null){
            sHttpClient = createClient();
        }
        return sHttpClient;
    }

    protected void signRequest(HttpRequest httpRequest) throws AuthorizationError {}

    private HttpUriRequest createRequest(NetworkRequest networkRequest) {
        HttpUriRequest httpRequest = null;

        String url = networkRequest.getUrl();

        switch (networkRequest.getMethod()) {
            case GET:
                httpRequest = new HttpGet(url);
                NetworkTask.addUrlParameters((HttpGet) httpRequest, networkRequest);
                break;
            case POST:
                httpRequest = new HttpPost(url);
                NetworkTask.addPostParameters((HttpPost)httpRequest,networkRequest);
                break;
            case PUT:
                httpRequest = new HttpPut(url);
                NetworkTask.addPostParameters((HttpPut)httpRequest,networkRequest);
                break;
            case DELETE:
                httpRequest = new HttpDelete(url);
                break;
        }
        addHeaders(httpRequest, networkRequest);
        return httpRequest;
    }

    protected NetworkResponse createResponseWithError(NetworkError.Type type, int code,String errorMessage){
        NetworkError error = new NetworkError(type,code,errorMessage);
        return new NetworkResponse(error);
    }

    private static void addHeaders(HttpRequest httpRequest, NetworkRequest networkRequest) {
        Map<String,String> headers = networkRequest.getHeaders();
        for(String headerName : headers.keySet()){
            httpRequest.addHeader(headerName, headers.get(headerName));
        }
    }

    private static void addUrlParameters(HttpRequestBase httpRequest, NetworkRequest networkRequest) {
        List<NameValuePair> params = networkRequest.getRequestParameters();

        Uri.Builder uri = Uri.parse(networkRequest.getUrl()).buildUpon();
        for(NameValuePair param : params){
            uri.appendQueryParameter(param.getName(),param.getValue());
        }
        String newUri = uri.build().toString();
        try {
            httpRequest.setURI(new URI(newUri));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private static void addPostParameters(HttpEntityEnclosingRequestBase httpRequest, NetworkRequest networkRequest) {
        List<NameValuePair> params = networkRequest.getRequestParameters();

        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params);
            httpRequest.setEntity(entity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void logRequestError(String method,String url, String responseCode,String body){
        // Error from server tracking is not really needed here.
//        WoraPayApplication.getContext().trackEvent("error",responseCode + ", " +url +", "+method,body,0L);
    }

    // Parameters handling
    /**
     * Adds the parameter.
     *
     * @param name the name
     * @param value the value
     */
    public void addParameter(String name,String value) {
        mRequest.addParameter(name, value);
    }

    /**
     * Adds the parameter.
     *
     * @param name the name
     * @param value the value
     */
    public void addParameter(String name, int value) {
        addParameter(name, String.valueOf(value));
    }

    /**
     * Adds the parameter.
     *
     * @param name the name
     * @param value the value
     */
    public void addParameter(String name, boolean value) {
        addParameter(name, String.valueOf(value));
    }

    /**
     * add parameters to request
     * @param params parameters to add
     */
    public void addParameters(ArrayList<NameValuePair> params){
        mRequest.addRequestParameters(params);
    }
    public List<NameValuePair> getParameters() {
        return mRequest.getParams();
    }

    /**
     * Parses the {@code mRawStringResponse}. Parser can be set externally
     * @return parsed response to NetworkResponse
     */
    protected NetworkResponse parseJsonResponseBody() throws IOException {
        // parser can be set externally
        if(mParser == null)
            throw new IOException("No parser pre-set");

        NetworkResponse networkResponse = mParser.parse(mRawStringResponse,mResponseEntity);
        Log.v(TAG,"mRawStringResponse parsed");
        // NetworkResponse networkResponse = new NetworkResponse(entity);
        return networkResponse;
    }


    // TODO DIRTY CODE REFACTOR THIS!!!!

    public DefaultHttpClient createClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
            HttpConnectionParams.setConnectionTimeout(params, REQUEST_TIMEOUT * 1000);
            HttpConnectionParams.setSoTimeout(params, REQUEST_TIMEOUT * 1000);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            e.printStackTrace();
            return new DefaultHttpClient();
        }
    }

    protected StringBuilder responseToRawString(HttpResponse resp) throws IOException {
        InputStream content = resp.getEntity().getContent();
        InputStreamReader inputStreamReader = new InputStreamReader(content, Charset.forName("UTF-8"));
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuilder sb = new StringBuilder();

        try {
            String line = reader.readLine();
            while (line != null) {
                sb.append(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            Log.d(TAG, "rethrow IOException: " + e.getLocalizedMessage());
            //rethrow, catch for closing streams
            throw e;
        } finally {
            inputStreamReader.close();
            reader.close();
            content.close();
            resp.getEntity().consumeContent();
        }
        return sb;
    }

    class MySSLSocketFactory extends SSLSocketFactory {
        SSLContext sslContext = SSLContext.getInstance("TLS");

        public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };

            sslContext.init(null, new TrustManager[] { tm }, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }


    /** GETTERS AND SETTERS **********************************************************************************************/
    protected abstract String getServerUrl();

    public int getTag() {
        return mTag;
    }

    public void setTag(int mTag) {
        this.mTag = mTag;
    }

    /**
     * change retry policy, default is zero retries
     * @param mNumberOfRetries number of retries
     */
    @SuppressWarnings("unused")
    public void setNumberOfRetries(long mNumberOfRetries) {
        this.mNumberOfRetries = mNumberOfRetries;
    }

    public void setSuccessListener(SuccessListener mSuccessListener) {
        this.mListeners.setSuccessListener(mSuccessListener);
    }

    @SuppressWarnings("unused")
    public void setErrorListener(ErrorListener mErrorListener) {
        this.mListeners.setErrorListener(mErrorListener);
    }

    @SuppressWarnings("unused")
    public void setRetryListener(RetryListener mRetryListener) {
        this.mListeners.setRetryListener(mRetryListener);
    }

    @SuppressWarnings("unused")
    public void setLoadingStateListener(LoadingStateListener mLoadingStateListener) {
        this.mListeners.setLoadingStateListener(mLoadingStateListener);
    }

    @SuppressWarnings("unused")
    public void setCancelListener(CancelListener mCancelListener) {
        this.mListeners.setCancelListener(mCancelListener);
    }

    @SuppressWarnings("unused")
    public void setHttpClient(DefaultHttpClient mHttpClient) {
        this.mHttpClient = mHttpClient;
    }

    /**
     * @param mIgnoreBodyParsing if set to true, response parsing ignores body parsing, and returns empty response
     * useful for simple posts when no body is expected, to avoid json parsing error.
     */
    @SuppressWarnings("unused")
    public void setIgnoreBodyParsing(boolean mIgnoreBodyParsing) {
        this.mIgnoreBodyParsing = mIgnoreBodyParsing;
    }

    public void setParser(NetworkResponseParser<ResponseType> mParser) {
        this.mParser = mParser;
    }

    public NetworkRequest getRequest() {
        return mRequest;
    }

    public NetworkListenersWrapper<ResponseType> getListeners() {
        return mListeners;
    }
}
