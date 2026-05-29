package test.unit;

import static org.junit.Assert.*;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;
import dao.ProductDAO;
import model.Category;
import model.Product;
import model.ProductVariant;

public class ProductDAOTest {

    private ProductDAO productDAO;

    @Before
    public void setUp() {
        productDAO = new ProductDAO();
    }

    // ─── searchProducts ───────────────────────────────────────────────────────

    @Test
    public void testSearchProducts_AllResults() {
        ArrayList<Product> result = productDAO.searchProducts("", 0);
        assertNotNull("Search result should not be null", result);
    }

    @Test
    public void testSearchProducts_ByKeyword() {
        ArrayList<Product> result = productDAO.searchProducts("Nike", 0);
        for (Product p : result) {
            assertTrue("All results should contain keyword",
                p.getName().toLowerCase().contains("nike")
                || p.getBrand().toLowerCase().contains("nike"));
        }
    }

    @Test
    public void testSearchProducts_NoMatchReturnsEmptyList() {
        ArrayList<Product> result = productDAO.searchProducts("XXXNONEXISTENTXXX", 0);
        assertNotNull(result);
        assertEquals("Should return empty list for no match", 0, result.size());
    }

    // ─── addProduct / getProductById ─────────────────────────────────────────

    @Test
    public void testAddAndGetProduct() {
        // Assumes category with id=1 exists
        Category cat = new Category();
        cat.setId(1);
        cat.setName("Football Boots");

        Product p = new Product(
            "SKU-TEST-001", "Test Boot", "TestBrand",
            "STANDARD", "FG", "Test description",
            "img/test.jpg", "ACTIVE", cat
        );

        ProductVariant v = new ProductVariant("42", "Black", 10, 1500000f, 3, "img/v1.jpg", 0);
        p.getVariants().add(v);

        boolean added = productDAO.addProduct(p);
        assertTrue("Product should be added successfully", added);
        assertTrue("Product should receive an id > 0", p.getId() > 0);
        assertTrue("Variant should receive an id > 0", v.getId() > 0);

        // Verify retrieval
        Product fetched = productDAO.getProductById(p.getId());
        assertNotNull("Fetched product should not be null", fetched);
        assertEquals("SKU-TEST-001", fetched.getSku());
        assertEquals(1, fetched.getVariants().size());
    }

    // ─── updateProduct ────────────────────────────────────────────────────────

    @Test
    public void testUpdateProduct() {
        // Assumes product with id=1 exists
        Product p = productDAO.getProductById(1);
        if (p == null) return;  // skip if no seed data
        String originalName = p.getName();
        p.setName("Updated Name");
        boolean result = productDAO.updateProduct(p);
        assertTrue("Update should succeed", result);

        // Restore original
        p.setName(originalName);
        productDAO.updateProduct(p);
    }

    // ─── decreaseStock ────────────────────────────────────────────────────────

    @Test
    public void testDecreaseStock_Sufficient() {
        // Add a variant with known stock first
        Category cat = new Category(); cat.setId(1);
        Product p = new Product("SKU-STOCK-001", "Stock Test", "Brand",
            "STANDARD", "FG", "", "", "ACTIVE", cat);
        ProductVariant v = new ProductVariant("40", "Red", 5, 1000f, 1, "", 0);
        p.getVariants().add(v);
        productDAO.addProduct(p);

        boolean result = productDAO.decreaseStock(v.getId(), 3);
        assertTrue("Decrease should succeed when stock is sufficient", result);
    }

    @Test
    public void testDecreaseStock_Insufficient() {
        // Add a variant with 0 stock
        Category cat = new Category(); cat.setId(1);
        Product p = new Product("SKU-ZERO-001", "Zero Stock", "Brand",
            "STANDARD", "FG", "", "", "ACTIVE", cat);
        ProductVariant v = new ProductVariant("41", "Blue", 0, 1000f, 1, "", 0);
        p.getVariants().add(v);
        productDAO.addProduct(p);

        boolean result = productDAO.decreaseStock(v.getId(), 1);
        assertFalse("Decrease should fail when stock is 0", result);
    }

    // ─── deleteProduct (soft delete) ─────────────────────────────────────────

    @Test
    public void testDeleteProduct_SoftDelete() {
        Category cat = new Category(); cat.setId(1);
        Product p = new Product("SKU-DEL-001", "To Be Deleted", "Brand",
            "BUDGET", "TF", "", "", "ACTIVE", cat);
        productDAO.addProduct(p);

        boolean result = productDAO.deleteProduct(p.getId());
        assertTrue("Soft delete should succeed", result);

        Product fetched = productDAO.getProductById(p.getId());
        if (fetched != null) {
            assertEquals("Status should be INACTIVE after soft delete", "INACTIVE", fetched.getStatus());
        }
    }
}
