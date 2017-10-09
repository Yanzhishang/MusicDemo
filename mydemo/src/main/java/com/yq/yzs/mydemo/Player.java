package com.yq.yzs.mydemo;

import android.os.Handler;
import android.os.Message;
import android.widget.SeekBar;

import java.util.Timer;
import java.util.TimerTask;

public class Player{
    private SeekBar     skbProgress;
    private Timer mTimer = new Timer();

    public Player() {

        mTimer.schedule(mTimerTask, 0, 1000);
    }


    /**
     * 通过定时器和Handler来更新进度条
     */
    TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
                handleProgress.sendEmptyMessage(0);

        }
    };

    //处理消息
    Handler handleProgress = new Handler() {
        public void handleMessage(Message msg) {

        }
    };

}