package com.example.dingcore.puzzle;

import org.litepal.crud.DataSupport;

/**
 * Created by DingCore on 2018/4/21.
 */

public class Score extends DataSupport {

    private int type;
    private int mode;
    private int time;
    private int steps;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    @Override
    public String toString() {
        return "Score{" +
                "type=" + type +
                ", mode=" + mode +
                ", time=" + time +
                ", steps=" + steps +
                '}';
    }
}
