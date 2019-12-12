package com.ceiv.meetinghelper.bean;

/**
 */

public class ResponseBaseBean {
    protected int returnCode;
    protected String returnMsg;

    public int getReturnCode() {
        return returnCode;
    }

    public String getReturnMsg() {
        return returnMsg;
    }

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }

    public void setReturnMsg(String returnMsg) {
        this.returnMsg = returnMsg;
    }
}
