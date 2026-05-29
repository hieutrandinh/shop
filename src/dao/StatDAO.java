package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import model.ClientStat;
import model.ProductStat;
import model.RevenueStat;

public class StatDAO extends DAO {

    public StatDAO() { super(); }

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

    public RevenueStat getRevenueStat(Date startDate, Date endDate) {
        String sql = "SELECT COUNT(*) AS totalOrders, ISNULL(SUM(totalAmount), 0) AS totalRevenue "
                + "FROM tblOrder "
                + "WHERE orderStatus = 'DELIVERED' "
                + "AND orderDate >= ? AND orderDate <= ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, SDF.format(startDate));
            ps.setString(2, SDF.format(endDate));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                RevenueStat stat = new RevenueStat();
                stat.setTotalOrders(rs.getInt("totalOrders"));
                stat.setTotalRevenue(rs.getFloat("totalRevenue"));
                stat.setPeriod(SDF.format(startDate) + " → " + SDF.format(endDate));
                return stat;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public ArrayList<RevenueStat> getRevenueByCategory(Date startDate, Date endDate) {
        ArrayList<RevenueStat> result = new ArrayList<>();
        String sql = "SELECT c.name AS categoryName, "
                + "ISNULL(SUM(ol.subTotal), 0) AS revenue "
                + "FROM tblCategory c "
                + "LEFT JOIN tblProduct p ON p.tblCategoryId = c.id "
                + "LEFT JOIN tblProductVariant v ON v.tblProductId = p.id "
                + "LEFT JOIN tblOrderLine ol ON ol.tblProductVariantId = v.id "
                + "LEFT JOIN tblOrder o ON ol.tblOrderId = o.id "
                + "    AND o.orderStatus = 'DELIVERED' "
                + "    AND o.orderDate >= ? AND o.orderDate <= ? "
                + "GROUP BY c.name ORDER BY revenue DESC";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, SDF.format(startDate));
            ps.setString(2, SDF.format(endDate));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                RevenueStat stat = new RevenueStat();
                stat.setPeriod(rs.getString("categoryName"));
                stat.setRevenueByCategory(rs.getFloat("revenue"));
                result.add(stat);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return result;
    }

    public ArrayList<ProductStat> getTopProducts(Date startDate, Date endDate, int topN) {
        ArrayList<ProductStat> result = new ArrayList<>();
        String sql = "SELECT TOP (?) p.name AS productName, c.name AS categoryName, "
                + "SUM(ol.quantity) AS soldQty, SUM(ol.subTotal) AS revenue, "
                + "ROW_NUMBER() OVER (ORDER BY SUM(ol.quantity) DESC) AS rank "
                + "FROM tblOrderLine ol "
                + "JOIN tblProductVariant v ON ol.tblProductVariantId = v.id "
                + "JOIN tblProduct p ON v.tblProductId = p.id "
                + "JOIN tblCategory c ON p.tblCategoryId = c.id "
                + "JOIN tblOrder o ON ol.tblOrderId = o.id "
                + "WHERE o.orderStatus = 'DELIVERED' "
                + "AND o.orderDate >= ? AND o.orderDate <= ? "
                + "GROUP BY p.name, c.name "
                + "ORDER BY soldQty DESC";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, topN);
            ps.setString(2, SDF.format(startDate));
            ps.setString(3, SDF.format(endDate));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ProductStat stat = new ProductStat();
                stat.setProductName(rs.getString("productName"));
                stat.setCategoryName(rs.getString("categoryName"));
                stat.setSoldQuantity(rs.getInt("soldQty"));
                stat.setRevenue(rs.getFloat("revenue"));
                stat.setRank(rs.getInt("rank"));
                result.add(stat);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return result;
    }

    public ClientStat getClientStat(Date startDate, Date endDate) {
        String sql = "SELECT "
                + "(SELECT COUNT(*) FROM tblUser WHERE role='CLIENT' "
                + "    AND createdAt >= ? AND createdAt <= ?) AS newReg, "
                + "(SELECT COUNT(DISTINCT tblClientId) FROM tblOrder "
                + "    WHERE orderStatus = 'DELIVERED' "
                + "    AND orderDate >= ? AND orderDate <= ?) AS activeClients";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, SDF.format(startDate));
            ps.setString(2, SDF.format(endDate));
            ps.setString(3, SDF.format(startDate));
            ps.setString(4, SDF.format(endDate));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ClientStat stat = new ClientStat();
                stat.setNewRegistrations(rs.getInt("newReg"));
                stat.setActiveCustomers(rs.getInt("activeClients"));
                stat.setPeriod(SDF.format(startDate) + " → " + SDF.format(endDate));
                return stat;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public ArrayList<ClientStat> getTopClients(Date startDate, Date endDate, int topN) {
        ArrayList<ClientStat> result = new ArrayList<>();
        String sql = "SELECT TOP (?) u.id, u.fullName, SUM(o.totalAmount) AS spent "
                + "FROM tblOrder o JOIN tblUser u ON o.tblClientId = u.id "
                + "WHERE o.orderStatus = 'DELIVERED' "
                + "AND o.orderDate >= ? AND o.orderDate <= ? "
                + "GROUP BY u.id, u.fullName ORDER BY spent DESC";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, topN);
            ps.setString(2, SDF.format(startDate));
            ps.setString(3, SDF.format(endDate));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ClientStat stat = new ClientStat();
                stat.setClientId(rs.getInt("id"));
                stat.setPeriod(rs.getString("fullName"));
                stat.setActiveCustomers((int) rs.getFloat("spent"));
                result.add(stat);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return result;
    }
}