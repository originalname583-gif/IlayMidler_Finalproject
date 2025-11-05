package com.alma.ilaymidler_finalproject.Model;

public class TimeSlot {

    private String id;
    private String date;
    private String startTime;
    private String endTime;
    private boolean reserved;

    public TimeSlot() {
    }

    public TimeSlot(String id, String date, String startTime, String endTime, boolean reserved) {
        this.id = id;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reserved = reserved;
    }

    // Getter & Setter עבור id
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public boolean isReserved() { return reserved; }
    public void setReserved(boolean reserved) { this.reserved = reserved; }

    @Override
    public String toString() {
        return "TimeSlot{" +
                "id='" + id + '\'' +
                ", date='" + date + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", reserved=" + reserved +
                '}';
    }
}
