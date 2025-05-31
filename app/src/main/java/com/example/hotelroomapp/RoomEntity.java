package com.example.hotelroomapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "room")
public class RoomEntity {

    @PrimaryKey
    int number;
    String type;
    double price;
    boolean available;
    boolean allowsPets;
    boolean smokingAllowed;

    public RoomEntity(int number, String type, double price, boolean available, boolean allowsPets, boolean smokingAllowed) {
        this.number = number;
        this.type = type;
        this.price = price;
        this.available = available;
        this.allowsPets = allowsPets;
        this.smokingAllowed = smokingAllowed;
    }

    public int getNumber() { return number; }
    public String getType() { return type; }
    public double getPrice() { return price; }
    public boolean isAvailable() { return available; }
    public boolean isAllowsPets() { return allowsPets; }
    public boolean isSmokingAllowed() { return smokingAllowed; }

    public void setNumber(int number) { this.number = number; }
    public void setType(String type) { this.type = type; }
    public void setPrice(double price) { this.price = price; }
    public void setAvailable(boolean available) { this.available = available; }
    public void setAllowsPets(boolean allowsPets) { this.allowsPets = allowsPets; }
    public void setSmokingAllowed(boolean smokingAllowed) { this.smokingAllowed = smokingAllowed; }
}
