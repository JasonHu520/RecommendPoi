package com.example.jasonhu.recommendpoi.BaseClass.MessageBeanForChat;

public class Message {
    private String current_user;
    private String chat_message;
    private String to_user;
    private String current_date;

    public Message(String current_user,String chat_message,String to_user,String current_date){
        this.chat_message = chat_message;
        this.current_date = current_date;
        this.current_user = current_user;
        this.to_user = to_user;
    }

    public String getCurrent_user() {
        return current_user;
    }

    public String getChat_message() {
        return chat_message;
    }

    public void setChat_message(String chat_message) {
        this.chat_message = chat_message;
    }

    public void setCurrent_user(String current_user) {
        this.current_user = current_user;
    }

    public String getTo_user() {
        return to_user;
    }

    public void setTo_user(String to_user) {
        this.to_user = to_user;
    }

    public String getCurrent_date() {
        return current_date;
    }

    public void setCurrent_date(String current_date) {
        this.current_date = current_date;
    }
}
