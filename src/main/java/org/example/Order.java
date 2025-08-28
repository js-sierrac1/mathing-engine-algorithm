package org.example;

import java.time.LocalDateTime;
import java.util.Objects;

public class Order {

    private final int id;

    private final Side side;
    private final LocalDateTime entryTime;
    private double price;
    private int quantity;

    public Order(int id, Side side, double price, int quantity) {
        this.id = id;
        this.side = side;
        this.entryTime = LocalDateTime.now();
        this.price = price;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public Side getSide() {
        return side;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final Order order = (Order) obj;
        return id == order.id && Double.compare(price, order.price) == 0 && quantity == order.quantity && side == order.side && Objects.equals(entryTime, order.entryTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, side, entryTime, price, quantity);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", side=" + side +
                ", entryTime=" + entryTime +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}
