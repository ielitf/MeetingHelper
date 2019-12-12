package com.ceiv.meetinghelper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.ceiv.meetinghelper.R;
import com.ceiv.meetinghelper.bean.MqttMeetingListBean;
import com.ceiv.meetinghelper.utils.DateTimeUtil;

import java.util.List;

public class MeetingAdapter extends MyBaseAdapter<MqttMeetingListBean> {
    private LayoutInflater inflater;
    private Context context;
    public MeetingAdapter(Context context, List<MqttMeetingListBean> mData) {
        super(mData, context);
        this.context = context;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data == null ? 0: data.size();
    }

    @Override
    public MqttMeetingListBean getItem(int position) {
        // TODO Auto-generated method stub
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holderView;
        if (convertView == null) {
            convertView=inflater.inflate(R.layout.item_meetting, null);
            holderView=new ViewHolder();
            holderView.day = convertView.findViewById(R.id.meeting_data_day);
            holderView.hour = convertView.findViewById(R.id.meeting_data_hour);
            holderView.title =  convertView.findViewById(R.id.meeting_title);
            holderView.order = convertView.findViewById(R.id.meeting_order);
            convertView.setTag(holderView);
        }else{
            holderView=(ViewHolder) convertView.getTag();
        }
        MqttMeetingListBean item = data.get(position);

        holderView.day.setText(DateTimeUtil.getInstance().transTimeToMMDD(item.getStartDate()));
        holderView.hour.setText(DateTimeUtil.getInstance().transTimeToHHMM(item.getStartDate())+"-"+DateTimeUtil.getInstance().transTimeToHHMM(item.getEndDate()));
        if ("1".equals(item.getIsOpen())){
            holderView.title.setText(item.getName());
        } else
        if("0".equals(item.getIsOpen())){
            holderView.title.setText("未公开");
        }else{
            holderView.title.setText(item.getName());
        }
        holderView.order.setText(item.getBookPerson());
//        convertView.measure(0,0);
//         int height=convertView.getMeasuredHeight();
        return convertView;
    }

    class ViewHolder {
        private TextView day,hour,title,order;
    }
}
