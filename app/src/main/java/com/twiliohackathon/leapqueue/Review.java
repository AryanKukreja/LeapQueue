package com.twiliohackathon.leapqueue;

import java.util.Date;

@SuppressWarnings({"WeakerAccess", "unused"})
public class Review {
    boolean am;
    String comment, date, userName;
    Date dateDate;
    Double itemAvail, staffEff;
    int minute, hour, queueTime;

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment2) {
        this.comment = comment2;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName2) {
        this.userName = userName2;
    }

    public int getQueueTime() {
        return this.queueTime;
    }

    public void setQueueTime(int queueTime2) {
        this.queueTime = queueTime2;
    }

    public double getStaffEff() {
        return this.staffEff;
    }

    public void setStaffEff(double staffEff2) {
        this.staffEff = staffEff2;
    }

    public double getItemAvail() {
        return this.itemAvail;
    }

    public void setItemAvail(double itemAvail2) {
        this.itemAvail = itemAvail2;
    }

    public int getHour() {
        return this.hour;
    }

    public void setHour(int hour2) {
        this.hour = hour2;
    }

    public int getMinute() {
        return this.minute;
    }

    public void setMinute(int minute2) {
        this.minute = minute2;
    }

    public void setStaffEff(Double staffEff2) {
        this.staffEff = staffEff2;
    }

    public void setItemAvail(Double itemAvail2) {
        this.itemAvail = itemAvail2;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date2) {
        this.date = date2;
    }

    public Date getDateDate() {
        return this.dateDate;
    }

    public void setDateDate(Date dateDate2) {
        this.dateDate = dateDate2;
    }

    public boolean isAm() {
        return this.am;
    }

    public void setAm(boolean am) {
        this.am = am;
    }
}
