package innsync.model;

public class Room {

    private final String number;
    private String type;
    private int capacity;
    private double rate;

    public Room(String number, String type, int capacity, double rate) {
        this.number = number;
        this.type = type;
        this.capacity = capacity;
        this.rate = rate;
    }

    public String getNumber() { return number; }
    public String getType() { return type; }
    public int getCapacity() { return capacity; }
    public double getRate() { return rate; }

    public void setType(String type) { this.type = type; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public void setRate(double rate) { this.rate = rate; }

    @Override
    public String toString() {
        return number + " - " + type;
    }
}
