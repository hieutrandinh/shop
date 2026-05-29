package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import model.Voucher;

public class VoucherDAO extends DAO {

    public VoucherDAO() { super(); }

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

    public Voucher findByCode(String code) {
        String sql = "SELECT * FROM tblVoucher WHERE voucherCode = ? "
                + "AND expiryDate >= CAST(GETDATE() AS DATE) AND usageCount < maxUsage";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapVoucher(rs);
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public boolean incrementUsage(int voucherId) {
        String sql = "UPDATE tblVoucher SET usageCount = usageCount + 1 WHERE id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, voucherId);
            ps.executeUpdate();
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public ArrayList<Voucher> getAllVouchers() {
        ArrayList<Voucher> result = new ArrayList<>();
        String sql = "SELECT * FROM tblVoucher ORDER BY expiryDate DESC";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) result.add(mapVoucher(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return result;
    }

    public boolean addVoucher(Voucher v) {
        String sql = "INSERT INTO tblVoucher(voucherCode, discountValue, minOrderAmount, "
                + "maxUsage, usageCount, expiryDate) VALUES(?,?,?,?,0,?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, v.getVoucherCode());
            ps.setFloat(2, v.getDiscountValue());
            ps.setFloat(3, v.getMinOrderAmount());
            ps.setInt(4, v.getMaxUsage());
            ps.setString(5, SDF.format(v.getExpiryDate()));
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) v.setId(keys.getInt(1));
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean updateVoucher(Voucher v) {
        String sql = "UPDATE tblVoucher SET voucherCode=?, discountValue=?, minOrderAmount=?, "
                + "maxUsage=?, expiryDate=? WHERE id=?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, v.getVoucherCode());
            ps.setFloat(2, v.getDiscountValue());
            ps.setFloat(3, v.getMinOrderAmount());
            ps.setInt(4, v.getMaxUsage());
            ps.setString(5, SDF.format(v.getExpiryDate()));
            ps.setInt(6, v.getId());
            ps.executeUpdate();
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    private Voucher mapVoucher(ResultSet rs) throws Exception {
        Voucher v = new Voucher();
        v.setId(rs.getInt("id"));
        v.setVoucherCode(rs.getString("voucherCode"));
        v.setDiscountValue(rs.getFloat("discountValue"));
        v.setMinOrderAmount(rs.getFloat("minOrderAmount"));
        v.setMaxUsage(rs.getInt("maxUsage"));
        v.setUsageCount(rs.getInt("usageCount"));
        v.setExpiryDate(rs.getDate("expiryDate"));
        return v;
    }
}