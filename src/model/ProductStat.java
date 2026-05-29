package model;

import java.io.Serializable;


public class ProductStat implements Serializable {
    private int id;
    private String productName;
    private String categoryName;
    private int soldQuantity;
    private float revenue;
    private int rank;

    public ProductStat() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public int getSoldQuantity() { return soldQuantity; }
    public void setSoldQuantity(int soldQuantity) { this.soldQuantity = soldQuantity; }

    public float getRevenue() { return revenue; }
    public void setRevenue(float revenue) { this.revenue = revenue; }

    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }
}
