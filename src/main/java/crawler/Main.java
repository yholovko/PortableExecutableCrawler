package crawler;

import zdnet.com.ZDNet;

/**
 * Created by JACOB on 02.06.2015.
 */
public class Main {
    public static void main(String[] args) {
        if (args.length < 5) {
            System.out.println("java -jar PortableExecutableCrawler.jar <host> <user> <passwordToDB> <databaseName> <locationToFilesSaving>");
            System.out.println("example: java -jar PortableExecutableCrawler.jar 127.0.0.1 root root pefilesdb C:\\Users\\JACOB\\Desktop");
            return;
        }
        String host = args[0];
        String user = args[1];
        String passwordToDB = args[2];
        String databaseName = args[3];
        String locationToFilesSaving = args[4];

        Thread zdNetThread = new Thread(new ZDNet(host, user, passwordToDB, databaseName, locationToFilesSaving));
        zdNetThread.start();

    }
}