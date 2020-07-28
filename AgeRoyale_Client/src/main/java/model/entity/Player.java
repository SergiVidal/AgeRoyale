package model.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** Representa la clase Player que contiene toda la información relacionada con un jugador de una partida */
public class Player implements Serializable {
    /** Representa el id del jugador */
    private int id;
    /** Representa el id del usuario vinculado a ese jugador */
    private Integer userId;
    /** Representa el nobre del jugador/usuario */
    private String username;
    /** Representa los puntos de salud del usuario */
    private int vitalityPoints;
    /** Representa el dinero disponible del usuario */
    private int availableMoney;
    /** Representa el listado de tropas del usuario */
    private List<Troop> troops;

    /**
     * Crea un Player
     */
    public Player() {
    }

    /**
     * Crea un Player
     * @param id - Representa el id del jugador
     * @param username - Representa el nombre del jugador/usuario
     * @param vitalityPoints - Representa los puntos de salud del usuario
     * @param availableMoney - Representa el dinero disponible del usuario
     * @param troops - Representa el listado de tropas del usuario
     */
    public Player(Integer id, String username, int vitalityPoints, int availableMoney, List<Troop> troops) {
        this.id = id;
        this.username = username;
        this.vitalityPoints = vitalityPoints;
        this.availableMoney = availableMoney;
        this.troops = troops;
    }

    /**
     * Crea un Player
     * @param id - Representa el id del jugador
     * @param username - Representa el nombre del jugador/usuario
     * @param vitalityPoints - Representa los puntos de salud del usuario
     * @param availableMoney - Representa el dinero disponible del usuario
     */
    public Player(Integer id, String username, int vitalityPoints, int availableMoney) {
        this.id = id;
        this.username = username;
        this.vitalityPoints = vitalityPoints;
        this.availableMoney = availableMoney;
        this.troops = new ArrayList<Troop>();
    }

    /**
     * Crea un Player
     * @param username - Representa el nombre del jugador/usuario
     * @param vitalityPoints - Representa los puntos de salud del usuario
     * @param availableMoney - Representa el dinero disponible del usuario
     */
    public Player(String username, int vitalityPoints, int availableMoney) {
        this.username = username;
        this.vitalityPoints = vitalityPoints;
        this.availableMoney=availableMoney;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getVitalityPoints() {
        return vitalityPoints;
    }

    public void setVitalityPoints(int vitalityPoints) {
        this.vitalityPoints = vitalityPoints;
    }

    public int getAvailableMoney() {
        return availableMoney;
    }

    public void setAvailableMoney(int availableMoney) {
        this.availableMoney = availableMoney;
    }

    public List<Troop> getTroops() {
        return troops;
    }

    public void setTroops(List<Troop> troops) {
        this.troops = troops;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", vitalityPoints=" + vitalityPoints +
                ", availableMoney=" + availableMoney +
                ", troops=" + troops +
                '}';
    }

    public void addTroop(Troop t) {
        troops.add(t);
    }
}