package crawler;

import zdnet.com.ZDNet;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException {
        if (args.length < 6) {
            System.out.println("java -jar PortableExecutableCrawler.jar <dbHost> <dbPort> <dbUser> <dbPassword> <dbName> <locationToFilesSaving>");
            System.out.println("example: java -jar PortableExecutableCrawler.jar 127.0.0.1 3306 root root pefilesdb C:\\Users\\JACOB\\Desktop");
            return;
        }
        Constants.DB_HOST = args[0];
        Constants.DB_PORT = args[1];
        Constants.DB_USER = args[2];
        Constants.DB_PASSWORD = args[3];
        Constants.DB_NAME = args[4];
        Constants.LOCATION_TO_FILES_SAVING = args[5];

        Class.forName(Constants.JDBC_DRIVER);

        Thread zdNetThread = new Thread(new ZDNet());
        zdNetThread.start();

    }
}