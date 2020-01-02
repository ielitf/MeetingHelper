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
import com.ceiv.meetinghelper.control.CodeConstants;
import com.ceiv.meetinghelper.listener.RoomNumChangeListener;
import com.ceiv.meetinghelper.listener.FragmentCallBackA;
import com.ceiv.meetinghelper.log4j.LogUtils;
import com.ceiv.meetinghelper.utils.DateTimeUtil;
import com.ceiv.meetinghelper.utils.SharedPreferenceTools;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 会议开始前5分钟
 */
public class AFragment extends Fragment implements  FragmentCallBackA {
    private String TAG = getClass().getSimpleName();
    private Context context;
    private DateTimeUtil dateTimeUtil;
    private TextView room_num,time_going;
    private static String roomNum;//会议室编号
    private static long goingTime;//当前会议持续时间,剩余时间
    private Timer timeGoingTimer;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    roomNum = (String) msg.obj;
                    room_num.setText("会议室编号：" + roomNum);
                    if(timeGoingTimer != null){
                        timeGoingTimer.purge();
                        timeGoingTimer.cancel();
                        timeGoingTimer = null;
                    }
                    break;
                case 2:
                    long t = (long) msg.obj;
                    if (t >=3600000){
                        time_going.setText(dateTimeUtil.transTimeToClock2(t));
                    }else{
                        time_going.setText(dateTimeUtil.transTimeToClock(t));
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public AFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View convertView = inflater.inflate(R.layout.fragment_form_a, container, false);
        context = getActivity();
        dateTimeUtil = DateTimeUtil.getInstance();
        initViews(convertView);
        return convertView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        roomNum = (String) SharedPreferenceTools.getValueofSP(context, CodeConstants.DEVICE_NUM, "");//获取会议室编号
        room_num.setText("会议室编号：" + roomNum);
        MainActivity.setFragmentCallBackA(this);
    }

    private void initViews(View view) {
        room_num = view.findViewById(R.id.room_num_a);
        time_going = view.findViewById(R.id.time_going_tv);
    }

    @Override
    public void TransDataA(String topic, List mList) {
        LogUtils.i(TAG,"topic：" + topic);
        showTimeGoing();
    }

    /**
     * 显示会议已经进行的时间
     */
    private void showTimeGoing() {
        if(timeGoingTimer != null){
            timeGoingTimer.purge();
            timeGoingTimer.cancel();
            timeGoingTimer = null;
            goingTime = 0;
        }
        timeGoingTimer = new Timer();
        timeGoingTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 2;
                message.obj = goingTime;
                handler.sendMessage(message);
//                if(goingTime == ( 1* 60*1000)){
//                    timeGoingTimer.purge();
//                    timeGoingTimer.cancel();
//                    timeGoingTimer = null;
//                }
                goingTime = goingTime + 1000;
            }
        },0,1000);
    }

    /**
     * 更新会议室编号
     */
    protected void whenRoomNumChanged(String roomNum){
        LogUtils.i("BFragment", "更新会议室编号:"+roomNum);
        Message message = new Message();
        message.what = 1;
        message.obj = roomNum;
        handler.sendMessage(message);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
