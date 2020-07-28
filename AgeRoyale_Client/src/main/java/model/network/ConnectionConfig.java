package model.network;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/** Representa la clase ConnectionConfig, esta contiene la información necesaria para conectarse via sockets con el servidor */
public class ConnectionConfig {
    /** Representa la dirección IP del servidor */
    private String serverCommunicationIP;
    /** Representa el puerto del servidor */
    private Integer serverCommunicationPort;

    /**
     * Crea una ConnectionConfig
     */
    public ConnectionConfig() {
    }

    public String getServerCommunicationIP() {
        return serverCommunicationIP;
    }

    public void setServerCommunicationIP(String serverCommunicationIP) {
        this.serverCommunicationIP = serverCommunicationIP;
    }

    public Integer getServerCommunicationPort() {
        return serverCommunicationPort;
    }

    public void setServerCommunicationPort(Integer serverCommunicationPort) {
        this.serverCommunicationPort = serverCommunicationPort;
    }

    /**
     * Función encargada de leer el archivo de configuración .JSON que contiene la información necesaria para conectarse via sockets con el servidor
     * @return - Devuelve el objeto ConnectionConfig que contiene la información
     */
    public ConnectionConfig readJSON() throws FileNotFoundException {
        String path = (getClass().getResource("/config.json").getPath());
        path = URLDecoder.decode(path, StandardCharsets.UTF_8);
        return new Gson().fromJson(new FileReader(new File(path)), ConnectionConfig.class);
    }


    @Override
    public String toString() {
        return "ConnectionConfig{" +
                "serverCommunicationIP='" + serverCommunicationIP + '\'' +
                ", serverCommunicationPort=" + serverCommunicationPort +
                '}';
    }
}
