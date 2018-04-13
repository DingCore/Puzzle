package com.example.dingcore.puzzle;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.PopupWindow;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChooseActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RESULT_IMAGE = 100;
    private static final int RESULT_CAMERA = 200;
    private Uri imageUri;
    private GridView pic_List;
    private List<Bitmap> picList;
    private int[] resPicId;
    private TextView typeChoose;
    private TextView modeChoose;
    private LayoutInflater layoutInflater;
    private PopupWindow popupWindow;
    private PopupWindow modeSelecter;
    private View popupView;
    private View popupView2;
    private TextView typeChoose2;
    private TextView typeChoose3;
    private TextView typeChoose4;
    private TextView modeChoose2;
    private TextView modeChoose3;
    private int type = 3;
    private int mode = 1;
    private String[] customItems = new String[]{"本地图册","相机拍照"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        picList = new ArrayList<Bitmap>();
        initViews();
        pic_List.setAdapter(new GridPicListAdapter(ChooseActivity.this,picList));
        pic_List.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == resPicId.length - 1) {
                    showDialogCustom();
                } else {
                    Intent intent;
                    if (mode == 1) {
                        intent = new Intent(ChooseActivity.this, GameActivity.class);
                        intent.putExtra("picChooseId",resPicId[position]);
                        intent.putExtra("type",type);
                        startActivity(intent);
                    } else if (mode == 2) {
                        intent = new Intent(ChooseActivity.this, Game2Activity.class);
                        intent.putExtra("picChooseId",resPicId[position]);
                        intent.putExtra("type",type);
                        startActivity(intent);
                    }
                }
            }
        });

        typeChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupShow(v);
            }
        });

        modeChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupShow2(v);
            }
        });
    }

    private void showDialogCustom() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChooseActivity.this);
        builder.setTitle("选择：");
        builder.setItems(customItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (0 == which) {
                    //动态申请SD卡读取权限
                    if (ContextCompat.checkSelfPermission(ChooseActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(ChooseActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                    } else {
                        Intent intent = new Intent("android.intent.action.GET_CONTENT");
                        intent.setType("image/*");
                        startActivityForResult(intent, RESULT_IMAGE);
                    }
                } else if (1 == which) {
                    File outputImage = new File(getExternalCacheDir(),"output_image.jpg");
                    try {
                        if (outputImage.exists()) {
                            outputImage.delete();
                        }
                        outputImage.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (Build.VERSION.SDK_INT >= 24) {
                        imageUri = FileProvider.getUriForFile(ChooseActivity.this,"com.example.dingcore.puzzle.fileprovider",outputImage);
                    } else {
                        imageUri = Uri.fromFile(outputImage);
                    }
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                    startActivityForResult(intent,RESULT_CAMERA);
                }
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RESULT_IMAGE && data != null) {
                String imagePath = null;
                Uri uri = data.getData();
                if(Build.VERSION.SDK_INT >= 19) {
                    imagePath = handleImage1(uri);
                } else {
                    imagePath = handleImage2(uri);
                }
                if (mode == 1) {
                    Intent intent = new Intent(ChooseActivity.this, GameActivity.class);
                    intent.putExtra("picPath", imagePath);
                    intent.putExtra("type", type);
                    startActivity(intent);
                } else if (mode == 2) {
                    Intent intent = new Intent(ChooseActivity.this, Game2Activity.class);
                    intent.putExtra("picPath", imagePath);
                    intent.putExtra("type",type);
                    startActivity(intent);
                }
            } else if (requestCode == RESULT_CAMERA) {
                if (mode == 1) {
                    Intent intent = new Intent(ChooseActivity.this, GameActivity.class);
                    intent.putExtra("photoPath", imageUri.toString());
                    intent.putExtra("type", type);
                    startActivity(intent);
                } else if (mode == 2) {
                    Intent intent = new Intent(ChooseActivity.this, Game2Activity.class);
                    intent.putExtra("photoPath", imageUri.toString());
                    intent.putExtra("type",type);
                    startActivity(intent);
                }
            }
        }
    }

    private String handleImage1(Uri uri) {
        String imagePath = null;
        if (DocumentsContract.isDocumentUri(this,uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath = getImagePath(uri,null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        }
        return imagePath;
    }

    private String handleImage2(Uri uri) {
        String imagePath = getImagePath(uri,null);
        return imagePath;
    }

    private String getImagePath(Uri uri,String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if(cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return  path;
    }

    private void popupShow(View view) {
        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setBackgroundAlpha(0.5f);
        popupWindow.setFocusable(true); //可以获取焦点
        popupWindow.setOutsideTouchable(true); //响应touch事件
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAtLocation(view, Gravity.BOTTOM | Gravity.LEFT,0,0); //显示在跟布局的底部
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(1.0f);
            }
        });
    }

    private void popupShow2(View view) {
        modeSelecter = new PopupWindow(popupView2, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setBackgroundAlpha(0.5f);
        modeSelecter.setFocusable(true); //可以获取焦点
        modeSelecter.setOutsideTouchable(true); //响应touch事件
        modeSelecter.setBackgroundDrawable(new BitmapDrawable());
        modeSelecter.showAtLocation(view, Gravity.BOTTOM | Gravity.LEFT,0,0); //显示在跟布局的底部
        modeSelecter.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(1.0f);
            }
        });
    }

    public void setBackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha;
        getWindow().setAttributes(lp);
    }

    private void initViews() {
        pic_List = (GridView) findViewById(R.id.puzzle_pic_list);
        resPicId = new int[] {R.drawable.pic1,R.drawable.pic2,R.drawable.pic3,R.drawable.pic4,R.drawable.pic5,
                R.drawable.pic6,R.drawable.pic7,R.drawable.pic8,R.drawable.pic9,R.drawable.plus};
        Bitmap[] bitmaps = new Bitmap[resPicId.length];
        for (int i = 0;i < bitmaps.length;i++) {
            bitmaps[i] = BitmapFactory.decodeResource(getResources(),resPicId[i]);
            picList.add(bitmaps[i]);
        }
        typeChoose = (TextView) findViewById(R.id.type_choose);
        modeChoose = (TextView) findViewById(R.id.mode_choose);
        layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = layoutInflater.inflate(R.layout.popupwindow,null);
        popupView2 = layoutInflater.inflate(R.layout.modeselecter,null);
        typeChoose2 = (TextView) popupView.findViewById(R.id.type_choose_2);
        typeChoose3 = (TextView) popupView.findViewById(R.id.type_choose_3);
        typeChoose4 = (TextView) popupView.findViewById(R.id.type_choose_4);
        modeChoose2 = (TextView) popupView2.findViewById(R.id.mode_choose_2);
        modeChoose3 = (TextView) popupView2.findViewById(R.id.mode_choose_3);
        typeChoose2.setOnClickListener(this);
        typeChoose3.setOnClickListener(this);
        typeChoose4.setOnClickListener(this);
        modeChoose2.setOnClickListener(this);
        modeChoose3.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.type_choose_2:
                type = 3;
                typeChoose.setText("3 X 3");
                popupWindow.dismiss();
                break;
            case R.id.type_choose_3:
                type = 4;
                typeChoose.setText("4 X 4");
                popupWindow.dismiss();
                break;
            case R.id.type_choose_4:
                type = 5;
                typeChoose.setText("5 X 5");
                popupWindow.dismiss();
                break;
            case R.id.mode_choose_2:
                mode = 1;
                modeChoose.setText("简易模式");
                modeSelecter.dismiss();
                break;
            case R.id.mode_choose_3:
                mode = 2;
                modeChoose.setText("经典模式");
                modeSelecter.dismiss();
                break;
            default:
                break;
        }
    }
}
