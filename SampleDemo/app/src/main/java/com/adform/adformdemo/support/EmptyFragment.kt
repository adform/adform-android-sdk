package com.adform.adformdemo.support

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.adform.adformdemo.R

class EmptyFragment : Fragment() {
    private var text: String? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.fragment_empty, container, false) as ViewGroup
        if (savedInstanceState != null) text = savedInstanceState.getString(INSTANCE_TEXT)
        if (text != null) (rootView.findViewById<View>(R.id.text_view) as TextView).text = text
        return rootView
    }

    fun setText(text: String?) {
        this.text = text
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INSTANCE_TEXT, text)
    }

    companion object {
        const val INSTANCE_TEXT = "TEXT"
    }
}
