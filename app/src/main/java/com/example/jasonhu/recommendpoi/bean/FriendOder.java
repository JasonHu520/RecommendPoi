package com.example.jasonhu.recommendpoi.bean;

public class FriendOder {
    private String message,name,time,head_pic;

    public FriendOder(String message,String name,String head_pic){
        this.message=message;
        this.name=name;
        this.head_pic = head_pic;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public String getName() {
        return name;
    }

    public String getHead_pic() {
        return head_pic;
    }

    public void setHead_pic(String head_pic) {
        this.head_pic = head_pic;
    }
}
