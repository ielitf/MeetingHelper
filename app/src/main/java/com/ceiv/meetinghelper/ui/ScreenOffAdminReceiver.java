package com.ceiv.meetinghelper.ui;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.ceiv.meetinghelper.log4j.LogUtils;
import com.ceiv.meetinghelper.utils.ToastUtils;

/**
 * created by ltf on 2019/12/30
 */
public class ScreenOffAdminReceiver extends DeviceAdminReceiver {
    private String TAG = getClass().getSimpleName();
    @Override
    public void onEnabled(Context context, Intent intent) {
//        ToastUtils.showToast(context,"设备管理器使能");
        LogUtils.i(TAG,"设备管理器使能");
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
//        ToastUtils.showToast(context,"设备管理器没有使能");
        LogUtils.i(TAG,"设备管理器没有使能");
    }

}
