package model;

import java.io.Serializable;


public class OrderLine implements Serializable {
    private int id;
    private int quantity;
    private float priceAtPurchase;
    private float subTotal;
    private ProductVariant variant;
    private int orderId;

    public OrderLine() { super(); }

    public OrderLine(int quantity, float priceAtPurchase, ProductVariant variant) {
        this.quantity = quantity;
        this.priceAtPurchase = priceAtPurchase;
        this.variant = variant;
        this.subTotal = quantity * priceAtPurchase;
    }

    /** Recalculate subTotal when quantity or price changes. */
    public void recalculate() {
        this.subTotal = this.quantity * this.priceAtPurchase;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        recalculate();
    }

    public float getPriceAtPurchase() { return priceAtPurchase; }
    public void setPriceAtPurchase(float priceAtPurchase) {
        this.priceAtPurchase = priceAtPurchase;
        recalculate();
    }

    public float getSubTotal() { return subTotal; }
    public void setSubTotal(float subTotal) { this.subTotal = subTotal; }

    public ProductVariant getVariant() { return variant; }
    public void setVariant(ProductVariant variant) { this.variant = variant; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    @Override
    public String toString() {
        return "OrderLine{qty=" + quantity + ", price=" + priceAtPurchase + ", sub=" + subTotal + "}";
    }
}
