package model.database;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import model.network.ConnectionConfig;
import utils.Utility;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Representa el objeto que nos permite hacer las peticiones a la base de datos (DB).
 */
public class DBConnector {
     /** Representa el objeto ConnectionConfig que contiene la información necesaria para la conexión a la DB */
     private static ConnectionConfig config;
     /** Representa el nombre del usuario de la DB */
     private static String userName;
     /** Representa la contraseña del usuario de la DB */
     private static String password;
     /** Representa el nombre de la DB */
     private static String db;
     /** Representa el puerto de la DB */
     private static int port;
     /** Representa la url utilizada para la conexión a la DB */
     private static String url = "jdbc:mysql://localhost";
    /** Representa el objeto que nos permite establecer la conexión con el servidor MySQL */
     private static Connection conn;
    /** Representa el objeto que nos permite preparar la sentencia para ejecutar la query */
     private static Statement s;
    /** Representa el connector (mediante JDBC) que nos permite acceder a la DB mediante una conexión con el servidor MySQL*/
     private static DBConnector instance;

     /**
      * Crea el DBConnector.
      * @param usr - Representa el usuario de la base de datos.
      * @param pass - Representa la contraseña de la base de datos.
      * @param db - Representa la base de datos de la cual se quieren obtener datos.
      * @param port - Representa el puerto con el que se conectará con el servidor de base de datos (en este caso MySQL).
      */
    private DBConnector(String usr, String pass, String db, int port) {
        this.userName = usr;
        this.password = pass;
        this.db = db;
        this.port = port;
        this.url += ":" + port + "/";
        this.url += db;
        this.instance = null;
    }

    /**
     * Método que sirve para hacer una sola instancia - Singleton para que así se pueda usar la misma instancia en cualquier parte de la aplicación.
     * @return Devuelve el DBConnector.
     */
    public static DBConnector getInstance() {
        if (instance == null) {
            //todo to modify reading from json
            applyConfig();
            instance = new DBConnector(userName, password, db, port);
            instance.connect();
        }
        return instance;
    }

    /**
     * Método qeu sirve para conectarse a la DB.
     */
    public void connect() {
        try {
            Class.forName("com.mysql.jdbc.Connection");
            conn = (Connection) DriverManager.getConnection(url, userName, password);
            if (conn != null) {
                System.out.println("Connexió a base de dades " + url + " ... Ok");
            }
        } catch (SQLException ex) {
            System.out.println("Problema al connecta-nos a la BBDD --> " + url);
        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
        }

    }

    /**
     * Método que sirve para hacer un registro / insert en la DB.
     * @param query - Query que nos permitirá hacer la inserción / insert en la DB.
     * @return Devuelve un boolean que indica si se ha podido realizar o no la inserción.
     */
    public boolean insertQuery(String query) {
        try {
            s = (Statement) conn.createStatement();
            s.executeUpdate(query);
            return true;

        } catch (SQLException ex) {
            System.out.println("Problema al Inserir --> " + ex.getSQLState());
        }
        return false;
    }

    /**
     * Método que sirve para poder hacer una actualización / modificación de un registro o varios según filtro en la DB.
     * @param query - Query que nos va a permitir hacer la modificación.
     * @return Devuelve un boolean que indica si se ha podido realizar o no la modificación.
     */
    public boolean updateQuery(String query) {
        try {
            s = (Statement) conn.createStatement();
            s.executeUpdate(query);

        } catch (SQLException ex) {
            System.out.println("Problema al Modificar --> " + ex.getSQLState());
            return false;
        }
        return true;
    }

    /**
     * Método que sirve para hacer la eliminación de uno o más registros en la DB.
     * @param query - Query que nos va a permitir hacer la eliminación en la DB.
     * @return Devuelve un boolean que indica si se ha podido realizar o no la modificación.
     */
    public boolean deleteQuery(String query) {
        try {
            s = (Statement) conn.createStatement();
            s.executeUpdate(query);
        } catch ( SQLException ex) {
                System.out.println("Problema al Eliminar --> " + ex.getSQLState());
            return false;
        }
        return true;
    }

    /**
     * Método que nos va a permitir hacer una selección en la DB (1 o más tablas de la DB).
     * @param query - Query que nos va a permitir seleccionar los datos.
     * @return Devuelve una instancia de resultset con los datos para aquel o aquellos registros de la DB.
     */
    public ResultSet selectQuery(String query) {
        ResultSet rs = null;
        try {
            s = (Statement) conn.createStatement();
            rs = s.executeQuery(query);

        } catch (SQLException | NullPointerException ex) {
            rs=null;
            System.out.println("Problema al Recuperar les dades");
        }
        return rs;
    }

    /**
     * Método que nos va a permitir hacer la desconexión de la DB.
     */
    public void disconnect() {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("Problema al tancar la connexió --> " + e.getSQLState());
        }
    }

    /**
     * Método que nos va a permitir resetear valores por si se cayera la conexión con el servidor de bases de datos MySQL.
     */
    public void resetValues() {
        if (conn != null){
            conn = null;
        }
        if (instance != null){
            instance = null;
        }
        url = "jdbc:mysql://localhost";
    }

    /**
     * Método que nos va a permitir aplicar la configuración de la DB leída del fichero JSON.
     */
    public static void applyConfig() {
        config = Utility.getServerConfig();
        userName= config.getDbUsername();
        password = config.getDbPassword();
        db = config.getDbName();
        port = config.getDbPort();
    }
}