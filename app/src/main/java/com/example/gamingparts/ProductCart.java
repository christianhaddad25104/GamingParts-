package com.example.gamingparts;

public class ProductCart {
    public String name;
    public int quantity;
    public int totalPrice;

    public ProductCart() {}

    public ProductCart(String name, int quantity, int totalPrice) {
        this.name = name;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }
}
