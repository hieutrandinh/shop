package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import model.Category;
import model.Product;
import model.ProductVariant;

public class ProductDAO extends DAO {

    public ProductDAO() { super(); }

    public ArrayList<Category> getAllCategories() {
        ArrayList<Category> result = new ArrayList<>();
        String sql = "SELECT * FROM tblCategory";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Category cat = new Category();
                cat.setId(rs.getInt("id"));
                cat.setName(rs.getString("name"));
                cat.setDescription(rs.getString("description"));
                result.add(cat);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return result;
    }

    public ArrayList<Product> searchProducts(String key, int categoryId) {
        ArrayList<Product> result = new ArrayList<>();
        String sql = "SELECT p.id, p.sku, p.name, p.brand, p.tier, p.studType, "
                + "p.description, p.images, p.status, "
                + "c.id AS catId, c.name AS catName, c.description AS catDesc "
                + "FROM tblProduct p JOIN tblCategory c ON p.tblCategoryId = c.id "
                + "WHERE p.name LIKE ? "
                + (categoryId > 0 ? "AND p.tblCategoryId = ? " : "")
                + "ORDER BY p.name";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, "%" + key + "%");
            if (categoryId > 0) ps.setInt(2, categoryId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(mapProduct(rs));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return result;
    }

    public Product getProductById(int id) {
        String sql = "SELECT p.id, p.sku, p.name, p.brand, p.tier, p.studType, "
                + "p.description, p.images, p.status, "
                + "c.id AS catId, c.name AS catName, c.description AS catDesc "
                + "FROM tblProduct p JOIN tblCategory c ON p.tblCategoryId = c.id "
                + "WHERE p.id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Product p = mapProduct(rs);
                p.setVariants(getVariantsByProductId(id));
                return p;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public boolean addProduct(Product product) {
        String sqlProd = "INSERT INTO tblProduct(sku, name, brand, tier, studType, "
                + "description, images, status, tblCategoryId) "
                + "VALUES(?,?,?,?,?,?,?,?,?)";
        try {
            con.setAutoCommit(false);
            PreparedStatement ps = con.prepareStatement(sqlProd, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, product.getSku());
            ps.setString(2, product.getName());
            ps.setString(3, product.getBrand());
            ps.setString(4, product.getTier());
            ps.setString(5, product.getStudType());
            ps.setString(6, product.getDescription());
            ps.setString(7, product.getImages());
            ps.setString(8, product.getStatus());
            ps.setInt(9, product.getCategory().getId());

            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                product.setId(keys.getInt(1));
                for (ProductVariant v : product.getVariants()) {
                    v.setProductId(product.getId());
                    addVariant(v);
                }
            }
            con.commit();
            con.setAutoCommit(true);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            try { con.rollback(); con.setAutoCommit(true); } catch (Exception ex) { ex.printStackTrace(); }
            return false;
        }
    }

    public boolean updateProduct(Product product) {
        String sql = "UPDATE tblProduct SET sku=?, name=?, brand=?, tier=?, studType=?, "
                + "description=?, images=?, status=?, tblCategoryId=? WHERE id=?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, product.getSku());
            ps.setString(2, product.getName());
            ps.setString(3, product.getBrand());
            ps.setString(4, product.getTier());
            ps.setString(5, product.getStudType());
            ps.setString(6, product.getDescription());
            ps.setString(7, product.getImages());
            ps.setString(8, product.getStatus());
            ps.setInt(9, product.getCategory().getId());
            ps.setInt(10, product.getId());
            ps.executeUpdate();
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean deleteProduct(int productId) {
        String sql = "UPDATE tblProduct SET status = 'INACTIVE' WHERE id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, productId);
            ps.executeUpdate();
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public ArrayList<ProductVariant> getVariantsByProductId(int productId) {
        ArrayList<ProductVariant> result = new ArrayList<>();
        String sql = "SELECT * FROM tblProductVariant WHERE tblProductId = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(mapVariant(rs));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return result;
    }

    public boolean addVariant(ProductVariant v) {
        String sql = "INSERT INTO tblProductVariant(size, color, stock, price, "
                + "safetyThreshold, image, tblProductId) VALUES(?,?,?,?,?,?,?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, v.getSize());
            ps.setString(2, v.getColor());
            ps.setInt(3, v.getStock());
            ps.setFloat(4, v.getPrice());
            ps.setInt(5, v.getSafetyThreshold());
            ps.setString(6, v.getImage());
            ps.setInt(7, v.getProductId());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) v.setId(keys.getInt(1));
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean updateVariant(ProductVariant v) {
        String sql = "UPDATE tblProductVariant SET size=?, color=?, stock=?, price=?, "
                + "safetyThreshold=?, image=? WHERE id=?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, v.getSize());
            ps.setString(2, v.getColor());
            ps.setInt(3, v.getStock());
            ps.setFloat(4, v.getPrice());
            ps.setInt(5, v.getSafetyThreshold());
            ps.setString(6, v.getImage());
            ps.setInt(7, v.getId());
            ps.executeUpdate();
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean decreaseStock(int variantId, int quantity) {
        String sql = "UPDATE tblProductVariant SET stock = stock - ? "
                + "WHERE id = ? AND stock >= ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, quantity);
            ps.setInt(2, variantId);
            ps.setInt(3, quantity);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    private Product mapProduct(ResultSet rs) throws Exception {
        Product p = new Product();
        p.setId(rs.getInt("id"));
        p.setSku(rs.getString("sku"));
        p.setName(rs.getString("name"));
        p.setBrand(rs.getString("brand"));
        p.setTier(rs.getString("tier"));
        p.setStudType(rs.getString("studType"));
        p.setDescription(rs.getString("description"));
        p.setImages(rs.getString("images"));
        p.setStatus(rs.getString("status"));

        Category cat = new Category();
        cat.setId(rs.getInt("catId"));
        cat.setName(rs.getString("catName"));
        cat.setDescription(rs.getString("catDesc"));
        p.setCategory(cat);
        return p;
    }

    private ProductVariant mapVariant(ResultSet rs) throws Exception {
        ProductVariant v = new ProductVariant();
        v.setId(rs.getInt("id"));
        v.setSize(rs.getString("size"));
        v.setColor(rs.getString("color"));
        v.setStock(rs.getInt("stock"));
        v.setPrice(rs.getFloat("price"));
        v.setSafetyThreshold(rs.getInt("safetyThreshold"));
        v.setImage(rs.getString("image"));
        v.setProductId(rs.getInt("tblProductId"));
        return v;
    }
}