package com.alma.ilaymidler_finalproject.Model;

public class Reservation {
    private String id;
    private String courtId;
    private String userId;
    private String userName;
    private String slotId;
    private String startTime;
    private String endTime;
    private String bookingDate;
    private long createdAt;

    public Reservation() {
    }

    public Reservation(String id, String courtId, String userId, String userName,
                       String slotId, String startTime, String endTime,
                       String bookingDate, long createdAt) {
        this.id = id;
        this.courtId = courtId;
        this.userId = userId;
        this.userName = userName;
        this.slotId = slotId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.bookingDate = bookingDate;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCourtId() { return courtId; }
    public void setCourtId(String courtId) { this.courtId = courtId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getSlotId() { return slotId; }
    public void setSlotId(String slotId) { this.slotId = slotId; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getBookingDate() { return bookingDate; }
    public void setBookingDate(String bookingDate) { this.bookingDate = bookingDate; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}