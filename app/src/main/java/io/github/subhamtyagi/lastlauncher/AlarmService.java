package io.github.subhamtyagi.lastlauncher;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.widget.Toast;

public class AlarmService extends Service {
    private static final int PENDING_REQUEST=0;
    private static int is_wechat_running = -1; // 微信是否正在运行
    private static long last_close_time = System.currentTimeMillis();
    private static long start_time = 0;
    private static final long pause_time = 10 * 1000;
    private static final long available_time = 30 * 1000;
    public AlarmService() {}

    public void backToDesktop() {
        Intent home=new Intent(Intent.ACTION_MAIN);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
    }

    public void start_wechat(){
        is_wechat_running = 1;
        start_time = System.currentTimeMillis();
        Toast toast=Toast.makeText(getApplicationContext(), "start_wechat", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void stop_wechat(){
        backToDesktop();
        is_wechat_running = 0;
        last_close_time = System.currentTimeMillis();
        Toast toast=Toast.makeText(getApplicationContext(), "stop_wechat", Toast.LENGTH_SHORT);
        toast.show();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String content = UStats.printCurrentUsageStatus(this);

        // 如果微信正在前台运行
        if (content.equals("com.tencent.mm") || (content.equals("lastMoveToFGEvent为空") && is_wechat_running==1)){
            if (is_wechat_running == 1){
                long use_time = System.currentTimeMillis() - start_time;
                // 如果超时了
                if (use_time > available_time){
                    stop_wechat();
                }
                else{
                    long left_time = available_time - use_time;
                    long left_time_min = left_time/1000/60;
                    long left_time_sec = left_time/1000%60;
                    Toast toast=Toast.makeText(getApplicationContext(), "剩余"+left_time_min+":"+left_time_sec, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            else if (is_wechat_running == 0){  // 上次终止微信后，首次启动微信
                if (System.currentTimeMillis() - last_close_time > pause_time){  // 休息了足够长时间
                    // 允许微信正常运行
                    start_wechat();
                }
                else{  // 禁止微信运行
                    backToDesktop();
                    long wait_time = System.currentTimeMillis() - last_close_time;
                    long left_time = pause_time - wait_time;
                    long left_time_min = left_time/1000/60;
                    long left_time_sec = left_time/1000%60;
                    Toast toast=Toast.makeText(getApplicationContext(), "剩余"+left_time_min+":"+left_time_sec, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            else if (is_wechat_running == -1){  // 首次启动
                start_wechat();
            }
        }
        else{ // 如果在前台运行的不是微信，则直接返回桌面。
            backToDesktop();
        }

        //通过AlarmManager定时启动广播
        AlarmManager alarmManager= (AlarmManager) getSystemService(ALARM_SERVICE);
        long triggerAtTime= SystemClock.elapsedRealtime() + 20000;
        //SystemClock.elapsedRealtime()：从开机到现在的毫秒书（手机睡眠(sleep)的时间也包括在内

        Intent i=new Intent(this, AlarmReceive.class);
        @SuppressLint({"WrongConstant", "UnspecifiedImmutableFlag"}) PendingIntent pIntent=PendingIntent.getBroadcast(this,PENDING_REQUEST,i,PENDING_REQUEST);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pIntent);
        // 设置闹钟

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}