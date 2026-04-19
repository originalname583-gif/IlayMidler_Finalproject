package com.alma.ilaymidler_finalproject.utils;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.alma.ilaymidler_finalproject.R;
import com.alma.ilaymidler_finalproject.ReservationReminderReceiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NotificationHelper {

    private static final String CHANNEL_ID = "fieldtime_reservations";

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Reservation Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Notifications for court reservations");

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    public static void showReservationNotification(Context context, String courtName, String date, String start, String end) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Reservation confirmed")
                .setContentText(courtName + " | " + date + " | " + start + " - " + end)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat.from(context).notify((int) System.currentTimeMillis(), builder.build());
    }

    public static void showReminderNotification(Context context, String courtName, String date, String start, String end) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Upcoming reservation reminder")
                .setContentText(courtName + " | " + date + " | " + start + " - " + end)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat.from(context).notify((int) System.currentTimeMillis(), builder.build());
    }

    public static void scheduleReservationReminder(Context context,
                                                   String courtName,
                                                   String bookingDate,
                                                   String startTime,
                                                   String endTime) {
        try {
            String dateTimeString = bookingDate + " " + startTime;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date reservationDateTime = sdf.parse(dateTimeString);

            if (reservationDateTime == null) return;

            long reminderTimeMillis = reservationDateTime.getTime() - (30 * 60 * 1000L);
            long now = System.currentTimeMillis();

            // Don't schedule reminders in the past
            if (reminderTimeMillis <= now) {
                return;
            }

            Intent intent = new Intent(context, ReservationReminderReceiver.class);
            intent.putExtra("courtName", courtName);
            intent.putExtra("bookingDate", bookingDate);
            intent.putExtra("startTime", startTime);
            intent.putExtra("endTime", endTime);

            int requestCode = (courtName + bookingDate + startTime + endTime).hashCode();

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager == null) return;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        reminderTimeMillis,
                        pendingIntent
                );
            } else {
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        reminderTimeMillis,
                        pendingIntent
                );
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}