package crawler;

public class Constants {
    public static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

    public static final String ZD_NET_COM = "http://downloads.zdnet.com/";
    public static final String ZD_NET_COM_LOGIN = "https://secure.zdnet.com/user/login/?appId=1101";
    public static final String ZD_NET_COM_DOWNLOAD = "http://downloads.zdnet.com/price/all/";

    public static final String CNET_COM = "http://download.cnet.com/";
    public static final String CNET_COM_WINDOWS = "http://download.cnet.com/windows/";

    public static final String GOOGLEPLAY_COM = "https://play.google.com";

    public static final String email = "portexcrawler@gmail.com";
    public static final String password = "portexcrawler777";
    public static final String ANDROID_ID = "36b5e85b9970a533";

    public static final String[] CATEGORIES_CNET = {
            "http://download.cnet.com/windows/security-software/",
            "http://download.cnet.com/windows/browsers/",
            "http://download.cnet.com/windows/business-software/",
            "http://download.cnet.com/windows/communications/",
            "http://download.cnet.com/windows/desktop-enhancements/",
            "http://download.cnet.com/windows/developer-tools/",
            "http://download.cnet.com/windows/digital-photo-software/",
            "http://download.cnet.com/windows/drivers/",
            "http://download.cnet.com/windows/educational-software/",
            "http://download.cnet.com/windows/entertainment-software/",
            "http://download.cnet.com/windows/3150-2012_4-0.html",
            "http://download.cnet.com/windows/graphic-design-software/",
            "http://download.cnet.com/windows/home-software/",
            "http://download.cnet.com/windows/internet-software/",
            "http://download.cnet.com/windows/itunes-and-ipod-software/",
            "http://download.cnet.com/windows/mp3-and-audio-software/",
            "http://download.cnet.com/windows/networking-software/",
            "http://download.cnet.com/windows/productivity-software/",
            "http://download.cnet.com/windows/screensavers-and-wallpaper/",
            "http://download.cnet.com/windows/travel/",
            "http://download.cnet.com/windows/utilities-and-operating-systems/",
            "http://download.cnet.com/windows/video-software/"
    };

    public static final String[] CATEGORIES_GOOGLE = {
            "/store/apps/category/BOOKS_AND_REFERENCE/collection/topselling_free",
            "/store/apps/category/BUSINESS/collection/topselling_free",
            "/store/apps/category/COMICS/collection/topselling_free",
            "/store/apps/category/COMMUNICATION/collection/topselling_free",
            "/store/apps/category/EDUCATION/collection/topselling_free",
            "/store/apps/category/ENTERTAINMENT/collection/topselling_free",
            "/store/apps/category/FINANCE/collection/topselling_free",
            "/store/apps/category/HEALTH_AND_FITNESS/collection/topselling_free",
            "/store/apps/category/LIBRARIES_AND_DEMO/collection/topselling_free",
            "/store/apps/category/LIFESTYLE/collection/topselling_free",
            "/store/apps/category/APP_WALLPAPER/collection/topselling_free",
            "/store/apps/category/MEDIA_AND_VIDEO/collection/topselling_free",
            "/store/apps/category/MEDICAL/collection/topselling_free",
            "/store/apps/category/MUSIC_AND_AUDIO/collection/topselling_free",
            "/store/apps/category/NEWS_AND_MAGAZINES/collection/topselling_free",
            "/store/apps/category/PERSONALIZATION/collection/topselling_free",
            "/store/apps/category/PHOTOGRAPHY/collection/topselling_free",
            "/store/apps/category/PRODUCTIVITY/collection/topselling_free",
            "/store/apps/category/SHOPPING/collection/topselling_free",
            "/store/apps/category/SOCIAL/collection/topselling_free",
            "/store/apps/category/SPORTS/collection/topselling_free",
            "/store/apps/category/TOOLS/collection/topselling_free",
            "/store/apps/category/TRANSPORTATION/collection/topselling_free",
            "/store/apps/category/TRAVEL_AND_LOCAL/collection/topselling_free",
            "/store/apps/category/WEATHER/collection/topselling_free",
            "/store/apps/category/APP_WIDGETS/collection/topselling_free",
            "/store/apps/category/GAME_ACTION/collection/topselling_free",
            "/store/apps/category/GAME_ADVENTURE/collection/topselling_free",
            "/store/apps/category/GAME_ARCADE/collection/topselling_free",
            "/store/apps/category/GAME_BOARD/collection/topselling_free",
            "/store/apps/category/GAME_CARD/collection/topselling_free",
            "/store/apps/category/GAME_CASINO/collection/topselling_free",
            "/store/apps/category/GAME_CASUAL/collection/topselling_free",
            "/store/apps/category/GAME_EDUCATIONAL/collection/topselling_free",
            "/store/apps/category/GAME_MUSIC/collection/topselling_free",
            "/store/apps/category/GAME_PUZZLE/collection/topselling_free",
            "/store/apps/category/GAME_RACING/collection/topselling_free",
            "/store/apps/category/GAME_ROLE_PLAYING/collection/topselling_free",
            "/store/apps/category/GAME_SIMULATION/collection/topselling_free",
            "/store/apps/category/GAME_SPORTS/collection/topselling_free",
            "/store/apps/category/GAME_STRATEGY/collection/topselling_free",
            "/store/apps/category/GAME_TRIVIA/collection/topselling_free",
            "/store/apps/category/GAME_WORD/collection/topselling_free",
            "/store/apps/category/FAMILY_ACTION/collection/topselling_free",
            "/store/apps/category/FAMILY_BRAINGAMES/collection/topselling_free",
            "/store/apps/category/FAMILY_CREATE/collection/topselling_free",
            "/store/apps/category/FAMILY_EDUCATION/collection/topselling_free",
            "/store/apps/category/FAMILY_MUSICVIDEO/collection/topselling_free",
            "/store/apps/category/FAMILY_PRETEND/collection/topselling_free"
    };

    //will get from command line parameters
    public static String DB_HOST;
    public static String DB_PORT;
    public static String DB_USER;
    public static String DB_PASSWORD;
    public static String DB_NAME;
    public static String LOCATION_TO_FILES_SAVING_PE;
    public static String LOCATION_TO_FILES_SAVING_APK;
    public static String PROXY_HOST;
    public static String PROXY_PORT;
    public static int DEEP_FOR_SIMILAR;

}