package com.techvengershurricane.model;

public class SupplyItem {
    private String name;
    private int quantity;

    public SupplyItem() {
        /* TODO: connect supplies to Shelter. */
    }

    public SupplyItem(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void restock(int amount) { this.quantity += Math.max(0, amount); }
}
