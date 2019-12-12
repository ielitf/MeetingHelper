package com.ceiv.meetinghelper.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.ceiv.meetinghelper.listener.NetEvevtListener;

/**
 * 自定义检查手机网络状态是否切换的广播接受器
 */

public class NetBroadcastReceiver extends BroadcastReceiver {
    public static NetEvevtListener netEvevtListener = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        // 如果相等的话就说明网络状态发生了变化
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            int netWorkState = NetUtil.getNetWorkState(context);
            // 接口回调传过去状态的类型
            LogUtil.d("===netEvevtListener:",netEvevtListener+"");
            if(netEvevtListener != null){
                netEvevtListener.onNetChange(netWorkState);
            }
        }
    }
    public static void setNetEvevtListener(NetEvevtListener listener){
        netEvevtListener = listener;
    }
}
