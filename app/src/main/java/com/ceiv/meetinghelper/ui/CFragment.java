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
import com.ceiv.meetinghelper.control.CodeConstants;
import com.ceiv.meetinghelper.listener.RoomNumChangeListener;
import com.ceiv.meetinghelper.listener.FragmentCallBackC;
import com.ceiv.meetinghelper.log4j.LogUtils;
import com.ceiv.meetinghelper.utils.DateTimeUtil;
import com.ceiv.meetinghelper.utils.SharedPreferenceTools;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 会议结束前5分钟
 */
public class CFragment extends Fragment implements  FragmentCallBackC {
    private String TAG = getClass().getSimpleName();
    private Context context;
    private DateTimeUtil dateTimeUtil;
    private TextView room_num, time_remain;
    private static String roomNum;//会议室编号
    private static long durationTime, remainTime;//当前会议持续时间,剩余时间
    private Timer timeMainTimer;
    private List<MqttMeetingCurrentBean> myCurMeetingList = new ArrayList<>();
    private Timer remainTimeTimer;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1://更新剩余时间显示
                    roomNum = (String) msg.obj;
                    room_num.setText("会议室编号：" + roomNum);
                    if(remainTimeTimer != null){
                        remainTimeTimer.purge();
                        remainTimeTimer.cancel();
                        remainTimeTimer = null;
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
                default:
                    break;
            }
        }
    };

    public CFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_c, container, false);
        context = getActivity();
        dateTimeUtil = DateTimeUtil.getInstance();
        initViews(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        roomNum = (String) SharedPreferenceTools.getValueofSP(context, CodeConstants.DEVICE_NUM, "");//获取会议室编号
        room_num.setText("会议室编号：" + roomNum);
        MainActivity.setFragmentCallBackC(this);
    }

    private void initViews(View view) {
        room_num = view.findViewById(R.id.room_num_c);
        time_remain = view.findViewById(R.id.time_remain_c_tv);
    }


    @Override
    public void TransDataC(String topic, List mList) {
        LogUtils.i(TAG, "topic：" + topic + ";----mList:" + mList.toString());
        myCurMeetingList.clear();
        myCurMeetingList.addAll(mList);
        if (myCurMeetingList.size() > 0) {
            durationTime = myCurMeetingList.get(0).getEndDate() - System.currentTimeMillis();
            if (durationTime < 0) {
                durationTime = 0;
            }
            showRemainTime(durationTime);
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
     * 更新会议室编号
     */
    protected void whenRoomNumChanged(String roomNum){
        LogUtils.i("CFragment", "更新会议室编号:"+roomNum);
        Message message = new Message();
        message.what = 1;
        message.obj = roomNum;
        handler.sendMessage(message);
    }
}
