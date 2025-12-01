package com.example.hotelmanagementsystem.form2;

public class Hotel {
    public final int hotelId;
    public final String hotelName;
    public final int chainId;
    public final String chainName;
    public final int starRating;

    public Hotel(int hotelId, String hotelName, int chainId, String chainName, int starRating) {
        this.hotelId = hotelId;
        this.hotelName = hotelName;
        this.chainId = chainId;
        this.chainName = chainName;
        this.starRating = starRating;
    }

    @Override
    public String toString() {
        return hotelName + " (" + "★".repeat(starRating) + ")";
    }
}
