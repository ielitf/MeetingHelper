package com.ceiv.meetinghelper.K780;

/**
 * K780数据操作类
 */
public class K780Utils {
	public static final String host = "http://api.k780.com/?app=weather.today";
	public static final String weaid = "1";
	public static final String APPKEY = "42583";
	public static final String SIGN = "fc72d21319ae9e52b8830f9dff9332c9";
	public static final String WEATHER_URL = host
			+ "&weaid="+ weaid
			+ "&appkey=" + APPKEY
			+ "&sign=" + SIGN
			+ "&format=json";

}
