package model.entity;


import model.enumeration.CellType;

import java.io.Serializable;

public class GenericCell implements Serializable {
    private CellType type;
    private String hostIcon;
    private String guestIcon;
    private String color;

    public GenericCell(CellType type, String hostIcon, String guestIcon, String color) {
        this.type = type;
        this.hostIcon = hostIcon;
        this.guestIcon = guestIcon;
        this.color = color;
    }

    public CellType getType() {
        return type;
    }

    public void setType(CellType type) {
        this.type = type;
    }

    public String getHostIcon() {
        return hostIcon;
    }

    public void setHostIcon(String hostIcon) {
        this.hostIcon = hostIcon;
    }

    public String getGuestIcon() {
        return guestIcon;
    }

    public void setGuestIcon(String guestIcon) {
        this.guestIcon = guestIcon;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "GenericCell{" +
                "type=" + type +
                ", hostIcon='" + hostIcon + '\'' +
                ", guestIcon='" + guestIcon + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}