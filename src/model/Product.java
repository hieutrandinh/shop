package model;

import java.io.Serializable;
import java.util.ArrayList;

public class Product implements Serializable {
    private int id;
    private String sku;
    private String name;
    private String brand;
    private String tier;        // "PREMIUM", "STANDARD", "BUDGET"
    private String studType;    // For football boots: "FG", "AG", "SG", "IC", "TF"
    private String description;
    private String images;
    private String status;      // "ACTIVE", "INACTIVE", "OUT_OF_STOCK"
    private Category category;
    private ArrayList<ProductVariant> variants;

    public Product() {
        super();
        this.variants = new ArrayList<>();
    }

    public Product(String sku, String name, String brand, String tier,
                   String studType, String description, String images,
                   String status, Category category) {
        this.sku = sku;
        this.name = name;
        this.brand = brand;
        this.tier = tier;
        this.studType = studType;
        this.description = description;
        this.images = images;
        this.status = status;
        this.category = category;
        this.variants = new ArrayList<>();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getTier() { return tier; }
    public void setTier(String tier) { this.tier = tier; }

    public String getStudType() { return studType; }
    public void setStudType(String studType) { this.studType = studType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public ArrayList<ProductVariant> getVariants() { return variants; }
    public void setVariants(ArrayList<ProductVariant> variants) { this.variants = variants; }

    @Override
    public String toString() {
        return "Product{id=" + id + ", sku='" + sku + "', name='" + name + "', brand='" + brand + "'}";
    }
}
