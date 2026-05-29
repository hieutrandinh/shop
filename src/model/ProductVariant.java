package model;

import java.io.Serializable;


public class ProductVariant implements Serializable {
    private int id;
    private String size;            // e.g. "39", "40", "41", "42", "43", "44"
    private String color;           // e.g. "Black/White", "Red", "Blue"
    private int stock;
    private float price;
    private int safetyThreshold;
    private String image;
    private int productId;

    public ProductVariant() { super(); }

    public ProductVariant(String size, String color, int stock, float price,
                          int safetyThreshold, String image, int productId) {
        this.size = size;
        this.color = color;
        this.stock = stock;
        this.price = price;
        this.safetyThreshold = safetyThreshold;
        this.image = image;
        this.productId = productId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public float getPrice() { return price; }
    public void setPrice(float price) { this.price = price; }

    public int getSafetyThreshold() { return safetyThreshold; }
    public void setSafetyThreshold(int safetyThreshold) { this.safetyThreshold = safetyThreshold; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public boolean isLowStock() { return stock <= safetyThreshold; }

    @Override
    public String toString() {
        return "Size " + size + " / " + color + " (stock: " + stock + ", price: " + price + ")";
    }
}
