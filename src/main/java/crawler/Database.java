package crawler;


import org.apache.log4j.Logger;

import java.sql.*;

public class Database {
    static final Logger ZD_NET_LOG = Logger.getLogger("zdNetLogger");
    static final Logger C_NET_LOG = Logger.getLogger("cNetLogger");
    static final Logger APK_LOG = Logger.getLogger("googlePlayLogger");


    public static boolean containsUrlAndVersionPE(String url, String version) {
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
            ZD_NET_LOG.error(e);
            C_NET_LOG.error(e);
        }
        return false;
    }

    public static boolean isAppExistsPE(String md5, String sha1, String sha256) {
        String sql = "SELECT count(id) FROM pe_file WHERE md5=? AND sha1=? AND sha256=?";
        try (Connection conn = DriverManager.getConnection(String.format("jdbc:mysql://%s:%s/%s", Constants.DB_HOST, Constants.DB_PORT, Constants.DB_NAME), Constants.DB_USER, Constants.DB_PASSWORD); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, md5);
            ps.setString(2, sha1);
            ps.setString(3, sha256);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (rs.getInt("count(id)") > 0) {
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            ZD_NET_LOG.error(e);
            C_NET_LOG.error(e);
        }
        return false;
    }

    public static void insertToDatabasePE(PortableExecutableFile pe) {
        String sql = "INSERT INTO `pe_file` (`url`, `location`, `name`, `category`, `description`, `license`, `version`, `operation_system`, `system_requirements`, `md5`, `sha1`, `sha256`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        try (Connection conn = DriverManager.getConnection(String.format("jdbc:mysql://%s:%s/%s", Constants.DB_HOST, Constants.DB_PORT, Constants.DB_NAME), Constants.DB_USER, Constants.DB_PASSWORD); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pe.getUrl());
            ps.setString(2, pe.getLocation());
            ps.setString(3, pe.getName());
            ps.setString(4, pe.getCategory());
            ps.setString(5, pe.getDescription());
            ps.setString(6, pe.getLicense());
            ps.setString(7, pe.getVersion());
            ps.setString(8, pe.getOperationSystem());
            ps.setString(9, pe.getSystemRequirements());
            ps.setString(10, pe.getMd5());
            ps.setString(11, pe.getSha1());
            ps.setString(12, pe.getSha256());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            ZD_NET_LOG.error(e);
            C_NET_LOG.error(e);
        }
    }

    public static boolean containsUrlAndVersionApk(String url, String version) {
        String sql = "SELECT count(id) FROM apk_file WHERE url=? AND version=?";
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
            APK_LOG.error(e);
        }
        return false;
    }

    public static void insertToDatabaseAPK(ApkFile apkFile) {
        String sql = "INSERT INTO `apk_file` (`url`, `location`, `name`, `category`, `description`, `version`, `md5`, `sha1`, `sha256`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
        try (Connection conn = DriverManager.getConnection(String.format("jdbc:mysql://%s:%s/%s", Constants.DB_HOST, Constants.DB_PORT, Constants.DB_NAME), Constants.DB_USER, Constants.DB_PASSWORD); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, apkFile.getUrl());
            ps.setString(2, apkFile.getLocation());
            ps.setString(3, apkFile.getName());
            ps.setString(4, apkFile.getCategory());
            ps.setString(5, apkFile.getDescription());
            ps.setString(6, apkFile.getVersion());
            ps.setString(7, apkFile.getMd5());
            ps.setString(8, apkFile.getSha1());
            ps.setString(9, apkFile.getSha256());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            APK_LOG.error(e);
        }
    }

    public static boolean isAppExistsAPK(String md5) {
        String sql = "SELECT count(id) FROM apk_file WHERE md5=?";
        try (Connection conn = DriverManager.getConnection(String.format("jdbc:mysql://%s:%s/%s", Constants.DB_HOST, Constants.DB_PORT, Constants.DB_NAME), Constants.DB_USER, Constants.DB_PASSWORD); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, md5);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (rs.getInt("count(id)") > 0) {
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            APK_LOG.error(e);
        }
        return false;
    }
}