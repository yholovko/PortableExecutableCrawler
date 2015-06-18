package crawler;

import cnet.com.CNet;
import googleplay.com.GooglePlay;
import org.apache.log4j.PropertyConfigurator;
import zdnet.com.ZDNet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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

    public static void main(String[] args) throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        PropertyConfigurator.configure(classLoader.getResource("log4j.properties"));

        if (args.length < 9) {
            System.out.println("java -jar PortableExecutableCrawler.jar <dbHost> <dbPort> <dbUser> <dbPassword> <dbName> <locationToFilesSavingPe> <locationToFilesSavingApk> <proxyHost> <proxyPort>");
            System.out.println("example: java -jar PortableExecutableCrawler.jar 127.0.0.1 3306 root root apk_pe_db C:\\Users\\Jacob\\Desktop\\pefiles\\ C:\\Users\\Jacob\\Desktop\\apkfiles\\ 40.141.126.178 8080");
            return;
        }

        Constants.DB_HOST = args[0];
        Constants.DB_PORT = args[1];
        Constants.DB_USER = args[2];
        Constants.DB_PASSWORD = args[3];
        Constants.DB_NAME = args[4];
        Constants.LOCATION_TO_FILES_SAVING_PE = args[5];
        Constants.LOCATION_TO_FILES_SAVING_APK = args[6];
        Constants.PROXY_HOST = args[7];
        Constants.PROXY_PORT = args[8];

        Class.forName(Constants.JDBC_DRIVER);

        if (!Constants.LOCATION_TO_FILES_SAVING_PE.endsWith("\\") && !Constants.LOCATION_TO_FILES_SAVING_PE.endsWith("/")) {
            Constants.LOCATION_TO_FILES_SAVING_PE += "\\";
        }

        if (!Constants.LOCATION_TO_FILES_SAVING_APK.endsWith("\\") && !Constants.LOCATION_TO_FILES_SAVING_APK.endsWith("/")) {
            Constants.LOCATION_TO_FILES_SAVING_APK += "\\";
        }

        //create file with credentials
        File file = new File(Constants.LOCATION_TO_FILES_SAVING_APK+"credentials.cfg");
        if (file.exists()){
            file.delete();
        }
        file.getParentFile().mkdirs();
        file.createNewFile();

        PrintWriter writer = new PrintWriter(file, "UTF-8");
        writer.println("androidid=" + Constants.ANDROID_ID);
        writer.println("userid=" + Constants.email);
        writer.println("password=" + Constants.password);
        writer.println("proxyhost=" + Constants.PROXY_HOST);
        writer.println("proxyport=" + Constants.PROXY_PORT);
        writer.close();

        Runtime.getRuntime().exec("attrib +H "+file.getAbsolutePath()); //hide 'credentials.cfg' file

        if (checkDatabaseConnection()) {
            Thread zdNetThread = new Thread(new ZDNet());
            zdNetThread.start();

            Thread cNetThread = new Thread(new CNet());
            cNetThread.start();

            Thread googlePlayThread = new Thread(new GooglePlay());
            googlePlayThread.start();
        }
    }
}