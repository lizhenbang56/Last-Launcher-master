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
    private static final long loop_time = 40 * 1000;
    public AlarmService() {}

    public void backToDesktop() {
        Toast toast=Toast.makeText(getApplicationContext(), "backToDesktop", Toast.LENGTH_SHORT);
        toast.show();
        Intent home=new Intent(Intent.ACTION_MAIN);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        backToDesktop();

        //通过AlarmManager定时启动广播
        AlarmManager alarmManager= (AlarmManager) getSystemService(ALARM_SERVICE);
        long triggerAtTime= SystemClock.elapsedRealtime() + loop_time;
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