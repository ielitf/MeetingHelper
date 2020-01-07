package com.ceiv.meetinghelper.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ceiv.meetinghelper.R;
import com.ceiv.meetinghelper.bean.MqttMeetingCurrentBean;
import com.ceiv.meetinghelper.bean.MqttMeetingListBean;
import com.ceiv.meetinghelper.control.CodeConstants;
import com.ceiv.meetinghelper.greendao.DaoMaster;
import com.ceiv.meetinghelper.greendao.DaoSession;
import com.ceiv.meetinghelper.greendao.MqttMeetingListBeanDao;
import com.ceiv.meetinghelper.listener.FragmentCallBackB;
import com.ceiv.meetinghelper.log4j.LogUtils;
import com.ceiv.meetinghelper.utils.DateTimeUtil;
import com.ceiv.meetinghelper.utils.IPAddressUtils;
import com.ceiv.meetinghelper.utils.SharedPreferenceTools;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 会议开始5分钟后 -- 会议结束前5分钟
 */
public class BFragment extends Fragment implements FragmentCallBackB {
    private String TAG = getClass().getSimpleName();
    private Context context;
    private DateTimeUtil dateTimeUtil;
    private TextView room_num, time_remain, meeting_name, meeting_date, meeting_department, meeting_order;
    private TextView meeting_name_next, meeting_detail_next;
    private String roomNum;//会议室编号
    private List<MqttMeetingListBean> myMeetingList = new ArrayList<>();
    private List<MqttMeetingListBean> meetingListQuery = new ArrayList<>();
    private List<MqttMeetingCurrentBean> myCurMeetingList = new ArrayList<>();
    private int curIndex, size;//当前会议在今日会议列表中的位置,今日会议列表的size
    private Timer checkCurMeetingTime, remainTimeTimer;
    private CheckCurMeetingTask checkCurMeetingTask;
    private static long durationTime, remainTime;//当前会议持续时间,剩余时间
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private MqttMeetingListBeanDao meetingListBeanDao;
    private String ip;
    private ImageView weather_icon;
    private TextView weather_wind,weather_type,weather_temp;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1://显示当前会议
                    if (myCurMeetingList.size() > 0) {
                        //设置当前会议数据
                        meeting_name.setText(myCurMeetingList.get(0).getMeetingName());
                        String startTime = DateTimeUtil.getInstance().transTimeToHHMM(myCurMeetingList.get(0).getStartDate());
                        String endTime = DateTimeUtil.getInstance().transTimeToHHMM(myCurMeetingList.get(0).getEndDate());
                        meeting_date.setText(startTime + "-" + endTime);
                        String department = myCurMeetingList.get(0).getDepartment();
                        if (TextUtils.isEmpty(department)) {
                            department = "未知";
                        }
                        meeting_department.setText(department);
                        meeting_order.setText(myCurMeetingList.get(0).getBookPersion() + "    " + myCurMeetingList.get(0).getBookPersonPhone());
                        if (checkCurMeetingTask != null) {
                            checkCurMeetingTask.cancel();
                            checkCurMeetingTask = null;
                        }
                        if (checkCurMeetingTime != null) {
                            checkCurMeetingTime.purge();
                            checkCurMeetingTime.cancel();
                            checkCurMeetingTime = null;
                        }
                        checkCurMeetingTime = new Timer();
                        checkCurMeetingTask = new CheckCurMeetingTask();
                        checkCurMeetingTime.schedule(checkCurMeetingTask, durationTime);//在会议结束后，显示当前无会议

                    } else {
                        meeting_name.setText("当前无会议");
                        meeting_date.setText("");
                        meeting_department.setText("");
                        meeting_order.setText("");
                    }
                    break;

