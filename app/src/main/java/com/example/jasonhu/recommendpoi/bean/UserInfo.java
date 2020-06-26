package com.example.jasonhu.recommendpoi.bean;

public class UserInfo {
    private String userName;
    private String phoneNumber;
    private String email;
    private String city;
    private String secret;
    private String logstate;
    private String head_picture;
    public UserInfo(){

    }
    /**
     * 设置城市
     * @param city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * 设置邮箱
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 设置登录状态
     * @param logstate
     */
    public void setLogstate(String logstate) {
        this.logstate = logstate;
    }

    /**
     * 设置电话号码
     * @param phoneNumber
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * 设置密码
     * @param secret
     */
    public void setSecret(String secret) {
        this.secret = secret;
    }

    /**
     * 设置用户名
     * @param userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 设置头像
     * @param head_picture
     */
    public void setHead_picture(String head_picture) {
        this.head_picture = head_picture;
    }

    /**
     * 获取城市
     * @return
     */
    public String getCity() {
        return city;
    }

    /**
     * 获取邮箱地址
     * @return
     */
    public String getEmail() {
        return email;
    }

    /**
     * 获取登录状态
     * @return
     */
    public String getLogstate() {
        return logstate;
    }

    /**
     * 获取电话号码
     * @return
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * 获取密码
     * @return
     */
    public String getSecret() {
        return secret;
    }

    /**
     * 获取用户名
     * @return
     */
    public String getUserName() {
        return userName;
    }

    public String getHead_picture() {
        return head_picture;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "userName='" + userName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", city='" + city + '\'' +
                ", secret='" + secret + '\'' +
                ", logstate='" + logstate + '\'' +
                ", head_picture='" + head_picture + '\'' +
                '}';
    }
}
