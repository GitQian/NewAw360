package com.android.allwinner.newaw360.Receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import java.util.List;

public class TvdStateChangedReceiver extends BroadcastReceiver {
    public TvdStateChangedReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");
//        if (intent.getAction().equals("android.hardware.newtvd.state.change")) {
//            LogUtil.e("qiansheng", "TvdStateChanged!!!!");
//            int state = intent.getIntExtra("state", 0);
//            if (state == 1) {
//                //TODO: 显示倒车界面
//                Intent intentTvd = new Intent(context, TvdActivity.class);
//                intentTvd.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(intentTvd);
//
//            } else if (state == 0) {
//                //TODO: 关闭倒车界面
//                if (isForeground(context, "com.xinzhihui.mydvr.TvdActivity")) {
//                    ActivityCollector.finishActivity("com.xinzhihui.mydvr.TvdActivity");
//                }
//            }
//        }
    }


    /**
     * 判断某个界面是否在前台
     *
     * @param context
     * @param className 某个界面名称
     */
    private boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName())) {
                return true;
            }
        }

        return false;
    }
}
