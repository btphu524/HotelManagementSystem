package com.example.hotelmanagementsystem.form2;

public class Guest {
    public final int guestId;
    public final String firstName;
    public final String lastName;
    public final String email;
    public final String phone;

    public Guest(int guestId, String firstName, String lastName,
                 String email, String phone) {
        this.guestId = guestId;
        this.firstName = firstName != null ? firstName.trim() : "";
        this.lastName = lastName != null ? lastName.trim() : "";
        this.email = email != null ? email : "";
        this.phone = phone != null ? phone : "";
    }

    // Dùng để hiển thị tên đầy đủ ở bất kỳ đâu
    public String getFullName() {
        return (firstName + " " + lastName).trim();
    }
}