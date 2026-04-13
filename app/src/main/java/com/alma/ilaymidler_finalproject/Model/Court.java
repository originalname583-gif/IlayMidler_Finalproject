package com.alma.ilaymidler_finalproject.Model;

import java.util.List;

public class Court {

    private String id;
    private String name;
    private String city;
    private String address;
    private String type;
    private List<TimeSlot> timeSlots;

    // Required for Firebase
    public Court() {
    }

    public Court(String id, String name, String city, String address, String type) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.address = address;
        this.type = type;
        this.timeSlots = TimeSlot.generateDailySlots(); // ✅ FIXED HERE
    }

    // GETTERS & SETTERS

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name != null ? name : "";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city != null ? city : "";
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address != null ? address : "";
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type != null ? type : "";
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<TimeSlot> getTimeSlots() {
        if (timeSlots == null) {
            timeSlots = TimeSlot.generateDailySlots(); // ✅ important fallback
        }
        return timeSlots;
    }

    public void setTimeSlots(List<TimeSlot> timeSlots) {
        this.timeSlots = timeSlots;
    }

    @Override
    public String toString() {
        return name + " - " + city + " (" + address + ")";
    }
}