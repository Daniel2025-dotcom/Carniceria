package org.example.models;

public class Item {
    private Product product;
    private float subtotal;
    private float quantity;

    public Item(Product product, float quantity) {
        this.product = product;
        this.quantity = quantity;
        this.subtotal = product.getPrice() * quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public float getSubtotal() {
        return subtotal;
    }

}
