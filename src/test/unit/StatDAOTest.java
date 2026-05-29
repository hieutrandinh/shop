package test.unit;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import dao.StatDAO;
import model.ClientStat;
import model.ProductStat;
import model.RevenueStat;

public class StatDAOTest {

    private StatDAO statDAO;
    private Date startDate;
    private Date endDate;

    @Before
    public void setUp() {
        statDAO = new StatDAO();
        // Use a wide date range to capture any seeded data
        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.JANUARY, 1);
        startDate = cal.getTime();
        endDate = new Date();  // today
    }

    // ─── getRevenueStat ───────────────────────────────────────────────────────

    @Test
    public void testGetRevenueStat_NotNull() {
        RevenueStat stat = statDAO.getRevenueStat(startDate, endDate);
        assertNotNull("Revenue stat should not be null", stat);
    }

    @Test
    public void testGetRevenueStat_TotalOrdersNonNegative() {
        RevenueStat stat = statDAO.getRevenueStat(startDate, endDate);
        assertNotNull(stat);
        assertTrue("Total orders should be >= 0", stat.getTotalOrders() >= 0);
    }

    @Test
    public void testGetRevenueStat_TotalRevenueNonNegative() {
        RevenueStat stat = statDAO.getRevenueStat(startDate, endDate);
        assertNotNull(stat);
        assertTrue("Total revenue should be >= 0", stat.getTotalRevenue() >= 0);
    }

    // ─── getRevenueByCategory ─────────────────────────────────────────────────

    @Test
    public void testGetRevenueByCategory_ReturnsAllCategories() {
        ArrayList<RevenueStat> stats = statDAO.getRevenueByCategory(startDate, endDate);
        assertNotNull("Category revenue list should not be null", stats);
        // Should return at least as many rows as there are categories (even if revenue=0)
        assertFalse("Category revenue list should not be empty", stats.isEmpty());
    }

    // ─── getTopProducts ───────────────────────────────────────────────────────

    @Test
    public void testGetTopProducts_LimitRespected() {
        int topN = 5;
        ArrayList<ProductStat> stats = statDAO.getTopProducts(startDate, endDate, topN);
        assertNotNull(stats);
        assertTrue("Result count should not exceed topN=" + topN, stats.size() <= topN);
    }

    @Test
    public void testGetTopProducts_RanksAscending() {
        ArrayList<ProductStat> stats = statDAO.getTopProducts(startDate, endDate, 10);
        for (int i = 1; i < stats.size(); i++) {
            assertTrue("Products should be ordered by descending sold quantity",
                stats.get(i - 1).getSoldQuantity() >= stats.get(i).getSoldQuantity());
        }
    }

    // ─── getClientStat ────────────────────────────────────────────────────────

    @Test
    public void testGetClientStat_NotNull() {
        ClientStat stat = statDAO.getClientStat(startDate, endDate);
        assertNotNull("Client stat should not be null", stat);
    }

    @Test
    public void testGetClientStat_NonNegativeValues() {
        ClientStat stat = statDAO.getClientStat(startDate, endDate);
        assertNotNull(stat);
        assertTrue("New registrations should be >= 0", stat.getNewRegistrations() >= 0);
        assertTrue("Active customers should be >= 0", stat.getActiveCustomers() >= 0);
    }

    // ─── getTopClients ────────────────────────────────────────────────────────

    @Test
    public void testGetTopClients_LimitRespected() {
        int topN = 3;
        ArrayList<ClientStat> clients = statDAO.getTopClients(startDate, endDate, topN);
        assertNotNull(clients);
        assertTrue("Result count should not exceed topN=" + topN, clients.size() <= topN);
    }
}
