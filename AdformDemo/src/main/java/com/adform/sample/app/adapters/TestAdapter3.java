package com.adform.sample.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.adform.sample.app.R;
import com.adform.sdk2.utils.Utils;

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
        if (getItemViewType(position) == 1) {
            if ((rowView != null && !(rowView.getTag() instanceof ViewAdHolder)) || rowView == null) {
                rowView = inflater.inflate(R.layout.lw_layout_2_3, parent, false);
                ViewAdHolder viewHolder = new ViewAdHolder();
                rowView.setTag(viewHolder);
            }
            ViewAdHolder holder = (ViewAdHolder) rowView.getTag();
        } else {
            if ((rowView != null && !(rowView.getTag() instanceof ViewTextHolder)) || rowView == null) {
                rowView = inflater.inflate(R.layout.lw_layout_1_3, parent, false);
                ViewTextHolder viewHolder = new ViewTextHolder();
                viewHolder.text = (TextView) rowView.findViewById(R.id.text_view);
                rowView.setTag(viewHolder);
            }
            ViewTextHolder holder = (ViewTextHolder) rowView.getTag();
            holder.text.setText(values.get(position));
        }

        return rowView;
    }


    @Override
    public int getItemViewType(int position) {
        return mTestAdapterListener.getCustomViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    static class ViewTextHolder {
        public TextView text;
    }

    static class ViewAdHolder {}
}

