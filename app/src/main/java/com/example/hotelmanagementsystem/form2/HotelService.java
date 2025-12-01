package com.example.hotelmanagementsystem.form2;

import java.math.BigDecimal;

public class HotelService {
    public final int serviceId;
    public final String serviceName;
    public final BigDecimal cost;
    private boolean selected = false;

    public HotelService(int serviceId, String serviceName, BigDecimal cost) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.cost = cost;
    }

    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }
    public BigDecimal getCost() { return cost; }
}