                case 2://更新剩余时间显示
                    long t = (long) msg.obj;
                    if (t >= 3600000) {
                        time_remain.setText(dateTimeUtil.timeToClock2(t));
                    } else {
                        time_remain.setText(dateTimeUtil.timeToClock(t));
                    }
                    break;
                case 3://当前会议结束清空当前会议显示
                    room_num.setText("会议室编号：" + roomNum);
                    meeting_name.setText("");
                    meeting_date.setText("");
                    meeting_department.setText("");
                    meeting_order.setText("");
                    break;
                case 4://显示下一会议内容。
                    LogUtils.i(TAG, "今日会议size=" + myMeetingList.size() + "/当前会议所在位置index=" + curIndex);
                    if ((curIndex + 1) < myMeetingList.size()) {
                        String department = myMeetingList.get(curIndex + 1).getDepartment();
                        if (TextUtils.isEmpty(department)) {
                            meeting_name_next.setText(myMeetingList.get(curIndex + 1).getName());
                        } else {
                            meeting_name_next.setText(myMeetingList.get(curIndex + 1).getDepartment() + "：" + myMeetingList.get(curIndex + 1).getName());
                        }
                        String startTime = dateTimeUtil.transTimeToHHMM(myMeetingList.get(curIndex + 1).getStartDate());
                        String endTime = dateTimeUtil.transTimeToHHMM(myMeetingList.get(curIndex + 1).getEndDate());
                        meeting_detail_next.setText(dateTimeUtil.transTimeToYYMMDD2(myMeetingList.get(curIndex + 1).getStartDate()) + "     " + startTime + "-" + endTime + "     预约人：" + myMeetingList.get(curIndex + 1).getBookPerson() + "  " + myMeetingList.get(curIndex + 1).getBookPersonPhone());
                        meeting_detail_next.setVisibility(View.VISIBLE);
                    } else {
                        meeting_name_next.setText("暂无");
                        meeting_detail_next.setVisibility(View.INVISIBLE);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View convertView = inflater.inflate(R.layout.fragment_form_b, container, false);
        context = getActivity();
        initViews(convertView);
        getStuDao();
        dateTimeUtil = DateTimeUtil.getInstance();
        return convertView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.setFragmentCallBackB(this);
        roomNum = (String) SharedPreferenceTools.getValueofSP(context, CodeConstants.DEVICE_NUM, "");//获取会议室编号
        room_num.setText("会议室编号：" + roomNum);
//        showRemainTime(3610000);
        loadWeatherData("北京");
    }

    private void initViews(View view) {
        room_num = view.findViewById(R.id.room_num);
        time_remain = view.findViewById(R.id.time_remaining_tv);
        meeting_name = view.findViewById(R.id.meeting_name);
        meeting_date = view.findViewById(R.id.meeting_time);
        meeting_department = view.findViewById(R.id.meeting_department);
        meeting_order = view.findViewById(R.id.meeting_order);
        meeting_name_next = view.findViewById(R.id.meeting_name_next);
        meeting_detail_next = view.findViewById(R.id.meeting_detail_next);
        weather_icon = view.findViewById(R.id.weather_icon);
        weather_wind = view.findViewById(R.id.weather_wind);
        weather_type = view.findViewById(R.id.weather_type);
        weather_temp = view.findViewById(R.id.weather_temp);
    }

    private void getStuDao() {
        // 创建数据
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(getActivity(), "meetingHelper.db", null);
        daoMaster = new DaoMaster(devOpenHelper.getWritableDb());
        daoSession = daoMaster.newSession();
        meetingListBeanDao = daoSession.getMqttMeetingListBeanDao();

    }

    /**
     * 收到信息
     */
    @Override
    public void TransDataB(String topic, List mList) {
        LogUtils.i(TAG, "topic:" + topic + ";----mList:" + mList.toString());
        if (topic.equals(MqttService.TOPIC_MEETING_CUR)) { //当前会议
            myCurMeetingList.clear();
            myCurMeetingList.addAll(mList);
            if (myCurMeetingList.size() > 0) {
                durationTime = myCurMeetingList.get(0).getEndDate() - System.currentTimeMillis();
                if (durationTime < 0) {
                    durationTime = 0;
                }
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
                showRemainTime(durationTime);
                checkTodayMeeting();
                checkNextMeeting();
            }
        }
        if (topic.equals(MqttService.TOPIC_MEETING_LIST)) { //今日会议
            myMeetingList.clear();
            myMeetingList.addAll(mList);
            LogUtils.i(TAG, "数据库中今日会议列表myMeetingList:" + myMeetingList.toString());
            checkNextMeeting();
        }
    }

