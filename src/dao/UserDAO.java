package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import model.Admin;
import model.Client;
import model.User;

public class UserDAO extends DAO {

    public UserDAO() { super(); }

    public boolean checkLogin(User user) {
        boolean result = false;
        String sql = "SELECT id, fullName, role, status FROM tblUser "
                + "WHERE username = ? AND password = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if ("ACTIVE".equalsIgnoreCase(rs.getString("status"))) {
                    user.setId(rs.getInt("id"));
                    user.setFullName(rs.getString("fullName"));
                    user.setRole(rs.getString("role"));
                    result = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean registerClient(Client client) {
        String sqlUser   = "INSERT INTO tblUser(username, password, fullName, role, status) "
                + "VALUES(?,?,?,'CLIENT','ACTIVE')";
        String sqlClient = "INSERT INTO tblClient(id, tel, address, email, totalSpent) "
                + "VALUES(?,?,?,?,0)";
        try {
            con.setAutoCommit(false);

            PreparedStatement psUser = con.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS);
            psUser.setString(1, client.getUsername());
            psUser.setString(2, client.getPassword());
            psUser.setString(3, client.getFullName());
            psUser.executeUpdate();

            ResultSet keys = psUser.getGeneratedKeys();
            if (keys.next()) {
                int newId = keys.getInt(1);
                client.setId(newId);

                PreparedStatement psClient = con.prepareStatement(sqlClient);
                psClient.setInt(1, newId);
                psClient.setString(2, client.getTel());
                psClient.setString(3, client.getAddress());
                psClient.setString(4, client.getEmail());
                psClient.executeUpdate();
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

    public boolean isUsernameTaken(String username) {
        String sql = "SELECT id FROM tblUser WHERE username = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<Client> searchClients(String key) {
        ArrayList<Client> result = new ArrayList<>();
        String sql = "SELECT u.id, u.username, u.fullName, u.status, "
                + "c.tel, c.address, c.email, c.totalSpent "
                + "FROM tblUser u JOIN tblClient c ON u.id = c.id "
                + "WHERE u.fullName LIKE ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, "%" + key + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Client c = new Client();
                c.setId(rs.getInt("id"));
                c.setUsername(rs.getString("username"));
                c.setFullName(rs.getString("fullName"));
                c.setStatus(rs.getString("status"));
                c.setTel(rs.getString("tel"));
                c.setAddress(rs.getString("address"));
                c.setEmail(rs.getString("email"));
                c.setTotalSpent(rs.getFloat("totalSpent"));
                result.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean updateStatus(int userId, String status) {
        String sql = "UPDATE tblUser SET status = ? WHERE id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, userId);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        String sqlCheck = "SELECT id FROM tblUser WHERE id = ? AND password = ?";
        String sqlUpdate = "UPDATE tblUser SET password = ? WHERE id = ?";
        try {
            PreparedStatement psCheck = con.prepareStatement(sqlCheck);
            psCheck.setInt(1, userId);
            psCheck.setString(2, oldPassword);
            ResultSet rs = psCheck.executeQuery();
            if (!rs.next()) return false;

            PreparedStatement psUpdate = con.prepareStatement(sqlUpdate);
            psUpdate.setString(1, newPassword);
            psUpdate.setInt(2, userId);
            psUpdate.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Client getClientById(int id) {
        String sql = "SELECT u.id, u.username, u.fullName, u.status, "
                + "c.tel, c.address, c.email, c.totalSpent "
                + "FROM tblUser u JOIN tblClient c ON u.id = c.id "
                + "WHERE u.id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Client c = new Client();
                c.setId(rs.getInt("id"));
                c.setUsername(rs.getString("username"));
                c.setFullName(rs.getString("fullName"));
                c.setStatus(rs.getString("status"));
                c.setTel(rs.getString("tel"));
                c.setAddress(rs.getString("address"));
                c.setEmail(rs.getString("email"));
                c.setTotalSpent(rs.getFloat("totalSpent"));
                return c;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Admin getAdminById(int id) {
        String sql = "SELECT u.id, u.username, u.fullName, u.status "
                + "FROM tblUser u JOIN tblAdmin a ON u.id = a.id "
                + "WHERE u.id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Admin admin = new Admin();
                admin.setId(rs.getInt("id"));
                admin.setUsername(rs.getString("username"));
                admin.setFullName(rs.getString("fullName"));
                admin.setStatus(rs.getString("status"));
                return admin;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}