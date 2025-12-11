package com.alma.ilaymidler_finalproject.Model;

import java.util.List;

public class Court {

    private String id;
    private String name;
    private String city;
    private String address;
    private List<TimeSlot> timeSlots;

    public Court() {}

    public Court(String id, String name, String city, String address) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.address = address;
        this.timeSlots = TimeSlot.generateDefaultTimeslots();
    }

    // GETTERS & SETTERS
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public List<TimeSlot> getTimeSlots() { return timeSlots; }
    public void setTimeSlots(List<TimeSlot> timeSlots) { this.timeSlots = timeSlots; }

    @Override
    public String toString() {
        return name + " - " + city + " (" + address + ")";
    }
}
