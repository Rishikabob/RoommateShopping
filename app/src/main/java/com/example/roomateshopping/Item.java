package com.example.roomateshopping;

/**
 * Class for an item
 */
public class Item {
    private String itemName;
    private Double price;
    private Integer quantity;
    private String purchaserEmail;


    public Item() {
    }

    public Item(String itemName, Double price, Integer quantity, String purchaserEmail) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
        this.purchaserEmail = purchaserEmail;

    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getPurchaserEmail() {
        return purchaserEmail;
    }

    public void setPurchaserEmail(String purchaserEmail) {
        this.purchaserEmail = purchaserEmail;
    }
}

