package model.network;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class ConnectionConfig {
    private Integer dbPort;
    private String dbServerIP;
    private String dbName;
    private String dbUsername;
    private String dbPassword;
    private Integer serverCommunicationPort;

    public ConnectionConfig() {
    }

    public Integer getDbPort() {
        return dbPort;
    }

    public void setDbPort(Integer dbPort) {
        this.dbPort = dbPort;
    }

    public String getDbServerIP() {
        return dbServerIP;
    }

    public void setDbServerIP(String dbServerIP) {
        this.dbServerIP = dbServerIP;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public void setDbUsername(String dbUsername) {
        this.dbUsername = dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public Integer getServerCommunicationPort() {
        return serverCommunicationPort;
    }

    public void setServerCommunicationPort(Integer serverCommunicationPort) {
        this.serverCommunicationPort = serverCommunicationPort;
    }

    public ConnectionConfig readJSON() throws FileNotFoundException {
        String path = (getClass().getResource("/config.json").getPath());
        path = URLDecoder.decode(path, StandardCharsets.UTF_8);
        return new Gson().fromJson(new FileReader(new File(path)), ConnectionConfig.class);
    }

    @Override
    public String toString() {
        return "ConnectionConfig{" +
                "dbPort=" + dbPort +
                ", dbServerIP='" + dbServerIP + '\'' +
                ", dbName='" + dbName + '\'' +
                ", dbUsername='" + dbUsername + '\'' +
                ", dbPassword='" + dbPassword + '\'' +
                ", serverCommunicationPort=" + serverCommunicationPort +
                '}';
    }
}
