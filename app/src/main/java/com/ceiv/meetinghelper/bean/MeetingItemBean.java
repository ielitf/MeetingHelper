package com.ceiv.meetinghelper.bean;

public class MeetingItemBean {
    private String day;
    private String hour;
    private String title;
    private String order;

    public MeetingItemBean(String day,String hour,String title,String order){
        this.day = day;
        this.hour = hour;
        this.title = title;
        this.order = order;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "MeetingItemBean{" +
                "day='" + day + '\'' +
                ", hour='" + hour + '\'' +
                ", title='" + title + '\'' +
                ", order='" + order + '\'' +
                '}';
    }
}
