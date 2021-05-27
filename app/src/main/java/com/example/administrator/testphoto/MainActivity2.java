package com.example.administrator.testphoto;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity2 extends AppCompatActivity {
    private final JSONObject data = new JSONObject();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        this.initListView();
        setTitle("企业信息");
        try{
            Intent intent = getIntent();
            String stringData = intent.getStringExtra("data");
            System.out.println("详情页面收到的数据");
            try {
                JSONObject result = new JSONObject(stringData);
                //取数据
                JSONArray words_array = result.getJSONArray("words_result");
                System.out.println(result);
                System.out.println(words_array);
                String FM = null;
                for (int i = 0; i < words_array.length(); i++) {
                    //通过角标获取"数组"的对象
                    JSONObject jsonObject = words_array.getJSONObject(i);
                    //通过调用getString("划红线部分的键名")方法获取想要的数据
                    String str_addr = jsonObject.getString("words");
                    if (FM != null){
                        if (str_addr.contains("登记") || str_addr.contains("核准")) {
                            this.data.put("经营范围",FM);
                            FM = null;
                        } else {
                            FM += str_addr;
                        }
                    } else {
                        if (str_addr.contains("注册号")) {
                            this.data.put("注册号",str_addr);
                        } else if (str_addr.contains("核准时间")) {
                            this.data.put("核准时间",str_addr);
                        } else if (str_addr.contains("企业名称")) {
                            this.data.put("企业名称",str_addr);
                        } else if (str_addr.contains("经营")) {
                            FM = " ";
                        } else if (str_addr.contains("期限")) {
                            this.data.put("经营期限",str_addr);
                        }
                    }
                    //打开Logcat,查看是是否得到想要获取的数据
                Log.d("logcity", str_addr);
                }
                System.out.println("分组数据");
                System.out.println(this.data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (Error e) {
            e.printStackTrace();
        }
    }

    public void initListView() {
        ListView list = (ListView)findViewById(R.id.list_view);
        String data[] = {"注册号","核准时间","企业名称","经营范围","经营期限"};
        ArrayAdapter adapter1;
        adapter1 = new ArrayAdapter<String>(MainActivity2.this,android.R.layout.simple_list_item_1,data);
        list.setAdapter(adapter1);
        final MainActivity2 self = this;
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String result = parent.getItemAtPosition(position).toString();//获取选择项的值
            Toast.makeText(MainActivity2.this,"您点击了"+result, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity2.this, com.example.administrator.testphoto.MainActivity3.class);
                String[] data = new String[0];
                try {
                    data = new String[]{result,self.data.getString(result)};
                    intent.putExtra("data", data);
                    startActivity(intent);
                } catch (JSONException e) {
                    intent.putExtra("data",  new String[]{result,"空"});
                    startActivity(intent);
                    e.printStackTrace();
                }
            }
        });
    }
}