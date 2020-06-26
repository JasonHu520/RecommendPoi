package com.example.jasonhu.recommendpoi.BaseClass.Callback;

public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
