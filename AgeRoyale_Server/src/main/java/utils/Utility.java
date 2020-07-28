package utils;

import model.network.ConnectionConfig;

import java.io.FileNotFoundException;

public class Utility {

    public static final int STANDARD_SIZE = 50;
    public static final String APP_LOGO = "/logo.png";
    public static final String FAVICON = "/favicon.png";
    public static final String FONT_NAME = "Arial";
    public static final Integer PRIMARY_FONT_SIZE = 18;
    public static final Integer SECONDARY_FONT_SIZE = 14;

    public static ConnectionConfig getServerConfig(){
        try {
            return new ConnectionConfig().readJSON();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}