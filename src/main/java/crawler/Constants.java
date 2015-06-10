package crawler;

public class Constants {
    public static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

    public static final String ZD_NET_COM = "http://downloads.zdnet.com/";
    public static final String ZD_NET_COM_LOGIN = "https://secure.zdnet.com/user/login/?appId=1101";
    public static final String ZD_NET_COM_DOWNLOAD = "http://downloads.zdnet.com/price/all/";

    public static final String CNET_COM = "http://download.cnet.com/";
    public static final String CNET_COM_WINDOWS = "http://download.cnet.com/windows/";

    public static final String email = "portexcrawler@gmail.com";
    public static final String password = "portexcrawler777";

    //will get from command line parameters
    public static String DB_HOST;
    public static String DB_PORT;
    public static String DB_USER;
    public static String DB_PASSWORD;
    public static String DB_NAME;
    public static String LOCATION_TO_FILES_SAVING;
}