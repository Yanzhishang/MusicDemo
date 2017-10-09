package com.yq.yzs.demo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
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
        setContentView(R.layout.main);
        this.setTitle("在线音乐播放---hellogv编写");

        btnPlayUrl = (Button) this.findViewById(R.id.btnPlayUrl);
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

            player.setProgressBar(skbProgress);

            if (arg0 == btnPause) {// 播放暂停按钮

                if (player.isplaying())// 如果正在播放则暂停
                {
                    player.pause();
                    btnPause.setText("播放");
                } else// 如果暂停则播放
                {
                    btnPause.setText("暂停");
                    if (first == 0)// 初始播放
                    {
                        String luyin_Path = Environment
                                .getExternalStorageDirectory()
                                .getAbsolutePath()
                                + "/" + "she.mp3";

                        player.playUrl(luyin_Path);
                    } else// 继续播放
                    {
                        player.play();
                    }
                }

                first++;

            } else if (arg0 == btnPlayUrl) {
                // // 在百度MP3里随便搜索到的,大家可以试试别的链接
                 String url = "http://stdj2.60dj.com/geshou/邓紫棋/01/邓紫棋 - 泡沫 - Dj.mp3";
                // player.playUrl(url);

//                String music_path = "http://stream19.qqmusic.qq.com/30974808.mp3";

                player.playUrl(url);

            } else if (arg0 == btnStop) {
                player.stop();
            }
        }
    }

    class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {
        int progress;

        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            // 原本是(progress/seekBar.getMax())*player.mediaPlayer.getDuration()
            this.progress = progress * player.mediaPlayer.getDuration()
                    / seekBar.getMax();
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            // seekTo()的参数是相对与影片时间的数字，而不是与seekBar.getMax()相对的数字
            player.mediaPlayer.seekTo(progress);

        }
    }

}
