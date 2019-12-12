package com.ceiv.meetinghelper.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferenceManager {
    static SharedPreferences sp;

    public static void init(Context context, String name) {
        sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }
    /**
     * 选择的会议室编号
     */
    private static final String MEETING_ROOM_NUM = "meeting_room_num";
    public static void setMeetingRoomNum(String  num) {
        if (null != sp) {
            sp.edit().putString(MEETING_ROOM_NUM,num).commit();
        }
    }

    public static String  getMeetingRoomNum() {
        if (null != sp) {
            return sp.getString(MEETING_ROOM_NUM, "002");
        }
        return "002";
    }
    /**
     * 当前会议数据
     */
    private static final String MEETING_CURRENT_DATA = "meeting_current_data";
    public static void setMeetingCurrentData(String jsonStr) {
        if (null != sp) {
            sp.edit().putString(MEETING_CURRENT_DATA, jsonStr).commit();
        }
    }

    public static String getMeetingCurrentData() {
        if (null != sp) {
            return sp.getString(MEETING_CURRENT_DATA, null);
        }
        return "";
    }
    /**
     * 当前会议显示的模板类型
     */
    private static final String MEETING_MUBAN_TYPE = "meeting_mubant_type";
    public static void setMeetingMuBanType(int jsonStr) {
        if (null != sp) {
            sp.edit().putInt(MEETING_MUBAN_TYPE, jsonStr).commit();
        }
    }

    public static int getMeetingMuBanType() {
        if (null != sp) {
            return sp.getInt(MEETING_MUBAN_TYPE, 1);
        }
        return 1;
    }
    /**
     * 今日会议列表
     */
    private static final String MEETING_TODAY_DATA = "meeting_today_data";
    public static void setMeetingTodayData(String jsonStr) {
        if (null != sp) {
            sp.edit().putString(MEETING_TODAY_DATA, jsonStr).commit();
        }
    }

    public static String getMeetingTodayData() {
        if (null != sp) {
            return sp.getString(MEETING_TODAY_DATA, null);
        }
        return "";
    }
    /**
     * 缓存主页数据
     */
    private static final String CACHED_HOMEPAGE_DATA = "cached_homepage_data";
    public static void setCachedHomepageData(String jsonStr) {
        if (null != sp) {
            sp.edit().putString(CACHED_HOMEPAGE_DATA, jsonStr).commit();
        }
    }

    public static String getCachedHomepageData() {
        if (null != sp) {
            return sp.getString(CACHED_HOMEPAGE_DATA, null);
        }
        return null;
    }

    /**
     * 是否是第一次使用
     */
    private static final String IS_FIRST_USE = "Is_app_first_use";
    public static void setIsFirstUse(boolean value) {
        if (null != sp) {
            sp.edit().putBoolean(IS_FIRST_USE, value).commit();
        }
    }

    public static boolean getIsFirstUse() {
        if (null != sp) {
            return sp.getBoolean(IS_FIRST_USE, true);
        }
        return true;
    }
}
