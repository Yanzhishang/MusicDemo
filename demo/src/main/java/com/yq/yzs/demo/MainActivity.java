package com.yq.yzs.demo;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {
    private Button btnPause, btnPlayUrl, btnStop;
    private SeekBar skbProgress;
    private Player player;
    private int first;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.setTitle("�������ֲ���---hellogv��д");

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

    class ClickEvent implements View.OnClickListener {

        public void onClick(View arg0) {

            player.setProgressBar(skbProgress);

            if (arg0 == btnPause) {// ������ͣ��ť

                if (player.isplaying())// ������ڲ�������ͣ
                {
                    player.pause();
                    btnPause.setText("����");
                } else// �����ͣ�򲥷�
                {
                    btnPause.setText("��ͣ");
                    if (first == 0)// ��ʼ����
                    {
                        String luyin_Path = Environment
                                .getExternalStorageDirectory()
                                .getAbsolutePath()
                                + "/" + "she.mp3";

                        player.playUrl(luyin_Path);
                    } else// ��������
                    {
                        player.play();
                    }
                }

                first++;

            } else if (arg0 == btnPlayUrl) {
                // // �ڰٶ�MP3�������������,��ҿ������Ա������
                // String url = "http://219.138.125.22/myweb/mp3/CMP3/JH19.MP3";
                // player.playUrl(url);

                String music_path = "http://stream19.qqmusic.qq.com/30974808.mp3";

                player.playUrl(music_path);

            } else if (arg0 == btnStop) {
                player.stop();
            }
        }
    }

    class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {
        int progress;

        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            // ԭ����(progress/seekBar.getMax())*player.mediaPlayer.getDuration()
            this.progress = progress * player.mediaPlayer.getDuration()
                    / seekBar.getMax();
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            // seekTo()�Ĳ����������ӰƬʱ������֣���������seekBar.getMax()��Ե�����
            player.mediaPlayer.seekTo(progress);
        }
    }

}
