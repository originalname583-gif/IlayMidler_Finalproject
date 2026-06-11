package com.alma.ilaymidler_finalproject.utils;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.alma.ilaymidler_finalproject.R;
import com.alma.ilaymidler_finalproject.ReservationReminderReceiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NotificationHelper {

    private static final String CHANNEL_ID = "fieldtime_reservations";
    // מזהה קבוע לערוץ ההתראות של האפליקציה.

    public static void createNotificationChannel(Context context) {
        // הפונקציה יוצרת ערוץ התראות.
        // באנדרואיד 8 ומעלה חייבים ערוץ כדי שהתראות יעבדו.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // בודק אם גרסת האנדרואיד היא 8 ומעלה.

            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Reservation Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            // יוצר ערוץ חדש להתראות של הזמנות.

            channel.setDescription("Notifications for court reservations");
            // מוסיף תיאור לערוץ ההתראות.

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            // מקבל את מנהל ההתראות של המכשיר.

            if (manager != null) {
                manager.createNotificationChannel(channel);
                // יוצר את הערוץ בפועל.
            }
        }
    }

    public static void showReservationNotification(Context context,
                                                   String courtName,
                                                   String date,
                                                   String start,
                                                   String end) {
        // הפונקציה מציגה התראה כאשר הזמנה בוצעה בהצלחה.

        if (!canShowNotifications(context)) {
            return;
            // אם אין הרשאת התראות, לא מציגים התראה כדי למנוע קריסה.
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                // האייקון שיופיע בהתראה.

                .setContentTitle("Reservation confirmed")
                // הכותרת של ההתראה.

                .setContentText(courtName + " | " + date + " | " + start + " - " + end)
                // התוכן של ההתראה: שם מגרש, תאריך ושעה.

                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // רמת חשיבות רגילה להתראה.

                .setAutoCancel(true);
        // ההתראה תיעלם לאחר שהמשתמש ילחץ עליה.

        NotificationManagerCompat.from(context).notify(
                (int) System.currentTimeMillis(),
                builder.build()
        );
        // מציג את ההתראה בפועל.
    }

    public static void showReminderNotification(Context context,
                                                String courtName,
                                                String date,
                                                String start,
                                                String end) {
        // הפונקציה מציגה תזכורת לפני ההזמנה.

        if (!canShowNotifications(context)) {
            return;
            // אם אין הרשאה להתראות, לא מציגים התראה.
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                // האייקון של ההתראה.

                .setContentTitle("Upcoming reservation reminder")
                // הכותרת של התזכורת.

                .setContentText(courtName + " | " + date + " | " + start + " - " + end)
                // פרטי ההזמנה שמוצגים בהתראה.

                .setPriority(NotificationCompat.PRIORITY_HIGH)
                // חשיבות גבוהה כי זו תזכורת לפני הזמנה.

                .setAutoCancel(true);
        // ההתראה תיעלם אחרי לחיצה.

        NotificationManagerCompat.from(context).notify(
                (int) System.currentTimeMillis(),
                builder.build()
        );
        // מציג את התזכורת בפועל.
    }

    public static void scheduleReservationReminder(Context context,
                                                   String courtName,
                                                   String bookingDate,
                                                   String startTime,
                                                   String endTime) {
        // הפונקציה קובעת תזכורת אוטומטית 30 דקות לפני ההזמנה.

        try {
            String dateTimeString = bookingDate + " " + startTime;
            // מחבר את התאריך והשעה למחרוזת אחת.

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            // מגדיר את הפורמט של התאריך והשעה.

            Date reservationDateTime = sdf.parse(dateTimeString);
            // ממיר את הטקסט של התאריך והשעה לאובייקט Date.

            if (reservationDateTime == null) {
                return;
                // אם ההמרה נכשלה, מפסיקים.
            }

            long reminderTimeMillis = reservationDateTime.getTime() - (30 * 60 * 1000L);
            // מחשב את זמן התזכורת: 30 דקות לפני ההזמנה.

            long now = System.currentTimeMillis();
            // הזמן הנוכחי.

            if (reminderTimeMillis <= now) {
                return;
                // אם זמן התזכורת כבר עבר, לא קובעים תזכורת.
            }

            Intent intent = new Intent(context, ReservationReminderReceiver.class);
            // יוצר Intent שיפעיל את ה-Receiver בזמן התזכורת.

            intent.putExtra("courtName", courtName);
            // שולח את שם המגרש ל-Receiver.

            intent.putExtra("bookingDate", bookingDate);
            // שולח את תאריך ההזמנה.

            intent.putExtra("startTime", startTime);
            // שולח את שעת ההתחלה.

            intent.putExtra("endTime", endTime);
            // שולח את שעת הסיום.

            int requestCode = (courtName + bookingDate + startTime + endTime).hashCode();
            // יוצר קוד ייחודי לכל תזכורת כדי שלא ידרסו אחת את השנייה.

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            // יוצר PendingIntent שהמערכת תפעיל בזמן המתאים.

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            // מקבל את AlarmManager שאחראי על תזמונים.

            if (alarmManager == null) {
                return;
                // אם אין AlarmManager, לא ניתן לקבוע תזכורת.
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                return;
                // באנדרואיד 12 ומעלה צריך הרשאה מיוחדת לשעון מדויק.
                // אם אין הרשאה, לא קובעים תזכורת מדויקת.
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        reminderTimeMillis,
                        pendingIntent
                );
                // קובע תזכורת מדויקת גם אם המכשיר במצב חיסכון.
            } else {
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        reminderTimeMillis,
                        pendingIntent
                );
                // קובע תזכורת מדויקת בגרסאות ישנות יותר.
            }

        } catch (ParseException e) {
            e.printStackTrace();
            // אם הייתה בעיה בקריאת התאריך, מדפיסים את השגיאה.
        }
    }

    private static boolean canShowNotifications(Context context) {
        // הפונקציה בודקת האם מותר לאפליקציה להציג התראות.

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true;
            // לפני Android 13 לא צריך לבקש הרשאת התראות בזמן ריצה.
        }

        return ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED;
        // באנדרואיד 13 ומעלה בודקים אם המשתמש אישר התראות.
    }
}