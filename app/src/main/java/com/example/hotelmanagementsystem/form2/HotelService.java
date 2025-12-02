package com.example.hotelmanagementsystem.form2;

import java.math.BigDecimal;

public class HotelService {
    public final int serviceId;
    public final String serviceName;
    public final BigDecimal cost;
    public final boolean isCommon;  // ← THÊM DÒNG NÀY
    private boolean selected = false;

    public HotelService(int serviceId, String serviceName, BigDecimal cost, boolean isCommon) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.cost = cost;
        this.isCommon = isCommon;
    }

    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }
}