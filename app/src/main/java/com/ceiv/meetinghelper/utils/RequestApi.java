package com.ceiv.meetinghelper.utils;

/**
 * 网络请求的url
 */
public class RequestApi {
    public static String  getUpdataAppUrl(){
        return BaseConfig.ServiceIp+"/app/uploadVersionInfo";
    }

}
