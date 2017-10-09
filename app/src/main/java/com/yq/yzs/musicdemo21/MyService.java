package com.yq.yzs.musicdemo21;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.provider.MediaStore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static com.yq.yzs.musicdemo21.MainActivity.mRecyclerView;

public class MyService extends Service {
    private static final String TAG = "/MyService";

    //控制播放、暂停的命令
    // 命令
    public static final int CMD_PLAY      = Binder.FIRST_CALL_TRANSACTION + 0;//播放
    public static final int CMD_PAUSE     = Binder.FIRST_CALL_TRANSACTION + 1;//暂停
    public static final int CMD_PREVIOUS  = Binder.FIRST_CALL_TRANSACTION + 2;//上一首
    public static final int CMD_NEXT      = Binder.FIRST_CALL_TRANSACTION + 3;//下一首
    public static final int CMD_CIRCULATE = Binder.FIRST_CALL_TRANSACTION + 4;//循环播放
    public static final int CMD_RANDOM    = Binder.FIRST_CALL_TRANSACTION + 5;//随机播放
    public static final int CMD_LIST      = Binder.FIRST_CALL_TRANSACTION + 6;//随机播放
    public static final int CMD_TOUCh     = Binder.FIRST_CALL_TRANSACTION + 7;//
    public static final int CMD_DEL       = Binder.FIRST_CALL_TRANSACTION + 8;//删除


    private ContentResolver resolver;//遍历数据
    private Intent  mIntent = new Intent("com.yzs.music2");//广播
    private IBinder mBinder = new MyBinder();

    private ArrayList<String> mListName = new ArrayList();//遍历存放SDCard的音乐的文件名

    private ArrayList<Data> mListDatas;//存放歌曲的列表

    public static MediaPlayer mMediaPlayer = new MediaPlayer();//音乐播放器

    private boolean isSttoped = true;//判断是否播放

    private boolean Completion = false;//判断完成播放

    public  int     pos    = 9;//播放的是哪一首音乐
    private boolean mIsRan = false;//判断是否循环播放
    private String musicPath;//播放时的文件路径

    //定时器
    //    Timer timer = new Timer();

    private int mDuration;

    //构造器
    public MyService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        //        timer.schedule(timerTask, 1000, 1000);
        loadMusicInfo();
        //播放完成
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (Completion) {
                    if (mIsRan) {
                        Random random = new Random();
                        pos = random.nextInt(mListDatas.size());
                        mIsRan = true;
                    } else {
                        pos = (pos + 1) % mListDatas.size();
                        mIsRan = false;
                    }
                    prepareMusic(pos);
                    if (pos < (mListName.size() - 3)) {
                        mRecyclerView.scrollToPosition(pos + 3);
                    }
                    mRecyclerView.scrollToPosition(pos + 3);
                }
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    //搜索公有目录的音频文件
    private void loadMusicInfo() {
//        mListName.clear();
//        mListDatas.clear();
        // 访问系统外部的音频信息
        // 使用 MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        resolver = getContentResolver();
        // 获取所有歌曲
        Cursor cursor = resolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null, null, null, null);
        mListDatas = new ArrayList<>();
        while (cursor.moveToNext()) {
            // 获取多媒体标题
            int index = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            String title = cursor.getString(index);
            int index2 = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            String name = cursor.getString(index2);
            //时间
            int index3 = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int sum = cursor.getInt(index3);

            String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));//获得歌曲的绝对路径

            int tMinute = sum / 1000 / 60;
            int tSecond = ((sum) / 1000 - tMinute * 60);
            String time = tMinute + ":" + tSecond;
            Data data = new Data(false, title, name, time);

            mListDatas.add(data);
            mListName.add(path);
        }
        //发送Action为com.example.communication.RECEIVER的广播
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("data", mListDatas);
        mIntent.putExtra("list", bundle);
        mIntent.putExtra("isUpDate", true);
        sendBroadcast(mIntent);
    }

    int ran;//当前播放的歌曲条目

    //播放前的准备
    private void prepareMusic(int pos) {
        mMediaPlayer.reset();
        try {
            if (mIsRan) {//如果为true，就是随机播放
                Random random = new Random();
                pos = random.nextInt(mListDatas.size());
                if (pos != ran) {
                    musicPath = mListName.get(pos);
                } else {
                    pos++;
                    if (pos <= (mListName.size() - 1)) {
                        playMusic(musicPath);
                    }
                }
                ran = pos;//当前播放的歌曲条目
                playMusic(musicPath);
            } else {
                musicPath = mListName.get(pos);
                playMusic(musicPath);
            }
            if (pos < (mListName.size() - 3)) {
                mRecyclerView.scrollToPosition(pos + 3);
            }
            mRecyclerView.scrollToPosition(pos + 3);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //播放
    private void playMusic(String musicPath) throws IOException {
        mMediaPlayer.setDataSource(musicPath);
        mMediaPlayer.prepare();
        mDuration = mMediaPlayer.getDuration();
        mIntent.putExtra("duration", mDuration);
        sendBroadcast(mIntent);
        mMediaPlayer.start();
        mMediaPlayer.setLooping(false);
    }

/*
    //定时器//用于发送当前的播放的时间== 到主线程跟新UI
    public TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            int nowTime = mMediaPlayer.getCurrentPosition();
            mIntent.putExtra("pos", nowTime);
            sendBroadcast(mIntent);
        }
    };
*/


    //测试删除歌曲
    public void ddd(String mStr) {
        //                String mFilePath = SDCardUtils.getSDCardPublicDir(Environment.DIRECTORY_MUSIC) + File.separator + mStr;
////        String mFilePath = SDCardUtils.getSDCardPublicDir(mStr);
//        SDCardUtils.removeFileFromSDCard(mStr);
//        loadMusicInfo();
//        Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
    }

    //播放歌曲
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int cmd = intent.getIntExtra("cmd", 0);//命令
        switch (cmd) {
            case CMD_PLAY://播放
                if (isSttoped) {
                    // 未开始
                    isSttoped = false;
                    Completion = true;
                    prepareMusic(pos);
                    //                    }
                } else if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
                    // 暂停中
                    mMediaPlayer.start();
                    isSttoped = false;
                } else if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    isSttoped = false;
                }
                break;
            case CMD_PAUSE://暂停
                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                }
                break;
            case CMD_PREVIOUS://上一首
                if (pos > 0) {
                    pos = (pos - 1) % mListDatas.size();
                } else if (pos == 0) {
                    pos = mListName.size() - 1;
                }
                Completion = true;
                prepareMusic(pos);
                break;
            case CMD_NEXT://下一首
                if (pos < mListName.size() - 1) {
                    pos = (pos + 1) % mListDatas.size();
                } else {
                    pos = 0;
                }
                Completion = true;
                prepareMusic(pos);
                break;
            case CMD_LIST://点击播放
                isSttoped = false;
                Completion = true;
                pos = intent.getIntExtra("pos", 0);
                prepareMusic(pos);
                break;
            case CMD_CIRCULATE://循环播放
                mIsRan = false;
                Completion = true;
                break;
            case CMD_RANDOM://随机播放
                mIsRan = true;
                Completion = true;
                break;
            case CMD_TOUCh://随机播放
                int seek = intent.getIntExtra("seekbarPos", 0);
                mMediaPlayer.seekTo(seek);

                break;
            case CMD_DEL://长按删除
//                int delPos = intent.getIntExtra("delPos", 0);
//                ddd(mListName.get(delPos));
                break;
        }
        return Service.START_STICKY;
    }

    private class MyBinder extends Binder {
        @Override
        protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            return super.onTransact(code, data, reply, flags);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

}
