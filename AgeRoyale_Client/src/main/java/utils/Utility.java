package utils;

import model.network.ConnectionConfig;

import java.io.FileNotFoundException;

/** Representa la clase Utility, esta contiene valores de configuración comunes para todas las clases del cliente */
public class Utility {
    /* Constantes */
    public static final int STANDARD_SIZE = 50;
    public static final String APP_LOGO = "/logo.png";
    public static final String FAVICON = "/favicon.png";
    public static final String FONT_NAME = "Arial";
    public static final Integer PRIMARY_FONT_SIZE = 18;
    public static final Integer SECONDARY_FONT_SIZE = 14;
    /**/
    
    /**
     * Función encargada de llamar a la función encargada de leer el archivo de configuración .JSON que contiene la información necesaria para conectarse via sockets con el servidor
     * @return - Devuelve el objeto ConnectionConfig que contiene la información
     */
    public static ConnectionConfig getServerConfig(){
        try {
            return new ConnectionConfig().readJSON();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
