package com.adform.sdk.utils;

import android.os.Bundle;
import com.adform.sdk.Constants;
import com.adform.sdk.network.app.AdformNetworkTask;
import com.adform.sdk.network.app.RawNetworkTask;
import com.adform.sdk.network.app.entities.entities.AdServingEntity;
import com.adform.sdk.network.app.entities.entities.RawResponse;
import com.adform.sdk.network.base.ito.network.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mariusm on 29/04/14.
 * Helps to load content that is parsed from json script source.
 */
public class AdformContentLoadManager implements ErrorListener, LoadingStateListener {

    public static final String INSTANCE_LAST_RESPONSE = "INSTANCE_LAST_RESPONSE";
    public static final String INSTANCE_LAST_MRAID_FLAG = "INSTANCE_LAST_MRAID_FLAG";

    /**
     * An interface that returns events after loading from network task
     */
    public interface ContentLoaderListener {
        /**
         * Callback when loaded mraid type of content
         * @param content provided content
         */
        public void onContentMraidLoadSuccessful(String content);
        /**
         * Callback when content load failed
         */
        public void onContentLoadFailed();
    }

    public class ContentLoadException extends Exception {
        public ContentLoadException(String detailMessage) {
            super(detailMessage);
        }
    }

    private ContentLoaderListener mListener;
    private DocumentBuilderFactory mDocBuilderFactory;
    private boolean isLoading = false;
    private RawResponse mLastRawResponse;
    private AdServingEntity mLastAdServingResponse;

    public AdformContentLoadManager() {}

    public void loadContent(NetworkTask<?> getTask) throws ContentLoadException {
//        if (isLoading())
//            throw new ContentLoadException("Content already being loaded");
        getTask.execute();
    }

    /**
     * Loads url.
     * @param url   provided url to load
     * @param isUrlInXml if content is wrapped in xml script tags, isXml flag should be true
     *              for taking out content
     */
    public RawNetworkTask getRawGetTask(String url, boolean isUrlInXml) throws ContentLoadException {
        if (url == null || (url != null && url.length() == 0))
            throw new ContentLoadException("Url content is empty");
        String pulledUrl = null;
        if (isUrlInXml) {
            pulledUrl = pullUrlFromXmlScript(url);
            if (pulledUrl == null)
                throw new ContentLoadException("Provided content is not in script tags, o can't be parsed");
        }
        RawNetworkTask rawTask =
                new RawNetworkTask(NetworkRequest.Method.GET, (isUrlInXml)?pulledUrl:url);
        rawTask.setSuccessListener(new SuccessListener<RawResponse>() {
            @Override
            public void onSuccess(NetworkTask request, NetworkResponse<RawResponse> response) {
                if (response != null && response.getEntity() != null) {
                    mLastRawResponse = response.getEntity();
                    if (mListener != null) {
                        mListener.onContentMraidLoadSuccessful(response.getEntity().getContent());
                    }
                }
            }
        });
        rawTask.setLoadingStateListener(this);
        rawTask.setErrorListener(this);
        return rawTask;
    }

    /**
     * Loads url.
     * @param url   provided url to load
     */
    public RawNetworkTask getRawPostTask(String url, String properties) throws ContentLoadException {
        if (url == null || (url != null && url.length() == 0))
            throw new ContentLoadException("Url content is empty");
        RawNetworkTask rawTask =
                new RawNetworkTask(NetworkRequest.Method.POST, url);
        if (properties != null)
            rawTask.setJsonEntity(properties);
        rawTask.setSuccessListener(new SuccessListener<RawResponse>() {
            @Override
            public void onSuccess(NetworkTask request, NetworkResponse<RawResponse> response) {
                if (response != null && response.getEntity() != null) {
                    mLastRawResponse = response.getEntity();
                    if (mListener != null) {
                        mListener.onContentMraidLoadSuccessful(response.getEntity().getContent());
                    }
                }
            }
        });
        rawTask.setLoadingStateListener(this);
        rawTask.setErrorListener(this);
        return rawTask;
    }

