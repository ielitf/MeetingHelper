package com.ceiv.meetinghelper.bean;

public class JiaWeatherBean {
    private String day;
    private int icon;
    private String temHigh;
    private String temLow;

    public JiaWeatherBean(String day, int icon, String temHigh, String temLow){
        this.day = day;
        this.icon = icon;
        this.temHigh = temHigh;
        this.temLow = temLow;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTemHigh() {
        return temHigh;
    }

    public void setTemHigh(String temHigh) {
        this.temHigh = temHigh;
    }

    public String getTemLow() {
        return temLow;
    }

    public void setTemLow(String temLow) {
        this.temLow = temLow;
    }

    @Override
    public String toString() {
        return "JiaWeatherBean{" +
                "day='" + day + '\'' +
                ", icon=" + icon +
                ", temHigh='" + temHigh + '\'' +
                ", temLow='" + temLow + '\'' +
                '}';
    }
}
