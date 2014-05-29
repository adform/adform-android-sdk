package com.adform.sample.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.adform.sample.app.R;

import java.util.ArrayList;

/**
 * Created by mariusm on 13/05/14.
 */
public class TestAdapter3 extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> values;
    private final TestAdapterListener mTestAdapterListener;

    public interface TestAdapterListener {
        public int getCustomViewType(int position);
    }

    public TestAdapter3(Context context, ArrayList<String> values, TestAdapterListener l) {
        super(context, R.layout.lw_layout_1_3, values);
        this.context = context;
        this.values = values;
        this.mTestAdapterListener = l;
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
                rowView = inflater.inflate(R.layout.lw_layout_2_3, parent, false);
                ViewAdHolder viewHolder = new ViewAdHolder();
                rowView.setTag(viewHolder);
            }
            ViewAdHolder holder = (ViewAdHolder) rowView.getTag();
        } else {

            // We initialize a simple view if no instance is returned from the scrap list.
            if ((rowView != null && !(rowView.getTag() instanceof ViewTextHolder)) || rowView == null) {
                rowView = inflater.inflate(R.layout.lw_layout_1_3, parent, false);
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
        return mTestAdapterListener.getCustomViewType(position);
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

    static class ViewAdHolder {}
}

