package com.alma.ilaymidler_finalproject.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TimeSlot {

    private String id;
    // מזהה ייחודי של חלון הזמן.

    private String startTime;
    // שעת ההתחלה של חלון הזמן.

    private String endTime;
    // שעת הסיום של חלון הזמן.

    private boolean reserved;
    // האם חלון הזמן תפוס או פנוי.

    private String reservedByUserId;
    // מזהה המשתמש שהזמין את השעה.

    public TimeSlot() {
        // בנאי ריק.
        // Firebase משתמש בו כאשר הוא טוען נתונים מהמסד.
    }

    public TimeSlot(String id, String startTime, String endTime, boolean reserved) {

        this.id = id;
        // שומר את מזהה חלון הזמן.

        this.startTime = startTime;
        // שומר את שעת ההתחלה.

        this.endTime = endTime;
        // שומר את שעת הסיום.

        this.reserved = reserved;
        // שומר האם השעה תפוסה.

        this.reservedByUserId = "";
        // בהתחלה אף אחד לא הזמין את השעה.
    }

    public static List<TimeSlot> generateDailySlots() {

        List<TimeSlot> slots = new ArrayList<>();
        // יוצרים רשימה חדשה שתכיל את כל שעות היום.

        for (int hour = 8; hour < 23; hour++) {
            // רץ משעה 08:00 בבוקר עד 23:00 בלילה.

            String start = String.format(
                    Locale.getDefault(),
                    "%02d:00",
                    hour
            );
            // יוצר את שעת ההתחלה.

            String end = String.format(
                    Locale.getDefault(),
                    "%02d:00",
                    hour + 1
            );
            // יוצר את שעת הסיום.

            String id = start + "_" + end;
            // יוצר מזהה ייחודי לשעה.

            slots.add(
                    new TimeSlot(
                            id,
                            start,
                            end,
                            false
                    )
            );
            // מוסיף שעה חדשה לרשימה.
        }

        return slots;
        // מחזיר את כל שעות היום.
    }

    public String getId() {

        return id != null ? id : "";
        // מחזיר את מזהה השעה.
    }

    public void setId(String id) {

        this.id = id;
        // מעדכן את מזהה השעה.
    }

    public String getStartTime() {

        return startTime != null ? startTime : "";
        // מחזיר את שעת ההתחלה.
    }

    public void setStartTime(String startTime) {

        this.startTime = startTime;
        // מעדכן את שעת ההתחלה.
    }

    public String getEndTime() {

        return endTime != null ? endTime : "";
        // מחזיר את שעת הסיום.
    }

    public void setEndTime(String endTime) {

        this.endTime = endTime;
        // מעדכן את שעת הסיום.
    }

    public boolean isReserved() {

        return reserved;
        // מחזיר האם השעה תפוסה.
    }

    public void setReserved(boolean reserved) {

        this.reserved = reserved;
        // מעדכן האם השעה תפוסה.

        if (!reserved) {
            // אם השעה שוחררה.

            this.reservedByUserId = "";
            // מוחק את מזהה המשתמש שהזמין.
        }
    }

    public String getReservedByUserId() {

        return reservedByUserId != null ? reservedByUserId : "";
        // מחזיר את מזהה המשתמש שהזמין.
    }

    public void setReservedByUserId(String reservedByUserId) {

        this.reservedByUserId =
                reservedByUserId != null
                        ? reservedByUserId
                        : "";
        // שומר את מזהה המשתמש בצורה בטוחה.

        this.reserved =
                !this.reservedByUserId.isEmpty();
        // אם יש מזהה משתמש,
        // השעה מסומנת כתפוסה.
    }
}