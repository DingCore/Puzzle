package com.example.dingcore.puzzle;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import org.litepal.crud.DataSupport;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {

    private PuzzleLayout puzzleLayout;
    private PopupWindow popupWindow;
    private View popupView;
    private TextView steps;
    private TextView tv_Timer;
    private Timer timer;
    private static int timerIndex = 0;
    private static int stepIndex = 0;
    private TimerTask timerTask;
    private SoundPool sp;
    private int music;
    //UI更新handler
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
        setContentView(R.layout.activity_game);
        sp = new SoundPool(10, AudioManager.STREAM_MUSIC,5);
        music = sp.load(this,R.raw.click,1);
        Intent intent = getIntent();
        int id = intent.getExtras().getInt("picChooseId");
        String imagePath = intent.getExtras().getString("picPath");
        String photoPath = intent.getExtras().getString("photoPath");
        Uri imageUri = null;
        if(photoPath != null) {
            imageUri = Uri.parse(photoPath);
        }
        final int type = intent.getExtras().getInt("type",3);
        puzzleLayout = (PuzzleLayout) findViewById(R.id.id_gameview);
        if (id != 0) {
            puzzleLayout.setBitmap(id);
        } else if (imagePath != null){
            puzzleLayout.setBitmap(imagePath);
        } else {
            puzzleLayout.setBitmap(imageUri);
        }
        puzzleLayout.setColumn(type);
        tv_Timer = (TextView) findViewById(R.id.timer);
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
        steps = (TextView) findViewById(R.id.steps);
        steps.setText("0步");
        puzzleLayout.setCallBack(new CallBack() {
            @Override
            public void postExec() {
                stepIndex++;
                steps.setText(stepIndex + "步");
            }
        });
        Button check = (Button) findViewById(R.id.check_success);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.play(music,1,1,0,0,1);
                if (puzzleLayout.checkSucess()) {
                    Toast.makeText(GameActivity.this, "恭喜！游戏成功！", Toast.LENGTH_SHORT).show();
                    timer.cancel();
                    timerTask.cancel();
                    List<Score> scores = DataSupport.where("type = ? and mode = ?",String.valueOf(type),"1").find(Score.class);
                    if(scores.size() != 0) {
                        int time = scores.get(0).getTime();
                        int step = scores.get(0).getSteps();
                        if (time != 0 && step != 0 && (time + step) > (timerIndex + stepIndex)) {
                            Score score = new Score();
                            score.setTime(timerIndex);
                            score.setSteps(stepIndex);
                            score.updateAll("type = ? and mode = ?", String.valueOf(type), "1");
                        }
                    } else {
                        Score score = new Score();
                        score.setType(type);
                        score.setMode(1);
                        score.setTime(timerIndex);
                        score.setSteps(stepIndex);
                        score.save();
                    }
                    timerIndex = 0;
                    stepIndex = 0;
                    puzzleLayout.cleanConfig();
                    finish();
                } else {
                    Toast.makeText(GameActivity.this, "对不起，请继续努力！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button preview = (Button) findViewById(R.id.preview);
        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.play(music,1,1,0,0,1);
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
        stepIndex = 0;
        puzzleLayout.cleanConfig();
    }
}
