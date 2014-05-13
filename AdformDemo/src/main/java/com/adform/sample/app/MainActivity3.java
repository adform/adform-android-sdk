package com.adform.sample.app;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity3 extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        final ListView listView = (ListView) findViewById(R.id.list_view);
        final ArrayList<String> templateList = new ArrayList<String>();
        for (int i = 0; i < 200; ++i) {
            templateList.add("Template value #" + i);
        }

        final TestAdapter adapter = new TestAdapter(this, templateList);
        listView.setAdapter(adapter);
    }

    private class TestAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final ArrayList<String> values;

        public TestAdapter(Context context, ArrayList<String> values) {
            super(context, R.layout.lw_layout_1_3, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView;
            if (position == 20) {
                rowView = inflater.inflate(R.layout.lw_layout_1_3, parent, false);
            } else {
                rowView = inflater.inflate(R.layout.lw_layout_2_3, parent, false);
                TextView textView = (TextView) rowView.findViewById(R.id.text_view);
                textView.setText(values.get(position));
            }

            return rowView;
        }
    }

}
