package crawler;

import org.apache.log4j.PropertyConfigurator;
import zdnet.com.ZDNet;

import java.sql.*;

public class Main {

    private static boolean checkDatabaseConnection() {
        try (Connection conn = DriverManager.getConnection(String.format("jdbc:mysql://%s:%s/%s", Constants.DB_HOST, Constants.DB_PORT, Constants.DB_NAME), Constants.DB_USER, Constants.DB_PASSWORD)) {
            if (!conn.isClosed()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) throws ClassNotFoundException {
        PropertyConfigurator.configure("log4j.properties");


        if (args.length < 6) {
            System.out.println("java -jar PortableExecutableCrawler.jar <dbHost> <dbPort> <dbUser> <dbPassword> <dbName> <locationToFilesSaving>");
            System.out.println("example: java -jar PortableExecutableCrawler.jar 127.0.0.1 3306 root root pefilesdb C:\\Users\\JACOB\\Desktop\\");
            return;
        }
        Constants.DB_HOST = args[0];
        Constants.DB_PORT = args[1];
        Constants.DB_USER = args[2];
        Constants.DB_PASSWORD = args[3];
        Constants.DB_NAME = args[4];
        Constants.LOCATION_TO_FILES_SAVING = args[5];

        Class.forName(Constants.JDBC_DRIVER);

        if (!Constants.LOCATION_TO_FILES_SAVING.endsWith("\\")) {
            Constants.LOCATION_TO_FILES_SAVING += "\\";
        }

        if (checkDatabaseConnection()) {
            Thread zdNetThread = new Thread(new ZDNet());
            zdNetThread.start();
        }
    }
}