    /**
     * 从数据库中查询今日会议 以及 以后的列表数据
     */
    private List<MqttMeetingListBean> checkTodayMeeting() {
        roomNum = (String) SharedPreferenceTools.getValueofSP(context, "DeviceNum", "");//获取会议室编号
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
        meetingListQuery.clear();
        meetingListQuery.addAll(meetingListBeanDao.queryBuilder().where(MqttMeetingListBeanDao.Properties.RoomNum.eq(roomNum), MqttMeetingListBeanDao.Properties.StartDate
                .ge(dateTimeUtil.transDataToTime(dateTimeUtil.getCurrentDateYYMMDD() + " 00:00:00")))
//                .between(dateTimeUtil.transDataToTime(dateTimeUtil.getCurrentDateYYMMDD() + " 00:00:00"), dateTimeUtil.transDataToTime(dateTimeUtil.getCurrentDateYYMMDD() + " 23:59:59")))
                .orderAsc(MqttMeetingListBeanDao.Properties.EndDate)
                .build().list());
        if (meetingListQuery.size() >= 0) {
            myMeetingList.clear();
            myMeetingList.addAll(meetingListQuery);
            LogUtils.i(TAG, "数据库中今日会议列表myMeetingList:" + myMeetingList.toString());
        }
//            }
//        }, 60, 60 * 1000 * 15);
        return myMeetingList;
    }

    private void checkNextMeeting() {
//        checkTodayMeeting();

        if (myCurMeetingList.size() != 0) {
            for (int i = 0; i < myMeetingList.size(); i++) {
                if (myMeetingList.get(i).getId() == myCurMeetingList.get(0).getMeetingId()) {
                    curIndex = i;
                    break;
                }
            }
            Message msg2 = new Message();
            msg2.what = 4;
            handler.sendMessage(msg2);
        }
    }

