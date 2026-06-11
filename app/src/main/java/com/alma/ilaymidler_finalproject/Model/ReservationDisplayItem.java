package com.alma.ilaymidler_finalproject.Model;

public class ReservationDisplayItem {

    private Reservation reservation;
    // שומר את פרטי ההזמנה.

    private Court court;
    // שומר את פרטי המגרש שאליו שייכת ההזמנה.

    public ReservationDisplayItem() {
        // בנאי ריק.
        // Firebase משתמש בו כאשר הוא טוען נתונים מהמסד.
    }

    public ReservationDisplayItem(Reservation reservation, Court court) {

        this.reservation = reservation;
        // שומר את אובייקט ההזמנה.

        this.court = court;
        // שומר את אובייקט המגרש.
    }

    public Reservation getReservation() {

        return reservation;
        // מחזיר את פרטי ההזמנה.
    }

    public void setReservation(Reservation reservation) {

        this.reservation = reservation;
        // מעדכן את ההזמנה השמורה באובייקט.
    }

    public Court getCourt() {

        return court;
        // מחזיר את פרטי המגרש.
    }

    public void setCourt(Court court) {

        this.court = court;
        // מעדכן את המגרש השמור באובייקט.
    }
}