package com.example.dingcore.puzzle;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
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
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Game2Activity extends AppCompatActivity {

    private PuzzleLayout2 puzzleLayout;
    private PopupWindow popupWindow;
    private View popupView;
    private TextView tv_Timer;
    private Timer timer;
    private static int timerIndex = 0;
    private TimerTask timerTask;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    timerIndex++;
                    tv_Timer.setText(timerIndex + "秒");
                    break;
                default:
                    break;
            }
        }
    };

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
        final int type = intent.getExtras().getInt("type",3);
        puzzleLayout = (PuzzleLayout2) findViewById(R.id.id_gameview2);
        if (id != 0) {
            puzzleLayout.setBitmap(id);
        } else if (imagePath != null){
            puzzleLayout.setBitmap(imagePath);
        } else {
            puzzleLayout.setBitmap(imageUri);
        }
        puzzleLayout.setColumn(type);
        tv_Timer = (TextView) findViewById(R.id.timer_2);
        tv_Timer.setText("0秒");
        timer = new Timer(true);
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        };
        //每1000ms执行 延迟0s
        timer.schedule(timerTask,0,1000);
        Button check = (Button) findViewById(R.id.check_success_2);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (puzzleLayout.checkSucess()) {
                    Toast.makeText(Game2Activity.this, "恭喜！游戏成功！", Toast.LENGTH_SHORT).show();
                    timer.cancel();
                    timerTask.cancel();
                    List<Score> scores = DataSupport.where("type = ? and mode = ?",String.valueOf(type),"2").find(Score.class);
                    if (scores.size() != 0) {
                        int time = scores.get(0).getTime();
                        if (time != 0 && time > timerIndex) {
                            Score score = new Score();
                            score.setTime(timerIndex);
                            score.updateAll("type = ? and mode = ?", String.valueOf(type), "2");
                        }
                    } else {
                        Score score = new Score();
                        score.setType(type);
                        score.setMode(2);
                        score.setTime(timerIndex);
                        score.save();
                    }
                    timerIndex = 0;
                    puzzleLayout.cleanConfig();
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

    @Override
    protected void onStop() {
        super.onStop();
        timer.cancel();
        timerTask.cancel();
        timerIndex = 0;
        puzzleLayout.cleanConfig();
    }
}
