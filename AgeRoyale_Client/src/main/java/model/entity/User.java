package model.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/** Representa la clase User que contiene toda la información relacionada con un usuario */
public class User implements Serializable {
    /** Representa el id del usuario */
    private int id;
    /** Representa el nombre del usuario */
    private String username;
    /** Representa la contraseña del usuario */
    private String password;
    /** Representa el email del usuario */
    private String email;
    /** Representa el número de victorias del usuario */
    private int wins;
    /** Representa el número de derrotas del usuario */
    private int loses;
    /** Representa si el usuario esta baneado (true) o no (false) */
    private boolean banned;
    /** Representa la fecha desde que el usuario esta baneado */
    private LocalDateTime banDate;
    /** Representa si el usuario esta conectado (true) o no (false) */
    private boolean connected;

    /**
     * Crea un User
     */
    public User() {
    }

    /**
     * Crea un User
     * @param username - Representa el nombre del usuario
     * @param password - Representa la contraseña del usuario
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Crea un User
     * @param username - Representa el nombre del usuario
     * @param password - Representa la contraseña del usuario
     * @param email - Representa el email del usuario
     */
    public User(String username, String password, String email) {
        this.id = 0;
        this.username = username;
        this.password = password;
        this.email = email;
        this.wins = 0;
        this.loses = 0;
        this.banned = false;
        this.connected = false;
    }

    /**
     * Crea un User
     * @param id - Representa el id del usuario
     * @param username - Representa el nombre del usuario
     * @param password - Representa la contraseña del usuario
     * @param email - Representa el email del usuario
     * @param wins - Representa el número de victorias del usuario
     * @param loses - Representa el número de derrodas del usuario
     * @param banned - Representa si el usuario esta baneado o no
     * @param connected - Representa si el usuario esta conectado o no
     */
    public User(int id, String username, String password, String email, int wins, int loses, boolean banned, boolean connected) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.wins = wins;
        this.loses = loses;
        this.banned = banned;
        this.connected = connected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLoses() {
        return loses;
    }

    public void setLoses(int loses) {
        this.loses = loses;
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public LocalDateTime getBanDate() {
        return banDate;
    }

    public void setBanDate(LocalDateTime banDate) {
        this.banDate = banDate;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", wins=" + wins +
                ", loses=" + loses +
                ", banned=" + banned +
                ", banDate=" + banDate +
                ", connected=" + connected +
                '}';
    }
}