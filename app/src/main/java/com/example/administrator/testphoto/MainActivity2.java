package com.example.administrator.testphoto;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        this.initListView();

    }

    public void initListView() {
        ListView list = (ListView)findViewById(R.id.list_view);
        String data[] = {"注册号","核准时间","企业名称","经营范围以及经营期限","所有识别内容"};
        ArrayAdapter adapter1;
        adapter1 = new ArrayAdapter<String>(MainActivity2.this,android.R.layout.simple_list_item_1,data);
        list.setAdapter(adapter1);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String result = parent.getItemAtPosition(position).toString();//获取选择项的值
                Toast.makeText(MainActivity2.this,"您点击了"+result, Toast.LENGTH_SHORT).show();
            }
        });
    }
}