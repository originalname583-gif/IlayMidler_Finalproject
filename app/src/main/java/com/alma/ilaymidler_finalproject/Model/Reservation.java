package com.alma.ilaymidler_finalproject.Model;

public class Reservation {

    private String id;
    // מזהה ייחודי של ההזמנה.

    private String courtId;
    // מזהה המגרש שעליו בוצעה ההזמנה.

    private String userId;
    // מזהה המשתמש שביצע את ההזמנה.

    private String userName;
    // שם המשתמש שביצע את ההזמנה.

    private String slotId;
    // מזהה שעת ההזמנה.

    private String startTime;
    // שעת התחלת ההזמנה.

    private String endTime;
    // שעת סיום ההזמנה.

    private String bookingDate;
    // התאריך שבו בוצעה ההזמנה.

    private long createdAt;
    // הזמן שבו נוצרה ההזמנה (Timestamp).

    public Reservation() {
        // בנאי ריק.
        // Firebase משתמש בו כאשר הוא טוען נתונים מהמסד.
    }

    public Reservation(String id,
                       String courtId,
                       String userId,
                       String userName,
                       String slotId,
                       String startTime,
                       String endTime,
                       String bookingDate,
                       long createdAt) {

        this.id = id;
        // שומר את מזהה ההזמנה.

        this.courtId = courtId;
        // שומר את מזהה המגרש.

        this.userId = userId;
        // שומר את מזהה המשתמש.

        this.userName = userName;
        // שומר את שם המשתמש.

        this.slotId = slotId;
        // שומר את מזהה השעה.

        this.startTime = startTime;
        // שומר את שעת ההתחלה.

        this.endTime = endTime;
        // שומר את שעת הסיום.

        this.bookingDate = bookingDate;
        // שומר את תאריך ההזמנה.

        this.createdAt = createdAt;
        // שומר את זמן יצירת ההזמנה.
    }

    public String getId() {

        return id != null ? id : "";
        // מחזיר את מזהה ההזמנה.
        // אם הערך null מחזיר מחרוזת ריקה.
    }

    public void setId(String id) {

        this.id = id;
        // מעדכן את מזהה ההזמנה.
    }

    public String getCourtId() {

        return courtId != null ? courtId : "";
        // מחזיר את מזהה המגרש.
    }

    public void setCourtId(String courtId) {

        this.courtId = courtId;
        // מעדכן את מזהה המגרש.
    }

    public String getUserId() {

        return userId != null ? userId : "";
        // מחזיר את מזהה המשתמש.
    }

    public void setUserId(String userId) {

        this.userId = userId;
        // מעדכן את מזהה המשתמש.
    }

    public String getUserName() {

        return userName != null ? userName : "";
        // מחזיר את שם המשתמש.
    }

    public void setUserName(String userName) {

        this.userName = userName;
        // מעדכן את שם המשתמש.
    }

    public String getSlotId() {

        return slotId != null ? slotId : "";
        // מחזיר את מזהה השעה.
    }

    public void setSlotId(String slotId) {

        this.slotId = slotId;
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

    public String getBookingDate() {

        return bookingDate != null ? bookingDate : "";
        // מחזיר את תאריך ההזמנה.
    }

    public void setBookingDate(String bookingDate) {

        this.bookingDate = bookingDate;
        // מעדכן את תאריך ההזמנה.
    }

    public long getCreatedAt() {

        return createdAt;
        // מחזיר את זמן יצירת ההזמנה.
    }

    public void setCreatedAt(long createdAt) {

        this.createdAt = createdAt;
        // מעדכן את זמן יצירת ההזמנה.
    }
}