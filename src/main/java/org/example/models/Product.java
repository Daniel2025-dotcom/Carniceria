package org.example.models;

public class Product {
    private String name;
    private String description;
    private float price;
    private float stockActual;
    private String tipoVenta;
    private String code;
    public Product(String name, String description, float price,float stockActual,String tipoVenta, String code) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockActual = stockActual;
        this.tipoVenta = tipoVenta;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    public float getStockActual() {return stockActual;}
    public void setStockActual(float stockActual) {this.stockActual = stockActual;}
    public String getTipoVenta() {return tipoVenta;}
    public void setTipoVenta(String tipoVenta) {this.tipoVenta = tipoVenta;}
}
