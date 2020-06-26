package com.example.jasonhu.recommendpoi.service.serviceUtils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.jasonhu.recommendpoi.service.PollingScheduler;
import com.example.jasonhu.recommendpoi.service.PollingService;

/**
 * @author Created by JasonHu 2020/5/25 20:45
 * @version 1.0
 */
public class PollingUtil {
    /**
     * 开始轮询服务
     */
    public static void startPollingService(final Context context, String action) {
        //包装需要执行Service的Intent
        Intent intent = new Intent(context, PollingService.class);
        intent.setAction(action);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        PollingScheduler.getInstance().addScheduleTask(pendingIntent, 0, PollingService.DEFAULT_MIN_POLLING_INTERVAL);
    }

    public static void stopPollingServices() {
        PollingScheduler.getInstance().clearScheduleTasks();
    }
}
