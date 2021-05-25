package com.example.administrator.testphoto;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.testphoto.LongClickUtils;

public class MainActivity3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        this.initText();
        this.initListen();
        setTitle("详细信息");
    }

    public void initText() {
        TextView title = (TextView) findViewById(R.id.textView);
        TextView text = (TextView) findViewById(R.id.textView2);
        Intent intent = getIntent();
        String[] data = intent.getStringArrayExtra("data");
        title.setText(data[0]);
        text.setText(data[1]);
    }

    public void initListen() {
        LinearLayout View = (LinearLayout) findViewById(R.id.viewBox);
        LongClickUtils.setLongClick(new Handler(), View, 2000, new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                finish();
                return true;
            }
        });
    }
}