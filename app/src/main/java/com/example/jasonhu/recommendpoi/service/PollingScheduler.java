package com.example.jasonhu.recommendpoi.service;

import android.app.PendingIntent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Created by JasonHu 2020/5/25 20:45
 * @version 1.0
 */
public class PollingScheduler {
    private static PollingScheduler sInstance;
    private ScheduledExecutorService mScheduler;

    private PollingScheduler() {
        mScheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public static synchronized PollingScheduler getInstance() {
        if (sInstance == null) {
            sInstance = new PollingScheduler();
        }
        if (sInstance.mScheduler.isShutdown()) {
            sInstance.mScheduler = Executors.newSingleThreadScheduledExecutor();
        }
        return sInstance;
    }

    public void addScheduleTask(final PendingIntent pendingIntent, long initialDelay, long period) {
        Runnable command = new Runnable() {
            @Override
            public void run() {
                try {
                    pendingIntent.send();
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
            }
        };
        mScheduler.scheduleAtFixedRate(command, initialDelay, period, TimeUnit.MILLISECONDS);
    }

    public void clearScheduleTasks() {
        mScheduler.shutdownNow();
    }
}
