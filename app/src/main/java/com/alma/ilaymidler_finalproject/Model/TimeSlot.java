package com.alma.ilaymidler_finalproject.Model;

import java.util.ArrayList;
import java.util.List;

public class TimeSlot {

    private String id;
    private String date;
    private String startTime;
    private String endTime;
    private boolean reserved;

    public TimeSlot() {}

    public TimeSlot(String id, String date, String startTime, String endTime, boolean reserved) {
        this.id = id;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reserved = reserved;
    }

    public TimeSlot(String startTime, String endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.id = startTime + "-" + endTime;
        this.date = "";
        this.reserved = false;
    }

    public static List<TimeSlot> generateDefaultTimeslots() {
        List<TimeSlot> slots = new ArrayList<>();
        int startHour = 12;
        int endHour = 23;
        int currentHour = startHour;
        int currentMinute = 0;

        while (currentHour < endHour) {
            String start = formatTime(currentHour, currentMinute);
            int endTotalMinutes = currentHour * 60 + currentMinute + 90;
            int endH = endTotalMinutes / 60;
            int endM = endTotalMinutes % 60;
            String end = formatTime(endH, endM);

            slots.add(new TimeSlot(start, end));

            currentHour = endH;
            currentMinute = endM;
            if (currentHour >= endHour) break;
        }
        return slots;
    }

    private static String formatTime(int hour, int minute) {
        return String.format("%02d:%02d", hour, minute);
    }

    // GETTERS & SETTERS
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
        return startTime + " - " + endTime + (reserved ? " (Reserved)" : "");
    }
}
