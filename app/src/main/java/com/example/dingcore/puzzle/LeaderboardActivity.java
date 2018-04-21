package com.example.dingcore.puzzle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        TextView textView1_3 = (TextView) findViewById(R.id.mode1_type3);
        TextView textView1_4 = (TextView) findViewById(R.id.mode1_type4);
        TextView textView1_5 = (TextView) findViewById(R.id.mode1_type5);
        TextView textView2_3 = (TextView) findViewById(R.id.mode2_type3);
        TextView textView2_4 = (TextView) findViewById(R.id.mode2_type4);
        TextView textView2_5 = (TextView) findViewById(R.id.mode2_type5);
        List<Score> scores = new ArrayList<Score>();
        int time = 0;
        scores = DataSupport.where("type = ? and mode = ?","3","1").find(Score.class);
        if (scores.size() != 0) {
            time = scores.get(0).getTime();
            if (time != 0) {
                textView1_3.setText(time + "秒");
            }
        }
        scores = DataSupport.where("type = ? and mode = ?","4","1").find(Score.class);
        if (scores.size() != 0) {
            time = scores.get(0).getTime();
            if (time != 0) {
                textView1_4.setText(time + "秒");
            }
        }
        scores = DataSupport.where("type = ? and mode = ?","5","1").find(Score.class);
        if (scores.size() != 0) {
            time = scores.get(0).getTime();
            if (time != 0) {
                textView1_5.setText(time + "秒");
            }
        }
        scores = DataSupport.where("type = ? and mode = ?","3","2").find(Score.class);
        if (scores.size() != 0) {
            time = scores.get(0).getTime();
            if (time != 0) {
                textView2_3.setText(time + "秒");
            }
        }
        scores = DataSupport.where("type = ? and mode = ?","4","2").find(Score.class);
        if (scores.size() != 0) {
            time = scores.get(0).getTime();
            if (time != 0) {
                textView2_4.setText(time + "秒");
            }
        }
        scores = DataSupport.where("type = ? and mode = ?","5","2").find(Score.class);
        if (scores.size() != 0) {
            time = scores.get(0).getTime();
            if (time != 0) {
                textView2_5.setText(time + "秒");
            }
        }
        scores.clear();
    }
}
