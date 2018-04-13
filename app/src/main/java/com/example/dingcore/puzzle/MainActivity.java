package com.example.dingcore.puzzle;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private PopupWindow popupWindow;
    private View popupView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button1 = (Button) findViewById(R.id.button_start);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ChooseActivity.class);
                startActivity(intent);
            }
        });
        Button button2 = (Button) findViewById(R.id.button_help);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                popupView = layoutInflater.inflate(R.layout.gamehelper,null);
                popupShow(v);
            }
        });
        Button button3 = (Button) findViewById(R.id.button_quit);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void popupShow(View view) {
        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setBackgroundAlpha(0.3f);
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

    public void setBackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha;
        getWindow().setAttributes(lp);
    }
}
