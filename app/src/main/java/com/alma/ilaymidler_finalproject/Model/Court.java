package com.alma.ilaymidler_finalproject.Model;

import java.util.List;

public class Court {

    private String id;
    // מזהה ייחודי של המגרש ב-Firebase.

    private String name;
    // שם המגרש.

    private String city;
    // העיר שבה נמצא המגרש.

    private String address;
    // הכתובת של המגרש.

    private String type;
    // סוג המגרש (דשא, סינטטי וכו').

    private List<TimeSlot> timeSlots;
    // רשימת שעות ההזמנה של המגרש.

    public Court() {
        // בנאי ריק.
        // Firebase משתמש בו כאשר הוא טוען נתונים מהמסד.
    }

    public Court(String id, String name, String city, String address, String type) {

        this.id = id;
        // שומר את מזהה המגרש.

        this.name = name;
        // שומר את שם המגרש.

        this.city = city;
        // שומר את העיר.

        this.address = address;
        // שומר את הכתובת.

        this.type = type;
        // שומר את סוג המגרש.

        this.timeSlots = TimeSlot.generateDailySlots();
        // יוצר באופן אוטומטי את כל שעות ההזמנה היומיות של המגרש.
    }

    public String getId() {

        return id;
        // מחזיר את מזהה המגרש.
    }

    public void setId(String id) {

        this.id = id;
        // מעדכן את מזהה המגרש.
    }

    public String getName() {

        return name != null ? name : "";
        // מחזיר את שם המגרש.
        // אם השם null מחזיר טקסט ריק כדי למנוע שגיאות.
    }

    public void setName(String name) {

        this.name = name;
        // מעדכן את שם המגרש.
    }

    public String getCity() {

        return city != null ? city : "";
        // מחזיר את העיר.
        // אם העיר null מחזיר טקסט ריק.
    }

    public void setCity(String city) {

        this.city = city;
        // מעדכן את העיר.
    }

    public String getAddress() {

        return address != null ? address : "";
        // מחזיר את הכתובת.
        // אם הכתובת null מחזיר טקסט ריק.
    }

    public void setAddress(String address) {

        this.address = address;
        // מעדכן את הכתובת.
    }

    public String getType() {

        return type != null ? type : "";
        // מחזיר את סוג המגרש.
        // אם הסוג null מחזיר טקסט ריק.
    }

    public void setType(String type) {

        this.type = type;
        // מעדכן את סוג המגרש.
    }

    public List<TimeSlot> getTimeSlots() {

        if (timeSlots == null) {
            // בודק האם רשימת השעות קיימת.

            timeSlots = TimeSlot.generateDailySlots();
            // אם לא קיימת, יוצר רשימת שעות חדשה.
        }

        return timeSlots;
        // מחזיר את רשימת שעות ההזמנה.
    }

    public void setTimeSlots(List<TimeSlot> timeSlots) {

        this.timeSlots = timeSlots;
        // מעדכן את רשימת שעות ההזמנה.
    }

    @Override
    public String toString() {

        return name + " - " + city + " (" + address + ")";
        // מחזיר תיאור טקסטואלי של המגרש.
        // שימושי ל-Spinner, Log או Debug.
    }
}