    private void showRemainTime(long ime) {
        remainTime = ime;
        if (remainTimeTimer != null) {
            remainTimeTimer.purge();
            remainTimeTimer.cancel();
            remainTimeTimer = null;
        }
        remainTimeTimer = new Timer();
        remainTimeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (remainTime < 0) {
                    remainTime = 0;
                }
                Message message = new Message();
                message.what = 2;
                message.obj = remainTime;
                handler.sendMessage(message);

                if (remainTime == 0) {
                    remainTimeTimer.purge();
                    remainTimeTimer.cancel();
                    remainTimeTimer = null;
                }
                remainTime = remainTime - 1000;
            }
        }, 0, 1000);
    }

    /**
     * 更新会议室编号后
     */
    protected void whenRoomNumChanged(String roomNum) {
        LogUtils.i("BFragment", "更新会议室编号:" + roomNum);
        this.roomNum = roomNum;
        //设置当前会议
        if (checkCurMeetingTask != null) {
            checkCurMeetingTask.cancel();
            checkCurMeetingTask = null;
        }
        if (checkCurMeetingTime != null) {
            checkCurMeetingTime.purge();
            checkCurMeetingTime.cancel();
            checkCurMeetingTime = null;
        }
        checkCurMeetingTime = new Timer();
        checkCurMeetingTask = new CheckCurMeetingTask();
        checkCurMeetingTime.schedule(checkCurMeetingTask, 0);//在会议结束后，显示当前无会议

        //查询今日会议
        meetingListQuery.clear();
        meetingListQuery.addAll(meetingListBeanDao.queryBuilder().where(MqttMeetingListBeanDao.Properties.RoomNum.eq(roomNum), MqttMeetingListBeanDao.Properties.StartDate
                .between(dateTimeUtil.transDataToTime(dateTimeUtil.getCurrentDateYYMMDD() + " 00:00:00"), dateTimeUtil.transDataToTime(dateTimeUtil.getCurrentDateYYMMDD() + " 23:59:59")))
                .orderAsc(MqttMeetingListBeanDao.Properties.EndDate)
                .build().list());
        if (meetingListQuery.size() >= 0) {
            myMeetingList.clear();
            myMeetingList.addAll(meetingListQuery);
        }
    }

    /**
     * 当前会议：会议结束后，若当前无会议，，显示无会议
     */
    class CheckCurMeetingTask extends TimerTask {

        @Override
        public void run() {
            Message msg = new Message();
            msg.what = 3;
            handler.sendMessage(msg);

        }
    }

    public static void check() {
        LogUtils.i("BFragment", "===check===");

    }

    /**
     * 获取天气信息
     */
    private void loadWeatherData(String city) {
        ip = IPAddressUtils.getAndroidIp(context);
        LogUtils.i("===设备IP：", ip);
        OkGo.<String>get(CodeConstants.chinaWeaUrl + city)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        LogUtils.d("=========天气", response.body());
                        if (response.code() == 200) {
                            JSONObject info = JSONObject.parseObject(response.body());
                            if ("OK".equals(info.getString("desc"))) {
                                JSONObject dataObj = info.getJSONObject("data");
                                String city = dataObj.getString("city");
                                String wendu = dataObj.getString("wendu");
                                JSONArray forecast = dataObj.getJSONArray("forecast");
                                //索引是0 只获取当前日期的天气信息
                                JSONObject todayWea = forecast.getJSONObject(0);
                                String fengli = todayWea.getString("fengli").replace("<![CDATA[","").replace("]]>","");
                                String fengxiang = todayWea.getString("fengxiang");
                                String temp_high = todayWea.getString("high").replace("高温 ","");
                                String temp_low = todayWea.getString("low").replace("低温 ","");
                                String type = todayWea.getString("type");
                                Log.d(TAG, "##!!: " + fengli);

                                weather_wind.setText(fengxiang +" "+fengli);
                                weather_type.setText(type);
                                weather_temp.setText(temp_low + " ~ " +temp_high);



                                    switch (type){//xue, lei, shachen, wu, bingbao, yun, yu, yin, qing
                                        case "雪":
                                            weather_icon.setImageResource(R.mipmap.w_xue);
                                            break;
                                        case "雷雨":
                                            weather_icon.setImageResource(R.mipmap.w_lei);
                                            break;
                                        case "沙尘":
                                            weather_icon.setImageResource(R.mipmap.w_shachen);
                                            break;
                                        case "雾":
                                            weather_icon.setImageResource(R.mipmap.w_wu2);
                                            break;
                                        case "冰雹":
                                            weather_icon.setImageResource(R.mipmap.w_bingbao);
                                            break;
                                        case "多云":
                                            weather_icon.setImageResource(R.mipmap.w_yun);
                                            break;
                                        case "小雨":
                                            weather_icon.setImageResource(R.mipmap.w_yu);
                                            break;
                                        case "阵雨":
                                            weather_icon.setImageResource(R.mipmap.w_yu);
                                            break;
                                        case "中雨":
                                            weather_icon.setImageResource(R.mipmap.w_yu);
                                            break;
                                        case "大雨":
                                            weather_icon.setImageResource(R.mipmap.w_yu);
                                            break;
                                        case "阴":
                                            weather_icon.setImageResource(R.mipmap.w_yin);
                                            break;
                                        case "晴":
                                            weather_icon.setImageResource(R.mipmap.w_qing);
                                            break;
                                        default:
                                            weather_icon.setImageResource(R.mipmap.w_yun);
                                            break;
                                    }
                            }
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        LogUtils.w("=====天气获取失败：", response.body());
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (checkCurMeetingTask != null) {
            checkCurMeetingTask.cancel();
            checkCurMeetingTask = null;
        }
        if (checkCurMeetingTime != null) {
            checkCurMeetingTime.purge();
            checkCurMeetingTime.cancel();
            checkCurMeetingTime = null;
        }
    }
}
