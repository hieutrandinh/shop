package model;

import java.io.Serializable;

public class RevenueStat implements Serializable {
    private int id;
    private String period;
    private int totalOrders;
    private float totalRevenue;
    private float revenueByCategory;
    private float revenueByTier;

    public RevenueStat() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }

    public int getTotalOrders() { return totalOrders; }
    public void setTotalOrders(int totalOrders) { this.totalOrders = totalOrders; }

    public float getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(float totalRevenue) { this.totalRevenue = totalRevenue; }

    public float getRevenueByCategory() { return revenueByCategory; }
    public void setRevenueByCategory(float revenueByCategory) { this.revenueByCategory = revenueByCategory; }

    public float getRevenueByTier() { return revenueByTier; }
    public void setRevenueByTier(float revenueByTier) { this.revenueByTier = revenueByTier; }
}
