package com.alma.ilaymidler_finalproject.Model;

public class ReservationDisplayItem {

    private Reservation reservation;
    private Court court;

    public ReservationDisplayItem() {
    }

    public ReservationDisplayItem(Reservation reservation, Court court) {
        this.reservation = reservation;
        this.court = court;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public Court getCourt() {
        return court;
    }

    public void setCourt(Court court) {
        this.court = court;
    }
}