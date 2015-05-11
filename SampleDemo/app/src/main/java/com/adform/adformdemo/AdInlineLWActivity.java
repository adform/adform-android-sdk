package com.adform.adformdemo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.adform.sdk.pub.views.AdListViewItemBuilder;
import com.adform.sdk.utils.AdSize;

import java.util.ArrayList;

/**
 * The most basic ad implementation. You pass in mandatory events and parameters,
 * start loading ad, and it handles everything else automatically.
 */
public class AdInlineLWActivity extends Activity {
    public static final String MOCK_DATA = "Adform provides you with a digital advertising solution that unlocks your online potential. Our platform is simple and includes campaign planning, ad serving, optimisation, analytics, reports and much more. We have leading clients in all major industries and we are rapidly expanding our product palette and geographical presence. Today Adform delivers campaigns for over 3,062 clients across 7,542 global web publishers in more than 35 countries worldwide. To help you reach your digital advertising potential we always challenge the present in order to create tomorrow’s solutions.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adinline_lw);

        // Initializing data that will be used for items that are not ad type
        final ArrayList<String> templateList = new ArrayList<String>();
        for (int i = 0; i < 200; ++i)
            templateList.add(MOCK_DATA);

        // Initializing adapter that will be used with the listview
        TestAdapter adapter = new TestAdapter(this, templateList, new TestAdapter.TestAdapterListener() {
            @Override
            public int getCustomViewType(int position) {

                // We define the adapter to display each 5th item to be an ad...
                if (position % 5 == 0)
                    return 1;

                // ...otherwise its a simple list item
                return 0;
            }

            @Override
            public View inflateCustomView() {

                // Use builder to initialize ad view that will be used within the ListView
                return AdListViewItemBuilder.init(AdInlineLWActivity.this, 4016318, new AdSize(320, 50), true);
            }
        });

        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
    }

    /**
     * A basic test adapter that is used to display two kind of views, a mock TextView, and an ad View.
     */
    public static class TestAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final ArrayList<String> values;
        private final TestAdapterListener adapterListener;

        public TestAdapter(Context context, ArrayList<String> values, TestAdapterListener l) {
            super(context, R.layout.lw_empty, values);
            this.context = context;
            this.values = values;
            this.adapterListener = l;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = convertView;

            // Determines which type of view to use.
            if (getItemViewType(position) == 1) {

                // In this case we use AdView. If no instance is returned from scrap list, we initialize a new one
                if ((rowView != null && !(rowView.getTag() instanceof ViewAdHolder)) || rowView == null) {
                    rowView = adapterListener.inflateCustomView();
                    ViewAdHolder viewHolder = new ViewAdHolder();
                    rowView.setTag(viewHolder);
                }
                ViewAdHolder holder = (ViewAdHolder) rowView.getTag();
            } else {

                // We initialize a simple view if no instance is returned from the scrap list.
                if ((rowView != null && !(rowView.getTag() instanceof ViewTextHolder)) || rowView == null) {
                    rowView = inflater.inflate(R.layout.lw_empty, parent, false);
                    ViewTextHolder viewHolder = new ViewTextHolder();
                    viewHolder.text = (TextView) rowView.findViewById(R.id.text_view);

                    // We set a view holder as a tag for later reuse
                    rowView.setTag(viewHolder);
                }

                // Initialized anew or returned from scrap list we assign needed values.
                ViewTextHolder holder = (ViewTextHolder) rowView.getTag();
                holder.text.setText(values.get(position));
            }

            return rowView;
        }

        // Determines the case that defines which type of view should be used
        // In this case the interface returns the case from the outside
        @Override
        public int getItemViewType(int position) {
            return adapterListener.getCustomViewType(position);
        }

        // Determines different type of views to use
        // In this case we use 2 types, one for ad and one for simple view
        @Override
        public int getViewTypeCount() {
            return 2;
        }

        // Instance holder that is used when reusing the *same* view but assigning different values
        static class ViewTextHolder {
            public TextView text;
        }

        static class ViewAdHolder {
        }

        //region Listeners

        public interface TestAdapterListener {
            public int getCustomViewType(int position);

            public View inflateCustomView();
        }

        //endregion

    }

}
