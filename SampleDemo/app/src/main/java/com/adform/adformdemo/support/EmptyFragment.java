package com.adform.adformdemo.support;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adform.adformdemo.R;

/**
 * Created by mariusmerkevicius on 4/30/15.
 */
public class EmptyFragment extends Fragment {
    public static final String INSTANCE_TEXT = "TEXT";
    private String text;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_empty, container, false);
        if (savedInstanceState != null)
            text = savedInstanceState.getString(INSTANCE_TEXT);
        if (text != null)
            ((TextView) rootView.findViewById(R.id.text_view)).setText(text);
        return rootView;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(INSTANCE_TEXT, text);
    }
}
