package innsync.model;

public enum ReservationStatus {
    BOOKED("Booked"),
    CONFIRMED("Confirmed"),
    CHECKED_IN("Checked In"),
    CHECKED_OUT("Checked Out"),
    CANCELLED("Cancelled");

    private final String label;

    ReservationStatus(String label) {
        this.label = label;
    }

    /** Statuses that still hold a room. */
    public boolean holdsRoom() {
        return this == BOOKED || this == CONFIRMED || this == CHECKED_IN;
    }

    @Override
    public String toString() {
        return label;
    }
}
