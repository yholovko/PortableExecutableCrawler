package crawler;


import java.sql.*;

public class Database {
    public static boolean containsUrlAndVersion(String url, String version) {
        String sql = "SELECT count(id) FROM pe_file WHERE url=? AND version=?";
        try (Connection conn = DriverManager.getConnection(String.format("jdbc:mysql://%s:%s/%s", Constants.DB_HOST, Constants.DB_PORT, Constants.DB_NAME), Constants.DB_USER, Constants.DB_PASSWORD); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, url);
            ps.setString(2, version);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (rs.getInt("count(id)") > 0) {
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void insert(String url, String location, String name, String category, String description, String license, String version, String os, String systemRequirements, String md5, String sha1, String sha256){
        String sql = "INSERT INTO `pe_file` (`url`, `location`, `name`, `category`, `description`, `license`, `version`, `operation_system`, `system_requirements`, `md5`, `sha1`, `sha256`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        try (Connection conn = DriverManager.getConnection(String.format("jdbc:mysql://%s:%s/%s", Constants.DB_HOST, Constants.DB_PORT, Constants.DB_NAME), Constants.DB_USER, Constants.DB_PASSWORD); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, url);
            ps.setString(2, location);
            ps.setString(3, name);
            ps.setString(4, category);
            ps.setString(5, description);
            ps.setString(6, license);
            ps.setString(7, version);
            ps.setString(8, os);
            ps.setString(9, systemRequirements);
            ps.setString(10, md5);
            ps.setString(11, sha1);
            ps.setString(12, sha256);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}