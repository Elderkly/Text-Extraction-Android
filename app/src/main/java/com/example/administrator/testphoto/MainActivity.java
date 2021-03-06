package com.example.administrator.testphoto;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.baidu.ocr.sdk.OCR;

import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.sdk.model.GeneralBasicParams;
import com.baidu.ocr.sdk.model.GeneralResult;
import com.baidu.ocr.sdk.model.WordSimple;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int TAKE_PHOTO=1;
    private static final int CHOOSE_PHOTO=2;
    private Uri imageUri;
    private ProgressBar loading;
    public static Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("??????");

        this.initOCR();
        this.initPermissions();
        this.initButtonListen();
    }

    //  ???????????????
    public void initPermissions() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }

    //  ?????????OCR??????
    public void initOCR() {
        // ?????????????????????
        OCR.getInstance(this).initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken accessToken) {
                Log.d("OCR", "accessToken:"+accessToken);
            }

            @Override
            public void onError(OCRError ocrError) {
                Log.e("OCR","ocrError:"+ocrError);
            }
        }, getApplicationContext());
    }

    //  ???????????????????????????
    public void initButtonListen() {
        loading = findViewById(R.id.loading);
        Button button=findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file=new File(getCacheDir(),"take_photo1.jpg");
                try{
                    if (file.exists()){
                        file.delete();
                    }
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT>=24){
                    imageUri=FileProvider.getUriForFile(MainActivity.this,"fileProvider",file);
                }else {
                    imageUri=Uri.fromFile(file);
                }
                Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,TAKE_PHOTO);
            }
        });
        Button button1=findViewById(R.id.button2);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,CHOOSE_PHOTO);
            }
        });
    }

    //  ??????????????????
    @Override
    public void onRequestPermissionsResult(int requestCode,String [] permissions,int[] grantResults){
        switch (requestCode){
            case 1:
                if (grantResults.length>0&&grantResults[0]!=PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"?????????????????????????????????",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    //  ?????????/??????????????? ????????????????????????
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case TAKE_PHOTO:
                if (resultCode==RESULT_OK){
                    try{
                        String filePath=new File(getCacheDir(),"take_photo1.jpg").getPath();
                        ExifInterface exifInterface = new ExifInterface(filePath);
                        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        Log.i(TAG, "onActivityResult: orientation "+orientation);

                        Bitmap bitmap=BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        Matrix matrix = new Matrix();
//                        matrix.postRotate(90);
                        Bitmap bitmap1=Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
                        this.getImgSuccess(bitmap1);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode==RESULT_OK){
                    if (Build.VERSION.SDK_INT>=19){
                        handleImageNow(data);
                    }else {
                        handleImageBefore(data);
                    }
                }
                break;
        }
    }

    //  ??????????????????
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void handleImageNow(Intent data){
        String imagePath=null;
        Uri uri=data.getData();
        if (DocumentsContract.isDocumentUri(this,uri)){
            String docId=DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id=docId.split(":")[1];
                String selection=MediaStore.Images.Media._ID+"="+id;
                imagePath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri=ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath=getImagePath(contentUri,null);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())){
            imagePath=getImagePath(uri,null);
        }else if ("file".equalsIgnoreCase(uri.getScheme())){
            imagePath=uri.getPath();
        }
        getImgSuccess(imagePath);

    }

    //  ????????????????????????
    public void handleImageBefore(Intent data){
        Uri uri=data.getData();
        String imagePath=getImagePath(uri,null);
        getImgSuccess(imagePath);
    }

    //  ??????????????????
    public String getImagePath(Uri uri,String selection){
        String path=null;
        Cursor cursor=getContentResolver().query(uri,null,selection,null,null);
        if (cursor!=null){
            if (cursor.moveToFirst()){
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    //  ??????????????????
    public void getImgSuccess(String imagePath){
        if (imagePath!=null){
            Bitmap bitmap=BitmapFactory.decodeFile(imagePath);
            this.ocrNormal(bitmap);
        }else {
            Log.i(TAG, "????????????????????????");
        }
    }
    public void getImgSuccess(Bitmap bitmap){
        //            Bitmap bitmap=BitmapFactory.decodeStream(getContentResolver().openInputStream(galleryUri));
        this.ocrNormal(bitmap);
    }

    //  ????????????
    private void ocrNormal(final Bitmap bitmap) {
        loading.setVisibility(View.VISIBLE);
        File file;
        // ??????????????????????????????
        GeneralBasicParams param = new GeneralBasicParams();
        param.setDetectDirection(true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(System.currentTimeMillis());
        String filename = format.format(date);
        file = new File(Environment.getExternalStorageDirectory(), filename + ".png");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            try {
                fos.write(baos.toByteArray());
                fos.flush();
                fos.close();
            } catch (IOException e) {

                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }

        param.setImageFile(file);
        final MainActivity SELF = this;
        // ??????????????????????????????
        OCR.getInstance(getApplication()).recognizeAccurateBasic(param, new OnResultListener<GeneralResult>() {
            @Override
            public void onResult(GeneralResult result) {
                StringBuilder sb = new StringBuilder();
                // ?????????????????????GeneralResult??????
                for (WordSimple wordSimple : result.getWordList()) {
                    WordSimple word = wordSimple;
                    sb.append(word.getWords());
                }
                String ocrResult = sb.toString();
                loading.setVisibility(View.INVISIBLE);
                System.out.println("*********????????????*********"+ocrResult);
                try {
                    SELF.Jump(result.getJsonRes(), ocrResult, bitmap);
                } catch (JSONException e) {
                    MainActivity.show(SELF,"????????????");
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(OCRError error) {
                System.out.println("*********????????????*********"+error.getMessage());
                loading.setVisibility(View.INVISIBLE);
                MainActivity.show(SELF,error.getMessage());
            }
        });
    }

    //  ?????????????????????Toast
    static Toast toast = null;
    public static void show(Context context, String text) {
        try {
            if(toast!=null){
                toast.setText(text);
            }else{
                toast= Toast.makeText(context, text, Toast.LENGTH_SHORT);
            }
            toast.show();
        } catch (Exception e) {
            //???????????????????????????Toast?????????????????????
            Looper.prepare();
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            Looper.loop();
        }
    }

    //  ?????????????????? ??????????????????
    private void Jump(String jsonRes, String strRes,Bitmap bitmap) throws JSONException {
        System.out.println("123123"+jsonRes);
        MainActivity.bitmap = bitmap;
        Intent intent = new Intent(MainActivity.this, com.example.administrator.testphoto.MainActivity2.class);
        JSONObject data = new JSONObject();
        data.put("jsonRes",jsonRes);
        //  intent?????????????????????????????? ????????????????????????
        data.put("strRes",strRes);
        data.put("bitMap","123");
        intent.putExtra("data", data.toString());
        startActivity(intent);
    }
}
