package com.yq.yzs.demoseekbar;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class MainActivity extends Activity {

    private SeekBar seekbar = null;
    private String startTimeStr = "00:00:00";
    private String endTimeStr = "23:59:59";
    private TextView text, startTime, endTime;
    private ViewGroup.LayoutParams layoutParams;
    private float moveStep = 0; // 托动条的移动步调

    private SeekBar speed_seekbar = null;

    private Button dateBtn = null;
    private Button timeBtn = null;
    private final static int DATE_DIALOG = 0;
    private final static int TIME_DIALOG = 1;
    private Button start_btn=null;

    private boolean key = true;
    private MyThread thread;
    private int progressTemp = 0;
    public Handler handler;
    private int timeData=100;
    private int speedProgressTemp=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = new TextView(this);
        text.setTextSize(16);
        text.layout(0, 20, screenWidth, 80);
        /**
         * findView
         */
        seekbar = (SeekBar) findViewById(R.id.seekbar);
        speed_seekbar = (SeekBar) findViewById(R.id.speed_seekbar);
        startTime = (TextView) findViewById(R.id.start_time);
        endTime = (TextView) findViewById(R.id.end_time);
        choice_speed_text = (TextView) findViewById(R.id.choice_speed_text);
        dateBtn = (Button) findViewById(R.id.dateBtn);
        timeBtn = (Button) findViewById(R.id.timeBtn);
        start_btn = (Button) findViewById(R.id.start_btn);
        searchVideos();

        /**
         * setListener
         */
        seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener());
        speed_seekbar.setOnSeekBarChangeListener(new OnSpeedSeekBarChangeListener());

        dateBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("deprecation")
            public void onClick(View v) {
                showDialog(DATE_DIALOG);
            }
        });
        timeBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("deprecation")
            public void onClick(View v) {
                showDialog(TIME_DIALOG);
            }
        });
        start_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(start_btn.getText().toString().equals("开始")){
                    key = true;
                    thread = new MyThread();
                    thread.start();
                    start_btn.setText("暂停");
                }else{
                    key = false;
                    start_btn.setText("开始");
                }


            }
        });
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x1001) {
                    text.layout((int) (progressTemp * moveStep), 20,
                            screenWidth, 80);
                    text.setText(getCheckTimeBySeconds(progressTemp,
                            startTimeStr));
                    seekbar.setProgress(progressTemp);
                }
            }
        };

    }

    public void searchVideos() {
        startTime.setText(startTimeStr);
        endTime.setText(endTimeStr);
        text.setText(startTimeStr);
        totalSeconds = totalSeconds(startTimeStr, endTimeStr);
        seekbar.setEnabled(true);
        seekbar.setMax(totalSeconds);
        seekbar.setProgress(0);
        speed_seekbar.setEnabled(true);
        speed_seekbar.setMax(90);
        speed_seekbar.setProgress(0);
        choice_speed_text.setText(10+"");
        moveStep = (float) (((float) screenWidth / (float) totalSeconds) * 0.8);

    }

    private class OnSeekBarChangeListener implements
            SeekBar.OnSeekBarChangeListener {

        // //当进度改变时，参数fromUser判断是不是进度的改变由用户手动引起
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            progressTemp = progress;
        }

        // 表示进度条刚开始拖动，开始拖动时候触发的操作
        public void onStartTrackingTouch(SeekBar seekBar) {
            key = false;
            start_btn.setText("开始");
        }

        // 停止拖动时候
        public void onStopTrackingTouch(SeekBar seekBar) {
            key = true;
            thread = new MyThread();
            thread.start();
            start_btn.setText("暂停");
        }
    }

    private class OnSpeedSeekBarChangeListener implements
            SeekBar.OnSeekBarChangeListener {

        // //当进度改变时，参数fromUser判断是不是进度的改变由用户手动引起
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            speed_seekbar.setProgress(progress);
            choice_speed_text.setText((progress+10)+"");
            speedProgressTemp=1000/(progress+10);
        }

        // 表示进度条刚开始拖动，开始拖动时候触发的操作
        public void onStartTrackingTouch(SeekBar seekBar) {
            key = false;
            start_btn.setText("开始");
        }

        // 停止拖动时候
        public void onStopTrackingTouch(SeekBar seekBar) {
            key = true;
            thread = new MyThread();
            timeData=speedProgressTemp;
            thread.start();
            start_btn.setText("暂停");
        }
    }

    class MyThread extends Thread {
        public void run() {
            super.run();

            while (key) {
                try {
                    Thread.sleep(timeData);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                progressTemp++;
                handler.sendEmptyMessage(0x1001);
                if (progressTemp > totalSeconds) {
                    progressTemp = 0;
                }
            }
        }
    }

    /**
     * 创建日期及时间选择对话框
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch (id) {
            case DATE_DIALOG:
                c = Calendar.getInstance();
                dialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker dp, int year,
                                                  int month, int dayOfMonth) {
                                //							et.setText("您选择了：" + year + "年" + (month + 1) + "月"
                                //									+ dayOfMonth + "日");
                            }
                        }, c.get(Calendar.YEAR), // 传入年份
                        c.get(Calendar.MONTH), // 传入月份
                        c.get(Calendar.DAY_OF_MONTH) // 传入天数
                );
                break;
            case TIME_DIALOG:
                c = Calendar.getInstance();
                dialog = new TimePickerDialog(
                        this,
                        new TimePickerDialog.OnTimeSetListener() {
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                //							et.setText("您选择了：" + hourOfDay + "时" + minute + "分");
                                int minuteTemp = hourOfDay * 3600 + minute * 60;
                                progressTemp = minuteTemp;
                            }
                        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
                        false);
                break;
        }
        return dialog;
    }
    //
    //	protected void onResume() {
    //		super.onResume();
    //
    //		thread = new MyThread();
    //		thread.start();
    //	}

    /**
     * 计算两个时间之间的秒数
     */

    private static int totalSeconds(String startTime, String endTime) {

        String[] st = startTime.split(":");
        String[] et = endTime.split(":");

        int st_h = Integer.valueOf(st[0]);
        int st_m = Integer.valueOf(st[1]);
        int st_s = Integer.valueOf(st[2]);

        int et_h = Integer.valueOf(et[0]);
        int et_m = Integer.valueOf(et[1]);
        int et_s = Integer.valueOf(et[2]);

        int totalSeconds = (et_h - st_h) * 3600 + (et_m - st_m) * 60
                + (et_s - st_s);

        return totalSeconds;

    }

    /**
     * 根据当前选择的秒数还原时间点
     *
     * @param
     */

    private static String getCheckTimeBySeconds(int progress, String startTime) {

        String return_h = "", return_m = "", return_s = "";

        String[] st = startTime.split(":");

        int st_h = Integer.valueOf(st[0]);
        int st_m = Integer.valueOf(st[1]);
        int st_s = Integer.valueOf(st[2]);

        int h = progress / 3600;

        int m = (progress % 3600) / 60;

        int s = progress % 60;

        if ((s + st_s) >= 60) {

            int tmpSecond = (s + st_s) % 60;

            m = m + 1;

            if (tmpSecond >= 10) {
                return_s = tmpSecond + "";
            } else {
                return_s = "0" + (tmpSecond);
            }

        } else {
            if ((s + st_s) >= 10) {
                return_s = s + st_s + "";
            } else {
                return_s = "0" + (s + st_s);
            }

        }

        if ((m + st_m) >= 60) {

            int tmpMin = (m + st_m) % 60;

            h = h + 1;

            if (tmpMin >= 10) {
                return_m = tmpMin + "";
            } else {
                return_m = "0" + (tmpMin);
            }

        } else {
            if ((m + st_m) >= 10) {
                return_m = (m + st_m) + "";
            } else {
                return_m = "0" + (m + st_m);
            }

        }

        if ((st_h + h) < 10) {
            return_h = "0" + (st_h + h);
        } else {
            return_h = st_h + h + "";
        }

        return return_h + ":" + return_m + ":" + return_s;
    }
}