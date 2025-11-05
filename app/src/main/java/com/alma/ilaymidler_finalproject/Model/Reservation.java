package com.alma.ilaymidler_finalproject.Model;

public class Reservation {
    private String id;
    private User user;
    private TimeSlot timeSlot;

    public Reservation() {
    }

    public Reservation(String id, User user, TimeSlot timeSlot) {
        this.id = id;
        this.user = user;
        this.timeSlot = timeSlot;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public TimeSlot getTimeSlot() { return timeSlot; }
    public void setTimeSlot(TimeSlot timeSlot) { this.timeSlot = timeSlot; }

    @Override
    public String toString() {
        return "Reservation{" +
                "id='" + id + '\'' +
                ", user=" + user +
                ", timeSlot=" + timeSlot +
                '}';
    }
}
