package com.example.dingcore.puzzle;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

public class Game2Activity extends AppCompatActivity {

    private PopupWindow popupWindow;
    private View popupView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game2);
        Intent intent = getIntent();
        int id = intent.getExtras().getInt("picChooseId");
        String imagePath = intent.getExtras().getString("picPath");
        String photoPath = intent.getExtras().getString("photoPath");
        Uri imageUri = null;
        if(photoPath != null) {
            imageUri = Uri.parse(photoPath);
        }
        int type = intent.getExtras().getInt("type",3);
        final PuzzleLayout2 puzzleLayout = (PuzzleLayout2) findViewById(R.id.id_gameview2);
        if (id != 0) {
            puzzleLayout.setBitmap(id);
        } else if (imagePath != null){
            puzzleLayout.setBitmap(imagePath);
        } else {
            puzzleLayout.setBitmap(imageUri);
        }
        puzzleLayout.setColumn(type);
        Button check = (Button) findViewById(R.id.check_success_2);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (puzzleLayout.checkSucess()) {
                    Toast.makeText(Game2Activity.this, "恭喜！游戏成功！", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(Game2Activity.this, "对不起，请继续努力！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button preview = (Button) findViewById(R.id.preview_2);
        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                popupView = layoutInflater.inflate(R.layout.imagepreview,null);
                ImageView imageView = (ImageView) popupView.findViewById(R.id.image_preview);
                imageView.setImageBitmap(puzzleLayout.getBitmap());
                popupShow(v);
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
