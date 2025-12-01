package com.example.hotelmanagementsystem;

import java.math.BigDecimal;

public class Room {
    public int roomId;
    public String roomNumber;
    public String typeName;
    public BigDecimal price;
    public double discount;

    public Room(int roomId, String roomNumber, String typeName, BigDecimal price, double discount) {
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.typeName = typeName;
        this.price = price;
        this.discount = discount;
    }
}
