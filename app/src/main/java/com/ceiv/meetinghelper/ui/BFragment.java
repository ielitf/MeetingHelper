package com.ceiv.meetinghelper.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ceiv.meetinghelper.R;
import com.ceiv.meetinghelper.bean.MqttMeetingCurrentBean;
import com.ceiv.meetinghelper.bean.MqttMeetingListBean;
import com.ceiv.meetinghelper.control.CodeConstants;
import com.ceiv.meetinghelper.greendao.DaoMaster;
import com.ceiv.meetinghelper.greendao.DaoSession;
import com.ceiv.meetinghelper.greendao.MqttMeetingListBeanDao;
import com.ceiv.meetinghelper.listener.DataBaseQueryListenerB;
import com.ceiv.meetinghelper.listener.FragmentCallBackB;
import com.ceiv.meetinghelper.utils.DateTimeUtil;
import com.ceiv.meetinghelper.utils.LogUtil;
import com.ceiv.meetinghelper.utils.SharedPreferenceTools;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 会议开始5分钟后 -- 会议结束前5分钟
 */
public class BFragment extends Fragment implements FragmentCallBackB , DataBaseQueryListenerB {
    private Context context;
    private DateTimeUtil dateTimeUtil;
    private TextView room_num,time_remain,meeting_name,meeting_date,meeting_department,meeting_order;
    private TextView meeting_name_next,meeting_detail_next;
    private static String roomNum;//会议室编号
    private List<MqttMeetingListBean> myMeetingList = new ArrayList<>();
    private List<MqttMeetingListBean> meetingListQuery = new ArrayList<>();
    private List<MqttMeetingCurrentBean> myCurMeetingList = new ArrayList<>();
    private int curIndex,size;//当前会议在今日会议列表中的位置,今日会议列表的size
    private Timer checkCurMeetingTime,remainTimeTimer;
    private CheckCurMeetingTask checkCurMeetingTask;
    private static long durationTime,remainTime;//当前会议持续时间,剩余时间
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private MqttMeetingListBeanDao meetingListBeanDao;
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
//                        meeting_department.setText(myCurMeetingList.get(0).get);
//                        meeting_order.setText(myCurMeetingList.get(0).get);
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
                    if (t >=3600000){
                        time_remain.setText(dateTimeUtil.transTimeToClock2(t));
                    }else{
                        time_remain.setText(dateTimeUtil.transTimeToClock(t));
                    }
                    break;
                case 3://当前会议结束清空当前会议显示
                    meeting_name.setText("");
                    meeting_date.setText("");
                    meeting_department.setText("");
                    meeting_order.setText("");
                    break;
                case 4://显示下一会议内容。
                    if((curIndex +1) < size){
                        meeting_name_next.setText(myMeetingList.get(curIndex +1).getName());
                        String startTime = DateTimeUtil.getInstance().transTimeToHHMM(myMeetingList.get(curIndex +1).getStartDate());
                        String endTime = DateTimeUtil.getInstance().transTimeToHHMM(myMeetingList.get(curIndex +1).getEndDate());
                        meeting_detail_next.setText(startTime + "-" + endTime+"   会议主持："+ myMeetingList.get(curIndex +1).getBookPerson());
                        meeting_detail_next.setVisibility(View.VISIBLE);
                    }else{
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
        CustomEidtDialog.setOnDataBaseQueryListenerB(this);
        roomNum = (String) SharedPreferenceTools.getValueofSP(context, CodeConstants.DEVICE_NUM, "");//获取会议室编号
        room_num.setText("会议室编号：" + roomNum);
//        showRemainTime(3610000);
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

    }
    private void getStuDao() {
        // 创建数据
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(getActivity(), "meetingList.db", null);
        daoMaster = new DaoMaster(devOpenHelper.getWritableDb());
        daoSession = daoMaster.newSession();
        meetingListBeanDao = daoSession.getMqttMeetingListBeanDao();

    }
    /**
     * 收到信息
     */
    @Override
    public void TransDataB(String topic, List mList) {
        LogUtil.i("========BFragment", "topic:" + topic + ";----mList:" + mList.toString());
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

            checkNextMeeting();
        }
    }
    /**
     * 从数据库中查询今日会议列表数据
     */
    private List<MqttMeetingListBean> checkTodayMeeting() {
        roomNum = (String) SharedPreferenceTools.getValueofSP(context, "DeviceNum", "");//获取会议室编号
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
        meetingListQuery.clear();
        meetingListQuery.addAll(meetingListBeanDao.queryBuilder().where(MqttMeetingListBeanDao.Properties.RoomNum.eq(roomNum), MqttMeetingListBeanDao.Properties.StartDate
                .between(dateTimeUtil.transDataToTime(dateTimeUtil.getCurrentDateYYMMDD() + " 00:00:00"), dateTimeUtil.transDataToTime(dateTimeUtil.getCurrentDateYYMMDD() + " 23:59:59")))
                .orderAsc(MqttMeetingListBeanDao.Properties.EndDate)
                .build().list());
        if (meetingListQuery.size() >= 0) {
//                    TransDataB(MqttService.TOPIC_MEETING_LIST, meetingListQuery);
            myMeetingList.clear();
            myMeetingList.addAll(meetingListQuery);
        }
//            }
//        }, 60, 60 * 1000 * 15);
        return myMeetingList;
    }
    private void checkNextMeeting() {
//        checkTodayMeeting();

        if(myCurMeetingList.size() != 0){
        for(int i = 0; i < size; i++){
            if(myMeetingList.get(i).getId() == myCurMeetingList.get(0).getMeetingId()){
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
        if(remainTimeTimer != null){
            remainTimeTimer.purge();
            remainTimeTimer.cancel();
            remainTimeTimer = null;
        }
        remainTimeTimer = new Timer();
        remainTimeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(remainTime < 0){
                    remainTime = 0;
                }
                Message message = new Message();
                message.what = 2;
                message.obj = remainTime;
                handler.sendMessage(message);

                if(remainTime ==0){
                    remainTimeTimer.purge();
                    remainTimeTimer.cancel();
                    remainTimeTimer = null;
                }
                remainTime = remainTime - 1000;
            }
        }, 0,1000);
    }

    /**
     * 当切换会议室编号后，通知系统重新查询数据库，并更新页面
     *
     * @param roomNum
     */
    @Override
    public void onDataBaseQueryListenerB(String roomNum) {
        room_num.setText("会议室编号：" + roomNum);
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
//        TransDataB(MqttService.TOPIC_MEETING_LIST, meetingListQuery);
        if (meetingListQuery.size() >= 0) {
//                    TransDataB(MqttService.TOPIC_MEETING_LIST, meetingListQuery);17
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
