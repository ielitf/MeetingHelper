package com.ceiv.meetinghelper.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.ceiv.meetinghelper.R;
import com.ceiv.meetinghelper.listener.DataBaseQueryListenerA;
import com.ceiv.meetinghelper.listener.DataBaseQueryListenerB;
import com.ceiv.meetinghelper.utils.SharedPreferenceTools;
import com.ceiv.meetinghelper.utils.ToastUtils;

/**
 * @auther wjt
 * @date 2019/5/14
 */
public class CustomEidtDialog extends Dialog {
    private Button yes;//确定按钮
    private Button no;//取消按钮
    private Button exit;//退出按钮
    private TextView titleTV;//消息标题文本
    private String titleStr;//从外界设置的title文本
    private String messageStr;//从外界设置的消息文本
    private EditText ed_mqtt;//mqtt
    private EditText ed_service;//service
    private EditText ed_devicenum;//num
    private Context context;
    private Intent intent;
    private static DataBaseQueryListenerA dataBaseQueryListenerA;
    private static DataBaseQueryListenerB dataBaseQueryListenerB;

    //确定文本和取消文本的显示的内容
    private String yesStr, noStr;
    private onNoOnclickListener noOnclickListener;//取消按钮被点击了的监听器
    private onYesOnclickListener yesOnclickListener;//确定按钮被点击了的监听器

    public static void setOnDataBaseQueryListenerA(DataBaseQueryListenerA listener) {
        dataBaseQueryListenerA = listener;
    }
    public static void setOnDataBaseQueryListenerB(DataBaseQueryListenerB listener) {
        dataBaseQueryListenerB = listener;
    }

    public CustomEidtDialog(@NonNull Context context,Intent intent) {

        super(context, R.style.MyDialog);
        this.context = context;
        this.intent = intent;
    }

    /**
     * 设置取消按钮的显示内容和监听
     *
     * @param str
     * @param onNoOnclickListener
     */
    public void setNoOnclickListener(String str, onNoOnclickListener onNoOnclickListener) {
        if (str != null) {
            noStr = str;
        }
        this.noOnclickListener = onNoOnclickListener;
    }

    /**
     * 设置确定按钮的显示内容和监听
     *
     * @param str
     * @param yesOnclickListener
     */
    public void setYesOnclickListener(String str, onYesOnclickListener yesOnclickListener) {
        if (str != null) {
            yesStr = str;
        }
        this.yesOnclickListener = yesOnclickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_edit_dialog);
        //空白处不能取消动画
        setCanceledOnTouchOutside(false);
        //初始化界面控件
        initView();
        //初始化界面数据
        initData();
        //初始化界面控件的事件
        initEvent();
    }

    /**
     * 初始化界面控件
     */
    private void initView() {
        yes = findViewById(R.id.ed_yes);
        no = findViewById(R.id.ed_no);
        exit = findViewById(R.id.ed_exit);
        titleTV = (TextView) findViewById(R.id.ed_title);
        ed_mqtt = findViewById(R.id.ed_mqtt);
        ed_service = findViewById(R.id.ed_service);
        ed_devicenum = findViewById(R.id.ed_devicenum);

        String mqttIp = (String) SharedPreferenceTools.getValueofSP(context, "mqttIp", "aids.zdhs.com.cn:1883");
        String ServiceIp = (String) SharedPreferenceTools.getValueofSP(context, "ServiceIp", "https://aids.zdhs.com.cn");
        String DeviceNum = (String) SharedPreferenceTools.getValueofSP(context, "DeviceNum", "001");
        ed_mqtt.setText(mqttIp);
        ed_service.setText(ServiceIp);
        ed_devicenum.setText(DeviceNum);
    }

    /**
     * 初始化界面控件的显示数据
     */
    private void initData() {

    }

    /**
     * 初始化界面的确定和取消监听
     */
    private void initEvent() {
        //设置确定按钮被点击后，向外界提供监听
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ed_mqttvalue = ed_mqtt.getText().toString();
                String ed_servicevalue = ed_service.getText().toString();
                String ed_devicenumvalue = ed_devicenum.getText().toString();

                if (ed_mqttvalue.equals("") || ed_mqttvalue == null) {
                } else {
                    SharedPreferenceTools.putValuetoSP(context, "mqttIp", ed_mqttvalue);
                }
                if (ed_servicevalue.equals("") || ed_servicevalue == null) {
                } else {
                    SharedPreferenceTools.putValuetoSP(context, "ServiceIp", ed_servicevalue);
                }
                if (ed_devicenumvalue.equals("") || ed_devicenumvalue == null) {
                } else {
                    SharedPreferenceTools.putValuetoSP(context, "DeviceNum", ed_devicenumvalue);
                }
                //重启应用程序
                ToastUtils.showToast(context,"修改成功");
//                Intent i = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
//                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                context.startActivity(i);

                CustomEidtDialog.this.cancel();
//                dataBaseQueryListenerA.onDataBaseQueryListenerA(ed_devicenumvalue);
                dataBaseQueryListenerB.onDataBaseQueryListenerB(ed_devicenumvalue);
                context.stopService(intent);
                context.startService(intent);

            }
        });
        //设置取消按钮被点击后，向外界提供监听
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomEidtDialog.this.cancel();
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              context.stopService(intent);
              System.exit(0);
            }
        });
    }

    /**
     * 从外界Activity为Dialog设置标题
     *
     * @param title
     */
    public void setTitle(String title) {
        titleStr = title;
    }

    //取消按钮监听事件
    public interface onNoOnclickListener {
        public void onNoClick();
    }

    //确定按钮监听事件
    public interface onYesOnclickListener {
        public void onYesOnclick();
    }

}

