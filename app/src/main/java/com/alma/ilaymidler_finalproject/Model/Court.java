package com.alma.ilaymidler_finalproject.Model;

import java.util.ArrayList;
import java.util.List;

public class Court {
    private int id;
    private String name;
    private String location;
    private List<TimeSlot> timeSlots;

    public Court() {
        this.timeSlots = new ArrayList<>();
    }

    public Court(int id, String name, String location) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.timeSlots = new ArrayList<>();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public List<TimeSlot> getTimeSlots() { return timeSlots; }
    public void setTimeSlots(List<TimeSlot> timeSlots) { this.timeSlots = timeSlots; }

    public void addTimeSlot(TimeSlot slot) {
        this.timeSlots.add(slot);
    }

    @Override
    public String toString() {
        return "Court{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", timeSlots=" + timeSlots +
                '}';
    }
}
