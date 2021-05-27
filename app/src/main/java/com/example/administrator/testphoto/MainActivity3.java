package com.example.administrator.testphoto;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.testphoto.LongClickUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        this.initListen();
        setTitle("详细信息");
        try {
            this.initText();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void initText() throws JSONException {
        TextView title = (TextView) findViewById(R.id.textView);
        TextView text = (TextView) findViewById(R.id.textView2);
        Intent intent = getIntent();
        String[] data = intent.getStringArrayExtra("data");
        if (data[0].equals("所有数据")){
            System.out.println(data);
            JSONObject details = new JSONObject(data[1]);
            text.setText(details.getString("strRes"));
            ImageView img = (ImageView) findViewById(R.id.detailsImg);
            img.setImageBitmap(MainActivity.bitmap);
        } else {
            text.setText(data[1]);
        }
        title.setText(data[0]);

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