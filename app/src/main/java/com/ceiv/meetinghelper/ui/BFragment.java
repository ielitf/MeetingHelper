package com.ceiv.meetinghelper.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ceiv.meetinghelper.R;
/**
 * 会议开始5分钟后 -- 会议结束前5分钟
 */
public class BFragment extends Fragment {
    private Context context;
    private TextView room_num,time_remain,meeting_name,meeting_date,meeting_department,meeting_order;
    private TextView meeting_name_next,getMeeting_detail_next;
    private static String roomNum;//会议室编号
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
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
        return convertView;
    }


    private void initViews(View view) {
        room_num = view.findViewById(R.id.room_num);
        time_remain = view.findViewById(R.id.time_remaining_tv);
        meeting_name = view.findViewById(R.id.meeting_name);
        meeting_date = view.findViewById(R.id.meeting_time);
        meeting_department = view.findViewById(R.id.meeting_department);
        meeting_order = view.findViewById(R.id.meeting_order);
        meeting_name_next = view.findViewById(R.id.meeting_name_next);
        getMeeting_detail_next = view.findViewById(R.id.meeting_detail_next);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
