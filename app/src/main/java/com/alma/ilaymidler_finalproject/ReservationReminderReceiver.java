package com.alma.ilaymidler_finalproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.alma.ilaymidler_finalproject.utils.NotificationHelper;

public class ReservationReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // הפונקציה מופעלת אוטומטית כאשר ה-AlarmManager מגיע לזמן התזכורת.

        if (intent == null) {
            return;
            // אם משום מה לא התקבל Intent, אין מידע להציג ולכן עוצרים.
        }

        String courtName = intent.getStringExtra("courtName");
        // מקבל מה-Intent את שם המגרש.

        String bookingDate = intent.getStringExtra("bookingDate");
        // מקבל מה-Intent את תאריך ההזמנה.

        String startTime = intent.getStringExtra("startTime");
        // מקבל מה-Intent את שעת ההתחלה.

        String endTime = intent.getStringExtra("endTime");
        // מקבל מה-Intent את שעת הסיום.

        if (courtName == null) courtName = "Court";
        // אם שם המגרש לא התקבל, מציגים Court כברירת מחדל.

        if (bookingDate == null) bookingDate = "";
        // אם התאריך לא התקבל, משתמשים בטקסט ריק.

        if (startTime == null) startTime = "";
        // אם שעת ההתחלה לא התקבלה, משתמשים בטקסט ריק.

        if (endTime == null) endTime = "";
        // אם שעת הסיום לא התקבלה, משתמשים בטקסט ריק.

        NotificationHelper.showReminderNotification(
                context,
                courtName,
                bookingDate,
                startTime,
                endTime
        );
        // מציג התראת תזכורת למשתמש על ההזמנה הקרובה.
    }
}