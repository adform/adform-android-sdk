package com.adform.sample.app.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.adform.sample.app.R;
import com.adform.sample.app.adapters.TestAdapter3;

import java.util.ArrayList;

/**
 * Created by mariusm on 13/05/14.
 */
public class DemoFragment4 extends Fragment implements TestAdapter3.TestAdapterListener {

    private ListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final ArrayList<String> templateList = new ArrayList<String>();
        for (int i = 0; i < 200; ++i) {
            templateList.add(getString(R.string.temp_info));
        }

        final TestAdapter3 adapter = new TestAdapter3(getActivity(), templateList, this);
        mListView.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main3, null);
        mListView = (ListView) view.findViewById(R.id.list_view);
        return view;
    }

    @Override
    public int getCustomViewType(int position) {
        if (position == 5)
            return 1;
        return 0;
    }
}
