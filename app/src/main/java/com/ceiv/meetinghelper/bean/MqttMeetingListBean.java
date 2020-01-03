package com.ceiv.meetinghelper.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class MqttMeetingListBean {
    @Id
    private Long newsId;
    @Index(unique = true)
    private int id;//会议ID
    private String roomNum;//会议室编号
    private String roomName;//会议室名称
    private String department;//部门
    private String name;//会议主题名称
    private String isOpen;//是否公开  字符串1:公开 0:未公开
    private long endDate;
    private long startDate; //日期
    private int templateId;// 1 代表模板A   2代表模板B
    private String bookPerson;//预订人
    private String bookPersonPhone;//预订人联系方式
    private String sign;//用于标记消息类型："insert"增加   "delete" 删除   update 更改
    @Generated(hash = 1327061324)
    public MqttMeetingListBean(Long newsId, int id, String roomNum, String roomName,
            String department, String name, String isOpen, long endDate,
            long startDate, int templateId, String bookPerson,
            String bookPersonPhone, String sign) {
        this.newsId = newsId;
        this.id = id;
        this.roomNum = roomNum;
        this.roomName = roomName;
        this.department = department;
        this.name = name;
        this.isOpen = isOpen;
        this.endDate = endDate;
        this.startDate = startDate;
        this.templateId = templateId;
        this.bookPerson = bookPerson;
        this.bookPersonPhone = bookPersonPhone;
        this.sign = sign;
    }
    @Generated(hash = 1418683081)
    public MqttMeetingListBean() {
    }
    public Long getNewsId() {
        return this.newsId;
    }
    public void setNewsId(Long newsId) {
        this.newsId = newsId;
    }
    public int getId() {
        return this.id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getRoomNum() {
        return this.roomNum;
    }
    public void setRoomNum(String roomNum) {
        this.roomNum = roomNum;
    }
    public String getRoomName() {
        return this.roomName;
    }
    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getIsOpen() {
        return this.isOpen;
    }
    public void setIsOpen(String isOpen) {
        this.isOpen = isOpen;
    }
    public long getEndDate() {
        return this.endDate;
    }
    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }
    public long getStartDate() {
        return this.startDate;
    }
    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }
    public int getTemplateId() {
        return this.templateId;
    }
    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }
    public String getBookPerson() {
        return this.bookPerson;
    }
    public void setBookPerson(String bookPerson) {
        this.bookPerson = bookPerson;
    }
    public String getBookPersonPhone() {
        return this.bookPersonPhone;
    }
    public void setBookPersonPhone(String bookPersonPhone) {
        this.bookPersonPhone = bookPersonPhone;
    }
    public String getSign() {
        return this.sign;
    }
    public void setSign(String sign) {
        this.sign = sign;
    }
    public String getDepartment() {
        return this.department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return "MqttMeetingListBean{" +
                "newsId=" + newsId +
                ", id=" + id +
                ", roomNum='" + roomNum + '\'' +
                ", roomName='" + roomName + '\'' +
                ", department='" + department + '\'' +
                ", name='" + name + '\'' +
                ", isOpen='" + isOpen + '\'' +
                ", endDate=" + endDate +
                ", startDate=" + startDate +
                ", templateId=" + templateId +
                ", bookPerson='" + bookPerson + '\'' +
                ", bookPersonPhone='" + bookPersonPhone + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