    /**
     * Loads url.
     * @param urlPostfix postfix, that is added to the standard url
     * @param properties properties that are added with the request. This probably will be json properties
     */
    public AdformNetworkTask getContractTask(String urlPostfix, String properties) throws ContentLoadException {
        Utils.p("Generated params: "+properties);
        AdformNetworkTask<AdServingEntity> contractTask =
                new AdformNetworkTask<AdServingEntity>(NetworkRequest.Method.POST,
                        Constants.SDK_INFO_PATH+
                                (urlPostfix != null?urlPostfix:""),
                        AdServingEntity.class, AdServingEntity.responseParser);
        if (properties != null)
            contractTask.setJsonEntity(properties);
        contractTask.setSuccessListener(new SuccessListener<AdServingEntity>() {
            @Override
            public void onSuccess(NetworkTask request, NetworkResponse<AdServingEntity> response) {
                if (response != null && response.getEntity() != null) {
                    mLastAdServingResponse = response.getEntity();
                    if (mListener != null) {
                        if (mLastAdServingResponse != null
                                && mLastAdServingResponse.getAdEntity() != null
                                && mLastAdServingResponse.getAdEntity().getTagDataEntity() != null
                                && mLastAdServingResponse.getAdEntity().getTagDataEntity().getSrc() != null)
                            mListener.onContentMraidLoadSuccessful(
                                    mLastAdServingResponse.getAdEntity().getTagDataEntity().getSrc());
                    }
                }
            }
        });
        contractTask.setLoadingStateListener(this);
        contractTask.setErrorListener(this);
        return contractTask;
    }

    @Override
    public void onError(NetworkTask request, NetworkError error) {
        if (mListener != null)
            mListener.onContentLoadFailed();
    }

    @Override
    public void onStart(NetworkTask request) {
        isLoading = true;
    }

    @Override
    public void onFinnish(NetworkTask request) {
        isLoading = false;
    }

//    @Override
//    public void onSuccess(NetworkTask request, NetworkResponse<RawResponse> response) {
//        if (response != null && response.getEntity() != null) {
//            mLastRawResponse = response.getEntity();
//            if (mListener != null) {
//                mListener.onContentMraidLoadSuccessful(response.getEntity().getContent());
//            }
//        }
//    }

    private static final String HTML_TAG_PATTERN = "(\\\\x3Cscript|<script).{1,50}(mraid\\.js).*?(\\/>|script>)";
    /**
     * Retuns if content contains an mraid implementation
     * @param content provided content to look into
     * @return removed mraid.js implementation (between script tags), if no implementation is needed return null
     */
    public static String isMraidImpelemnetation(String content) {
        if (content.contains("mraid.js")) {
            Pattern pTag = Pattern.compile(HTML_TAG_PATTERN);
            Matcher mTag = pTag.matcher(content);

            while (mTag.find()) {
                return mTag.replaceAll("");
            }
        }
        return null;
    }

    /**
     * Pulls out an src attribute value from the script tag.
     * @param xml provided xml that should be parsed.
     * @return src attribute value. If there was an error parsing, null value is returned
     */
    // todo: maybe use something more lite here than parser
    private String pullUrlFromXmlScript(String xml) {
        // Inserting header
        if (mDocBuilderFactory == null)
            mDocBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            // todo: mock up server problems
            DocumentBuilder dBuilder = mDocBuilderFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));
            NodeList nList = doc.getElementsByTagName("script");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    return eElement.getAttribute("src");
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public String getResponse() {
        if (mLastRawResponse != null && mLastRawResponse.getContent() != null)
            return mLastRawResponse.getContent();
        return null;
    }

    public void setListener(ContentLoaderListener listener) {
        this.mListener = listener;
    }

    // -------------------------
    // Instance saving/restoring
    // -------------------------
    /**
     * @return a bundle of variables that should be saved into instance
     */
    public Bundle getSaveInstanceBundle() {
        Bundle bundle = new Bundle();
        if (mLastRawResponse != null)
            bundle.putString(INSTANCE_LAST_RESPONSE, mLastRawResponse.getContent());
        return bundle;
    }

    /**
     * Restores service state from the instance provided bundle
     * @param restoreBundle variable bundle with stored information
     */
    public void restoreInstanceWithBundle(Bundle restoreBundle) {
        if (restoreBundle == null)
            return;
        mLastRawResponse = new RawResponse(restoreBundle.getString(INSTANCE_LAST_RESPONSE));
    }

}
