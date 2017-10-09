package com.yq.yzs.mydemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;

public class TestMedia extends Activity {
    private Button btnPause, btnPlayUrl, btnStop;
    private SeekBar skbProgress;
    private Player  player;
    private int     first;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("在线音乐播放---hellogv编写");

        btnPlayUrl.setOnClickListener(new ClickEvent());

        btnPause = (Button) this.findViewById(R.id.btnPause);
        btnPause.setOnClickListener(new ClickEvent());

        btnStop = (Button) this.findViewById(R.id.btnStop);
        btnStop.setOnClickListener(new ClickEvent());

        skbProgress = (SeekBar) this.findViewById(R.id.skbProgress);
        skbProgress.setOnSeekBarChangeListener(new SeekBarChangeEvent());
        player = new Player();

    }

    class ClickEvent implements OnClickListener {

        public void onClick(View arg0) {

//            player.setProgressBar(skbProgress);/

        }
    }

    class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {
        int progress;

        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
//            // 原本是(progress/seekBar.getMax())*player.mediaPlayer.getDuration()
//            this.progress = progress * player.mediaPlayer.getDuration()
//                    / seekBar.getMax();
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            // seekTo()的参数是相对与影片时间的数字，而不是与seekBar.getMax()相对的数字
//            player.mediaPlayer.seekTo(progress);
        }
    }

}
