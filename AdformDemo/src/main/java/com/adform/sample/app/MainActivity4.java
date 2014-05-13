package com.adform.sample.app;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import com.adform.sample.app.adapters.TestAdapter;
import com.adform.sample.app.adapters.TestAdapter2;
import com.adform.sample.app.adapters.TestAdapter3;

import java.util.ArrayList;

public class MainActivity4 extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        final ListView listView = (ListView) findViewById(R.id.list_view);
        final ArrayList<String> templateList = new ArrayList<String>();
        for (int i = 0; i < 200; ++i) {
            templateList.add(getString(R.string.temp_info));
        }

        final TestAdapter3 adapter = new TestAdapter3(this, templateList);
        listView.setAdapter(adapter);
    }


}
