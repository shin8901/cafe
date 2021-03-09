package com.santa.cafe.order.dto;

import com.santa.cafe.beverage.model.BeverageSize;

public class OrderItemDTO {
    private int beverageId;
    private int count;

    public int getBeverageId() {
        return beverageId;
    }

    public void setBeverageId(int beverageId) {
        this.beverageId = beverageId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
