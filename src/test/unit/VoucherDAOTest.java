package test.unit;

import static org.junit.Assert.*;
import java.util.Calendar;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import dao.VoucherDAO;
import model.Voucher;

public class VoucherDAOTest {

    private VoucherDAO voucherDAO;

    @Before
    public void setUp() {
        voucherDAO = new VoucherDAO();
    }

    private Date futureDate(int daysAhead) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, daysAhead);
        return cal.getTime();
    }

    // ─── addVoucher / findByCode ──────────────────────────────────────────────

    @Test
    public void testAddAndFindVoucher() {
        String code = "TEST" + System.currentTimeMillis();
        Voucher v = new Voucher(code, 10f, 500000f, 100, futureDate(30));

        boolean added = voucherDAO.addVoucher(v);
        assertTrue("Voucher should be added", added);
        assertTrue("Voucher should receive id > 0", v.getId() > 0);

        Voucher found = voucherDAO.findByCode(code);
        assertNotNull("Added voucher should be findable by code", found);
        assertEquals(code, found.getVoucherCode());
        assertEquals(10f, found.getDiscountValue(), 0.01f);
    }

    @Test
    public void testFindByCode_ExpiredReturnsNull() {
        // Try to find a code that doesn't exist
        Voucher found = voucherDAO.findByCode("INVALID_CODE_XYZ");
        assertNull("Non-existent code should return null", found);
    }

    // ─── Voucher.isValid ──────────────────────────────────────────────────────

    @Test
    public void testVoucherIsValid_MeetsMinOrder() {
        Voucher v = new Voucher("VALID10", 10f, 200000f, 10, futureDate(7));
        assertTrue("Voucher should be valid when order amount exceeds minimum", v.isValid(300000f));
    }

    @Test
    public void testVoucherIsValid_BelowMinOrder() {
        Voucher v = new Voucher("VALID10", 10f, 500000f, 10, futureDate(7));
        assertFalse("Voucher should be invalid when order amount is below minimum", v.isValid(100000f));
    }

    @Test
    public void testVoucherIsValid_MaxUsageReached() {
        Voucher v = new Voucher("USED", 10f, 100000f, 5, futureDate(7));
        v.setUsageCount(5);
        assertFalse("Voucher should be invalid when max usage is reached", v.isValid(200000f));
    }

    // ─── Voucher.applyDiscount ────────────────────────────────────────────────

    @Test
    public void testApplyDiscount_10Percent() {
        Voucher v = new Voucher("DISC10", 10f, 0f, 100, futureDate(7));
        float discounted = v.applyDiscount(1000000f);
        assertEquals("10% off 1,000,000 should be 900,000", 900000f, discounted, 0.01f);
    }

    // ─── incrementUsage ───────────────────────────────────────────────────────

    @Test
    public void testIncrementUsage() {
        String code = "INC" + System.currentTimeMillis();
        Voucher v = new Voucher(code, 5f, 100000f, 10, futureDate(15));
        voucherDAO.addVoucher(v);

        boolean result = voucherDAO.incrementUsage(v.getId());
        assertTrue("Increment usage should succeed", result);

        Voucher updated = voucherDAO.findByCode(code);
        assertNotNull(updated);
        assertEquals("Usage count should be 1 after increment", 1, updated.getUsageCount());
    }
}
