package com.example.dingcore.puzzle;

import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {

    private SoundPool sp;
    private int music;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        sp = new SoundPool(10, AudioManager.STREAM_MUSIC,5);
        music = sp.load(this,R.raw.click,1);
        final TextView textView1_3 = (TextView) findViewById(R.id.mode1_type3);
        final TextView textView1_4 = (TextView) findViewById(R.id.mode1_type4);
        final TextView textView1_5 = (TextView) findViewById(R.id.mode1_type5);
        final TextView textView2_3 = (TextView) findViewById(R.id.mode2_type3);
        final TextView textView2_4 = (TextView) findViewById(R.id.mode2_type4);
        final TextView textView2_5 = (TextView) findViewById(R.id.mode2_type5);
        Button clearData = (Button) findViewById(R.id.clear_data);
        List<Score> scores = new ArrayList<Score>();
        int time = 0;
        int step = 0;
        scores = DataSupport.where("type = ? and mode = ?","3","1").find(Score.class);
        if (scores.size() != 0) {
            time = scores.get(0).getTime();
            step = scores.get(0).getSteps();
            if (time != 0) {
                textView1_3.setText(time + "秒," + step + "步");
            }
        }
        scores = DataSupport.where("type = ? and mode = ?","4","1").find(Score.class);
        if (scores.size() != 0) {
            time = scores.get(0).getTime();
            step = scores.get(0).getSteps();
            if (time != 0) {
                textView1_4.setText(time + "秒," + step + "步");
            }
        }
        scores = DataSupport.where("type = ? and mode = ?","5","1").find(Score.class);
        if (scores.size() != 0) {
            time = scores.get(0).getTime();
            step = scores.get(0).getSteps();
            if (time != 0) {
                textView1_5.setText(time + "秒," + step + "步");
            }
        }
        scores = DataSupport.where("type = ? and mode = ?","3","2").find(Score.class);
        if (scores.size() != 0) {
            time = scores.get(0).getTime();
            step = scores.get(0).getSteps();
            if (time != 0) {
                textView2_3.setText(time + "秒," + step + "步");
            }
        }
        scores = DataSupport.where("type = ? and mode = ?","4","2").find(Score.class);
        if (scores.size() != 0) {
            time = scores.get(0).getTime();
            step = scores.get(0).getSteps();
            if (time != 0) {
                textView2_4.setText(time + "秒," + step + "步");
            }
        }
        scores = DataSupport.where("type = ? and mode = ?","5","2").find(Score.class);
        if (scores.size() != 0) {
            time = scores.get(0).getTime();
            step = scores.get(0).getSteps();
            if (time != 0) {
                textView2_5.setText(time + "秒," + step + "步");
            }
        }
        scores.clear();
        clearData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.play(music,1,1,0,0,1);
                DataSupport.deleteAll(Score.class);
                textView1_3.setText("暂无");
                textView1_4.setText("暂无");
                textView1_5.setText("暂无");
                textView2_3.setText("暂无");
                textView2_4.setText("暂无");
                textView2_5.setText("暂无");
            }
        });
    }
}
