package com.ceiv.meetinghelper.listener;

/**
 * 用来当切换会议室编号后，通知系统查询数据库更新信息
 */

public interface DataBaseQueryListenerB {
    void onDataBaseQueryListenerB(String roomNum);
}
