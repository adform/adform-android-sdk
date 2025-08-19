package com.adform.adformdemo

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.adform.adformdemo.AdInlineLWActivity.TestAdapter.TestAdapterListener
import com.adform.sdk.pub.views.AdListViewItemBuilder
import com.adform.sdk.utils.AdSize
import java.util.ArrayList

/**
 * The most basic ad implementation. You pass in mandatory events and parameters,
 * start loading ad, and it handles everything else automatically.
 */
class AdInlineLWActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_adinline_lw)

        // Initializing data that will be used for items that are not ad type
        val templateList = ArrayList<String>()
        for (i in 0..199) templateList.add(MOCK_DATA)

        // Initializing adapter that will be used with the listview
        val adapter = TestAdapter(
            this, templateList,
            object : TestAdapterListener {
                override fun getCustomViewType(position: Int): Int {

                    // We define the adapter to display each 5th item to be an ad...
                    return if (position % 5 == 0) 1 else 0

                    // ...otherwise its a simple list item
                }

                override fun inflateCustomView(): View {

                    // Use builder to initialize ad view that will be used within the ListView
                    return AdListViewItemBuilder.init(
                        this@AdInlineLWActivity,
                        4016318,
                        AdSize(320, 50),
                        true
                    )
                }
            }
        )
        val listView = findViewById<View>(R.id.list_view) as ListView
        listView.adapter = adapter
    }

    /**
     * A basic test adapter that is used to display two kind of views, a mock TextView, and an ad View.
     */
    class TestAdapter(
        c: Context,
        private val values: List<String>,
        private val adapterListener: TestAdapterListener
    ) : ArrayAdapter<String?>(
        c, R.layout.lw_empty, values
    ) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val inflater = context
                .getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var rowView = convertView

            // Determines which type of view to use.
            if (getItemViewType(position) == 1) {

                // In this case we use AdView. If no instance is returned from scrap list, we initialize a new one
                if (rowView != null && rowView.tag !is ViewAdHolder || rowView == null) {
                    rowView = adapterListener.inflateCustomView()
                    val viewHolder = ViewAdHolder()
                    rowView.tag = viewHolder
                }
                val holder = rowView.tag as ViewAdHolder
            } else {

                // We initialize a simple view if no instance is returned from the scrap list.
                if (rowView != null && rowView.tag !is ViewTextHolder || rowView == null) {
                    rowView = inflater.inflate(R.layout.lw_empty, parent, false)
                    val viewHolder = ViewTextHolder()
                    viewHolder.text = rowView.findViewById<View>(R.id.text_view) as TextView

                    // We set a view holder as a tag for later reuse
                    rowView.tag = viewHolder
                }

                // Initialized anew or returned from scrap list we assign needed values.
                val holder = rowView!!.tag as ViewTextHolder
                holder.text!!.text = values[position]
            }
            return rowView
        }

        // Determines the case that defines which type of view should be used
        // In this case the interface returns the case from the outside
        override fun getItemViewType(position: Int): Int {
            return adapterListener.getCustomViewType(position)
        }

        // Determines different type of views to use
        // In this case we use 2 types, one for ad and one for simple view
        override fun getViewTypeCount(): Int {
            return 2
        }

        // Instance holder that is used when reusing the *same* view but assigning different values
        internal class ViewTextHolder {
            var text: TextView? = null
        }

        internal class ViewAdHolder

        //region Listeners
        interface TestAdapterListener {
            fun getCustomViewType(position: Int): Int
            fun inflateCustomView(): View
        } //endregion
    }

    companion object {
        const val MOCK_DATA =
            "Adform provides you with a digital advertising solution that unlocks your online potential. Our platform is simple and includes campaign planning, ad serving, optimisation, analytics, reports and much more. We have leading clients in all major industries and we are rapidly expanding our product palette and geographical presence. Today Adform delivers campaigns for over 3,062 clients across 7,542 global web publishers in more than 35 countries worldwide. To help you reach your digital advertising potential we always challenge the present in order to create tomorrow’s solutions."
    }
}
