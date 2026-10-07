package org.example.models;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
public class Deal {
    private int id;
    private List<Item> items; 
    private LocalDateTime date;
    private float totalPrice;
    private PaymentMethod paymentMethod;

    public Deal(List<Item> items, PaymentMethod paymentMethod) {
        this.items = items;
        this.paymentMethod = paymentMethod;
        this.date = LocalDateTime.now();
        this.totalPrice = calcularTotal(items);
    }
    public Deal(int id, LocalDateTime date, float totalPrice, PaymentMethod paymentMethod) {
        this.id = id;
        this.date = date;
        this.totalPrice = totalPrice;
        this.paymentMethod = paymentMethod;
    }

    private float calcularTotal(List<Item> listaItems) {
        float total = 0;
        for (Item item : listaItems) {
            total += item.getSubtotal();
        }
        return total;
    }

    public List<Item> getItems() { return items; }
    public void setItems(List<Item> items) { this.items = items; }

    public float getTotalPrice() { return totalPrice; }
    public void setTotalPrice(float totalPrice) { this.totalPrice = totalPrice; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
}