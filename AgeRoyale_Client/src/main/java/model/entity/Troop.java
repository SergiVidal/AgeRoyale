package model.entity;

import model.enumeration.TroopClass;
import model.enumeration.TroopType;

import java.io.Serializable;

/** Representa la clase Troop que contiene toda la información relacionada de una tropa */
public class Troop implements Serializable {
    /** Representa el id de la tropa */
    private Integer id;
    /** Representa el id de la partida al cual pertenece la tropa */
    private Integer matchTroopId;
    /** Representa el nombre de la tropa */
    private String name;
    /** Representa el tipo de la tropa, este puede ser: Ofensive, Defensive */
    private TroopType type;
    /** Representa la clase de la tropa, este puede ser: Warrior, Archer, Cannon, ArcherTower */
    private TroopClass troopClass;
    /** Representa los puntos de salud de la tropa */
    private int vitalityPoints;
    /** Representa la velocidad de la tropa, indicando si se puede mover o no (1: tropa Warrior/Archer o 2: estructura Cannon/ArcherTower) */
    private int speed;
    /** Representa el poder de ataque de la tropa */
    private int damage;
    /** Representa el rango de ataque de la tropa */
    private Integer range;
    /** Representa el icono/imagen de la tropa */
    private String icon;
    /** Representa el coste de la tropa */
    private Integer cost;
    /** Representa la fila en la que se encuentra la tropa */
    private Integer rowLocation;
    /** Representa la columna en la que se encuentra la tropa */
    private Integer colLocation;

    /**
     * Crea una Troop
     */
    public Troop() {
    }

    /**
     * Crea una Troop
     * @param id - Representa el id de la tropa
     * @param name - Representa el nombre de la tropa
     * @param type - Representa el tipo de la tropa
     * @param troopClass - Representa la clase de la tropa
     * @param vitalityPoints - Representa los puntos de salud de la tropa
     * @param speed - Representa la velocidad de la tropa
     * @param damage - Representa el poder de ataque de la tropa
     * @param range - Representa el rango de ataque de la tropa
     * @param icon - Representa el icono/imagen de la tropa
     * @param cost - Representa el coste de la tropa
     */
    public Troop(Integer id, String name, TroopType type, TroopClass troopClass, int vitalityPoints, int speed, int damage, Integer range, String icon, Integer cost) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.troopClass = troopClass;
        this.vitalityPoints = vitalityPoints;
        this.speed = speed;
        this.damage = damage;
        this.range = range;
        this.icon = icon;
        this.cost = cost;
    }

    /**
     * Crea una Troop
     * @param id - Representa el id de la tropa
     * @param name - Representa el nombre de la tropa
     * @param type - Representa el tipo de la tropa
     * @param troopClass - Representa la clase de la tropa
     * @param vitalityPoints - Representa los puntos de salud de la tropa
     * @param speed - Representa la velocidad de la tropa
     * @param damage - Representa el poder de ataque de la tropa
     * @param range - Representa el rango de ataque de la tropa
     * @param icon - Representa el icono/imagen de la tropa
     * @param cost - Representa el coste de la tropa
     * @param rowLocation - Representa la fila en la que se encuentra la tropa
     * @param colLocation - Representa la columna en la que se encuentra la tropa
     */
    public Troop(Integer id, String name, TroopType type, TroopClass troopClass, int vitalityPoints, int speed, int damage, Integer range, String icon, Integer cost, Integer rowLocation, Integer colLocation) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.troopClass = troopClass;
        this.vitalityPoints = vitalityPoints;
        this.speed = speed;
        this.damage = damage;
        this.range = range;
        this.icon = icon;
        this.cost = cost;
        this.rowLocation = rowLocation;
        this.colLocation = colLocation;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TroopType getType() {
        return type;
    }

    public void setType(TroopType type) {
        this.type = type;
    }

    public TroopClass getTroopClass() {
        return troopClass;
    }

    public void setTroopClass(TroopClass troopClass) {
        this.troopClass = troopClass;
    }

    public int getVitalityPoints() {
        return vitalityPoints;
    }

    public void setVitalityPoints(int vitalityPoints) {
        this.vitalityPoints = vitalityPoints;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public Integer getRange() {
        return range;
    }

    public void setRange(Integer range) {
        this.range = range;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public Integer getRowLocation() {
        return rowLocation;
    }

    public void setRowLocation(Integer rowLocation) {
        this.rowLocation = rowLocation;
    }

    public Integer getColLocation() {
        return colLocation;
    }

    public void setColLocation(Integer colLocation) {
        this.colLocation = colLocation;
    }

    public Integer getMatchTroopId() {
        return matchTroopId;
    }

    public void setMatchTroopId(Integer matchTroopId) {
        this.matchTroopId = matchTroopId;
    }

    @Override
    public String toString() {
        return "Troop{" +
                "id=" + id +
                ", matchTroopId=" + matchTroopId +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", troopClass=" + troopClass +
                ", vitalityPoints=" + vitalityPoints +
                ", speed=" + speed +
                ", damage=" + damage +
                ", range=" + range +
                ", icon='" + icon + '\'' +
                ", cost=" + cost +
                ", rowLocation=" + rowLocation +
                ", colLocation=" + colLocation +
                '}';
    }
}