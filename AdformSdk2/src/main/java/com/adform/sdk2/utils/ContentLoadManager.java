package com.adform.sdk2.utils;

import com.adform.sdk2.network.app.RawNetworkTask;
import com.adform.sdk2.network.app.entities.entities.RawResponse;
import com.adform.sdk2.network.base.ito.network.*;
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

/**
 * Created by mariusm on 29/04/14.
 * Helps to load content that is parsed from json script source.
 */
public class ContentLoadManager {

    private ContentLoaderListener mListener;
    private DocumentBuilderFactory mDocBuilderFactory;

    public ContentLoadManager(ContentLoaderListener l) {
        this.mListener = l;
    }

    /**
     * Loads url that should be displayed in webview. When url is loaded,
     * showContent(String) is initiated.
     * @param url provided url to load.
     */
    public void loadContent(String url) {
        Utils.p("Loading content...");
        String pulledUrl = pullUrlFromXmlScript(url);
        if (pulledUrl != null) {
            RawNetworkTask getTask =
                    new RawNetworkTask(NetworkRequest.Method.GET, pulledUrl);
            getTask.setSuccessListener(new SuccessListener<RawResponse>() {
                @Override
                public void onSuccess(NetworkTask request, NetworkResponse<RawResponse> response) {
                    if (response != null && response.getEntity() != null) {
                        if (mListener != null) {
                            if (isMraidImpelemnetation(response.getEntity().getContent()))
                                mListener.onContentMraidLoadSuccessful(response.getEntity().getContent());
                            else
                                mListener.onContentLoadSuccessful(response.getEntity().getContent());
                        }
                    }
                }
            });
            getTask.setErrorListener(new ErrorListener() {
                @Override
                public void onError(NetworkTask request, NetworkError error) {
                    if (mListener != null)
                        mListener.onContentLoadFailed();
                }
            });
            getTask.execute();
        }
    }

    /**
     * Retuns if content contains an mraid implementation
     * @param content provided content to look into
     * @return true if mraid.js is found, false otherwise.
     */
    private boolean isMraidImpelemnetation(String content) {
        if (content.contains("mraid.js") && content.contains("script type=\"text/javascript\" src=\"mraid.js\""))
            return true;
        return false;
    }

    /**
     * Pulls out an src attribute value from the script tag.
     * @param xml provided xml that should be parsed.
     * @return src attribute value. If there was an error parsing, null value is returned
     */
    private String pullUrlFromXmlScript(String xml) {
        // Inserting header
        if (mDocBuilderFactory == null)
            mDocBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            // todo: mock up server problems
            xml = xml.replaceAll("&lt", "<");
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

    public interface ContentLoaderListener {
        public void onContentLoadSuccessful(String content);
        public void onContentMraidLoadSuccessful(String content);
        public void onContentLoadFailed();
    }
}
