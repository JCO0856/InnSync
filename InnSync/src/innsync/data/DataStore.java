package innsync.data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import innsync.model.Reservation;
import innsync.model.ReservationStatus;
import innsync.model.Room;

//temporary lang ibang code dito hanaggat wala pa database
public class DataStore {

    private static final DataStore INSTANCE = new DataStore();

    public static DataStore get() {
        return INSTANCE;
    }

    private final List<Room> rooms = new ArrayList<>();
    private final List<Reservation> reservations = new ArrayList<>();
    private final List<Runnable> listeners = new ArrayList<>();
    private int nextId = 1001;

    private DataStore() {
        seed();
    }

    //sample data since wala pa database (tatanggalin rin)
    private void seed() {
        rooms.add(new Room("101", "Single", 1, 1200));
        rooms.add(new Room("102", "Single", 1, 1200));
        rooms.add(new Room("201", "Double", 2, 1800));
        rooms.add(new Room("202", "Double", 2, 1800));
        rooms.add(new Room("203", "Double", 3, 2100));
        rooms.add(new Room("301", "Deluxe", 4, 3000));
        rooms.add(new Room("302", "Deluxe", 4, 3000));
        rooms.add(new Room("401", "Suite", 6, 5000));
    }

    // ---------- change notification ----------
    public void addChangeListener(Runnable r) {
        listeners.add(r);
    }

    private void fireChanged() {
        for (Runnable r : listeners) {
            r.run();
        }
    }

    // ---------- rooms ----------
    public List<Room> getRooms() {
        return Collections.unmodifiableList(new ArrayList<>(rooms));
    }

    /** @return false if a room with this number already exists */
    public boolean addRoom(Room room) {
        for (Room r : rooms) {
            if (r.getNumber().equalsIgnoreCase(room.getNumber())) {
                return false;
            }
        }
        rooms.add(room);
        fireChanged();
        return true;
    }

    // Current status of a room: Occupied, Reserved (today) or Available. 
    public String getRoomStatus(Room room) {
        LocalDate today = LocalDate.now();
        for (Reservation r : reservations) {
            if (r.getRoom() == room && r.getStatus() == ReservationStatus.CHECKED_IN) {
                return "Occupied";
            }
        }
        for (Reservation r : reservations) {
            boolean waiting = r.getStatus() == ReservationStatus.BOOKED
                    || r.getStatus() == ReservationStatus.CONFIRMED;
            if (r.getRoom() == room && waiting
                    && !today.isBefore(r.getCheckIn()) && today.isBefore(r.getCheckOut())) {
                return "Reserved";
            }
        }
        return "Available";
    }

    public boolean isRoomAvailable(Room room, LocalDate in, LocalDate out) {
        return isRoomAvailable(room, in, out, null);
    }

    /** @param ignore a reservation to leave out of the check (the one being edited), or null */
    public boolean isRoomAvailable(Room room, LocalDate in, LocalDate out, Reservation ignore) {
        for (Reservation r : reservations) {
            if (r != ignore && r.getRoom() == room && r.isActive()
                    && in.isBefore(r.getCheckOut()) && out.isAfter(r.getCheckIn())) {
                return false;
            }
        }
        return true;
    }

    public List<Room> getAvailableRooms(LocalDate in, LocalDate out, int guests) {
        return getAvailableRooms(in, out, guests, null);
    }

    public List<Room> getAvailableRooms(LocalDate in, LocalDate out, int guests, Reservation ignore) {
        List<Room> result = new ArrayList<>();
        for (Room r : rooms) {
            if (r.getCapacity() >= guests && isRoomAvailable(r, in, out, ignore)) {
                result.add(r);
            }
        }
        return result;
    }

    // ---------- reservations ----------
    public List<Reservation> getReservations() {
        return Collections.unmodifiableList(new ArrayList<>(reservations));
    }

    public Reservation findReservation(int id) {
        for (Reservation r : reservations) {
            if (r.getId() == id) {
                return r;
            }
        }
        return null;
    }

    public Reservation addReservation(String guestName, String contact, Room room,
                                      LocalDate in, LocalDate out, int guests) {
        Reservation r = new Reservation(nextId++, guestName, contact, room, in, out, guests);
        reservations.add(r);
        fireChanged();
        return r;
    }

    /** @return false (and changes nothing) if the room is taken for those dates */
    public boolean updateReservation(Reservation r, String guestName, String contact, Room room,
                                     LocalDate in, LocalDate out, int guests,
                                     ReservationStatus status) {
        if (status.holdsRoom() && !isRoomAvailable(room, in, out, r)) {
            return false;
        }
        r.update(guestName, contact, room, in, out, guests, status);
        fireChanged();
        return true;
    }

    public void setStatus(Reservation r, ReservationStatus status) {
        r.setStatus(status);
        fireChanged();
    }

    public void deleteReservation(Reservation r) {
        reservations.remove(r);
        fireChanged();
    }

    public int countByStatus(ReservationStatus status) {
        int n = 0;
        for (Reservation r : reservations) {
            if (r.getStatus() == status) {
                n++;
            }
        }
        return n;
    }

    // Checked-in guests whose check-out date is today (or already passed). 
    public int countDeparting() {
        LocalDate today = LocalDate.now();
        int n = 0;
        for (Reservation r : reservations) {
            if (r.getStatus() == ReservationStatus.CHECKED_IN && !r.getCheckOut().isAfter(today)) {
                n++;
            }
        }
        return n;
    }
}
