package test.unit;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import dao.OrderDAO;
import dao.ProductDAO;
import model.Admin;
import model.Category;
import model.Client;
import model.Order;
import model.OrderLine;
import model.Product;
import model.ProductVariant;

public class OrderDAOTest {

    private OrderDAO orderDAO;
    private ProductDAO productDAO;

    @Before
    public void setUp() {
        orderDAO = new OrderDAO();
        productDAO = new ProductDAO();
    }

    // ─── Helper: build a minimal order ───────────────────────────────────────

    private Order buildOrder(int clientId, int variantId, float variantPrice, int qty) {
        Client c = new Client(); c.setId(clientId);
        Admin admin = new Admin(); admin.setId(1);

        Product p = new Product(); p.setId(1);
        ProductVariant v = new ProductVariant();
        v.setId(variantId);
        v.setPrice(variantPrice);

        OrderLine line = new OrderLine(qty, variantPrice, v);

        Order o = new Order("ORD-" + System.currentTimeMillis(), new Date(),
            "Test Receiver", "123 Test Street", "COD", c);
        o.setConfirmedBy(admin);
        o.getOrderLines().add(line);
        o.calculateTotal();
        return o;
    }

    // ─── addOrder ─────────────────────────────────────────────────────────────

    @Test
    public void testAddOrder_Success() {
        // Requires: client id=2, variant id=1 with stock >= 1
        Order order = buildOrder(2, 1, 1500000f, 1);

        // NOTE: comment out con.commit() and con.setAutoCommit(true) in OrderDAO
        // when running JUnit to avoid permanent DB changes (same pattern as hotel example)
        boolean result = orderDAO.addOrder(order);
        assertTrue("Order should be placed successfully", result);
        assertTrue("Order should receive id > 0", order.getId() > 0);
        assertTrue("OrderLine should receive id > 0", order.getOrderLines().get(0).getId() > 0);
    }

    @Test
    public void testAddOrder_InsufficientStock_Rollback() {
        // Requires: variant id=999 with stock=0 (or non-existent)
        Order order = buildOrder(2, 999, 1000f, 99999);
        boolean result = orderDAO.addOrder(order);
        assertFalse("Order should fail when stock is insufficient", result);
    }

    // ─── updateOrderStatus ────────────────────────────────────────────────────

    @Test
    public void testUpdateOrderStatus() {
        // Requires: order with id=1 exists
        boolean result = orderDAO.updateOrderStatus(1, "CONFIRMED", "TRK-001");
        assertTrue("Status update should succeed", result);
    }

    // ─── getOrdersByClient ────────────────────────────────────────────────────

    @Test
    public void testGetOrdersByClient_ReturnsList() {
        ArrayList<Order> orders = orderDAO.getOrdersByClient(2);
        assertNotNull("Result should not be null", orders);
    }

    // ─── getOrdersByStatus ────────────────────────────────────────────────────

    @Test
    public void testGetOrdersByStatus_Pending() {
        ArrayList<Order> orders = orderDAO.getOrdersByStatus("PENDING");
        assertNotNull(orders);
        for (Order o : orders) {
            assertEquals("All returned orders should have PENDING status",
                "PENDING", o.getOrderStatus());
        }
    }

    // ─── getOrderDetail ───────────────────────────────────────────────────────

    @Test
    public void testGetOrderDetail_LoadsLines() {
        // Requires: order with id=1 exists and has at least one line
        Order detail = orderDAO.getOrderDetail(1);
        if (detail != null) {
            assertFalse("Order detail should have at least one order line",
                detail.getOrderLines().isEmpty());
        }
    }

    @Test
    public void testGetOrderDetail_NonExistent() {
        Order detail = orderDAO.getOrderDetail(999999);
        assertNull("Non-existent order should return null", detail);
    }
}
