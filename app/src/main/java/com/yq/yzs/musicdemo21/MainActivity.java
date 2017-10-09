package com.yq.yzs.musicdemo21;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

import static com.yq.yzs.musicdemo21.MyAdapter.mPosition;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    public static final int MESSAGE_TEST = 9527;
    public SeekBar seekBar;//进度条控件
    public static TextView tv_now;//现在播放的时间
    public static TextView tv_duration;//

    public ImageButton mImPlay;//播放按钮
    private ToggleButton mTgRandom;//随机播放控件
    public static RecyclerView mRecyclerView;//RecyclerView控件
    private IBinder mBinder;
    public static Intent intent1 = new Intent();//开启服务
    private MsgReceiver msgReceiver;  //广播

    public MyAdapter mMyAdapter;//recycleView适配器

    public ArrayList<Data> liDatas = new ArrayList<>();//歌曲列表


    //请求开通权限的数组
    private static final String[] PERMISSION_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    //请求码
    private static final int REQUEST_EXTERNAL_STORAGE = 9527;//权限请求码
    public static int flag = 0;//标记点击播放
    public boolean isPlay = false; //播放标记
    private boolean isUpDate = true;
    private boolean isChanging = false;
    private Thread thread;
    private int mDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏标题栏
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        setContentView(R.layout.activity_main);


        initView();//初始化控件
        circulateListener();//循环随机播放监听
        //动态注册广播接收器
        msgReceiver = new MsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.yzs.music2");
        registerReceiver(msgReceiver, intentFilter);


        //检查系统是都允许该权限
        //=====参数1：context上下文（MainActivity.this）
        //=====参数2：要检查的权限（Manifest.permission.xxx）
        int peemission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (peemission != PackageManager.PERMISSION_GRANTED) {
            // 系统没有授予该权限  先申请该权限，在回调中再调用测试代码。比如Android6.0以上设备
            // 参数1  请求获取权限的activity 比如MainActivity.this
            // 参数2 请求获取权限的数组。给该Activity开通哪些权限
            // 参数3 请求码。理论上可以是任意值
            ActivityCompat.requestPermissions(MainActivity.this, PERMISSION_STORAGE, REQUEST_EXTERNAL_STORAGE);
        } else {
            // 系统已经授予该权限 直接调用测试代码。比如5.1以下设备，或者6.0以上设置中已经授予过的权限
            testSDCard(); // 测试
        }

        onSeekBarListener();
        recyclerViewClickListenet();
    }

    //SeekBar 的监听方法
    private void onSeekBarListener() {


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                //进度条改变时调用
                tv_now.setText(ShowTime(progress));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //按下进度条时调用
                //防止在拖动进度条进行进度设置时与Thread更新播放进度条冲突
                isChanging = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //松开进度条时调用
                tv_now.setText(ShowTime(seekBar.getProgress()));
                //将media进度设置为当前seekbar的进度
                MyService.mMediaPlayer.seekTo(seekBar.getProgress());
                isChanging = false;
                thread = new Thread(new SeekBarThread());
                // 启动线程
                thread.start();
            }
        });
    }

    int p = 0;

    //recycleView点击事件
    public void recyclerViewClickListenet() {
        //单击监听
        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                //单击列表播放
                intent1.putExtra("cmd", MyService.CMD_LIST);//点击播放的命令
                intent1.putExtra("pos", position);//播放哪一首
                startService(intent1);//启动服务

                mRecyclerView.scrollToPosition(mPosition + 4);
                //                Toast.makeText(MainActivity.this, "" + position, Toast.LENGTH_SHORT).show();
                mImPlay.setBackgroundResource(R.drawable.pause_normal);
                flag = 2;
                mMyAdapter.changeSelected(position);//刷新


            }
        });
        //长按点击事件
        ItemClickSupport.addTo(mRecyclerView).setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
                p = position;
                //                Toast.makeText(MainActivity.this, "" + position, Toast.LENGTH_SHORT).show();
                registerForContextMenu(mRecyclerView);
                return false;
            }
        });
    }

    // 创建上下文菜单
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add("试试");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    // 上下文菜单项被选中
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // 大神的代码（链式调用）
        new AlertDialog.Builder(MainActivity.this).setTitle("请问是否删除").setMessage("删除后不能恢复哦")
                // 设置积极的按钮
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "onClick: " + which);
                        intent1.putExtra("cmd", MyService.CMD_DEL);//点击播放的命令
                        intent1.putExtra("delPos", p);//播放哪一首
                        startService(intent1);//启动服务
                        Toast.makeText(MainActivity.this, "你点击了OK，开始删除。。。", Toast.LENGTH_LONG).show();
                    }
                })
                // 设置消极的按钮
                .setNegativeButton("Cancel", null)
                // 设置中立的按钮
                .setNeutralButton("让我想想", null).create().show();

        return super.onContextItemSelected(item);
    }

    // 上下文菜单被关闭
    @Override
    public void onContextMenuClosed(Menu menu) {
        super.onContextMenuClosed(menu);
    }


    //权限回调方法
    // 参数1 requestCode 请求码 就是requestPermissions()中的第三个参数
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            // 只有这两个值相等，才认为是上面的requestPermissions()发起的权限请求的回调
            // 此时才开始调用测试代码
            testSDCard();
        }
    }

    private void testSDCard() {
        intent1.setClass(MainActivity.this, MyService.class);
        bindService(intent1, conn, Context.BIND_AUTO_CREATE);
        thread = new Thread(new SeekBarThread());
        // 启动线程
        thread.start();
    }

    // 自定义的线程，用于跟新滑动条
    class SeekBarThread implements Runnable {

        @Override
        public void run() {
            while (!isChanging) {
                // 将SeekBa位置设置到当前播放位置
                seekBar.setProgress(MyService.mMediaPlayer.getCurrentPosition());
                try {
                    // 每100毫秒更新一次位置
                    Thread.sleep(100);
                    //播放进度
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //开启服务：ServiceConnection
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mBinder = iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    //广播接收器
    public class MsgReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if ("com.yzs.music2".equals(intent.getAction())) {
                //拿到数据
                Bundle bundle = intent.getBundleExtra("list");
                liDatas = bundle.getParcelableArrayList("data");
                //isUpDate = intent.getBooleanExtra("isUpDate", false);

                //Log.e("aaa", "onReceive: " + list);
                if (isUpDate) {
                    //创建适配器
                    mMyAdapter = new MyAdapter(liDatas);//定义适配器
                    //5、设置布局管理器
                    LinearLayoutManager manager = new LinearLayoutManager(MainActivity.this, LinearLayout.VERTICAL, false);
                    mRecyclerView.setLayoutManager(manager);
                    mRecyclerView.setAdapter(mMyAdapter);//设置适配器
                    //添加分割线
                    mRecyclerView.addItemDecoration(new com.yq.yzs.musicdemo21.DividerItemDecoration(MainActivity.this, LinearLayout.VERTICAL));
                    isUpDate = false;
                }

                mDuration = intent.getIntExtra("duration", 0);//获得当前歌曲的总时长
                seekBar.setMax(MyService.mMediaPlayer.getDuration());//设置SeekBar的长度
                tv_duration.setText(ShowTime(mDuration) + "");//设置总时长（剩余时间）
            }
        }
    }


    //计算时间（00:00）
    public String ShowTime(int time) {
        time /= 1000;
        int minute = time / 60;
        int hour = minute / 60;
        int second = time % 60;
        minute %= 60;
        return String.format("%02d:%02d", minute, second);
    }

    //初始化控件
    private void initView() {
        mImPlay = (ImageButton) findViewById(R.id.imPlay);
        mTgRandom = (ToggleButton) findViewById(R.id.tgRandom);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        tv_now = (TextView) findViewById(R.id.tv_now);
        tv_duration = (TextView) findViewById(R.id.tv_duration);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
    }

    //循环随机播放监听
    private void circulateListener() {
        mTgRandom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                mTgRandom.setChecked(isChecked);
                if (!isChecked) {
                    mTgRandom.setBackgroundResource(R.drawable.random);
                    //播放的命令
                    intent1.putExtra("cmd", MyService.CMD_RANDOM);
                    startService(intent1);
                    //Toast.makeText(MainActivity.this, "随机播放", Toast.LENGTH_SHORT).show();
                } else {
                    mTgRandom.setBackgroundResource(R.drawable.circulation);
                    //播放的命令
                    intent1.putExtra("cmd", MyService.CMD_CIRCULATE);
                    startService(intent1);
                    //Toast.makeText(MainActivity.this, "循环播放", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "isChecked: " + isChecked);
            }
        });
    }

    //播放/暂停监听
    public void btnPlay(View view) {

        if (flag == 2) {
            if (!isPlay) {
                mImPlay.setBackgroundResource(R.drawable.play_normal);
                //暂停的命令
                intent1.putExtra("cmd", MyService.CMD_PLAY);
                startService(intent1);
                //Toast.makeText(MainActivity.this, "暂停", Toast.LENGTH_SHORT).show();
                //isPlay = true;
                flag = 1;
            }
        } else {
            mImPlay.setBackgroundResource(R.drawable.pause_normal);
            //播放的命令
            intent1.putExtra("cmd", MyService.CMD_PLAY);
            startService(intent1);
            //Toast.makeText(MainActivity.this, "播放", Toast.LENGTH_SHORT).show();
            isPlay = false;
            flag = 2;
        }
    }

    //关闭
    public void btnOff(View view) {
        //        onDestroy();
        finish();
    }

    //上一首
    public void btnPrevious(View view) {
        mImPlay.setBackgroundResource(R.drawable.pause_normal);
        flag = 2;
        //上一首的命令
        intent1.putExtra("cmd", MyService.CMD_PREVIOUS);
        startService(intent1);
    }

    //下一首
    public void btnNext(View view) {
        mImPlay.setBackgroundResource(R.drawable.pause_normal);
        flag = 2;
        //下一首的命令
        intent1.putExtra("cmd", MyService.CMD_NEXT);
        startService(intent1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBinder != null) {
            unbindService(conn);
            mBinder = null;
            //停止服务
            stopService(intent1);
            //注销广播
            unregisterReceiver(msgReceiver);
        }
    }

}
