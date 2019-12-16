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
import com.ceiv.meetinghelper.utils.LogUtil;

/**
 * 会议开始前5分钟
 */
public class AFragment extends Fragment {
    private Context context;
    private TextView  room_num;
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

    public AFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View convertView = inflater.inflate(R.layout.fragment_form_a, container, false);
        context = getActivity();
        initViews(convertView);

        return convertView;
    }

    private void initViews(View view) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
