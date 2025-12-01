package com.example.hotelmanagementsystem.form2;

import java.math.BigDecimal;

public class Room {
    public final int roomId;
    public final String roomNumber;
    public final String typeName;        // ví dụ: "Deluxe King", "Standard Twin"
    public final BigDecimal originalPrice; // giá gốc từ room_type.room_cost
    public final double discountRate;     // % giảm giá theo mùa (0.0 → 100.0)
    public final BigDecimal finalPrice;   // giá sau khi đã giảm = originalPrice * (100 - discountRate)/100
    private boolean selected = false;

    public Room(int roomId, String roomNumber, String typeName,
                BigDecimal originalPrice, double discountRate) {
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.typeName = typeName;
        this.originalPrice = originalPrice;
        this.discountRate = discountRate;

        // Tính giá cuối cùng ngay khi tạo object
        BigDecimal discountMultiplier = BigDecimal.valueOf(100 - discountRate).divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP);
        this.finalPrice = originalPrice.multiply(discountMultiplier).setScale(0, BigDecimal.ROUND_HALF_UP);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public BigDecimal getFinalPrice() {
        return finalPrice;
    }
}
