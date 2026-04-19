package com.alma.ilaymidler_finalproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.alma.ilaymidler_finalproject.utils.NotificationHelper;

public class ReservationReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String courtName = intent.getStringExtra("courtName");
        String bookingDate = intent.getStringExtra("bookingDate");
        String startTime = intent.getStringExtra("startTime");
        String endTime = intent.getStringExtra("endTime");

        if (courtName == null) courtName = "Court";
        if (bookingDate == null) bookingDate = "";
        if (startTime == null) startTime = "";
        if (endTime == null) endTime = "";

        NotificationHelper.showReminderNotification(
                context,
                courtName,
                bookingDate,
                startTime,
                endTime
        );
    }
}