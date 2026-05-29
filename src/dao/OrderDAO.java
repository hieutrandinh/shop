package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import model.Admin;
import model.Client;
import model.Order;
import model.OrderLine;
import model.ProductVariant;
import model.Voucher;

public class OrderDAO extends DAO {

    public OrderDAO() { super(); }

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

    public boolean addOrder(Order order) {
        String sqlOrder = "INSERT INTO tblOrder(orderCode, orderDate, receiverName, "
                + "shippingAddress, paymentMethod, orderStatus, totalAmount, "
                + "tblAdminId, tblVoucherId, tblClientId, tblRevenueStatId) "
                + "VALUES(?,?,?,?,?,'PENDING',?,?,?,?,1)";
        String sqlLine  = "INSERT INTO tblOrderLine(quantity, priceAtPurchase, subTotal, "
                + "tblProductVariantId, tblProductStatId, tblOrderId) VALUES(?,?,?,?,1,?)";
        String sqlStock = "UPDATE tblProductVariant SET stock = stock - ? "
                + "WHERE id = ? AND stock >= ?";

        try {
            con.setAutoCommit(false);

            PreparedStatement psOrder = con.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS);
            psOrder.setString(1, order.getOrderCode());
            psOrder.setString(2, SDF.format(order.getOrderDate()));
            psOrder.setString(3, order.getReceiverName());
            psOrder.setString(4, order.getShippingAddress());
            psOrder.setString(5, order.getPaymentMethod());
            psOrder.setFloat(6, order.getTotalAmount());
            psOrder.setInt(7, order.getConfirmedBy() != null ? order.getConfirmedBy().getId() : 1);
            psOrder.setInt(8, order.getVoucher() != null ? order.getVoucher().getId() : 1);
            psOrder.setInt(9, order.getClient().getId());
            psOrder.executeUpdate();

            ResultSet keys = psOrder.getGeneratedKeys();
            if (!keys.next()) { con.rollback(); con.setAutoCommit(true); return false; }
            order.setId(keys.getInt(1));

            for (OrderLine line : order.getOrderLines()) {
                PreparedStatement psStock = con.prepareStatement(sqlStock);
                psStock.setInt(1, line.getQuantity());
                psStock.setInt(2, line.getVariant().getId());
                psStock.setInt(3, line.getQuantity());
                int affected = psStock.executeUpdate();
                if (affected == 0) {
                    con.rollback();
                    con.setAutoCommit(true);
                    return false;
                }

                PreparedStatement psLine = con.prepareStatement(sqlLine, Statement.RETURN_GENERATED_KEYS);
                psLine.setInt(1, line.getQuantity());
                psLine.setFloat(2, line.getPriceAtPurchase());
                psLine.setFloat(3, line.getSubTotal());
                psLine.setInt(4, line.getVariant().getId());
                psLine.setInt(5, order.getId());
                psLine.executeUpdate();
                ResultSet lineKeys = psLine.getGeneratedKeys();
                if (lineKeys.next()) line.setId(lineKeys.getInt(1));
            }

            if (order.getVoucher() != null) {
                PreparedStatement psVoucher = con.prepareStatement(
                        "UPDATE tblVoucher SET usageCount = usageCount + 1 WHERE id = ?");
                psVoucher.setInt(1, order.getVoucher().getId());
                psVoucher.executeUpdate();
            }

            PreparedStatement psSpent = con.prepareStatement(
                    "UPDATE tblClient SET totalSpent = totalSpent + ? WHERE id = ?");
            psSpent.setFloat(1, order.getTotalAmount());
            psSpent.setInt(2, order.getClient().getId());
            psSpent.executeUpdate();

            con.commit();
            con.setAutoCommit(true);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            try { con.rollback(); con.setAutoCommit(true); } catch (Exception ex) { ex.printStackTrace(); }
            return false;
        }
    }
    public boolean updateOrderStatus(int orderId, String newStatus, String trackingCode) {
        String sql = "UPDATE tblOrder SET orderStatus = ?, trackingCode = ? WHERE id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, newStatus);
            ps.setString(2, trackingCode);
            ps.setInt(3, orderId);
            ps.executeUpdate();
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public ArrayList<Order> getOrdersByClient(int clientId) {
        ArrayList<Order> result = new ArrayList<>();
        String sql = "SELECT id, orderCode, orderDate, receiverName, shippingAddress, "
                + "paymentMethod, orderStatus, trackingCode, totalAmount "
                + "FROM tblOrder WHERE tblClientId = ? ORDER BY orderDate DESC";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, clientId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) result.add(mapOrderHeader(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return result;
    }

    public ArrayList<Order> getOrdersByStatus(String status) {
        ArrayList<Order> result = new ArrayList<>();
        String sql = "SELECT o.id, o.orderCode, o.orderDate, o.receiverName, "
                + "o.shippingAddress, o.paymentMethod, o.orderStatus, "
                + "o.trackingCode, o.totalAmount, "
                + "u.id AS clientId, u.fullName AS clientName "
                + "FROM tblOrder o JOIN tblUser u ON o.tblClientId = u.id "
                + "WHERE o.orderStatus = ? ORDER BY o.orderDate DESC";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Order o = mapOrderHeader(rs);
                Client c = new Client();
                c.setId(rs.getInt("clientId"));
                c.setFullName(rs.getString("clientName"));
                o.setClient(c);
                result.add(o);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return result;
    }

    public Order getOrderDetail(int orderId) {
        String sqlOrder = "SELECT o.id, o.orderCode, o.orderDate, o.receiverName, "
                + "o.shippingAddress, o.paymentMethod, o.orderStatus, "
                + "o.trackingCode, o.totalAmount "
                + "FROM tblOrder o WHERE o.id = ?";
        String sqlLines = "SELECT ol.id, ol.quantity, ol.priceAtPurchase, ol.subTotal, "
                + "v.id AS variantId, v.size, v.color, v.image, v.tblProductId, "
                + "p.name AS productName "
                + "FROM tblOrderLine ol "
                + "JOIN tblProductVariant v ON ol.tblProductVariantId = v.id "
                + "JOIN tblProduct p ON v.tblProductId = p.id "
                + "WHERE ol.tblOrderId = ?";
        try {
            PreparedStatement psOrder = con.prepareStatement(sqlOrder);
            psOrder.setInt(1, orderId);
            ResultSet rsOrder = psOrder.executeQuery();
            if (!rsOrder.next()) return null;
            Order order = mapOrderHeader(rsOrder);

            PreparedStatement psLines = con.prepareStatement(sqlLines);
            psLines.setInt(1, orderId);
            ResultSet rsLines = psLines.executeQuery();
            while (rsLines.next()) {
                OrderLine line = new OrderLine();
                line.setId(rsLines.getInt("id"));
                line.setQuantity(rsLines.getInt("quantity"));
                line.setPriceAtPurchase(rsLines.getFloat("priceAtPurchase"));
                line.setSubTotal(rsLines.getFloat("subTotal"));

                ProductVariant v = new ProductVariant();
                v.setId(rsLines.getInt("variantId"));
                v.setSize(rsLines.getString("size"));
                v.setColor(rsLines.getString("color"));
                v.setImage(rsLines.getString("image"));
                v.setProductId(rsLines.getInt("tblProductId"));
                line.setVariant(v);
                order.getOrderLines().add(line);
            }
            return order;
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    private Order mapOrderHeader(ResultSet rs) throws Exception {
        Order o = new Order();
        o.setId(rs.getInt("id"));
        o.setOrderCode(rs.getString("orderCode"));
        o.setOrderDate(rs.getDate("orderDate"));
        o.setReceiverName(rs.getString("receiverName"));
        o.setShippingAddress(rs.getString("shippingAddress"));
        o.setPaymentMethod(rs.getString("paymentMethod"));
        o.setOrderStatus(rs.getString("orderStatus"));
        o.setTrackingCode(rs.getString("trackingCode"));
        o.setTotalAmount(rs.getFloat("totalAmount"));
        return o;
    }
}