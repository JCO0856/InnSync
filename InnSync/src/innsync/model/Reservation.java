package innsync.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Reservation {

    private final int id;
    private String guestName;
    private String contact;
    private Room room;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private int guests;
    private ReservationStatus status = ReservationStatus.BOOKED;

    public Reservation(int id, String guestName, String contact, Room room,
                       LocalDate checkIn, LocalDate checkOut, int guests) {
        this.id = id;
        this.guestName = guestName;
        this.contact = contact;
        this.room = room;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.guests = guests;
    }

    public int getId() { return id; }
    public String getGuestName() { return guestName; }
    public String getContact() { return contact; }
    public Room getRoom() { return room; }
    public LocalDate getCheckIn() { return checkIn; }
    public LocalDate getCheckOut() { return checkOut; }
    public int getGuests() { return guests; }
    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }

    // Replaces all editable fields at once (used by the Edit dialog). 
    public void update(String guestName, String contact, Room room, LocalDate checkIn,
                       LocalDate checkOut, int guests, ReservationStatus status) {
        this.guestName = guestName;
        this.contact = contact;
        this.room = room;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.guests = guests;
        this.status = status;
    }

    public long getNights() {
        return ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    public double getTotal() {
        return getNights() * room.getRate();
    }

    /** A reservation that still holds its room. */
    public boolean isActive() {
        return status.holdsRoom();
    }
}
