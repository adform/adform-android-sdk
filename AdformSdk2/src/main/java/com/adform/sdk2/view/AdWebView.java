package com.adform.sdk2.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.webkit.WebView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by mariusm on 29/04/14.
 */
public class AdWebView extends WebView {

    public AdWebView(Context context) {
        super(context);
    }

    public AdWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AdWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    public void injectJavascript(String script) {
        final String url = "javascript:" + script;
        super.loadUrl(url);
    }


    //    private static Method SET_LAYER_TYPE;
//    private static Field LAYER_TYPE_SOFTWARE;
//
//    static {
//        initCompatibility();
//    };
//
//    private static void initCompatibility() {
//        try {
//            for(Method m:WebView.class.getMethods()){
//                if(m.getName().equals("setLayerType")){
//                    SET_LAYER_TYPE = m;
//                    break;
//                }
//            }
//            LAYER_TYPE_SOFTWARE = WebView.class.getField("LAYER_TYPE_SOFTWARE");
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static void setLayer(WebView webView){
//        if (SET_LAYER_TYPE != null && LAYER_TYPE_SOFTWARE !=null) {
//            try {
//                SET_LAYER_TYPE.invoke(webView, LAYER_TYPE_SOFTWARE.getInt(WebView.class), null);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
