package io.github.subhamtyagi.lastlauncher;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


public class UStats {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("M-d-yyyy HH:mm:ss");
    public static final String TAG = UStats.class.getSimpleName();
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressWarnings("ResourceType")
    public static void getStats(Context context){
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService("usagestats");
        int interval = UsageStatsManager.INTERVAL_YEARLY;
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.YEAR, -1);
        long startTime = calendar.getTimeInMillis();

        String info = "";
        info = info + "Range start:" + dateFormat.format(startTime);

        Log.d(TAG, info);
        Log.d(TAG, "Range end:" + dateFormat.format(endTime));

        UsageEvents uEvents = usm.queryEvents(startTime,endTime);
        while (uEvents.hasNextEvent()){
            UsageEvents.Event e = new UsageEvents.Event();
            uEvents.getNextEvent(e);

            if (e != null){
                Log.d(TAG, "Event: " + e.getPackageName() + "\t" +  e.getTimeStamp());
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static List<UsageStats> getUsageStatsList(Context context){
        UsageStatsManager usm = getUsageStatsManager(context);
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.YEAR, -1);
        long startTime = calendar.getTimeInMillis();

        Log.d(TAG, "Range start:" + dateFormat.format(startTime) );
        Log.d(TAG, "Range end:" + dateFormat.format(endTime));

        List<UsageStats> usageStatsList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,startTime,endTime);
        return usageStatsList;
    }

    public static String getTopAppPackageName(Context context) {
        String packageName = "刚进入程序";
        ActivityManager activityManager =
            (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        try {
            if(Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
                List<ActivityManager.RunningAppProcessInfo> processes =
                    activityManager.getRunningAppProcesses();
                if (processes.size() == 0) {
                    return "线程数为0";
                }
                for (ActivityManager.RunningAppProcessInfo process : processes) {
                    if (process.importance ==
                            ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        return process.processName;
                    }
                }
            } else if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
                final long end = System.currentTimeMillis();
                final UsageStatsManager usageStatsManager =
                    (UsageStatsManager) context.getSystemService( Context.USAGE_STATS_SERVICE);
                if (null == usageStatsManager) {
                    return "usageStatsManager为空";
                }
                final UsageEvents events = usageStatsManager.queryEvents((end - 60 * 1000), end);
                if (null == events) {
                    return "events为空";
                }
                UsageEvents.Event usageEvent = new UsageEvents.Event();
                UsageEvents.Event lastMoveToFGEvent = null;
                while (events.hasNextEvent()) {
                    events.getNextEvent(usageEvent);
                    if (usageEvent.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                        lastMoveToFGEvent = usageEvent;
                    }
                }
                if (lastMoveToFGEvent != null) {
                    packageName = lastMoveToFGEvent.getPackageName();
                }
                else{
                    return "lastMoveToFGEvent为空";
                }
            }
        }
        catch (Exception ignored){
            return "抛出异常";
        }
        return packageName;
    }

    public static String printCurrentUsageStatus(Context context){
        return getTopAppPackageName(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static String printUsageStats(List<UsageStats> usageStatsList){
        for (UsageStats u : usageStatsList){
            if (u.getPackageName().equals("com.tencent.mm")){
                return "微信："
                        + u.getTotalTimeInForeground()/1000/60 + "分"
                        + u.getTotalTimeInForeground()/1000%60 + "秒";
            }
        }
        return "未查询到微信";
    }

//    public static String printCurrentUsageStatus(Context context){
//        return printUsageStats(getUsageStatsList(context));
//    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressWarnings("ResourceType")
    private static UsageStatsManager getUsageStatsManager(Context context){
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService("usagestats");
        return usm;
    }
}