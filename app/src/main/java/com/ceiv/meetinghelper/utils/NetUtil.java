package com.ceiv.meetinghelper.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 *判断网络工具类
 */
public class NetUtil {
    private static int type;
    /**
     * 没有连接网络
     */
    private static final int NETWORK_NONE = -1;
    /**
     * 移动网络
     */
    private static final int NETWORK_MOBILE = 0;
    /**
     * 无线网络
     */
    private static final int NETWORK_WIFI = 1;
    /**
     * 以太网
     */
    private static final int NETWORK_ETHERNET = 2;

    public static int getNetWorkState(Context context) {
        // 得到连接管理器对象
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            type = activeNetworkInfo.getType();
            if (type == (ConnectivityManager.TYPE_WIFI)) {
                return NETWORK_WIFI;
            } else if (type == (ConnectivityManager.TYPE_MOBILE)) {
                return NETWORK_MOBILE;
            }else if (type == (ConnectivityManager.TYPE_ETHERNET)) {
                return NETWORK_ETHERNET;
            }
        } else {
            return NETWORK_NONE;
        }
        return NETWORK_NONE;
    }
}
