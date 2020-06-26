package com.example.jasonhu.recommendpoi.bean;

public class HistoryOrder {
    private String tv_content;
    private String tv_time;

    public HistoryOrder(String content,String time){
        tv_content=content;
        tv_time=time;
    }

    public String getTv_content(){
        return tv_content;
    }
    public String getTv_time(){
        return tv_time;
    }
}
