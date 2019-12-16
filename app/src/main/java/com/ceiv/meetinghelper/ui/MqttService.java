package com.ceiv.meetinghelper.ui;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.widget.Toast;

import com.ceiv.meetinghelper.R;
import com.ceiv.meetinghelper.listener.CurMeetingCallBack;
import com.ceiv.meetinghelper.listener.MeetingEndListener;
import com.ceiv.meetinghelper.listener.MeetingGoingListener;
import com.ceiv.meetinghelper.listener.MeetingOverListener;
import com.ceiv.meetinghelper.listener.MeetingStartListener;
import com.ceiv.meetinghelper.listener.TodayMeetingCallBack;
import com.ceiv.meetinghelper.utils.LogUtil;
import com.ceiv.meetinghelper.utils.SharedPreferenceTools;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MqttService extends Service {
    private String TAG = getClass().getSimpleName();
    public static String clientId = "Defult";
    public static String API_HOST = "https://c.huihuinet.cn";
    public static int API_PORT = 1883;
    public static String TOPIC_MEETING_LIST = "";
    public static String TOPIC_MEETING_CUR = "";
    public static String TOPIC_MEETING_START = "";
    public static String TOPIC_MEETING_ONGOING = "";
    public static String TOPIC_MEETING_ONOVER = "";
    public static String TOPIC_MEETING_END = "";
    private static final String userName = "zzx";
    private static final String passWord = "zzx";
    //    private static final String userName = "atv";
//    private static final String passWord = "atv";
    private static String roomNum;//会议室编号

    public MqttClient mqttClient;
    public MqttConnectOptions options;
    private ScheduledExecutorService scheduler;
    private ConnectivityManager mConnectivityManager; //网络状态监测
    private static CurMeetingCallBack curMeetingCallBack;
    private static TodayMeetingCallBack todayMeetingCallBack;
    private static MeetingStartListener meetingStartListener;
    private static MeetingGoingListener meetingGoingListener;
    private static MeetingOverListener meetingOverListener;
    private static MeetingEndListener meetingEndListener;
    private static String[] topicFilters;
    private static int[] qos;

    public static void setCurMeetingCallBack(CurMeetingCallBack callBack) {
        curMeetingCallBack = callBack;
    }
    public static void setTodayMeetingCallBack(TodayMeetingCallBack callBack) {
        todayMeetingCallBack = callBack;
    }
    public static void setMeetingStartListener(MeetingStartListener listener) {
        meetingStartListener = listener;
    }
    public static void setMeetingGoingListener(MeetingGoingListener listener) {
        meetingGoingListener = listener;
    }
    public static void setMeetingOverListener(MeetingOverListener listener) {
        meetingOverListener = listener;
    }
    public static void setMeetingEndListener(MeetingEndListener listener) {
        meetingEndListener = listener;
    }

    public MqttService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.e("====service", "service.onStartCommand() is called");
        Notification.Builder builder = new Notification.Builder(this.getApplicationContext()); //获取一个Notification构造器
        Intent nfIntent = new Intent(this, MainActivity.class);
        builder.setContentIntent(PendingIntent.
                getActivity(this, 0, nfIntent, 0)) // 设置PendingIntent
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                        R.mipmap.ic_launcher))
                .setContentTitle("MeetingHelper")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText("namePlate正在运行")
                .setWhen(System.currentTimeMillis());
        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_SOUND;
        startForeground(119, notification);      // 开始前台服务
        init();
        connect();
        return super.onStartCommand(intent, flags, startId);
    }


    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 初始化相关数据
     */
    public void init() {
//        roomNum = SDCardUtils.readTxt(CodeConstants.ROOM_NUMBER);
        roomNum= (String) SharedPreferenceTools.getValueofSP(MqttService.this,"DeviceNum","");
        String IP_HOST_NEWS= (String) SharedPreferenceTools.getValueofSP(MqttService.this,"mqttIp","aids.zdhs.com.cn");
//        IP_HOST_NEWS = SDCardUtils.readTxt(CodeConstants.IP_HOST_NEWS);
        String URL_QUERY = "tcp://" + IP_HOST_NEWS;
        clientId = roomNum + "_client_id_mp_helper";
        TOPIC_MEETING_LIST = roomNum + "_meetList";
        TOPIC_MEETING_CUR = roomNum + "_currtMeet";
//        topicFilters = new String[]{TOPIC_MEETING_CUR, TOPIC_MEETING_LIST};
        topicFilters = new String[]{TOPIC_MEETING_CUR, TOPIC_MEETING_LIST, TOPIC_MEETING_START, TOPIC_MEETING_ONGOING, TOPIC_MEETING_ONOVER, TOPIC_MEETING_END};
        qos = new int[]{0, 1};
        LogUtil.d(TAG, "IP = " + URL_QUERY + "；会议室编号：" + roomNum + "；clientId = " + clientId
                + "；主题:" + TOPIC_MEETING_LIST + "---" + TOPIC_MEETING_CUR + "---" + TOPIC_MEETING_START + "---" + TOPIC_MEETING_ONGOING
                + "---" + TOPIC_MEETING_ONOVER + "---" + TOPIC_MEETING_END);

        // todo 设置主题
        try {
            //以下判断目的：当从新设置桌号会议室号后，重新startService,断开之前的连接。并重新设置参数，重新连接
            if(mqttClient!= null){
                unregisterReceiver(mConnectivityReceiver);
                if (mqttClient.isConnected()) {
                    mqttClient.disconnect(0);
                }
                mqttClient = null;
            }
            mqttClient = new MqttClient(URL_QUERY, clientId, new MemoryPersistence());
            //MQTT的连接设置
            options = new MqttConnectOptions();
            //设置是否清空session
            options.setCleanSession(false);
            //设置连接的用户名
            options.setUserName(userName);
            //设置连接的密码
            options.setPassword(passWord.toCharArray());
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(60);
            options.setKeepAliveInterval(60);
            options.setAutomaticReconnect(true);//设置自动重连

            mqttClient.setCallback(new MqttCallback() {
                                       @Override
                                       public void connectionLost(Throwable cause) {
                                           //连接丢失，进行重新连接
                                           LogUtil.i("===connectionLost", "connectionLost");
                                           if (isNetworkAvailable()) {
                                               reconnectIfNecessary();
                                           }
                                       }

                                       @Override
                                       public void messageArrived(String topic, MqttMessage message) {
                                           LogUtil.i("===", "接收消息主题 : " + topic + ";接收消息Qos :" + message.getQos());
                                           String str = new String(message.getPayload());
                                           LogUtil.i("===MqttService", "topic:" + topic + ";----message:" + str);
                                           if (TOPIC_MEETING_CUR.equals(topic)) {
                                               curMeetingCallBack.setDataCur(topic, str);
                                           } else if (TOPIC_MEETING_LIST.equals(topic)) {
                                               todayMeetingCallBack.setDataToday(topic, str);
                                           } else if (TOPIC_MEETING_START.equals(topic)){
                                               meetingStartListener.setDataMeetingStart(topic, str);
                                           }else if (TOPIC_MEETING_ONGOING.equals(topic)){
                                               meetingGoingListener.setDataMeetingGoing(topic, str);
                                           }else if (TOPIC_MEETING_ONOVER.equals(topic)){
                                               meetingOverListener.setDataMeetingOver(topic, str);
                                           }else if (TOPIC_MEETING_END.equals(topic)){
                                               meetingEndListener.setDataMeetingEnd(topic, str);
                                           }
                                       }

                                       @Override
                                       public void deliveryComplete(IMqttDeliveryToken token) {
                                           long messageId = token.getMessageId();
                                           LogUtil.e("===", "messageId=:" + messageId);
                                       }
                                   }


            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        mConnectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.registerReceiver(mConnectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }


    /**
     * 如果网络状态正常则返回true反之flase
     */
    private boolean isNetworkAvailable() {
        NetworkInfo info = mConnectivityManager.getActiveNetworkInfo();
        return (info == null) ? false : info.isConnected();
    }

    /**
     * 进行重新连接前判断client状态
     */
    public synchronized void reconnectIfNecessary() {
        if (!mqttClient.isConnected()) {
            connect();
        }
    }

    /*连接服务器，并订阅消息主题*/
    private void connect() {
        LogUtil.i("===connect", "connect");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mqttClient.connect(options);
                    mqttClient.subscribe(topicFilters);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 调用init() 方法之后，调用此方法。
     */
    public void startReconnect() {
        LogUtil.i("===startReconnect", "startReconnect");
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (!mqttClient.isConnected() && isNetworkAvailable()) {
                    LogUtil.i("===startReconnect", "开始连接");
                    connect();
                }
            }
        }, 0, 10 * 1000, TimeUnit.MILLISECONDS);
    }

    /**
     * Mqtt的连接监听器
     */
    private IMqttActionListener connectListener = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
            LogUtil.i(TAG, "Mqtt connect success!");
            Toast.makeText(MqttService.this,"Mqtt connect success",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
            LogUtil.d(TAG, "Mqtt connect failed: \n" + exception);
            Toast.makeText(MqttService.this,"Mqtt connect failed",Toast.LENGTH_SHORT).show();
            //连接失败，5s后重新尝试连接
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    startReconnect();
                }
            }, 5 * 1000);
        }
    };

    /**
     * 网络状态发生变化接收器
     */
    private final BroadcastReceiver mConnectivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.i("===BroadcastReceiver", "Connectivity Changed...");
            if (!isNetworkAvailable()) {
//                Toast.makeText(context, "网络连接不可用，请检查网络!", Toast.LENGTH_SHORT).show();
//                scheduler.shutdownNow();//如果当前无网络，调用此方法，此时打开app会崩溃，
            } else {
                startReconnect();
            }
        }
    };

    @Override
    public void onDestroy() {
        stopForeground(true);// 停止前台服务--参数：表示是否移除之前的通知
        super.onDestroy();
        LogUtil.e("====service", "service.onDestroy() is called");
        try {
            unregisterReceiver(mConnectivityReceiver);
            if (mqttClient.isConnected()){
                mqttClient.disconnect(0);
            }
            mqttClient = null;
        } catch (MqttException e) {
            LogUtil.e("====", "Something went wrong!" + e.getMessage());
            e.printStackTrace();
        }
    }
}
