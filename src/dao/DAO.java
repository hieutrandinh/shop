package dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class DAO {
    public static Connection con;

    public DAO() {
        if (con == null) {
            String dbUrl = "jdbc:sqlserver://localhost:1433;databaseName=SHOP;encrypt=false;trustServerCertificate=true";
            String dbClass = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
            try {
                Class.forName(dbClass);
                con = DriverManager.getConnection(dbUrl,"sa","YourStrong!Passw0rd");
                System.out.println(con);
            } catch (Exception e) {
                System.out.println("ket noi that bai");
                e.printStackTrace();
            }
        }
    }
}
