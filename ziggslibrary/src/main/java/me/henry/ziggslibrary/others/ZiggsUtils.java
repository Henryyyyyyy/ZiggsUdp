package me.henry.ziggslibrary.others;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by henry on 2017/11/16.
 */

public class ZiggsUtils {
    public static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
                mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList
                = activityManager.getRunningServices(40);
        if (!(serviceList.size()>0)) {
            return false;
        }
        for (int i=0; i<serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }
    public static boolean isIPV4(String ipAddress) {
        if (ipAddress.equals("") || ipAddress == null)
            return false;
        //IPv4正则
        String ippattern = "^((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)$";
        Pattern pattern = Pattern.compile(ippattern);
        Matcher matcher = pattern.matcher(ipAddress);
        //XLog.d(TAG, "正则匹配IP:" + ipAddress + ",返回" + matcher.matches());
        return matcher.matches();

    }
}
