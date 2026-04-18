package com.alma.ilaymidler_finalproject.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TimeSlot {

    private String id;
    private String startTime;
    private String endTime;
    private boolean reserved;
    private String reservedByUserId;

    public TimeSlot() {
    }

    public TimeSlot(String id, String startTime, String endTime, boolean reserved) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reserved = reserved;
        this.reservedByUserId = "";
    }

    public static List<TimeSlot> generateDailySlots() {
        List<TimeSlot> slots = new ArrayList<>();
        for (int hour = 8; hour < 23; hour++) {
            String start = String.format(Locale.getDefault(), "%02d:00", hour);
            String end = String.format(Locale.getDefault(), "%02d:00", hour + 1);
            String id = start + "_" + end;
            slots.add(new TimeSlot(id, start, end, false));
        }
        return slots;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public boolean isReserved() { return reserved; }

    public void setReserved(boolean reserved) {
        this.reserved = reserved;
        if (!reserved) {
            this.reservedByUserId = "";
        }
    }

    public String getReservedByUserId() { return reservedByUserId; }

    public void setReservedByUserId(String reservedByUserId) {
        this.reservedByUserId = reservedByUserId != null ? reservedByUserId : "";
        this.reserved = !this.reservedByUserId.isEmpty();
    }
}