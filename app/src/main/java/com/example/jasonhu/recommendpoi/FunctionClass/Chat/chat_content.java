package com.example.jasonhu.recommendpoi.FunctionClass.Chat;

/**
 * Created by xiatom on 2019/4/24.
 */

public class chat_content {
    private String fromUser;
    private String content;
    private String toUser;
    private String currentTime;
    private boolean fromOthor;
    private String position;

    public chat_content(boolean fromOther,String fromUser,String content,String toUser,String CurrentTime,String position){
        this.fromUser = fromUser;
        this.content = content;
        this.toUser = toUser;
        this.currentTime = CurrentTime;
        this.fromOthor = fromOther;
        this.position = position;
    }

    public String getFromUser() {
        return fromUser;
    }

    public String getContent() {
        return content;
    }

    public String getToUser() {
        return toUser;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public boolean isFromOthor() {
        return fromOthor;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
